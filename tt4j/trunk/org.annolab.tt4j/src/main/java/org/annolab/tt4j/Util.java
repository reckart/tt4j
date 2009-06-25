package org.annolab.tt4j;

import static java.io.File.separator;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public
class Util
{
	public static
	List<String> getSearchPaths(
			final List<String> aAdditionalPaths)
	{
		final List<String> paths = new ArrayList<String>();
		paths.addAll(aAdditionalPaths);
		if (System.getProperty("treetagger.home") != null) {
			paths.add(System.getProperty("treetagger.home")+separator+"models");
		}
		if (System.getenv("TREETAGGER_HOME") != null) {
			paths.add(System.getenv("TREETAGGER_HOME")+separator+"models");
		}
		if (System.getenv("TAGDIR") != null) {
			paths.add(System.getenv("TAGDIR")+"/models");
		}
//		String path = System.getenv("PATH");
//		if (path != null) {
//			paths.addAll(asList(path.split(File.pathSeparator)));
//		}
		return paths;
	}

    public static
	String join(
			final String[] s,
			final String sep)
	{
		final StringBuilder sb = new StringBuilder();

		for (int i = 0; i < s.length; i++) {
			sb.append(s[i]);
			if (i < s.length - 1) {
				sb.append(sep);
			}
		}

		return sb.toString();
	}

    public static
    void close(
    		final Closeable aClosable)
    {
    	if (aClosable != null) {
	    	try {
	    		aClosable.close();
	    	}
	    	catch (final IOException e) {
	    		// Ignore
	    	}
    	}
    }
}
