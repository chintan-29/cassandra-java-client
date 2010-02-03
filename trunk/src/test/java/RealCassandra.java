import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.UUID;

import org.apache.cassandra.thrift.Cassandra;
import org.apache.cassandra.thrift.ColumnPath;
import org.apache.cassandra.thrift.ConsistencyLevel;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * This package for test the real cassandra performance
 * 
 * @author sanli
 * 
 */
public class RealCassandra {

	public static final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<String>();
	public static final LinkedBlockingQueue<String> taskQueue = new LinkedBlockingQueue<String>();
	
	/*
	 * public static String[] hosts = {"10.252.238.201" , "10.252.238.202",
	 * "10.252.238.203", "10.252.238.204", "10.252.238.205"} ;
	 */

	public static String[] hosts = { "localhost" };

	/**
	 * Test read or write with multi thread, recode performance -r thread_num -w
	 * thread_num
	 * 
	 * @param args
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 */
	public static void main(String[] args) throws IOException,
			InstantiationException, IllegalAccessException {
		if (args.length <= 1) {
			System.out
					.println("RealCassandra -r thread_num \n RealCassandra -w thread_num ");
		}

		int threadcount = 10;
		if (args[0].equals("-r")) {
			threadcount = Integer.getInteger(args[1]);
			AvgThread[] threads = createTask(ReadThread.class, threadcount);
			startThread(threads);
			provideIDCount(threadcount * TASK_COUNT);
			joinThread(threads);
			printAvg(threads);
		} else if (args[0].equals("-w")) {
			threadcount = Integer.parseInt(args[1]);
			AvgThread[] threads = createTask(WriteThread.class, threadcount);
			startThread(threads);
			waitForIDCount(threadcount * TASK_COUNT);
			joinThread(threads);
			printAvg(threads);
		}

	}

	private static void provideIDCount(int idCount) {
		
		// create writer for append
		String keyfileName = System.getProperty("keyfile");
		if (keyfileName == null) {
			keyfileName = "key.out";
		}
		File outkey = new File(keyfileName);
		FileReader raf = null;
		try {
			raf = new FileReader(outkey);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for(int i= 0 ; i<= idCount - 1 ; i++){
			// read line and push to task list
		}
	}

	public static AvgThread[] createTask(Class<? extends AvgThread> taskClazz,
			int number) throws InstantiationException, IllegalAccessException {
		AvgThread[] threads = new AvgThread[number];
		for (int i = 0; i <= number - 1; i++) {
			AvgThread task = taskClazz.newInstance();
			task.setName(taskClazz.getName() + "_" + i);
			threads[i] = task;
		}

		return threads;
	}

	public static void startThread(Thread[] threads) {
		for (Thread t : threads) {
			t.start();
		}
	}

	public static void joinThread(Thread[] threads) {
		for (Thread t : threads) {
			try {
				t.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void printAvg(AvgThread[] threads) {
		if (threads.length == 0) {
			System.out.println("0 threads avg time:0 data size:0");
			return;
		}
		int count = 0;
		long time = 0;
		long data = 0;
		for (AvgThread t : threads) {
			count += t.count;
			time += t.time;
		}

		System.out.println(threads.length + " threads avg time:"
				+ (time / count) + " data size:" + data);
	}

	public static int TASK_COUNT = 10000;
	public static int DATA_SIZE = 10 * 1024; // 10K data

	public static abstract class AvgThread extends Thread {

		int count = 0;
		long time = 0;
		long datasize = 0;

		public long avg() {
			return time / count;
		}

		public long getDataSize() {
			return datasize;
		}

		public void printAvg() {
			System.out.println(this.getName() + " avg time:" + avg()
					+ " data size:" + getDataSize());
		}
	}

	public static class ReadThread extends AvgThread {

		@Override
		public void run() {
			Cassandra.Client client = null;
			try {
				client = createClient(getHost(), 9160);
				
				// get a random data block to read
				while( count <= TASK_COUNT - 1 ) {
					long timestamp = 0 ;
					String key = "null" ;
					try {
						key = queue.poll(1000 , TimeUnit.MILLISECONDS);
						
						if(key!=null){
							timestamp = System.currentTimeMillis();	
							// do query with key
							// TODO
							
							long x = System.currentTimeMillis() - timestamp ;
							time += x ;
							if(x > 1000){
								System.out.println("id:"+ key + " using many time:" + x);		
							}
							
							count++;
							datasize += DATA_SIZE;
						}						
						
					} catch (InterruptedException e) { 
						//
					} catch (Exception e){
						long x = System.currentTimeMillis() - timestamp ;
						System.out.println("id:"+ key + " have exception. using time:" + x);
						e.printStackTrace();
					}
				}
				printAvg();
			} catch (TTransportException e1) {
				e1.printStackTrace();
			} finally {
				if (client != null) {
					client.getInputProtocol().getTransport().close();
					client.getInputProtocol().getTransport().close();
				}
			}
		}

	}

	public static class WriteThread extends AvgThread {

		// write data into cassandra, and recode data into
		// key list file. read task will read out that file
		// and randome choice some key to do query.
		@Override
		public void run() {
			Cassandra.Client client = null;
			try {
				client = createClient(getHost(), 9160);

				for (int i = 0; i <= TASK_COUNT - 1; i++) {
					StringBuilder sb = new StringBuilder();
					String id = genKey();
					sb.append(id);
					long timestamp = System.currentTimeMillis();
					try {

						ColumnPath path = new ColumnPath("Standard1");
						path.setSuper_column(null);
						path.setColumn("value".getBytes("UTF-8"));
						

						timestamp = System.currentTimeMillis();
						client.insert("Keyspace1", id, path,
								genValue(DATA_SIZE), timestamp,
								ConsistencyLevel.QUORUM);
						long x = System.currentTimeMillis() - timestamp ;
						time += x ;
						count++;
						datasize += DATA_SIZE;
						sb.append(",").append(x);
					} catch (Exception e) {
						System.out.println("id:"+ id + " have exception.");
						e.printStackTrace();
						long x = System.currentTimeMillis() - timestamp ;
						sb.append(", E:").append(x);
					}
					queue.add(sb.toString());
				}

				printAvg();
			} catch (TTransportException e1) {
				e1.printStackTrace();
			} finally {
				if (client != null) {
					client.getInputProtocol().getTransport().close();
					client.getInputProtocol().getTransport().close();
				}
			}
		}

	}

	public static void waitForIDCount(int idCount) throws IOException {

		// create writer for append
		String keyfileName = System.getProperty("keyfile");
		if (keyfileName == null) {
			keyfileName = "key.out";
		}
		File outkey = new File(keyfileName);
		FileWriter fw = null;
		try {
			fw = new FileWriter(outkey, true);
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			int count = 0;
			while (count < (idCount - 1)) {
				String key = queue.poll(1000 , TimeUnit.MILLISECONDS);
				if(key != null) {
					fw.write(key);
					fw.write("\n");
				}
				count ++ ;
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			fw.close();
		}

	}

	public static Cassandra.Client createClient(String host, int port)
			throws TTransportException {
		TTransport tr = new TSocket("localhost", 9160);
		TProtocol proto = new TBinaryProtocol(tr);
		Cassandra.Client client = new Cassandra.Client(proto);
		tr.open();
		return client;
	}

	public static String genKey() {
		return UUID.randomUUID().toString();
	}

	static Random ra = new Random(System.currentTimeMillis());

	public static byte[] genValue(int size) {
		byte[] result = new byte[size];
		ra.nextBytes(result);
		return result;
	}

	public static int hostIndex = 0;

	public static String getHost() {
		int index = (hostIndex++) % hosts.length;
		return hosts[index];
	}

}
