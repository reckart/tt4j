package org.annolab.tt4j;

import java.io.IOException;

/**
 * Resolve the location of the TreeTagger executable.
 *
 * @author Richard Eckart de Castilho
 */
public
interface ExecutableResolver
{
	/**
	 * Set platform information.
	 *
	 * @param aPlatform the platform information.
	 */
	void setPlatformDetector(
			PlatformDetector aPlatform);

	/**
	 * Destroy transient resources for the executable file. E.g. if the file
	 * was extracted to a temporary location from an archive/classpath, it can
	 * be deleted by this method.
	 */
	void destroy();

	/**
	 * Get the executable file. If necessary the file can be provided in a
	 * temporary location by this method.
	 *
	 * @return the executable file.
	 * @throws IOException if the file cannot be located/provided.
	 */
	String getExecutable()
	throws IOException ;
}