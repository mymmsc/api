/**
 * @(#)WapSiPdu.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.w3c.gsm.pdu.wappush;

import java.util.Date;

import org.mymmsc.w3c.gsm.pdu.PduUtils;
import org.mymmsc.w3c.gsm.pdu.sms.SmsSubmitPdu;

/**
 * PduUtils Library - A Java library for generating GSM 3040 Protocol Data Units
 * (PDUs)
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-gsm 6.3.9
 */
public class WapSiPdu extends SmsSubmitPdu {
	// these are for the WSP header
	// content type
	// charset
	// etc.
	// these are for the <indication> tag
	public static final int WAP_SIGNAL_NONE = 0x05;

	public static final int WAP_SIGNAL_LOW = 0x06;

	public static final int WAP_SIGNAL_MEDIUM = 0x07;

	public static final int WAP_SIGNAL_HIGH = 0x08;

	public static final int WAP_SIGNAL_DELETE = 0x09;

	private int wapSignal = WAP_SIGNAL_MEDIUM;

	private String indicationText;

	private String url;

	private Date createDate;

	private Date expireDate;

	private String siId;

	private String siClass;

	public WapSiPdu() {
		setDataCodingScheme(PduUtils.DCS_ENCODING_8BIT
				| PduUtils.DCS_CODING_GROUP_DATA);
	}

	public String getSiId() {
		return siId;
	}

	public void setSiId(String siId) {
		this.siId = siId;
	}

	public String getSiClass() {
		return siClass;
	}

	public void setSiClass(String siClass) {
		this.siClass = siClass;
	}

	public String getIndicationText() {
		return indicationText;
	}

	public void setIndicationText(String indicationText) {
		this.indicationText = indicationText;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Date getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getExpireDate() {
		return expireDate;
	}

	public void setExpireDate(Date expireDate) {
		this.expireDate = expireDate;
	}

	public int getWapSignal() {
		return wapSignal;
	}

	public void setWapSignalFromString(String s) {
		if (s == null) {
			wapSignal = WAP_SIGNAL_MEDIUM;
			return;
		}
		s = s.trim();
		if (s.equalsIgnoreCase("none")) {
			wapSignal = WAP_SIGNAL_NONE;
		} else if (s.equalsIgnoreCase("low")) {
			wapSignal = WAP_SIGNAL_LOW;
		} else if ((s.equalsIgnoreCase("medium")) || (s.equals(""))) {
			wapSignal = WAP_SIGNAL_MEDIUM;
		} else if (s.equalsIgnoreCase("high")) {
			wapSignal = WAP_SIGNAL_HIGH;
		} else if (s.equalsIgnoreCase("delete")) {
			wapSignal = WAP_SIGNAL_DELETE;
		} else {
			throw new RuntimeException("Cannot determine WAP signal to use");
		}
	}

	public void setWapSignal(int i) {
		switch (i) {
		case WAP_SIGNAL_NONE:
		case WAP_SIGNAL_LOW:
		case WAP_SIGNAL_MEDIUM:
		case WAP_SIGNAL_HIGH:
		case WAP_SIGNAL_DELETE:
			wapSignal = i;
			break;
		default:
			throw new RuntimeException("Invalid wap signal value: " + i);
		}
	}

	@Override
	public byte[] getDataBytes() {
		if (super.getDataBytes() == null) {
			WapSiUserDataGenerator udGenerator = new WapSiUserDataGenerator();
			udGenerator.setWapSiPdu(this);
			setDataBytes(udGenerator.generateWapSiUDBytes());
		}
		return super.getDataBytes();
	}
}
