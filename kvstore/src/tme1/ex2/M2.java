package tme1.ex2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import oracle.kv.*;

/**
 * Solution to exercise 2 of tmeKVStore.
 * @author Robin Keunen
 * @author Clément Barbier
 */
public class M2 {
	private final KVStore store;

	public static void main(String[] args) {
		try {
			M2 m = new M2();
			m.go();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public M2() {
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
	private void go() {
		System.out.println("M2.go...");
		int lateWrite = 0;
		
		List<Key> keyList = new LinkedList<Key>();
		for (int j = 0; j < 5; j++) {
			String major = "C0";
			String minor = "P" + j;
			keyList.add(Key.createKey(major, minor));				
		}
		
		for (int i = 0; i < 1000; i++) {
			boolean writeDone = false;
			while (!writeDone) {
				// Scanning the values and determining the max value.
				List<ValueVersion> vss = new ArrayList<ValueVersion>();
				int max = 0;
				for (Key k : keyList) {
					ValueVersion vs = store.get(k);
					Integer v = Utils.intFromVV(vs);
					System.out.println(i + ". read" + k.toString() + " " + v.toString());
					vss.add(vs);
					if (v > max)
						max = v;
				}
				Value v = Value.createValue(Utils.bytesFromInteger(max + 1));

				// attempt to write max + 1 on P0 to P5
				int index = 0;
				for (Key k : keyList) {
					if (store.putIfVersion(k, v, vss.get(index).getVersion()) == null) {
						if (index > 0) {
							lateWrite++;
							System.out.println(i + ". ---- abort on later writes -----");
						}
						else
							System.out.println(i + ". ----- abort -----");
						writeDone = false;
						break;
					}
					else {
						System.out.println(i + ". wrote" + k.toString() + " " + (max + 1));
						index++;
						writeDone = true;
					}
				}
			}
		}
		
		System.out.println("M2.go() ... done");
		System.out.println("number of late writes: " + lateWrite);
	}
	
	}
