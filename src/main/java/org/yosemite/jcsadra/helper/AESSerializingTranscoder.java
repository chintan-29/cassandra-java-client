package org.yosemite.jcsadra.helper;

import java.util.Date;

public class AESSerializingTranscoder extends SerializingTranscoder implements
		Transcoder<Object> {

	
	@Override
	public Object decode( Class<? extends Object> clazz , byte[] d) {
		return decode( clazz , d , false) ;
	}
	

	/* (non-Javadoc)
	 */
	public Object decode( Class<? extends Object> clazz ,byte[] data , boolean comperessed ) {
		byte[] d = dencrypt( data ) ;
		return super.decode(clazz, d , comperessed) ;		
	}
	
	
	public byte[] encode(Object o ){
		return encode(o ,  false );
	}

	/* (non-Javadoc)
	 */
	public byte[] encode(Object o , boolean comperess) {
		byte[] data =  super.encode(o , comperess) ;
		return encrypt(data);
	}
	
	
	private byte[] encrypt(byte[] data){
		return data ;
		//TODO add AES encrypt support
	}
	
	private byte[] dencrypt(byte[] data){
		return data ;
		//TODO add AES dencrypt support
	}
	
}
