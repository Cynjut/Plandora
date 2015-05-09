package com.pandora.helper;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

/**
 * This class handle a DOM XML document. <br> 
 * The methods of getting specifics pieces of document (Nodes) is includes into this class too.
 */
public final class XmlDomParse {
	
	/** Default format for date into XML */
	public static final String XML_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
	
	/**
	 * Constructor
	 */
	private XmlDomParse(){
	}

	/**
	 * Constructor that allows the creation of a new XmlDomParse 
	 * based on a string (stream) of XML.
	 * @param string of xml 
	 */
	public static Document getXmlDom(String streamXml) throws Exception{
		InputStream is = new ByteArrayInputStream(streamXml.getBytes());
		return read(is);
	}
	
	/**
	 * Constructor that allows the creation of a new XmlDomParse 
	 * based on a stream of XML.
	 * @param streamXml
	 */
	public static Document getXmlDom(InputStream is) throws Exception{
		return read(is);
	}
	
	/**
	 * This method return a arrayList with all attributes of XML previouslly loaded by
	 * class, based on attribute's name and tag's name.
	 * @param tagName
	 * @param attrName
	 * @return ArrayList
	 */	
/*
	public ArrayList getAttrByTag(String tagName, String attrName) {
		ArrayList response = null;
		
		// Get a list of all elements in the document
		NodeList list = this.doc.getElementsByTagName(tagName);
		if (list!=null) {
			for (int i=0; i<list.getLength(); i++) {
				Node node = list.item(i);
	
				// Get element
				if (node.getNodeType()==1) {
					Element element = (Element)node;
					if (!element.getAttribute(attrName).equals("")) {
						if (response==null) response = new ArrayList();
						response.add(getDecode(element.getAttribute(attrName)));
					}
				}
			}
		}		
		return(response);		
	}
*/

	/**
	 * This method return a arrayList with all values (content) 
	 * of a specific tag's name.
	 * @param source
	 * @param tagName
	 * @return ArrayList
	 */
	public static ArrayList getTextByTag(Node source, String tagName) {
		ArrayList response = null;
		NodeList nlValue = null;
		String buff = "";
		
		// Get a list of all elements in the document	
		NodeList list = source.getChildNodes();
		if (list!=null) {
			for (int i=0; i<list.getLength(); i++) {
				Node node = list.item(i);
	
				//Get current element
				if (node.getNodeType()==1 && node.getNodeName().equals(tagName)) {
					Element element = (Element)node;
					nlValue = element.getChildNodes();
					if (nlValue.item(0)!=null) {
						buff = ((Text)nlValue.item(0)).getNodeValue();
						if (response==null) response = new ArrayList();
						response.add(getDecode(buff));
					}
				}
			}
		}
		return(response);		
	}


	/**
	 * This method return a first occurrence of content  
	 * for a specific tag's name.
	 * @param tagName
	 * @return String
	 */
	public static String getFirstTextByTag(Node source, String tagName) {
		String response = null;
		NodeList nlValue = null;
		String buff = "";
		
		// Get a list of all elements in the document	
		NodeList list = source.getChildNodes();
		if (list!=null) {
			for (int i=0; i<list.getLength(); i++) {
				Node node = list.item(i);
	
				//Get current element
				if (node.getNodeType()==1 && node.getNodeName().equals(tagName)) {
					Element element = (Element)node;
					nlValue = element.getChildNodes();
					if (nlValue.item(0)!=null) {
						buff = ((Text)nlValue.item(0)).getNodeValue();
						response = getDecode(buff);
						break;
					}
				}
			}
		}
		return(response);		
	}
	
	
	/**
	 * This method return a arrayList with all values (content) of a specific tag.
	 * @param tagName
	 * @return ArrayList
	 */
/*	
	public static ArrayList getTextByTag(Element element) {
		ArrayList response = null;
		NodeList nlValue = null;
		String buff = "";

		if (element!=null) {		
			nlValue = element.getChildNodes();
			if (nlValue.item(0)!=null) {
			buff = ((Text)nlValue.item(0)).getNodeValue();
				if (response==null) response = new ArrayList();
				response.add(getDecode(buff));
			}
		}
		return(response);
	}
*/
	/**
	 * Get the first occurrence of a specific tag, based on a Element object 
	 * @param element Element object (w3c.org)
	 * @param tagName
	 * @return String 
	 */
/*	
	public static String getFirstTextByTag(Element element, String tagName) {
		Element tagElement;
		try {
			tagElement = (Element) ((ArrayList) getNodesByTag(element,tagName)).get(0);
			return getDecode((String) ((ArrayList) getTextByTag(tagElement)).get(0));
		} catch (Exception e) {
			return null;
		}
	}
*/

	/**
	 * Get the tag's text (content) based on a Node object and an attribute's name.
	 * @param node object (w3c.org)
	 * @param attrName
	 * @return String 
	 */	
	public static String getAttributeTextByTag(Node node, String attrName) {
		return getAttributeTextByTag(node, attrName, false);
	}

	public static String getAttributeTextByTag(Node node, String attrName, boolean decodeIt) {
		try {
			Element element = (Element)node;
			Object x = element.getAttribute(attrName);
			if (decodeIt) {
				return getDecode((String) x);	 
			} else {
				return (String)x;
			}
		} catch (Exception e) {
			return null;
		}
	}

	
	/**
	 * Get a list of Nodes objects based on a parent Node object and an 
	 * attribute's name.
	 * @param  parent Node object (w3c.org)
	 * @param attrName
	 * @return ArrayList of Element
	 */
	public static ArrayList getNodesByTag(Node parent, String tagName) {
		ArrayList response = null;
		
		// Get a list of all elements in the document
		NodeList list = parent.getChildNodes();
		if (list!=null) {
			for (int i=0; i<list.getLength(); i++) {
				Node node = list.item(i);
	
				// Get element
				if (node.getNodeType()==1) {
					Element element = (Element)node;
	
					//verify if current node is tagName
					if (element.getNodeName().equals(tagName)) {
						if (response==null) response = new ArrayList();
						response.add(element);
					}
				}
			}
		}		
		return(response);		
	}

	
	/**
	 * Get the first occurrence of a specific tag into a parent Node.
	 * @param parent
	 * @param tagName
	 * @return
	 */
	public static Node getFirstNodeByTag(Node parent, String tagName) {
		Node response = null;
		
		// Get a list of all elements in the document
		NodeList list = parent.getChildNodes();
		if (list!=null) {
			for (int i=0; i<list.getLength(); i++) {
				Node node = list.item(i);
	
				// Get element
				if (node.getNodeType()==1) {
					Element element = (Element)node;
	
					//verify if current node is tagName
					if (element.getNodeName().equals(tagName)) {
						response = element;
						break;
					}
				}
			}
		}		
		return(response);		
	}
	
	
	/**
	 * Get a list of Nodes objects based on a parent Node object.
	 * @param  Node object (w3c.org)
	 * @return ArrayList  
	 */
	public static ArrayList getNodesByTag(Node parent) {
		ArrayList response = null;
		
		// Get a list of all elements in the document
		NodeList list = parent.getChildNodes();
		if (list!=null) {
			for (int i=0; i<list.getLength(); i++) {
				Node node = list.item(i);
	
				// Get element
				if (node.getNodeType()==1) {
					Element element = (Element)node;
					if (response==null) response = new ArrayList();
					response.add(element);
				}
			}
		}		
		return(response);		
	}
	
	/**
	 * Get a list of Nodes objects based on a tag id. 
	 * @param attrName
	 * @return ArrayList  
	 */
/*	
	public ArrayList getNodesByTag(String tagName) {
		ArrayList response = null;
		
		// Get a list of all elements in the document
		NodeList list = this.doc.getElementsByTagName(tagName);
		if (list!=null) {				
			for (int i=0; i<list.getLength(); i++) {
				Node node = list.item(i);
	
				// Get element
				if (node.getNodeType()==1) {
					Element element = (Element)node;
					if (response==null) response = new ArrayList();
					response.add(element);
				}
			}
		}
		return(response);		
	}
*/
	/**
	 * Get a list of Nodes objects based on a parent and a child tag id.
	 * @param parentId
	 * @param tagName
	 * @return ArrayList  
	 */
/*	
	public ArrayList getNodesByTag(String parentId, String tagName) {
		ArrayList response = null;

		// Get a list of all elements in the document
		NodeList list = doc.getElementsByTagName(tagName);
		for (int i = 0; i < list.getLength(); i++)	{
			// Get element
			Element element = (Element) list.item(i);
			Node parent = (Node) element.getParentNode();

			//verify if parent is 'parentId'
			if (parent.getNodeName().equals(parentId))	{
				if (response == null)
					response = new ArrayList();
				response.add(element);
			}
		}

		return (response);
	}
*/
	/**
	 * Get a NodeList object with a list of Nodes objects based on a tag name.
	 * @param attrName
	 * @return NodeList  
	 */
/*	
	public NodeList getNodeListByTag(String tagName) {
		return (this.doc.getElementsByTagName(tagName));
	}
*/
	/**
	 * Write the content of XML into a OutputStream object.
	 * @param outStr 
	 */
/*	
	public void write(java.io.OutputStream outStr) {
		try {
			Transformer trans = TransformerFactory.newInstance().newTransformer();
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			trans.transform(new DOMSource((Document) this.doc), new StreamResult(outStr));
		} catch (TransformerException e) {
			System.out.println(e.getMessage());
		}
	}
*/
	
	/**
	 * Write the content of XML into file. 
	 */
	public static void write(Document doc, String filePath) throws FileNotFoundException,
			TransformerFactoryConfigurationError,
			TransformerConfigurationException, TransformerException,
			IOException {
		
		FileOutputStream fos = new FileOutputStream(filePath);
		DOMSource domSource = new DOMSource(doc);
		StreamResult streamResult = new StreamResult(fos);
		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer serializer = tf.newTransformer();
		serializer.setOutputProperty(OutputKeys.ENCODING," ISO-8859-1");
		serializer.setOutputProperty(OutputKeys.INDENT,"yes");
		serializer.transform(domSource, streamResult);
		fos.close();
	}

/*
	private Document read(String filePath) throws Exception{
		Document response = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		response = builder.parse(filePath);
		return(response);
	}  	
*/

	private static Document read(InputStream is) throws Exception{
		Document response = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();		
		DocumentBuilder builder = factory.newDocumentBuilder();		
		response = builder.parse(is);		
		return(response);
	}
	
	/**
	 * Decode the data
	 * @param buff
	 * @return The Original String 
	 */
	public static String getDecode(String buff){
		String response = "";
		try {
			response = URLDecoder.decode(buff, "UTF-8");
		} catch (Exception e){
		    e.printStackTrace();
		}
		return(response);
	}

	/**
	 * Encode the data
	 * @param buff
	 * @return The Encode String
	 */
	public static String getEncode(String buff){
		String response = "";
		try {
		    if (buff!=null){
		        response = URLEncoder.encode(buff, "UTF-8");    
		    }
		} catch (Exception e){
		    e.printStackTrace();
		}
		return(response);
	}

}