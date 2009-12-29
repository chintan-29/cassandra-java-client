package org.yosemite.jcsadra.helper;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.junit.Before;
import org.junit.Test;
import org.yosemite.jcsadra.utils.EncryptUtils;


public class AESSerializingTranscoderTest {
	
	AESSerializingTranscoder transcoder ;
	String keyFile = "AESSerializingTranscoderTest.key" ;
	
	
	@Before
	public void createTranscoder() throws NoSuchAlgorithmException,
			IOException, InvalidKeyException, NoSuchPaddingException {
		SecretKey key = EncryptUtils.genRandomeKey(String.valueOf(
				System.currentTimeMillis()).getBytes());
		
		EncryptUtils.saveKey(new File(keyFile) , key );
		transcoder = new AESSerializingTranscoder(keyFile);
	}
	
	public void afterTranscoder(){
		File file = new File(keyFile);
		if(file.exists())
			file.delete() ;
	}

	
	
	@Test
	public void testDecodeClassOfQextendsObjectByteArray() {
		//String test = String.valueOf(System.currentTimeMillis());
		
		String test = "xxxxxxxxxx asdfasdf asdfa sdf asdfas dfasd fasd fasd " ;
		byte[] v = transcoder.encode(test);
		assertTrue( v.length > 0 ) ;
		String sv = (String) transcoder.decode(String.class, v);
		assertTrue(test.equals(sv));
		
		v = transcoder.encode(test,true);
		assertTrue( v.length > 0 ) ;
		sv = (String) transcoder.decode(String.class, v , true);
		assertTrue(test.equals(sv));
	}

}
