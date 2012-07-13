/*******************************************************************************
 * Copyright (c) 2012 Richard Eckart de Castilho.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     Richard Eckart de Castilho - initial API and implementation
 ******************************************************************************/
package org.annolab.tt4j;

import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.List;
import org.junit.Test;

public class TreeTaggerModelTest
{
	@Test
	public void testReadTaggerModelLittleEndianEn() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("ftp://ftp.ims.uni-stuttgart.de/pub/corpora/english-par-linux-3.2.bin.gz"), 
				"ISO-8859-1");
		
		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_2, model.getVersion());
		assertEquals(59, model.getTags().size());
		assertEquals(243334, model.getDictionary().size());
	}
	
	@Test
	public void testReadChunkerModelLittleEndianEn() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("ftp://ftp.ims.uni-stuttgart.de/pub/corpora/english-chunker-par-linux-3.2.bin.gz"), 
				"ISO-8859-1");
		
		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_2, model.getVersion());
		assertEquals(174, model.getTags().size());
		assertEquals(3, model.getDictionary().size());
	}

	@Test
	public void testReadTaggerModelBigEndianEn() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("ftp://ftp.ims.uni-stuttgart.de/pub/corpora/english-par-3.2.bin.gz"), 
				"ISO-8859-1");
		
		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_2, model.getVersion());
		assertEquals(59, model.getTags().size());
		assertEquals(243335, model.getDictionary().size());
	}

	@Test
	public void testReadChunkerModelBigEndianEn() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("ftp://ftp.ims.uni-stuttgart.de/pub/corpora/english-chunker-par-3.1.bin.gz"), 
				"ISO-8859-1");

		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_1, model.getVersion());
		assertEquals(172, model.getTags().size());
		assertEquals(3, model.getDictionary().size());
	}
	
	@Test
	public void testReadTaggerModelLittleEndianFr() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("ftp://ftp.ims.uni-stuttgart.de/pub/corpora/french-par-linux-3.2-utf8.bin.gz"), 
				"UTF-8");

		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_2, model.getVersion());
		assertEquals(33, model.getTags().size());
		assertEquals(84146, model.getDictionary().size());
	}
	
	@Test
	public void testReadTaggerModelLittleEndianRu() throws Exception
	{
		TreeTaggerModel model = TreeTaggerModelUtil.readModel(
				new URL("http://corpus.leeds.ac.uk/mocky/russian.par.gz"), 
				"UTF-8");

		dumpModel(model);
		
		assertEquals(TreeTaggerModel.VERSION_3_2, model.getVersion());
		assertEquals(1378, model.getTags().size());
		assertEquals(115123, model.getDictionary().size());
	}
	
	
	private static void dumpModel(TreeTaggerModel aModel)
	{
		List<String> dict = aModel.getDictionary();
		System.out.printf("Version                   : %d%n", aModel.getVersion());
		System.out.printf("Number of tags            : %d%n", aModel.getTags().size());
		System.out.printf("Tags                      : %s%n", aModel.getTags());
		System.out.printf("Dictionary size           : %s%n", dict.size());
		System.out.printf("Dictionary first 10 words : %s%n", dict.subList(0, Math.min(10, dict.size())));
		System.out.printf("Dictionary last 10 words  : %s%n%n", dict.subList(Math.max(0, dict.size() - 10), dict.size()));
	}
}
