package org.annolab.tt4j;

import static org.annolab.tt4j.Util.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.Thread.State;
import java.util.Collection;
import java.util.Iterator;
import java.util.regex.Pattern;

public
class TreeTaggerWrapper<O>
{
    private final static Pattern RE_TAB			= Pattern.compile("[\\t]");
    private final static Pattern RE_WHITESPACE	= Pattern.compile("[\\p{Zs}\\p{C}]");

    // A tag to identify begin/end of a text in the data flow.
    // (avoid to restart TreeTagger process each time)
    private static final String STARTOFTEXT = "<This-is-the-start-of-the-text />";
    private static final String ENDOFTEXT = "<This-is-the-end-of-the-text />";

	public final static String PARAM_MODEL = "model";

	private Model _model = null;

	private Process _proc = null;
	private String  _procCmd = null;

	private TokenHandler<O> _handler = null;
	private TokenAdapter<O> _adapter = null;
	private PlatformDetector _platform = null;
	private ModelResolver _modelResolver = null;
	private ExecutableResolver _exeResolver = null;

	{
		_modelResolver = new DefaultModelResolver();
		_exeResolver = new DefaultExecutableResolver();
		setPlatformDetector(new PlatformDetector());
	}

	public
	void setModelProvider(
			final ModelResolver aModelProvider)
	{
		_modelResolver = aModelProvider;
		_modelResolver.setPlatformDetector(_platform);
	}

	public
	void setExecutableProvider(
			final ExecutableResolver aExeProvider)
	{
		_exeResolver = aExeProvider;
		_exeResolver.setPlatformDetector(_platform);
	}

	public
	void setHandler(
			final TokenHandler<O> aHandler)
	{
		_handler = aHandler;
	}

	public
	void setAdapter(
			final TokenAdapter<O> aAdapter)
	{
		_adapter = aAdapter;
	}

	public
	void setPlatformDetector(
			final PlatformDetector aPlatform)
	{
		_platform = aPlatform;
		if (_modelResolver != null) {
			_modelResolver.setPlatformDetector(aPlatform);
		}
		if (_exeResolver != null) {
			_exeResolver.setPlatformDetector(aPlatform);
		}
	}

	public
	PlatformDetector getPlatformDetector()
	{
		return _platform;
	}

    /**
	 * Load the model with the given name.
	 *
	 * @param modelName the name of the model.
	 * @return the model.
	 * @throws IOException if the model can not be found.
	 */
	public
	void setModel(
			final String modelName)
	throws IOException
	{
		// If this model is already set, do nothing.
		if (_model != null && _model.getName().equals(modelName)) {
			return;
		}

		stopTaggerProcess();

		// If the previous model was temporary, we have to clean it up
		if (_model != null) {
			_model.destroy();
		}

		if (modelName != null) {
			_model = _modelResolver.getModel(modelName);
		}
		else {
			_model = null;
		}
	}

	public
	Model getModel()
	{
		return _model;
	}

	public
	void destroy()
	{
		// Clear the model resources
		try {
			setModel(null);
		}
		catch (final IOException e) {
			// Ignore
		}

		// Clear the executable
    	if (_exeResolver != null) {
    		_exeResolver.destroy();
    	}
	}

	@Override
	protected
	void finalize()
	throws Throwable
	{
		destroy();
		super.finalize();
	}

	public
	void process(
			final Collection<O> aTokens)
	throws IOException, TreeTaggerException
	{
		final Process taggerProc = getTaggerProcess();

		// One thread reads the output.
		final Reader reader = new Reader(
				taggerProc.getInputStream(), aTokens.iterator());
		final Thread readerThread = new Thread(reader);
		readerThread.start();

		// One thread consumes stderr so we do not get a deadlock.
		final StreamGobbler gob = new StreamGobbler(taggerProc.getErrorStream());
		new Thread(gob).start();

		// Now we can start writing.
		final Writer writer = new Writer(aTokens.iterator());
		new Thread(writer).start();

		// Wait for the processing to end. Every once in a while we check if an
		// exception has been thrown. When the Reader thread is complete, we can
		// stop.
		try {
			while (true) {
				// If the reader or writer fail, we kill the treetagger and bail
				// out. This may be a bit harsh, but easier than coding the
				// Reader and Writer so that we can abort them. If the process
				// is dead, the streams die and then the threads will also die
				// with an IOException.
				if (writer.getException() != null) {
					taggerProc.destroy();
					throw new TreeTaggerException(writer.getException());
				}

				if (reader.getException() != null) {
					taggerProc.destroy();
					throw new TreeTaggerException(reader.getException());
				}

				// Otherwise we wait for the Reader thread to end.
				if (readerThread.getState() == State.TERMINATED) {
					break;
				}

				Thread.sleep(100);
			}
		}
		catch (final InterruptedException e) {
			// Ignore
		}
		finally {
			gob.done();
		}

//		info("Parsed " + count + " pos segments");
	}

	/**
     * Start tagger process.
     *
     * @return
     * @throws IOException
     */
    private
    Process getTaggerProcess()
    throws IOException
    {
    	if (_proc == null) {
        	_model.install();

			final String commands[] = {
					_exeResolver.getExecutable(),
					"-quiet",
					"-no-unknown",
					"-sgml",
					"-token",
					"-lemma",
					_model.getFile().getAbsolutePath() };
			_procCmd = join(commands, " ");

//			info("Starting treetagger: " + _procCmd);
			final ProcessBuilder pb = new ProcessBuilder(commands);
			_proc = pb.start();
    	} else {
//    		info("Re-using treetagger: " + _procCmd);
    	}
    	return _proc;
    }

    /**
     * Kill tagger process.
     */
    private
    void stopTaggerProcess()
    {
    	if (_proc != null) {
	    	_proc.destroy();
	    	_proc = null;
	    	_procCmd = null;
	    	// getContext().getLogger().log(Level.INFO, "Stopped TreeTagger sub-process");
    	}
    }

    private
	String getText(
			final O o)
	{
		if (_adapter == null) {
			return o.toString();
		}
		else {
			return _adapter.getText(o);
		}
	}

    private
    class StreamGobbler
    implements Runnable
    {
    	private final InputStream in;
    	private boolean done = false;

    	public
    	StreamGobbler(
    			final InputStream aIn)
    	{
			in = aIn;
		}

    	public
    	void done()
    	{
    		done = true;
    	}

    	public
    	void run()
    	{
    		try {
	    		while(!done) {
	    			in.skip(in.available());
	    			in.wait(100);
	    		}
    		}
    		catch (final Exception e) {
    			done = true;
    		}
    	}
    }

	private
    class Reader
    implements Runnable
    {
		private final Iterator<O> tokenIterator;
		private final BufferedReader in;
		private final InputStream ins;
		private Exception _exception;

    	public
    	Reader(
    			final InputStream aIn,
    			final Iterator<O> aTokenIterator)
    	throws UnsupportedEncodingException
		{
    		ins = aIn;
    		in = new BufferedReader(new InputStreamReader(
    			    ins, _model.getEncoding()));
    		tokenIterator = aTokenIterator;
		}

    	public
    	void run()
    	{
    		try {
	    		String s;
	    		boolean inText = false;
	    		while (true) {
	    			s = in.readLine();

	    			if (s == null) {
	    				throw new IOException(
	    						"TreeTagger has died. Make sure the following " +
	    						"comand (in parentheses) works when running " +
	    						"it from the command line: [echo \"test\" | " +
	    						_procCmd+"]");
	    			}

	    			s = s.trim();

	    			if (STARTOFTEXT.equals(s)) {
	    				inText = true;
	    				continue;
	    			}

	    			if (ENDOFTEXT.equals(s)) {
	    				break;
	    			}

	    			if (inText) {
	    				// Get word and tag
	    				String fields[] = RE_TAB.split(s, 2);
	    				final String tags  = fields[1];
	    				fields = RE_WHITESPACE.split(tags, 3);

	    				// Get original token segment
	    				if (_handler != null) {
	    					_handler.token(
	    							tokenIterator.next(),
	    							fields[0].trim().intern(),
	    							fields[1].trim());
	    				}
	    			}
	    		}
    		}
    		catch (final Exception e) {
    			_exception = e;
    		}
    	}

    	public
    	Exception getException() {
			return _exception;
		}
    }

	private
    class Writer
    implements Runnable
    {
		private final Iterator<O> tokenIterator;
		private Exception _exception;
		private PrintWriter _pw;

    	public
    	Writer(
    			final Iterator<O> aTokenIterator)
		{
    		tokenIterator = aTokenIterator;
		}

    	public
    	void run()
    	{
    		try {
    			final OutputStream os = _proc.getOutputStream();

    			_pw = new PrintWriter(new BufferedWriter(
    			    new OutputStreamWriter(os, _model.getEncoding())));

    			send(STARTOFTEXT);

    			while (tokenIterator.hasNext()) {
    				send(getText(tokenIterator.next()));
    			}

    			send(ENDOFTEXT);
				send("\n.\n"+_model.getFlushSequence()+"\n\n");
    		}
    		catch (final Exception e) {
    			_exception = e;
    		}
    	}

    	private
    	void send(
    			final String line)
    	{
    		_pw.println(line);
//    		System.out.println("--> "+line);
    		_pw.flush();
    	}

    	public
    	Exception getException()
		{
			return _exception;
		}
    }
}
