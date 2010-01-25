package org.jcsadra.tool.statusboard.client.service;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface MBeanInfoServiceAsync {

	void getAllMBeans(AsyncCallback<Map<String, Map<String, String>>> callback);

	void getMBeanNames(AsyncCallback<List<String>> callback);

	void getBeanProperties(String mBeanName,
			AsyncCallback<Map<String, String>> callback);

}
