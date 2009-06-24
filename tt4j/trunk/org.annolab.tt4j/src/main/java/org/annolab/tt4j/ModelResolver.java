package org.annolab.tt4j;

import java.io.IOException;

public 
interface ModelResolver 
{
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