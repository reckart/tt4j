package org.annolab.tt4j;

/**
 * Exception throw if an error occurs while tagging is in process.
 *
 * @author Richard Eckart de Castilho
 */
public
class TreeTaggerException
extends Exception
{
	private static final long serialVersionUID = -862590343816183238L;

	public
	TreeTaggerException(
			final String aMessage)
	{
		super(aMessage);
	}

	public
	TreeTaggerException(
			final Throwable aCause)
	{
		super(aCause);
	}

	public
	TreeTaggerException(
			final String aMessage,
			final Throwable aCause)
	{
		super(aMessage, aCause);
	}
}
