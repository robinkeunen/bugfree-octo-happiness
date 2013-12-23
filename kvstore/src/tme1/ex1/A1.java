package tme1.ex1;

import oracle.kv.*;

/**
 * Our solution to the exercises 1 tme KVstore
 * @author Robin Keunen
 * @author Clément Barbier
 */
public class A1 {
	private final KVStore store;

	public static void main(String[] args) {
		try {
			//Init.initTME(args);
			A1 a = new A1();
			a.go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public A1() {
		String storeName = "kvstore";
		String hostName = "localhost";
		String hostPort = "5000";

		store = KVStoreFactory.getStore
				(new KVStoreConfig(storeName, hostName + ":" + hostPort));
	}

	/**
	 * Reads item P1 and increments it a thousand time.
	 * Robust to concurrent writes; 
	 */
	public void go() {
		System.out.println("A.go...");
		Key k = Key.createKey("P1");
		for (int i = 0; i < 1000; i++) {
			// attempts to increment until it succeeds.
			ValueVersion vs = store.get(k);
			int quantity = Integer.parseInt(new String(vs.getValue().getValue()));
			System.out.println("read " + quantity);
			quantity++;

			store.put(k, Value.createValue(String.valueOf(quantity).getBytes()));
			System.out.println("wrote " + quantity);
		}
		System.out.println("A.go() ... done");	
	}
}
