package org.annolab.tt4j;

/**
 * A token handler receives a notification for each tagged token.
 *
 * @author Richard Eckart de Castilho
 *
 * @param <O> the token type.
 */
public
interface TokenHandler<O>
{
	/**
	 * Processs a token that TreeTagger has analyzed.
	 *
	 * @param token the one of the token objects passed to
	 *     {@link TreeTaggerWrapper#process(java.util.Collection)}
	 * @param pos the Part-of-Speech tag as produced by TreeTagger.
	 * @param lemma the lemma as produced by TreeTagger.
	 */
	void token(
			O token,
			String pos,
			String lemma);
}
