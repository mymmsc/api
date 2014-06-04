/**
 * 
 */
package org.mymmsc.api.context;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;

import org.mymmsc.api.assembly.Api;

/**
 * 类成员变量的遍历操作
 * 
 * @author WangFeng
 * @remark 遍历类的每一个成员变量
 * @remark Traversal, Traverse
 */
public abstract class ClassTraverser {

	/**
	 * 向聚合型字段传递字段援引路径
	 * 
	 * @param prefix
	 *            字段援引路径, 此处以各级类的字段名的联级表达式
	 * @param name
	 *            当前字段名
	 * @return
	 */
	protected abstract String subpath(String prefix, String name);
	
	/**
	 * 操作单个字段
	 * 
	 * @param prefix
	 *            字段援引路径, 此处以各级类的字段名的联级表达式
	 * @param name
	 *            当前字段名
	 * @param value
	 *            字符串类型的值
	 */
	protected abstract void operate(String prefix, String name, String value);

	/**
	 * 操作列表
	 * 
	 * @param prefix
	 *            字段援引路径, 此处以各级类的字段名的联级表达式
	 * @param name
	 *            当前字段名
	 */
	protected abstract void operateList(String prefix, String name);

	/**
	 * 遍历List对象
	 * 
	 * @param prefix
	 * @param list
	 */
	public void transitList(String prefix, List<?> list) {
		if (list != null) {
			int count = list.size();
			for (int i = 0; i < count; i++) {
				Object obj = list.get(i);
				if (obj != null) {
					transit(prefix, obj);
				}
			}
			operateList(prefix, null);
		}
	}

	/**
	 * 遍历数组
	 * 
	 * @param prefix
	 * @param array
	 */
	public void transitArray(String prefix, Object array) {
		if (array != null) {
			int count = Array.getLength(array);
			for (int i = 0; i < count; i++) {
				Object obj = Array.get(array, i);
				if (obj != null) {
					transit(prefix, obj);
				}
			} // end for
			operateList(prefix, null);
		}
	}

	/**
	 * 遍历对象
	 * 
	 * @param prefix
	 *            字段援引路径, 此处以各级类的字段名的联级表达式
	 * @param obj
	 *            对象
	 * @return
	 */
	public void transit(String prefix, Object obj) {
		// 取得clazz类的成员变量列表
		Class<?> clazz = null;
		Field[] fields = null;
		Field field = null;
		Class<?> fClass = null;
		String fName = null;
		Object fValue = null;
		boolean isAccessible = false;
		// 接口传递参数
		String name = null;
		String value = null;
		// 遍历开始
		while (obj != null) {
			if (clazz == null) {
				clazz = obj.getClass();
			} else {
				clazz = clazz.getSuperclass();
			}
			// 这个判断可以优化
			if (Api.isBaseType(clazz) && clazz != java.lang.Object.class) {
				if (prefix != null) {
					// 如果object是基本数据类型, 则赋予关键字value
					value = Api.toString(obj);
					operate(prefix, "value", value);
				}
				break;
			}
			// 获取字段列表
			fields = clazz.getDeclaredFields();
			for (int j = 0; j < fields.length; j++) {
				field = fields[j];
				fClass = field.getType();
				fName = field.getName().trim();
				name = fName;
				value = null;
				// 跳过序列化的特定字段
				if (fName.equalsIgnoreCase("serialVersionUID")) {
					continue;
				}
				// 获得存取权限
				isAccessible = field.isAccessible();
				try {
					field.setAccessible(true);
					fValue = field.get(obj);
					// 如果是基本数据类型
					if (fValue == null && Api.isBaseType(fClass)) {
						// 如果字段为null, 则按照class赋予默认值
						fValue = Api.valueOf(fClass, "");
					}
					if (fClass == List.class) {
						transitList(subpath(prefix, name), (List<?>) fValue);
					} else if (fClass.isArray()) {
						transitArray(subpath(prefix, name), fValue);
					} else if(fValue != null && !Api.isBaseType(fClass)) {
						transit(subpath(prefix, name), fValue);
					} else {
						value = Api.toString(fValue);
						operate(prefix, name, value);
					}
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} finally {
					// 恢复存取之前的类字段存取状态
					field.setAccessible(isAccessible);
				}
			} // end for
			//operateList(prefix, null);
		}
		// end while
		if (prefix != null) {
			operateList(prefix, null);
		}
	}
}
