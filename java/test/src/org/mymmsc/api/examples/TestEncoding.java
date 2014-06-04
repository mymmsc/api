/**
 * 
 */
package org.mymmsc.api.examples;

import org.mymmsc.api.encoding.UTF8;


/**
 * @author wangfeng
 * 
 */
public class TestEncoding {

	public static void main(String[] args) {
		String string = "台湾版啥时候搞,,,关公战秦琼~~";
		String t = UTF8.s2c(string);
		System.out.println("繁体中文:" + t);
		t = UTF8.c2s(t + "， 1～2～3.。。。。");
		System.out.println("简体中文:" + t);
	}
}
