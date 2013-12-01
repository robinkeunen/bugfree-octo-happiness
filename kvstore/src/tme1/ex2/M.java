package tme1.ex2;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import oracle.kv.*;

public class M {
	private final KVStore store;

	public static void main(String[] args) {
		try {
			M m = new M();
			m.transaction();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public M() {
		String storeName = "kvstore";
		String hostName = "localhost";
		String hostPort = "5000";

		store = KVStoreFactory.getStore
				(new KVStoreConfig(storeName, hostName + ":" + hostPort));
	}

	public void m1() {
		System.out.println("M1.go...");
		for (int i = 0; i < 1000; i++) {
			for (int j = 0; j < 5; j++) {
				while (true) {
					String keyname = "C0:P" + j;
					Key k = Key.createKey(keyname);
					ValueVersion vs = store.get(k);
					int quantity = Integer.parseInt(new String(vs.getValue().getValue()));
					System.out.println("read " + keyname +" " + quantity);
					quantity++;

					if (store.putIfVersion(k, Value.createValue(String.valueOf(quantity).getBytes()), vs.getVersion()) != null) {
						System.out.println("wrote" + keyname + " " + quantity);
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

	public void m2() {
		System.out.println("M2.go...");

		List<Key> keyList = new LinkedList<Key>();
		for (int j = 0; j < 5; j++) {
			String major = "C0";
			String minor = "P" + j;
			keyList.add(Key.createKey(major, minor));				
		}

		for (int i = 0; i < 300; i++) {
			boolean writeDone = false;
			while (!writeDone) {
				List<ValueVersion> vss = new ArrayList<ValueVersion>();
				int max = 0;
				for (Key k : keyList) {
					ValueVersion vs = store.get(k);
					Integer v = intFromVV(vs);
					System.out.println(i + ". read" + k.toString() + " " + v.toString());
					vss.add(vs);
					if (v > max)
						max = v;
				}
				Value v = Value.createValue(bytesFromInteger(max + 1));

				int index = 0;
				for (Key k : keyList) {
					if (store.putIfVersion(k, v, vss.get(index).getVersion()) == null) {
						System.out.println(i + ". ----- abort -----");
						writeDone = false;
						break;
					}
					else {
						System.out.println(i + ". wrote" + k.toString() + " " + max);
						index++;
						writeDone = true;
					}
				}
			}
		}
		System.out.println("M2.go() ... done");	
	}

	private void transaction() {
		System.out.println("transaction.go...");

		List<Key> keyList = new LinkedList<Key>();
		for (int j = 0; j < 5; j++) {
			String major = "C0";
			String minor = "P" + j;
			keyList.add(Key.createKey(major, minor));				
		}

		for (int i = 0; i < 1000; i++) {
			boolean succesfulWrite = false;

			while (!succesfulWrite) {
				int max = 0;
				try {
					List<ValueVersion> vss = new ArrayList<ValueVersion>();
					for (Key k : keyList) {
						ValueVersion vs = store.get(k);
						Integer v = intFromVV(vs);
						System.out.println(i + ". read" + k.toString() + " " + v.toString());
						vss.add(vs);
						if (v > max)
							max = v;
					}
					Value v = Value.createValue(bytesFromInteger(max + 1));

					List<Operation> operations = new ArrayList<Operation>();
					OperationFactory of = store.getOperationFactory();
					int index = 0;
					for (Key k : keyList) {
						Operation piv = of.createPutIfVersion(
								k, v, vss.get(index).getVersion(), 
								ReturnValueVersion.Choice.NONE, true);
						operations.add(piv);
						index++;
					}

					store.execute(operations);
					System.out.println(i + ". wrote " + (max + 1));
					succesfulWrite = true;
				} catch (OperationExecutionException e) {
					succesfulWrite = false;
					System.out.println("----- abort -----");
					continue;
				}
			}
		}
		System.out.println("transaction ... done");

	}

	private Integer intFromVV(ValueVersion vs) {
		return Integer.parseInt(new String(vs.getValue().getValue()));
	}

	private byte[] bytesFromInteger(Integer i) {
		return String.valueOf(i).getBytes();
	}
}
