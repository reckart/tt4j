/*******************************************************************************
 * Copyright (c) 2009-2014 Richard Eckart de Castilho.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Richard Eckart de Castilho - initial API and implementation
 ******************************************************************************/
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
