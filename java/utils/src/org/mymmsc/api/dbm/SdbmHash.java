package org.mymmsc.api.dbm;

/**
 * Java rewrite of sdbm. sdbm - ndbm work-alike hashed database library based on
 * Per-Aake Larson's Dynamic Hashing algorithms. BIT 18 (1978). original author:
 * oz@nexus.yorku.ca status: public domain. keep it that way.
 * 
 * @author Justin Chapweske (justin@chapweske.com)
 * @version .01 06/06/98 hashing routine
 */
public class SdbmHash {

	/**
	 * polynomial conversion ignoring overflows [this seems to work remarkably
	 * well, in fact better then the ndbm hash function. Replace at your own
	 * risk] use: 65599 nice. 65587 even better.
	 */
	public static final int hash(String str) {
		return hash(str.getBytes());
	}

	public static final int hash(byte[] b) {
		int n = 0;
		int len = b.length;

		for (int i = 0; i < len; i++) {
			n = b[i] + 65599 * n;
		}
		return n;
	}
}
