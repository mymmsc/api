/**
 * @(#)DbmTest2.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.samples.test;

import junit.framework.TestCase;

/**
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-test 6.3.9
 */
public class DbmTest2 extends TestCase {
	private StorageHashTableTest shtt = new StorageHashTableTest("2", 10000);

	public DbmTest2(String name) {
		super(name);
	}

	public void testIt() throws Exception {
		shtt.testIt();
	}
}
