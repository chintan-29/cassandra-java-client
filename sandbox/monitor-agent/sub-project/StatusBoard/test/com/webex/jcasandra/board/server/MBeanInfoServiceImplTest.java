package com.webex.jcasandra.board.server;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;

public class MBeanInfoServiceImplTest {
	
	
	private MBeanInfoServiceImpl mbeanInfo;

	@Before
	public void create(){
		mbeanInfo = new MBeanInfoServiceImpl();
	}
	
	
	@Test
	public void testGetMBeanNames(){
		 List<String> beans = mbeanInfo.getMBeanNames();
		 assertTrue(beans.size() > 0 );
	}
	
	
	@Test
	public void testGetBeanProperties(){
		List<String> beans = mbeanInfo.getMBeanNames();
		for(String beanname : beans){
			Map<String, String> props = mbeanInfo.getBeanProperties(beanname);
			assertTrue(props.size()>0);
			System.out.println(props);
		}
	}
	
	
	public void testGetAllMBeans(){
		Map<String, Map<String, String>> beans = mbeanInfo.getAllMBeans();
		assertTrue(beans.size() > 0);
	}

}
