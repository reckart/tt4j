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

/**
 * A token handler receives a notification for each tagged token.
 *
 * @author Richard Eckart de Castilho
 *
 * @param <O> the token type.
 */
public
interface TokenHandler<O>
{
	/**
	 * Process a token that TreeTagger has analyzed.
	 *
	 * @param token the one of the token objects passed to
	 *     {@link TreeTaggerWrapper#process(java.util.Collection)}
	 * @param pos the Part-of-Speech tag as produced by TreeTagger or <code>null</code>.
	 * @param lemma the lemma as produced by TreeTagger or <code>null</code>.
	 */
	void token(
			O token,
			String pos,
			String lemma);
}
