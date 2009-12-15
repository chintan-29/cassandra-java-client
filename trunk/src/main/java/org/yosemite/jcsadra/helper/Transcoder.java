package org.yosemite.jcsadra.helper;

/**
 * Transcoder is an interface for classes that convert between byte arrays and
 * objects for storage in the cache.
 * This idea was come from the excellent memcache lib, spymemecached, only change 
 * was I leave the caller to specify whether compress data.
 */
public interface Transcoder<T> {



	/**
	 * Encode the given object for storage.
	 *
	 * @param o the object
	 * @return the CachedData representing what should be sent
	 */
	public byte[] encode(T o);
	
	
	/**
	 * Encode the given object for storage, also do comperess on 
	 * object.
	 *
	 * @param o the object
	 * @return the CachedData representing what should be sent
	 */
	public byte[] encode(T o , boolean comperess) ; 

	/**
	 * Decode the cached object into the object it represents.
	 *
	 * @param d the data
	 * @return the return value
	 */
	T decode(byte[] d);
	
	
	/**
	 * Decode the cached object into the object it represents, using
	 * if the data was compressed, should set the compressed = true
	 * @param d
	 * @param compressed
	 * @return
	 */
	T decode(byte[] d , boolean compressed);
}
