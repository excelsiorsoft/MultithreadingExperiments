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

public class QueuePutTest {

	static Integer testRepeats = 100;
	static Integer repeats = 100;
	static Integer workers = 50;

	static List<Double> testTPSAdd = new Vector<Double>();
	static List<Double> testTPSAddAll = new Vector<Double>();
	
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
			
			System.out.println("==== round "+j+" ======");
			
			abQueue = new ArrayBlockingQueue<String>(5000);
			lbQueue = new LinkedBlockingQueue<String>(5000);
			
			clQueue = new ConcurrentLinkedQueue<String>();
			ll = new LinkedList<String>();
			
			for (int i = 0; i < workers; i++) {
				Thread worker = new Thread(new QueuePutTest.Worker(i));
				worker.start();
			}

			while (testTPSAdd.size() < workers){
				System.out.println("Size="+testTPSAdd.size()+". It's < "+workers+" workers.  Waiting...");
				Thread.sleep(1);
			}

			System.out.println("Every worker touched common collection once.  Calculating...");
			
			double dblWorkers = workers.doubleValue();
			double testTPSAddSUM = 0;
			for (int i = 0; i < testTPSAdd.size(); i++)
				testTPSAddSUM = testTPSAddSUM + testTPSAdd.get(i);
			
			System.out.println("Accumulated sum for "+testTPSAdd.size()+" repeats for trial "+j+"="+testTPSAddSUM);
			
			double avgSum = testTPSAddSUM / dblWorkers;
			System.out.println("Average accumulated sum="+avgSum);
			
			testTPSAddAll.add(Double.valueOf(avgSum));
			
			//System.out.println(testTPSAddSUM / dblWorkers);

			testTPSAdd.clear();
			
			Thread.sleep(1000);
			System.out.println("Done test in round "+j+"\n===================\n");
			
		}

		
		System.out.println("Ready to do final calculation.");
		System.out.println(testTPSAddAll);
		
		double testTPSAddAllSUM = 0;
		for (int i = 0; i < testTPSAddAll.size(); i++)
			testTPSAddAllSUM = testTPSAddAllSUM + testTPSAddAll.get(i);

		System.out.println("\nTest TPS Add Average: " + testTPSAddAllSUM
				/ testRepeats.doubleValue() + "\n");
		
		testTPSAddAll.clear();

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
					
					System.out.println("Worker "+id+"; size="+testTPSAdd.size());

			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		private boolean performTest(int count) throws Exception {
			
			boolean res = true;
			
//			abQueue.put(testStr + "-" + id + "-" + count);
			
//			lbQueue.put(testStr + "-" + id + "-" + count);
			
//			clQueue.offer(testStr + "-" + id + "-" + count);
			
			synchronized (ll) {
				ll.offer(testStr + "-" + id + "-" + count);
			}
			
			return res;
			
		}

	}

}
