package tests;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import project.ConfigsServer;
import project.application.ClientApplication;
import project.application.ClientApplicationResult;
import project.master.StoreMaster;

public class LoadBalancingTest {
	public static void main(String[] args) {
		try {
			LoadBalancingTest t = new LoadBalancingTest();
			t.go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public LoadBalancingTest() {
		StoreMaster.setKVStores(ConfigsServer.getServersStores());
	}

	private void go() {
		System.out.println("Load Balancing Test go ...");

		final int clientNumber = 10; 

		// gives the allowed profile targets to the client application (0 to 30)
		ArrayList<Long> profileTargets = new ArrayList<Long>();
		for (long pt = 0; pt < 30; pt++)
			profileTargets.add(pt);

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
		System.out.println("  " + clientNumber + " clients");
		System.out.println("Average transaction execution time:\n    " + average/1000000L + " ms");

		executor.shutdown();
		
		System.out.println("Load Balancing Test ... Done");
	}

}
