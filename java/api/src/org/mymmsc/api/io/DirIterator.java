/**
 * 
 */
package org.mymmsc.api.io;

import java.io.File;
import java.util.ArrayList;

/**
 * 目录扫描
 * 
 * @author WangFeng
 * 
 */
public abstract class DirIterator {
	/** 目录级数, 0代表初始目录, 1代表二级 */
	private int level = -1;
	/** 文件列表 */
	private ArrayList<String> filelist = new ArrayList<String>();

	/**
	 * 是否能进行目录深度扫描
	 * 
	 * @return
	 */
	protected abstract boolean canDirProcess();

	/**
	 * 是否进行文件遍历
	 * 
	 * @return
	 */
	protected abstract boolean canFileProcess();

	/**
	 * 目录处理
	 * 
	 * @param dir
	 *            目录对象
	 * @remark 不能在此方法中再次遍历目录
	 */
	protected abstract void dirProcess(File dir);

	/**
	 * 文件处理
	 * 
	 * @param file
	 *            文件对象
	 * @remark 具体文件的处理
	 */
	protected abstract void fileProcess(File file);

	/**
	 * 扫描目录
	 * 
	 * @param filepath
	 * @return 如果不是目录, 则返回false
	 * @remark 这里没有对filepath是否null进行检查
	 */
	public boolean scan(String filepath) {
		boolean bRet = true;
		File dir = new File(filepath);
		if (!dir.isDirectory()) {
			bRet = false;
		} else {
			// 目录级数加1
			level += 1;
			dirProcess(dir);
			File[] files = dir.listFiles();
			if (files != null) {
				File file = null;
				for (int i = 0; i < files.length; i++) {
					file = files[i];
					if (file.isDirectory()) {
						if (canDirProcess()) {
							scan(file.getAbsolutePath());
						}
					} else if (file.isFile()) {
						if (canFileProcess()) {
							filelist.add(file.getAbsolutePath());
							fileProcess(file);
						}
					}
				}
			}
		}

		return bRet;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * @param level
	 *            the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}
}
