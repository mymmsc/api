/**
 * @(#)XmlParserTest.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.samples.test;

import javax.xml.xpath.XPathExpressionException;

import org.mymmsc.api.assembly.XmlParser;
import org.mymmsc.api.sql.JdbcParams;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-test 6.3.9
 */
public class XmlParserTest {

	/**
	 * TestXerce
	 */
	public XmlParserTest() {
		
	}

	/**
	 * @param args
	 * @throws XPathExpressionException
	 */
	public static void main(String[] args) {
		String expression = "//dbcp/*";
		String systemid = "~/temp/iptable.conf";
		XmlParser xp = new XmlParser(systemid);
		JdbcParams params = null;
		NodeList nl = null;
		try {
			params = (JdbcParams) xp.valueOf("//dbcp/*", JdbcParams.class);
			System.out.println(params.getUrl());
			nl = (NodeList) xp.query(expression);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = (Node) nl.item(i);
			System.out.print(node.getNodeName());
			System.out.print(": ");
			NamedNodeMap attrList = (NamedNodeMap) node.getAttributes();
			for (int j = 0; j < attrList.getLength(); j++) {
				Node attNode = (Node) attrList.item(j);
				System.out.format("%s=%s", attNode.getNodeName(), attNode
						.getTextContent());
			}
			System.out.println(node.getNodeValue());

		}
		// NodeSet nodes = (NodeSet) xpath.evaluate(expression, inputSource,
		// XPathConstants.NODESET);
		System.out.println(nl.getLength());

	}

}
