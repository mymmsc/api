/**
 * 
 */
package org.mymmsc.api.dbm.samples;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import org.mymmsc.api.dbm.StorageHashTable;

/**
 * @author WangFeng
 * 
 */
public class TestStoreHashMap {

	/**
	 * 
	 */
	public TestStoreHashMap() {
		//
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			StorageHashTable shm = new StorageHashTable(null);
			boolean bRet = shm.open("sso");
			if (bRet) {
				TestObject to = new TestObject("wangfeng", 36);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos);
				oos.writeObject(to);
				byte[] data = baos.toByteArray();
				shm.set("wangfeng", data);

				TestObject to1 = (TestObject) shm.get("wangfeng", null);
				System.out.println("Hello " + to1.getName() + ", "
						+ to1.getAgo());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
