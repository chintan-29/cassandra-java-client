package org.yosemite.jcsadra.helper;

import static org.junit.Assert.*;

import org.junit.Test;

public class SerializingTranscoderTest {
	
	
	

	@Test
	public void testDecodeString() {
		
		SerializingTranscoder trans = new SerializingTranscoder();
		
		String s = "this is my test string";
		byte[] t1 = trans.encode(s) ;
		
		assertTrue(t1.length == 22) ;
		String s1 = (String) trans.decode(String.class , t1);
		assertTrue(s.equals(s1));
		
		
		s = "" ;
		byte[] t2 = trans.encode(s) ;
		
		assertTrue(t2.length == 0) ;
		String s2 = (String) trans.decode(String.class , t2);
		assertTrue(s.equals(s2));
		
	}

}
