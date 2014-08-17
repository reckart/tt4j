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

public class Example
{
	public static void main(String[] args) throws Exception
	{
		// Point TT4J to the TreeTagger installation directory. The executable is expected
		// in the "bin" subdirectory - in this example at "/opt/treetagger/bin/tree-tagger"
		System.setProperty("treetagger.home", "/opt/treetagger");
		TreeTaggerWrapper<String> tt = new TreeTaggerWrapper<String>();
		try {
			tt.setModel("/opt/treetagger/models/english.par:iso8859-1");
			tt.setHandler(new TokenHandler<String>()
			{
				public void token(String token, String pos, String lemma)
				{
					System.out.println(token + "\t" + pos + "\t" + lemma);
				}
			});
			tt.process(new String[] { "This", "is", "a", "test", "." });
		}
		finally {
			tt.destroy();
		}
	}
}
