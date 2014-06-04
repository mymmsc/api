/**
 * 
 */
package org.mymmsc.j2ee;

import org.mymmsc.api.assembly.XmlParser;


/**
 * 适配器
 * 
 * @author WangFeng
 *
 */
public class Adapter {
	private static Adapter instance = null;
	
	/** XML解析器 */
	protected XmlParser xp = null;
	
	/**
	 * 创建一个Adapter实例
	 * @return
	 */
	public synchronized static Adapter newInstance() {
		if (instance == null) {
			instance = new Adapter();
		}
		return instance;
	}
	
	/**
	 * 解析XML文件
	 * @param filepath 文件路径
	 * @return
	 */
	public synchronized boolean loadConfig(String filepath) {
		boolean bRet = false;
		if (xp == null) {
			xp = new XmlParser(filepath, true);
		}
		if (xp != null) {
			bRet = true;
		}
		return bRet;
	}
}
