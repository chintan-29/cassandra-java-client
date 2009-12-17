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
	
	@Test
	public void testDecodeAndEncode() {
		SerializingTranscoder trans = new SerializingTranscoder();
		
		String t = "this is my test" ;
		byte[] b = trans.encode(t) ;
		String r = (String) trans.decode(String.class , b);
		
		assertTrue(t.equals(r));
	}
	
	

	@Test
	public void testDecodeAndEncodeEmpty() {
		SerializingTranscoder trans = new SerializingTranscoder();
		
		String t = "" ;
		byte[] b = trans.encode(t) ;
		assertTrue(b.length == 0 );
		String r = (String) trans.decode( String.class , b );
		assertTrue(t.equals(r));
		
		try{
			String nt = null ;
			trans.encode(nt);
			fail("should throw out exception");
		}catch(Exception e){
			
		}
	}


}
