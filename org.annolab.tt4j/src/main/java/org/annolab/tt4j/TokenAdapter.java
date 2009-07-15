package org.annolab.tt4j;

/**
 * Adapter to extract a token from the list of objects passed to
 * {@link TreeTaggerWrapper#process(java.util.Collection)}.
 *
 * @author Richard Eckart de Castilho
 *
 * @param <O> the type of object containing the token information.
 */
public
interface TokenAdapter<O>
{
	/**
	 * Extract the token string from the given object.
	 *
	 * @param object and object containing token information.
	 * @return the token string.
	 */
	String getText(
			O object);
}
