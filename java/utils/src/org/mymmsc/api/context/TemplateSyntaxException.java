/**
 * @(#)TemplateSyntaxException.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.context;

/**
 * Thrown when a syntax error is encountered within the template.
 *
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public class TemplateSyntaxException extends RuntimeException {
	private static final long serialVersionUID = 1;

	public TemplateSyntaxException(String msg) {
		super("Syntax error in template: " + msg);
	}
}
