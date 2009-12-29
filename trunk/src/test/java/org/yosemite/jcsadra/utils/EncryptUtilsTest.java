package org.yosemite.jcsadra.utils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.ByteArrayOutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.SecretKey;

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
		
		String msg = "this is test message kjadfls asdfalsk akdhlkasd asdkjhakl kldhl " ;
		
		byte[] result = EncryptUtils.encrypt(msg.getBytes("utf-8"), key);
		byte[] result2 = EncryptUtils.decrypt(result, key);
		
		assertTrue(msg.equals(new String(result2,"utf-8")));
		
		
		
		msg = "a" ;
		result = EncryptUtils.encrypt(msg.getBytes("utf-8"), key);
		result2 = EncryptUtils.decrypt(result, key);
		assertTrue(msg.equals(new String(result2,"utf-8")));
		
		
		msg = "" ;
		result = EncryptUtils.encrypt(msg.getBytes("utf-8"), key);
		result2 = EncryptUtils.decrypt(result, key);
		assertTrue(msg.equals(new String(result2,"utf-8")));
		
		
		try{
			msg = null ;
			EncryptUtils.encrypt(msg.getBytes("utf-8"), key);
			fail();
		}catch(Exception e){
			
		}
		
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
	
	
	@Test
	public void testKeyStoreAndRetrive() throws NoSuchAlgorithmException,
			IOException {
		SecretKey key = EncryptUtils.genRandomeKey(String.valueOf(System.currentTimeMillis())
				.getBytes("utf-8"));
		assertTrue(key!=null) ;
		
		File keyfile = new File("testKeyStoreAndRetrive.key") ;
		if(keyfile.exists()){
			keyfile.delete() ;
		}
		EncryptUtils.saveKey( keyfile , key);
		
		assertTrue(keyfile.exists());
		assertTrue(keyfile.length() > 100 );
		
		SecretKey loadkey = EncryptUtils.loadKey(keyfile) ;
		assertTrue(loadkey!=null);
		assertTrue(key.equals(loadkey));
	}

}
