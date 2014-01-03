package tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.LinkedList;
import java.util.List;

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
import project.master.MissingConfigurationException;
import project.master.StoreMaster;

public class Tests {
	private final KVStore store;

	public static void main(String[] args) {
		try {
			//Init.initTME(args);
			Tests t = new Tests();
			t.go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Tests() {
		String storeName = "store1";
		String hostName = "localhost";
		String hostPort = "5000";

		store = KVStoreFactory.getStore
				(new KVStoreConfig(storeName, hostName + ":" + hostPort));
	}

	/**
	 * Reads item P1 and increments it a thousand time.
	 * Robust to concurrent writes; 
	 * @throws IOException 
	 * @throws MissingConfigurationException 
	 * 
	 */
	public void go() throws IOException, MissingConfigurationException {
		System.out.println("Tests.go() ...");
		ClientApplication ca = new ClientApplication("A");
		ca.go();
		System.out.println("Tests.go() ... done");	
	}

}
