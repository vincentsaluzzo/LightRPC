/**
 * 
 */
package org.vincentsaluzzo.lightrpc.common;

/**
 * @author vincentsaluzzo
 *
 */
public class LightRPCException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3933750492185699793L;
	
	private String message;
	
	public LightRPCException(String pMessage) {
		super();
		message = pMessage;
	}

	@Override
	public String getMessage() {
		return this.message;
	}
	
	
}
