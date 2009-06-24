package org.annolab.tt4j;

import java.io.Closeable;
import java.io.IOException;


public 
class Util
{
    public static 
	String join(
			String[] s, 
			String sep)
	{
		StringBuilder sb = new StringBuilder();
	
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
	    	catch (IOException e) {
	    		// Ignore
	    	}
    	}
    }
}
