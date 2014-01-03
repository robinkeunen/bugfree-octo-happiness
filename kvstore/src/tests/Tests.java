package tests;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.Key;
import oracle.kv.Value;
import oracle.kv.ValueVersion;
import oracle.kv.avro.AvroCatalog;
import oracle.kv.avro.GenericAvroBinding;
import project.Item;
import project.application.ClientApplication;
import project.application.ClientApplicationResult;
import project.master.MissingConfigurationException;
import project.master.StoreMaster;

public class Tests {

	public static void main(String[] args) {
		try {
			//Init.initTME(args);
			Tests t = new Tests();
			t.step1A();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Tests() {
	}

	/**
	 * 
	 * 
	 * @throws MissingConfigurationException 
	 * 
	 */
	public void step1A() throws MissingConfigurationException {
		System.out.println("Tests.go() ...");
		final int maxClientNumber = 10; 

		for (int clientNumber = 1; clientNumber <= maxClientNumber; clientNumber++) {
			ExecutorService executor = Executors.newFixedThreadPool(clientNumber);
			List<Future<ClientApplicationResult>> results = new ArrayList<Future<ClientApplicationResult>>();
			for (int i = 0; i < clientNumber; i++) {
				Callable<ClientApplicationResult> app = new ClientApplication("Client " + String.valueOf(i));
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
			System.out.println("Average transaction execution time:\n    " + average/1000000L + " ms");
			executor.shutdown();
		}
		
		System.out.println("Tests.go() ... done");	
	}

}
