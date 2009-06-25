package org.annolab.tt4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.io.File.separator;
import static org.annolab.tt4j.Util.getSearchPaths;

/**
 * Assume that TreeTagger is installed and available in the path.
 *
 * @author Richard Eckart
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
		for (final String p : getSearchPaths(_additionalPaths)) {
			if (p == null) {
				continue;
			}

			final File exe = new File(p+separator+"tree-tagger"+_platform.getExecutableSuffix());
			if (exe.exists()) {
				return exe.getAbsolutePath();
			}
		}

		throw new IOException("Unable to locate tree-tagger binary");
	}

	public
	void setPlatformDetector(
			final PlatformDetector aPlatform)
	{
		_platform = aPlatform;
	}
}
