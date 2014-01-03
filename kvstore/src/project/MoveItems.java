package project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;

import oracle.kv.KVStore;
import oracle.kv.Key;
import oracle.kv.Value;
import oracle.kv.ValueVersion;

public class MoveItems {

	private final KVStore storeA;
	private final KVStore storeB;
	private final int NB_PROFIL = 10;
	
	public MoveItems(String[] args) {
		ServerParameters kvstoreA = new ServerParameters("store1","127.0.0.1","5000");
		ServerParameters kvstoreB = new ServerParameters("store2","127.0.0.1","5020");
		this.storeA = kvstoreA.getStore();
		this.storeB = kvstoreB.getStore();
	}

	public static void main(String[] args) {
		try {
			MoveItems mvi = new MoveItems(args);
			mvi.removeAll();
			mvi.test();
			mvi.init();
			mvi.test();
			mvi.move();
			mvi.test();
			mvi.end();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void removeAll() {
		for(int i = 1; i <= NB_PROFIL; i++) {
			// Profil
			String pdtKey = "P"+i;
			if(storeA !=null )removeProfil(storeA, pdtKey);
		}
	}

	private void init() throws IOException {
		System.out.println("Initialisation...");
		if(storeA != null) {
			for(int i = 1; i <= NB_PROFIL; i++) {
				// Profil
				String pdtKey = "P"+i;
				// Items
		        Random rand = new Random();
		        int nbItems = rand.nextInt(5) + 1;
		        for(int j = 1; j <= nbItems; j++) {
					// Key
		        	String objKey = "I"+j;
					Key key = Key.createKey(pdtKey, objKey);
					// Value
					Item wItem = Item.createRandomItem();
					byte[] itemValue = itemToByteArray(wItem);
					// Store Created Item.
					if(itemValue != null) {
						storeA.putIfAbsent(key, Value.createValue(itemValue));
						System.out.println("Put "+key.toString()+"= "+wItem.toString());
					}
					else {
						System.err.println("ERROR : Put "+key.toString()+"= "+wItem.toString());
					}
		        }
			}
			System.out.println("...Initialisation Done");
		}
		else {
			System.out.println("...Initialisation Failed");
		}
	}
	
	/**
	 * Print every items for every profils.
	 */
	private void test() {
		System.out.println("Test Store(s)...");
		System.out.println("--- Store1 ---");
		this.printStore(this.storeA);
		System.out.println("--- Store2 ---");
		this.printStore(this.storeB);
		System.out.println("...Test Store(s) Done");
	}
	
	private void move() {
		// Move P5
		String profilKey = "P5";
		moveProfil(storeA, storeB, profilKey);
	}

	private void end() {
		if(this.storeA != null) this.storeA.close();
		if(this.storeB != null) this.storeB.close();
	}
	
	private void moveProfil(KVStore kv_src, KVStore kv_targ, String profilID) {
		// Read in StoreA
		Key key = Key.createKey(profilID);
		SortedMap<Key, ValueVersion> profilItems = kv_src.multiGet(key, null, null);
		System.out.println("MOVE - GET ITEMS OF "+profilID+" FROM StoreSrc = "+profilItems.size()+" item(s).");
		// Write in StoreB
		for (Map.Entry<Key, ValueVersion> entry : profilItems.entrySet()) {
			System.out.println("MOVE - MOVE "+entry.getKey().getFullPath()+" from StoreSrc to StoreTarg");
			kv_targ.put(entry.getKey(), entry.getValue().getValue());
		}
		// Delete in StoreA
		System.out.println("MOVE - DELETE "+profilID+" from StoreSrc");
		removeProfil(kv_src, profilID);		
	}
	
	private void removeProfil(KVStore kv, String profilID) {
		Key key = Key.createKey(profilID);
		kv.multiDelete(key, null, null);
	}
	
	private void printStore(KVStore kv) {
		for(int i = 1; i <= NB_PROFIL; i++) {
			// Key
			String profilID = "P"+i;
			Key key = Key.createKey(profilID);
			SortedMap<Key, ValueVersion> items = kv.multiGet(key, null, null);
			for (Map.Entry<Key, ValueVersion> entry : items.entrySet()) {
				Item item = byteArrayToItem(entry.getValue().getValue().getValue());
				if(item != null) {
					System.out.println("Get "+entry.getKey().getFullPath()+" = "+item.toString());
				}
			} 
		}
	}
	
	private byte[] itemToByteArray(final Item it) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] ret = null;
		try {
			out = new ObjectOutputStream(bos);   
			out.writeObject(it);
			ret = bos.toByteArray();
		} catch (IOException e) {
			return null;
		} finally {
			try {
				if (out != null) {
					out.close();
				}
				bos.close();					
			} catch (IOException e) {
			
			}
		}	
		return ret;
	}
	
	private Item byteArrayToItem(final byte[] byteItem) {
		ByteArrayInputStream bis = new ByteArrayInputStream(byteItem);
		ObjectInput in = null;
		Item ret = null;
		try {
			in = new ObjectInputStream(bis);
			ret = (Item) in.readObject(); 
		} catch (ClassNotFoundException | IOException e) {
			return null;
		} finally {
			  try {
				if (in != null) {
					in.close();
				}
			    bis.close();
			  } catch (IOException e) {
			  }
		}	
		return ret;
	}
}
