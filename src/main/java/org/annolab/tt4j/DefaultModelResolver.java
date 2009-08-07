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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static java.io.File.separator;
import static org.annolab.tt4j.Util.*;

/**
 * Simple model provider. The model name is actually the path to the model.
 * The path has to be followed by a ":" and the name model encoding. Example
 * {@code /usr/lib/model.par:UTF-8}.
 *
 * @author Richard Eckart de Castilho
 */
public
class DefaultModelResolver
implements ModelResolver
{
	protected PlatformDetector _platform;

	protected List<String> _additionalPaths = new ArrayList<String>();

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

	/**
	 * Get platform information.
	 *
	 * @return platform information.
	 */
	public
	PlatformDetector getPlatformDetector()
	{
		return _platform;
	}

	public
	Model getModel(
			final String aModelName)
	throws IOException
	{
		return new Model() {
			private final String _encoding;
			private File _file;
			private final String _name;
			{
				_name = aModelName;
				final String[] fields = aModelName.split(":");

				// The using the name as path
				_encoding =  (fields.length > 1) ? fields[1] : "UTF-8";

				if (new File(fields[0]).exists()) {
					_file = new File(fields[0]);

				}
				else {
					boolean found = false;
					for (final String p : getSearchPaths(_additionalPaths, "models")) {
						if (p == null) {
							continue;
						}

						_file = new File(p+separator+fields[0]);
						if (_file.exists()) {
							found = true;
							break;
						}
					}

					if (!found) {
						throw new IOException("Unable to locate model ["+fields[0]+"]");
					}
				}
			}

			public
			String getName()
			{
				return _name;
			}

			public
			void destroy()
			{
				// Do nothing
			}

			public
			String getEncoding()
			{
				return _encoding;
			}

			public
			File getFile() {
				return _file;
			}

			public
			String getFlushSequence()
			{
				return ".\n.\n.\n.\n";
			}

			public
			void install()
			throws IOException
			{
				// Do nothing.
			}

		};
	}

	public
	void setPlatformDetector(
			final PlatformDetector aPlatform)
	{
		_platform = aPlatform;
	}
}
