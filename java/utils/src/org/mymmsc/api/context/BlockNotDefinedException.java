/**
 * 
 */
package org.mymmsc.api.context;

/**
 * Thrown when {@link Templator#addBlock Minitemplator.addBlock} is called with
 * a <code>blockName</code> that is not defined within the template.
 * 
 * @author WangFeng
 */
public class BlockNotDefinedException extends RuntimeException {
	private static final long serialVersionUID = 1;

	public BlockNotDefinedException(String blockName) {
		super("Block \"" + blockName + "\" not defined in template.");
	}
}
