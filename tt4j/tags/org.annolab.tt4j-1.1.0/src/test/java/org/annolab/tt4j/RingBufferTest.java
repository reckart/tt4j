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
