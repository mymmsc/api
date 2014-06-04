/**
 * 
 */
package org.mymmsc.api.services.samples;

import org.mymmsc.api.services.ApiClassLoader;
import org.mymmsc.api.services.InterfaceApi;

/**
 * @author WangFeng
 * 
 */
public class TestApiMainThread {
	private InterfaceApi service = null;

	/**
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * 
	 */
	public TestApiMainThread() throws InstantiationException,
			IllegalAccessException {
		ApiClassLoader loader = new ApiClassLoader();
		service = (InterfaceApi) loader.loadFromCustomRepository(
				"org.mymmsc.api.services.samples.TestApi").newInstance();
		service.start();

		while (true) {

			service.doBusiness();
			try {
				Thread.sleep(1000 * 3);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			@SuppressWarnings("unused")
			TestApiMainThread t = new TestApiMainThread();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

	}

	public void notifyReLoad() throws InstantiationException,
			IllegalAccessException {
		service.close();
		ApiClassLoader loader = new ApiClassLoader();
		service = (InterfaceApi) loader.loadFromCustomRepository(
				"org.mymmsc.api.services.samples.TestApi").newInstance();
		service.start();
	}
}
