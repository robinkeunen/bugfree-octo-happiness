package tme1.ex1;

import oracle.kv.*;

public class A {
	private final KVStore store;
	
	public static void main(String[] args) {
		try {
			//Init.initTME(args);
			A a = new A();
			a.go();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public A() {
		String storeName = "kvstore";
		String hostName = "localhost";
		String hostPort = "5000";
		
		store = KVStoreFactory.getStore
				(new KVStoreConfig(storeName, hostName + ":" + hostPort));
	}
	
	public void go() {
		System.out.println("A.go...");
		Key k = Key.createKey("P1");
		for (int i = 0; i < 1000; i++) {
			while (true) {
				ValueVersion vs = store.get(k);
				int quantity = Integer.parseInt(new String(vs.getValue().getValue()));
				//System.out.println("read " + quantity);
				quantity++;
				
				if (store.putIfVersion(k, Value.createValue(String.valueOf(quantity).getBytes()), vs.getVersion()) != null) {
					//System.out.println("wrote " + quantity);
					break;
				}
				else {
					System.out.println("abort write");
				}
			}
		}
		System.out.println("A.go() ... done");	
	}
}
