package project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Random;
import java.util.UUID;

public class Item implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4235893260127955802L;

	public Item() {
		this.itemId = 0;
	}
	
	private long itemId;

	private int intField1;
	private int intField2;
	private int intField3;
	private int intField4;
	private int intField5;

	private String stringField1;
	private String stringField2;
	private String stringField3;
	private String stringField4;
	private String stringField5;


	public static Item createRandomItem() {
		Item item = new Item();

		Random random = new Random();

		item.setIntField1(random.nextInt());
		item.setIntField2(random.nextInt());
		item.setIntField3(random.nextInt());
		item.setIntField4(random.nextInt());
		item.setIntField5(random.nextInt());

		item.setStringField1(Utils.randomWord(10));
		item.setStringField2(Utils.randomWord(10));
		item.setStringField3(Utils.randomWord(10));
		item.setStringField4(Utils.randomWord(10));
		item.setStringField5(Utils.randomWord(10));

		return item;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Item [intField1=" + intField1 + ", intField2=" + intField2
				+ ", intField3=" + intField3 + ", intField4=" + intField4
				+ ", intField5=" + intField5 + ",\n      stringField1=" + stringField1
				+ ", stringField2=" + stringField2 + ", stringField3="
				+ stringField3 + ", stringField4=" + stringField4
				+ ", stringField5=" + stringField5 + "]";
	}

	public byte[] toByteArray() throws IOException {
		byte[] bytes;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);   
			out.writeObject(this);
			bytes = bos.toByteArray();
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
		return bytes;
	}
	
	/**
	 * 
	 * @param bytes
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException if the byte array did not represent a known class.
	 */
	public static Item fromByteArray(byte[] bytes) throws IOException, ClassNotFoundException {
		
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = null;
		Item item = null;
		try {
		  in = new ObjectInputStream(bis);
		  item = (Item) in.readObject(); 
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
		return item;
	}


	/**
	 * @return the intField1
	 */
	public int getIntField1() {
		return intField1;
	}
	/**
	 * @param intField1 the intField1 to set
	 */
	public void setIntField1(int intField1) {
		this.intField1 = intField1;
	}
	/**
	 * @return the intField2
	 */
	public int getIntField2() {
		return intField2;
	}
	/**
	 * @param intField2 the intField2 to set
	 */
	public void setIntField2(int intField2) {
		this.intField2 = intField2;
	}
	/**
	 * @return the intField3
	 */
	public int getIntField3() {
		return intField3;
	}
	/**
	 * @param intField3 the intField3 to set
	 */
	public void setIntField3(int intField3) {
		this.intField3 = intField3;
	}
	/**
	 * @return the intField4
	 */
	public int getIntField4() {
		return intField4;
	}
	/**
	 * @param intField4 the intField4 to set
	 */
	public void setIntField4(int intField4) {
		this.intField4 = intField4;
	}
	/**
	 * @return the intField5
	 */
	public int getIntField5() {
		return intField5;
	}
	/**
	 * @param intField5 the intField5 to set
	 */
	public void setIntField5(int intField5) {
		this.intField5 = intField5;
	}
	/**
	 * @return the stringField1
	 */
	public String getStringField1() {
		return stringField1;
	}
	/**
	 * @param stringField1 the stringField1 to set
	 */
	public void setStringField1(String stringField1) {
		this.stringField1 = stringField1;
	}
	/**
	 * @return the stringField2
	 */
	public String getStringField2() {
		return stringField2;
	}
	/**
	 * @param stringField2 the stringField2 to set
	 */
	public void setStringField2(String stringField2) {
		this.stringField2 = stringField2;
	}
	/**
	 * @return the stringField3
	 */
	public String getStringField3() {
		return stringField3;
	}
	/**
	 * @param stringField3 the stringField3 to set
	 */
	public void setStringField3(String stringField3) {
		this.stringField3 = stringField3;
	}
	/**
	 * @return the stringField4
	 */
	public String getStringField4() {
		return stringField4;
	}
	/**
	 * @param stringField4 the stringField4 to set
	 */
	public void setStringField4(String stringField4) {
		this.stringField4 = stringField4;
	}
	/**
	 * @return the stringField5
	 */
	public String getStringField5() {
		return stringField5;
	}
	/**
	 * @param stringField5 the stringField5 to set
	 */
	public void setStringField5(String stringField5) {
		this.stringField5 = stringField5;
	}

	public long getItemId() {
		return this.itemId;
	}	
	
	public void setItemId(long id) {
		this.itemId = id;
	}	

}
