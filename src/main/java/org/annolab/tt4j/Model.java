package org.annolab.tt4j;

import java.io.File;
import java.io.IOException;

public
interface Model
{
	String getName();

	void install()
	throws IOException;

	File getFile();

	String getEncoding();

	String getFlushSequence();

	void destroy();
}