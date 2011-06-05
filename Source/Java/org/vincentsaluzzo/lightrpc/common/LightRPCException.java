/**
 * 
 */
package org.vincentsaluzzo.lightrpc.common;

/**
 * @author vincentsaluzzo
 *
 */
public class LightRPCException extends Exception {

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
