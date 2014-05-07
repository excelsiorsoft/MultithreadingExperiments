package com.excelsiorsoft.queue.benchmark;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Queue2TakeTest { 

	static Integer testRepeats = 100;
	static Integer repeats = 100;
	static Integer workers = 50;

	static List<Double> testTPS = new Vector<Double>();
	static List<Double> testTPSAdd = new Vector<Double>();
	static List<Double> testTPSAll = new Vector<Double>();
	
	static BlockingQueue<String> abQueue = null;
	static Queue<String> clQueue = null;
	static BlockingQueue<String> lbQueue = null;
	static Queue<String> ll = null;
	
	static String testStr = "helloWorld";

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		for (int j = 0; j < testRepeats; j++) {
			
			abQueue = new ArrayBlockingQueue<String>(5000);
			lbQueue = new LinkedBlockingQueue<String>(5000);
			
			clQueue = new ConcurrentLinkedQueue<String>();
			ll = new LinkedList<String>();
			
			for (int i = 0; i < workers; i++) {
				Thread worker = new Thread(new Queue2TakeTest.Worker(i));
				worker.start();
			}

			while (testTPSAdd.size() < workers)
				Thread.sleep(1000);
			
			testTPSAdd.clear();
			
			for (int i = 0; i < workers; i++) {
				Thread worker1 = new Thread(new Queue2TakeTest.Worker1());
				worker1.start();
			}

			while (testTPS.size() < workers)
				Thread.sleep(1000);

			double testTPSSUM = 0;
			for (int i = 0; i < testTPS.size(); i++)
				testTPSSUM = testTPSSUM + testTPS.get(i);

			testTPSAll.add(Double.valueOf(testTPSSUM
					/ (workers.doubleValue())));
			
			System.out.println(testTPSSUM
					/ (workers.doubleValue()));

			testTPS.clear();
			
			Thread.sleep(1000);
			
		}
		
		double testTPSAllSUM = 0;
		for (int i = 0; i < testTPSAll.size(); i++)
			testTPSAllSUM = testTPSAllSUM + testTPSAll.get(i);

		System.out.println("\nTest TPS Average: " + testTPSAllSUM
				/ testRepeats.doubleValue());
		
		testTPSAll.clear();

	}

	public static class Worker implements Runnable {

		private int id;
		
		public Worker(int id) {
			this.id = id;
		}
		
		@Override
		public void run() {

			try {

				List<Double> testTPSInternal = new ArrayList<Double>();

					for (int i = 0; i < repeats; i++) {

						long start = System.nanoTime();

						boolean res = performTest(i);
						
						long end = System.nanoTime();
						
						if(res)
							testTPSInternal.add(Double
									.valueOf(end - start));

					}

					double testTPSInternalSUM = 0;
					for (int i = 0; i < testTPSInternal.size(); i++)
						testTPSInternalSUM = testTPSInternalSUM
								+ testTPSInternal.get(i);
					
					testTPSAdd.add(Double.valueOf(1000000000 / (testTPSInternalSUM
							/ repeats.doubleValue())));

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		private boolean performTest(int count) throws Exception {
			
			boolean res = true;
			
			abQueue.put(testStr + "-" + id + "-" + count);
			
//			lbQueue.put(testStr + "-" + id + "-" + count);
			
//			clQueue.offer(testStr + "-" + id + "-" + count);
			
//			synchronized(ll) {
//				ll.offer(testStr + "-" + id + "-" + count);
//			}
			
			return res;
			
		}

	}
	
	public static class Worker1 implements Runnable {
		
		@Override
		public void run() {

			try {

				List<Double> testTPSInternal = new ArrayList<Double>();

					for (int i = 0; i < repeats; i++) {

						long start = System.nanoTime();

						String res = performTest();
						
						long end = System.nanoTime();
						
						if(res != null)
							testTPSInternal.add(Double
									.valueOf(end - start));

					}

					double testTPSInternalSUM = 0;
					for (int i = 0; i < testTPSInternal.size(); i++)
						testTPSInternalSUM = testTPSInternalSUM
								+ testTPSInternal.get(i);
					
					testTPS.add(Double.valueOf(1000000000 / (testTPSInternalSUM
							/ repeats.doubleValue())));

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		private String performTest() throws Exception {
			
			String res = null;
			
			res = abQueue.take();
			
//			Iterator<String> it = abQueue.iterator();
//			while(it.hasNext())
//				res = it.next();
			
//			res = lbQueue.take();
			
//			Iterator<String> it = lbQueue.iterator();
//			while(it.hasNext())
//				res = it.next();
			
//			res = clQueue.poll();
			
//			Iterator<String> it = clQueue.iterator();
//			while(it.hasNext())
//				res = it.next();

//			synchronized(ll) {
//				res = ll.poll();
//			}
			
//			Iterator<String> it = ll.iterator();
//			while(it.hasNext())
//				res = it.next();
			
			return res;
		}

	}

}
