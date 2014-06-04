/**
 * @(#)TestTemplate.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.context.samples;

import java.io.IOException;

import org.mymmsc.api.context.TemplateSyntaxException;
import org.mymmsc.api.context.Templator;

/**
 * 测试模板
 * 
 * @author WangFeng
 * @version 6.3.9 09/10/02
 * @since 6.3.9
 */
public class TestTemplate {
	private static final String templateFileName = "D:/temp/MiniTemplator_java/Sample2_template.htm";
	private static final String outputFileName = "D:/temp/MiniTemplator_java/Sample2_out.htm";

	/**
	 * 
	 */
	public TestTemplate() {
		// 
	}

	/**
	 * @param args
	 * @throws IOException
	 * @throws TemplateSyntaxException
	 */
	public static void main(String[] args) throws TemplateSyntaxException,
			IOException {
		Templator t = new Templator(templateFileName);
		t.setVariable("year", "2003");
		t.setVariable("month", "April");
		for (int weekOfYear = 14; weekOfYear <= 18; weekOfYear++) {
			for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
				t.addBlock("day");
				int dayOfMonth = (weekOfYear * 7 + dayOfWeek) - 98;
				if (dayOfMonth >= 1 && dayOfMonth <= 30) {
					t.setVariable("day.Month", Integer.toString(dayOfMonth));
				} else {
					t.setVariable("day.Month", "&nbsp;");
				}
				//
			}
			t.setVariable("weekOfYear", Integer.toString(weekOfYear));
			t.addBlock("week");
		}
		t.generateOutput(outputFileName);
	}

}
