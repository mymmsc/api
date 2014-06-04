/**
 * 
 */
package org.mymmsc.api.context;


/**
 * 模版迭代器
 * 
 * @author WangFeng
 * @param <E>
 * 
 */
public class TemplateIterator extends ClassTraverser{

	/** 模版实例 */
	private Templator tpl = null;
	
	public TemplateIterator(Templator template) {
		this.tpl = template;
	}
	
	@Override
	protected void operate(String prefix, String name, String value) {
		String key = subpath(prefix, name);
		tpl.setVariableQuietly(key, value);
	}

	@Override
	protected void operateList(String prefix, String name) {
		tpl.addBlock(prefix, true);
	}

	@Override
	protected String subpath(String prefix, String name) {
		String key = prefix;
		if (key != null) {
			key += '.' + name; 
		} else {
			key = name;
		}
		return key;
	}
	
}
