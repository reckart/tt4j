package org.annolab.tt4j;

public 
interface TokenHandler<O>
{
	void token(
			O token,
			String pos,
			String lemma);
}
