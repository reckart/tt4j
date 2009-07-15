package org.annolab.tt4j;

import java.io.File;
import java.io.IOException;

/**
 * A TreeTagger model.
 *
 * @author Richard Eckart de Castilho
 */
public
interface Model
{
	/**
	 * Get the name of the model.
	 *
	 * @return the model name.
	 */
	String getName();

	/**
	 * Install the model to the file system (if necessary).
	 *
	 * @throws IOException if the model cannot be installed.
	 */
	void install()
	throws IOException;

	/**
	 * Get the location of the model. Unless {@link #install()} is called before,
	 * the model may not actually be present at this location.
	 *
	 * @return the model location.
	 */
	File getFile();

	/**
	 * Get the model encoding.
	 *
	 * @return the model encoding.
	 */
	String getEncoding();

	/**
	 * The the token sequence used to flush the TreeTagger state for the
	 * given model. Usually this is a short full sentence in the language the
	 * model is trained on. The returned string needs to contain each token
	 * separated by a space including a full stop, e.g.
	 * {@literal "This is a sentence ."}.
	 *
	 * @return the flush sequence.
	 */
	String getFlushSequence();

	/**
	 * Destroy transient resources for the model. E.g. if the model
	 * was extracted to a temporary location from an archive/classpath, it can
	 * be deleted by this method.
	 */
	void destroy();
}