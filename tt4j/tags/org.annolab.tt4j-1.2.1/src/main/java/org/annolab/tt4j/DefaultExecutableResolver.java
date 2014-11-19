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

import static java.io.File.separator;
import static org.annolab.tt4j.Util.getSearchPaths;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Assume that TreeTagger is installed and available in the path.
 *
 * @author Richard Eckart de Castilho
 */
public
class DefaultExecutableResolver
implements ExecutableResolver
{
	protected PlatformDetector _platform;
	protected List<String> _additionalPaths = new ArrayList<String>();

	public
	void destroy()
	{
		// Do nothing
	}

	/**
	 * Set additional paths that will be used for searching the TreeTagger
	 * executable.
	 *
	 * @param aAdditionalPaths list of additional paths.
	 * @see Util#getSearchPaths(List, String)
	 */
	public
	void setAdditionalPaths(
			final List<String> aAdditionalPaths)
	{
		_additionalPaths.clear();
		_additionalPaths.addAll(aAdditionalPaths);
	}

	public
	String getExecutable()
	throws IOException
	{
		Set<String> searchedIn = new HashSet<String>();
		for (final String p : getSearchPaths(_additionalPaths, "bin")) {
			if (p == null) {
				continue;
			}

			final File exe = new File(p+separator+"tree-tagger"+_platform.getExecutableSuffix());
			searchedIn.add(exe.getAbsolutePath());
			if (exe.exists()) {
				return exe.getAbsolutePath();
			}
		}

		throw new IOException("Unable to locate tree-tagger binary in the following locations " +
				searchedIn + ". Make sure the environment variable 'TREETAGGER_HOME' or " +
				"'TAGDIR' or the system property 'treetagger.home' point to the TreeTagger " +
				"installation directory.");
	}

	/**
	 * Set platform information.
	 */
	public
	void setPlatformDetector(
			final PlatformDetector aPlatform)
	{
		_platform = aPlatform;
	}
}
