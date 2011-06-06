/**
 * 
 */
package org.vincentsaluzzo.lightrpc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.AbstractHandler;
import org.vincentsaluzzo.lightrpc.common.LightRPCConfig;
import org.vincentsaluzzo.lightrpc.common.LightRPCException;
import org.vincentsaluzzo.lightrpc.common.LightRPCRequest;
import org.vincentsaluzzo.lightrpc.common.LightRPCResponse;
import org.vincentsaluzzo.lightrpc.common.security.Blowfish;

import sun.misc.IOUtils;


/**
 * This abstract class must be overridden to use this. 
 * You should implement the doMethod abstract method to override this class.
 * The doMethod method contains all the method of the LightRPC Server.
 * It is a Jetty Handler can be use directly in a Embedded Jetty HTTP server.
 * @author vincentsaluzzo
 */
public abstract class LightRPCHandler extends AbstractHandler {

	/**
	 * Configuration object for the server handler
	 */
	LightRPCConfig configuration;
	
	/**
	 * Construct a LightRPCHandler with a configuration object
	 * @param pConfiguration a LightRPC Configuration object
	 */
	public LightRPCHandler(LightRPCConfig pConfiguration) {
		this.configuration = pConfiguration;	
	}

	/* (non-Javadoc)
	 * @see org.mortbay.jetty.Handler#handle(java.lang.String, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, int)
	 */
	@Override
	final public void handle(String target, HttpServletRequest request,
			HttpServletResponse response, int dispatch) throws IOException,
			ServletException {
		response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        
        
        String bodyRequest = getBodyOfRequest(request);
		try {
	        LightRPCRequest lightrpcRequest = buildRequest(bodyRequest, this.configuration);
	        
	        String[]responseParameter = doMethod(lightrpcRequest.getMethodName(), lightrpcRequest.getParameterList().toArray(new String[]{}));
	        ArrayList<String> paramResponse = new ArrayList<String>();
	        for(String str : responseParameter) {
	        	paramResponse.add(str);
	        }
	        LightRPCResponse lightrpcResponse = new LightRPCResponse(lightrpcRequest.getMethodName(), "GoodResponse", paramResponse);
	        response.getWriter().println(buildResponse(lightrpcResponse, this.configuration));
		} catch (Exception e) {
			e.printStackTrace();
			String responseStr;
			try {
				responseStr = buildResponse(new LightRPCResponse("", "Exception", new String[]{e.getMessage()}), this.configuration);
				response.getWriter().println(responseStr);
			} catch (Exception e1) {
			}
		}
        
	    
        ((Request)request).setHandled(true);

	}

	/**
	 * This method is used to retrieve the body of a request
	 * @param request the request to use
	 * @return the body serialized to string
	 */
	final private String getBodyOfRequest(HttpServletRequest request) {

		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(
						inputStream));
				char[] charBuffer = new char[128];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				}
			}
		}
		String body = stringBuilder.toString();
		return body;
	}
	
	
	/**
	 * Build a serliazed response with a LightRPCResponse object and a configuration
	 * @param pResponse the response object to use for serialization
	 * @param pConfiguration the configuration object to use
	 * @return the serialized string
	 * @throws Exception a LightRPCException
	 */
	final private String buildResponse(LightRPCResponse pResponse, LightRPCConfig pConfiguration) throws Exception {
		Element racine = new Element("lightrpc");
		Document document = new Document(racine);
		
		Element header = new Element("header");
		racine.addContent(header);
		Element content = new Element("content");
		racine.addContent(content);
		
		if(pConfiguration.getSecurityEncryption() == true) {
			Element securityEncryption = new Element("security-encryption");
			securityEncryption.setText("true");
			header.addContent(securityEncryption);
			
			if(pConfiguration.getSecurityEncryptionType().equals(LightRPCConfig.SECURITY_ENCRYPTION_TYPE_BLOWFISH)) {
				Blowfish blowfish = new Blowfish(pConfiguration.getSecurityEncryptionPassphrase());
				String reqSerialized = pResponse.getXML();
				byte[] reqSerializedAndEncrypted = blowfish.crypt(reqSerialized);
				content.setText(Blowfish.asHex(reqSerializedAndEncrypted));		
			} else {
				throw new LightRPCException("Bad Encryption algorithm");
			}
		} else {
			SAXBuilder sxb = new SAXBuilder();
			String reqSerialized = pResponse.getXML();
			Document documentRequest;
		    StringReader sr = new StringReader(reqSerialized);
		    try
		    {
		    	documentRequest = sxb.build(sr);
		    } catch(Exception e) {
		    	throw new LightRPCException("Bad XML Format");
		    }

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
	 * Build a LightRPCRequest with a XML format serialized of a request
	 * @param pRequestSerialized the XML Format serialized of a request
	 * @param pConfiguration the configuration of server
	 * @return a LightRPCRequest which representing the client request
	 * @throws Exception a LightRPC Exception
	 */
	final private LightRPCRequest buildRequest(String pRequestSerialized, LightRPCConfig pConfiguration) throws Exception {
		SAXBuilder sxb = new SAXBuilder();
		Document document;
	    StringReader sr = new StringReader(pRequestSerialized);
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
	    	if(pConfiguration.getSecurityEncryptionType().equals(LightRPCConfig.SECURITY_ENCRYPTION_TYPE_BLOWFISH)) {
	    		Blowfish blowfish = new Blowfish(pConfiguration.getSecurityEncryptionPassphrase());
	    		byte[] responseEncrypted = Blowfish.hexStringToByteArray(content.getText());
	    		String responseDecrypted = blowfish.decrypt(responseEncrypted);
	    		LightRPCRequest request = new LightRPCRequest(responseDecrypted);
	    		return request;
	    	} else {
	    		throw new LightRPCException("Bad Security encryption algorithm used");
	    	}
	    } else {
	    	//the content are not encrypted
	    	XMLOutputter sortie = new XMLOutputter(Format.getPrettyFormat());
			StringWriter sw = new StringWriter();
			sortie.output(new Document((Element) content.getChild("request").clone()), sw);
			LightRPCRequest request = new LightRPCRequest(sw.toString());
			return request;
	    }
	}
	
	/**
	 * This method is abstract and it will be use to determine the method of the Web Services which client can use
	 * It must be overridden in the implementation of this class
	 * @param pName the method name send by the client
	 * @param pParameter the list of parameter send by the client in the request
	 * @return a list of parameter to return in a response to the client
	 */
	abstract public String[] doMethod(String pName, String[] pParameter);
	
	public static void main(String[] args) throws Exception {
		Server server = new Server(8080);
		
		final class myHandler extends LightRPCHandler {
			LightRPCConfig c;
			public myHandler(LightRPCConfig c) {
				super(c);
			}
			
			@Override
			public String[] doMethod(String pName, String[] pParameter) {
				if(pName.equals("myRequest")) {
					return new String[]{"toto"};
				}
				return null;
			}
			
			
		}
		
		LightRPCConfig c = new LightRPCConfig("http://localhost:8080");
		c.setSecurityEncryption(true);
		c.setSecurityEncryptionPassphrase("pass");
		c.setSecurityEncryptionType(LightRPCConfig.SECURITY_ENCRYPTION_TYPE_BLOWFISH);
		server.setHandler(new myHandler(c));
		server.start();
	}
}
