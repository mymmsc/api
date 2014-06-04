/**
 * 
 */
package org.mymmsc.api.context;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Set;

/**
 * Specifies the parameters for constructing a {@link Templator} object.
 * 
 * @author WangFeng
 */
public class TemplateSpecification { // template specification
	/**
	 * The file name of the template file.
	 */
	public String templateFileName;
	public InputStream is;
	/**
	 * The path of the base directory for reading subtemplate files. This path
	 * is used to convert the relative paths of subtemplate files (specified
	 * with the include commands) into absolute paths. If this field is null,
	 * the parent directory of the main template file (specified by
	 * <code>templateFileName</code>) is used.
	 */
	public String subtemplateBasePath;
	/**
	 * The character set to be used for reading and writing files. This charset
	 * is used for reading the template and subtemplate files and for writing
	 * output with {@link #generateOutput(String outputFileName)} . If this
	 * field is null, the default charset of the Java VM is used.
	 */
	public Charset charset;
	/**
	 * The contents of the template file. This field may be used instead of
	 * <code>templateFileName</code> to pass the template text in memory. If
	 * this field is not null, <code>templateFileName</code> will be ignored.
	 */
	public String templateText;
	/**
	 * Flags for the conditional commands (if, elseIf). A set of flag names,
	 * that can be used with the if and elseIf commands. The flag names are
	 * case-insensitive.
	 */
	public Set<String> conditionFlags;
}
