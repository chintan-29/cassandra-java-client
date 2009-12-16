package org.yosemite.jcsadra.helper;

import static org.junit.Assert.*;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.yosemite.jcsadra.utils.TranscoderUtils;

public class TranscoderUtilsTest {

	TranscoderUtils utils ;
	Random ra ;
	
	
	@Before
	public void setupUtils(){
		 utils = new TranscoderUtils(false);
		 ra = new Random(System.currentTimeMillis());
	}

	@Test
	public void testEncodeLong() {
		long lo = ra.nextLong() ;
		byte[] lob = utils.encodeLong(lo);
		long lo2 = utils.decodeLong(lob);
		assertTrue(lo == lo2 );
		
		
		long max = Long.MAX_VALUE ;
		byte[] maxlob = utils.encodeLong(max);
		long max1 = utils.decodeLong(maxlob);
		assertTrue( max == max1 );
	}

	@Test
	public void testEncodeInt() {
		int lo = ra.nextInt() ;
		byte[] lob = utils.encodeInt(lo);
		int lo2 = utils.decodeInt(lob);
		assertTrue(lo == lo2 );
	}

	@Test
	public void testEncodeByte() {
		//fail("Not yet implemented");
	}

	@Test
	public void testEncodeBoolean() {
		//fail("Not yet implemented");
	}

}
