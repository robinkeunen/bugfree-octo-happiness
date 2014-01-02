package project;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import oracle.kv.KVStore;
import oracle.kv.Key;

public class MoveItems {

	private final KVStore storeA;
	private final KVStore storeB;
	
	public MoveItems(String[] args) {
		KvStore kvstoreA = new KvStore("store1","127.0.0.1","5000");
		KvStore kvstoreB = new KvStore("store2","127.0.0.1","5020");
		this.storeA = kvstoreA.getStore();
		this.storeB = kvstoreB.getStore();
	}

	public static void main(String[] args) {
		try {
			MoveItems mvi = new MoveItems(args);
			mvi.init();
			mvi.move();
			mvi.end();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void init() {
		System.out.println("Initialisation...");
		if(storeA != null && storeB != null) {
			System.out.println("...Initialisation Done");
			for(int i = 1; i <= 1000; i++) {
				// Profil
				String pdtKey = "P"+i;
				// Items
		        Random rand = new Random();
		        int nbItems = rand.nextInt(5) + 1;
		        for(int j = 1; j <= nbItems; j++) {
					String objKey = "I"+j;
					List<String> majorPath = Arrays.asList(pdtKey);
					Key key = Key.createKey(majorPath, objKey);
					System.out.println("Profile "+i+" Item "+j+"= "+key.toString());
		        }
			}
		}
		else {
			System.out.println("...Initialisation Failed");
		}
	}
	
	private void move() {
		
	}

	private void end() {
		this.storeA.close();
		this.storeB.close();
	}
}
