/**
 * 
 */
package org.mymmsc.api.test;

import javapns.back.PushNotificationManager;
import javapns.back.SSLConnectionHelper;
import javapns.data.Device;
import javapns.data.PayLoad;

/**
 * @author wangfeng
 * 
 */
public class TestSSL {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String deviceToken1 = "f7f89a999f9f4eb943ddf17c5f1635a7d1bd30c4ae2fed88ef821fb2c0c24985";// iphone手机获取的token
			String deviceToken2 = "187f18de42255dec804d229c378ce9840afe6d817ab835a1f55786355e784448";
			PayLoad payLoad = new PayLoad();
			payLoad.addAlert("墨迹，扣你工资～～");// push的内容
			payLoad.addBadge(1);// 图标小红圈的数值
			payLoad.addSound("default");// 铃音

			PushNotificationManager pushManager = PushNotificationManager
					.getInstance();
			pushManager.addDevice("iPhone1", deviceToken1);
			pushManager.addDevice("iPhone2", deviceToken2);

			// Connect to APNs
			/************************************************
			 * 测试的服务器地址：gateway.sandbox.push.apple.com /端口2195
			 * 产品推送服务器地址：gateway.push.apple.com / 2195
			 ***************************************************/
			String host = "gateway.sandbox.push.apple.com";
			int port = 2195;
			String certificatePath = "/Users/wangfeng/projects/ifengzi/release/iOS/Certificates.p12";// 导出的证书
			String certificatePassword = "123456";// 此处注意导出的证书密码不能为空因为空密码会报错
			pushManager.initializeConnection(host, port, certificatePath,
					certificatePassword,
					SSLConnectionHelper.KEYSTORE_TYPE_PKCS12);

			// Send Push
			Device client = pushManager.getDevice("iPhone1");
			pushManager.sendNotification(client, payLoad);
			client = pushManager.getDevice("iPhone2");
			pushManager.sendNotification(client, payLoad);
			pushManager.stopConnection();

			pushManager.removeDevice("iPhone");
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("OK");
	}

}
