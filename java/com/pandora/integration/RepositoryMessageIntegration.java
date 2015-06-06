package com.pandora.integration;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.ArrayList;

import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.integration.exception.IntegrationException;

public class RepositoryMessageIntegration extends Integration {

    /** Used by XML tag reference */
    public static final String REPOSITORY_PROJ_ID = "PROJ_ID";
    public static final String REPOSITORY_REPOS   = "REPOS";
    public static final String REPOSITORY_FILES   = "FILES";
    public static final String REPOSITORY_LOG     = "LOG";
    public static final String REPOSITORY_AUTHOR  = "AUTHOR";
    
	private static final long serialVersionUID = 1L;

	private String projectId;
	
	private String repositoryPath;
	
	private String files;

	private String author;
	
	private UserTO authorUser;
	
	
	public String getLastTokenPath() {
		String token = "";
		if (repositoryPath!=null && !repositoryPath.trim().equals("")) {
			String[] t = repositoryPath.split("/");
			token = t[t.length-1];
			token = token.trim();
		}
		return token;
	}
	
	
	public ArrayList<TransferObject> getParsedFiles(){
		ArrayList<TransferObject> response = null;
		
		try {
			if (files!=null && !files.trim().equals("")) {
				
				BufferedReader br = new BufferedReader(new StringReader(files));
				if (br!=null) {
					String strLine = "";
					while( (strLine = br.readLine()) != null) {
						String[] filesToken = strLine.split("  ");
						if (filesToken!=null && filesToken.length>1) {
							String stat = filesToken[0];
							String path = filesToken[filesToken.length-1];
							
							if (stat!=null && path!=null && !path.trim().equals("")) {
								if (response==null) {
									response = new ArrayList<TransferObject>();
								}
								TransferObject aFile = new TransferObject(stat.trim(), path);
								response.add(aFile);								
							}
						}
					}							
				}							
			}											
		} catch(Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	
	///////////////////////////////////////////
	public String getProjectId() {
		return projectId;
	}
	public void setProjectId(String newValue) {
		this.projectId = newValue;
	}

	
	///////////////////////////////////////////
	public String getRepositoryPath() {
		return repositoryPath;
	}
	public void setRepositoryPath(String newValue) {
		this.repositoryPath = newValue;
	}

	
	///////////////////////////////////////////
	public String getFiles() {
		return files;
	}
	public void setFiles(String newValue) {
		this.files = newValue;
	}

	
	///////////////////////////////////////////
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String newValue) {
		this.author = newValue;
	}


	///////////////////////////////////////////
	public UserTO getAuthorUser() {
		return authorUser;
	}
	public void setAuthorUser(UserTO newValue) {
		this.authorUser = newValue;
	}
	
	
	public void validateDelete() throws IntegrationException {
	}

	public void validateInsert() throws IntegrationException {
	}

	public void validateUpdate() throws IntegrationException {
	}



}
