package org.jcsadra.tool.statusboard.client.service;

import java.util.Set;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MBeanProxyServiceAsync {

	void getSetProperty(String mbean, String prop,
			AsyncCallback<Set<String>> callback);

	void getStringProperty(String mbean, String prop,
			AsyncCallback<String> callback);

}
