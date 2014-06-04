/**
 * 
 */
package org.mymmsc.j2ee.http;

/**
 * @author wangfeng
 * 
 */
public class HttpFile {
	/** 表单对象字段名 */
	private String fieldName;
	/** 文件名 */
	private String fileName;
	/** 文件类型 */
	private String fileType;
	/** 文件数据 */
	private byte[] fileData;

	/**
	 * @return the fieldName
	 */
	public String getFieldName() {
		return fieldName;
	}

	/**
	 * @param fieldName
	 *            the fieldName to set
	 */
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	/**
	 * @return the fileName
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * @param fileName
	 *            the fileName to set
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	/**
	 * @return the fileType
	 */
	public String getFileType() {
		return fileType;
	}

	public String fileType() {
		String sRet = null;
		if (fileType != null) {
			String[] a = fileType.split("\\/");
			if (a.length == 1) {
				sRet = a[0].trim();
			} else if (a.length > 1) {
				sRet = a[1].trim();
			}
		}
		if (sRet.toLowerCase().startsWith("octet-stream")) {
			// 这个是二进制文件, 暂时只有取扩展名了
			int pos = fileName.lastIndexOf('.');
			sRet = fileName.substring(pos);
		}
		return sRet.toLowerCase();
	}

	/**
	 * @param fileType
	 *            the fileType to set
	 */
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	/**
	 * @return the fileData
	 */
	public byte[] getFileData() {
		return fileData;
	}

	/**
	 * @param fileData
	 *            the fileData to set
	 */
	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}
}
