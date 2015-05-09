package com.pandora.bus.artifact;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import com.pandora.helper.XmlDomParse;

public class PDFArtifactExport extends ArtifactExport {

	@Override
	public byte[] export(String header, String body, String footer) throws Exception {
		byte[] response = null;
		String content = getContent(header, body, footer);
		ByteArrayInputStream bis = new ByteArrayInputStream(content.getBytes());  
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		
		try {			
			Tidy tidy = new Tidy();             
			Document doc = tidy.parseDOM(bis, null);
						
			XmlDomParse.write(doc, "teste.xml");

			
			ITextRenderer renderer = new ITextRenderer();
			
			renderer.setDocument(doc, null);  
			renderer.layout();
			
			renderer.createPDF(out);
			
			response = out.toByteArray();
			
		} catch(Exception e){
			e.printStackTrace();
		} finally{
			if (out!=null) {
				out.close();
			}
			if (bis!=null) {
				bis.close();
			}
		}
		return response;	
	}




	
	@Override
	public String getContentType() {
		return "application/pdf";
	}
	

	@Override
	public String getExtension() {
		return ".pdf";
	}
	

	@Override
	public String getUniqueName() {
		return "label.artifactTag.export.pdf";
	}
	
	
	
	private String getContent(String header, String body, String footer){
		String content = "";
		
		if (header!=null) {
			content = header; 
		}

		if (body!=null) {
			content = content + body; 
		}

		if (footer!=null) {
			content = content + footer; 
		}
		
		return content;
	}
}
