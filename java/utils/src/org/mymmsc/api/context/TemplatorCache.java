/**
 * @(#)TemplatorCache.java	6.3.9 09/10/02
 *
 * Copyright 2000-2010 MyMMSC Software Foundation (MSF), Inc. All rights reserved.
 * MyMMSC PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.mymmsc.api.context;

import java.io.IOException;
import java.util.HashMap;

/**
* A cache manager for MiniTemplator objects.
* This class is used to cache MiniTemplator objects in memory, so that
* each template file is only read and parsed once.
*
* <p>
* Home page: <a href="http://www.source-code.biz/MiniTemplator">www.source-code.biz/MiniTemplator</a><br>
* Author: Christian d'Heureuse, Inventec Informatik AG, Zurich, Switzerland<br>
* Multi-licensed: EPL/LGPL.
*
* <p>
* Version history:</p>
* <table cellpadding=3 cellspacing=0 border=1><tbody style="vertical-align:top">
* <tr><td>2004-11-06<td>chdh<td> Module created.
* <tr><td>2004-11-07<td>chdh<td> Method "clear" added.<br>
* <tr><td>2006-07-07<td>chdh<td> Extended constructor with <code>charset</code> argument added.
* <tr><td>2007-05-19<td>chdh<td><ul style="margin-top:0; margin-bottom:0">
*  <li>Second constructor removed.
*  <li>Parameters of get() method changed.
*  </ul>
* </tbody></table>
*/
public class TemplatorCache {

	private HashMap<String, Templator> cache; // buffered MiniTemplator objects

	/**
	 * Creates a new MiniTemplatorCache object.
	 */
	public TemplatorCache() {
		cache = new HashMap<String, Templator>();
	}

	/**
	 * Returns a cloned MiniTemplator object from the cache. If there is not yet
	 * a MiniTemplator object with the specified <code>templateFileName</code>
	 * in the cache, a new MiniTemplator object is created and stored in the
	 * cache. Then the cached MiniTemplator object is cloned and the clone
	 * object is returned.
	 * 
	 * @param templateSpec
	 *            the template specification.
	 * @return a cloned and reset MiniTemplator object.
	 */
	public synchronized Templator get(TemplateSpecification templateSpec)
			throws IOException, TemplateSyntaxException {
		String key = generateCacheKey(templateSpec);
		Templator mt = cache.get(key);
		if (mt == null) {
			mt = new Templator(templateSpec);
			cache.put(key, mt);
		}
		return mt.cloneReset();
	}

	private static String generateCacheKey(TemplateSpecification templateSpec) {
		StringBuilder key = new StringBuilder(128);
		if (templateSpec.templateText != null)
			key.append(templateSpec.templateText);
		else if (templateSpec.templateFileName != null)
			key.append(templateSpec.templateFileName);
		else
			throw new IllegalArgumentException(
					"No templateFileName or templateText specified.");
		if (templateSpec.conditionFlags != null) {
			for (String flag : templateSpec.conditionFlags) {
				key.append('|');
				key.append(flag.toUpperCase());
			}
		}
		return key.toString();
	}

	/**
	 * Clears the cache.
	 */
	public synchronized void clear() {
		cache.clear();
	}

} // end class MiniTemplatorCache
