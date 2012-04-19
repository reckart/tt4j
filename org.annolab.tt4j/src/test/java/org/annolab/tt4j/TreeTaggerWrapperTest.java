package org.annolab.tt4j;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

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
		List<String> actual = run(new TreeTaggerWrapper<String>(), 
		        "english-par-linux-3.2.bin:iso8859-1",
		        "This", "is", "a", "test", ".");
		
		List<String> expected = asList(
		        "This DT this", 
		        "is VBZ be", 
		        "a DT a", 
		        "test NN test", 
		        ". SENT .");
		
		assertEquals(expected, actual);
	}

	@Test
	public void testEnglishWithProbabilities() throws Exception
	{
		TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
		tt.setProbabilityThreshold(0.1);
		List<String> actual =  run(tt, "english-par-linux-3.2.bin:iso8859-1", 
		        "This", "is", "a", "test", ".");
		
		List<String> expected = asList(
                "This DT this 1.0", 
                "is VBZ be 1.0", 
                "a DT a 1.0", 
                "test NN test 0.999661", 
                ". SENT . 1.0");
        
        assertEquals(expected, actual);
	}

	private List<String> run(final TreeTaggerWrapper<String> aWrapper, final String aModel, 
	        final String... aTokens)
		throws IOException, TreeTaggerException
	{
		try {
			final List<String> output = new ArrayList<String>();
			aWrapper.setModel(aModel);
			aWrapper.setHandler(new ProbabilityHandler<String>()
			{
			    private String token;
			    
				public void token(String aToken, String aPos, String aLemma)
				{
				    token = aToken;
				    if (aWrapper.getProbabilityThreshold() == null) {
	                    output.add(aToken + " " + aPos + " " + aLemma);
				    }
				}
				
				public void probability(String pos, String lemma, double probability)
				{
                    output.add(token + " " + pos + " " + lemma + " " + probability);
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
