/**
 * @(#)TestJsonAdapter.java	6.3.12 2012/05/11
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.context.samples;

import org.mymmsc.api.context.JsonAdapter;

/**
 * 测试JSON解析器
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.12 2012/05/11
 * @since mymmsc-api 6.3.12
 * @since mymmsc-api 6.3.9
 */
public class TestJsonAdapter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Response<Test> data = new Response<Test>();
		Test t1 = new Test();
		t1.setAmount("1");
		t1.setBillId("ahkjsdhas");
		data.setData(t1);
		String string = null;
		string = JsonAdapter.get(data);
		
		JsonAdapter json = JsonAdapter.parse(string);
		if (json != null) {
			Response<Test> data1 = json.get(Response.class, Test.class);
			System.out.println("还原：" + string);
			System.out.println("还原：" + JsonAdapter.get(data1));
		}
		string = "{\"order\":{\"id\":123,\"name\":\"bichao\"},\"errNo\":900,\"message\":\"success\",\"bills\":[{\"amount\":\"11\",\"test\":{\"amount\":\"12\",\"billId\":\"billid2\"},\"billId\":\"billid1\"},{\"amount\":\"13\",\"billId\":\"billid3\"}]}";
		string = "{\"order\":{\"id\":123,\"name\":\"bichao\"},\"errNo\":900,\"bills\":[{\"amount\":\"11\",\"test\":{\"amount\":\"12\",\"billId\":\"billid2\"},\"billId\":\"billid1\"},{\"amount\":\"13\",\"billId\":\"billid3\"}]}";
		//String string = "{\"bills\":[{\"amount\":\"11\",\"test\":{\"amount\":\"12\",\"billId\":\"billid2\"},\"billId\":\"billid1\"},{\"amount\":\"13\",\"billId\":\"billid3\"}]}";
		System.out.println("第1次 ===>");
		System.out.println("       原json串: " + string);
		// 解析JSON串, 有可能因解析失败返回null
		JsonAdapter parser = JsonAdapter.parse(string);
		if (parser != null) {
			TObject obj = parser.get(TObject.class);
			//System.out.println(Api.getClass(obj, "bills"));
			System.out.println("       输出bean: "  + obj);
			string = JsonAdapter.get(obj, true);
			System.out.println("输出bean的JSON串: "  + string);
			// 最后释放资源
			parser.close();
			System.out.println("第2次 ===>");
			parser = JsonAdapter.parse(string);
			obj = parser.get(TObject.class);
			//System.out.println(Api.getClass(obj, "bills"));
			System.out.println("       输出bean: "  + obj);
			string = JsonAdapter.get(obj, true);
			System.out.println("输出bean的JSON串: "  + string);
			
			Test test = new Test();
			test.setAmount("1");
			test.setBillId("2");
			System.out.println("输出bean的JSON串: "  + JsonAdapter.get(test, false));
		}
	}
}
