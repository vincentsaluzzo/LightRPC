package org.vincentsaluzzo.lightrpc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.vincentsaluzzo.lightrpc.common.LightRPCConfig;
import org.vincentsaluzzo.lightrpc.common.LightRPCException;
import org.vincentsaluzzo.lightrpc.common.LightRPCRequest;
import org.vincentsaluzzo.lightrpc.common.LightRPCResponse;
import org.vincentsaluzzo.lightrpc.common.security.AES256;
import org.vincentsaluzzo.lightrpc.common.security.Blowfish;
import org.vincentsaluzzo.lightrpc.common.security.TripleDES;

public class LightRPCServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2138423905531766584L;

	/**
	 * Configuration object for the server handler
	 */
	LightRPCConfig configuration;
	
	/**
	 * List of Object use at End Point of Web Services
	 */
	private ArrayList<Object> implementorList;
	
	/**
	 * Construct a LightRPCHandler with a configuration object
	 * @param pConfiguration a LightRPC Configuration object
	 * @throws LightRPCException 
	 */
	public LightRPCServlet(LightRPCConfig pConfiguration, Object implementor) throws ClassNotFoundException, LightRPCException {
		this.configuration = pConfiguration;
		
		this.implementorList = new ArrayList<Object>();
		implementorList.add(implementor);
		System.out.println(this.implementorList.get(0).getClass().getSimpleName());
		Class classe = Class.forName(implementor.getClass().getName());
		Method[] listOfMethodImplemented = classe.getDeclaredMethods();
		
		for(Method m : listOfMethodImplemented) {
			if(m.getReturnType() != String.class && m.getReturnType() != String[].class && m.getReturnType() != void.class) {
				if(m.getGenericReturnType() instanceof ParameterizedType) {
					ParameterizedType type = (ParameterizedType) m.getGenericReturnType();
				    if(!haveAvailableGenericType(type)) {
				    	throw new LightRPCException("Bad Type implemented in LightRPC Server method: Only String or different type of String array are accepted");
				    }
				} else {
					throw new LightRPCException("Bad Type implemented in LightRPC Server method: Only String or different type of String array are accepted");
				}
			}
			
			for(int i = 0; i < m.getParameterTypes().length; i++) {
				if(m.getParameterTypes()[i] != String.class && m.getReturnType() != String[].class) {
					if(m.getGenericParameterTypes()[i] instanceof ParameterizedType) {
						ParameterizedType type = (ParameterizedType) m.getGenericParameterTypes()[i];
						if(!haveAvailableGenericType(type)) {
					    	throw new LightRPCException("Bad Type implemented in LightRPC Server method: Only String or different type of String array are accepted");
					    }
					} else {
						throw new LightRPCException("Bad Type implemented in LightRPC Server method: Only String or different type of String array are accepted");
					}
				}
			}
		}
	}
	
	/**
	 * Construct a LightRPCHandler with a configuration object
	 * @param pConfiguration a LightRPC Configuration object
	 * @throws LightRPCException 
	 */
	public LightRPCServlet(LightRPCConfig pConfiguration, Object... implementor) throws ClassNotFoundException, LightRPCException {
		this.configuration = pConfiguration;
		
		this.implementorList = new ArrayList<Object>();
		for(Object o : implementor) {
			implementorList.add(o);
			
			Class classe = Class.forName(implementor.getClass().getName());
			Method[] listOfMethodImplemented = classe.getDeclaredMethods();
			
			for(Method m : listOfMethodImplemented) {
				if(m.getReturnType() != String.class && m.getReturnType() != String[].class && m.getReturnType() != void.class) {
					if(m.getGenericReturnType() instanceof ParameterizedType) {
						ParameterizedType type = (ParameterizedType) m.getGenericReturnType();
					    if(!haveAvailableGenericType(type)) {
					    	throw new LightRPCException("Bad Type implemented in LightRPC Server method: Only String or different type of String array are accepted");
					    }
					} else {
						throw new LightRPCException("Bad Type implemented in LightRPC Server method: Only String or different type of String array are accepted");
					}
				}
				
				for(int i = 0; i < m.getParameterTypes().length; i++) {
					if(m.getParameterTypes()[i] != String.class && m.getReturnType() != String[].class) {
						if(m.getGenericParameterTypes()[i] instanceof ParameterizedType) {
							ParameterizedType type = (ParameterizedType) m.getGenericParameterTypes()[i];
							if(!haveAvailableGenericType(type)) {
						    	throw new LightRPCException("Bad Type implemented in LightRPC Server method: Only String or different type of String array are accepted");
						    }
						} else {
							throw new LightRPCException("Bad Type implemented in LightRPC Server method: Only String or different type of String array are accepted");
						}
					}
				}
			}
		}
	}
	
	/**
	 * Construct a LightRPCHandler with a configuration object
	 * @param pConfiguration a LightRPC Configuration object
	 * @throws LightRPCException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public LightRPCServlet(LightRPCConfig pConfiguration, Class implementor) throws ClassNotFoundException, LightRPCException, InstantiationException, IllegalAccessException {
		this.configuration = pConfiguration;
		
		this.implementorList = new ArrayList<Object>();
		implementorList.add(implementor.newInstance());
		System.out.println(this.implementorList.get(0).getClass().getSimpleName());
		Method[] listOfMethodImplemented = implementor.getDeclaredMethods();
		
		for(Method m : listOfMethodImplemented) {
			if(m.getReturnType() != String.class && m.getReturnType() != String[].class && m.getReturnType() != void.class) {
				if(m.getGenericReturnType() instanceof ParameterizedType) {
					ParameterizedType type = (ParameterizedType) m.getGenericReturnType();
				    if(!haveAvailableGenericType(type)) {
				    	throw new LightRPCException("Bad Type implemented in LightRPC Server method: Only String or different type of String array are accepted");
				    }
				} else {
					throw new LightRPCException("Bad Type implemented in LightRPC Server method: Only String or different type of String array are accepted");
				}
			}
			
			for(int i = 0; i < m.getParameterTypes().length; i++) {
				if(m.getParameterTypes()[i] != String.class && m.getReturnType() != String[].class) {
					if(m.getGenericParameterTypes()[i] instanceof ParameterizedType) {
						ParameterizedType type = (ParameterizedType) m.getGenericParameterTypes()[i];
						if(!haveAvailableGenericType(type)) {
					    	throw new LightRPCException("Bad Type implemented in LightRPC Server method: Only String or different type of String array are accepted");
					    }
					} else {
						throw new LightRPCException("Bad Type implemented in LightRPC Server method: Only String or different type of String array are accepted");
					}
				}
			}
		}
	}
	
	/**
	 * Construct a LightRPCHandler with a configuration object
	 * @param pConfiguration a LightRPC Configuration object
	 * @throws LightRPCException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public LightRPCServlet(LightRPCConfig pConfiguration, Class... implementor) throws ClassNotFoundException, LightRPCException, InstantiationException, IllegalAccessException {
		this.configuration = pConfiguration;
		
		this.implementorList = new ArrayList<Object>();
		for(Class o : implementor) {
			implementorList.add(o.newInstance());
			
			Method[] listOfMethodImplemented = o.getDeclaredMethods();
			
			for(Method m : listOfMethodImplemented) {
				if(m.getReturnType() != String.class && m.getReturnType() != String[].class && m.getReturnType() != void.class) {
					if(m.getGenericReturnType() instanceof ParameterizedType) {
						ParameterizedType type = (ParameterizedType) m.getGenericReturnType();
					    if(!haveAvailableGenericType(type)) {
					    	throw new LightRPCException("Bad Type implemented in LightRPC Server method: Only String or different type of String array are accepted");
					    }
					} else {
						throw new LightRPCException("Bad Type implemented in LightRPC Server method: Only String or different type of String array are accepted");
					}
				}
				
				for(int i = 0; i < m.getParameterTypes().length; i++) {
					if(m.getParameterTypes()[i] != String.class && m.getReturnType() != String[].class) {
						if(m.getGenericParameterTypes()[i] instanceof ParameterizedType) {
							ParameterizedType type = (ParameterizedType) m.getGenericParameterTypes()[i];
							if(!haveAvailableGenericType(type)) {
						    	throw new LightRPCException("Bad Type implemented in LightRPC Server method: Only String or different type of String array are accepted");
						    }
						} else {
							throw new LightRPCException("Bad Type implemented in LightRPC Server method: Only String or different type of String array are accepted");
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * Recusive method to determine if a type are available or not (search in deep)
	 * @param pType type to analyse
	 * @return true if the type are available
	 */
	private boolean haveAvailableGenericType(ParameterizedType pType) {
		boolean ok = true;
		Type[] typeArguments = pType.getActualTypeArguments();
		for(Type typeArgument : typeArguments) {
			if(typeArgument instanceof ParameterizedType) {
	        	if(haveAvailableGenericType((ParameterizedType)typeArgument)) {
	        	} else {
	        		ok = false;
	        	}
	        } else {
	        	Class typeArgClass = (Class) typeArgument;
		        if(typeArgClass == String.class) {
		        } else if(typeArgClass == String[].class) {
		        } else {
		        	ok = false;
		        }
	        }
		}
		return ok;
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
			} else if(pConfiguration.getSecurityEncryptionType().equals(LightRPCConfig.SECURITY_ENCRYPTION_TYPE_3DES)) {
				TripleDES aes = new TripleDES(pConfiguration.getSecurityEncryptionPassphrase());
				String reqSerialized = pResponse.getXML();
				byte[] reqSerializedAndEncrypted = aes.crypt(reqSerialized);
				content.setText(TripleDES.asHex(reqSerializedAndEncrypted));		
			}  else if(pConfiguration.getSecurityEncryptionType().equals(LightRPCConfig.SECURITY_ENCRYPTION_TYPE_AES256)) {
				AES256 aes = new AES256(pConfiguration.getSecurityEncryptionPassphrase());
				String reqSerialized = pResponse.getXML();
				byte[] reqSerializedAndEncrypted = aes.crypt(reqSerialized);
				content.setText(AES256.asHex(reqSerializedAndEncrypted));		
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
	    	} else if(pConfiguration.getSecurityEncryptionType().equals(LightRPCConfig.SECURITY_ENCRYPTION_TYPE_3DES)) {
	    		TripleDES aes = new TripleDES(pConfiguration.getSecurityEncryptionPassphrase());
	    		byte[] responseEncrypted = TripleDES.hexStringToByteArray(content.getText());
	    		String responseDecrypted = aes.decrypt(responseEncrypted);
	    		LightRPCRequest request = new LightRPCRequest(responseDecrypted);
	    		return request;
	    	} else if(pConfiguration.getSecurityEncryptionType().equals(LightRPCConfig.SECURITY_ENCRYPTION_TYPE_AES256)) {
	    		AES256 aes = new AES256(pConfiguration.getSecurityEncryptionPassphrase());
	    		byte[] responseEncrypted = AES256.hexStringToByteArray(content.getText());
	    		String responseDecrypted = aes.decrypt(responseEncrypted);
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
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	private Object doMethod(String pName, ArrayList<Object> pParameter) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		String[] a = pName.split("\\.");
		if(a.length <= 1) {
			if(this.implementorList.size() == 1) {
				Method[] listOfMethodImplemented = implementorList.get(0).getClass().getDeclaredMethods();
				for(Method m : listOfMethodImplemented) {
					if(m.getName().equals(pName)) {
						return m.invoke(implementorList.get(0), pParameter.toArray());
					}
				}
				return new LightRPCException("Bad method name");
			} else {
				return new LightRPCException("Bad method name");
			}
		} else {
			String methodNameClass = a[0];
			String methodName = a[1];
			for(int i = 0; i < this.implementorList.size(); i++) {
				if(methodNameClass.equals(this.implementorList.get(i).getClass().getSimpleName())) {
					Method[] listOfMethodImplemented = implementorList.get(0).getClass().getDeclaredMethods();
					for(Method m : listOfMethodImplemented) {
						if(m.getName().equals(methodName)) {
							return m.invoke(implementorList.get(0), pParameter.toArray());
						}
					}
				}
			}
			return new LightRPCException("Bad method name");
		}
		
	}


	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		//super.doPost(req, resp);
		
		resp.setContentType("text/html");
		resp.setStatus(HttpServletResponse.SC_OK);
        
        
        String bodyRequest = getBodyOfRequest(req);
		try {
	        LightRPCRequest lightrpcRequest = buildRequest(bodyRequest, this.configuration);
	        LightRPCResponse lightrpcResponse = null;
	        Object responseParameter = doMethod(lightrpcRequest.getMethodName(), lightrpcRequest.getParameterList());
	        if(responseParameter != null) {
	        	if(responseParameter.getClass() == Object[].class) {
	        
		        	ArrayList<Object> paramResponse = new ArrayList<Object>();
		        	Object[] o = (Object[]) responseParameter;
		        	for(Object oo : o) {
		        		paramResponse.add(oo);
		        	}
		        	lightrpcResponse = new LightRPCResponse(lightrpcRequest.getMethodName(), "GoodResponse", paramResponse);
		        } else if(responseParameter.getClass() == String[].class) {
		        	lightrpcResponse = new LightRPCResponse(lightrpcRequest.getMethodName(), "GoodResponse", (String[])responseParameter);
		        } else if(responseParameter.getClass() == String.class) {
		        	lightrpcResponse = new LightRPCResponse(lightrpcRequest.getMethodName(), "GoodResponse", new String[]{(String) responseParameter});
		        } else if(responseParameter.getClass() == ArrayList.class) {
		        	ArrayList<Object> paramResponse = (ArrayList) responseParameter;
		        	lightrpcResponse = new LightRPCResponse(lightrpcRequest.getMethodName(), "GoodResponse", paramResponse);
		        } else if(responseParameter.getClass() == LightRPCException.class) {
		        	lightrpcResponse = new LightRPCResponse(lightrpcRequest.getMethodName(), "Exception", new String[]{"This method doesn't exist"});
	        	} else {
		        	lightrpcResponse = new LightRPCResponse(lightrpcRequest.getMethodName(), "Exception", new String[]{"the remode method return a non serializable object"});
		        }
	        } else {
	        	lightrpcResponse = new LightRPCResponse(lightrpcRequest.getMethodName(), "GoodResponse", new ArrayList<Object>());
	        }
	        
	        resp.getWriter().println(buildResponse(lightrpcResponse, this.configuration));
		} catch (Exception e) {
			e.printStackTrace();
			String responseStr;
			try {
				responseStr = buildResponse(new LightRPCResponse("", "Exception", new String[]{e.getMessage()}), this.configuration);
				resp.getWriter().println(responseStr);
			} catch (Exception e1) {
			}
		}
	}
	/*
	public static void main(String[] args) throws Exception {
		
		final class test {
			
			public void sayHello() {
				System.err.println("Hello");
			}
			
			public String getHello() {
				return "Hello";
			}
			
			public void say(final String pString) {
				System.out.println(pString);
			}
			
			public String[] getHelloHello() {
				return new String[]{"Hello", "Hello"};
			}
			
			public ArrayList<String> getArrayListHello() {
				ArrayList<String> t = new ArrayList<String>();
				for(int i = 0 ; i< 10; i++) {
					t.add("Hello"+i);
				}
				return t;
			}
			
			public ArrayList<ArrayList<String>> getArrayListOfArrayListHello() {
				ArrayList<ArrayList<String>> T = new ArrayList<ArrayList<String>>();
				ArrayList<String> t1 = new ArrayList<String>();
				for(int i = 0 ; i< 10; i++) {
					t1.add("Hello"+i);
				}
				
				ArrayList<String> t2 = new ArrayList<String>();
				for(int i = 0 ; i< 10; i++) {
					t2.add("Hello"+i);
				}
				
				ArrayList<String> t3 = new ArrayList<String>();
				for(int i = 0 ; i< 10; i++) {
					t3.add("Hello"+i);
				}
				T.add(t1);
				T.add(t2);
				T.add(t3);
				return T;
			}
		}
		
		final class truc {
			
			public void sayHello() {
				System.err.println("Hello");
			}
			
			public ArrayList<String> getArrayListHello() {
				ArrayList<String> t = new ArrayList<String>();
				for(int i = 0 ; i< 10; i++) {
					t.add("Hello"+i);
				}
				return t;
			}
		}
		
		LightRPCConfig c = new LightRPCConfig("http://localhost:8080");
		c.addAESSecurityEncryption("passpasspasspass");
		
		test t = new test();
		truc t2 = new truc();
		LightRPCServlet a = new LightRPCServlet(c, test.class, truc.class);
		
		Server server = new Server(8080);
		Context root = new Context(server,"/",Context.SESSIONS);
		root.addServlet(new ServletHolder(a), "/*");
		server.start();
	}
*/
}
