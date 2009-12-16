package org.yosemite.jcsadra.utils;

import java.io.File;
import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
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
	private static Key toKey(byte[] key) throws Exception {
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
	 * @throws Exception
	 */
	public static byte[] decrypt(byte[] data, String key) throws Exception {
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
	public static byte[] encrypt(byte[] data, String key) throws Exception {
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
		SecureRandom secureRandom = null;

		if (seed != null) {
			secureRandom = new SecureRandom(Base64.decodeBase64(seed));
		} else {
			secureRandom = new SecureRandom();
		}

		KeyGenerator kg = KeyGenerator.getInstance( DEFAULT_ALGORITHM );
		kg.init( DEFAULT_KEYSIZE , secureRandom );

		SecretKey secretKey = kg.generateKey();

		return Base64.encodeBase64String(secretKey.getEncoded());
	}

	
	public static void saveKey(File file , SecretKey key){
		//TODO write key to file
	}
	
	public static SecretKey loadKey(File file){
		//TODO load key from file.
		return null ;
	}
}
