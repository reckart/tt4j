package org.annolab.tt4j;

import static org.annolab.tt4j.Util.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
	private ModelResolver _modelProvider = null;
	private ExecutableResolver _exeProvider = null;

	{
		_modelProvider = new DefaultModelResolver();
		_exeProvider = new DefaultExecutableResolver();
		setPlatformDetector(new PlatformDetector());
	}

	public
	void setModelProvider(
			ModelResolver aModelProvider)
	{
		_modelProvider = aModelProvider;
		_modelProvider.setPlatformDetector(_platform);
	}

	public
	void setExecutableProvider(
			ExecutableResolver aExeProvider)
	{
		_exeProvider = aExeProvider;
		_exeProvider.setPlatformDetector(_platform);
	}

	public
	void setHandler(
			TokenHandler<O> aHandler)
	{
		_handler = aHandler;
	}

	public
	void setAdapter(
			TokenAdapter<O> aAdapter)
	{
		_adapter = aAdapter;
	}

	public
	void setPlatformDetector(
			PlatformDetector aPlatform)
	{
		_platform = aPlatform;
		if (_modelProvider != null) {
			_modelProvider.setPlatformDetector(aPlatform);
		}
		if (_exeProvider != null) {
			_exeProvider.setPlatformDetector(aPlatform);
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
			String modelName)
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
			_model = _modelProvider.getModel(modelName);
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
		catch (IOException e) {
			// Ignore
		}

		// Clear the executable
    	if (_exeProvider != null) {
    		_exeProvider.destroy();
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
			Collection<O> aTokens)
	throws IOException
	{
		Process taggerProc = getTaggerProcess();

    	Thread writer = new Thread(new Writer(aTokens.iterator()));

    	writer.start();

		BufferedReader in = new BufferedReader(new InputStreamReader(
		    taggerProc.getInputStream(), _model.getEncoding()));

		String s;
		Iterator<O> tokenIterator = aTokens.iterator();
		boolean inText = false;
		while (true) {
			s = in.readLine();
//    		System.out.println("<-- "+s);

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
//				String word = fields[0].trim();
				String tags  = fields[1];
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

		try {
			writer.join();
		}
		catch (InterruptedException e) {
			// Ignore
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

			// fetch output from pos tagger
			// fill output into annotation
			String commands[] = {
					_exeProvider.getExecutable(),
					"-quiet",
					"-no-unknown",
					"-sgml",
					"-token",
					"-lemma",
					_model.getFile().getAbsolutePath() };

			_procCmd = join(commands, " ");

//			info("Starting treetagger: " + _procCmd);
			_proc = Runtime.getRuntime().exec(commands);
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
			O o)
	{
		if (_adapter == null) {
			return o.toString();
		}
		else {
			return _adapter.getText(o);
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
    			Iterator<O> aTokenIterator)
		{
    		tokenIterator = aTokenIterator;
		}

    	public
    	void run()
    	{
    		try {
    			OutputStream os = _proc.getOutputStream();

    			_pw = new PrintWriter(new BufferedWriter(
    			    new OutputStreamWriter(os, _model.getEncoding())));

    			send(STARTOFTEXT);

    			while (tokenIterator.hasNext()) {
    				send(getText(tokenIterator.next()));
    			}

    			send(ENDOFTEXT);
				send("\n.\n"+_model.getFlushSequence()+"\n\n");
    		}
    		catch (Exception e) {
    			_exception = e;
    		}
    	}

    	private
    	void send(
    			String line)
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
