/**
 * 
 */
package org.mymmsc.j2ee.struts;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Action实体类访问路径映射
 * 
 * @author WangFeng
 * 
 */
@Target(ElementType.TYPE) //类，接口或enum
@Retention(RetentionPolicy.RUNTIME)
public @interface WebAction {
	/** url映射路径 */
	String url();
}
