package org.mymmsc.api.samples.test;

import java.util.Date;

import org.mymmsc.api.dbm.StorageHashTable;
import org.mymmsc.api.dbm.samples.TestObject;

/**
 * Test Sdbm.
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-test 6.3.9
 */
public class StorageHashTableTest {
	private String m_name = null;
	// keep track of how long the test took
	private long m_time;
	private int m_high = 10000;

	public StorageHashTableTest(String name, int num) {
		m_name = name;
		m_high = num;
	}

	public void testIt() throws Exception {
		StorageHashTable sht = new StorageHashTable(System
				.getProperty("java.io.tmpdir")
				+ "/mymmsc");
		sht.open("filt");
		startClock();

		int low = 1;
		int n = 0;
		boolean found = false;
		int count = 0;
		int lastCount = -1;
		String pad = null;
		String key = null;
		String value = null;
		long countX = 0;
		while (!found) {

			count = ((int) (m_high + low) / 2);
			System.out.println(m_name + "> Current size: " + count + " low: "
					+ low + " m_high: " + m_high);
			boolean error = false;
			sht.clear();
			TestObject to = null;
			for (int i = 0; i < count; i++) {
				pad = pad(i);
				key = "key" + pad;
				value = "val" + pad;
				to = new TestObject(value, i);
				sht.set(key, to);
			}

			int j = 0;
			while (!error && j < count) {

				pad = pad(j);
				key = "key" + pad;
				value = "val" + pad;
				TestObject to1 = (TestObject) sht.get(key, null);
				error = (!value.equals(to1.getName()));
				j++;
			}

			if (error) {
				System.out.println(m_name + "> ERROR: Lost value for key "
						+ key + " at " + count + " entries");
				m_high = count - 1;
				System.out.println(m_name + "> ERROR: Lost value for key "
						+ key + " at " + count + " entries");
			} else {
				System.out.println(m_name + "> SUCCESS: No loss with  " + count
						+ " entries");
				low = count + 1;
			}

			found = Math.abs(count - lastCount) < 2;
			lastCount = count;
			countX += count;
			n++;

		}

		System.out.println(m_name + "> Stopped at: " + count + " in "
				+ (getTime() / 1000) + " seconds and " + n + " iterations");
		System.out.println(m_name + "> " + (countX / (getTime() / 1000))
				+ "%f / s.");

	}

	private void startClock() {
		m_time = new Date().getTime();
	}

	private long getTime() {
		long now = new Date().getTime();
		return (now - m_time);
	}

	private String pad(int i) {
		if (i > 999999)
			return "" + i;
		if (i > 99999)
			return "0" + i;
		if (i > 9999)
			return "00" + i;
		if (i > 999)
			return "000" + i;
		if (i > 99)
			return "0000" + i;
		if (i > 9)
			return "00000" + i;
		return "000000" + i;
	}

}
