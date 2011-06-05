package org.vincentsaluzzo.lightrpc.common.security;

import java.security.*;

import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;

/**
* This class provides crypt/decrypt method to encrypt and decrypt a string with a passphrase.
*/
public class Blowfish {
	
	/**
	 * the passphrase used in the encryption/decryption
	 */
	String passphrase;
	
	/**
	 * Iniate the Blowfish object with a passphrase
	 * @param pPassphrase passphrase used 
	 */
	public Blowfish(String pPassphrase) {
		this.passphrase = pPassphrase;
	}

	/**
	 * crypt a string into a byte array with the passphrase
	 * @param pStringToCrypt string to crypt
	 * @return the byte of array match to crypted string
	 * @throws Exception encryption exception
	 */
	public byte[] crypt(String pStringToCrypt) throws Exception{
		
		String message=pStringToCrypt;


	    KeyGenerator kgen = KeyGenerator.getInstance("Blowfish");
	    kgen.init(128); // 192 and 256 bits may not be available

	    // Generate the secret key specs.
	    SecretKey skey = kgen.generateKey();
	    byte[] raw = skey.getEncoded();

	    String key = new String(raw);
	    //System.out.println(key);
	    SecretKeySpec skeySpec = new SecretKeySpec(this.passphrase.getBytes(), "Blowfish");


	    // Instantiate the cipher

	    Cipher cipher = Cipher.getInstance("Blowfish");

	    cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

	    byte[] encrypted =
	      cipher.doFinal(pStringToCrypt.getBytes());
	   // System.out.println("encrypted string: " + asHex(encrypted));

		
		
		return encrypted;
		
	}
	
	/**
	 * decrypt a byte array into a string with the passphrase
	 * @return the string match to original string before crypting
	 * @throws Exception decryption exception
	 * @param pCryptedStringtoDecrypt byte array to decrypt
	 * 
	 */
	public String decrypt(byte[] pCryptedStringtoDecrypt) throws Exception{


	    KeyGenerator kgen = KeyGenerator.getInstance("Blowfish");
	    kgen.init(128); // 192 and 256 bits may not be available

	    // Generate the secret key specs.
	    SecretKey skey = kgen.generateKey();
	    byte[] raw = skey.getEncoded();

	    String key = new String(raw);
	    //System.out.println(key);
	    SecretKeySpec skeySpec = new SecretKeySpec(this.passphrase.getBytes(), "Blowfish");


	    // Instantiate the cipher

	    Cipher cipher = Cipher.getInstance("Blowfish");


	    byte[] encrypted = pCryptedStringtoDecrypt;

	    cipher.init(Cipher.DECRYPT_MODE, skeySpec);
	    byte[] original =
	      cipher.doFinal(encrypted);
	    String originalString = new String(original);
	    /*System.out.println("Original string: " +
	      originalString + " " + asHex(original));*/
		
		
		return originalString;
	}
	
  /**
  * Turns array of bytes into string
  *
  * @param buf	Array of bytes to convert to hex string
  * @return	Generated hex string
  */
  public static String asHex (byte buf[]) {
   StringBuffer strbuf = new StringBuffer(buf.length * 2);
   int i;

   for (i = 0; i < buf.length; i++) {
    if (((int) buf[i] & 0xff) < 0x10)
	    strbuf.append("0");

    strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
   }

   return strbuf.toString();
  }

  public static void main(String[] args) throws Exception {

	  Blowfish b = new Blowfish("test");
	  byte[] crypt = b.crypt("This is just an example");
	  System.out.println(asHex(crypt));
	  String decrypt = b.decrypt(crypt);
	  System.out.println(decrypt);
	  
  }
}
