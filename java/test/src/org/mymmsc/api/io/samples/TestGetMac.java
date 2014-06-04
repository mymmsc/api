package org.mymmsc.api.io.samples;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 获取主机mac地址
 * @author WangFeng
 *
 */
public class TestGetMac {

	public static void main(String[] args) {
		try {
			// 枚举所有网络接口设备
			Enumeration<?> interfaces = NetworkInterface.getNetworkInterfaces();
			// 循环处理每一个网络接口设备
			while (interfaces.hasMoreElements()) {
				NetworkInterface face = (NetworkInterface) interfaces
						.nextElement();
				// 环回设备（lo）设备不处理
				if (face.isUp() && !face.getName().equals("lo")) {
					// 获取硬件地址
					byte[] mac = face.getHardwareAddress();
					if (mac != null && mac.length == 6) {
						System.out.println(" 硬件地址（MAC）:" + bytes2mac(mac));
						// 显示当前网络接口设备显示名称
						System.out.println("网卡显示名称:" + face.getDisplayName());
						// 显示当前设备内部名称
						//System.out.println(" 网卡设备名称:" + face.getName());
						Enumeration<InetAddress> ips = face.getInetAddresses();
						if (ips.hasMoreElements()) {
							InetAddress id = ips.nextElement();
							System.out.println("网络地址: " + id.getHostAddress());
						}
					}
				}
			}
		} catch (SocketException se) {
			System.err.println("错误：" + se.getMessage());
		}
	}

	private static String bytes2mac(byte[] bytes) {
		// MAC 地址应为6 字节
		if (bytes == null || bytes.length != 6) {
			return null;
		}
		StringBuffer macString = new StringBuffer();
		byte currentByte;
		for (int i = 0; i < bytes.length; i++) {
			// 与11110000 作按位与运算以便读取当前字节高4 位
			currentByte = (byte) ((bytes[i] & 240) >> 4);
			macString.append(Integer.toHexString(currentByte));
			// 与00001111 作按位与运算以便读取当前字节低4 位
			currentByte = (byte) ((bytes[i] & 15));
			macString.append(Integer.toHexString(currentByte));
			// 追加字节分隔符“-”
			macString.append("-");
		}
		// 删除多加的一个“-”
		macString.delete(macString.length() - 1, macString.length());
		// 统一转换成大写形式后返回
		return macString.toString().toUpperCase();
	}
}
