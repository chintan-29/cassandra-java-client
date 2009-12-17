package org.yosemite.jcsadra.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class EncryptUtils {

	/**
	 * Default encrypt/dencrypt algorithm, can any of bellow:
	 * 
	 * <pre>
	 * DES                  key size must be equal to 56 
	 * DESede(TripleDES)    key size must be equal to 112 or 168 
	 * AES                  key size must be equal to 128, 192 or 256,but 192 and 256 bits may not be available 
	 * Blowfish             key size must be multiple of 8, and can only range from 32 to 448 (inclusive) 
	 * RC2                  key size must be between 40 and 1024 bits 
	 * RC4(ARCFOUR)         key size must be between 40 and 1024 bits
	 * </pre>
	 * 
	 */
	public static final String DEFAULT_ALGORITHM = "AES";
	public static final int DEFAULT_KEYSIZE = 128 ;

	/**
	 * create a SecretKey from byte array
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private static SecretKey toKey(byte[] key) {
		/*DESKeySpec dks = new DESKeySpec(key);
		SecretKeyFactory keyFactory = SecretKeyFactory
				.getInstance(DEFAULT_ALGORITHM);*/
		//SecretKey secretKey = keyFactory.generateSecret(dks);

		// using other algorithm should uncomment bellow line
		SecretKey secretKey = new SecretKeySpec(key, "AES" );
		return secretKey;
	}

	/**
	 * decrypt data
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws NoSuchPaddingException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidKeyException 
	 * @throws BadPaddingException 
	 *key @throws IllegalBlockSizeException 
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, String key)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Key k = toKey(Base64.decodeBase64(key));

		Cipher cipher = Cipher.getInstance(DEFAULT_ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, k);

		return cipher.doFinal(data);
	}

	

	/**
	 * encrypt data
	 * 
	 * @param data
	 * @param key
	 * @return
	 * @throws Exception
	 */
	public static byte[] encrypt(byte[] data, String key)
			throws NoSuchAlgorithmException, NoSuchPaddingException,
			InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		Key k = toKey(Base64.decodeBase64(key));
		Cipher cipher = Cipher.getInstance(DEFAULT_ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, k);

		return cipher.doFinal(data);
	}

	/**
	 * generate security key
	 * 
	 * @return
	 * @throws Exception
	 */
	public static String initKey() throws Exception {
		return initKey(null);
	}

	/**
	 * generate security key base on some seed, and result key as base64 present
	 * string
	 * 
	 * @param seed
	 * @return
	 * @throws Exception
	 */
	public static String initKey(String seed) throws Exception {
		SecretKey secretKey = genRandomeKey(Base64.decodeBase64(seed)) ;
		return Base64.encodeBase64String(secretKey.getEncoded());
	}

	
	
	
	/**
	 * Generate a key with random seed
	 * @param seed
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static SecretKey genRandomeKey(byte[] seed) throws NoSuchAlgorithmException{
		SecureRandom secureRandom = null;

		if (seed != null) {
			secureRandom = new SecureRandom(seed);
		} else {
			secureRandom = new SecureRandom();
		}

		KeyGenerator kg = KeyGenerator.getInstance( DEFAULT_ALGORITHM );
		kg.init( DEFAULT_KEYSIZE , secureRandom );

		SecretKey secretKey = kg.generateKey();
		return secretKey ;
	}
	
	
	/**
	 * the start token in key store file, all String between START_TOKEN
	 * and END_TOKEN will be consider as key content.
	 */
	public static String START_TOKEN =  "==== START ====" ;
	/**
	 * the end token in key store file
	 */
	public static String END_TOKEN =  "==== END ====" ;
	/**
	 * save a security key to a raw file, all keys was transcode to BASE64.
	 * 
	 * @param file  the store file, if file not exist will try to create it
	 * @param key the scurity key
	 * @throws IOException 
	 */
	public static void saveKey(File file , SecretKey key) throws IOException{
		FileWriter sof = new FileWriter(file);
		
		try{
			String keys =Base64.encodeBase64String(key.getEncoded());			
			sof.write("===========================================================\n");
			sof.write("=     AES 128 KEY, GEN BY APP, PLEASE DON'T EDIT IT       =\n");
			sof.write("===========================================================\n");
			sof.write(START_TOKEN + "\n");
			sof.write(keys);
			sof.write(END_TOKEN + "\n");
			sof.flush();
		}finally{
			sof.close() ;
		}
	}
	
	
	/**
	 * load a BASE64 based security key
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public static SecretKey loadKey(File file) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String line ;
		boolean iskey = false ;
		StringBuilder key  =  new StringBuilder() ;
		while(( line = reader.readLine() ) != null ){

			
			if(line.contains(END_TOKEN))
				iskey = false ;
			
			if(iskey)
				key.append(line.trim());		
			
			if(line.contains(START_TOKEN))
				iskey = true ;		
		}
		
		byte[] keybuf = Base64.decodeBase64(key.toString()) ; 
		return toKey( keybuf ) ;
	}
}
