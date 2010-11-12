/*******************************************************************************
 * Copyright (c) 2009-2010 Richard Eckart de Castilho.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Contributors:
 *     Richard Eckart de Castilho - initial API and implementation
 ******************************************************************************/
package org.annolab.tt4j;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

public
class DefaultModelResolverTest
{
	@Test
	public
	void testGetModel()
	throws Exception
	{
		verifyModel("src/test/resources/dummyModel.par", null, true);
		verifyModel("src/test/resources/dummyModel.par", "UTF-8", true);
		verifyModel("C:\\dummyModel.par", null, false);
		verifyModel("C:/dummyModel.par", null, false);
		verifyModel("C:\\dummyModel.par", "UTF-8", false);
		verifyModel("C:/dummyModel.par", "UTF-8", false);
	}

	@Test(expected=IOException.class)
	public
	void testGetModelFail()
	throws Exception
	{
		verifyModel("file:src/test/resources/dummyModel.par", null, true);
	}


	private
	void verifyModel(
			final String aModelPath,
			final String aEncoding,
			final boolean aCheckExistence)
	throws IOException
	{
		String modelName = aModelPath;
		if (aEncoding != null) {
			modelName = modelName + ":" + aEncoding;
		}
		DefaultModelResolver res = new DefaultModelResolver();
		res._checkExistence = aCheckExistence;
		Model model = res.getModel(modelName);

		assertEquals(aModelPath, model.getFile().getPath());
		assertEquals(modelName, model.getName());
		assertEquals(aEncoding != null ? aEncoding : "UTF-8", model.getEncoding());
	}
}
