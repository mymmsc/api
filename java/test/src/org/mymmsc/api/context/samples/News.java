/**
 * 
 */
package org.mymmsc.api.context.samples;

import org.mymmsc.api.context.JsonAdapter;

/**
 * 图文新闻类
 * 
 * @author WangFeng
 * 
 */
public class News {
	private String title = null;
	private String image = null;
	private String content = null;

	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * @param title
	 *            the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @return the image
	 */
	public String getImage() {
		return image;
	}

	/**
	 * @param image
	 *            the image to set
	 */
	public void setImage(String image) {
		this.image = image;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content
	 *            the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		String sRet = null;
		sRet = JsonAdapter.get(this);
		return sRet;
	}
}
