package project.application;

import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.Callable;

import oracle.kv.OperationExecutionException;
import project.master.MissingConfigurationException;
import project.master.StoreMaster;

public class ClientApplication implements Callable<ClientApplicationResult> {

	private long id;
	private ArrayList<Long> profileTargets;
	private StoreMaster storeMaster;

	private ClientApplication() {
		
		this.setId(0);
		try {
			this.storeMaster = StoreMaster.getStoreMaster();
		} catch (MissingConfigurationException e) {
			e.printStackTrace();
		}
	}

	public ClientApplication(long id, ArrayList<Long> profileTargets) {
		this();
		this.profileTargets = profileTargets;
		this.setId(id);
		
	}

	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	private void setId(long id) {
		this.id = id;
	}

	@Override
	public ClientApplicationResult call() throws Exception {
		
		Random random = new Random();

		long startTime = System.nanoTime();
		//long fiveSeconds = 5000000000L;
		long tenSeconds = 10000000000L;

		int iterations = 0;
		long profileId = 0;

		while (System.nanoTime() - startTime < tenSeconds) {
			int randIndex = random.nextInt(profileTargets.size());
			profileId = profileTargets.get(randIndex);
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

		return result;
	}

}
