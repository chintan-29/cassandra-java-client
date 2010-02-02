import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;

import org.apache.cassandra.service.Cassandra;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.yosemite.jcsadra.impl.CassandraClientImpl;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This package for test the real cassandra performance
 *  
 * @author sanli
 *
 */
public class RealCassandra {
	
	public static final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();

	public static String[] hosts = {"1" , "2" , "3" , "4"} ;
	
	/**
	 * Test read or write with multi thread, recode performance
	 * -r thread_num
	 * -w thread_num
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length <= 1){
			System.out.println("RealCassandra -r thread_num \n RealCassandra -w thread_num ");
		}	
		
		int threadcount = 10 ;
		if(args[0].equals("-r")){
			threadcount = Integer.getInteger(args[1]);
			
		}else if(args[0].equals("-w")){
			threadcount = Integer.getInteger(args[1]);
		}
		
		// create writer for append
		File outkey =  new File("key.out");
		FileWriter fw ;
		try {
			 fw b= new FileWriter(outkey , true);
		} catch (IOException e) {
			e.printStackTrace();
		} 
		
		int count = 0 ;
		while (count < (threadcount * TASK_COUNT - 1)) {
			String key = queue.remove();
			
		}
	}
	
	
	
	public static Thread[] createTask(Runnable task , int number){
		Thread[] threads = new Thread[number];
		for(int i = 0 ; i<= number -1 ; i++){
			Thread t = new Thread(task , task.getClass().getName() + "_" + i);
			threads[i] = t;
		}
		
		return threads ;
	}
	
	public static void startThread(Thread[] threads){
		for(Thread t : threads){
			t.start() ;
		}
	}
	
	
	public static void joinThread(Thread[] threads){
		for(Thread t : threads){
			try {
				t.join() ;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static int TASK_COUNT = 10000 ;
	
	public class ReadTask implements Runnable{
		int count ;
		long time ;
		long datasize ;

		@Override
		public void run() {
			
		}
		
	}
	
	public class WriteTask implements Runnable{
		
		int count ; 
		long time ;
		long datasize ;

		// write data into cassandra, and recode data into 
		// key list file. read task will read out that file
		// and randome choice some key to do query.		
		@Override
		public void run() {
			
		}
		
	}
	
	
	
	
	public static Cassandra.Client createClient(String host, int port)
			throws TTransportException {
	    TTransport tr = new TSocket( "localhost" , 9160 );
	    TProtocol proto = new TBinaryProtocol(tr);
	    Cassandra.Client client = new Cassandra.Client(proto);
	    tr.open();
	    return client ;
	}
	
	
	public static String genKey(){
		return UUID.randomUUID().toString() ;
	}
	
	
	
	static Random ra = new Random(System.currentTimeMillis());	
	public static byte[] genValue(int size){
		byte[] result = new byte[size] ;
		ra.nextBytes(result);
		return result ;
	}
	
	
	public static int hostIndex = 0 ;
	public static String getHost(){
		int index = ( hostIndex ++ ) % hosts.length ;
		return hosts[index];
	}
	

}
