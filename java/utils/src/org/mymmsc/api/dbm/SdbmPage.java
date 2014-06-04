package org.mymmsc.api.dbm;

import java.util.Enumeration;
import java.util.NoSuchElementException;

/**
 * Java rewrite of sdbm sdbm - ndbm work-alike hashed database library based on
 * Per-Aake Larson's Dynamic Hashing algorithms. BIT 18 (1978). original author:
 * oz@nexus.yorku.ca status: public domain.
 * 
 * @author Justin F. Chapweske <justin@chapweske.com>
 * @version .01 06/06/98 page-level routines
 */

public class SdbmPage implements Cloneable {

	public byte[] m_pag;
	public int m_bno; // FIX this should be seperate from the page.
	public int m_pageSize;

	public SdbmPage(int pageSize) {
		this.m_pageSize = pageSize;
		this.m_pag = new byte[pageSize];
	}

	public SdbmPage(byte[] b) {
		this.m_pageSize = b.length;
		this.m_pag = b;
	}

	public Object clone() {
		byte[] b = new byte[m_pag.length];
		System.arraycopy(m_pag, 0, b, 0, m_pag.length);
		return new SdbmPage(b);
	}

	/**
	 * Returns a short from two bytes with MSB last (little endian)
	 */
	private short getIno(int i) {
		return (short) (((m_pag[2 * i + 1] & 0xff) << 8) | (m_pag[2 * i] & 0xff));
	}

	/**
	 * Sets a short from two bytes with MSB last (little endian)
	 */
	private void setIno(int i, short val) {
		m_pag[2 * i + 1] = (byte) ((val >>> 8) & 0xff);
		m_pag[2 * i] = (byte) (val & 0xff);
	}

	/**
	 * <pre>
	 * page format:
	 *     +------------------------------+
	 * ino  | n | keyoff | datoff | keyoff |
	 * 	    +------------+--------+--------+
	 *     | datoff | - - - ---->	   |
	 *     +--------+---------------------+
	 *      |	    F R E E A R E A        |
	 *     +--------------+---------------+
	 *     |  <---- - - - | data          |
	 *     +--------+-----+----+----------+
	 *     |  key   | data     | key      |
	 *     +--------+----------+----------+
	 * 
	 * </pre>
	 * 
	 * calculating the offsets for free area: if the number of entries (ino[0])
	 * is zero, the offset to the END of the free area is the block size.
	 * Otherwise, it is the nth (ino[ino[0]]) entry's offset.
	 */
	public boolean hasRoom(int need) {
		int n;
		int off;
		int free;

		off = ((n = getIno(0)) > 0) ? getIno(n) : m_pageSize;
		free = off - (n + 1) * StorageHashTable.SHORTSIZ;
		need += 2 * StorageHashTable.SHORTSIZ;

		// System.out.println("free "+new Integer(free)+" need "+
		// new Integer(need));

		return need <= free;
	}

	public byte[] put(byte[] key, byte[] val) {
		// Remove any previous values
		remove(key);

		if (!hasRoom(key.length + val.length)) {
			throw new IllegalStateException("Not enough room for : key="
					+ new String(key) + ",val=" + new String(val));
		}

		int n;
		int off;

		off = ((n = getIno(0)) > 0) ? getIno(n) : m_pageSize;

		// enter the key first
		off -= key.length;
		System.arraycopy(key, 0, m_pag, off, key.length);
		setIno(n + 1, (short) off);

		// now the data
		off -= val.length;
		System.arraycopy(val, 0, m_pag, off, val.length);
		setIno(n + 2, (short) off);

		// adjust item count
		setIno(0, (short) (getIno(0) + 2));
		return val;
	}

	public byte[] get(byte[] key) {
		int i;
		if ((i = indexOfValue(key)) == -1) {
			return null;
		}
		byte[] b = new byte[getIno(i) - getIno(i + 1)];
		System.arraycopy(m_pag, getIno(i + 1), b, 0, getIno(i) - getIno(i + 1));
		return b;
	}

	public byte[] getKeyAt(int n) {
		if (n >= size()) {
			throw new ArrayIndexOutOfBoundsException(n);
		}

		int off = getIno(n * 2 + 1);
		int len = (n == 0 ? m_pageSize : getIno((n * 2 + 1) - 1)) - off;

		byte[] b = new byte[len];
		System.arraycopy(m_pag, off, b, 0, len);
		return b;
	}

	public byte[] getElementAt(int n) {
		if (n >= size()) {
			throw new ArrayIndexOutOfBoundsException(n);
		}

		int off = getIno(n * 2 + 2);
		int len = getIno((n * 2 + 2) - 1) - off;

		byte[] b = new byte[len];
		System.arraycopy(m_pag, off, b, 0, len);
		return b;
	}

	public Enumeration<?> keys() {
		return new Enumerator(true);
	}

	public Enumeration<?> elements() {
		return new Enumerator(false);
	}

	private class Enumerator implements Enumeration<Object> {
		boolean key;
		int i = 0;
		byte[] next;

		Enumerator(boolean key) {
			this.key = key;
			next = getNext();
		}

		public boolean hasMoreElements() {
			return next != null;
		}

		private byte[] getNext() {
			byte[] b = null;
			if (i < size()) {
				b = key ? getKeyAt(i) : getElementAt(i);
				i++;
			}
			return b;
		}

		public Object nextElement() {
			byte[] b = next;
			if (b == null) {
				throw new NoSuchElementException("Enumerator");
			}
			next = getNext();
			return b;
		}
	}

	public byte[] remove(byte[] key) {
		int n;
		int i;

		if ((n = getIno(0)) == 0) {
			return null;
		}
		if ((i = indexOfValue(key)) == -1) {
			return null;
		}

		byte[] val = new byte[getIno(i) - getIno(i + 1)];
		System.arraycopy(m_pag, getIno(i + 1), val, 0, getIno(i)
				- getIno(i + 1));
		// found the key. if it is the last entry
		// [i.e. i == n - 1] we just adjust the entry count.
		// hard case: move all data down onto the deleted pair,
		// shift offsets onto deleted offsets, and adjust them.
		// [note: 0 < i < n]
		if (i < n - 1) {
			int m;
			int dst = (i == 1 ? m_pageSize : getIno(i - 1));
			int src = getIno(i + 1);
			int zoo = dst - src;

			// System.out.println("free-up "+zoo);

			// shift data/keys down
			m = getIno(i + 1) - getIno(n);
			System.arraycopy(m_pag, src - m, m_pag, dst - m, m);

			// adjust offset index up
			while (i < n - 1) {
				setIno(i, (short) (getIno(i + 2) + zoo));
				i++;
			}
		}
		setIno(0, (short) (getIno(0) - 2));
		return val;
	}

	/**
	 * @param key
	 *            The key to check for existance
	 * 
	 * @return true if the page contains the key
	 */
	public boolean containsKey(byte[] key) {
		return indexOfValue(key) != -1;
	}

	/**
	 * search for the key in the page. return offset index in the range 0 < i <
	 * n. return -1 if not found.
	 */
	public int indexOfValue(byte[] key) {
		int n;
		int i;
		int off = m_pageSize;
		int siz = key.length;

		if ((n = getIno(0)) == 0) {
			return -1;
		}

		// System.out.println("Key:"+key);
		// print();
		for (i = 1; i < n; i += 2) {
			// System.out.println("siz:"+new Integer(siz));
			// System.out.println("off-ino:"+new Integer(off-getIno(i)));
			// if (siz == off - getIno(i)) {
			// System.out.println("key?:"+new String(m_pag).
			// substring(getIno(i),getIno(i)+siz).equals(key));
			// }
			if (siz == off - getIno(i)
					&& byteArraysEqual(m_pag, getIno(i), key, 0, siz)) {
				return i;
			}
			off = getIno(i + 1);
		}
		return -1;
	}

	/**
	 * @return true if the page is empty.
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * @return the number of key/value pairs in this page
	 */
	public int size() {
		return getIno(0) / 2;
	}

	/**
	 * Fast function to compare two byte arrays, starts from back because my
	 * strings are dissimilar from the end
	 */
	private static boolean byteArraysEqual(byte[] arr1, int start1,
			byte[] arr2, int start2, int len) {
		for (int i = len - 1; i >= 0; i--) {
			if (arr1[start1 + i] != arr2[start2 + i]) {
				return false;
			}
		}
		return true;
	}

	public static void clearByteArray(byte[] arr) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = 0;
		}
	}

	public void split(SdbmPage newPage, int sbit) {
		byte[] key;
		byte[] val;

		int n;
		int off = m_pageSize;
		SdbmPage cur = new SdbmPage(m_pageSize);

		System.arraycopy(m_pag, 0, cur.m_pag, 0, m_pageSize);
		clearByteArray(m_pag);
		clearByteArray(newPage.m_pag);

		n = cur.getIno(0);
		for (int i = 1; n > 0; i += 2) {
			key = new byte[off - cur.getIno(i)];
			System.arraycopy(cur.m_pag, cur.getIno(i), key, 0, off
					- cur.getIno(i));
			val = new byte[cur.getIno(i) - cur.getIno(i + 1)];
			System.arraycopy(cur.m_pag, cur.getIno(i + 1), val, 0, cur
					.getIno(i)
					- cur.getIno(i + 1));

			// System.out.println("Hash:"+Hash.hash(key));
			// select the page pointer (by looking at sbit) and insert.
			SdbmPage p = (SdbmHash.hash(key) & sbit) != 0 ? newPage : this;
			p.put(key, val);

			off = cur.getIno(i + 1);
			n -= 2;
		}

		// System.out.println((short) cur.getIno(0)/2+" split "+
		// (short) newPage.getIno(0)/2+"/"+(short) getIno(0)/2);
	}

	/**
	 * check page sanity: number of entries should be something reasonable, and
	 * all offsets in the index should be in order. this could be made more
	 * rigorous.
	 */
	public boolean isValid() {
		int n;
		int off;

		if ((n = getIno(0)) < 0 || n > m_pageSize / StorageHashTable.SHORTSIZ)
			return false;

		if (n > 0) {
			off = m_pageSize;
			for (int i = 1; n > 0; i += 2) {
				if (getIno(i) > off || getIno(i + 1) > off
						|| getIno(i + 1) > getIno(i))
					return false;
				off = getIno(i + 1);
				n -= 2;
			}
		}
		return true;
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof SdbmPage)) {
			return false;
		}
		SdbmPage other = (SdbmPage) obj;
		return other.m_pag.length == m_pageSize
				&& byteArraysEqual(other.m_pag, 0, m_pag, 0, m_pageSize);
	}

	public void print() {
		int n = getIno(0);
		System.out.println("Num of Elements :" + n);
		for (int i = 1; i <= n; i++) {
			System.out.println("["
					+ i
					+ "] -> "
					+ getIno(i)
					+ " : "
					+ (getIno(i) == 0 ? "" : new String(m_pag, getIno(i),
							(i == 1) ? m_pageSize - getIno(i) : getIno(i - 1)
									- getIno(i))));
		}
	}
}
