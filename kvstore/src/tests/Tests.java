package tests;


import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import project.ConfigsServer;
import project.application.ClientApplication;
import project.application.ClientApplicationResult;
import project.master.MissingConfigurationException;
import project.master.StoreMaster;
import project.masters.dispatchers.SingleStoreDispatcher;
import project.masters.dispatchers.TwoStoreDispatcher;

public class Tests {

	public static void main(String[] args) {
		try {
			//Init.initTME(args);
			Tests t = new Tests();
			t.step1B();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Tests() {
		StoreMaster.setKVStores(ConfigsServer.getServersStores());
	}

	/**
	 * 
	 * 
	 * @throws MissingConfigurationException 
	 * 
	 */
	public void step1A() throws MissingConfigurationException {
		System.out.println("Tests.step1A() ...");

		// setup store master
		StoreMaster storeMaster = StoreMaster.getStoreMaster();
		storeMaster.setDispatcher(new SingleStoreDispatcher());

		final int maxClientNumber = 10; 

		// gives the allowed profile targets to the client application (0 to 4)
		ArrayList<Long> profileTargets = new ArrayList<Long>();
		for (long pt = 0; pt < 5; pt++)
			profileTargets.add(pt);

		Hashtable<Integer, Long> averageSumByClient = new Hashtable<Integer, Long>();
		for (int i = 1; i <= 10; i++)
			averageSumByClient.put(i, 0L);

		// ten tests to average
		for (int testNumber = 1; testNumber <= 10; testNumber++) {
			
			// for 1..10 client applications
			for (int clientNumber = 1; clientNumber <= maxClientNumber; clientNumber++) {
				System.out.println("test " + testNumber + ", " + clientNumber + " clients");
				// thread number
				ExecutorService executor = Executors.newFixedThreadPool(clientNumber);

				// List of Futures to catch results
				List<Future<ClientApplicationResult>> results = new ArrayList<Future<ClientApplicationResult>>();
				for (int i = 0; i < clientNumber; i++) {

					// Create and launch callables
					Callable<ClientApplicationResult> app = new ClientApplication(i, profileTargets);
					Future<ClientApplicationResult> submit = executor.submit(app);
					results.add(submit);
				}

				long sum = 0;
				// now retrieve the result
				for (Future<ClientApplicationResult> future : results) {
					try {
						sum += future.get().getAverageTransactionTime();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}
				}

				long average = sum/results.size();
				//System.out.println("  " + clientNumber + " clients");
				//System.out.println("Average transaction execution time:\n    " + average/1000000L + " ms");

				// compute and store new average
				long newSum = averageSumByClient.get(clientNumber) + average;
				averageSumByClient.put(clientNumber, newSum);
				executor.shutdown();
			}
		}

		// get averages over the 10 tests
		for (int i = 1; i <= 10; i++) {
			System.out.println(i + " clients: " + (averageSumByClient.get(i)/10));
		}
		System.out.println("Tests.step1A() ... done");	
	}

	public void step1B() throws MissingConfigurationException {
		System.out.println("Tests.step1B() go... ");	

		// setup storeMaster
		StoreMaster storeMaster = StoreMaster.getStoreMaster();
		storeMaster.setDispatcher(new TwoStoreDispatcher());
				
		// setup clients and profile targets
		final int clientNumber = 10;

		ArrayList<Long> evenTargets = new ArrayList<Long>();
		ArrayList<Long> oddTargets = new ArrayList<Long>();


		for (long profile = 0; profile < clientNumber; profile++) {
			if (profile%2 == 0)
				evenTargets.add(profile);
			else 
				oddTargets.add(profile);
		}

		// thread number
		ExecutorService executor = Executors.newFixedThreadPool(clientNumber);
		long testSum = 0;

		for (int test = 1; test <= 10; test++) {
			System.out.println("test " + test + "...");
			
			// List of Futures to catch results
			List<Future<ClientApplicationResult>> results = new ArrayList<Future<ClientApplicationResult>>();
			for (int client = 0; client < clientNumber; client++) {

				// Create and launch callables
				Callable<ClientApplicationResult> app = null;
				if (client%2 == 0) {
					app = new ClientApplication(client, evenTargets);
				}
				else {
					app = new ClientApplication(client, oddTargets);
				}
				Future<ClientApplicationResult> submit = executor.submit(app);
				results.add(submit);
			}

			long sum = 0;
			// now retrieve the result
			for (Future<ClientApplicationResult> future : results) {
				try {
					sum += future.get().getAverageTransactionTime();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
			}

			long average = sum/results.size();
			testSum += average;
			//System.out.println("  " + clientNumber + " clients");
			//System.out.println("Average transaction execution time:\n    " + average/1000000L + " ms");

		}
		long transactionAverageOverTests = testSum / 10;
		System.out.println("Transaction average over 10 tests: " + transactionAverageOverTests + " ns");
		executor.shutdown();

		System.out.println("Tests.step1B() ... done");	

	}

}
