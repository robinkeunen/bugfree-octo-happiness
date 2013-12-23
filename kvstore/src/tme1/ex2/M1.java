package tme1.ex2;

import java.util.LinkedList;
import java.util.List;

import oracle.kv.*;

/**
 * Solution to exercise 2 of tmeKVStore.
 * @author Robin Keunen
 * @author Clément Barbier
 */
public class M1 {
	private final KVStore store;

	public static void main(String[] args) {
		try {
			M1 m = new M1();
			m.m1();
			//m.transaction();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public M1() {
		String storeName = "kvstore";
		String hostName = "localhost";
		String hostPort = "5000";

		store = KVStoreFactory.getStore
				(new KVStoreConfig(storeName, hostName + ":" + hostPort));
	}

	/**
	 * m1 iterates 1000 times. Each iteration reads the value of 
	 * products P0 to P4 and increments it 
	 */
	public void m1() {
		System.out.println("M1.go...");
		for (int i = 0; i < 500; i++) {
			
			// key list creation
			List<Key> keyList = new LinkedList<Key>();
			for (int j = 0; j < 5; j++) {
				String major = "C0";
				String minor = "P" + j;
				keyList.add(Key.createKey(major, minor));				
			}
			
			for (Key k : keyList) {
				while (true) {
					ValueVersion vs = store.get(k);
					int quantity = Integer.parseInt(new String(vs.getValue().getValue()));
					System.out.println("read " + k.toString() +" " + quantity);
					quantity++;

					// Only writes if the version in KVStore is the same 
					// as the version read
					if (store.putIfVersion(k, Value.createValue(String.valueOf(quantity).getBytes()), vs.getVersion()) != null) {
						System.out.println("wrote" + k.toString() + " " + quantity);
						break;
					}
					else {
						System.out.println("------ abort write ------");
					}
				}

			}
		}
		System.out.println("M1.go() ... done");	
	}
	
}
