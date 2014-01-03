package project.application;

import java.util.Random;
import java.util.concurrent.Callable;

import oracle.kv.OperationExecutionException;
import project.ConfigsServer;
import project.master.MissingConfigurationException;
import project.master.StoreMaster;

public class ClientApplication implements Callable<ClientApplicationResult> {

	private String name;
	private StoreMaster storeMaster;

	public ClientApplication() {
		this.name = "";
		try {
			StoreMaster.setKVStores(ConfigsServer.getServersStores());
			this.storeMaster = StoreMaster.getStoreMaster();

		} catch (MissingConfigurationException e) {
			e.printStackTrace();
		}
	}

	public ClientApplication(String name) {
		this();
		this.name = name;
	}

	@Override
	public ClientApplicationResult call() throws Exception {

		System.out.println("  ClientApplication " + this.name + " go...");

		Random random = new Random();

		long startTime = System.nanoTime();
		long fiveSeconds = 5000000000L;
		long tenSeconds = 10000000000L;

		int iterations = 0;
		long profileId = 0;

		while (System.nanoTime() - startTime < tenSeconds) {
			profileId = (long) random.nextInt(5);
			try {
				storeMaster.doProfileTransaction(profileId);
			} catch (OperationExecutionException e) {
				e.printStackTrace();
			}
			iterations++;
		}
		long executionTime = System.nanoTime() - startTime;
		long averageIterationTime = executionTime/ iterations;
		ClientApplicationResult result = 
				new ClientApplicationResult(iterations, executionTime, averageIterationTime);
		//System.out.println("    " + this.name + " Performed  " + iterations + "  iterations.");
		//System.out.println("    " + this.name + " Total execution time: " + executionTime / 1000000L + " ms.");
		//System.out.println("    " + this.name + " Average execution time: " + (executionTime/iterations) + " ns = " + (executionTime/iterations) / 1000000L + " ms");

		System.out.println("  ClientApplication " + this.name + " go... done");
		return result;
	}

}
