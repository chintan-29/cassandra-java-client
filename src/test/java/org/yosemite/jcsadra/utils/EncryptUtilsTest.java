package org.yosemite.jcsadra.utils;

import static org.junit.Assert.*;

import java.io.UnsupportedEncodingException;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;


/**
 * test EncryptUtils class
 * 
 * @author sanli
 *
 */
public class EncryptUtilsTest {
	
	private String key ; 
	
	/**
	 * generate a key before test
	 * @throws Exception 
	 */
	@Before
	public void genKey() throws Exception{
		key = EncryptUtils.initKey(String.valueOf( System.currentTimeMillis()) ) ;
	}
	
	
	@Test
	public void testBasicEncryptAndDencrypt() throws UnsupportedEncodingException, Exception{
		
		String msg = "this is test message" ;
		
		byte[] result = EncryptUtils.encrypt(msg.getBytes("utf-8"), key);
		byte[] result2 = EncryptUtils.decrypt(result, key);
		
		assertTrue(msg.equals(new String(result2,"utf-8")));
	}
	
	/**
	 * this test will using about 10K data to test encrypte and decrypt, for make sure it can
	 * work with huge data block.
	 * @throws UnsupportedEncodingException
	 * @throws Exception
	 */
	@Test
	public void testBigMessageEncryptAndDencrypt() throws UnsupportedEncodingException, Exception{
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for(int i =0 ; i<= 1024 ; i++ ){
			bos.write("0123456789".getBytes("utf-8"));
		}
		byte[] data = bos.toByteArray() ;
		byte[] result = EncryptUtils.encrypt( data , key);
		byte[] result2 = EncryptUtils.decrypt(result, key);
		
		assertTrue(Arrays.equals(data, result2));
	}

}
