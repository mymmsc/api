/**
 * @(#)PduGenerator.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.w3c.gsm.pdu;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;

import org.mymmsc.w3c.gsm.pdu.context.ConcatInformationElement;
import org.mymmsc.w3c.gsm.pdu.context.InformationElement;
import org.mymmsc.w3c.gsm.pdu.context.InformationElementFactory;
import org.mymmsc.w3c.gsm.pdu.sms.SmsDeliveryPdu;
import org.mymmsc.w3c.gsm.pdu.sms.SmsStatusReportPdu;
import org.mymmsc.w3c.gsm.pdu.sms.SmsSubmitPdu;

//PduUtils Library - A Java library for generating GSM 3040 Protocol Data Units (PDUs)
//
//Copyright (C) 2008, Ateneo Java Wireless Competency Center/Blueblade Technologies, Philippines.
//PduUtils is distributed under the terms of the Apache License version 2.0
//
//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//http://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.
public class PduGenerator {
	private ByteArrayOutputStream baos;

	private int firstOctetPosition = -1;

	private boolean updateFirstOctet = false;

	protected void writeSmscInfo(PduContext pduContext) throws Exception {
		if (pduContext.getSmscAddress() != null) {
			writeBCDAddress(pduContext.getSmscAddress(), pduContext
					.getSmscAddressType(), pduContext.getSmscInfoLength());
		} else {
			writeByte(0);
		}
	}

	protected void writeFirstOctet(PduContext pduContext) {
		// store the position in case it will need to be updated later
		firstOctetPosition = pduContext.getSmscInfoLength() + 1;
		writeByte(pduContext.getFirstOctet());
	}

	// validity period conversion from hours to the proper integer
	protected void writeValidityPeriodInteger(int validityPeriod) {
		if (validityPeriod == -1) {
			baos.write(0xFF);
		} else {
			int validityInt;
			if (validityPeriod <= 12)
				validityInt = (validityPeriod * 12) - 1;
			else if (validityPeriod <= 24)
				validityInt = (((validityPeriod - 12) * 2) + 143);
			else if (validityPeriod <= 720)
				validityInt = (validityPeriod / 24) + 166;
			else
				validityInt = (validityPeriod / 168) + 192;
			baos.write(validityInt);
		}
	}

	protected void writeTimeStampStringForDate(Date timestamp) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(timestamp);
		int year = cal.get(Calendar.YEAR) - 2000;
		int month = cal.get(Calendar.MONTH) + 1;
		int dayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
		int hourOfDay = cal.get(Calendar.HOUR_OF_DAY);
		int minute = cal.get(Calendar.MINUTE);
		int sec = cal.get(Calendar.SECOND);
		TimeZone tz = cal.getTimeZone();
		int offset = tz.getOffset(timestamp.getTime());
		int minOffset = offset / 60000;
		int tzValue = minOffset / 15;
		// for negative offsets, add 128 to the absolute value
		if (tzValue < 0) {
			tzValue = 128 - tzValue;
		}
		// note: the nibbles are written as BCD style
		baos.write(PduUtils.createSwappedBCD(year));
		baos.write(PduUtils.createSwappedBCD(month));
		baos.write(PduUtils.createSwappedBCD(dayOfMonth));
		baos.write(PduUtils.createSwappedBCD(hourOfDay));
		baos.write(PduUtils.createSwappedBCD(minute));
		baos.write(PduUtils.createSwappedBCD(sec));
		baos.write(PduUtils.createSwappedBCD(tzValue));
	}

	protected void writeAddress(String address, int addressType,
			int addressLength) throws Exception {
		switch (PduUtils.extractAddressType(addressType)) {
		case PduUtils.ADDRESS_TYPE_ALPHANUMERIC:
			byte[] textSeptets = PduUtils.stringToUnencodedSeptets(address);
			byte[] alphaNumBytes = PduUtils.encode7bitUserData(null,
					textSeptets);
			// ADDRESS LENGTH - should be the semi-octet count
			// - this type is not used for SMSCInfo
			baos.write(alphaNumBytes.length * 2);
			// ADDRESS TYPE
			baos.write(addressType);
			// ADDRESS TEXT
			baos.write(alphaNumBytes);
			break;
		default:
			// BCD-style
			writeBCDAddress(address, addressType, addressLength);
		}
	}

	protected void writeBCDAddress(String address, int addressType,
			int addressLength) throws Exception {
		// BCD-style
		// ADDRESS LENGTH - either an octet count or semi-octet count
		baos.write(addressLength);
		// ADDRESS TYPE
		baos.write(addressType);
		// ADDRESS NUMBERS
		// if address.length is not even, pad the string an with F at the end
		if (address.length() % 2 == 1) {
			address = address + "F";
		}
		int digit = 0;
		for (int i = 0; i < address.length(); i++) {
			char c = address.charAt(i);
			if (i % 2 == 1) {
				digit |= ((Integer.parseInt(Character.toString(c), 16)) << 4);
				baos.write(digit);
				// clear it
				digit = 0;
			} else {
				digit |= (Integer.parseInt(Character.toString(c), 16) & 0x0F);
			}
		}
	}

	protected void writeUDData(PduContext pduContext, int mpRefNo, int partNo) {
		int dcs = pduContext.getDataCodingScheme();
		try {
			switch (PduUtils.extractDcsEncoding(dcs)) {
			case PduUtils.DCS_ENCODING_7BIT:
				writeUDData7bit(pduContext, mpRefNo, partNo);
				break;
			case PduUtils.DCS_ENCODING_8BIT:
				writeUDData8bit(pduContext, mpRefNo, partNo);
				break;
			case PduUtils.DCS_ENCODING_UCS2:
				writeUDDataUCS2(pduContext, mpRefNo, partNo);
				break;
			default:
				throw new RuntimeException("Invalid DCS encoding: "
						+ PduUtils.extractDcsEncoding(dcs));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void writeUDH(PduContext pduContext) throws IOException {
		// stream directly into the internal baos
		writeUDH(pduContext, baos);
	}

	protected void writeUDH(PduContext pduContext, ByteArrayOutputStream udhBaos)
			throws IOException {
		// need to insure that proper concat info is inserted
		// before writing if needed
		// i.e. the reference number, maxseq and seq have to be set from
		// outside (OutboundMessage)
		udhBaos.write(pduContext.getUDHLength());
		for (Iterator<InformationElement> ieIterator = pduContext
				.getInformationElements(); ieIterator.hasNext();) {
			InformationElement ie = ieIterator.next();
			udhBaos.write(ie.getIdentifier());
			udhBaos.write(ie.getLength());
			udhBaos.write(ie.getData());
		}
	}

	protected int computeOffset(PduContext pduContext, int maxMessageLength,
			int partNo) {
		// computes offset to which part of the string is to be encoded into the
		// PDU
		// also sets the MpMaxNo field of the concatInfo if message is
		// multi-part
		int offset;
		int maxParts = 1;
		if (!pduContext.isBinary()) {
			maxParts = pduContext.getDecodedText().length() / maxMessageLength
					+ 1;
		} else {
			maxParts = pduContext.getDataBytes().length / maxMessageLength + 1;
		}
		if (pduContext.hasTpUdhi()) {
			if (pduContext.getConcatInfo() != null) {
				if (partNo > 0) {
					pduContext.getConcatInfo().setMpMaxNo(maxParts);
				}
			}
		}
		if ((maxParts > 1) && (partNo > 0)) {
			// - if partNo > maxParts
			// - error
			if (partNo > maxParts) {
				throw new RuntimeException("Invalid partNo: " + partNo
						+ ", maxParts=" + maxParts);
			}
			offset = ((partNo - 1) * maxMessageLength);
		} else {
			// just get from the start
			offset = 0;
		}
		return offset;
	}

	protected void checkForConcat(PduContext pduContext, int lengthOfText,
			int maxLength, int maxLengthWithUdh, int mpRefNo, int partNo) {
		if ((lengthOfText <= maxLengthWithUdh)
				|| ((lengthOfText > maxLengthWithUdh) && (lengthOfText <= maxLength))) {
			// nothing needed
		} else {
			// need concat
			if (pduContext.getConcatInfo() != null) {
				// if concatInfo is already present then just replace the values
				// with the supplied
				pduContext.getConcatInfo().setMpRefNo(mpRefNo);
				pduContext.getConcatInfo().setMpSeqNo(partNo);
			} else {
				// add concat info with the specified mpRefNo, bogus maxSeqNo,
				// and partNo
				// bogus maxSeqNo will be replaced once it is known in the later
				// steps
				// this just needs to be added since its presence is needed to
				// compute
				// the UDH length
				ConcatInformationElement concatInfo = InformationElementFactory
						.generateConcatInfo(mpRefNo, partNo);
				pduContext.addInformationElement(concatInfo);
				updateFirstOctet = true;
			}
		}
	}

	protected int computePotentialUdhLength(PduContext pduContext) {
		int currentUdhLength = pduContext.getTotalUDHLength();
		if (currentUdhLength == 0) {
			// add 1 for the UDH Length field
			return ConcatInformationElement.getDefaultConcatLength() + 1;
		} else {
			// this already has the UDH Length field, no need to add 1
			return currentUdhLength
					+ ConcatInformationElement.getDefaultConcatLength();
		}
	}

	protected void writeUDData7bit(PduContext pduContext, int mpRefNo,
			int partNo) throws Exception {
		String decodedText = pduContext.getDecodedText();
		// partNo states what part of the unencoded text will be used
		// - max length is based on the size of the UDH
		// for 7bit => maxLength = 160 - total UDH septets
		// check if this message needs a concat
		byte[] textSeptetsForDecodedText = PduUtils
				.stringToUnencodedSeptets(decodedText);
		int potentialUdhLength = PduUtils
				.getNumSeptetsForOctets(computePotentialUdhLength(pduContext));

		checkForConcat(pduContext, textSeptetsForDecodedText.length,
				160 - PduUtils.getNumSeptetsForOctets(pduContext
						.getTotalUDHLength()), // CHANGED
				160 - potentialUdhLength, mpRefNo, partNo);

		// given the IEs in the pdu derive the max message body length
		// this length will include the potential concat added in the previous
		// step
		int totalUDHLength = pduContext.getTotalUDHLength();
		int maxMessageLength = 160 - PduUtils
				.getNumSeptetsForOctets(totalUDHLength);

		// get septets for part
		byte[] textSeptets = getUnencodedSeptetsForPart(pduContext,
				maxMessageLength, partNo);

		// udlength is the sum of udh septet length and the text septet length
		int udLength = PduUtils.getNumSeptetsForOctets(totalUDHLength)
				+ textSeptets.length;
		baos.write(udLength);
		// generate UDH byte[]
		// UDHL (sum of all IE lengths)
		// IE list
		byte[] udhBytes = null;
		if (pduContext.hasTpUdhi()) {
			ByteArrayOutputStream udhBaos = new ByteArrayOutputStream();
			writeUDH(pduContext, udhBaos);
			// buffer the udh since this needs to be 7-bit encoded with the text
			udhBytes = udhBaos.toByteArray();
		}
		// encode both as one unit
		byte[] udBytes = PduUtils.encode7bitUserData(udhBytes, textSeptets);
		// write combined encoded array
		baos.write(udBytes);
	}

	private byte[] getUnencodedSeptetsForPart(PduContext pduContext,
			int maxMessageLength, int partNo) {
		// computes offset to which part of the string is to be encoded into the
		// PDU
		// also sets the MpMaxNo field of the concatInfo if message is
		// multi-part
		int offset;
		int maxParts = 1;

		// must use the unencoded septets not the actual string since
		// it is possible that some special characters in string are
		// multi-septet
		byte[] unencodedSeptets = PduUtils.stringToUnencodedSeptets(pduContext
				.getDecodedText());

		maxParts = (unencodedSeptets.length / maxMessageLength) + 1;

		if (pduContext.hasTpUdhi()) {
			if (pduContext.getConcatInfo() != null) {
				if (partNo > 0) {
					pduContext.getConcatInfo().setMpMaxNo(maxParts);
				}
			}
		}
		if ((maxParts > 1) && (partNo > 0)) {
			// - if partNo > maxParts
			// - error
			if (partNo > maxParts) {
				throw new RuntimeException("Invalid partNo: " + partNo
						+ ", maxParts=" + maxParts);
			}
			offset = ((partNo - 1) * maxMessageLength);
		} else {
			// just get from the start
			offset = 0;
		}

		// copy the portion of the full unencoded septet array for this part
		byte[] septetsForPart = new byte[Math.min(maxMessageLength,
				unencodedSeptets.length - offset)];
		System.arraycopy(unencodedSeptets, offset, septetsForPart, 0,
				septetsForPart.length);

		return septetsForPart;
	}

	protected void writeUDData8bit(PduContext pduContext, int mpRefNo,
			int partNo) throws Exception {
		// NOTE: binary messages are also handled here
		byte[] data;

		if (pduContext.isBinary()) {
			// use the supplied bytes
			data = pduContext.getDataBytes();
		} else {
			// encode the text
			data = PduUtils.encode8bitUserData(pduContext.getDecodedText());
		}
		// partNo states what part of the unencoded text will be used
		// - max length is based on the size of the UDH
		// for 8bit => maxLength = 140 - the total UDH bytes
		// check if this message needs a concat
		int potentialUdhLength = computePotentialUdhLength(pduContext);

		checkForConcat(pduContext, data.length, 140 - pduContext
				.getTotalUDHLength(), // CHANGED
				140 - potentialUdhLength, mpRefNo, partNo);

		// given the IEs in the pdu derive the max message body length
		// this length will include the potential concat added in the previous
		// step
		int totalUDHLength = pduContext.getTotalUDHLength();
		int maxMessageLength = 140 - totalUDHLength;
		// compute which portion of the message will be part of the message
		int offset = computeOffset(pduContext, maxMessageLength, partNo);
		byte[] dataToWrite = new byte[Math.min(maxMessageLength, data.length
				- offset)];
		System.arraycopy(data, offset, dataToWrite, 0, dataToWrite.length);
		// generate udlength
		// based on partNo
		// udLength is an octet count for 8bit/ucs2
		int udLength = totalUDHLength + dataToWrite.length;
		// write udlength
		baos.write(udLength);
		// write UDH to the stream directly
		if (pduContext.hasTpUdhi()) {
			writeUDH(pduContext, baos);
		}
		// write data
		baos.write(dataToWrite);
	}

	protected void writeUDDataUCS2(PduContext pduContext, int mpRefNo,
			int partNo) throws Exception {
		String decodedText = pduContext.getDecodedText();
		// partNo states what part of the unencoded text will be used
		// - max length is based on the size of the UDH
		// for ucs2 => maxLength = (140 - the total UDH bytes)/2
		// check if this message needs a concat
		int potentialUdhLength = computePotentialUdhLength(pduContext);

		checkForConcat(pduContext, decodedText.length(), (140 - pduContext
				.getTotalUDHLength()) / 2, // CHANGED
				(140 - potentialUdhLength) / 2, mpRefNo, partNo);

		// given the IEs in the pdu derive the max message body length
		// this length will include the potential concat added in the previous
		// step
		int totalUDHLength = pduContext.getTotalUDHLength();
		int maxMessageLength = (140 - totalUDHLength) / 2;
		// compute which portion of the message will be part of the message
		int offset = computeOffset(pduContext, maxMessageLength, partNo);
		String textToEncode = decodedText.substring(offset, Math.min(offset
				+ maxMessageLength, decodedText.length()));
		// generate udlength
		// based on partNo
		// udLength is an octet count for 8bit/ucs2
		int udLength = totalUDHLength + (textToEncode.length() * 2);
		// write udlength
		baos.write(udLength);
		// write UDH to the stream directly
		if (pduContext.hasTpUdhi()) {
			writeUDH(pduContext, baos);
		}
		// write encoded text
		baos.write(PduUtils.encodeUcs2UserData(textToEncode));
	}

	protected void writeByte(int i) {
		baos.write(i);
	}

	protected void writeBytes(byte[] b) throws Exception {
		baos.write(b);
	}

	public List<String> generatePduList(PduContext pduContext, int mpRefNo) {
		// generate all required PDUs for a given message
		// mpRefNo comes from the ModemGateway
		ArrayList<String> pduList = new ArrayList<String>();
		for (int i = 1; i <= pduContext.getMpMaxNo(); i++) {
			String pduString = generatePduString(pduContext, mpRefNo, i);
			pduList.add(pduString);
		}
		return pduList;
	}

	public String generatePduString(PduContext pduContext) {
		return generatePduString(pduContext, -1, -1);
	}

	// NOTE: partNo indicates which part of a multipart message to generate
	// assuming that the message is multipart, this will be ignored if the
	// message is not a concat message
	public String generatePduString(PduContext pduContext, int mpRefNo,
			int partNo) {
		try {
			baos = new ByteArrayOutputStream();
			firstOctetPosition = -1;
			updateFirstOctet = false;
			// process the PDU
			switch (pduContext.getTpMti()) {
			case PduUtils.TP_MTI_SMS_DELIVER:
				generateSmsDeliverPduString((SmsDeliveryPdu) pduContext,
						mpRefNo, partNo);
				break;
			case PduUtils.TP_MTI_SMS_SUBMIT:
				generateSmsSubmitPduString((SmsSubmitPdu) pduContext, mpRefNo,
						partNo);
				break;
			case PduUtils.TP_MTI_SMS_STATUS_REPORT:
				generateSmsStatusReportPduString((SmsStatusReportPdu) pduContext);
				break;
			}
			// in case concat is detected in the writeUD() method
			// and there was no UDHI at the time of detection
			// the old firstOctet must be overwritten with the new value
			byte[] pduBytes = baos.toByteArray();
			if (updateFirstOctet) {
				pduBytes[firstOctetPosition] = (byte) (pduContext
						.getFirstOctet() & 0xFF);
			}
			return PduUtils.bytesToPdu(pduBytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected void generateSmsSubmitPduString(SmsSubmitPdu pdu, int mpRefNo,
			int partNo) throws Exception {
		// SMSC address info
		writeSmscInfo(pdu);
		// first octet
		writeFirstOctet(pdu);
		// message reference
		writeByte(pdu.getMessageReference());
		// destination address info
		writeAddress(pdu.getAddress(), pdu.getAddressType(), pdu.getAddress()
				.length());
		// protocol id
		writeByte(pdu.getProtocolIdentifier());
		// data coding scheme
		writeByte(pdu.getDataCodingScheme());
		// validity period
		switch (pdu.getTpVpf()) {
		case PduUtils.TP_VPF_INTEGER:
			writeValidityPeriodInteger(pdu.getValidityPeriod());
			break;
		case PduUtils.TP_VPF_TIMESTAMP:
			writeTimeStampStringForDate(pdu.getValidityDate());
			break;
		}
		// user data
		// headers
		writeUDData(pdu, mpRefNo, partNo);
	}

	// NOTE: the following are just for validation of the PduParser
	// - there is no normal scenario where these are used
	protected void generateSmsDeliverPduString(SmsDeliveryPdu pdu, int mpRefNo,
			int partNo) throws Exception {
		// SMSC address info
		writeSmscInfo(pdu);
		// first octet
		writeFirstOctet(pdu);
		// originator address info
		writeAddress(pdu.getAddress(), pdu.getAddressType(), pdu.getAddress()
				.length());
		// protocol id
		writeByte(pdu.getProtocolIdentifier());
		// data coding scheme
		writeByte(pdu.getDataCodingScheme());
		// timestamp
		writeTimeStampStringForDate(pdu.getTimestamp());
		// user data
		// headers
		writeUDData(pdu, mpRefNo, partNo);
	}

	protected void generateSmsStatusReportPduString(SmsStatusReportPdu pdu)
			throws Exception {
		// SMSC address info
		writeSmscInfo(pdu);
		// first octet
		writeFirstOctet(pdu);
		// message reference
		writeByte(pdu.getMessageReference());
		// destination address info
		writeAddress(pdu.getAddress(), pdu.getAddressType(), pdu.getAddress()
				.length());
		// timestamp
		writeTimeStampStringForDate(pdu.getTimestamp());
		// discharge time(timestamp)
		writeTimeStampStringForDate(pdu.getDischargeTime());
		// status
		writeByte(pdu.getStatus());
	}
}
