package project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;

import oracle.kv.Depth;
import oracle.kv.Direction;
import oracle.kv.KVStore;
import oracle.kv.Key;
import oracle.kv.Value;
import oracle.kv.ValueVersion;

public class MoveItems {

	private final KVStore storeA;
	private final KVStore storeB;
	private final int NB_PROFIL = 10;
	
	public MoveItems(String[] args) {
		KvStore kvstoreA = new KvStore("store1","127.0.0.1","5000");
		//KvStore kvstoreB = new KvStore("store2","127.0.0.1","5020");
		this.storeA = kvstoreA.getStore();
		//this.storeB = kvstoreB.getStore();
		this.storeB = null;
	}

	public static void main(String[] args) {
		try {
			MoveItems mvi = new MoveItems(args);
			mvi.init();
			mvi.test();
			mvi.move();
			mvi.end();
		} catch (Exception e) {
			e.printStackTrace();
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
		for(int i = 1; i <= NB_PROFIL; i++) {
			// Key
			String pdtKey = "P"+i;
			Key key = Key.createKey(pdtKey);
			// Get Stored Items for this majorKey
			SortedMap<Key, ValueVersion> items = storeA.multiGet(key, null, null);
			for (Map.Entry<Key, ValueVersion> entry : items.entrySet()) {
				Item item = byteArrayToItem(entry.getValue().getValue().getValue());
				if(item != null) {
					System.out.println("Get "+entry.getKey().getFullPath()+" = "+item.toString());
				}
				else {
					System.err.println("ERROR : Get "+entry.getKey().getFullPath()+" = "+entry.getValue().getValue().toString());
				}
			} 
		}
		System.out.println("...Test Store(s) Done");
	}
	
	private void move() {
		
	}

	private void end() {
		if(this.storeA != null) this.storeA.close();
		if(this.storeB != null) this.storeB.close();
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
