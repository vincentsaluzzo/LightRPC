/**
 * 
 */
package org.vincentsaluzzo.lightrpc.common;




/**
 * @author vincentsaluzzo
 *
 */
public class LightRPCConfig {
	
	/**
	 * Constant used to define the security encryption to Blowfish
	 */
	static public String SECURITY_ENCRYPTION_TYPE_BLOWFISH = "Blowfish";

	/**
	 * the address (URL) of the server without the port
	 */
	String address;
	
	/**
	 * the port of the server
	 */
	String port;
	
	/**
	 * define if the content are encrypted or not
	 */
	Boolean securityEncryption;
	
	/**
	 * define the algorithm encryption used to encrypt the content
	 */
	String securityEncryptionType;
	
	

	/**
	 * Construct a LightRPCConfig object with two parameters
	 * @param pAddress the address of the server (URL) without the port number
	 * @param pPort the port number of the server
	 */
	public LightRPCConfig(String pAddress, String pPort) {
		this.address = pAddress;
		this.port = pPort;
	}
	
	/**
	 * @return the securityEncryption
	 */
	public Boolean getSecurityEncryption() {
		return securityEncryption;
	}

	/**
	 * @param securityEncryption the securityEncryption to set
	 */
	public void setSecurityEncryption(Boolean securityEncryption) {
		this.securityEncryption = securityEncryption;
	}

	/**
	 * @return the securityEncryptionType
	 */
	public String getSecurityEncryptionType() {
		return securityEncryptionType;
	}

	/**
	 * @param securityEncryptionType the securityEncryptionType to set
	 */
	public void setSecurityEncryptionType(String securityEncryptionType) {
		this.securityEncryptionType = securityEncryptionType;
	}
	
	
}
