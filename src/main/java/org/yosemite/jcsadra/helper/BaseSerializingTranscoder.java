package org.yosemite.jcsadra.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;



/**
 * Base class for any transcoders that may want to work with serialized or
 * compressed data.
 */
public abstract class BaseSerializingTranscoder {

	private static final String DEFAULT_CHARSET = "UTF-8";

	protected String charset=DEFAULT_CHARSET;


	/**
	 * Set the character set for string value transcoding (defaults to UTF-8).
	 */
	public void setCharset(String to) {
		// Validate the character set.
		try {
			new String(new byte[97], to);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		charset=to;
	}

	/**
	 * Get the bytes representing the given serialized object.
	 */
	protected byte[] serialize(Object o) {
		if(o == null) {
			throw new NullPointerException("Can't serialize null");
		}
		byte[] rv=null;
		try {
			ByteArrayOutputStream bos=new ByteArrayOutputStream();
			ObjectOutputStream os=new ObjectOutputStream(bos);
			os.writeObject(o);
			os.close();
			bos.close();
			rv=bos.toByteArray();
		} catch(IOException e) {
			throw new IllegalArgumentException("Non-serializable object", e);
		}
		return rv;
	}

	/**
	 * Get the object represented by the given serialized bytes.
	 */
	protected Object deserialize(byte[] in) {
		Object rv=null;
		try {
			if(in != null) {
				ByteArrayInputStream bis=new ByteArrayInputStream(in);
				ObjectInputStream is=new ObjectInputStream(bis);
				rv=is.readObject();
				is.close();
				bis.close();
			}
		} catch(IOException e) {
			throw new RuntimeException("have IO Exception" ,e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("have Exception" ,e);
		}
		return rv;
	}

	/**
	 * Compress the given array of bytes.
	 */
	protected byte[] compress(byte[] in) {
		if(in == null) {
			throw new NullPointerException("Can't compress null");
		}
		ByteArrayOutputStream bos=new ByteArrayOutputStream();
		GZIPOutputStream gz=null;
		try {
			gz = new GZIPOutputStream(bos);
			gz.write(in);
		} catch (IOException e) {
			throw new RuntimeException("IO exception compressing data", e);
		} finally {
			close(gz);
			close(bos);
		}
		byte[] rv=bos.toByteArray();

		return rv;	//
	}

	/**
	 * Decompress the given array of bytes.
	 *
	 * @return null if the bytes cannot be decompressed
	 */
	protected byte[] decompress(byte[] in) {
		ByteArrayOutputStream bos=null;
		if(in != null) {
			ByteArrayInputStream bis=new ByteArrayInputStream(in);
			bos=new ByteArrayOutputStream();
			GZIPInputStream gis;
			try {
				gis = new GZIPInputStream(bis);

				byte[] buf=new byte[8192];
				int r=-1;
				while((r=gis.read(buf)) > 0) {
					bos.write(buf, 0, r);
				}
			} catch (IOException e) {
				bos = null;
			}
		}
		return bos == null ? null : bos.toByteArray();
	}

	/**
	 * Decode the string with the current character set.
	 */
	protected String decodeString(byte[] data) {
		String rv=null;
		try {
			if(data != null) {
				rv=new String(data, charset);
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return rv;
	}

	/**
	 * Encode a string into the current character set.
	 */
	protected byte[] encodeString(String in) {
		byte[] rv=null;
		try {
			rv=in.getBytes(charset);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		return rv;
	}

	
    /**
     * Close a closeable data source
     */
    private void close(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                throw new RuntimeException("close data source have exception.",e);
            }
        }
    }
}
