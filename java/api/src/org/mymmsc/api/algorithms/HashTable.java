/**
 * @(#)HashTable.java	6.3.9 09/11/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.algorithms;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 哈希表
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 * @param <K>
 * @param <V>
 */
public class HashTable<K, V> extends ConcurrentHashMap<K, V> {
	private static final long serialVersionUID = 8316785322074950971L;

	/**
	 * HashTable构造函数
	 */
	public HashTable() {
		super();
	}

	/**
	 * get
	 * 
	 * @param index
	 *            int
	 * @return Object
	 */
	public V get(int index) {
		V value = null;
		if (index >= 0 || index < size()) {
			Iterator<Entry<K, V>> it = entrySet().iterator();
			for (int i = 0; i < index; i++) {
				it.next();
			}
			Entry<K, V> entry =  it.next();
			value = entry.getValue();
		}
		return value;
	}

	public V getX(K key) {
		V x = (V) get(key);

		return x;
	}

	/**
	 * getX
	 * 
	 * @param hashmap
	 *            ConcurrentHashMap
	 * @param key
	 *            Object
	 * @param defaultT
	 *            T
	 * @return T
	 */
	@SuppressWarnings("unchecked")
	public <T extends Object> T getX(ConcurrentHashMap<?, ?> hashmap, Object key,
			T defaultT) {
		T x = (T) hashmap.get(key);

		if (x == null) {
			x = defaultT;
		}
		return x;
	}

	/**
	 * getString
	 * 
	 * @param pHashmap
	 *            ConcurrentHashMap
	 * @param sKey
	 *            String
	 * @return String
	 */
	public String getString(ConcurrentHashMap<?, ?> pHashmap, Object sKey) {
		String __sValue = (String) pHashmap.get(sKey);
		if (__sValue == null) {
			__sValue = "";
		}
		return (__sValue.trim());
	}

	public String getString(ConcurrentHashMap<?, ?> pHashmap, Object sKey,
			String sDefault) {
		String __sValue = (String) pHashmap.get(sKey);
		if (__sValue == null) {
			__sValue = sDefault;
		}
		return (__sValue.trim());
	}

	/**
	 * getInt
	 * 
	 * @param pHashmap
	 *            ConcurrentHashMap
	 * @param sKey
	 *            String
	 * @return int
	 */
	public int getInt(ConcurrentHashMap<?, ?> pHashmap, Object sKey) {
		String __sValue = getString(pHashmap, sKey);
		if (__sValue.length() == 0) {
			__sValue = "0";
		}
		return (new Integer(__sValue).intValue());
	}

	public int getInt(ConcurrentHashMap<?, ?> pHashmap, Object sKey, int nDefault) {
		String __sValue = getString(pHashmap, sKey);
		if (__sValue.length() == 0) {
			return nDefault;
		}
		return (new Integer(__sValue).intValue());
	}
}
