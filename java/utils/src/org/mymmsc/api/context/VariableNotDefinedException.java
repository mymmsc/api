/**
 * 
 */
package org.mymmsc.api.context;

/**
 * Thrown when {@link Templator#setVariable(String, String, boolean)
 * Minitemplator.setVariable} is called with a <code>variableName</code> that is
 * not defined within the template and the <code>isOptional</code> parameter is
 * <code>false</code>.
 */
public class VariableNotDefinedException extends RuntimeException {
	private static final long serialVersionUID = 1;

	public VariableNotDefinedException(String variableName) {
		super("Variable \"" + variableName + "\" not defined in template.");
	}
}
