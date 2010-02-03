import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.UUID;

import org.apache.cassandra.service.Cassandra;
import org.apache.cassandra.service.ColumnPath;
import org.apache.cassandra.service.ConsistencyLevel;
import org.apache.cassandra.service.InvalidRequestException;
import org.apache.cassandra.service.TimedOutException;
import org.apache.cassandra.service.UnavailableException;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * This package for test the real cassandra performance
 *  
 * @author sanli
 *
 */
public class RealCassandra {
	
	public static final ConcurrentLinkedQueue<String> queue = new ConcurrentLinkedQueue<String>();

	public static String[] hosts = {"10.252.238.201" , "10.252.238.202", "10.252.238.203", "10.252.238.204",
        "10.252.238.205"} ;
	
	/**
	 * Test read or write with multi thread, recode performance
	 * -r thread_num
	 * -w thread_num
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		if(args.length <= 1){
			System.out.println("RealCassandra -r thread_num \n RealCassandra -w thread_num ");
		}	
		
		int threadcount = 10 ;
		if(args[0].equals("-r")){
			threadcount = Integer.getInteger(args[1]);
			
		}else if(args[0].equals("-w")){
			threadcount = Integer.getInteger(args[1]);
			
			
			
			
			// create writer for append
			String keyfileName = System.getProperty("keyfile");
			if(keyfileName==null){
				keyfileName = "key.out" ;
			}
			File outkey =  new File(keyfileName);
			FileWriter fw = null ;
			try {
				fw = new FileWriter(outkey , true);
			} catch (IOException e) {
				e.printStackTrace();
			} 
			
			int count = 0 ;
			while (count < (threadcount * TASK_COUNT - 1)) {
				String key = queue.remove();
				fw.write(key);
				fw.write("\n");
			}
			
			fw.close() ;
		}

	}
	
	
	public static void writeKey(){
		
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
	public static int DATA_SIZE = 10 * 1024 ; // 10K data
	
	public class ReadTask implements Runnable{
		int count ;
		long time ;
		long datasize ;

		@Override
		public void run() {
			
		}
		
	}
	
	public class WriteTask implements Runnable{
		
		int count = 0  ; 
		long time = 0 ;
		long datasize = 0 ;

		// write data into cassandra, and recode data into 
		// key list file. read task will read out that file
		// and randome choice some key to do query.		
		@Override
		public void run() {
			Cassandra.Client client = null ;
			try {
				client = createClient(getHost() , 9160);
			
				for (int i = 0; i <= TASK_COUNT - 1; i++) {
					try {
	
						ColumnPath path = new ColumnPath("Standard1");
						path.setSuper_column(null);
						path.setColumn("value".getBytes("UTF-8"));
	
						String id = genKey();
						long timestamp = System.currentTimeMillis();
						client.insert("Keyspace1", id, path, genValue(DATA_SIZE),
								timestamp, ConsistencyLevel.QUORUM);
						time += (System.currentTimeMillis() - timestamp);
						count ++ ;						
						datasize += DATA_SIZE ;
						queue.add(id);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} catch (TTransportException e1) {
				e1.printStackTrace();
			} finally {
				if(client != null){
					client.getInputProtocol().getTransport().close() ;
					client.getInputProtocol().getTransport().close() ;
				}
			}
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
