package com.webex.jcasandra.board.server;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;

import javax.management.JMX;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

import org.apache.cassandra.db.CompactionManager;
import org.apache.cassandra.db.CompactionManagerMBean;
import org.apache.cassandra.service.StorageServiceMBean;



/**
 * this is a JMX bean property accesser, can get access remote 
 * mbean object to got information
 * @author sanli
 *
 */
public class MBeanAgent {
    private static final String fmtUrl = "service:jmx:rmi:///jndi/rmi://%s:%d/jmxrmi";
    private static final String ssObjName = "org.apache.cassandra.service:type=StorageService";
    private static final int defaultPort = 8080;
    
    
    private String host;
    private int port;
    
    private MBeanServerConnection mbeanServerConn;
    private StorageServiceMBean ssProxy;
    private MemoryMXBean memProxy;
    private RuntimeMXBean runtimeProxy;
    private CompactionManagerMBean mcmProxy;
    
    
    public MBeanAgent(){
    	this( "localhost" , 8080) ;
    }
    
    
    public MBeanAgent(String host, int port){
    	this.host = host ;
    	this.port = port ;
    }
	
    /**
     * Create a connection to the JMX agent and setup the M[X]Bean proxies.
     * 
     * @throws IOException on connection failures
     */
    public void connect() throws IOException
    {
        JMXServiceURL jmxUrl = new JMXServiceURL(String.format(fmtUrl, host, port));
        JMXConnector jmxc = JMXConnectorFactory.connect(jmxUrl, null);
        mbeanServerConn = jmxc.getMBeanServerConnection();
        
        
        try
        {
            ObjectName name = new ObjectName(ssObjName);
            ssProxy = JMX.newMBeanProxy(mbeanServerConn, name, StorageServiceMBean.class);
            name = new ObjectName(CompactionManager.MBEAN_OBJECT_NAME);
            mcmProxy = JMX.newMBeanProxy(mbeanServerConn, name, CompactionManagerMBean.class);
        } catch (MalformedObjectNameException e)
        {
            throw new RuntimeException(
                    "Invalid ObjectName? Please report this as a bug.", e);
        }
        
        memProxy = ManagementFactory.newPlatformMXBeanProxy(mbeanServerConn, 
                ManagementFactory.MEMORY_MXBEAN_NAME, MemoryMXBean.class);
        runtimeProxy = ManagementFactory.newPlatformMXBeanProxy(
                mbeanServerConn, ManagementFactory.RUNTIME_MXBEAN_NAME, RuntimeMXBean.class);
    }
    
    

    

}
