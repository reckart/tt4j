package org.annolab.tt4j;

import java.io.File;
import java.io.IOException;

public
class DefaultModel
implements Model
{
	private String _encoding;
	private File _file;
	private String _flushSequence = ".\n.\n.\n.\n";
	private String _name;

	public
	DefaultModel(
			final String aName,
			final File aFile,
			final String aEncoding)
	{
		_name = aName;
		_file = aFile;
		_encoding = aEncoding;
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
	File getFile()
	{
		return _file;
	}

	public
	String getFlushSequence()
	{
		return _flushSequence;
	}

	public
	String getName()
	{
		return _name;
	}

	public
	void install()
	throws IOException
	{
		// Do nothing
	}
}
