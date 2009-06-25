package org.annolab.tt4j;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.*;

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
			List<String> aAdditionalPaths)
	{
		_additionalPaths.clear();
		_additionalPaths.addAll(aAdditionalPaths);
	}

	public
	String getExecutable()
	throws IOException
	{
		List<String> paths = new ArrayList<String>();
		if (System.getProperty("treetagger.home") != null) {
			paths.add(System.getProperty("treetagger.home")+"/bin");
		}
		if (System.getenv("TAGDIR") != null) {
			paths.add(System.getenv("TAGDIR")+"/bin");
		}
		if (System.getenv("TREETAGGER_HOME") != null) {
			paths.add(System.getenv("TREETAGGER_HOME")+"/bin");
		}
		String path = System.getenv("PATH");
		if (path != null) {
			paths.addAll(asList(path.split(File.pathSeparator)));
		}
		paths.addAll(_additionalPaths);

		for (String p : paths) {
			if (p == null) {
				continue;
			}

			File exe = new File(p+"/"+"tree-tagger"+_platform.getExecutableSuffix());
			if (exe.exists()) {
				return exe.getAbsolutePath();
			}
		}

		throw new IOException("Unable to locate tree-tagger binary");
	}

	public
	void setPlatformDetector(
			PlatformDetector aPlatform)
	{
		_platform = aPlatform;
	}
}
