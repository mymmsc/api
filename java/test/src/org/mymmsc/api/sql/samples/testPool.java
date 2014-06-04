/**
 * @(#)testPool.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.sql.samples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.mymmsc.api.assembly.Api;
import org.mymmsc.api.sql.ConnectionFactory;
import org.mymmsc.api.sql.ConnectionParam;
import org.mymmsc.api.sql.FactoryParam;
import org.mymmsc.api.sql.SQLApi;

public class testPool {
	
	@SuppressWarnings("unused")
	public void test1() {
		String user = "labs";
		String password = "123456";
		String url = "jdbc:mysql://192.168.1.200:18036/mysql?&useUnicode=true&characterEncoding=gbk&autoReconnect=true&failOverReadOnly=false";
		String driver = "com.mysql.jdbc.Driver";

		ConnectionParam param = new ConnectionParam(driver, url, user, password);
		ConnectionFactory cf = null;
		Integer a = new Integer(0);
		// new ConnectionFactory(param,new FactoryParam());
		try {
			cf = new ConnectionFactory(param, new FactoryParam());
			Connection conn1 = cf.getFreeConnection();
			Connection conn2 = cf.getFreeConnection();
			Connection conn3 = cf.getFreeConnection();
			int c = SQLApi.getRow(conn1, Integer.class, "select count(*) from user");
			int c1 = (int)c;
			TestObj d = SQLApi.getRow(conn1, TestObj.class, "select count(*) as count from user");
			Statement stmt = conn1.createStatement();
			ResultSet rs = stmt.executeQuery("select * from ADMINISTRATION");
			if (rs.next()) {
				System.out.println("conn1 y");
			} else {
				System.out.println("conn1 n");
			}
			stmt.close();
			conn1.close();
			Connection conn4 = cf.getFreeConnection();
			Connection conn5 = cf.getFreeConnection();

			stmt = conn5.createStatement();
			rs = stmt.executeQuery("select * from ADMINISTRATION");
			if (rs.next()) {
				System.out.println("conn5 y");
			} else {
				System.out.println("conn5 n");
			}

			conn2.close();
			conn3.close();
			conn4.close();
			conn5.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			try {
				cf.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		testPool tp = new testPool();
		tp.test1();
		System.out.println(Api.md5("0eTong/EMPP1.3.3"));
		String user = "labs";
		String password = "123456";
		String url = "jdbc:mysql://192.168.1.200:180366/mysql";
		String driver = "com.mysql.jdbc.Driver";
		ConnectionParam param = new ConnectionParam(driver, url, user, password);
		System.out.println("-------" + param.getDriver());
		ConnectionFactory cf = null;
		try {
			cf = new ConnectionFactory(param, new FactoryParam());
			Connection conn1 = null;
			long time = System.currentTimeMillis();
			for (int i = 0; i < 1000; i++) {
				conn1 = cf.getFreeConnection();
				Statement stmt = conn1.createStatement();
				ResultSet rs = stmt.executeQuery("select * from sys_admin");
				if (rs.next()) {
					System.out.println("conn1 y");
				} else {
					System.out.println("conn1 n");
				}
				conn1.close();
			}
			System.out.println("pool:" + (System.currentTimeMillis() - time));
			time = System.currentTimeMillis();
			Class.forName(param.getDriver()).newInstance();
			for (int i = 0; i < 10; i++) {
				conn1 = DriverManager.getConnection(param.getUrl(), param
						.getUser(), param.getPassword());
				Statement stmt = conn1.createStatement();
				ResultSet rs = stmt.executeQuery("select * from sys_admin");
				if (rs.next()) {
					System.out.println("conn1 y");
					System.out.println(rs.getString(1));
					System.out.println(rs.getString(2));
				} else {
					System.out.println("conn1 n");
				}
				conn1.close();
			}
			System.out
					.println("no pool:" + (System.currentTimeMillis() - time));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				cf.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
