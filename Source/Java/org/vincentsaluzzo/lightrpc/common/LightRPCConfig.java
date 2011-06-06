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
	 * the address (URL) of the server
	 */
	String address;
	
	
	/**
	 * define if the content are encrypted or not
	 */
	Boolean securityEncryption;
	
	/**
	 * define the algorithm encryption used to encrypt the content
	 */
	String securityEncryptionType;
	
	/**
	 * define the passphrase for the security encryption used in encryption/decryption process
	 */
	String securityEncryptionPassphrase;

	/**
	 * Construct a LightRPCConfig object with two parameters
	 * @param pAddress the address of the server (URL) 
	 */
	public LightRPCConfig(String pAddress) {
		this.address = pAddress;
		
		this.securityEncryption = false;
		this.securityEncryptionType = "";
		this.securityEncryptionPassphrase = "";
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

	/**
	 * @return the securityEncryptionPassphrase
	 */
	public String getSecurityEncryptionPassphrase() {
		return securityEncryptionPassphrase;
	}

	/**
	 * @param securityEncryptionPassphrase the securityEncryptionPassphrase to set
	 */
	public void setSecurityEncryptionPassphrase(String securityEncryptionPassphrase) {
		this.securityEncryptionPassphrase = securityEncryptionPassphrase;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	
	
}
