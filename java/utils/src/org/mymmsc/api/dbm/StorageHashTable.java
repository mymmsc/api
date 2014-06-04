/**
 * @(#)StorageHashTable.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.dbm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import org.mymmsc.api.Environment;
import org.mymmsc.api.algorithms.InterfaceHashTable;

/**
 * Java rewrite of sdbm. sdbm - ndbm work-alike hashed database library based on
 * Per-Aake Larson's Dynamic Hashing algorithms. BIT 18 (1978). original author:
 * oz@nexus.yorku.ca status: public domain.
 * 
 * @author Justin F. Chapweske <justin@chapweske.com>
 * @version .01 06/06/98
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public class StorageHashTable implements InterfaceHashTable {

	public static final int DBLKSIZ = 4096;
	public static final int PBLKSIZ = 1024;
	public static final int PAIRMAX = 1008; // arbitrary on PBLKSIZ-N
	public static final int SPLTMAX = 10; // maximum allowed splits

	public static final int SHORTSIZ = 2;
	public static final int BITSINBYTE = 8;

	public static final String DIREXT = ".dbdir";
	public static final String PAGEXT = ".dbpag";

	private String m_databaseHome = null;
	private File m_basedir = null;
	private RandomAccessFile m_dirRaf; // directory file descriptor
	private RandomAccessFile m_pagRaf; // page file descriptor
	private File m_dirFile, m_pagFile;
	private String m_mode;
	private int m_maxbno; // size of dirfile in bits
	private int m_curbit; // current bit number
	private int m_hmask; // current hash mask
	private SdbmPage m_sdbmPage; // page file block buffer
	private int m_dirbno; // current block in m_dirbuf
	private byte[] m_dirbuf; // directory file block buffer
	private int m_elementCount; // The number of elements.

	/**
	 * 构造方法
	 * 
	 * @param databaseHome
	 *            数据库主目录
	 * @throws IOException
	 */
	public StorageHashTable(String databaseHome) throws IOException {
		boolean bRet = false;
		m_databaseHome = databaseHome;
		if (m_databaseHome == null) {
			m_databaseHome = Environment.getTempPath();
		}
		m_basedir = new File(m_databaseHome);
		if (!m_basedir.isDirectory()) {
			bRet = m_basedir.mkdirs();
			if (!bRet) {
				System.out.println("目录创建失败!");
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.mymmsc.api.algorithms.InterfaceHashTable#open(java.lang.String)
	 */
	public boolean open(String dbname) throws IOException {
		boolean bRet = false;
		m_mode = "rw";
		m_dirFile = new File(m_basedir, dbname + DIREXT);
		m_pagFile = new File(m_basedir, dbname + PAGEXT);
		if (m_dirFile != null && m_pagFile != null) {
			m_dirRaf = new RandomAccessFile(m_dirFile, m_mode);
			m_pagRaf = new RandomAccessFile(m_pagFile, m_mode);
			m_dirbuf = new byte[DBLKSIZ];
			if (m_dirRaf != null && m_pagRaf != null) {
				// need the dirfile size to establish max bit number.
				// zero size: either a fresh database, or one with a single,
				// unsplit data page: dirpage is all zeros.
				m_dirbno = m_dirRaf.length() == 0 ? 0 : -1;
				m_maxbno = (int) m_dirRaf.length() * BITSINBYTE;
				// System.out.println("MAXBNO:"+m_maxbno);
				// System.out.println("BITSINBYTE:"+BITSINBYTE);
				// System.out.println("size:"+m_dirRaf.length());

				for (Enumeration<?> en = pages(); en.hasMoreElements();) {
					SdbmPage p = (SdbmPage) en.nextElement();
					if (m_sdbmPage == null) {
						m_sdbmPage = p;
					}
					m_elementCount += p.size();
				}
				bRet = true;
			}
		}
		return bRet;
	}

	private static final void checkKey(String key) {
		if (key == null) {
			throw new NullPointerException();
		} else if (key.length() <= 0) {
			throw new IllegalArgumentException("key too small: " + key.length());
		}
	}

	private static final int OFF_PAG(int off) {
		return off * PBLKSIZ;
	}

	private static final int OFF_DIR(int off) {
		return off * DBLKSIZ;
	}

	private static final int masks[] = { 000000000000, 000000000001,
			000000000003, 000000000007, 000000000017, 000000000037,
			000000000077, 000000000177, 000000000377, 000000000777,
			000000001777, 000000003777, 000000007777, 000000017777,
			000000037777, 000000077777, 000000177777, 000000377777,
			000000777777, 000001777777, 000003777777, 000007777777,
			000017777777, 000037777777, 000077777777, 000177777777,
			000377777777, 000777777777, 001777777777, 003777777777,
			007777777777, 017777777777 };

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		System.out.println("内存数据库已经自动回收.");
		close();
	}

	/**
	 * Close the database.
	 */
	public synchronized void close() throws IOException {
		m_dirRaf.close();
		m_pagRaf.close();
	}

	@Override
	public synchronized Object get(String key, Class<? extends Object> cls)
			throws IOException {
		Object obj = null;
		checkKey(key);
		byte[] keyBytes = key.getBytes();

		// System.out.println(key);
		m_sdbmPage = getPage(SdbmHash.hash(keyBytes));
		// System.out.println(page.bno);
		// page.print();
		if (m_sdbmPage == null) {
			return null;
		}
		byte[] b = m_sdbmPage.get(keyBytes);
		if (b == null) {
			return null;
		} else {
			ByteArrayInputStream bais = null;
			ObjectInputStream ois = null;
			try {
				bais = new ByteArrayInputStream(b);
				ois = new ObjectInputStream(bais);
				obj = ois.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				// System.out.println(b.toString());
				if (bais != null) {
					bais.close();
				}
				if (ois != null) {
					ois.close();
				}
			}
		}
		return obj;
	}

	/**
	 * @param key
	 *            the key to check.
	 * 
	 * @return true if the dbm contains the key
	 */
	public synchronized boolean containsKey(String key) throws IOException {
		checkKey(key);

		byte[] keyBytes = key.getBytes();
		m_sdbmPage = getPage(SdbmHash.hash(keyBytes));

		if (m_sdbmPage == null) {
			return false;
		}
		return m_sdbmPage.containsKey(keyBytes);
	}

	/**
	 * Clear the database of all entries.
	 */
	public synchronized void clear() throws IOException {
		if (!m_mode.startsWith("rw")) {
			throw new IOException("This file is opened Read only");
		}

		m_dirRaf.close();
		m_pagRaf.close();

		try {
			if (!m_dirFile.delete()) {
				throw new IOException("Unable to delete :" + m_dirFile);
			}
		} finally {
			if (!m_pagFile.delete()) {
				throw new IOException("Unable to delete :" + m_pagFile);
			}
		}

		m_dirRaf = new RandomAccessFile(m_dirFile, m_mode);
		m_pagRaf = new RandomAccessFile(m_pagFile, m_mode);
		// zero the m_dirbuf
		m_dirbuf = new byte[DBLKSIZ];

		m_curbit = 0;
		m_hmask = 0;
		m_sdbmPage = null;

		// need the dirfile size to establish max bit number.
		// zero size: either a fresh database, or one with a single,
		// unsplit data page: dirpage is all zeros.
		m_dirbno = m_dirRaf.length() == 0 ? 0 : -1;
		m_maxbno = (int) m_dirRaf.length() * BITSINBYTE;
		m_elementCount = 0;
	}

	/**
	 * removes the value associated with the key
	 * 
	 * @returns the removed value, null if it didn't exist.
	 */
	public synchronized boolean remove(String key) throws IOException {
		checkKey(key);

		byte[] keyBytes = key.getBytes();
		m_sdbmPage = getPage(SdbmHash.hash(keyBytes));
		if (m_sdbmPage == null) {
			return false;
		}

		int n = m_sdbmPage.size();
		byte[] removeBytes = m_sdbmPage.remove(keyBytes);
		if (removeBytes != null) {
			//
		}
		if (m_sdbmPage.size() < n) {
			m_elementCount--;
		}

		// update the page file
		m_pagRaf.seek(OFF_PAG(m_sdbmPage.m_bno));
		m_pagRaf.write(m_sdbmPage.m_pag, 0, PBLKSIZ);

		return true;
	}

	/**
	 * @returns true if it is empty, false otherwise.
	 */
	public synchronized boolean isEmpty() {
		return m_elementCount <= 0;
	}

	/**
	 * @returns the number of elements in the database.
	 */
	public synchronized int size() {
		return m_elementCount;
	}

	/**
	 * makroom - make room by splitting the overfull page this routine will
	 * attempt to make room for SPLTMAX times before giving up.
	 */
	private synchronized void makeRoom(int hash, int need) throws IOException,
			DbmException {

		SdbmPage newPage;

		int smax = SPLTMAX;
		do {

			// Very important, don't want to write over newPage on loop.
			newPage = new SdbmPage(PBLKSIZ);
			// split the current page
			m_sdbmPage.split(newPage, m_hmask + 1);

			// address of the new page
			newPage.m_bno = (hash & m_hmask) | (m_hmask + 1);

			// write delay, read avoidence/cache shuffle:
			// select the page for incoming pair: if key is to go to the new
			// page, write out the previous one, and copy the new one over,
			// thus making it the current page. If not, simply write the new
			// page, and we are still looking at the page of interest. current
			// page is not updated here, as put will do so, after it inserts
			// the incoming pair.
			if ((hash & (m_hmask + 1)) != 0) {
				m_pagRaf.seek(OFF_PAG(m_sdbmPage.m_bno));
				m_pagRaf.write(m_sdbmPage.m_pag, 0, PBLKSIZ);
				m_sdbmPage = newPage;
			} else {
				m_pagRaf.seek(OFF_PAG(newPage.m_bno));
				m_pagRaf.write(newPage.m_pag, 0, PBLKSIZ);
			}

			setdbit(m_curbit);

			// see if we have enough room now
			if (m_sdbmPage.hasRoom(need)) {
				return;
			}
			// try again... update m_curbit and m_hmask as getpage would have
			// done. because of our update of the current page, we do not
			// need to read in anything. BUT we have to write the current
			// [deferred] page out, as the window of failure is too great.
			m_curbit = 2 * m_curbit + ((hash & (m_hmask + 1)) != 0 ? 2 : 1);
			m_hmask |= m_hmask + 1;

			m_pagRaf.seek(OFF_PAG(m_sdbmPage.m_bno));
			m_pagRaf.write(m_sdbmPage.m_pag, 0, PBLKSIZ);

		} while (--smax != 0);

		// if we are here, this is real bad news. After SPLTMAX splits,
		// we still cannot fit the key. say goodnight.
		throw new DbmException("AIEEEE! Cannot insert after SPLTMAX attempts");
	}

	/**
	 * returns an enumeration of the pages in the database.
	 */
	private Enumeration<?> pages() {
		return new PageEnumerator();
	}

	private class PageEnumerator implements Enumeration<Object> {
		private int blkptr;

		PageEnumerator() {
			//
		}

		public boolean hasMoreElements() {
			synchronized (StorageHashTable.this) {
				// If we're at the end of the file.
				try {
					if (OFF_PAG(blkptr) >= m_pagRaf.length()) {
						return false;
					}

				} catch (IOException e) {
					return false;
				}
				return true;
			}
		}

		public Object nextElement() {
			synchronized (StorageHashTable.this) {
				if (!hasMoreElements()) {
					throw new NoSuchElementException("PageEnumerator");
				}
				SdbmPage p = new SdbmPage(PBLKSIZ);
				if (m_sdbmPage == null || m_sdbmPage.m_bno != blkptr) {
					try {
						m_pagRaf.seek(OFF_PAG(blkptr));
						readLots(m_pagRaf, p.m_pag, 0, PBLKSIZ);
					} catch (IOException e) {
						throw new NoSuchElementException(e.getMessage());
					}
				} else {
					p = m_sdbmPage;
				}

				if (!p.isValid() || p == null)
					throw new NoSuchElementException("PageEnumerator");
				blkptr++;
				return p;
			}
		}
	}

	/**
	 * returns an enumeration of the keys in the database.
	 */
	public synchronized Enumeration<?> keys() {
		return new Enumerator(true);
	}

	/**
	 * returns an enumeration of the elements in the database.
	 */
	public synchronized Enumeration<?> elements() {
		return new Enumerator(false);
	}

	private class Enumerator implements Enumeration<Object> {
		boolean key;
		Enumeration<?> penum;
		SdbmPage p;
		Enumeration<?> enum_v;
		String next;

		Enumerator(boolean key) {
			this.key = key;
			penum = pages();
			if (penum.hasMoreElements()) {
				p = (SdbmPage) penum.nextElement();
				enum_v = key ? p.keys() : p.elements();
				next = getNext();
			} else {
				next = null;
			}
		}

		public boolean hasMoreElements() {
			synchronized (StorageHashTable.this) {
				return next != null;
			}
		}

		private String getNext() {
			for (;;) {
				if (!(penum.hasMoreElements() || enum_v.hasMoreElements())) {
					return null;
				}
				if (enum_v.hasMoreElements()) {
					byte[] b = (byte[]) enum_v.nextElement();
					if (b != null) {
						return new String(b);
					}
				} else if (penum.hasMoreElements()) {
					p = (SdbmPage) penum.nextElement();
					enum_v = key ? p.keys() : p.elements();
				}
			}
		}

		public Object nextElement() {
			synchronized (StorageHashTable.this) {
				String s = next;
				if (s == null) {
					throw new NoSuchElementException("Enumerator");
				}
				next = getNext();
				return s;
			}
		}
	}

	/**
	 * all important binary tree traversal
	 */
	protected synchronized SdbmPage getPage(int hash) throws IOException {
		int hbit = 0;
		int dbit = 0;
		int pagb;
		SdbmPage newPage;
		// System.out.println("m_maxbno:"+m_maxbno);
		// System.out.println("hash:"+hash);

		while (dbit < m_maxbno && getdbit(dbit) != 0) {
			dbit = 2 * dbit + ((hash & (1 << hbit++)) != 0 ? 2 : 1);
		}

		// System.out.println("dbit: "+dbit+"...");

		m_curbit = dbit;
		m_hmask = masks[hbit];

		pagb = hash & m_hmask;

		// System.out.println("pagb: "+pagb);
		// see if the block we need is already in memory.
		// note: this lookaside cache has about 10% hit rate.
		if (m_sdbmPage == null || pagb != m_sdbmPage.m_bno) {

			// note: here, we assume a "hole" is read as 0s.
			// if not, must zero pagbuf first.
			m_pagRaf.seek(OFF_PAG(pagb));
			byte[] b = new byte[PBLKSIZ];
			readLots(m_pagRaf, b, 0, PBLKSIZ);
			newPage = new SdbmPage(b);

			if (!newPage.isValid()) {
				// FIX maybe there is a better way to deal with corruption?
				// Corrupt page, return an empty one.
				b = new byte[PBLKSIZ];
				newPage = new SdbmPage(b);
			}

			newPage.m_bno = pagb;

			// System.out.println("m_pag read: "+pagb);
		} else {
			newPage = m_sdbmPage;
		}

		return newPage;
	}

	protected synchronized int getdbit(int dbit) throws IOException {
		int c;
		int dirb;

		c = dbit / BITSINBYTE;
		dirb = c / DBLKSIZ;

		if (dirb != m_dirbno) {
			m_dirRaf.seek(OFF_DIR(dirb));
			readLots(m_dirRaf, m_dirbuf, 0, DBLKSIZ);

			m_dirbno = dirb;

			// System.out.println("dir read: "+dirb);
		}

		return m_dirbuf[c % DBLKSIZ] & (1 << dbit % BITSINBYTE);
	}

	protected synchronized void setdbit(int dbit) throws IOException {
		int c = dbit / BITSINBYTE;
		int dirb = c / DBLKSIZ;

		if (dirb != m_dirbno) {
			clearByteArray(m_dirbuf);
			m_dirRaf.seek(OFF_DIR(dirb));
			readLots(m_dirRaf, m_dirbuf, 0, DBLKSIZ);

			m_dirbno = dirb;

			// System.out.println("dir read: "+dirb);
		}

		m_dirbuf[c % DBLKSIZ] |= (1 << dbit % BITSINBYTE);

		if (dbit >= m_maxbno) {
			m_maxbno += DBLKSIZ * BITSINBYTE;
		}
		m_dirRaf.seek(OFF_DIR(dirb));
		m_dirRaf.write(m_dirbuf, 0, DBLKSIZ);

	}

	public static void clearByteArray(byte[] arr) {
		for (int i = 0; i < arr.length; i++) {
			arr[i] = 0;
		}
	}

	public static void readLots(RandomAccessFile f, byte[] b, int off, int len)
			throws IOException {
		int n = 0;
		while (n < len) {
			int count = f.read(b, off + n, len - n);
			n += count;
			if (count < 0) {
				break;
			}
		}
	}

	private byte[] getBytes(Object obj) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(obj);
		byte[] data = baos.toByteArray();
		baos.close();
		oos.close();
		return data;
	}

	public synchronized boolean set(String key, Object obj) throws IOException {
		byte[] value = getBytes(obj);
		checkKey(key);

		byte[] keyBytes = key.getBytes();

		int need = key.length() + value.length;
		// is the pair too big for this database ??
		if (need > PAIRMAX) {
			throw new DbmException("Pair is too big for this database");
		}

		int hash = SdbmHash.hash(keyBytes);

		m_sdbmPage = getPage(hash);

		// if we need to replace, delete the key/data pair
		// first. If it is not there, ignore.
		int n = m_sdbmPage.size();
		byte[] valBytes = m_sdbmPage.remove(keyBytes);
		@SuppressWarnings("unused")
		String val = null;
		if (valBytes != null) {
			val = new String(valBytes);
		}

		if (m_sdbmPage.size() < n) {
			m_elementCount--;
		}

		// if we do not have enough room, we have to split.
		if (!m_sdbmPage.hasRoom(need)) {
			makeRoom(hash, need);
		}

		// we have enough room or split is successful. insert the key,
		// and update the page file.

		m_sdbmPage.put(keyBytes, value);

		m_elementCount++;
		// page.print();

		m_pagRaf.seek(OFF_PAG(m_sdbmPage.m_bno));
		m_pagRaf.write(m_sdbmPage.m_pag, 0, PBLKSIZ);

		return true;
	}

	/**
	 * puts the value into the database using key as its key.
	 * 
	 * @param key
	 * @param value
	 * @return
	 * @throws IOException
	 * @throws DbmException
	 */
	public synchronized String set(String key, byte[] value)
			throws IOException, DbmException {

		checkKey(key);

		byte[] keyBytes = key.getBytes();

		int need = key.length() + value.length;
		// is the pair too big for this database ??
		if (need > PAIRMAX) {
			throw new DbmException("Pair is too big for this database");
		}

		int hash = SdbmHash.hash(keyBytes);

		m_sdbmPage = getPage(hash);

		// if we need to replace, delete the key/data pair
		// first. If it is not there, ignore.
		int n = m_sdbmPage.size();
		byte[] valBytes = m_sdbmPage.remove(keyBytes);
		String val = null;
		if (valBytes != null) {
			val = new String(valBytes);
		}

		if (m_sdbmPage.size() < n) {
			m_elementCount--;
		}

		// if we do not have enough room, we have to split.
		if (!m_sdbmPage.hasRoom(need)) {
			makeRoom(hash, need);
		}

		// we have enough room or split is successful. insert the key,
		// and update the page file.

		m_sdbmPage.put(keyBytes, value);

		m_elementCount++;
		// page.print();

		m_pagRaf.seek(OFF_PAG(m_sdbmPage.m_bno));
		m_pagRaf.write(m_sdbmPage.m_pag, 0, PBLKSIZ);

		return val;
	}

	/**
	 * 模板函数(保留)
	 * @param <T>
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public <T extends Object> T get(String key, T defaultValue) {
		return defaultValue;
	}

	public void print() throws IOException {
		System.out.print("[");
		for (Enumeration<?> en = keys(); en.hasMoreElements();) {
			String key = (String) en.nextElement();
			System.out.print(key + "=" + get(key, null));
			if (en.hasMoreElements()) {
				System.out.print(",");
			}
		}
		System.out.println("]");
	}

}
