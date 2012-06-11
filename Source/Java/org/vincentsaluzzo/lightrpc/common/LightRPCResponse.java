/**
 * 
 */
package org.vincentsaluzzo.lightrpc.common;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * @author vincentsaluzzo
 *
 */
public class LightRPCResponse {

	/**
	 * this object contains the method name for the response
	 */
	private String methodName;
	
	/**
	 * this object contains a list of parameter for the response
	 */
	private ArrayList<Object> parameterList;
	
	private String type;
	
	/**
	 * Construct a LightRPCResponse with three parameter 
	 * @param pMethod the name of the method
	 * @param pType the type of the response 
	 * @param pParameter a list of String for parameter (if null, the list is initialize to empty)
	 */
	public LightRPCResponse(String pMethod, String pType, String[] pParameter) {
		this.methodName = pMethod;
		this.type = pType;
		this.parameterList = new ArrayList<Object>();
		if(pParameter != null) {
			for(int i = 0; i < pParameter.length; i++) {
				this.parameterList.add(pParameter[i]);
			}
		}
	}
	
	/**
	 * Construct a LightRPCResponse with three parameter
	 * @param pMethod the name of the method
	 * @param pType the type of the response
	 * @param pParameter the list of the parameter (if null, the list is initialize to empty)
	 */
	public LightRPCResponse(String pMethod, String pType, ArrayList<Object> pParameter) {
		this.methodName = pMethod;
		this.type = pType;
		if(pParameter != null) {
			this.parameterList = pParameter;
		} else {
			this.parameterList = new ArrayList<Object>();
		}
	}
	
	
	public LightRPCResponse(String pXMLData) throws LightRPCException {
		//On crée une instance de SAXBuilder
	    SAXBuilder sxb = new SAXBuilder();
	    Document document;
	    StringReader sr = new StringReader(pXMLData);
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
	    if(!racine.getName().equals("response")) {
	    	throw new LightRPCException("Root element are not a 'response' element");	
	    }
	    
	    Element method = racine.getChild("method");
    	if(method == null) {
    		throw new LightRPCException("No 'method' element found in the XML");
    	}

    	this.methodName = method.getText();
    	
    	Element type = racine.getChild("type");
    	if(type == null) {
    		throw new LightRPCException("No 'type' element found in the XML");
    	}
    	this.type = type.getText();
    	
    	Element paramaterList = racine.getChild("parameter");
    	if(paramaterList == null) {
    		throw new LightRPCException("No 'parameter' element found in the XML");
    	}
    	List param = paramaterList.getChildren();
    	this.parameterList = new ArrayList<Object>();
    	for(int i = 0; i < param.size(); i++) {
    		if(param.get(i).getClass() == Element.class) {
    			Element e = (Element)param.get(i);
    			this.parameterList.add(this.parseXMLForParameter(e));
    		}
    	}
	}
	
	private Object parseXMLForParameter(Element pE) {
		if(pE.getName().equals("array")) {
			List param = pE.getChildren();
			ArrayList<Object> array = new ArrayList<Object>();
			for(int i = 0; i < param.size(); i++) {
				if(param.get(i).getClass() == Element.class) {
					array.add(this.parseXMLForParameter((Element)param.get(i)));
				}
			}
			return array;
		} else if(pE.getName().equals("string")) {
			return pE.getText();
		} else {
			return null;
		}
	}
	
	private Element parseParameterForXML(Object pE) {
		if(pE.getClass() == ArrayList.class) {
			Element array = new Element("array");
			ArrayList<Object> arraylist = (ArrayList)pE;
			for(int i = 0; i < arraylist.size(); i++) {
				array.addContent(this.parseParameterForXML(arraylist.get(i)));
			}
			return array;
		} else if(pE.getClass() == String.class) {
			Element param = new Element("string");
			param.setText((String)pE);
			return param;
		} else {
			return null;
		}
	}
	
	/**
	 * Setter of methodName object
	 * @param methodName the new value for the methodName
	 */
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	/**
	 * Getter of methodName object
	 * @return the value of methodName
	 */
	public String getMethodName() {
		return methodName;
	}

	/**
	 * Setter of parameterList array object
	 * @param parameterList the new array for the list of parameter
	 */
	public void setParameterList(ArrayList<Object> parameterList) {
		this.parameterList = parameterList;
	}

	/**
	 * Getter of parameterList array object
	 * @return the value of parameterList array
	 */
	public ArrayList<Object> getParameterList() {
		return parameterList;
	}
	
	/**
	 * this function return the XML serialized format of the response
	 * the Server or the Client should use this method to retrieve the data to send/receive
	 * @return the XML serialized format of the response object
	 */
	public String getXML() {
		Element racine = new Element("response");
		Document document = new Document(racine);
		
		Element method = new Element("method");
		method.setText(this.methodName);
		racine.addContent(method);
		
		Element type = new Element("type");
		type.setText(this.type);
		racine.addContent(type);
		
		Element parameterList = new Element("parameter");
		racine.addContent(parameterList);
		
		for(int i = 0; i < this.parameterList.size(); i++) {
			parameterList.addContent(this.parseParameterForXML(this.parameterList.get(i)));
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
	
	/*
	public static void main(String[] args) {
		System.out.println("PARSING XML RESPONSE:");
		try {
			LightRPCResponse resp = new LightRPCResponse("<?xml version='1.0' encoding='UTF-8'?>" +
					"<response>" +
					"<method>toto</method>" +
					"<type>response</type>" +
					"<parameter>" +
					"<param>titi</param>" +
					"<param>tata</param>" +
					"</parameter>" +
					"</response>");
			
			System.out.println(resp.getXML());
		} catch (LightRPCException e) {
			System.out.println(e.getMessage());
		}
		System.out.println("BUILDING XML RESPONSE AND SERIALIZE IT");
		LightRPCResponse resp = new LightRPCResponse("toto", "response", new String[]{"titi","tata"});
		System.out.println(resp.getXML());
	}
	*/
}
