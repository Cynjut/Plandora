package com.pandora.bus.artifact;


public abstract class ArtifactExport {

	public abstract String getUniqueName();

	public abstract byte[] export(String header, String body, String footer) throws Exception;	

	public abstract String getExtension();

	public abstract String getContentType();
}
