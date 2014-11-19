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

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class TreeTaggerModelTest
{
	@Test
	public void testReadTaggerModelLittleEndianEn() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/data/english-par-linux-3.2-utf8.bin.gz"), 
				"UTF-8");
		
		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_2, model.getVersion());
		assertEquals(59, model.getTags().size());
		assertEquals(243331, model.getLemmas().size());
	}
	
	@Test
	public void testReadChunkerModelLittleEndianEn() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/data/english-chunker-par-linux-3.2-utf8.bin.gz"), 
				"UTF-8");
		
		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_2, model.getVersion());
		assertEquals(174, model.getTags().size());
		assertEquals(3, model.getLemmas().size());
	}

    @Ignore("Test not strictly necessary")
	@Test
	public void testReadTaggerModelLittleEndianNl() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/data/dutch-par-linux-3.2-utf8.bin.gz"), 
				"UTF-8");
		
		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_2, model.getVersion());
		assertEquals(42, model.getTags().size());
		assertEquals(71039, model.getLemmas().size());
	}

	@Ignore("Test not strictly necessary")
	@Test
	public void testReadChunkerModelLittleEndianFr() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("ftp://ftp.ims.uni-stuttgart.de/pub/corpora/french-chunker-par-linux-3.2-utf8.bin.gz"), 
				"UTF-8");
		
		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_1, model.getVersion());
		assertEquals(504, model.getTags().size());
		assertEquals(3, model.getLemmas().size());
	}

	@Ignore("Test not strictly necessary")
	@Test
	public void testReadTaggerModelBigEndianEn() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("ftp://ftp.ims.uni-stuttgart.de/pub/corpora/english-par-3.2.bin.gz"), 
				"ISO-8859-1");
		
		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_2, model.getVersion());
		assertEquals(59, model.getTags().size());
		assertEquals(243335, model.getLemmas().size());
	}

	@Ignore("Test not strictly necessary")
	@Test
	public void testReadTaggerModelBigEndianDe() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("ftp://ftp.ims.uni-stuttgart.de/pub/corpora/german-par-3.2.bin.gz"), 
				"ISO-8859-1");
		
		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_2, model.getVersion());
		assertEquals(54, model.getTags().size());
		assertEquals(544113, model.getLemmas().size());
	}

	@Ignore("Test not strictly necessary")
	@Test
	public void testReadChunkerModelBigEndianEn() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("ftp://ftp.ims.uni-stuttgart.de/pub/corpora/english-chunker-par-3.1.bin.gz"), 
				"ISO-8859-1");

		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_1, model.getVersion());
		assertEquals(172, model.getTags().size());
		assertEquals(3, model.getLemmas().size());
	}
	
	@Ignore("Test not strictly necessary")
	@Test
	public void testReadTaggerModelLittleEndianFr() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("ftp://ftp.ims.uni-stuttgart.de/pub/corpora/french-par-linux-3.2-utf8.bin.gz"), 
				"UTF-8");

		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_2, model.getVersion());
		assertEquals(33, model.getTags().size());
		assertEquals(84146, model.getLemmas().size());
	}
	
	@Ignore("Test not strictly necessary")
	@Test
	public void testReadTaggerModelBigEndianFr() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("ftp://ftp.ims.uni-stuttgart.de/pub/corpora/french-par-3.2.bin.gz"), 
				"ISO-8859-1");

		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_2, model.getVersion());
		assertEquals(33, model.getTags().size());
		assertEquals(84146, model.getLemmas().size());
	}
	
	@Ignore("Test not strictly necessary")
	@Test
	public void testReadTaggerModelLittleEndianRu() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("http://corpus.leeds.ac.uk/mocky/russian.par.gz"), 
				"UTF-8");

		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_2, model.getVersion());
		assertEquals(1378, model.getTags().size());
		assertEquals(115123, model.getLemmas().size());
	}

	@Test
	public void testReadTaggerModelLittleEndianIt() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("http://www.cis.uni-muenchen.de/~schmid/tools/TreeTagger/data/italian-par-linux-3.2-utf8.bin.gz"), 
				"UTF-8");

		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_2, model.getVersion());
		assertEquals(39, model.getTags().size());
		assertEquals(58511, model.getLemmas().size());
	}

	@Ignore("Test not strictly necessary")
	@Test
	public void testReadTaggerModelLittleEndianSw() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("ftp://ftp.ims.uni-stuttgart.de/pub/corpora/swahili-par-linux-3.2.bin.gz"), 
				"ISO-8859-1");

		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_2, model.getVersion());
		assertEquals(74, model.getTags().size());
		assertEquals(110465, model.getLemmas().size());
	}

	@Ignore("Test not strictly necessary")
	@Test
	public void testReadTaggerModelLittleEndianLa() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("ftp://ftp.ims.uni-stuttgart.de/pub/corpora/latin-par-linux-3.2.bin.gz"), 
				"ISO-8859-1");

		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_2, model.getVersion());
		assertEquals(50, model.getTags().size());
		assertEquals(35453, model.getLemmas().size());
	}

	private static void dumpModel(TreeTaggerModel aModel)
	{
		List<String> lemmas = aModel.getLemmas();
		List<String> tokens = aModel.getTokens();
		System.out.printf("Source                    : %s%n", aModel.getSource());
		System.out.printf("Version                   : %d%n", aModel.getVersion());
		System.out.printf("Number of tags            : %d%n", aModel.getTags().size());
		System.out.printf("Tags                      : %s%n", aModel.getTags());
		if (aModel.getTags().get(0).indexOf('/') != -1) {
			System.out.printf("Tags (chunks)             : %s%n", extractChunkTags(aModel));
		}
		System.out.printf("Lemma dictionary size     : %s%n", lemmas.size());
		System.out.printf("      first 10            : %s%n", lemmas.subList(0, Math.min(10, lemmas.size())));
		System.out.printf("      last 10             : %s%n", lemmas.subList(Math.max(0, lemmas.size() - 10), lemmas.size()));
		System.out.printf("Token dictionary size     : %s%n", tokens.size());
		System.out.printf("      first 10            : %s%n", tokens.subList(0, Math.min(10, tokens.size())));
		System.out.printf("      last 10             : %s%n%n", tokens.subList(Math.max(0, tokens.size() - 10), tokens.size()));
	}
	
	private static List<String> extractChunkTags(TreeTaggerModel aModel)
	{
		Set<String> chunkTagSet = new HashSet<String>();
		for (String s : aModel.getTags()) {
			int i = s.indexOf('/');
			if (i != -1) {
				s = s.substring(i+1);
			}
			i = s.indexOf('-');
			if (i != -1) {
				s = s.substring(i+1);
			}
			chunkTagSet.add(s);
		}
		List<String> chunkTags = new ArrayList<String>(chunkTagSet);
		Collections.sort(chunkTags);
		return chunkTags;
	}
	
	@Rule
	public TestName name = new TestName();

	@Before
	public void printSeparator()
	{
		System.out.println("\n=== " + name.getMethodName() + " =====================");
	}
}
