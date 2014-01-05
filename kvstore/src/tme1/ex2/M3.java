package tme1.ex2;

import java.util.ArrayList;
import java.util.List;

import oracle.kv.KVStore;
import oracle.kv.KVStoreConfig;
import oracle.kv.KVStoreFactory;
import oracle.kv.Key;
import oracle.kv.OperationExecutionException;
import oracle.kv.Value;
import oracle.kv.ValueVersion;

public class M3 {
	
	private final KVStore store;

	public static void main(String[] args) {
		try {
			M3 m = new M3();
			m.go();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public M3() {
		String storeName = "kvstore";
		String hostName = "localhost";
		String hostPort = "5000";

		store = KVStoreFactory.getStore
				(new KVStoreConfig(storeName, hostName + ":" + hostPort));
	}
	
	private void go() {
		System.out.println("M3.go...");

		List<Key> keyList = new ArrayList<Key>();
		for (int j = 0; j < 5; j++) {
			String major = "C0";
			String minor = "P" + j;
			keyList.add(Key.createKey(major, minor));				
		}
		
		for (int i = 0; i < 500; i++) {
			
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
				
				// written value
				Value value = Value.createValue(Utils.bytesFromInteger(max + 1));
				
				// add putIfVersion operations to the atomic transaction
				Transaction transaction = new Transaction();
				for (int index = 0; index < keyList.size(); index++) {
					transaction.addPutIfVersion(
							keyList.get(index), value, vss.get(index).getVersion());
				}
				
				try {
					transaction.execute();
					System.out.println(i + ". wrote " + (max + 1));
					writeDone = true;
				} catch (OperationExecutionException e) {
					System.out.println(i + ". ----- abort -----");
					writeDone = false;
				}		
			}
		}
		
		System.out.println("M3 ... done");
	}
	
	private void transaction() {
		

		for (int i = 0; i < 1000; i++) {
			boolean succesfulWrite = false;

			/*List<Operation> operations = new ArrayList<Operation>();
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
			}*/
		}
		

	}

	
}
