/**
 * @(#)ApiInitialContextFactoryBuilder.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.context;

import java.util.Hashtable;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import javax.naming.spi.NamingManager;

/**
 * 上下文工厂
 * 
 * @author WangFeng(wangfeng@yeah.net)
 * @version 6.3.9 09/10/02
 * @since mymmsc-api 6.3.9
 */
public class ApiInitialContextFactoryBuilder implements
		InitialContextFactoryBuilder {
	private java.util.concurrent.ConcurrentHashMap<String, Object> m_chmObjects = null;

	public static void initialize() {
		try {
			// 如果不存在, 就创建
			if (!NamingManager.hasInitialContextFactoryBuilder()) {
				NamingManager
						.setInitialContextFactoryBuilder(new ApiInitialContextFactoryBuilder());
			}
		} catch (Exception e) {
			e.printStackTrace(System.err);
		}
	}

	public InitialContextFactory createInitialContextFactory(
			Hashtable<?, ?> environment) throws NamingException {
		return new ApiInitialContextFactory();
	}

	private class ApiInitialContextFactory implements InitialContextFactory {
		public Context getInitialContext(Hashtable<?, ?> environment)
				throws NamingException {
			ApiContextImpl apiContextImpl = new ApiContextImpl();
			return apiContextImpl;
		}
	}

	private class ApiContextImpl implements Context {
		private void init() {
			if (m_chmObjects == null) {
				m_chmObjects = new java.util.concurrent.ConcurrentHashMap<String, Object>();
			}
		}

		public Object addToEnvironment(String propName, Object propVal)
				throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void bind(Name name, Object obj) throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.naming.Context#bind(java.lang.String, java.lang.Object)
		 */
		public synchronized void bind(String name, Object obj)
				throws NamingException {
			init();
			if (m_chmObjects.containsKey(name)) {
				m_chmObjects.replace(name, obj);
			} else {
				m_chmObjects.put(name, obj);
			}
		}

		public void close() throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public Name composeName(Name name, Name prefix) throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public String composeName(String name, String prefix)
				throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public Context createSubcontext(Name name) throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public Context createSubcontext(String name) throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void destroySubcontext(Name name) throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void destroySubcontext(String name) throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public Hashtable<?, ?> getEnvironment() throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public String getNameInNamespace() throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public NameParser getNameParser(Name name) throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public NameParser getNameParser(String name) throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public NamingEnumeration<NameClassPair> list(Name name)
				throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public NamingEnumeration<NameClassPair> list(String name)
				throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public NamingEnumeration<Binding> listBindings(Name name)
				throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public NamingEnumeration<Binding> listBindings(String name)
				throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public Object lookup(Name name) throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public synchronized Object lookup(String name) throws NamingException {
			init();
			// 在这里将JNDI名帮定到实际的内容上
			if (name == null) {
				throw new NamingException("Name must not be null.");
			} else if (m_chmObjects.containsKey(name)) {
				return m_chmObjects.get(name);
			} else {
				throw new NamingException("Name \"" + name + "\" not found.");
			}
		}

		public Object lookupLink(Name name) throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public Object lookupLink(String name) throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void rebind(Name name, Object obj) throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void rebind(String name, Object obj) throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public Object removeFromEnvironment(String propName)
				throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void rename(Name oldName, Name newName) throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void rename(String oldName, String newName)
				throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void unbind(Name name) throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

		public void unbind(String name) throws NamingException {
			throw new UnsupportedOperationException("Not supported yet.");
		}

	}
}