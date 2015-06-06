package com.pandora.bus.artifact;

import java.util.ArrayList;
import java.util.Vector;

import org.w3c.dom.Document;

import com.pandora.ArtifactTO;
import com.pandora.ArtifactTemplateTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.ReportTO;
import com.pandora.RepositoryFileTO;
import com.pandora.UserTO;
import com.pandora.bus.GeneralBusiness;
import com.pandora.bus.ProjectBUS;
import com.pandora.bus.repository.RepositoryBUS;
import com.pandora.dao.ArtifactTemplateDAO;
import com.pandora.delegate.DbQueryDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.DataAccessException;
import com.pandora.exception.MaxSizeAttachmentException;
import com.pandora.exception.RepositoryPolicyException;
import com.pandora.helper.DateUtil;
import com.pandora.helper.StringUtil;
import com.pandora.helper.XmlDomParse;
import com.pandora.integration.RepositoryMessageIntegration;

public class ArtifactTemplateBUS extends GeneralBusiness {

    /** The Data Access Object related with current business entity */
    ArtifactTemplateDAO dao = new ArtifactTemplateDAO();

    
    public Vector<ArtifactTemplateTO> getListByProject(String projectId) throws BusinessException {
        Vector<ArtifactTemplateTO> response = new Vector<ArtifactTemplateTO>();
        try {
            response = dao.getListByProject(projectId);
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
        return response;
    }

    
	public String getTemplateContent(String templateId, ProjectTO project, UserTO uto, String name) throws BusinessException {
		String response = "";
		try {
			ArtifactTemplateTO atto = (ArtifactTemplateTO) dao.getObject(new ArtifactTemplateTO(templateId));
			
			String body = atto.getBody();
			response = this.parseKeyWords(body, project, uto, name);
						
        } catch (Exception e) {
            throw new BusinessException(e);
        }
		return response;
	}


	public void commitArtifact(ArtifactTO ato) throws MaxSizeAttachmentException, BusinessException {
		RepositoryBUS rbus = new RepositoryBUS();
		ArtifactBUS abus = new ArtifactBUS();
		ProjectBUS pbus = new ProjectBUS();

		byte[] fileData = null;
		long size = 307200;
		ProjectTO pto = null;
		ArtifactExport ae = null;
		String filePath = "";
		String fileName = ato.getFileName();
		
        try {
			if (ato.getPath()!=null && !ato.getPath().equals("")) {
				filePath = filePath + ato.getPath() + "/";
			} 
			filePath = filePath + fileName;

			//get related artifact class converter and clean the extension of file path and name  
			ae = getArtifactExpClass(ato.getExportType());
			fileName = fileName.replaceAll(ae.getExtension(), "");
			filePath = filePath.replaceAll(ae.getExtension(), "");
			
			ArtifactTemplateTO atto = (ArtifactTemplateTO) dao.getObject(new ArtifactTemplateTO(ato.getTemplateId()));
			pto = pbus.getProjectObject(new ProjectTO(ato.getProjectId()), true);
			
			//perform the key-word transformation into header and footer
			String header = atto.getHeader();
			header = this.parseKeyWords(header, pto, ato.getHandler(), fileName);
			String footer = atto.getFooter();
			footer = this.parseKeyWords(footer, pto, ato.getHandler(), fileName);

			//check the commit policies...
			this.checkRepositoryPolicy(pto, ato.getLogMessage(), ato.getUser(), filePath, ato.getHandler());

			//transform the artifact content to selected format...			
			fileData = ae.export(header, ato.getBody(), footer);

			//get max artifact size from system config...
			size = this.getMaxArtifactSize();

        } catch (RepositoryPolicyException e) {
            throw e;
            
        } catch (Exception e) {
            throw new BusinessException(e);
        }

        //check max size of file...        
		if (fileData!=null && size>0 && fileData.length>size) {
			throw new MaxSizeAttachmentException("The size of artifact has exceeded the maximum size (" + size + " bytes).");	
		}

		try {
        	if (fileData!=null && size>0 && fileData.length<size) {
    			rbus.commitFile(pto, ato.getPath(), fileName + ae.getExtension(), RepositoryFileTO.REPOSITORY_HEAD, 
    					ato.getLogMessage(), ae.getContentType(), fileData, ato.getUser(), ato.getPass());

    			//insert a new ArtifactTO into data base
    			abus.updateArtifact(filePath + ae.getExtension(), ato);
    				
    			//create a link between repository file and planningId (if necessary)
    			if (ato.getPlanningId()!=null && !ato.getPlanningId().trim().equals("")) {
    				rbus.updateRepositoryFilePlan(filePath + ae.getExtension(), ato.getPlanningId(), pto, false);
    			}        		
        	} else {
        		throw new BusinessException("An error occurrs during commit generation. The file could not be converted to selected format.");	
        	}

        } catch (Exception e) {
            throw new BusinessException(e);
        }
	}


	/**
	 * Static method used to return a instance of artifact export class 
	 */
	public static ArtifactExport getArtifactExpClass(String className){
		ArtifactExport response = null;
        try {
            @SuppressWarnings("rawtypes")
			Class klass = Class.forName(className);
            response = (ArtifactExport)klass.newInstance();
        } catch (Exception e) {
            response = null;
        }
        return response;
	}
	
	
	private String parseKeyWords(String body, ProjectTO project, UserTO uto, String fileName) throws Exception {
		StringBuffer response = new StringBuffer();
		int f =0;
		
		if (body!=null) {
			body = this.preProcess(body, project, uto, fileName);
			String enc = uto.getBundle().getMessage(uto.getLocale(), "encoding");
			
			int cutCursor = 0;
			int i = body.indexOf("<DB_CONTENT");
			while (i>0) {
				f = body.indexOf("</DB_CONTENT>", i+13);
				if (f>0 && f > i+13) {
					String token = body.substring(i, f+13);

					String sql = null;
					String type = null;
					Document d = null;
					try {
						d = XmlDomParse.getXmlDom("<?xml version=\"1.0\" encoding=\"" + enc + "\" standalone=\"yes\"?>" + token);
						sql = d.getFirstChild().getTextContent();
						type = XmlDomParse.getAttributeTextByTag(d.getFirstChild(), "type");
					} catch (Exception e){
						e.printStackTrace();
					}

					String content = this.peformToken(sql, type, d, project, uto);
					response.append(body.substring(cutCursor, i));
					response.append(content);
					//response.append(body.substring(f+13));
					cutCursor = f + 13;
					i = body.indexOf("<DB_CONTENT", cutCursor);
				} else {
					i = -1;
				}
			}
			response.append(body.substring(cutCursor));			
		}
		
		return response.toString();
	}	
	
	
	private String peformToken(String sql, String type, Document d, ProjectTO pto, UserTO uto) throws BusinessException{
		StringBuffer response = new StringBuffer("");
		DbQueryDelegate db = new DbQueryDelegate();
		
		if (sql!=null && sql.trim().length()>0 && type!=null) {
			
			Vector<Vector<Object>> list = db.performQuery(sql);
			if (list.size()>1) {
				
				if (type.equalsIgnoreCase("TABLE")) {
					response.append(this.performTableToken(d, list));
					
				} else if (type.equalsIgnoreCase("TABLE_V")) {
					response.append(this.performVTableToken(d, list));
					
				} else if (type.equalsIgnoreCase("OL") || type.equalsIgnoreCase("UL")) {
					response.append("<" + type + ">");
					for (int i=1; i<list.size(); i++) {
						Vector<Object> row = (Vector<Object>)list.elementAt(i);
						for (int j=0; j<row.size(); j++) {
							response.append("<li>" + row.elementAt(j) + "</li>");	
						}
					}
					response.append("</" + type + ">");					
										
				} else {
					for (int i=1; i<list.size(); i++) {
						Vector<Object> row = (Vector<Object>)list.elementAt(i);
						for (int j=0; j<row.size(); j++) {
							response.append(row.elementAt(j));	
						}
					}
				}					
			}			
		}
		
		String content = StringUtil.formatWordToHtml(response.toString());
		
		return content;
	}

	private StringBuffer performTableToken(Document d, Vector<Vector<Object>> list) {
		StringBuffer response = new StringBuffer("");
		
		String header = this.getHeader(d, response);
		for (int i=1; i<list.size(); i++) {
			Vector<Object> row = (Vector<Object>)list.elementAt(i);					
			response.append("<tr>");
			for (int j=0; j<row.size(); j++) {
				response.append("<td>" + row.elementAt(j) + "</td>");	
			}
			response.append("</tr>");
		}		
		
		if (header!=null && !header.trim().equals("")) {
			response.append("</table>");
		}
		
		return response;
	}

	private StringBuffer performVTableToken(Document d, Vector<Vector<Object>> list) {
		StringBuffer response = new StringBuffer("");
		
		ArrayList<String> titles = new ArrayList<String>();
		if (d!=null) {
			String titLbl = XmlDomParse.getAttributeTextByTag(d.getFirstChild(), "titles");
			if (titLbl!=null && !titLbl.trim().equals("")) {
				String[] titLbls = titLbl.split("\\|");
				if (titLbls!=null && titLbls.length>0) {
					for(int t=0; t<titLbls.length; t++) {
						titles.add(titLbls[t]);				
					}							
				}
			}
		}
		
		for (int i=1; i<list.size(); i++) {
			response.append("<table border=\"1\" cellspacing=\"0\" cellpadding=\"0\"><tbody>");			
			Vector<Object> row = (Vector<Object>)list.elementAt(i);					
			for (int j=0; j<row.size(); j++) {
				response.append("<tr>");				
				if (titles.size()>j) {
					response.append("<td><b>" + titles.get(j) + "</b></td>");	
				} else {
					response.append("<td>&nbsp;</td>");
				}
				response.append("<td>" + row.elementAt(j) + "</td>");
				response.append("</tr>");
			}
			response.append("</table></br>");			
		}
		
		return response;
	}


	private String getHeader(Document d, StringBuffer response) {
		String header = null;
		if (d!=null) {
			header = XmlDomParse.getAttributeTextByTag(d.getFirstChild(), "header");
			if (header!=null && !header.trim().equals("")) {
				String[] headers = header.split("\\|");
				if (headers!=null && headers.length>0) {
					response.append("<table><tr>");
					for(int h=0; h<=headers.length; h++) {
						response.append("<td><h3>" + headers[h] + "</h3></td>");				
					}
					response.append("</tr>");								
				} else {
					header = null;
				}
			}
		}
		return header;
	}
	
	/*
	private String getSQL(String rowSql, ProjectTO pto, UserTO uto) throws BusinessException {
		ProjectDelegate pdel = new ProjectDelegate();
		String response = rowSql;

		response = response.replaceAll("?" + ReportTO.PROJECT_ID, "'" + pto.getId() + "'");
		response = response.replaceAll("?" + ReportTO.USER_ID, "'" + uto.getId() + "'");
		if (rowSql.indexOf(ReportTO.PROJECT_DESCENDANT)>-1) {
			String childList = pdel.getProjectIn(pto.getId());
			response = response.replaceAll("?" + ReportTO.PROJECT_DESCENDANT, childList);
		}
		
		return response;
	}
	*/
	
	
	private void checkRepositoryPolicy(ProjectTO pto, String logMessage, String author,
			String filePath, UserTO handler) throws BusinessException {
		ProjectBUS pbus = new ProjectBUS();		
    	RepositoryMessageIntegration rep = new RepositoryMessageIntegration();
    	rep.setAuthor(author);
    	rep.setProjectId(pto.getId());
    	rep.setFiles(filePath);
    	rep.setComment(logMessage);
    	rep.setRepositoryPath(pto.getRepositoryURL());
    	String respMessage = pbus.applyRepositoryPolicies(rep, true);    	
		if (!respMessage.trim().equals("0")) {
			throw new RepositoryPolicyException("label.formRepository.msg." + respMessage);
		}
	}
	
	
	private long getMaxArtifactSize() throws BusinessException{
		long size = 307200;
		UserDelegate udel = new UserDelegate();
		UserTO root = udel.getRoot();
		String mxSize = root.getPreference().getPreference(PreferenceTO.ARTIFACT_MAX_SIZE);
		try {
			size = Long.parseLong(mxSize);
		} catch (Exception e){
			e.printStackTrace();
			size = 307200;
		}
		return size;
	}
	
	
	private String preProcess(String content, ProjectTO project, UserTO uto, String fileName) throws BusinessException{
		ProjectDelegate pdel = new ProjectDelegate();
		if (content.indexOf(ReportTO.PROJECT_ID)>0) {
			content = content.replaceAll("\\?" + ReportTO.PROJECT_ID, project.getId());	
		}
		if (content.indexOf(ReportTO.USER_ID)>0) {
			content = content.replaceAll("\\?" + ReportTO.USER_ID, uto.getId());	
		}
		if (content.indexOf(ArtifactTemplateTO.USER_NAME)>0) {
			content = content.replaceAll("\\?" + ArtifactTemplateTO.USER_NAME, uto.getUsername());	
		}
		if (content.indexOf(ArtifactTemplateTO.USER_FULLNAME)>0) {
			content = content.replaceAll("\\?" + ArtifactTemplateTO.USER_FULLNAME, uto.getName());	
		}		
		if (content.indexOf(ArtifactTemplateTO.CURDATE_MM_DD_YYYY)>0) {
			content = content.replaceAll("\\?" + ArtifactTemplateTO.CURDATE_MM_DD_YYYY, DateUtil.getDateTime(DateUtil.getNow(), "MM/dd/yyyy"));	
		}
		if (content.indexOf(ArtifactTemplateTO.CURDATE_DD_MM_YYYY)>0) {
			content = content.replaceAll("\\?" + ArtifactTemplateTO.CURDATE_DD_MM_YYYY, DateUtil.getDateTime(DateUtil.getNow(), "dd/MM/yyyy"));	
		}
		if (content.indexOf(ArtifactTemplateTO.CURDATE_YYYYMMDD)>0) {
			content = content.replaceAll("\\?" + ArtifactTemplateTO.CURDATE_YYYYMMDD, DateUtil.getDateTime(DateUtil.getNow(), "yyyyMMdd"));	
		}
		if (content.indexOf(ArtifactTemplateTO.ARTIFACT_NAME)>0) {
			content = content.replaceAll("\\?" + ArtifactTemplateTO.ARTIFACT_NAME, fileName);	
		}
		if (content.indexOf(ReportTO.PROJECT_DESCENDANT)>0) {
			String descendant = pdel.getProjectIn(project.getId());
			content = content.replaceAll("\\?" + ReportTO.PROJECT_DESCENDANT, descendant);	
		}
		
		return content;
	}
}
