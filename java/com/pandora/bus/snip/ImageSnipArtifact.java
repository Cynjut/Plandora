package com.pandora.bus.snip;

import javax.servlet.http.HttpServletRequest;

public class ImageSnipArtifact extends SnipArtifact {

	@Override
	public String getId() {
		return "snip_img";
	}

	@Override
	public String getUniqueName() {
		return "label.artifactTag.snip.addImage";
	}

	@Override
	public int getWidth() {
		return 400;
	}

	@Override
	public int getHeight() {
		return 300;
	}

	@Override
	public String getHtmlBody(HttpServletRequest request) {
		return "";
	}

}
