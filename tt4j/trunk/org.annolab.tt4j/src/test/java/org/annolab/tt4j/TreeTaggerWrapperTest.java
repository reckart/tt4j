package org.annolab.tt4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class TreeTaggerWrapperTest
{
	{
		TreeTaggerWrapper.TRACE = true;
	}
	
	@Test
	public void testEnglish() throws Exception
	{
		run(new TreeTaggerWrapper<String>(), "english.par:iso8859-1", "This", "is", "a", "test",
				".");
	}

	@Test
	public void testEnglishWithProbabilities() throws Exception
	{
		TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
		tt.setArguments(new String[] { "-quiet", "-no-unknown", "-sgml", "-token", "-lemma",
				"-prob", "-threshold", "0.1" });
		run(tt, "english.par:iso8859-1", "This", "is", "a", "test", ".");
	}

	private List<String> run(TreeTaggerWrapper<String> aWrapper, String aModel, String... aTokens)
		throws IOException, TreeTaggerException
	{
		try {
			final List<String> output = new ArrayList<String>();
			aWrapper.setModel(aModel);
			aWrapper.setHandler(new TokenHandler<String>()
			{
				public void token(String token, String pos, String lemma)
				{
					output.add(token + " " + pos + " " + lemma);
				}
			});
			aWrapper.process(aTokens);
			return output;
		}
		finally {
			aWrapper.destroy();
		}
	}
}
