/*******************************************************************************
 * Copyright (c) 2009 Richard Eckart de Castilho.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     Richard Eckart de Castilho - initial API and implementation
 ******************************************************************************/
package org.annolab.tt4j;

import static org.annolab.tt4j.Util.join;

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Main TreeTagger wrapper class. One TreeTagger process will be created and
 * maintained for each instance of this class. The associated process will be
 * terminated and restarted automatically if the model is changed
 * ({@link #setModel(String)}). Otherwise the process remains running,
 * in the background once it is started which saves a lot of time. The process
 * remains dormant while not used and only consumes some memory, but no CPU
 * while it is not used.
 * <p>
 * During analysis, two threads are used to communicate with the TreeTagger.
 * One process writes tokens to the TreeTagger process, while the other
 * receives the analyzed tokens.
 * <p>
 * For easy integration into application, this class takes any object containing
 * token information and either uses its {@link Object#toString()} method or
 * an {@link TokenAdapter} set using {@link #setAdapter(TokenAdapter)} to extract
 * the actual token. To receive the an analyzed token, set a custom
 * {@link TokenHandler} using {@link #setHandler(TokenHandler)}.
 * <p>
 * Per default the TreeTagger executable is searched for in the directories
 * indicated by the system propery {@literal treetagger.home}, the
 * environment variables {@literal TREETAGGER_HOME} and {@literal TAGDIR}
 * in this order. A full path to a model file optionally appended by a
 * {@literal :} and the model encoding is expected by the {@link #setModel(String)}
 * method.
 * <p>
 * For additional flexibility, register a custom {@link ExecutableResolver}
 * using {@link #setExecutableProvider(ExecutableResolver)} or a custom
 * {@link ModelResolver} using {@link #setModelProvider(ModelResolver)}. Custom
 * providers may extract models and executable from archives or download them
 * from some location and temporarily or permanently install them in the file
 * system. A custom model resolver may also be used to resolve a language code
 * (e.g. {@literal en}) to a particular model.
 * <p>
 * A simple illustration of how to use this class:
 * <pre>
 * TreeTaggerWrapper tt = new TreeTaggerWrapper<String>();
 * try {
 *     tt.setModel("/treetagger/models/english.par:iso8859-1");
 *     tt.setHandler(new TokenHandler<String>() {
 *         void token(String token, String pos, String lemma) {
 *             System.out.println(token+"\t"+pos+"\t"+lemma);
 *         }
 *     });
 *     tt.process(asList(new String[] {"This", "is", "a", "test", "."}));
 * }
 * finally {
 *     tt.destroy();
 * }
 * </pre>
 * @author Richard Eckart de Castilho
 *
 * @param <O> the token type.
 */
public
class TreeTaggerWrapper<O>
{
	public static boolean TRACE = false;

    private final static Pattern RE_TAB			= Pattern.compile("[\\t]");
    private final static Pattern RE_WHITESPACE	= Pattern.compile("[\\p{Zs}\\p{C}]");

    // A tag to identify begin/end of a text in the data flow.
    // (avoid to restart TreeTagger process each time)
    private static final String STARTOFTEXT = "<This-is-the-start-of-the-text />";
    private static final String ENDOFTEXT = "<This-is-the-end-of-the-text />";

	private Model _model = null;

	private Process _proc = null;
	private String  _procCmd = null;

	private TokenHandler<O> _handler = null;
	private TokenAdapter<O> _adapter = null;
	private PlatformDetector _platform = null;
	private ModelResolver _modelResolver = null;
	private ExecutableResolver _exeResolver = null;

	private Double _epsilon = null;
	private boolean _hyphenHeuristics = false;

	private String[] _ttArgs = { "-quiet", "-no-unknown", "-sgml",
			"-token", "-lemma" };

	private int _numTokens = 0;
	private int _tokensWritten = 0;
	private O _lastTokenWritten;
	private int _tokensRead = 0;
	private O _lastTokenRead;
	private String _lastTokenReadTT;
	private int _restartCount = 0;

	private boolean _performanceMode = false;

	{
		_modelResolver = new DefaultModelResolver();
		_exeResolver = new DefaultExecutableResolver();
		setPlatformDetector(new PlatformDetector());

		if (!"false".equals(System.getProperty(getClass().getName()+".TRACE", "false"))) {
			TRACE = true;
		}
	}

	/**
	 * Disable some sanity checks, e.g. whether tokens contain line breaks
	 * (which is not allowed). Turning this on will increase your performance,
	 * but the wrapper may throw exceptions if illegal data is provided.
	 *
	 * @param performanceMode
	 */
	public
	void setPerformanceMode(
			boolean performanceMode)
	{
		_performanceMode = performanceMode;
	}

	/**
	 * Set the arguments that are passed to the TreeTagger executable. A call
	 * to this method will cause a running TreeTagger process to be shut down
	 * and restarted with the new arguments.
	 *
	 * Using this method can cause TT4J to not work any longer. TTJ4 expects
	 * that TreeTagger prints a set of line each containing three tokens
	 * separated by spaces.
	 *
	 * @param aArgs the arguments.
	 */
	public
	void setArguments(
			String[] aArgs)
	{
		_ttArgs = aArgs;
		stopTaggerProcess();
	}

	public
	String[] getArguments() {
		return _ttArgs;
	}

	/**
	 * Set minimal tag frequency to {@code epsilon}
	 *
	 * @param aEpsilon epsilon
	 */
	public
	void setEpsilon(
			final Double aEpsilon)
	{
		_epsilon = aEpsilon;
		stopTaggerProcess();
	}

	/**
	 * Turn on the heuristics fur guessing the parts of speech of unknown
	 * hyphenated words.
	 *
	 * @param hyphenHeuristics use hyphen heuristics.
	 */
	public
	void setHyphenHeuristics(
			boolean hyphenHeuristics)
	{
		_hyphenHeuristics = hyphenHeuristics;
		stopTaggerProcess();
	}

	/**
	 * Set a custom model resolver.
	 *
	 * @param aModelProvider a model resolver.
	 */
	public
	void setModelProvider(
			final ModelResolver aModelProvider)
	{
		_modelResolver = aModelProvider;
		_modelResolver.setPlatformDetector(_platform);
	}

	/**
	 * Set a custom executable resolver.
	 *
	 * @param aExeProvider a executable resolver.
	 */
	public
	void setExecutableProvider(
			final ExecutableResolver aExeProvider)
	{
		_exeResolver = aExeProvider;
		_exeResolver.setPlatformDetector(_platform);
	}

	/**
	 * Set a {@link TokenHandler} to receive the analyzed tokens.
	 *
	 * @param aHandler a token handler.
	 */
	public
	void setHandler(
			final TokenHandler<O> aHandler)
	{
		_handler = aHandler;
	}

	/**
	 * Set a {@link TokenAdapter} used to extract the token string from
	 * a token objects passed to {@link #process(Collection)}. If no adapter
	 * is set, the {@link Object#toString()} method is used.
	 *
	 * @param aAdapter the adapter.
	 */
	public
	void setAdapter(
			final TokenAdapter<O> aAdapter)
	{
		_adapter = aAdapter;
	}

	/**
	 * Set platform information. Also sets the platform information in
	 * the model resolver and the executable resolver.
	 *
	 * @param aPlatform the platform information.
	 */
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

	/**
	 * Get platform information.
	 *
	 * @return the platform information.
	 */
	public
	PlatformDetector getPlatformDetector()
	{
		return _platform;
	}

    /**
	 * Load the model with the given name.
	 *
	 * @param modelName the name of the model.
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

	/**
	 * Get the currently set model.
	 *
	 * @return the current model.
	 */
	public
	Model getModel()
	{
		return _model;
	}

	/**
	 * Stop the TreeTagger process and clean up the model and executable.
	 */
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

	/**
	 * Process the given list of token objects.
	 *
	 * @param aTokens the token objects.
	 * @throws IOException if there is a problem providing the model or executable.
	 * @throws TreeTaggerException if there is a problem communication with TreeTagger.
	 */
	public
	void process(
			final Collection<O> aTokenList)
	throws IOException, TreeTaggerException
	{
		// In normal more sort out all tokens that we cannot handle. In
		// particular line breaks and tabs cannot be handled by TreeTagger.
		Collection<O> aTokens;
		if (!_performanceMode) {
			aTokens = removeProblematicTokens(aTokenList);
		}
		else {
			aTokens = aTokenList;
		}

		// Remember the number of tokens we originally got.
		_numTokens = aTokens.size();
		_tokensRead = 0;
		_tokensWritten = 0;
		_lastTokenRead = null;
		_lastTokenReadTT = null;
		_lastTokenWritten = null;

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
			// Wait for the Reader thread to end.
			synchronized (reader) {
				while (readerThread.getState() != State.TERMINATED) {
					try {
						// If the reader or writer fail, we kill the treetagger and bail
						// out. This may be a bit harsh, but easier than coding the
						// Reader and Writer so that we can abort them. If the process
						// is dead, the streams die and then the threads will also die
						// with an IOException.
						if (writer.getException() != null) {
							destroy();
							throw new TreeTaggerException(writer.getException());
						}

						if (reader.getException() != null) {
							destroy();
							throw new TreeTaggerException(reader.getException());
						}

						reader.wait(20);
					}
					catch (final InterruptedException e) {
						// Ignore
					}
				}
			}
		}
		finally {
			gob.done();
		}

//		info("Parsed " + count + " pos segments");
	}

	/**
	 *
	 *
	 * @param aTokenList
	 * @return
	 */
	protected
	Collection<O> removeProblematicTokens(
			Collection<O> aTokenList)
	{
		Collection<O> filtered = new ArrayList<O>(aTokenList.size());
		Iterator<O> i = aTokenList.iterator();
		boolean skipped = true;
		String text = null;
		skipToken: while (i.hasNext()) {
			if (TRACE && skipped && text != null) {
				System.err.println("["+TreeTaggerWrapper.this+
						"|TRACE] Skipping illegal token ["+text+"]");
			}

			skipped = true;
			O token = i.next();
			text = getText(token);
			if (text == null) {
				continue;
			}

			boolean onlyWhitespace = true;
			for (int n = 0; n < text.length(); n++) {
				char c = text.charAt(n);
				switch (c) {
				case '\n':     continue skipToken; // Line break
				case '\r':     continue skipToken; // Carriage return
				case '\t':     continue skipToken; // Tab
				case '\u200E': continue skipToken; // LEFT-TO-RIGHT MARK
				case '\u200F': continue skipToken; // RIGHT-TO-LEFT MARK
				case '\u2028': continue skipToken; // LINE SEPARATOR
				case '\u2029': continue skipToken; // PARAGRAPH SEPARATOR
				default:
					if (onlyWhitespace) {
						onlyWhitespace &= Character.isWhitespace(c);
					}
				}
			}

			if (onlyWhitespace) {
				continue skipToken;
			}

			filtered.add(token);
			skipped = false;
		}
		return filtered;
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

//			info("Starting treetagger: " + _procCmd);
			List<String> cmd = new ArrayList<String>();
			cmd.add(_exeResolver.getExecutable());
			for (String arg : _ttArgs) {
				cmd.add(arg);
			}

			if (_epsilon != null) {
				cmd.add("-eps");
				cmd.add(String.format(Locale.US, "%.12f", _epsilon));
			}

			if (_hyphenHeuristics) {
				cmd.add("-hyphen-heuristics");
			}

			cmd.add(_model.getFile().getAbsolutePath());
			_procCmd = join(cmd, " ");

			final ProcessBuilder pb = new ProcessBuilder();
			pb.command(cmd);
			_proc = pb.start();
			_restartCount++;
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

    public
    String getStatus()
    {
		StringBuilder sb = new StringBuilder();
		sb.append("Last token read (#").append(_tokensRead).append("): ");
		if (_lastTokenRead != null) {
			sb.append("[").append(getText(_lastTokenRead)).append("]");
			sb.append(" - (").append(_lastTokenReadTT+")");
		}
		else {
			sb.append("none");
		}
		sb.append("\n");

		sb.append("Last token written (#").append(_tokensWritten).append("): ");
		if (_lastTokenWritten != null) {
			sb.append("[").append(getText(_lastTokenWritten)).append("]");
		}
		else {
			sb.append("none");
		}
		sb.append("\n");

		sb.append("Tokens originally recieved: ").append(_numTokens).append("\n");
		sb.append("Tokens written            : ").append(_tokensWritten).append("\n");
		sb.append("Tokens read               : ").append(_tokensRead).append("\n");

		return sb.toString();
    }

    /**
     * Get the number of times a TreeTagger process was started.
     *
     * @return the number of times a TreeTagger process was started.
     */
    public
    int getRestartCount()
	{
		return _restartCount;
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
						if (TRACE) {
							System.err.println("["+TreeTaggerWrapper.this+
									"|TRACE] ("+_tokensRead+") START ["+s+"]");
						}
	    				continue;
	    			}

	    			if (ENDOFTEXT.equals(s)) {
						if (TRACE) {
							System.err.println("["+TreeTaggerWrapper.this+
									"|TRACE] ("+_tokensRead+") COMPLETE ["+s+"]");
						}
	    				break;
	    			}

	    			if (inText) {
	    				// Get word and tag
	    				String posTag = null;
	    				String lemma  = null;

	    				// Sometimes TT seems to return odd lines, e.g.
	    				// containing only a tag but no token and no lemma.
	    				// For such cases we only return the original token
	    				// we got, but lemma and pos will be null.
	    				String fields1[] = RE_TAB.split(s, 2);
	    				if (fields1.length == 2) {
		    				final String tags  = fields1[1];
		    				String fields2[] = RE_WHITESPACE.split(tags, 3);
		    				if (fields2.length >= 2) {
			    				posTag = fields2[0].trim().intern();
			    				lemma  = fields2[1].trim();
		    				}
	    				}

	    				// Get original token segment
    					if (tokenIterator.hasNext()) {
	    					O token = tokenIterator.next();
	    					_tokensRead++;
	    					_lastTokenRead = token;
	    					_lastTokenReadTT = s;
    						if (TRACE) {
    							System.err.println("["+TreeTaggerWrapper.this+
    									"|TRACE] ("+_tokensRead+") IN ["+
    									getText(token)+"] -- OUT: ["+s+
    									"] -- POS: ["+posTag+"] -- LEMMA: ["+
    									lemma+"]");
    						}

		    				if (_handler != null) {
								_handler.token(token, posTag, lemma);
	    					}
	    				}
    					else {
							throw new IllegalStateException(
									"["+TreeTaggerWrapper.this+"] Have not seen ENDOFTEXT-marker but no more "+
    								"tokens are available.\n"+
    								"TT returned: ["+s+"]\n"+getStatus());
    					}
	    			}
	    		}
    		}
    		catch (final Exception e) {
    			_exception = e;
    		}

    		synchronized (this) {
        		notifyAll();
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
    				O token = tokenIterator.next();
    				send(getText(token));
    				_lastTokenWritten = token;
    				_tokensWritten++;
    			}

    			send(ENDOFTEXT);
				send("\n.\n"+_model.getFlushSequence()+".\n.\n.\n.\n");
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
