package org.annolab.tt4j;

import java.io.File;
import java.io.IOException;

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
	public 
	Model getModel(
			final String aModelName) 
	throws IOException 
	{
		return new Model() {
			private final String _encoding;
			private final File _file;
			private final String _name;
			{
				_name = aModelName;
				String[] fields = aModelName.split(":");
				_file = new File(fields[0]);
				
				if (fields.length > 1) {
					_encoding = fields[1];
				}
				else {
					_encoding = "UTF-8";
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
		// Do nothing
	}
}
