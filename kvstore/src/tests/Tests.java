package tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;

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
	 * 
	 */
	public void go() throws IOException {
		System.out.println("Tests.go...");

		Item wItem = Item.createRandomItem();
		System.out.println("wItem: \n" + wItem.toString()); 
		byte[] yourBytes;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);   
			out.writeObject(wItem);
			yourBytes = bos.toByteArray();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
			try {
				bos.close();
			} catch (IOException ex) {
				// ignore close exception
			}
		}
		
		store.put(Key.createKey("key"), Value.createValue(yourBytes));
		ValueVersion vv = store.get(Key.createKey("key"));

		ByteArrayInputStream bis = new ByteArrayInputStream(vv.getValue().getValue());
		ObjectInput in = null;
		try {
		  in = new ObjectInputStream(bis);
		  Item o = (Item) in.readObject(); 
		  System.out.println(o.toString());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
		  try {
		    bis.close();
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		  try {
		    if (in != null) {
		      in.close();
		    }
		  } catch (IOException ex) {
		    // ignore close exception
		  }
		}



		//		Schema.Parser parser = new Schema.Parser();
		//		parser.parse(new File("/Users/roke/Dropbox/eclipse-ws/abdr/kvstore/src/project/item.avsc"));
		//		Schema schema = parser.getTypes().get("project.Item");
		//		AvroCatalog catalog = store.getAvroCatalog();
		//		GenericAvroBinding binding = catalog.getGenericBinding(schema);
		//		
		//		GenericRecord item = new GenericData.Record(schema);
		//		item.put("intField1", 3);
		//		store.put(Key.createKey("key"), binding.toValue(item));
		//		
		//		ValueVersion vv = store.get(Key.createKey("key"));
		//		GenericRecord read;
		//		int readField;
		//		
		//		if (vv != null) {
		//			read = binding.toObject(vv.getValue());
		//			readField = (Integer) read.get("intField1");
		//			System.out.println("read " + readField);
		//		}

		System.out.println("Tests.go() ... done");	
	}

}
