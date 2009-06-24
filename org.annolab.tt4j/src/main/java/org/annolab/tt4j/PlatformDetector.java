package org.annolab.tt4j;

import java.nio.ByteOrder;

public 
class PlatformDetector
{
	private String _arch = "";
	private String _os = "";
	private String _executableSuffix = "";
	private ByteOrder _byteOrder = ByteOrder.nativeOrder();
	private String[] _chmodCmd;
	
	{
		updatePlatform(
				System.getProperties().getProperty("os.name"),
				System.getProperties().getProperty("os.arch"),
				ByteOrder.nativeOrder());
	}
	
	/**
	 * Override the operating system name.
	 * This should only be used in test cases.
	 * 
	 * @param aArch an OS name as could be found in the os.name system
	 * 		  property.
	 */
	public 
	void setOs(
			String aOs)
	{
		updatePlatform(aOs, _arch, _byteOrder);
	}
	
	public 
	String getOs() 
	{
		return _os;
	}

	/**
	 * Override the architecture.
	 * This should only be used in test cases.
	 * 
	 * @param aArch an architecture name as could be found in the os.arch system
	 * 		  property.
	 */
	public 
	void setArch(
			String aArch) 
	{
		updatePlatform(_os, aArch, _byteOrder);
	}
	
	public 
	String getArch() 
	{
		return _arch;
	}
	
	/**
	 * Set the byte order. TreeTagger models are sensitive to the byte order.
	 * This should only be used in test cases.
	 * 
	 * @param aByteOrder the byte order.
	 */
	public
	void setByteOrder(
			ByteOrder aByteOrder)
	{
		updatePlatform(_os, _arch, aByteOrder);
	}
	
	/**
	 * Get the file suffix used for executable files on the currently configured
	 * platform.
	 * 
	 * @return the file suffix used for executable files.
	 */
	public 
	String getExecutableSuffix() 
	{
		return _executableSuffix;
	}

	/**
	 * Get the byte order.
	 * 
	 * @return the byte order.
	 */
	public
	String getByteOrder()
	{
		return _byteOrder.toString().replace("_", "-").toLowerCase();
	}

	public
	String getPlatformId() 
	{
		return _os+"-"+_arch;
	}

    /**
     * Updates the platform-specific settings and normalizes them.
     */
    public
    void updatePlatform(
    		final String aOs,
    		final String aArch,
    		final ByteOrder aByteOrder)
    {
    	_os = aOs.toLowerCase();
    	_arch = aArch.toLowerCase();
    	String[] chmod = { "chmod", "755", null };
    	
    	// Resolve arch "synonyms"
    	if (
    			_arch.equals("x86") ||
    			_arch.equals("i386") ||
    			_arch.equals("i486") ||
    			_arch.equals("i586") ||
    			_arch.equals("i686")
    	) {
    		_arch = "x86_32";
    	}
    	if (_arch.equals("powerpc")) {
    		_arch = "ppc";
    	}
    	
    	// Resolve name "synonyms"
    	if (_os.startsWith("windows")) {
    		_os = "windows";
    		_executableSuffix = ".exe";
    		chmod  = null;
    	}
    	if (_os.startsWith("mac")) {
    		_os = "osx";
    	}
    	if (_os.startsWith("linux")) {
    		_os = "linux";
    	}
    	if (_os.startsWith("sun")) {
    		_os = "solaris";
    	}
    	
    	_chmodCmd = chmod;
    	
    	_byteOrder = aByteOrder;
    }    
    
    public 
    String[] getChmodCmd() 
    {
		return _chmodCmd;
	}
}
