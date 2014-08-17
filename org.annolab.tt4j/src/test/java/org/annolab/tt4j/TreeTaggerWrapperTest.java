/*******************************************************************************
 * Copyright (c) 2009-2014 Richard Eckart de Castilho.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 * Contributors:
 *     Richard Eckart de Castilho - initial API and implementation
 ******************************************************************************/
package org.annolab.tt4j;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Assume;
import org.junit.Test;

public class TreeTaggerWrapperTest
{
	@Test
	public 
	void testEnglish() 
	throws Exception
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
	public 
	void testBruteCharsUTF8()
	throws Exception
	{
		System.out.println("--- UTF8 (no probability) ---");
		testBruteChars("german-par-linux-3.2-utf8.bin:utf-8", false);
		System.out.println("--- UTF8 (with probability) ---");
		testBruteChars("german-par-linux-3.2-utf8.bin:utf-8", true);
	}

	@Test
	public 
	void testBruteCharsISO8859()
	throws Exception
	{
		System.out.println("--- ISO8859 (no probability)  ---");
		testBruteChars("english-par-linux-3.2.bin:iso8859-1", false);
		System.out.println("--- ISO8859 (with probability)  ---");
		testBruteChars("english-par-linux-3.2.bin:iso8859-1", true);
	}

	public 
	void testBruteChars(
			final String aModel,
			final boolean aWithProbability)
	throws Exception
	{
		Assume.assumeTrue(System.getenv("TREETAGGER_HOME") != null);
		
		TreeTaggerWrapper.TRACE = false;

		TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
		if (aWithProbability) {
			tt.setProbabilityThreshold(0.1);
		}
		tt.setModel(aModel);
		
		int exceptionCount = 0;
		int[] cp = new int[1];
		int lastGood = -1;
		int lastLog = 0;
		for (int c = 0x0; c <= Character.MAX_CODE_POINT; c++) {
			try {
				cp[0] = c;
				tt.process(new String[] { new String(cp, 0, 1) } );
				
				if (c <= 0xFFFF && lastGood != c-1) {
					System.out.printf("[0x%08X] - [0x%08X] causes problems %n", lastGood + 1, c-1);
				}
				lastGood = cp[0];
				if (c > 0xFFFF) {
					// Faster scanning above 16bit
					c += 12;
				}
				if (c / 0x1000 > lastLog) {
					System.out.printf("Progress: %08X%n", cp[0]);
					lastLog = c / 0x1000;
				}
			}
			catch (TreeTaggerException e) {
				System.out.printf("[0x%08X] - %s%n", cp[0], e.getMessage());
				tt.destroy();
				if (aWithProbability) {
					tt.setProbabilityThreshold(0.1);
				}
				tt.setModel(aModel);
				exceptionCount++;
			}
		}
		assertEquals(0,  exceptionCount);
	}

	@Test
	public 
	void testGermanText()
	throws Exception
	{
		String text = Util.readFile(new File("src/test/resources/text/test-de.txt"), "UTF-8");
		String actual = Util.join(run(new TreeTaggerWrapper<String>(), 
		        "german-par-linux-3.2-utf8.bin:utf-8",
		        Util.tokenize(text, Locale.GERMAN)), "\n");
		
		String expected = Util.readFile(new File("src/test/resources/text/test-de-expected.txt"), "UTF-8");
		Util.writeFile(actual, new File("target/test-output/test-de-actual.txt"), "UTF-8");

		assertEquals(expected, actual);
	}

	@Test
	public 
	void testEnglishWithProbabilities()
	throws Exception
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

    @Test
    public 
    void testEnglishWithProbabilities2()
    throws Exception
    {
        TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
        tt.setProbabilityThreshold(0.1);
        List<String> actual =  run(tt, "english-par-linux-3.2.bin:iso8859-1", 
                "He", "could", "lead", "if", "he", "would", "get", "the", "lead", "out", ".");
        
        List<String> expected = asList(
                "He PP he 1.0",
                "could MD could 1.0",
                "lead VV lead 0.999748",
                "if IN if 1.0",
                "he PP he 1.0",
                "would MD would 1.0",
                "get VV get 1.0",
                "the DT the 0.999993",
                "lead NN lead 0.753085",
                "lead VV lead 0.103856",
                "out RP out 0.726204",
                "out IN out 0.226546",
                ". SENT . 1.0");
        
        assertEquals(expected, actual);
    }

    @Test
    public
    void testEnglishWithProbabilities4()
    throws Exception
    {
    	TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
        tt.setProbabilityThreshold(0.1);
        List<String> actual =  run(tt, "english-par-linux-3.2.bin:iso8859-1", 
               "lead");
       
        List<String> expected = asList(
               "lead NN lead 0.647454",
               "lead VV lead 0.196787",
               "lead JJ lead 0.142647");
       
        assertEquals(expected, actual);
    }
	

	private 
	List<String> run(
			final TreeTaggerWrapper<String> aWrapper, 
			final String aModel, 
	        final String... aTokens)
	throws IOException, TreeTaggerException
	{
		Assume.assumeTrue(System.getenv("TREETAGGER_HOME") != null);

		TreeTaggerWrapper.TRACE = true;

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
