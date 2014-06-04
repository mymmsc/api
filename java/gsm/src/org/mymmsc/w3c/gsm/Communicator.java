/**
 * @(#)Communication.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.w3c.gsm;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.TooManyListenersException;

import javax.comm.CommPortIdentifier;
import javax.comm.PortInUseException;
import javax.comm.SerialPort;
import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import javax.comm.UnsupportedCommOperationException;

import org.mymmsc.w3c.dso.AbstractLibrary;
import org.mymmsc.w3c.gsm.pdu.PduContext;
import org.mymmsc.w3c.gsm.pdu.PduParser;

/**
 * 串口通信
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-gsm 6.3.9
 */
public class Communicator extends AbstractLibrary implements
		SerialPortEventListener {

	private Enumeration<?> m_port_list;
	private String m_port_name;
	private CommPortIdentifier m_port_id;// 串口管理器,负责打开,调用,和初始化串口等管理工作
	private SerialPort m_serial_port = null;
	private int m_reply_interval;
	private int m_command_delay;
	private String m_reply_string;
	private InputStream m_input_stream = null;
	private OutputStream m_output_stream = null;
	private int m_baud_rate;
	private String m_send_mode;
	private String m_message;
	// private int m_msgcount = 0;
	private boolean m_err_flag = false;
	private InBox m_inBox = null;
	private OutBox m_outBox = null;

	// private List<Integer> m_listsmg = new LinkedList<Integer>();

	public Communicator() {
		m_port_name = "COM1";
		m_baud_rate = 9600;
		m_send_mode = "0";
		m_reply_interval = 200;
		m_command_delay = 200;

		m_inBox = new InBox();
		m_outBox = new OutBox();
	}

	/**
	 * 列出所有串口
	 */
	public void listSerialPort() {
		// CommPortIdentifier类的getPortIdentifiers方法可以找到系统所有的串口，
		// 每个串口对应一个CommPortIdentifier类的实例。
		m_port_list = null;
		m_port_id = null;
		m_port_list = CommPortIdentifier.getPortIdentifiers();
		info("串口列表：");
		while (m_port_list.hasMoreElements()) {
			m_port_id = (CommPortIdentifier) m_port_list.nextElement();
			/* 如果端口类型是串口，则打印出其端口信息 */
			if (m_port_id.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				info(m_port_id.getName());
			}
		}
		info("串口列表显示结束！");
	}

	/**
	 * 获得串口
	 */
	public void getSerialPort() {
		if (m_err_flag == true) {
			return;
		}
		info("检查连接情况...");
		if (m_port_name == "") {
			info("串口号为空，请检查配置文件！");
			m_err_flag = true;
			return;
			// System.out.println("Portname is null, get err, the program now exit!");
			// System.exit(0);
		}
		m_port_list = CommPortIdentifier.getPortIdentifiers();
		while (m_port_list.hasMoreElements()) {
			m_port_id = (CommPortIdentifier) m_port_list.nextElement();
			if ((m_port_id.getPortType() == CommPortIdentifier.PORT_SERIAL)
					&& m_port_id.getName().equalsIgnoreCase(m_port_name)) {
				try {
					m_serial_port = (SerialPort) m_port_id
							.open("SendSms", 2000);
				} catch (PortInUseException e) {
					info("获取" + m_port_name + "时出错！原因：" + e.getMessage());
					m_err_flag = true;
					return;
				}
			}
		}
	}

	public void listenSerialPort() {
		if (m_err_flag == true) {
			return;
		}
		if (m_serial_port == null) {
			info("不存在" + m_port_name + "，请检查相关配置！");
			m_err_flag = true;
			return;
		}
		// 设置输入输出流
		try {
			m_output_stream = (OutputStream) m_serial_port.getOutputStream();
			m_input_stream = (InputStream) m_serial_port.getInputStream();
		} catch (IOException e) {
			info(e.getMessage());
		}
		try {
			// 监听端口
			m_serial_port.notifyOnDataAvailable(true);
			m_serial_port.notifyOnBreakInterrupt(true);
			m_serial_port.addEventListener(this);
		} catch (TooManyListenersException e) {
			m_serial_port.close();
			info(e.getMessage());
		}
		try {
			m_serial_port.enableReceiveTimeout(20);
		} catch (UnsupportedCommOperationException e) {
			//
		}
		// 设置端口的基本参数
		try {
			m_serial_port.setSerialPortParams(m_baud_rate,
					SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
		} catch (UnsupportedCommOperationException e) {
			e.printStackTrace();
		}
	}

	// 对串口的读写操作
	public void writeToSerialPort(String msgString) {
		try {
			m_output_stream.write(msgString.getBytes());
			// CTRL+Z=(char)26
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	private void waitForRead(int waitTime) {
		try {
			Thread.sleep(waitTime);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public String readFromSerialPort(String message) {
		int strLength;
		String messageStr;
		String returnString = "";
		strLength = message.length();
		System.out.println("READ: " + message);
		if (strLength >= 6) {
			messageStr = message.substring(strLength - 4, strLength - 2);
			if (messageStr.equals("OK")) {
				returnString = messageStr;
			} else {
				returnString = message;
			}
			messageStr = message.substring(strLength - 6, strLength - 2);
			if (messageStr.equals("ERROR")) {
				returnString = messageStr;
			}
		}
		return returnString;
	}

	// 操作结束，关闭所用资源
	public void closeSerialPort() {
		if (m_serial_port != null) {
			try {
				m_serial_port.close();
			} catch (RuntimeException e) {
				System.out.println(e.getMessage());
			}
		}
		info("已断开连接！");
	}

	public void closeIOStream() {
		if (m_input_stream != null) {
			try {
				m_input_stream.close();
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
		}
		if (m_output_stream != null) {
			try {
				m_output_stream.close();
			} catch (IOException e1) {
				System.out.println(e1.getMessage());
			}
		}
		// returnStateInfo("已关闭I/O流！");
	}

	public void setToNull() {
		if (m_serial_port != null) {
			m_serial_port.close();
		}
		m_serial_port = null;
		m_input_stream = null;
		m_output_stream = null;
	}

	/*
	 * 监听事件 (non-Javadoc)
	 * 
	 * @see
	 * javax.comm.SerialPortEventListener#serialEvent(javax.comm.SerialPortEvent
	 * )
	 */
	public void serialEvent(SerialPortEvent e) {
		StringBuffer inputBuffer = new StringBuffer();
		int newData = 0;
		info("事件: " + CommEvent.getDesc(e.getEventType()));
		switch (e.getEventType()) {
		case SerialPortEvent.DATA_AVAILABLE:// DATA_AVAILABLE - 有数据到达
			while (newData != -1) {
				try {
					newData = m_input_stream.read();
					if (newData == -1) {
						break;
					}
					// if ('\r' == (char) newData) {
					// inputBuffer.append('\n');
					// } else {
					inputBuffer.append((char) newData);
					// }
				} catch (IOException ex) {
					System.err.println(ex);
					return;
				}
			}
			info("有数据: " + inputBuffer.toString());
			m_message += new String(inputBuffer);
			if (m_message.trim().startsWith("+CMGL:")) {
				PduParser pp = new PduParser();
				PduContext pdu = null;
				String s = m_message.trim();
				String sline = "";
				int tmpIndex = -1;
				int tmpType = -1;
				int tmpLength = -1;

				for (int i = 0; i < s.length(); i++) {
					char ch = s.charAt(i);
					switch (ch) {
					case '+':
						sline += ch;
						break;
					case '\r':
					case '\n':
						sline = sline.trim();
						if (sline.length() > 1) {
							System.out
									.println("------------------------------>");
							System.out.println("<" + sline + ">");
							if (sline.charAt(0) == '+') {
								String tmpParams = null;
								// 命令
								if (sline.startsWith(Constants.GSM_READ)) {
									// 短信 信息列表: +CMGL: 索引,类型,,长度<内容>
									tmpParams = sline
											.substring(Constants.GSM_READ
													.length() + 1);
									String[] params = tmpParams.split(",");
									if (params.length == 4) {
										System.out.println("params[0]: "
												+ params[0]);
										tmpIndex = Integer.valueOf(
												params[0].trim()).intValue();
										tmpType = Integer.valueOf(
												params[1].trim()).intValue();
										tmpLength = Integer.valueOf(
												params[3].trim()).intValue();
									}
								}
							} else if (tmpIndex > 0) {
								// 解析短信内容
								pdu = pp.parsePdu(sline.trim());
								info(pdu.toString());
								if (pdu.getSmscAddress().length() > 5
										&& tmpIndex >= 0) {
									pdu.setIndex(tmpIndex);
									pdu.setInfoType(tmpType);
									pdu.setInfoLength(tmpLength);
									pdu.setInfoOps(0);
									// 投递到收件箱
									m_inBox.add(pdu);
									tmpIndex = -1;
									tmpType = -1;
									tmpLength = -1;
								}
							}
							sline = "";
							System.out
									.println("------------------------------>");
						}
						break;
					default:
						sline += ch;
						break;
					}
				}
			}
			info("message: " + m_message);
			break;
		case SerialPortEvent.BI:// BI - 通讯中断.
			info("\n--- BREAK RECEIVED ---\n");
		}
	}

	/**
	 * 信息发送
	 * 
	 * @param messageString
	 * @param phoneNumber
	 */
	public void sendmsg(String messageString, String phoneNumber) {
		// boolean sendSucc = false;
		getSerialPort();
		listenSerialPort();
		checkConn();
		if (m_err_flag == true) {
			return;
		}
		int msglength;
		String sendmessage, tempSendString;
		info("开始发送...");
		switch (Integer.parseInt(m_send_mode)) {
		case 0:// 按PDU方式发送
		{
			m_message = "";
			writeToSerialPort("AT+CMGF=0\r");
			waitForRead(m_command_delay);
			msglength = messageString.length();
			if (msglength < 8) {
				tempSendString = "000801" + "0"
						+ Integer.toHexString(msglength * 2).toUpperCase()
						+ asc2unicode(new StringBuffer(messageString));
			} else {
				tempSendString = "000801"
						+ Integer.toHexString(msglength * 2).toUpperCase()
						+ asc2unicode(new StringBuffer(messageString));
			}
			// "000801"说明：分为00,08,01,
			// "00",普通GSM类型，点到点方式
			// "08",UCS2编码
			// "01",有效期
			if (phoneNumber.trim().length() > 0) {
				String[] infoReceiver = phoneNumber.split(",");
				int receiverCount = infoReceiver.length;
				if (receiverCount > 0) {
					for (int i = 0; i < receiverCount; i++) {
						sendmessage = "0011000D91" + "68"
								+ changePhoneNumber(infoReceiver[i])
								+ tempSendString;
						m_reply_string = readFromSerialPort(m_message);
						if (!m_reply_string.equals("ERROR")) {
							m_message = "";
							writeToSerialPort("AT+CMGS=" + (msglength * 2 + 15)
									+ "\r");
							waitForRead(m_command_delay);
							writeToSerialPort(sendmessage);
							try {
								m_output_stream.write((char) 26);
							} catch (IOException ioe) {
								//
							}
							getReply();
							if (m_reply_string.equals("OK")) {
								info("成功发送到 " + infoReceiver[i]);
							}
							if (m_reply_string.equals("ERROR")) {
								System.out.println("发送给 " + infoReceiver[i]
										+ " 时失败！");
							}
						}
					}
				}
			}
			break;
		}
		case 1:// 按文本方式发送，不能发送中文
		{
			m_message = "";
			writeToSerialPort("AT+CMGF=1\r");
			waitForRead(m_command_delay);
			if (phoneNumber.trim().length() > 0) {
				String[] infoReceiver = phoneNumber.split(",");
				int receiverCount = infoReceiver.length;
				if (receiverCount > 0) {
					for (int i = 0; i < receiverCount; i++) {
						m_reply_string = readFromSerialPort(m_message);
						if (!m_reply_string.equals("ERROR")) {
							writeToSerialPort("AT+CMGS=" + infoReceiver[i]
									+ "\r");
							waitForRead(m_command_delay);
							writeToSerialPort(messageString);
							try {
								m_output_stream.write((char) 26);
							} catch (IOException ioe) {
							}
							getReply();
							if (m_reply_string.equals("OK")) {
								info("成功发送到 " + infoReceiver[i]);
							}
							if (m_reply_string.equals("ERROR")) {
								System.out.println("发送给 " + infoReceiver[i]
										+ " 时失败！");
							}
						}
					}
				}
			}
			break;
		}
		default: {
			info("发送方式不对，请检查配置文件！");
			System.exit(0);
			break;
		}
		}
		closeIOStream();
		closeSerialPort();
		m_message = "";
		info("发送完毕！");
	}

	// 读取所有短信
	public void readAllMessage(int readType) {
		getSerialPort();
		listenSerialPort();
		checkConn();
		if (m_err_flag == true) {
			return;
		}
		info("开始获取信息，可能要些时间，请等待...");
		// String tempAnalyseMessage = "";
		writeToSerialPort("AT+CMGF=0\r");
		waitForRead(m_command_delay);
		m_message = "";
		writeToSerialPort("AT+CMGL=" + readType + "\r");
		waitForRead(m_command_delay);
		try {
			getReply();
		} catch (Exception e) {
			//
		}
		info("信息获取结束！");
		closeIOStream();
		closeSerialPort();
		m_message = "";
	}

	/**
	 * 读取指定短信
	 * 
	 * @param msgIndex
	 */
	public void readMessage(int msgIndex) {
		getSerialPort();
		listenSerialPort();
		checkConn();
		if (m_err_flag == true) {
			return;
		}
		// String[] tempAnalyseMessage = null;
		writeToSerialPort("AT+CMGF=0\r");
		waitForRead(m_command_delay);
		m_message = "";
		if (msgIndex < 10) {
			writeToSerialPort("AT+CMGR=0" + msgIndex + "\r");
		} else {
			writeToSerialPort("AT+CMGR=" + msgIndex + "\r");
		}
		waitForRead(m_command_delay);
		try {
			getReply();
		} catch (Exception e) {
			//
		}
		closeIOStream();
		closeSerialPort();
		m_message = "";
	}

	/**
	 * 对短信时间进行处理
	 * 
	 * @param msgBuffer
	 * @return
	 */
	public String fixInfoTime(StringBuffer msgBuffer) {
		// msgBuffer.insert(12, "+");
		for (int i = 1; i < 3; i++) {
			msgBuffer.insert(12 - i * 2, ":");
		}
		msgBuffer.insert(6, " ");// 设置日期与时间之间的连字符号
		for (int i = 1; i < 3; i++) {
			msgBuffer.insert(6 - i * 2, "-");// 设置年、月、日之间的连字符号
		}
		return (new String(msgBuffer));
	}

	// 修正号码在内存中的表示，每2位为1组，每组2个数字交换，
	// 若号码个数为奇数，则在末尾补'F'凑成偶数，然后再进行变换，
	// 因为在计算机中，表示数字高低位顺序与我们的习惯相反.
	// 如："8613851872468" --> "683158812764F8"
	public String changePhoneNumber(String phoneNumber) {
		int numberLength = phoneNumber.length();
		if (phoneNumber.length() % 2 != 0) {
			phoneNumber = phoneNumber + "F";
			numberLength += 1;
		}
		char newPhoneNumber[] = new char[numberLength];
		for (int i = 0; i < numberLength; i += 2) {
			newPhoneNumber[i] = phoneNumber.charAt(i + 1);
			newPhoneNumber[i + 1] = phoneNumber.charAt(i);
		}
		return (new String(newPhoneNumber));
	}

	/**
	 * 转换为UNICODE编码
	 * 
	 * @param msgString
	 * @return
	 */
	public String asc2unicode(StringBuffer msgString) {
		StringBuffer msgReturn = new StringBuffer();
		int msgLength = msgString.length();
		if (msgLength > 0) {
			for (int i = 0; i < msgLength; i++) {
				new Integer((int) msgString.charAt(0)).toString();
				msgReturn.append(new StringBuffer());
				String msgCheck = new String(Integer
						.toHexString((int) msgString.charAt(i)));
				if (msgCheck.length() < 4) {
					msgCheck = "00" + msgCheck;
				}
				msgReturn.append(new StringBuffer(msgCheck));
			}
		}
		return (new String(msgReturn).toUpperCase());
	}

	/**
	 * UNICODE编码转换为正常文字
	 * 
	 * @param msgString
	 * @return
	 */
	public String unicode2asc(String msgString) {
		int msgLength = msgString.length();
		char msg[] = new char[msgLength / 4];
		for (int i = 0; i < msgLength / 4; i++) {
			// UNICODE编码转成十六进制数，再转换为正常文字
			msg[i] = (char) Integer.parseInt((msgString.substring(i * 4,
					4 * i + 4)), 16);
		}
		return (new String(msg));
	}

	/**
	 * 不断读取返回信号, 当收到OK信号时, 停止读取, 以执行下面的操作
	 */
	public void getReply() {
		m_reply_string = readFromSerialPort(m_message);
		while (m_reply_string != null) {
			// if (m_reply_string.equals("OK") ||
			// m_reply_string.equals("ERROR")) {
			if (m_reply_string.indexOf("OK") >= 0
					|| m_reply_string.indexOf("ERROR") >= 0) {
				return;
			}
			waitForRead(m_reply_interval);
			m_reply_string = readFromSerialPort(m_message);
		}
	}

	/**
	 * 检查GSM Modem或卡有无连接错误
	 */
	public void checkConn() {
		if (m_err_flag == true) {
			return;
		}
		m_message = "";
		writeToSerialPort("AT+CSCA?\r");
		waitForRead(m_command_delay);
		getReply();
		if (m_reply_string.equals("ERROR")) {
			info("Modem 或手机卡连接有误，请检查！");
			m_err_flag = true;
			closeIOStream();
			closeSerialPort();
			return;
		}
		info("连接正常！");
	}

	/**
	 * 删除短信
	 * 
	 * @param msgIndex
	 */
	public void remove(int msgIndex) {
		m_message = "";
		getSerialPort();
		listenSerialPort();
		checkConn();
		writeToSerialPort("AT+CMGF=0\r");
		waitForRead(m_command_delay);
		getReply();
		if (m_reply_string.equals("OK")) {
			m_message = "";
			try {
				writeToSerialPort("AT+CMGD=" + msgIndex + "\r");
			} catch (RuntimeException e) {
				System.out.println(e.getMessage());
			}
		}
		closeIOStream();
		closeSerialPort();
	}

	/**
	 * 清空消息容器
	 * 
	 * @param box
	 */
	private void removeBox(AbstractBox box) {
		int idx = -1;
		for (PduContext pdu : box) {
			idx = pdu.getIndex();
			info("正在删除第" + idx + "条短信.");
			remove(idx);
		}
		// box.clear();
	}

	/**
	 * 删除所有消息
	 */
	public void removeAll() {
		removeBox(m_inBox);
		removeBox(m_outBox);
	}

	public void sendAll() {
		int idx = -1;
		int tmpSize = m_inBox.size();
		for (int i = 0; i < tmpSize; i++) {
			PduContext pdu = m_inBox.get(0);
			idx = pdu.getIndex();
			sendmsg(pdu.getDecodedText(), pdu.getAddress().substring(2));
			info("正在删除第" + idx + "条短信.");
			remove(idx);
			m_inBox.remove(0);
		}
		// m_inBox.clear();
	}

	public void checkAll() {
		int idx = -1;
		int tmpSize = m_inBox.size();
		for (int i = 0; i < tmpSize; i++) {
			PduContext pdu = m_inBox.get(0);
			idx = pdu.getIndex();
			if(pdu.getAddress().indexOf("10658658") >=0) {
				info("****************************************************************");
				info(pdu.getAddress() + " >> " + pdu.getDecodedText());
				info("****************************************************************");
			}
			remove(idx);
			m_inBox.remove(0);
		}
		// m_inBox.clear();
	}

	private void info(String errInfo) {
		System.out.println(errInfo);
		logger.info(errInfo);
	}

	@Override
	public boolean close() {
		// TODO Auto-generated method stub
		closeIOStream();
		closeSerialPort();
		return true;
	}

	@Override
	public String getUnixClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getWindowsClass() {
		// TODO Auto-generated method stub
		return null;
	}
}
