package org.annolab.tt4j;

import java.io.IOException;


public 
interface ExecutableResolver 
{
	void setPlatformDetector(
			PlatformDetector aPlatform);

	void destroy();

	String getExecutable()
	throws IOException ;

}