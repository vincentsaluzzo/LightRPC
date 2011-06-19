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
	 * Constant used to define the security encryption to 3DES
	 */
	static public String SECURITY_ENCRYPTION_TYPE_3DES = "3DES";

	/**
	 * Constant used to define the security encryption to AES 256
	 */
	static public String SECURITY_ENCRYPTION_TYPE_AES256 = "AES256";
	
	/**
	 * the address (URL) of the server
	 */
	private String address;
	
	
	/**
	 * define if the content are encrypted or not
	 */
	private Boolean securityEncryption;
	
	/**
	 * define the algorithm encryption used to encrypt the content
	 */
	private String securityEncryptionType;
	
	/**
	 * define the passphrase for the security encryption used in encryption/decryption process
	 */
	private String securityEncryptionPassphrase;

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
	 * @return the securityEncryptionType
	 */
	public String getSecurityEncryptionType() {
		return securityEncryptionType;
	}
	/**
	 * @return the securityEncryptionPassphrase
	 */
	public String getSecurityEncryptionPassphrase() {
		return securityEncryptionPassphrase;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}
	
	public void addAESSecurityEncryption(String pPassphrase) throws Exception {
		if(pPassphrase.length() == 16) {
			this.securityEncryption = true;
			this.securityEncryptionPassphrase = pPassphrase;
			this.securityEncryptionType = SECURITY_ENCRYPTION_TYPE_AES256;
		} else {
			throw new Exception("InvalidPassphrase, the length of AES passphrase must be 16 characters.");
		}
	}
	
	public void add3DESSecurityEncryption(String pPassphrase) throws Exception {
		if(pPassphrase.length() == 24) {
			this.securityEncryption = true;
			this.securityEncryptionPassphrase = pPassphrase;
			this.securityEncryptionType = SECURITY_ENCRYPTION_TYPE_3DES;
		} else {
			throw new Exception("InvalidPassphrase, the length of 3DES passphrase must be 24 characters.");
		}
	}
	
	public void removeSecurityEncryption() {
		this.securityEncryption = false;
		this.securityEncryptionPassphrase = null;
		this.securityEncryptionType = null;
	}
}
