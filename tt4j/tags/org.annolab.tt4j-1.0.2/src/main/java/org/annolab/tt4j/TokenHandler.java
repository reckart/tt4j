package org.annolab.tt4j;

/**
 * A token handler recieves a notification for each tagged token.
 *
 * @author Richard Eckart de Castilho
 *
 * @param <O> the token type.
 */
public
interface TokenHandler<O>
{
	void token(
			O token,
			String pos,
			String lemma);
}
