package org.vincentsaluzzo.lightrpc.common.security;

import javax.crypto.*;
import javax.crypto.spec.*;

/**
* This class provides crypt/decrypt method to encrypt and decrypt a string with a passphrase.
*/
public class RC4 {
	
	/**
	 * the passphrase used in the encryption/decryption
	 */
	String passphrase;
	
	/**
	 * Iniate the RC4 object with a passphrase
	 * @param pPassphrase passphrase used 
	 */
	public RC4(String pPassphrase) {
		this.passphrase = pPassphrase;
	}

	/**
	 * crypt a string into a byte array with the passphrase
	 * @param pStringToCrypt string to crypt
	 * @return the byte of array match to crypted string
	 * @throws Exception encryption exception
	 */
	public byte[] crypt(String pStringToCrypt) throws Exception{
		
		//String message=pStringToCrypt;


	    KeyGenerator kgen = KeyGenerator.getInstance("RC4");
	    kgen.init(168); // 192 and 256 bits may not be available

	    // Generate the secret key specs.
	    //SecretKey skey = kgen.generateKey();
	    //byte[] raw = skey.getEncoded();

	    //String key = new String(raw);
	    //System.out.println("Key use: " + key + " - Hex: " + asHex(raw));
	    SecretKeySpec skeySpec = new SecretKeySpec(this.passphrase.getBytes(), "RC4");


	    // Instantiate the cipher

	    Cipher cipher = Cipher.getInstance("RC4");

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


	    KeyGenerator kgen = KeyGenerator.getInstance("RC4");
	    kgen.init(168); // 192 and 256 bits may not be available

	    // Generate the secret key specs.
	    //SecretKey skey = kgen.generateKey();
	    //byte[] raw = skey.getEncoded();

	    //String key = new String(raw);
	    //System.out.println(key);
	    SecretKeySpec skeySpec = new SecretKeySpec(this.passphrase.getBytes(), "RC4");


	    // Instantiate the cipher

	    Cipher cipher = Cipher.getInstance("RC4");


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
  
  public static byte[] hexStringToByteArray(String s) {
	  int len = s.length();
	  byte[] data = new byte[len / 2];
	  for (int i = 0; i < len; i += 2) {
		  data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
				  + Character.digit(s.charAt(i+1), 16));
	  }
	  return data;
  }
/*
  public static void main(String[] args) throws Exception {

	  RC4 b = new RC4("123456789ABCDEFGHIJKLMNO");
	  //byte[] crypt = b.crypt("toto");
	  //System.out.println(new String(crypt));
	  //System.out.println(asHex(crypt));
	  byte[] toto = RC4.hexStringToByteArray("7CB2D1B549C2C3868978F2CAF9B2DEF892C2171DE10200223CB9E4413EA8DE6EB4E8AD367D60DD605B79D91F00713CBC03C2744B49231947601E18F0E1E3FD5EC67D9A3A8105CE9AE079C3775C44E0349AE9753F094EC006030D744489AE2065544C885709E4010A14C7D3CF21771D69ADE4");
	  String decrypt = b.decrypt(toto);
	  System.out.println(decrypt);
	  
  }
*/
}
