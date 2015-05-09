package com.pandora.bus.artifact;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.tidy.Tidy;

import com.pandora.helper.XmlDomParse;

public class MSWordArtifactExport extends ArtifactExport {
	
	private final static int FLAT_RUNNER = 0;
	
	private final static int H3_RUNNER   = 1;
	
	@Override
	public byte[] export(String header, String body, String footer) throws Exception {
		ByteArrayInputStream bis = new ByteArrayInputStream(body.getBytes());
		FileOutputStream fos = new FileOutputStream("simpleTable.docx");
		XWPFDocument doc = new XWPFDocument();
		byte[] response = null;
		
		try {			
			Tidy tidy = new Tidy();             
			Document docHtml = tidy.parseDOM(bis, null);
			XmlDomParse.write(docHtml, "artifact.xml");
			NodeList list = docHtml.getElementsByTagName("body");
			if (list!=null) {
				Node bodyNode = list.item(0);				
				this.format(doc, bodyNode);				
		        doc.write(fos);
		        fos.close();		   				
			}
			
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			if (fos!=null) {
				fos.close();
			}
			if (bis!=null) {
				bis.close();
			}
		}
		
/*			
			Node htmlNode = doc.getFirstChild();
			if (htmlNode!=null) {
				
				Node bodyNode = htmlNode.getFirstChild();
				if (htmlNode!=null) {
					
				} else {
					throw new BusinessException("The artifact content cannot be parsed. The body node was not found");	
				}
			} else {
				throw new BusinessException("The artifact content cannot be parsed. The root node was not found");
			}
*/		
		return response;	
	}

	
	@Override
	public String getContentType() {
		return "application/msword";
	}
	

	@Override
	public String getExtension() {
		return ".doc";
	}
	

	@Override
	public String getUniqueName() {
		return "label.artifactTag.export.msword";
	}
	

	private void format(XWPFDocument doc, Node node) throws Exception{

		boolean isEmpty = false;
		if (node.getNodeName().equalsIgnoreCase("p")){
			XWPFParagraph p1 = doc.createParagraph();
			isEmpty = formatText(node, p1, FLAT_RUNNER);						
			
		} else	if (node.getNodeName().equalsIgnoreCase("h3")){
			XWPFParagraph p1 = doc.createParagraph();
			isEmpty = formatText(node, p1, H3_RUNNER);
			
		} else	if (node.getNodeName().equalsIgnoreCase("ul")){
			isEmpty = formatList(doc, node, false, 0);
			
		} else	if (node.getNodeName().equalsIgnoreCase("ol")){
			isEmpty = formatList(doc, node, true, 0);
		
		}
		
		if (isEmpty ) {
			XWPFParagraph p1 = doc.createParagraph();
			XWPFRun r1 = this.getRunner(p1, FLAT_RUNNER);			
			r1.addBreak();
		}

		
		NodeList nodes = node.getChildNodes();
		for (int i =0 ; i<nodes.getLength(); i++) {
			Node child = nodes.item(i);
			format(doc, child);
		}
	}


	private boolean formatList(XWPFDocument doc, Node node, boolean isOrdered, int level) throws UnsupportedEncodingException {
		boolean isEmpty = false;
		level = level + 1;
		
		NodeList nodes = node.getChildNodes();
		for (int i=0 ; i<nodes.getLength(); i++) {
			Node n = nodes.item(i);				
			if (n.getNodeName().equalsIgnoreCase("li")) {
				XWPFParagraph p1 = doc.createParagraph();
				XWPFRun liRun = this.getRunner(p1, FLAT_RUNNER);
				
				String content = "";
				for (int sp=0; sp<=level; sp++){
					content = content + "  ";
				}
				
				if (isOrdered) {
					content = content + "1 ";	
				} else {
					content = content + "- ";
				}
				
				liRun.setText(content);
				liRun.setBold(true);
				isEmpty = formatText(n, p1, FLAT_RUNNER);

				NodeList liNodes = n.getChildNodes();
				for (int j=0 ; j<liNodes.getLength(); j++) {
					Node liChild = liNodes.item(j);
					if (n.getNodeName().equalsIgnoreCase("ul")) {
						isEmpty = formatList(doc, node, false, level);
					} else if (n.getNodeName().equalsIgnoreCase("ol")) {
						isEmpty = formatList(doc, node, true, level);
					}				
				}
			}
		}
		return isEmpty;
	}
	

	private boolean formatText(Node node, XWPFParagraph parag, int runnerType) throws UnsupportedEncodingException {
		boolean isEmpty = true;
				
		NodeList nodes = node.getChildNodes();
		for (int i =0 ; i<nodes.getLength(); i++) {
			Node n = nodes.item(i);
			
			if (n.getNodeName().equalsIgnoreCase("span")) {
				
				//TODO the poi api do not contain portability to color and background, then I will set a underline...
				XWPFRun spanRun = this.getRunner(parag, runnerType);
				spanRun.setUnderline(UnderlinePatterns.DOTTED);
				
				String c2 = getText(n);
				if (c2!=null) {

					String font = getStyle(n, "font-family");
					if (font!=null && !font.trim().equals("")) {
						String[] fonts = font.split(",");
						if (fonts!=null) {
							spanRun.setFontFamily(fonts[0].trim());
						}
					}
					
					spanRun.setText(c2);
					isEmpty = false;
				}
				
			} else if (n.getNodeType()==3) {				
				String content = n.getNodeValue();
				if (content!=null && !URLEncoder.encode(content, "UTF-8").equals("%C2%A0") ) {
					XWPFRun r1 = this.getRunner(parag, runnerType);										
					r1.setText(content);
					isEmpty = false;
				}
			}			
		}

		return isEmpty;
	}

	private String getText(Node node) throws UnsupportedEncodingException {
		String response = null;
		
		NodeList nodes = node.getChildNodes();
		for (int i =0 ; i<nodes.getLength(); i++) {
			Node c = nodes.item(i);
			if (c.getNodeType()==3) {
				String content = c.getNodeValue();
				if (content!=null && !URLEncoder.encode(content, "UTF-8").equals("%C2%A0") ) {
					response = content;
				}
			}			
		}
		return response;
	}

	
	private void test() {
        XWPFDocument document = new XWPFDocument();
        
        // New 2x2 table
        XWPFTable tableOne = document.createTable();
        XWPFTableRow tableOneRowOne = tableOne.getRow(0);
        tableOneRowOne.getCell(0).setText("Hello");
        tableOneRowOne.addNewTableCell().setText("World");

        XWPFTableRow tableOneRowTwo = tableOne.createRow();
        tableOneRowTwo.getCell(0).setText("This is");
        tableOneRowTwo.getCell(1).setText("a table");

        //Add a break between the tables
        document.createParagraph().createRun().addBreak();

        // New 3x3 table
        XWPFTable tableTwo = document.createTable();
        XWPFTableRow tableTwoRowOne = tableTwo.getRow(0);
        tableTwoRowOne.getCell(0).setText("col one, row one");
        tableTwoRowOne.addNewTableCell().setText("col two, row one");
        tableTwoRowOne.addNewTableCell().setText("col three, row one");

        XWPFTableRow tableTwoRowTwo = tableTwo.createRow();
        tableTwoRowTwo.getCell(0).setText("col one, row two");
        tableTwoRowTwo.getCell(1).setText("col two, row two");
        tableTwoRowTwo.getCell(2).setText("col three, row two");

        XWPFTableRow tableTwoRowThree = tableTwo.createRow();
        tableTwoRowThree.getCell(0).setText("col one, row three");
        tableTwoRowThree.getCell(1).setText("col two, row three");
        tableTwoRowThree.getCell(2).setText("col three, row three");

        FileOutputStream outStream = null;
        try {
            outStream = new FileOutputStream("test.docx");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            document.write(outStream);
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}	

	
	private XWPFRun getRunner(XWPFParagraph parag, int runnerType){
		
		XWPFRun r = parag.createRun();
		if (runnerType==FLAT_RUNNER) {
	        r.setBold(false);
			r.setFontFamily("Arial");
			r.setFontSize(10);
			
		} else if (runnerType==H3_RUNNER) {			
	        r.setBold(true);
	        r.setFontFamily("Arial");
	        r.setFontSize(13);			
		}
        return r;
	}
	
	private String getStyle(Node n, String styleId){
		String response = null;
		
		String style = XmlDomParse.getAttributeTextByTag(n, "style");
		if (style!=null && !style.trim().equals("")) {
			String[] styles = style.split(";");
			if (styles!=null) {
				for (int s=0; s<styles.length; s++) {
					String theStyle = styles[s];
					if (theStyle!=null && !theStyle.trim().equals("")) {
						String[] tokens = theStyle.trim().split(":");
						if (tokens!=null && tokens.length==2) {
							if (tokens[0].trim().equals(styleId)) {
								response = tokens[1].trim();
							}
						}
					}
				}
			}
		}
		
		return response;
		
	}
}
