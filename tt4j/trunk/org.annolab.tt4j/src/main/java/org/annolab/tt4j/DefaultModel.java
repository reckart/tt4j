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

import java.io.File;
import java.io.IOException;

public
class DefaultModel
implements Model
{
    public static final String DEFAULT_FLUSH_SEQUENCE = "\n.\n.\n.\n.\n.\n(\n)\n.\n.\n.\n.\n";
    
	private String _encoding;
	private File _file;
	// Issue 6 - We need the "()" to flush properly when using the chinese model
	private String _flushSequence;
	private String _name;

	public
	DefaultModel(
			final String aName,
			final File aFile,
			final String aEncoding)
	{
	    this(aName, aFile, aEncoding, DEFAULT_FLUSH_SEQUENCE);
	}

	   public
	    DefaultModel(
	            final String aName,
	            final File aFile,
	            final String aEncoding,
	            final String aFlushSequence)
	    {
	        _name = aName;
	        _file = aFile;
	        _encoding = aEncoding;
	        _flushSequence = aFlushSequence;
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
