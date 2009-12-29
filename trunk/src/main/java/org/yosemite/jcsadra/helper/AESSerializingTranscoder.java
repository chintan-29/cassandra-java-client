package org.yosemite.jcsadra.helper;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import org.yosemite.jcsadra.utils.EncryptUtils;

public class AESSerializingTranscoder extends SerializingTranscoder implements
		Transcoder<Object> {
	
	
	//============= property ================
	SecretKey sceKey ;
	Cipher dencryptCipher ;
	Cipher encryptCipher ;


	public AESSerializingTranscoder(String keyfile) throws IOException,
			InvalidKeyException, NoSuchAlgorithmException,
			NoSuchPaddingException {
		File file = new File(keyfile);

		if (!file.isFile()) {
			throw new IllegalArgumentException("key file not exist");
		}

		sceKey = EncryptUtils.loadKey(file);
		dencryptCipher = Cipher.getInstance(EncryptUtils.DEFAULT_ALGORITHM);
		dencryptCipher.init(Cipher.DECRYPT_MODE, sceKey);
		
		encryptCipher = Cipher.getInstance(EncryptUtils.DEFAULT_ALGORITHM);
		encryptCipher.init(Cipher.ENCRYPT_MODE, sceKey);

	}
	




	//============= public ================
	
	@Override
	public Object decode( Class<? extends Object> clazz , byte[] d) {
		return decode( clazz , d , false) ;
	}
	

	/* (non-Javadoc)
	 */
	public Object decode( Class<? extends Object> clazz ,byte[] data , boolean comperessed ) {
		byte[] d;
		try {
			d = dencrypt( data );
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		} catch (BadPaddingException e) {
			throw new RuntimeException(e);
		}
		return super.decode(clazz, d , comperessed) ;		
	}
	
	
	public byte[] encode(Object o ){
		return encode(o ,  false );
	}

	/* (non-Javadoc)
	 */
	public byte[] encode(Object o , boolean comperess) {
		byte[] data =  super.encode(o , comperess) ;
		try {
			return encrypt(data);
		} catch (IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		} catch (BadPaddingException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private byte[] encrypt(byte[] data) throws IllegalBlockSizeException,
			BadPaddingException {
		return encryptCipher.doFinal(data) ;
	}
	
	private byte[] dencrypt(byte[] data) throws IllegalBlockSizeException,
			BadPaddingException {
		return dencryptCipher.doFinal(data) ;
	}
	
	
	
	public void setSceKey(SecretKey sceKey) {
		this.sceKey = sceKey;
	}



	public SecretKey getSceKey() {
		return sceKey;
	}
	
}
