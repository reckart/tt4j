package org.annolab.tt4j;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static java.io.File.separator;

/**
 * Simple model provider. The model name is actually the path to the model.
 * The path has to be followed by a ":" and the name model encoding. Example
 * <code>/usr/lib/model.par:UTF-8</code>.
 *
 * @author Richard Eckart
 */
public
class DefaultModelResolver
implements ModelResolver
{
	protected PlatformDetector _platform;

	protected List<String> _additionalPaths = new ArrayList<String>();

	public
	void setAdditionalPaths(
			List<String> aAdditionalPaths)
	{
		_additionalPaths.clear();
		_additionalPaths.addAll(aAdditionalPaths);
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
				String[] fields = aModelName.split(":");

				// The using the name as path
				_encoding =  (fields.length > 1) ? fields[1] : "UTF-8";

				List<String> paths = new ArrayList<String>();
				if (System.getProperty("treetagger.home") != null) {
					paths.add(System.getProperty("treetagger.home")+separator+"models");
				}
				if (System.getenv("TAGDIR") != null) {
					paths.add(System.getenv("TAGDIR")+"/models");
				}
				if (System.getenv("TREETAGGER_HOME") != null) {
					paths.add(System.getenv("TREETAGGER_HOME")+separator+"models");
				}
				String path = System.getenv("PATH");
				if (path != null) {
					paths.addAll(asList(path.split(File.pathSeparator)));
				}
				paths.addAll(_additionalPaths);

				boolean found = false;
				for (String p : paths) {
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
			PlatformDetector aPlatform)
	{
		_platform = aPlatform;
	}
}
