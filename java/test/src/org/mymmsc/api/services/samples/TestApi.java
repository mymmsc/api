package org.mymmsc.api.services.samples;

import org.mymmsc.api.services.AbstractApi;
import org.mymmsc.api.services.InterfaceApi;

public class TestApi extends AbstractApi implements InterfaceApi {

	public TestApi() {
		//
	}

	@Override
	public void close() {
		System.out.println("close TestApi:");
	}

	@Override
	public void doBusiness() {
		System.out.println("hello boydfd ");
	}

	@Override
	public void start() {
		System.out.println("Start TestApi:");
		System.out.println(TestApi.class.getClassLoader());
	}

}
