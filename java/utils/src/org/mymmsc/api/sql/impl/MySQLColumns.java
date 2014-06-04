/**
 * 
 */
package org.mymmsc.api.sql.impl;

/**
 * 数据表字段信息
 * 
 * @author WangFeng
 * @remark show full columns from order_channel
 */
public class MySQLColumns {
	/** 字段名 */
	private String field;
	/** 字段类型 */
	private String type;
	/** 字符集 */
	private String collation;
	/** 是否为空 */
	private String NULL;
	/** 是否索引 */
	private String key;
	/** 默认值 */
	private String Default;
	/** 额外的属性 */
	private String extra;
	/** 权限 */
	private String privileges;
	/** 注解 */
	private String comment;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCollation() {
		return collation;
	}

	public void setCollation(String collation) {
		this.collation = collation;
	}

	public String getNULL() {
		return NULL;
	}

	public void setNULL(String nULL) {
		NULL = nULL;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getDefault() {
		return Default;
	}

	public void setDefault(String default1) {
		Default = default1;
	}

	public String getExtra() {
		return extra;
	}

	public void setExtra(String extra) {
		this.extra = extra;
	}

	public String getPrivileges() {
		return privileges;
	}

	public void setPrivileges(String privileges) {
		this.privileges = privileges;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
