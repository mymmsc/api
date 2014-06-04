/**
 * 
 */
package org.mymmsc.android.app.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.mymmsc.api.assembly.Api;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * @author WangFeng(mail:wangfeng@yeah.net, mobile:13911113230)
 * @version 1.0.1 2011-5-28
 * @since mymmsc-android 1.0.1
 */
public class AndroidXmlParser {
	private DocumentBuilderFactory m_builderFactory = null;
	private DocumentBuilder m_documentBuilder = null;
	/** 资源输入流 */
	private InputSource m_is = null;
	/** 系统资源标识串, 可以是本地文件路径, 或者是URL */
	@SuppressWarnings("unused")
	private String m_systemId = null;
	/** XML文档 */
	private Document m_document = null;
	/** 是否检测资源更新 */
	@SuppressWarnings("unused")
	private boolean m_checkMotified = false;
	/** 记录最近一次系统更新时的时间戳, 仅对文件有效 */
	@SuppressWarnings("unused")
	private long m_lastmotified = 0;

	/**
	 * XmlParser构造函数
	 * 
	 * Create a new input source with a system identifier.
	 * 
	 * <p>
	 * Applications may use setPublicId to include a public identifier as well,
	 * or setEncoding to specify the character encoding, if known.
	 * </p>
	 * 
	 * <p>
	 * If the system identifier is a URL, it must be fully resolved (it may not
	 * be a relative URL).
	 * </p>
	 * 
	 * @param systemId
	 *            The system identifier (URI).
	 * @param checkMotified
	 *            是否检查XML文件更新
	 * @see #setPublicId
	 * @see #setSystemId
	 * @see #setByteStream
	 * @see #setEncoding
	 * @see #setCharacterStream
	 */
	public AndroidXmlParser(String systemId, boolean checkMotified) {
		m_systemId = systemId;
		parse(new InputSource(systemId), checkMotified);
	}

	/**
	 * XmlParser构造函数
	 * 
	 * @param steam
	 */
	public AndroidXmlParser(InputStream steam) {
		parse(new InputSource(steam), false);
	}

	/**
	 * XmlParser构造函数
	 * 
	 * @param s
	 *            xml字符串
	 */
	public AndroidXmlParser(String s) {
		parse(new InputSource(new StringReader(s)), false);
	}

	/**
	 * XmlParser构造函数
	 * 
	 * @param is
	 */
	public AndroidXmlParser(InputSource is) {
		parse(is, false);
	}

	/**
	 * 关闭XML解析器
	 */
	public void close() {
		//
	}

	private void init() throws ParserConfigurationException {
		if (m_builderFactory == null) {
			System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
					"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
			m_builderFactory = DocumentBuilderFactory.newInstance();
		}
		if (m_documentBuilder == null) {
			m_documentBuilder = m_builderFactory.newDocumentBuilder();
		}
	}

	/**
	 * 解析 系统id
	 * 
	 * @param is
	 * @param checkMotified
	 */
	private void parse(InputSource is, boolean checkMotified) {
		m_is = is;
		m_checkMotified = checkMotified;
		try {
			init();
			m_document = m_documentBuilder.parse(m_is);
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取一个节点属性值
	 * 
	 * @param node
	 * @param name
	 * @return String, 如果没有找到对应的属性则返回null
	 */
	public String valueOf(Node node, String name) {
		String value = null;
		if (node != null && node.hasAttributes() && name.length() > 0) {
			// 获得一个节点属性值
			NamedNodeMap map = node.getAttributes();
			if (map != null && map.getLength() > 0) {
				Node tmpNode = map.getNamedItem(name);
				if (tmpNode != null) {
					value = tmpNode.getNodeValue();
				}
			}
		}
		return value;
	}

	/**
	 * 类反射得到一个XPATH查询结果的对象 , 此方法适合以节点为主的一组关联属性的读取
	 * 
	 * @param expression
	 * @param clazz
	 * @return
	 * @throws XPathExpressionException
	 */
	public <T> T valueOf(String expression, Class<T> clazz) {
		// Type[] types = ((ParameterizedType)
		// getClass().getGenericSuperclass()).getActualTypeArguments();
		// entityClass = (Class<T>) types[0];
		// 初始状态为null
		T obj = null;
		// 取得clazz类的成员变量列表
		Field[] fields = clazz.getDeclaredFields();
		Field field = null;
		// 查询XML节点
		NodeList list = m_document.getElementsByTagName(expression);

		if (list != null && list.getLength() > 0) {
			list = list.item(0).getChildNodes();
			Node node = null;
			String nodeName = null;
			String nodeValue = null;
			boolean isAccessible = false;
			for (int i = 0; list != null && i < list.getLength(); i++) {
				// 读取一个节点
				node = list.item(i);
				nodeName = node.getNodeName().trim();
				if (node.getFirstChild() == null) {
					continue;
				}
				nodeValue = node.getFirstChild().getNodeValue().trim();
				// 遍历所有类成员变量, 为赋值作准备
				for (int j = 0; j < fields.length; j++) {
					field = fields[j];
					// 忽略字段名大小写
					if (field.getName().equalsIgnoreCase(nodeName)) {
						// 得到类成员变量数据类型
						Class<?> cClass = field.getType();
						Object objValue = Api.valueOf(cClass, nodeValue);
						if (obj == null) {
							try {
								obj = clazz.newInstance();
							} catch (InstantiationException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
						}
						if (obj != null) {
							// 保存现在的字段存储"权限"(对于不同属性的类成员变量)状态
							isAccessible = field.isAccessible();
							// 设定为可存取
							field.setAccessible(true);
							try {
								// 对象字段赋值
								field.set(obj, objValue);
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} finally {
								// 恢复之前的存储权限状态
								field.setAccessible(isAccessible);
							}
						}
						break;
					}
				}
			}
		}
		return obj;
	}

	/**
	 * 类反射得到一个XPATH查询结果的对象 , 此方法适合以节点为主的一组关联属性的读取
	 * 
	 * @param expression
	 * @param attrName
	 * @return
	 * @throws XPathExpressionException
	 */
	public String valueOf2(String expression, String attrName) {
		String sRet = null;
		// 查询XML节点
		NodeList list = m_document.getElementsByTagName(expression);
		if (list != null && list.getLength() > 0) {
			Node node = list.item(0);
			sRet = valueOf(node, attrName);
		}
		return sRet;
	}

	/**
	 * 类反射得到一个XPATH查询结果的对象 , 此方法适合以节点为主的一组关联属性的读取
	 * 
	 * @param expression
	 * @param clazz
	 * @return
	 * @throws XPathExpressionException
	 */
	public <T> List<T> valueOf5(String expression, Class<T> clazz) {
		List<T> objList = new ArrayList<T>();
		// 初始状态为null
		T obj = null;
		// 取得clazz类的成员变量列表
		Field[] fields = clazz.getDeclaredFields();
		Field field = null;
		// 查询XML节点
		NodeList list = m_document.getElementsByTagName(expression);
		for (int k = 0; list != null && k < list.getLength(); k++) {
			NodeList list2 = list.item(k).getChildNodes();
			Node node = null;
			String nodeName = null;
			String nodeValue = null;
			boolean isAccessible = false;
			obj = null;
			for (int i = 0; i < list2.getLength(); i++) {
				// 读取一个节点
				node = list2.item(i);
				nodeName = node.getNodeName().trim();
				if (node.getFirstChild() == null) {
					continue;
				}
				nodeValue = node.getFirstChild().getNodeValue().trim();
				System.out.println(nodeName + "," + nodeValue);
				for (int j = 0; j < fields.length; j++) {
					field = fields[j];
					// 忽略字段名大小写
					if (field.getName().equalsIgnoreCase(nodeName)) {
						// 得到类成员变量数据类型
						Class<?> cClass = field.getType();
						Object objValue = Api.valueOf(cClass, nodeValue);
						if (obj == null) {
							try {
								obj = clazz.newInstance();
							} catch (InstantiationException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							}
						}
						if (obj != null) {
							// 保存现在的字段存储"权限"(对于不同属性的类成员变量)状态
							isAccessible = field.isAccessible();
							// 设定为可存取
							field.setAccessible(true);
							try {
								// 对象字段赋值
								field.set(obj, objValue);
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} finally {
								// 恢复之前的存储权限状态
								field.setAccessible(isAccessible);
							}
						}
						break;
					}
				}
			}
			objList.add(obj);
		}
		return objList;
	}

	/**
	 * 修改XML文档节点值
	 * 
	 * @param node
	 * @param nodeValue
	 * @return
	 */
	public boolean updateValue(Node node, String nodeValue) {
		boolean bRet = false;
		if (node != null && nodeValue != null) {
			try {
				node.setNodeValue(nodeValue);
				bRet = true;
			} catch (DOMException e) {
				e.printStackTrace();
			}
		}
		return bRet;
	}
}
