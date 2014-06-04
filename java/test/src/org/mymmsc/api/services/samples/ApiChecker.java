/**
 * 
 */
package org.mymmsc.api.services.samples;

import org.mymmsc.api.assembly.Api;


/**
 * @author WangFeng
 * 
 */
public class ApiChecker extends Thread {
	private long m_class_timestamp = 0;
	TestApiMainThread main = null;

	/**
	 * 
	 */
	public ApiChecker() {
		//
	}

	public void setMain(TestApiMainThread main) {
		this.main = main;

	}

	private boolean check() {
		boolean bRet = false;
		long t = Api
				.getLastModified("E:/projects/mymmsc/api/libjava/api/bin/org/mymmsc/api/services/samples/TestApi.class");
		if (m_class_timestamp == 0) {
			m_class_timestamp = t;
		} else if (m_class_timestamp != t) {
			m_class_timestamp = t;
			bRet = true;
		}
		return bRet;
	}

	public void run() {
		while (!interrupted()) {
			try {
				boolean isChanged = check();
				if (isChanged) {
					main.notifyReLoad();
				} else {
					Thread.sleep(1000 * 50);

				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
