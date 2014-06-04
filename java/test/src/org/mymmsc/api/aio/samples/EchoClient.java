package org.mymmsc.api.aio.samples;

import java.net.*;
import java.io.*;

public class EchoClient {
	private String remoteHost = "localhost";
	private int remotePort = 17080;
	private DatagramSocket socket;

	public EchoClient() throws IOException {
		socket = new DatagramSocket(); // 与本地的任意一个UDP端口绑定
	}

	public static void main(String args[]) throws IOException {
		new EchoClient().talk();
	}

	public void talk() throws IOException {
		try {
			InetAddress remoteIP = InetAddress.getByName(remoteHost);
			String msg = "hello world!";
			
				byte[] outputData = msg.getBytes();
				DatagramPacket outputPacket = new DatagramPacket(outputData,
						outputData.length, remoteIP, remotePort);
				socket.send(outputPacket); // 给EchoServer发送数据报

				DatagramPacket inputPacket = new DatagramPacket(new byte[512],
						512);
				socket.receive(inputPacket);
				System.out.println(new String(inputPacket.getData(), 0,
						inputPacket.getLength()));
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			socket.close();
		}
	}
}