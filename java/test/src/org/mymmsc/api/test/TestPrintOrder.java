/**
 * 
 */
package org.mymmsc.api.test;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WangFeng
 *
 */
public class TestPrintOrder {
	private static String[] CC = {"分","角","圆","拾","佰","仟","万","拾","佰","仟","亿",
			"拾"};
	private static String[] NN = {"零","壹","贰","叁","肆","伍","陆","柒","拐","玖"};
	
	public static String toDX(double amount) {
		long t = (long)(amount * 100);
		System.out.println("t = " + t);
		StringBuffer sb = new StringBuffer();
		String s = String.valueOf(t);
		int len = s.length();
		int i = 0;
		List<String> tmpArray = new ArrayList<String>(); 
		while(t > 0) {
			long a = t - (t/10 * 10);
			s = NN[(int)a] + CC[i++];
			//System.out.println("a = " + s);
			tmpArray.add(s);
			t /= 10;
			//System.out.println("t = " + t);
		}
		for (i = 0; i < len; i++) {
			sb.append(tmpArray.get(len - 1 - i));
		}
		return sb.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		double amount = 123456789.50;
		System.out.println("amount = " + amount);
		
		System.out.println("----------------------------------");
		System.out.println(toDX(amount));
		
		String a = "1\n2\n3\n";
		System.out.println(a.replaceAll("\n", "<br/>"));
	}

}
