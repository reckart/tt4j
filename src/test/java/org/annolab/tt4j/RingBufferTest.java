package org.annolab.tt4j;

import static org.junit.Assert.assertEquals;

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
}
