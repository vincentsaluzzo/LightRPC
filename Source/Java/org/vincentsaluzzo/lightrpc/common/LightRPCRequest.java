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
public class LightRPCRequest {
	
	/**
	 * this object contains the method name for the request
	 */
	private String methodName;
	
	/**
	 * this object contains a list of parameter for the request
	 */
	private ArrayList<Object> parameterList;

	/**
	 * Construct a LightRPCRequest with two parameter 
	 * @param pMethod the name of the method
	 * @param pParameter a list of string for parameter (if null, the list is initialize to empty)
	 */
	public LightRPCRequest(String pMethod, String[] pParameter) {
		this.methodName = pMethod;
		this.parameterList = new ArrayList<Object>();
		if(pParameter != null) {
			for(int i = 0; i < pParameter.length; i++) {
				this.parameterList.add(pParameter[i]);
			}
		}
	}
	
	/**
	 * Construct a LightRPCRequest with two parameter
	 * @param pMethod the name of the method
	 * @param pParameter the list of parameter (if null, the list is initialize to empty)
	 */
	public LightRPCRequest(String pMethod, ArrayList<Object> pParameter) {
		this.methodName = pMethod;
		if(pParameter != null) {
			this.parameterList = pParameter;
		} else {
			this.parameterList = new ArrayList<Object>();
		}
	}
	
	public LightRPCRequest(String pXMLData) throws LightRPCException {
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
	    if(!racine.getName().equals("request")) {
	    	throw new LightRPCException("Root element are not a 'request' element");	
	    }
	    
	    Element method = racine.getChild("method");
    	if(method == null) {
    		throw new LightRPCException("No 'method' element found in the XML");
    	}

    	this.methodName = method.getText();
    	
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
	 * this function return the XML serialized format of the request
	 * the Server or the Client should use this method to retrieve the data to send/receive
	 * @return the XML serialized format of the request object
	 */
	public String getXML() {
		Element racine = new Element("request");
		Document document = new Document(racine);
		
		Element method = new Element("method");
		method.setText(this.methodName);
		racine.addContent(method);
		
		
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
	static public void main(String[] args) {
		System.out.println("PARSING XML REQUEST:");
	try {
		LightRPCRequest resp = new LightRPCRequest("<?xml version='1.0' encoding='UTF-8'?>" +
				"<request>" +
				"<method>toto</method>" +
				"<parameter>" +
				"<string>titi</string>" +
				"<string>tata</string>" +
				"<array>" +
				"<string>toto0</string>" +
				"<string>toto1</string>" +
				"<string>toto2</string>" +
				"</array>" +
				"</parameter>" +
				"</request>");
		
		System.out.println(resp.getXML());
	} catch (LightRPCException e) {
		System.out.println(e.getMessage());
	}
	System.out.println("BUILDING XML REQUEST AND SERIALIZE IT");
		LightRPCRequest req = new LightRPCRequest("toto", new String[]{"titi","tata"});
		System.out.println(req.getXML());
	}
	*/
}
