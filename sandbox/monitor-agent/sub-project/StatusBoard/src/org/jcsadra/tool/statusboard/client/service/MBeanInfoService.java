/**
 * 
 */
package org.jcsadra.tool.statusboard.client.service;

import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * @author sanli
 *
 */
@RemoteServiceRelativePath("beaninfo")
public interface MBeanInfoService extends RemoteService {
	
	
	/**
	 * get all Mbean info names
	 * @return
	 */
	public List<String> getMBeanNames();
	
	
	/**
	 * get all properties list of specify mbean
	 * @param mBeanName
	 * @return
	 */
	public Map<String , String> getBeanProperties(String mBeanName);
	
	
	
	/**
	 * retrive all mbeans in bundle.
	 * @return
	 */
	public Map< String, Map<String , String> >  getAllMBeans();
	
	

}
