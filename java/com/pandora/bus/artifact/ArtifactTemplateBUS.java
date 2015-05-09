package com.pandora.bus.artifact;

import java.util.Vector;

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
						
        } catch (DataAccessException e) {
            throw new BusinessException(e);
        }
		return response;
	}


	public void commitArtifact(String path, String fileName, String exportType, String body, String planningId, String templateId, 
			String projectId, String logMessage, String user, String pass, UserTO handler) throws MaxSizeAttachmentException, BusinessException {
		RepositoryBUS rbus = new RepositoryBUS();
		ProjectBUS pbus = new ProjectBUS();

		byte[] fileData = null;
		long size = 307200;
		ProjectTO pto = null;
		ArtifactExport ae = null;
		String filePath = "";
		
        try {		
			if (path!=null && !path.equals("")) {
				filePath = filePath + path + "/";
			} 
			filePath = filePath + fileName;

			//get related artifact class converter and clean the extension of file path and name  
			ae = getArtifactExpClass(exportType);
			fileName = fileName.replaceAll(ae.getExtension(), "");
			filePath = filePath.replaceAll(ae.getExtension(), "");
			
			ArtifactTemplateTO atto = (ArtifactTemplateTO) dao.getObject(new ArtifactTemplateTO(templateId));
			pto = pbus.getProjectObject(new ProjectTO(projectId), true);
			
			//perform the key-word transformation into header and footer
			String header = atto.getHeader();
			header = this.parseKeyWords(header, pto, handler, fileName);
			String footer = atto.getFooter();
			footer = this.parseKeyWords(footer, pto, handler, fileName);

			//check the commit policies...
			this.checkRepositoryPolicy(pto, logMessage, user, filePath, handler);

			//transform the artifact content to selected format...			
			fileData = ae.export(header, body, footer);

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
			//TODO para commit no SVN ainda falta a atualizacao de um arquivo existente. 
			rbus.commitFile(pto, path, fileName + ae.getExtension(), RepositoryFileTO.REPOSITORY_HEAD, 
					logMessage, ae.getContentType(), fileData, user, pass);

			//create a link between repository file and planningId (if necessary)
			if (planningId!=null && !planningId.trim().equals("")) {
				rbus.updateRepositoryFilePlan(filePath + ae.getExtension(), planningId, pto, ae.getClass().getName(), false);
			}

        } catch (Exception e) {
            throw new BusinessException(e);
        }
	}


	/**
	 * Static method used to return a instance of artifact export class 
	 */
	@SuppressWarnings("unchecked")
	public static ArtifactExport getArtifactExpClass(String className){
		ArtifactExport response = null;
        try {
            Class klass = Class.forName(className);
            response = (ArtifactExport)klass.newInstance();
        } catch (Exception e) {
            response = null;
        }
        return response;
	}
	
	
	private String parseKeyWords(String body, ProjectTO project, UserTO uto, String fileName) throws BusinessException {
		StringBuffer response = new StringBuffer();
		int f =0;
		
		if (body!=null) {
			body = this.preProcess(body, project, uto, fileName);

			int cutCursor = 0;
			int i = body.indexOf("?#");
			while (i>0) {
				f = body.indexOf("#", i+2);
				if (f>0 && f > i+2) {
					String token = body.substring(i+2, f);
					String content = this.peformToken(token);
					response.append(body.substring(cutCursor, i));
					response.append(content);
					cutCursor = f + 1;
					i = body.indexOf("?#", f);
				} else {
					i = -1;
				}
			}
			response.append(body.substring(cutCursor));			
		}
		
		return response.toString();
	}	
	
	
	private String peformToken(String token) throws BusinessException{
		StringBuffer response = new StringBuffer("");
		DbQueryDelegate db = new DbQueryDelegate();
		
		String[] tokens = token.split("\\|");
		if (tokens.length>=2) {
			String type = tokens[1].trim();
			Vector<Vector<Object>> list = db.performQuery(tokens[0].trim());
			if (list.size()>1) {
				if (type.equalsIgnoreCase("TABLE")) {
					for (int i=1; i<list.size(); i++) {
						Vector<Object> row = (Vector<Object>)list.elementAt(i);					
						response.append("<tr>");
						for (int j=0; j<row.size(); j++) {
							response.append("<td>" + row.elementAt(j) + "</td>");	
						}
						response.append("</tr>");
					}
					
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
