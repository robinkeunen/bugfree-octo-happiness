package project.application;

import java.util.Random;

<<<<<<< HEAD
import oracle.kv.OperationExecutionException;
=======
import project.ConfigsServer;
>>>>>>> 6109e2e22bef114d0bbad77368dedcac0f95d45b
import project.master.MissingConfigurationException;
import project.master.StoreMaster;

public class ClientApplication {
	
	private String name;
	private StoreMaster storeMaster;
	
	public ClientApplication() {
		this.name = "";
		try {
			this.storeMaster = StoreMaster.getStoreMaster();
			StoreMaster.setKVStores(ConfigsServer.getServersStores());
		} catch (MissingConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	public ClientApplication(String name) {
		this();
		this.name = name;
	}
	
	
	public long go() throws MissingConfigurationException {
		System.out.println("  ClientApplication " + this.name + " go...");
		
		Random random = new Random();
		StoreMaster master = StoreMaster.getStoreMaster(); 
		
		long startTime = System.nanoTime();
		long fiveSeconds = 5000000000L;
		long tenSeconds = 10000000000L;
		
		int iterations = 0;
		long profileId = 0;
		
		while (System.nanoTime() - startTime < fiveSeconds) {
			profileId = (long) random.nextInt(5);
			try {
				storeMaster.doProfileTransaction(profileId);
			} catch (OperationExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			iterations++;
		}
		long executionTime = System.nanoTime() - startTime;
		float averageIterationTime = (float) executionTime/ (float) iterations;
		System.out.println("    Performed  " + iterations + "  iterations.");
		System.out.println("    Total execution time: " + executionTime / 1000000L + " ms.");
		System.out.println("    Average execution time: " + (executionTime/iterations) + " ns = " + (executionTime/iterations) / 1000000L + " ms");
		
		System.out.println("  ClientApplication " + this.name + " go... done");
		return (long) averageIterationTime;	
		
	}
}
