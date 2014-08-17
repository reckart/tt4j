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
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public
class RingBufferTest
{
	@Test
	public
	void testRingBuffer()
	{
		RingBuffer rb = new RingBuffer(3);
		String now = rb.toString();
		assertEquals("",  now);
		rb.add("1");
		now = rb.toString();
		assertEquals("1", now);
		rb.add("2");
		now = rb.toString();
		assertEquals("1 2", now);
		rb.add("3");
		now = rb.toString();
		assertEquals("1 2 3", now);
		rb.add("4");
		now = rb.toString();
		assertEquals("2 3 4", now);
	}
	
	@Test
	public
	void testSize()
	{
		RingBuffer rb = new RingBuffer(3);
		for (int i = 1; i <= 20; i++) {
			rb.add("a");
			assertTrue((i < 3 && i == rb.size()) || rb.size() == 3);
		}
	}
}
