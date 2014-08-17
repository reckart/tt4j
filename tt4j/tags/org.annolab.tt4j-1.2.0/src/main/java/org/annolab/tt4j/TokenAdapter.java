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
 * Adapter to extract a token from the list of objects passed to
 * {@link TreeTaggerWrapper#process(java.util.Collection)}.
 *
 * @author Richard Eckart de Castilho
 *
 * @param <O> the type of object containing the token information.
 */
public
interface TokenAdapter<O>
{
	/**
	 * Extract the token string from the given object.
	 *
	 * @param object and object containing token information.
	 * @return the token string.
	 */
	String getText(
			O object);
}
