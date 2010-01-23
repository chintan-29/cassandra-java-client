package com.webex.jcasandra.board.server;

import java.util.Set;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.webex.jcasandra.board.client.servcie.MBeanProxyService;


/**
 *  
 * @author sanli
 */
public class MBeanProxyServiceImpl extends RemoteServiceServlet implements
		MBeanProxyService {

	@Override
	public Set<String> getSetProperty(String mbean, String prop) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getStringProperty(String mbean, String prop) {
		// TODO Auto-generated method stub
		return null;
	}

}
