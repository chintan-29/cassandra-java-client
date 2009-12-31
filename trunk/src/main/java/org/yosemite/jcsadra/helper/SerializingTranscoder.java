package org.yosemite.jcsadra.helper;

import java.util.Date;

import org.yosemite.jcsadra.utils.TranscoderUtils;



/**
 * Transcoder that serializes and compresses objects.
 */
public class SerializingTranscoder extends BaseSerializingTranscoder
	implements Transcoder<Object> {

	private final TranscoderUtils tu=new TranscoderUtils(true);
	

	public Object decode( Class<? extends Object> clazz , byte[] d) {
		return decode( clazz , d , false) ;
	}
	

	/* (non-Javadoc)
	 */
	public Object decode( Class<? extends Object> clazz ,byte[] data , boolean comperessed ) {
		Object rv=null;
		if(comperessed) {
			data=decompress(data);
		}		
			
		if( clazz.equals(String.class) ) {
			rv= decodeString(data);
		}else if(clazz.equals(Boolean.class)){
			rv=Boolean.valueOf(tu.decodeBoolean(data));
		}else if(clazz.equals(Integer.class)){
			rv=new Integer(tu.decodeInt(data));
		}else if(clazz.equals(Long.class)){
			rv=new Long(tu.decodeLong(data));
		}else if(clazz.equals(Date.class)){
			rv=new Date(tu.decodeLong(data));
		}else if(clazz.equals(Byte.class)){
			rv=new Byte(tu.decodeByte(data));
		}else if(clazz.equals(Float.class)){
			rv=new Float(Float.intBitsToFloat(tu.decodeInt(data)));
		}else if(clazz.equals(Double.class)){
			rv=new Double(Double.longBitsToDouble(tu.decodeLong(data)));
		}else if(clazz.equals(byte[].class)){
			rv=data;
		}else{
			rv = deserialize(data);
		}
			
		assert rv != null;
		return rv ;
	}
	
	
	public byte[] encode(Object o ){
		return encode(o ,  false );
	}

	/* (non-Javadoc)
	 */
	public byte[] encode(Object o , boolean comperess) {
		byte[] b=null;
		if(o instanceof String) {
			b=encodeString((String)o);
		} else if(o instanceof Long) {
			b=tu.encodeLong((Long)o);
		} else if(o instanceof Integer) {
			b=tu.encodeInt((Integer)o);
		} else if(o instanceof Boolean) {
			b=tu.encodeBoolean((Boolean)o);
		} else if(o instanceof Date) {
			b=tu.encodeLong(((Date)o).getTime());
		} else if(o instanceof Byte) {
			b=tu.encodeByte((Byte)o);
		} else if(o instanceof Float) {
			b=tu.encodeInt(Float.floatToRawIntBits((Float)o));
		} else if(o instanceof Double) {
			b=tu.encodeLong(Double.doubleToRawLongBits((Double)o));
		} else if(o instanceof byte[]) {
			b=(byte[])o;
		} else {
			b=serialize(o);
		}
		
		assert b != null;
		if(comperess) {
			b=compress(b);
		}
		return b;
	}


	@Override
	public Object decode(byte[] d) {
		return decode(d, false);
	}


	@Override
	public Object decode(byte[] d, boolean compressed) {
		if(compressed) {
			d=decompress(d);
		}	
		return deserialize(d);		
	}
}
