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
import project.master.MissingConfigurationException;
import project.master.StoreMaster;
import project.store.TransactionMetrics;

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

	private void go() throws MissingConfigurationException {
		System.out.println("Load Balancing Test go ...");

		final int clientNumber = 20; 

		// targets for app with uniform profile distribution
		ArrayList<Long> uniformTargets = new ArrayList<Long>();
		for (long ut = 0; ut < 20; ut++)
			if (ut%5 != 4)
				uniformTargets.add(ut);
		
		// targets on specific profiles
		ArrayList<Long> specificTargets = new ArrayList<Long>();
		for (long st = 0; st < 20 ; st++) {
			if (st % 5 == 0)
				specificTargets.add(st);
		}
		
		ExecutorService executor = Executors.newFixedThreadPool(clientNumber);

		// List of Futures to catch results
		List<Future<ClientApplicationResult>> results = new ArrayList<Future<ClientApplicationResult>>();
		for (int i = 0; i < clientNumber; i++) {
			
			// Create and launch callables
			Callable<ClientApplicationResult> app = null;
			if (i < 10)
				app = new ClientApplication(i, uniformTargets);
			else
				app = new ClientApplication(i, specificTargets);
			
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
		
		StoreMaster master = StoreMaster.getStoreMaster();
		List<TransactionMetrics> transactionMetricsList = master.getTransactionMetrics();
		int index = 0;
		for (TransactionMetrics transactionMetrics: transactionMetricsList) {
			String prefix = "  store " + index + " - " + transactionMetrics.getOperationName() + " - ";
			System.out.println(prefix + transactionMetrics.getTotalRequests() + " requests");
			System.out.println(prefix + transactionMetrics.getTotalOps() + " operations");
			System.out.println(prefix + "min latency " + transactionMetrics.getMinLatencyMs() + " ms");
			System.out.println(prefix + "avg latency " + transactionMetrics.getAverageLatencyMs() + " ms");
			System.out.println(prefix + "max latency " + transactionMetrics.getMaxLatencyMs() + " ms");
			System.out.println(prefix + "filtered latency " + transactionMetrics.getFilteredLatency() + " ms");
			System.out.println();
			index++;
		}
		
		master.stop();
		System.out.println("Load Balancing Test ... Done");
	}

}
