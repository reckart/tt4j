package org.annolab.tt4j;

import java.io.IOException;

/**
 * Resolve the location of the TreeTagger model.
 *
 * @author Richard Eckart de Castilho
 */
public
interface ModelResolver
{
	/**
	 * Set platform information.
	 *
	 * @param aPlatform the platform information.
	 */
	void setPlatformDetector(
			PlatformDetector aPlatform);

	/**
	 * Load the model with the given name.
	 *
	 * @param modelName the name of the model.
	 * @return the model.
	 * @throws IOException if the model can not be found.
	 */
	Model getModel(
			String modelName)
	throws IOException;
}