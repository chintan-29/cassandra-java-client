package org.jcsadra.tool.statusboard.client.service;

import java.util.Set;

import com.google.gwt.user.client.rpc.RemoteService;

/**
 * this proxy service will delegate all property request to 
 * internal JMX call.
 * 
 * @author sanli
 * 
 */
public interface MBeanProxyService extends RemoteService {
	
	public String getStringProperty(String mbean , String prop) ;
	
	public Set<String> getSetProperty(String mbean , String prop) ;

}
