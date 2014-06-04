/**
 * @(#)ApiException.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.adapter;

/**
 * MyMMSC-API 异常处理类
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public class ApiException extends Throwable {
	private static final long serialVersionUID = -7582890077906902320L;

	/**
	 * Constructs an {@code ApiException} with {@code null} as its error detail
	 * message.
	 */
	public ApiException() {
		super();
	}

	/**
	 * Constructs an {@code ApiException} with the specified detail message.
	 * 
	 * @param message
	 *            The detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method)
	 */
	public ApiException(String message) {
		super(message);
	}

	/**
	 * Constructs an {@code ApiException} with the specified detail message and
	 * cause.
	 * 
	 * <p>
	 * Note that the detail message associated with {@code cause} is <i>not</i>
	 * automatically incorporated into this exception's detail message.
	 * 
	 * @param message
	 *            The detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method)
	 * 
	 * @param cause
	 *            The cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A null value is permitted, and
	 *            indicates that the cause is nonexistent or unknown.)
	 * 
	 * @since 6.3.9
	 */
	public ApiException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs an {@code ApiException} with the specified cause and a detail
	 * message of {@code (cause==null ? null : cause.toString())} (which
	 * typically contains the class and detail message of {@code cause}). This
	 * constructor is useful for Api exceptions that are little more than
	 * wrappers for other throwables.
	 * 
	 * @param cause
	 *            The cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A null value is permitted, and
	 *            indicates that the cause is nonexistent or unknown.)
	 * 
	 * @since 6.3.9
	 */
	public ApiException(Throwable cause) {
		super(cause);
	}
}
