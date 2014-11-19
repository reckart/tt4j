/*******************************************************************************
 * Copyright (c) 2012-2014 Richard Eckart de Castilho.
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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class TreeTaggerModelUtil
{
	/**
	 * Read the tagset from a model.
	 * 
	 * @param aModelFile the model file.
	 * @param aCharsetName the model encoding (optional).
	 * @return the tag set.
	 * @throws IOException if an I/O error occurs.
	 */
	public static 
	List<String> getTagset(
			final File aModelFile, 
			final String aCharsetName)
	throws IOException
	{
		InputStream is = null;
		try {
			return getTagset(new FileInputStream(aModelFile), aCharsetName);
		}
		finally {
			Util.close(is);
		}
	}

	/**
	 * Read the tagset from a model.
	 * 
	 * @param aInputStream the model file.
	 * @param aCharsetName the model encoding (optional).
	 * @return the tag set.
	 * @throws IOException if an I/O error occurs.
	 */
	public static 
	List<String> getTagset(
			final InputStream aInputStream, 
			final String aCharsetName) 
	throws IOException
	{
		InputStream is = new BufferedInputStream(aInputStream);
			
		TreeTaggerModelReader reader = new TreeTaggerModelReader();
		reader.setReadDictionary(false);
		if (aCharsetName != null) {
			reader.setEncoding(aCharsetName);
		}
		TreeTaggerModel model = reader.read(is);

		return model.getTags();
	}

	/**
	 * Read a model from a file.
	 * 
	 * @param aFile the file to load the model from. Supports gzipped models when the file name ends
	 *        in ".gz".
	 * @param aCharsetName the model encoding (optional).
	 * @return the model.
	 * @throws IOException if an I/O error occurs.
	 */
	public static
	TreeTaggerModel readModel(
			final File aFile,
			final String aCharsetName)
	throws IOException
	{
		InputStream is = null;
		try {
			is = new FileInputStream(aFile);
			if (aFile.getName().toLowerCase().endsWith(".gz")) {
				is = new GZIPInputStream(is);
			}
			TreeTaggerModel model = readModel(is, aCharsetName);
			model.setSource(aFile.toString());
			return model;
		}
		finally {
			Util.close(is);
		}
	}

	/**
	 * Read a model from an URL.
	 * 
	 * @param aUrl the URL to load the model from. Supports gzipped models when the URL ends in
	 *        ".gz".
	 * @param aCharsetName the model encoding (optional).
	 * @return the model.
	 * @throws IOException if an I/O error occurs.
	 */
	public static
	TreeTaggerModel readModel(
			final URL aUrl,
			final String aCharsetName)
	throws IOException
	{
		InputStream is = null;
		try {
			is = aUrl.openStream();
			if (aUrl.getFile().toLowerCase().endsWith(".gz")) {
				is = new GZIPInputStream(is);
			}
			TreeTaggerModel model = readModel(is, aCharsetName);
			model.setSource(aUrl.toString());
			return model;
		}
		finally {
			Util.close(is);
		}
	}

	/**
	 * Read a model.
	 * 
	 * @param aInputStream the stream to read the model from. The stream is not closed after 
	 *        reading.
	 * @param aCharsetName the model encoding (optional).
	 * @return the model.
	 * @throws IOException if an I/O error occurs.
	 */
	public static 
	TreeTaggerModel readModel(
			final InputStream aInputStream, 
			final String aCharsetName) 
	throws IOException
	{
		InputStream is = new BufferedInputStream(aInputStream);

		TreeTaggerModelReader reader = new TreeTaggerModelReader();
		if (aCharsetName != null) {
			reader.setEncoding(aCharsetName);
		}
		TreeTaggerModel model = reader.read(is);

		return model;
	}
}
