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

import static org.annolab.tt4j.TreeTaggerModel.VERSION_3_1;
import static org.annolab.tt4j.TreeTaggerModel.VERSION_3_2;

import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 * Reader for TreeTagger model files. 
 * 
 * @author Richard Eckart de Castilho
 */
public class TreeTaggerModelReader
{
	private String charsetName = "UTF-8";
	private InputStream inStream;
	private DataInput in;
	
	private boolean readDictionary = true;
		
	/**
	 * Read the model from a stream.
	 * 
	 * @param aIn an input stream.
	 * @return the decoded model.
	 * @throws IOException if an I/O error occurs.
	 */
	public TreeTaggerModel read(InputStream aIn) throws IOException
	{
		TreeTaggerModel model = new TreeTaggerModel();
		
		inStream = aIn;
		in = new DataInputStream(inStream);
		
		try {
			int version = in.readInt();
			
			// Test big-endian
			if (!decodeVersion(model, version, ByteOrder.BIG_ENDIAN)) {
				// Test little-endian
				if (decodeVersion(model, Integer.reverseBytes(version), ByteOrder.LITTLE_ENDIAN)) {
					in = new LittleEndianDataInputStream(aIn);
				}
				else {
					throw new IllegalStateException("Unknown version or file format");
				}
			}
			
			// Read rest of the header data
			int numberOfTags = -1;
			
			switch (model.getVersion()) {
			case VERSION_3_1:
				in.readInt(); // Unknown
				numberOfTags = in.readInt(); // Number of tags
				break;
			case VERSION_3_2:
				in.readInt(); // Unknown
				in.readInt(); // Unknown
				numberOfTags = in.readInt(); // Number of tags
				break;
			}
			
			// Read tags
			model.setTags(readStrings(numberOfTags));
			
			if (readDictionary) {
				// Read lemma dictionary size
				int lemmaSize = in.readInt();
				model.setLemmas(readStrings(lemmaSize));

				// Read token dictionary size
				int tokenSize = in.readInt();
				
				int marker1 = in.readInt();
				assert 0xFFFFFFFE == marker1; // Assert marker
                byte marker2 = in.readByte();
				assert 0x00 == marker2; // Assert end of block
	
				// Read unknown block
				int c1 = in.readInt(); // Read block size
				for (int c1i = 0; c1i < c1; c1i ++) {
					in.readInt(); // Unknown
					in.readInt(); // Unknown
					in.readInt(); // Unknown
				}
				in.readInt(); // Unknown
				byte marker3 = in.readByte();
				assert 0x00 == marker3; // Assert end of block
	
				// Read unknown block
				int c2 = in.readInt(); // Read block size
				in.readInt(); // Unknown
				for (int band = 0; band < 3; band ++) {
					for (int c2i = 0; c2i < c2; c2i ++) {
						in.readInt(); // Unknown
					}
				}
	
				List<String> tokens = new ArrayList<String>();
				for (int ct = 0; ct < tokenSize; ct ++) {
					String token = readZeroTerminatedString(charsetName);
					tokens.add(token);
					
					// Read token data
					int bsize = in.readInt(); // Block size size
					in.readInt(); // Unknown
					for (int cb = 0; cb < bsize; cb++) {
						in.readInt(); // Unknown
						in.readInt(); // Unknown
						in.readInt(); // Unknown
					}
				}
				model.setTokens(tokens);
			}

			return model;
		}
		finally {
			inStream = null;
			in = null;
		}
	}
	
	protected List<String> readStrings(int aCount) throws IOException
	{
		List<String> tags = new ArrayList<String>();
		
		for (int i = 0; i < aCount; i++) {
			String tag = readZeroTerminatedString(charsetName);
			tags.add(tag);
		}
		
		return tags;
	}
	
	protected boolean decodeVersion(TreeTaggerModel aModel, int aVersion, ByteOrder aByteOrder)
	{
		switch (aVersion) {
		case VERSION_3_1:
			// Fall-through
		case VERSION_3_2:
			aModel.setVersion(aVersion);
			aModel.setByteOrder(aByteOrder);
			return true;
		}
		
		return false;
	}
	
	protected String readZeroTerminatedString(String aCharsetName)
		throws IOException
	{
		return new String(readZeroTerminatedByteArray(), aCharsetName);
	}
	
	protected byte[] readZeroTerminatedByteArray() throws IOException
	{
		int bytesRead = 0;
		byte[] buffer = new byte[128];
		int b;
		while ((b = inStream.read()) != -1) {
			// Finished / zero terminated
			if (b == 0) {
				// Shrink buffer
				byte[] buf = buffer;
				buffer = new byte[bytesRead];
				System.arraycopy(buf, 0, buffer, 0, bytesRead);
				return buffer;
			}
			
			// Extend buffer
			if (bytesRead == buffer.length) {
				byte[] buf = buffer;
				buffer = new byte[buf.length + 128];
				System.arraycopy(buf, 0, buffer, 0, buf.length);
			}
			
			buffer[bytesRead] = (byte) b;
			bytesRead++;
		}
		
		throw new IOException("Unexpected end of file.");
	}

	/**
	 * Get the encoding used for reading the dictionary. This information need to be provided
	 * externally, it is not present in the TreeTagger model file. Per default, the UTF-8
	 * character set is used. 
	 * 
	 * @return the encoding.
	 */
	public String getEncoding()
	{
		return charsetName;
	}

	/**
	 * Set the encoding used by the dictionary.
	 * 
	 * @param aCharsetName the encoding.
	 */
	public void setEncoding(String aCharsetName)
	{
		charsetName = aCharsetName;
	}

	/**
	 * Check if the dictionary is read or skipped. Per default the dictionary is read. 
	 * 
	 * @return if the dictionary is read.
	 */
	public boolean isReadDictionary()
	{
		return readDictionary;
	}

	/**
	 * Set if the dictionary is read or skipped.
	 * 
	 * @param aReadDictionary if the dictionary is read.
	 */
	public void setReadDictionary(boolean aReadDictionary)
	{
		readDictionary = aReadDictionary;
	}
}
