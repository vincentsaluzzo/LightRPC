package org.vincentsaluzzo.lightrpc.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import org.vincentsaluzzo.lightrpc.common.LightRPCConfig;
import org.vincentsaluzzo.lightrpc.common.LightRPCException;
import org.vincentsaluzzo.lightrpc.common.LightRPCResponse;
import org.vincentsaluzzo.lightrpc.common.LightRPCRequest;
import org.vincentsaluzzo.lightrpc.common.security.AES256;
import org.vincentsaluzzo.lightrpc.common.security.TripleDES;
import org.vincentsaluzzo.lightrpc.common.security.Blowfish;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * @author vincentsaluzzo
 *
 */
public class LightRPCClient {

	/**
	 * the LightRPC Configuration object for this communication
	 */
	private LightRPCConfig configuration;
	
	/**
	 * the last request send to the server
	 */
	private LightRPCRequest lastRequest;
	
	/**
	 * the last response receive from the server
	 */
	private LightRPCResponse lastResponse;
	
	/**
	 * Construct a LightRPC client with a configuration object (LightRPCConfig)
	 * @param pConfiguration
	 */
	public LightRPCClient(LightRPCConfig pConfiguration) {
		configuration = pConfiguration;
	}
	
	/**
	 * Execute a request to the server and return the response of this
	 * @param pRequest a LightRPCRequest object representing the request to send to the server
	 * @return a LightRPCResponse object representing the response of the server
	 */
	public LightRPCResponse execute(LightRPCRequest pRequest) {
		try {
			String request = buildRequest(pRequest);
			System.out.println(request);
			
			URL url = new URL(this.configuration.getAddress());
			HttpURLConnection conn =  (HttpURLConnection)url.openConnection();
			conn.setDoOutput(true);
			
			OutputStreamWriter out = new OutputStreamWriter(
                    conn.getOutputStream());
			out.write(request);
			out.close();

			BufferedReader in = new BufferedReader(
					new InputStreamReader(
							conn.getInputStream()));
		
			String decodedString;
			String appendedString = "";
			while ((decodedString = in.readLine()) != null) {
				System.out.println(decodedString);
				appendedString += decodedString;
			}
			in.close();
			LightRPCResponse response = buildResponse(appendedString);
			this.lastRequest = pRequest;
			this.lastResponse = response;
			return response;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * build the request into a string to send them to a server
	 * @param pRequest the LightRPCRequest object to serialize
	 * @return the xml format serialized to send to the server
	 * @throws Exception a lightrpc exception
	 */
	private String buildRequest(LightRPCRequest pRequest) throws Exception {
		Element racine = new Element("lightrpc");
		Document document = new Document(racine);
		
		Element header = new Element("header");
		racine.addContent(header);
		Element content = new Element("content");
		racine.addContent(content);
		
		if(this.configuration.getSecurityEncryption() == true) {
			Element securityEncryption = new Element("security-encryption");
			securityEncryption.setText("true");
			header.addContent(securityEncryption);
			
			if(this.configuration.getSecurityEncryptionType().equals(LightRPCConfig.SECURITY_ENCRYPTION_TYPE_BLOWFISH)) {
				Blowfish blowfish = new Blowfish(this.configuration.getSecurityEncryptionPassphrase());
				String reqSerialized = pRequest.getXML();
				byte[] reqSerializedAndEncrypted = blowfish.crypt(reqSerialized);
				content.setText(Blowfish.asHex(reqSerializedAndEncrypted));		
			} else if(this.configuration.getSecurityEncryptionType().equals(LightRPCConfig.SECURITY_ENCRYPTION_TYPE_3DES)) {
				TripleDES aes = new TripleDES(this.configuration.getSecurityEncryptionPassphrase());
				String reqSerialized = pRequest.getXML();
				byte[] reqSerializedAndEncrypted = aes.crypt(reqSerialized);
				content.setText(TripleDES.asHex(reqSerializedAndEncrypted));		
			} else if(this.configuration.getSecurityEncryptionType().equals(LightRPCConfig.SECURITY_ENCRYPTION_TYPE_AES256)) {
				AES256 aes = new AES256(this.configuration.getSecurityEncryptionPassphrase());
				String reqSerialized = pRequest.getXML();
				byte[] reqSerializedAndEncrypted = aes.crypt(reqSerialized);
				content.setText(AES256.asHex(reqSerializedAndEncrypted));		
			} else {
				throw new LightRPCException("Bad Encryption algorithm");
			}
		} else {
			SAXBuilder sxb = new SAXBuilder();
			String reqSerialized = pRequest.getXML();
			Document documentRequest;
		    StringReader sr = new StringReader(reqSerialized);
		    try
		    {
		    	//On crée un nouveau document JDOM avec en argument le fichier XML
		    	//Le parsing est terminé ;)
		    	documentRequest = sxb.build(sr);
		    } catch(Exception e) {
		    	throw new LightRPCException("Bad XML Format");
		    }

		    //On initialise un nouvel élément racine avec l'élément racine du document.
		    Element racineRequest = documentRequest.getRootElement();
		    content.addContent((Element)racineRequest.clone());
		}
		
		try {
			XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			StringWriter sw = new StringWriter();
			sortie.output(document, sw);
			return sw.toString();
		} catch (java.io.IOException e) {
			return null;
		}
	}

	/**
	 * build the LightRPCResponse object from the XML format serialized of the response sended by the server
	 * @param pResponseSerialized the XML format serialized string send by the server
	 * @return the LightRPCResponse object representation of the response of the server
	 * @throws Exception a LightRPC Exception
	 */
	private LightRPCResponse buildResponse(String pResponseSerialized) throws Exception {
		SAXBuilder sxb = new SAXBuilder();
		Document document;
	    StringReader sr = new StringReader(pResponseSerialized);
	    try
	    {
	    	//On crée un nouveau document JDOM avec en argument le fichier XML
	    	//Le parsing est terminé ;)
	    	document = sxb.build(sr);
	    } catch(Exception e) {
	    	throw new LightRPCException("Bad XML Format");
	    }

	    //On initialise un nouvel élément racine avec l'élément racine du document.
	    Element racine = document.getRootElement();
	    if(!racine.getName().equals("lightrpc")) {
	    	throw new LightRPCException("Not a LightRPC response");
	    }
	    
	    Element header = racine.getChild("header");
	    if(header == null) {
	    	throw new LightRPCException("Bad format Exception: missing 'header' element");
	    }
	    Element content = racine.getChild("content");
	    if(content == null) {
	    	throw new LightRPCException("Bad format Exception: missing 'content' element");
	    }
	    
	    
	    Element securityEncryption = header.getChild("security-encryption");
	    if(securityEncryption != null && securityEncryption.getText().equals("true")) {
	    	//the content are encrypted
	    	if(this.configuration.getSecurityEncryptionType().equals(LightRPCConfig.SECURITY_ENCRYPTION_TYPE_BLOWFISH)) {
	    		Blowfish blowfish = new Blowfish(this.configuration.getSecurityEncryptionPassphrase());
	    		byte[] responseEncrypted = Blowfish.hexStringToByteArray(content.getText());
	    		String responseDecrypted = blowfish.decrypt(responseEncrypted);
	    		LightRPCResponse response = new LightRPCResponse(responseDecrypted);
	    		return response;
	    	} else if(this.configuration.getSecurityEncryptionType().equals(LightRPCConfig.SECURITY_ENCRYPTION_TYPE_3DES)) {
	    		TripleDES aes = new TripleDES(this.configuration.getSecurityEncryptionPassphrase());
	    		byte[] responseEncrypted = TripleDES.hexStringToByteArray(content.getText());
	    		String responseDecrypted = aes.decrypt(responseEncrypted);
	    		LightRPCResponse response = new LightRPCResponse(responseDecrypted);
	    		return response;
	    	} else if(this.configuration.getSecurityEncryptionType().equals(LightRPCConfig.SECURITY_ENCRYPTION_TYPE_AES256)) {
	    		AES256 aes = new AES256(this.configuration.getSecurityEncryptionPassphrase());
	    		byte[] responseEncrypted = AES256.hexStringToByteArray(content.getText());
	    		String responseDecrypted = aes.decrypt(responseEncrypted);
	    		LightRPCResponse response = new LightRPCResponse(responseDecrypted);
	    		return response;
	    	} else {
	    		throw new LightRPCException("Bad Security encryption algorithm used");
	    	}
	    } else {
	    	//the content are not encrypted
	    	XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			StringWriter sw = new StringWriter();
			sortie.output(new Document((Element) content.getChild("response").clone()), sw);
			LightRPCResponse response = new LightRPCResponse(sw.toString());
			return response;
	    }
	}
	
	/**
	 * Get the last request sended by the client
	 * @return the lastRequest
	 */
	public LightRPCRequest getLastRequest() {
		return lastRequest;
	}

	/**
	 * Get the last response receive by the client
	 * @return the lastResponse
	 */
	public LightRPCResponse getLastResponse() {
		return lastResponse;
	}
	
	/*
	public static void main(String[] args) throws IOException {
		
		LightRPCConfig c = new LightRPCConfig("http://localhost:8080");
		try {
			//c.add3DESSecurityEncryption("123456789ABCDEFGHIJKLMNO");
			c.addAESSecurityEncryption("passpasspasspass");
			LightRPCClient client = new LightRPCClient(c);
			LightRPCRequest req = new LightRPCRequest("sayHello", new String[]{});
			LightRPCResponse rep = client.execute(req);
			System.out.println(rep.getParameterList().get(0));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}*/
	
	
}
