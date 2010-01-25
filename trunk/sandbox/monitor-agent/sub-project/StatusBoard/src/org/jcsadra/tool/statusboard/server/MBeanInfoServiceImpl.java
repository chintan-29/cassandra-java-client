package org.jcsadra.tool.statusboard.server;

import java.beans.BeanDescriptor;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.management.MBeanServerConnection;

import org.apache.cassandra.db.CompactionManagerMBean;
import org.apache.cassandra.service.StorageServiceMBean;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.webex.jcasandra.board.client.servcie.MBeanInfoService;

public class MBeanInfoServiceImpl extends RemoteServiceServlet implements
		MBeanInfoService {

	
    private MBeanServerConnection mbeanServerConn;
    private StorageServiceMBean ssProxy;
    private MemoryMXBean memProxy;
    private RuntimeMXBean runtimeProxy;
    private CompactionManagerMBean mcmProxy;
    
    public static final LinkedHashMap<String, Class<?>> mbeanInfoMap = new LinkedHashMap<String, Class<?>>();
    public static final LinkedHashMap<Class<?> , Method[]> mbeanMethodMap = new LinkedHashMap< Class<?> , Method[]>();
    
    static{
    	mbeanInfoMap.put("StorageServiceMBean", StorageServiceMBean.class);
    	mbeanInfoMap.put("MemoryMXBean", MemoryMXBean.class);
    	mbeanInfoMap.put("CompactionManagerMBean", CompactionManagerMBean.class);
    }
    


    @Override
	public List<String> getMBeanNames() {
    	return new ArrayList<String>(mbeanInfoMap.keySet());
	}
	
    
	@Override
	public Map<String, String> getBeanProperties(String mBeanName) {
		HashMap<String , String> result = new HashMap<String, String>();
		
		Class<?> clazz = null;
		if(( clazz = mbeanInfoMap.get(mBeanName)) ==null ){
			throw new IllegalArgumentException("given mbean not exist:" + mBeanName);
		}
		
		Method[] methods = null ;
		if( ( methods = mbeanMethodMap.get(clazz)) == null ){
			methods = clazz.getMethods() ;
			mbeanMethodMap.put(clazz, methods);
		}
		
		
		for(Method method : methods ){
			String name = method.getName() ; 
			if(name.startsWith("get")){
				Class<?> type = method.getReturnType();
				result.put( name , type.toString() );
			}
		}
		
		return result;
	}

	
	
	@Override
	public Map<String, Map<String, String>> getAllMBeans() {
		Map<String, Map<String, String>> result = new HashMap<String, Map<String, String>>();
		
		List<String> beans = getMBeanNames();
		for(String beanName : beans){
			Map<String,String> props = getBeanProperties(beanName);
			result.put(beanName, props);
		}
		
		return result;
	}

}
