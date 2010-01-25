package org.jcsadra.tool.monitor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.mortbay.jetty.Server ;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.resource.FileResource;

/**
 * Provide a monitor agent for Cassandra server.
 * will talk with wbx SNMP agent, also will provide HTTP based wrap.
 * @author sanli
 * 
 */
public class Monitor {
	
	public static Log logger = LogFactory.getLog(Monitor.class);

	public static ApplicationContext context ;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// init spring context
		context = new ClassPathXmlApplicationContext("com/webex/jcsandra/monitor/Context.xml");
		if(context == null){
			logger.fatal("Init context error");
			System.exit(-1);
		}
		logger.info("Init context finished.");
		
		// start jetty for service GWT/JMX bridge, support dash borad and
		// GSB status url.
		Server webServer = (Server) context.getBean("WebServer");		
		try {
			webServer.start() ;
		} catch (Exception e) {
			logger.error("start jetty server error:" + e.getMessage() , e);
			System.exit(-2);
		}
		
		// start SNMP agent

		
	}

}
