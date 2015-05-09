package com.pandora.bus.artifact;


public class HtmlArtifactExport extends ArtifactExport {
	
	@Override
	public byte[] export(String header, String body, String footer) {
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

		return content.getBytes();
	}
	
	

	@Override
	public String getUniqueName() {
		return "label.artifactTag.export.html";
	}

	@Override
	public String getExtension() {
		return ".html";
	}


	@Override
	public String getContentType() {
		return "text/html";
	}
}
