package com.pandora.gui.struts.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.actions.DispatchAction;
import org.apache.struts.util.MessageResources;

import com.pandora.AdditionalFieldTO;
import com.pandora.AdditionalTableTO;
import com.pandora.MetaFieldTO;
import com.pandora.PlanningRelationTO;
import com.pandora.PlanningTO;
import com.pandora.ProjectTO;
import com.pandora.RepositoryFilePlanningTO;
import com.pandora.RepositoryFileTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.SystemSingleton;
import com.pandora.delegate.DiscussionTopicDelegate;
import com.pandora.delegate.PlanningDelegate;
import com.pandora.delegate.PlanningRelationDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.InvalidRelationException;
import com.pandora.exception.MetaFieldNumericTypeException;
import com.pandora.gui.struts.form.GeneralErrorForm;
import com.pandora.gui.struts.form.GeneralStrutsForm;
import com.pandora.gui.struts.form.GeneralSuccessForm;
import com.pandora.gui.struts.form.HosterRepositoryForm;
import com.pandora.gui.taglib.artifacts.Artifact;
import com.pandora.gui.taglib.decorator.RepositoryEntityCheckBoxDecorator;
import com.pandora.gui.taglib.decorator.RepositoryEntityRadioBoxDecorator;
import com.pandora.gui.taglib.decorator.RepositoryEntryNameDecorator;
import com.pandora.gui.taglib.decorator.RepositoryEntryTypeDecorator;
import com.pandora.gui.taglib.table.PTableHelper;
import com.pandora.helper.DateUtil;
import com.pandora.helper.LogUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

/**
 * Generic struts action. All action classes of project must extends this class 
 * in order to contain commons methods. 
 */
public class GeneralStrutsAction extends DispatchAction{
   

	/**
	  * into http session, the errorForm bean to be used on generic error form.  
	  * @param request The request
	  * @param errorMessage The key message in bundle to display
	  * @param exceptionMessage The exception message to display in source 
	  */
	public void setErrorFormSession(HttpServletRequest request, String errMsg, Exception e) {
		setErrorFormSession(request, errMsg, null, null, null, null, null, e);
	}

	public void setErrorFormSession(HttpServletRequest request, String errMsg, String arg0, String arg1, String arg2, String arg3, String arg4, Exception e) {
		try {
			GeneralErrorForm errFrm = new GeneralErrorForm();
			errFrm.setErrorMessage(errMsg, arg0, arg1, arg2, arg3, arg4);
			if (e!=null){
			    if (e.getMessage()==null) {
			        errFrm.setExceptionMessage(e.toString());    
			    } else {
			        errFrm.setExceptionMessage(e.getMessage());
			    }
			    errFrm.setStackTrace(StringUtil.getStackTraceToString(e));
			}
			request.getSession().setAttribute("errorForm", errFrm);
			request.getSession().removeAttribute("successForm");
		
		}catch(Exception ee) {
			ee.printStackTrace();
		}
	}
	
	/**
	  * Auxiliar Method. Set into http session, the successForm bean to be used on forms to show a successfully message.  
	  * @param request The request
	  * @param successMessage The key message in bundle to display
	  */
	public void setSuccessFormSession(HttpServletRequest request, String successMessage) {
		try {
			GeneralSuccessForm succFrm = new GeneralSuccessForm();
			succFrm.setSuccessMessage(successMessage);
			request.getSession().setAttribute("successForm", succFrm);
			request.getSession().removeAttribute("errorForm");
		}catch(Exception ee) {
			ee.printStackTrace();
		}			
	}

	/**
	 * Clear error and success objects from http session
	 * @param request
	 */
	protected void clearMessages(HttpServletRequest request){
		try {
			request.getSession().removeAttribute("errorForm");
			request.getSession().removeAttribute("successForm");
		}catch(Exception ee) {
			ee.printStackTrace();
		}
	}
		
	
	protected Vector<TransferObject> getEnableList(HttpServletRequest request){
	    Vector<TransferObject> response = new Vector<TransferObject>();

	    TransferObject option1 = new TransferObject();
	    option1.setId("1");
	    option1.setGenericTag(this.getBundleMessage(request, "label.yes"));
	    response.addElement(option1);

	    TransferObject option2 = new TransferObject();
	    option2.setId("0");
	    option2.setGenericTag(this.getBundleMessage(request, "label.no"));
	    response.addElement(option2);
	    
	    return response;
	}		

	
	public ActionForward requestArtifactBody(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		Artifact tartifact = new Artifact();
	    RepositoryDelegate rdel = new RepositoryDelegate();
	    
		try {
			String gridParam = request.getParameter("ajaxGridParam");
			String projectId = null;
			String entityId = null;
			String artifactId = null;
			Vector<RepositoryFilePlanningTO> list = new Vector<RepositoryFilePlanningTO>();
			
			if (gridParam!=null) {
				gridParam = new String(gridParam.getBytes(), this.getBundleMessage(request, "encoding"));
				
				String[] params = gridParam.split("\\|");
				if (params!=null && params.length>=2) {
					String[] projToken = params[0].split("=");
					if (projToken!=null && projToken.length==2) {
						projectId = projToken[1];
					}
					String[] entiToken = params[1].split("=");
					if (entiToken!=null && entiToken.length==2) {
						entityId = entiToken[1];
						list = rdel.getFilesFromPlanning(new PlanningTO(entityId));
					}
					if (params.length>2) {
						String[] artifToken = params[2].split("=");
						if (artifToken!=null && artifToken.length==2) {
							artifactId = artifToken[1];
						}						
					}
				}

			} else {
				gridParam = "";
			}

			UserTO uto = SessionUtil.getCurrentUser(request);
			String chartset = SystemSingleton.getInstance().getDefaultEncoding();
 	        response.setContentType("text/html; charset=" + chartset);
	        response.setHeader("Cache-Control", "no-cache");
			
        	String title = getBundleMessage(request, "label.artifactTag.title");
        	String newArtifact = getBundleMessage(request, "label.artifactTag.new");
        	String browse = getBundleMessage(request, "label.artifactTag.browse");
        	String confirmation = getBundleMessage(request, "label.artifactTag.removelink.confirm");	        
        	
	        PrintWriter out = response.getWriter();
	        tartifact.setOnlyBody("true");
	        tartifact.setAjax("true");
	        tartifact.setName("mindMapForm");
		    StringBuffer body = tartifact.renderArtifact(list, entityId, projectId, artifactId, uto, title, newArtifact, browse, confirmation, request.getSession());
		    if (body!=null) {
		    	out.println(body.toString());	
		    } else {
		    	out.println("");
		    }
	        out.flush();				
			
		} catch(Exception e){
			e.printStackTrace();
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
	    return null;		
	}
	
	
	public ActionForward requestPTableBody(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		PTableHelper tHelper = new PTableHelper();
		
		try {
			String gridId = request.getParameter("ajaxGridId");
			String gridParam = request.getParameter("ajaxGridParam");
			if (gridParam!=null) {
				gridParam = new String(gridParam.getBytes(), this.getBundleMessage(request, "encoding"));	
			} else {
				gridParam = "";
			}

			String chartset = SystemSingleton.getInstance().getDefaultEncoding();
 	        response.setContentType("text/html; charset=" + chartset);
	        response.setHeader("Cache-Control", "no-cache");
			
	        PrintWriter out = response.getWriter();  	       
	        
	        if ( gridParam.startsWith("show_panel") || gridParam.startsWith("search_like") ) {

	        	String panel = tHelper.reloadPanelList(gridId, request, gridParam);
	        	out.println(panel);
	        	
	        } else {
			    String body = tHelper.getTableBody(gridId, request, gridParam);					
			    String header = tHelper.getTableHeader(gridId, request, gridParam);
			    
		        header = header.replaceAll("&nbsp;", "");
		        body = body.replaceAll("&nbsp;", "");

		        out.println(header);
		        out.println(body);	        	
	        }
	         
	        out.flush();				
			
		} catch(Exception e){
			e.printStackTrace();
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
	    return null;
	}	

	
	public ActionForward requestPTableExport(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		PrintWriter out = null;
		PTableHelper tHelper = new PTableHelper();
		try {
			String gridId = request.getParameter("ajaxGridId");
	        if (gridId != null) {

	        	UserTO uto = SessionUtil.getCurrentUser(request);
	        	String userEnconding = uto.getBundle().getMessage(uto.getLocale(), "encoding");
	        	String content = tHelper.getCsvContent(gridId, request, "");
	        	
	            response.setContentType("text/csv; charset=" + userEnconding);  
	            response.setHeader("Cache-Control", "no-cache");
	            response.setHeader("Content-Disposition", "attachment; filename=\"grid.csv\"");	            
	            out = response.getWriter();  
	            out.println(content);  
	            out.flush();											
	        }
		}catch(Exception ee) {
			ee.printStackTrace();
		} finally {
			try {			
				if (out != null) {
					out.close();
				}
				
			} catch (Exception ex) {
				ex.printStackTrace();				
	        }
		}
		return null;		
	}
	
	
	/**
	 * This method redirect to home page of system. 
	 */
	public ActionForward backward(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "home";
		ActionForward fwrd = null;
		
		try {
			//clear messages of form
			this.clearMessages(request);
		
			fwrd = mapping.findForward(forward);
			
		}catch(Exception ee) {
			ee.printStackTrace();
		}
		
		return fwrd;		
	}

	
	/**
	 * This method receive the content of grids into http post payload
	 * and convert it to an export format.
	 */
	public ActionForward exportGrid(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		PrintWriter out = null;
		try {
			String exportkey = request.getParameter("exportkey");
			StringBuffer sb = (StringBuffer)request.getSession().getAttribute(exportkey);
	        if (sb != null) {
	        	
	        	UserTO uto = SessionUtil.getCurrentUser(request);
	        	String userEnconding = uto.getBundle().getMessage(uto.getLocale(), "encoding");
	        	
	            response.setContentType("text/csv; charset=" + userEnconding);  
	            response.setHeader("Cache-Control", "no-cache");
    			response.setHeader("Content-Disposition", "attachment; filename=\"grid.csv\"");	            
	            out = response.getWriter();  
	            out.println(sb.toString());  
	            out.flush();											
	        }
		}catch(Exception ee) {
			ee.printStackTrace();
		} finally {
			try {			
				if (out != null) {
					out.close();
				}
				
			} catch (Exception ex) {
				ex.printStackTrace();				
	        }
		}
		return null;		
	}
	
	
	public ActionForward browseRepository(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		try {
			RepositoryEntityCheckBoxDecorator chkDec = new RepositoryEntityCheckBoxDecorator();
			RepositoryEntityRadioBoxDecorator radDec = new RepositoryEntityRadioBoxDecorator();
			RepositoryEntryTypeDecorator typeDec = new RepositoryEntryTypeDecorator();
			RepositoryEntryNameDecorator nameDec = new RepositoryEntryNameDecorator();
			RepositoryViewerAction repAct = new RepositoryViewerAction();
			ProjectDelegate pdel = new ProjectDelegate();

			HosterRepositoryForm frm = (HosterRepositoryForm)form;
			chkDec.setSession(request.getSession());
			radDec.setSession(request.getSession());
			
			StringBuffer sb = new StringBuffer("");
			String projectId = frm.getId();
			String path = frm.getGenericTag();
			
			if (path!=null && projectId!=null) {
				boolean onlyFolders = (frm.getOnlyFolders()!=null && frm.getOnlyFolders().equalsIgnoreCase("on"));
				boolean multiple = (frm.getMultiple()!=null && frm.getMultiple().equalsIgnoreCase("on"));
				ProjectTO pto = pdel.getProjectObject(new ProjectTO(projectId), true);
				
				Vector<RepositoryFileTO> fileList = repAct.getItens(path, projectId, "-1", true, onlyFolders);				
				if (fileList!=null) {
					Locale loc = SessionUtil.getCurrentLocale(request);
					
					sb.append("<table class=\"table\" width=\"100%\" border=\"1\" bordercolor=\"#10389C\" cellspacing=\"1\" cellpadding=\"2\">");
					sb.append("<tr><td colspan=\"10\"></td></tr>");
					
					sb.append("<tr class=\"tableRowHeader\">");
					sb.append("<th width=\"10\" align=\"center\" class=\"tableCellHeader\">&nbsp;</th>");
					sb.append("<th width=\"10\" align=\"center\" class=\"tableCellHeader\">&nbsp;</th>");
					sb.append("<th align=\"left\" class=\"tableCellHeader\">" + getBundleMessage(request, "label.formRepository.name") + "</th>");
					if (!onlyFolders) {
						sb.append("<th width=\"100\" align=\"center\" class=\"tableCellHeader\">" + getBundleMessage(request, "label.formRepository.author") + "</th>");
						sb.append("<th width=\"80\" class=\"tableCellHeader\">" + getBundleMessage(request, "label.formRepository.size") + "</th>");						
					}
					sb.append("<th width=\"50\" class=\"tableCellHeader\">" + getBundleMessage(request, "label.formRepository.revision") + "</th>");
					sb.append("<th width=\"130\" align=\"center\" class=\"tableCellHeader\">" + getBundleMessage(request, "label.formRepository.date") + "</th>");
					sb.append("</tr>");
					
					Iterator<RepositoryFileTO> i = fileList.iterator();
					while(i.hasNext()) {
						RepositoryFileTO rfto = i.next();
						rfto.setPlanning(pto);
						
						typeDec.initRow(rfto, 0, 0);
						chkDec.initRow(rfto, 0, 0);
						radDec.initRow(rfto, 0, 0);
						
						chkDec.setSession(request.getSession());						
						radDec.setSession(request.getSession());
						
						sb.append("<tr class=\"tableRowEven\">");
						sb.append("<td class=\"tableCell\" width=\"10\" align=\"center\" valign=\"top\">" + typeDec.decorate(rfto.getIsDirectory()) + "</td>");
						
						if (multiple) {
							sb.append("<td class=\"tableCell\" width=\"10\" align=\"center\" valign=\"top\">" + chkDec.decorate(rfto.getId()) + "</td>");							
						} else {
							sb.append("<td class=\"tableCell\" width=\"10\" align=\"center\" valign=\"top\">" + radDec.decorate(rfto.getId()) + "</td>");							
						}

						sb.append("<td class=\"tableCell\" align=\"left\" valign=\"top\">");
						if (rfto.getIsDirectory().booleanValue()) {
							String newpath = nameDec.formatPath(rfto.getPath(),  pto.getRepositoryURL());
							sb.append("<a class=\"gridLink\" href=\"javascript:clickBrowseRepository('" + newpath + "', '" + projectId + "');\" border=\"0\">"+ rfto.getName() + "</a>");		
						} else {						
							sb.append(rfto.getName());
						}
						sb.append("</td>");
						
						if (!onlyFolders) {
							sb.append("<td  class=\"tableCell\" align=\"center\" valign=\"top\">" + rfto.getAuthor() + "</td>");
							sb.append("<td  class=\"tableCell\" align=\"center\" valign=\"top\">" + rfto.getFileSize() + "</td>");							
						}
						sb.append("<td  class=\"tableCell\" align=\"center\" valign=\"top\">" + (rfto.getRevision()!=null?rfto.getRevision():"&nbsp;") + "</td>");
						sb.append("<td  class=\"tableCell\" align=\"center\" valign=\"top\">" + (rfto.getCreationDate()!=null?DateUtil.getDateTime(rfto.getCreationDate(), loc, 2, 2):"&nbsp") + "</td>");
						sb.append("</tr>");
					}				
				}
							
	            response.setContentType("text/html");  
	            response.setHeader("Cache-Control", "no-cache");  
	            PrintWriter out = response.getWriter();  
	            out.println(sb.toString());  
	            out.flush();							
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}	
		
		return null;
	}

	public ActionForward checkBoxRepository(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		try {
			RepositoryDelegate rdel = new RepositoryDelegate();
			GeneralStrutsForm frm = (GeneralStrutsForm)form;			
			String entityId = frm.getId();
			String path = frm.getGenericTag();
			String chartset = SystemSingleton.getInstance().getDefaultEncoding();
			path = URLDecoder.decode(path, chartset);
			rdel.updateRepositoryFilePlan(path, entityId, null, true);
		} catch(Exception e) {
			e.printStackTrace();
		}			
		return null;
	}

	
	public ActionForward radioBoxRepository(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		try {
			GeneralStrutsForm frm = (GeneralStrutsForm)form;			
			String path = frm.getGenericTag();
			String chartset = SystemSingleton.getInstance().getDefaultEncoding();
			path = URLDecoder.decode(path, chartset);
			request.getSession().setAttribute(RepositoryEntityRadioBoxDecorator.REPOSITORY_SELECTED_PATH, path);
		} catch(Exception e) {
			e.printStackTrace();
		}			
		return null;
	}
	

	public ActionForward breakRepositoryEntityLink(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		try {
			RepositoryDelegate rdel = new RepositoryDelegate();
			GeneralStrutsForm frm = (GeneralStrutsForm)form;			
			String entityId = frm.getId();
			String pathId = frm.getGenericTag();
			rdel.breakRepositoryEntityLink(pathId, entityId);
		} catch(Exception e) {
			e.printStackTrace();
		}			
		return null;
	}
	
	/**
	 * This method is dispatched from the discussion topic TAG LIB 
	 */
	public ActionForward replyDiscussion(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		ActionForward forward = null;
		DiscussionTopicDelegate dtDel = new DiscussionTopicDelegate();
		try {
			String uri = (String)request.getSession().getAttribute("REPLY_FORWARD");			
			GeneralStrutsForm frm = (GeneralStrutsForm)form;
			String topicInfo = frm.getGenericTag();
			String[] tokens = topicInfo.split("#");
			if (tokens.length==3) {
				String replyContent = request.getParameter(topicInfo);
				if (replyContent!=null && !replyContent.trim().equals("")) {
					String planningId = tokens[2];
					if (planningId!=null && !planningId.trim().equals("")) {
						String parentId = tokens[1];
						if (parentId!=null && parentId.trim().equals("")) {
							parentId = null;
						}
						dtDel.replyDiscussionTopic(planningId, parentId, replyContent, frm.getCurrentUser());
					}
				}
			}

			RequestDispatcher dispatcher = request.getRequestDispatcher("../do/" + uri);   
			dispatcher.forward(request, response);
  			
		} catch(Exception e) {
			this.setErrorFormSession(request, "error.formForum.saveTopic", e);
		}
		
		return forward;		
	}


	public ActionForward removeTopic(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		ActionForward forward = null;
		DiscussionTopicDelegate dtDel = new DiscussionTopicDelegate();
		try {
			String uri = (String)request.getSession().getAttribute("REPLY_FORWARD");			
			GeneralStrutsForm frm = (GeneralStrutsForm)form;
			String topicInfo = frm.getGenericTag();
			if (topicInfo!=null && !topicInfo.trim().equals("")) {
				UserTO uto = SessionUtil.getCurrentUser(request);
				dtDel.removeDiscussionTopic(topicInfo, uto);
				LogUtil.log(this, LogUtil.LOG_INFO, "The topic [" + topicInfo + 
									"] was removed by user id: [" + uto.getUsername() + 
									"]", null);				
			}
			RequestDispatcher dispatcher = request.getRequestDispatcher("../do/" + uri);   
			dispatcher.forward(request, response);
			
		} catch(Exception e) {
			this.setErrorFormSession(request, "error.formForum.saveTopic", e);
		}
		
		return forward;		
	}

	
	
	public ActionForward removeLinkRelation(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		PlanningRelationDelegate del = new PlanningRelationDelegate();
		String fwd = request.getParameter("RELATION_FORWARD");
		
		try {
			String entityId = request.getParameter("SOURCE_ENTITY_ID");
			String relatedId = request.getParameter("linkRelation");
			String collectionKey = request.getParameter("COLLECTION_KEY");
			
			PlanningRelationTO prto = new PlanningRelationTO();
			prto.setPlanning(new PlanningTO(entityId));
			prto.setRelated(new PlanningTO(relatedId));
			del.removeRelation(prto);
			if (request.getSession().getAttribute(collectionKey)!=null) {
				@SuppressWarnings("rawtypes")
				Vector list = (Vector)request.getSession().getAttribute(collectionKey);
				@SuppressWarnings("unchecked")
				Iterator<PlanningRelationTO> i = list.iterator();
				while (i.hasNext()) {
					PlanningRelationTO item = (PlanningRelationTO)i.next();
					if (item.getPlanning().getId().equals(entityId) &&	item.getRelated().getId().equals(relatedId)) {
						list.remove(item);	
						break;
					}
				}
			}
			
		}catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		
		return mapping.findForward(fwd);		
		
	}

	@SuppressWarnings("unchecked")
	public ActionForward linkRelation(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		PlanningRelationDelegate del = new PlanningRelationDelegate();
		PlanningDelegate pdel = new PlanningDelegate();
		String fwd = request.getParameter("RELATION_FORWARD");
		
		try {
			String entityId = request.getParameter("SOURCE_ENTITY_ID");
			String relationType = request.getParameter("RELATION_TYPE");
			String collectionKey = request.getParameter("COLLECTION_KEY");
			
			//check if the entity exists
			String relatedId = request.getParameter("linkRelation");
			PlanningTO filter = new PlanningTO(relatedId);
			PlanningTO related = pdel.getSpecializedObject(filter);
			
			if (related!=null) {
				
				//if exists, fetch the TransferObject of source planning object 
				filter = new PlanningTO();
				filter.setId(entityId);
				PlanningTO planning = pdel.getSpecializedObject(filter);
				if (planning!=null) {
					
					//create a link between the current entity and the related entity
					PlanningRelationTO prto = new PlanningRelationTO();
					prto.setPlanning(planning);
					prto.setRelated(related);
					prto.setPlanType(planning.getType());
					prto.setRelatedType(related.getType());
					prto.setRelationType(relationType);
					
					if (planning.getId().equals(related.getId())) {
						this.setErrorFormSession(request, "label.relationTag.autoRelError", null);							
					} else {
						try {
							del.insertRelation(prto);
							if (request.getSession().getAttribute(collectionKey)!=null) {
								@SuppressWarnings("rawtypes")
								Vector list = (Vector)request.getSession().getAttribute(collectionKey);
								list.add(prto);							
							}
						} catch(InvalidRelationException e){
							this.setErrorFormSession(request, "label.relationTag.invalidRelation", null);
						} catch(Exception e){
							this.setErrorFormSession(request, "label.relationTag.pkError", null);	
						}						
					}

				} else {
					this.setErrorFormSession(request, "error.generic.showFormError", null);
				}

			} else {
				this.setErrorFormSession(request, "label.relationTag.searchNotFound", null);	
			}
			
		}catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		
		return mapping.findForward(fwd);		
	}

	
	public ActionForward getUserInfo(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    try {
	        GeneralStrutsForm frm = (GeneralStrutsForm)form;
	        UserDelegate udel = new UserDelegate();	        
	        UserTO uto = new UserTO();
	        uto.setUsername(frm.getId());
	        uto = udel.getObjectByUsername(uto);
	        String content = "???";
	        
	        if (uto!=null) {
	        	String phone = "&nbsp;";
	        	if (uto.getPhone()!=null) {
	        		phone = uto.getPhone();
	        	}

	        	String email = "&nbsp;";
	        	if (uto.getEmail()!=null) {
	        		email = uto.getEmail();
	        	}
	        	
	            content = "<table cellpadding=\"0\" border=\"0\" cellspacing=\"0\" width=\"100%\">" +
	            		  "<tr class=\"tableRowOdd\">" +
	            		  "<td width=\"50\" rowspan=\"3\"><img width=\"50\" height=\"60\" border=\"0\" src=\"../do/login?operation=getUserPic&id=" + uto.getId() + "&ts=" +DateUtil.getNow().toString() + "\"></td>" +
	            		  "<td width=\"3\">&nbsp;</td>" +
	            		  "<td class=\"successfullyMessage\"><center>" + uto.getName() + "</center></td>" +
	            		  "</tr>" +
	            		  "<tr class=\"tableRowOdd\"><td>&nbsp;</td><td class=\"formNotes\"><center>" + email + "</center></td></tr>" +
	            		  "<tr class=\"tableRowOdd\"><td>&nbsp;</td><td class=\"formNotes\"><center>" + phone + "</center></td></tr>" +
	            		  "</table>"; 
	        } else {
	            content = "<table cellpadding=\"0\" border=\"0\" cellspacing=\"0\" width=\"100%\">" +
	            			"<tr class=\"tableRowOdd\"><td>&nbsp;</td>" +
	            				"<td class=\"formNotes\"><center>" + getBundleMessage(request, "error.showUserInfo") + "</center></td>" +
	            			"</tr></table>"; 	        	
	        }

            response.setContentType("text/xml");  
            response.setHeader("Cache-Control", "no-cache");  
            PrintWriter out = response.getWriter();  
            out.println(content);  
            out.flush();	        
	        
	    } catch(Exception e){
	        this.setErrorFormSession(request, "error.generic.showFormError", e);
	    }
	    return null;
	}
	
	
	public ActionForward getUserPic(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
	    ServletOutputStream sos = null;
	    byte[] bytes = null;
	    
		try {
		    GeneralStrutsForm frm = (GeneralStrutsForm)form;
	        UserDelegate udel = new UserDelegate();	        
	        UserTO uto = udel.getUser(new UserTO(frm.getId()));
			
		    if (uto!=null) {
		        sos = response.getOutputStream();
		        
			    if (uto.getFileInBytes()!=null && uto.getFileInBytes().length >0 ){
					bytes = uto.getFileInBytes();
					
			    } else {
			        java.net.URL defaultPic = this.getClass().getClassLoader().getResource("../../images/emptypic.png");
			        String filepath = defaultPic.getFile();
					String chartset = SystemSingleton.getInstance().getDefaultEncoding();			        
			        filepath = URLDecoder.decode(filepath, chartset);
			        File file = new File(filepath);
			        InputStream is = new FileInputStream(file);
			        bytes = new byte[(int)file.length()];
		    
			        // Read in the bytes
			        int offset = 0;
			        int numRead = 0;
			        while (offset < bytes.length
			               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
			            offset += numRead;
			        }
			    }

				response.setContentType("image");
				response.setContentLength(bytes.length);
				sos.write(bytes);		        
		    }
			
		}catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		} finally {
		    try {
		        if (sos!=null) {
		            sos.close();
		        }
			} catch (Exception e2) {
			    this.setErrorFormSession(request, "error.generic.showFormError", null);    
			}
		}

		return null;	    
	}
	

   	public ActionForward getBundleMessage(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
	    ServletOutputStream sos = null;
		try {
			String content = "OK";
			//example: localhost:8080/pandora/do/login?operation=getBundleMessage&key=label.resHome.grabTask.confirm
			
			String key = request.getParameter("key");
			if (key!=null && !key.trim().equals("")) {
				content = getBundleMessage(request, key, true);
		        sos = response.getOutputStream();
				response.setContentType("text/plain");
				response.setContentLength(content.length());
				sos.write(content.getBytes());		        				
			}

		}catch(Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		} finally {
		    try {
		        if (sos!=null) {
		            sos.close();
		        }
			} catch (Exception e2) {
			    this.setErrorFormSession(request, "error.generic.showFormError", null);    
			}
		}
		return null;
   	}

   		
   	/**
   	 * This method get a value from resource bundle and can be used for all decorators.
  	 */
   	protected String getBundleMessage(HttpServletRequest request, String key){
        return getBundleMessage(request, key, false);
   	}

   	
    public String getCalendarMask(HttpServletRequest request){
   		return getBundleMessage(request, "calendar.format", false);
    }

    
   	/**
   	 * This method get a value from resource bundle and can be used for all decorators.
   	 * @param request
   	 * @param key
   	 * @param isTolerant if is true, the method return the key if the message isn't into the bundle. 
   	 * @return
   	 */
   	protected String getBundleMessage(HttpServletRequest request, String key, boolean isTolerant){
   	    String value = "";
   	    try {
	   	    MessageResources mr = this.getResources(request);
			value = mr.getMessage(request.getLocale(), key);
			if (isTolerant) {
		        if (value.startsWith("???")) {
		            value = key;
		        }		    
			}
		}catch(Exception ee) {
			ee.printStackTrace();
		}
        return value;
   	} 
   	
   	
   	/**
   	 * Get a list of transferObject and apply the resource bundle to the field tag
   	 */
   	protected Vector<TransferObject> applyBundle(HttpServletRequest request, Vector<TransferObject> transferObjectList){
   		try {
	    	Iterator<TransferObject> s = transferObjectList.iterator();
	    	while(s.hasNext()) {
	    	    TransferObject to = (TransferObject)s.next();
	    	    to.setGenericTag(this.getBundleMessage(request, to.getGenericTag(), true));
	    	}
		}catch(Exception ee) {
			ee.printStackTrace();
		}    	
   	    return transferObjectList;
   	}
   	
   	
   	/**
   	 * Get the values from form based to the meta fields of planning. 
   	 * @throws BusinessException 
   	 */
   	protected void setMetaFieldValuesFromForm(Vector<MetaFieldTO> metaFieldList, HttpServletRequest request, PlanningTO container) throws MetaFieldNumericTypeException{
		if (metaFieldList!=null) {
			container.setAdditionalFields(null);
		    Iterator<MetaFieldTO> i = metaFieldList.iterator();
		    while(i.hasNext()) {
		    	MetaFieldTO mto = i.next();
		    	
		    	String value = request.getParameter(mto.getHtmlName());
		    	if (value!=null) {
		    		//replace the html caracter 'nbsp' to empty string
		    		value = value.replaceAll("\u00a0", ""); 	
		    	}
		    	
		    	if (mto.getType().intValue()==MetaFieldTO.TYPE_TABLE) {
		    		
		    		String numCols = request.getParameter(mto.getHtmlName() + "_NUM_COLS");
		    		String numRows = request.getParameter(mto.getHtmlName() + "_NUM_ROWS");
		    		if (numCols!=null && numRows!=null) {
    					Vector<AdditionalTableTO> values = new Vector<AdditionalTableTO>();
    					
		    			for (int r=1; r<=Integer.parseInt(numRows); r++) {
		    				for (int c=1; c<=Integer.parseInt(numCols); c++) {
						    	String cellValue = request.getParameter("META_DATA_" + mto.getHtmlName() + "_" + r + "_" + c);
						    	String cellType = request.getParameter(mto.getHtmlName() + "_" + r + "_" + c + "_CEL_TYPE");
						    	if (cellValue!=null && cellType!=null) {
								    AdditionalTableTO atto = new AdditionalTableTO();
								    atto.setId(mto.getId());
								    atto.setCol(new Integer(c));
								    atto.setLine(new Integer(r));
								    atto.setValue(cellValue);
								    if (cellType.equals(MetaFieldTO.TYPE_CALENDAR+"")) {
					        			Locale loc = SessionUtil.getCurrentLocale(request);
					            		Timestamp cellDate = DateUtil.getDateTime(cellValue, getCalendarMask(request), loc);
								    	atto.setDateValue(cellDate);
								    } else {
								    	atto.setDateValue(null);	
								    }
								    values.addElement(atto);	    									    		
						    	}						    	
		    				}		    				
		    			}
				    	container.addAdditionalTable(mto, mto.getId(), values);		    			
		    		}
		    				    		
		    	} else if (mto.getType().intValue()==MetaFieldTO.TYPE_CALENDAR) {
		    		Timestamp iDate = null;
	        		try {
	        			Locale loc = SessionUtil.getCurrentLocale(request);
	            		iDate = DateUtil.getDateTime(value, getCalendarMask(request), loc);
	            		if (iDate!=null) {
		            		String[] options = mto.getDomain().split("\\|");	            		
		            		value = DateUtil.getDate(iDate, options[3], loc);
	            		} else {
	            			value = "";
	            		}
	        		}catch(Exception e) {
	        			value = "";
	        		}
	        		container.addAdditionalField(mto, value, iDate, null);
		    		
		    	}else if (mto.getType().intValue()==MetaFieldTO.TYPE_TEXT_BOX_NUMERIC) {
		    		try{
		    			String[] options = mto.getDomain().split("\\|");	
		    			Locale loc = SessionUtil.getCurrentLocale(request);
		    			DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(loc);
		    			df.applyPattern(options[3]);
		    			Float numericValue = (value != null && ! value.equals("") ? df.parse(value).floatValue() : null);
		    			value = (value != null && !value.equals("") ? df.format(numericValue) : "" );
		    			container.addAdditionalField(mto, value, numericValue);
		    		}catch (Exception e) {
		    			throw new MetaFieldNumericTypeException(mto.getName());
					}
		    	}else {
			    	container.addAdditionalField(mto, (value==null?"":value));		    		
		    	}
		    }
		}
   	}

   	
	public ActionForward metaTableRemoveRow(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String fwd = (String)request.getSession().getAttribute("META_FIELD_FORWARD");
		Vector<AdditionalTableTO> newList = new Vector<AdditionalTableTO>();
	    try {
	        GeneralStrutsForm frm = (GeneralStrutsForm)form;
	        String metaFieldId = frm.getGenericTag();
	        String[] tokens = metaFieldId.split("\\|");
	        if (tokens!=null && tokens.length==2) {
	        	Integer row = Integer.parseInt(tokens[1]);
		        metaFieldId = tokens[0].replaceAll("META_DATA_", "");
		        AdditionalFieldTO afto = frm.getAdditionalField(metaFieldId);
		        if (afto!=null && afto.getTableValues()!=null) {
		        	Vector<AdditionalTableTO> list = afto.getTableValues();
		        	Iterator<AdditionalTableTO> i = list.iterator();
		        	while(i.hasNext()) {
		        		AdditionalTableTO atto = i.next();
		        		if (!atto.getLine().equals(row)) {
		        			if (atto.getLine()>row) {
		        				atto.setLine(atto.getLine()-1);
		        			}
		        			newList.add(atto);
		        		}
		        	}
		        	afto.setTableValues(newList);	        	
		        }
	        }

	        RequestDispatcher dispatcher = request.getRequestDispatcher("../do/" + fwd);   
			dispatcher.forward(request, response);

	    } catch(Exception e){
	        this.setErrorFormSession(request, "error.generic.showFormError", e);
	    }
		return null;
	}
   	
	public ActionForward metaTableAddRow(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		
		Vector<AdditionalTableTO> newRow = new Vector<AdditionalTableTO>();
		String fwd = (String)request.getSession().getAttribute("META_FIELD_FORWARD");
	    try {
	        GeneralStrutsForm frm = (GeneralStrutsForm)form;
	        String metaFieldId = frm.getGenericTag();
	        metaFieldId = metaFieldId.replaceAll("META_DATA_", "");
	        AdditionalFieldTO afto = null;

        	//try to get fields from form...
			@SuppressWarnings({ "unchecked", "rawtypes" })
			Vector<MetaFieldTO> metaFieldList = (Vector)request.getSession().getAttribute("metaFieldList");
			if (metaFieldList!=null) {
				PlanningTO dummyContainer = new PlanningTO();
				this.setMetaFieldValuesFromForm(metaFieldList, request, dummyContainer);	    
				frm.setAdditionalFields(dummyContainer.getAdditionalFields());			
				afto = frm.getAdditionalField(metaFieldId);
			}	        
	        
	        if (afto!=null && afto.getTableValues()!=null) {
	        	Vector<AdditionalTableTO> list = afto.getTableValues();
	        	
	        	//get the total number of rows...
	        	int totalRows = 0;
	        	Iterator<AdditionalTableTO> i = list.iterator();
	        	while(i.hasNext()) {
	        		AdditionalTableTO atto = i.next();
	        		if (totalRows<atto.getLine().intValue()) {
	        			totalRows = atto.getLine().intValue();
	        		}
	        	}
	        	
	        	//increment a new row based to the first row
	        	Iterator<AdditionalTableTO> j = list.iterator();
	        	while(j.hasNext()) {
	        		AdditionalTableTO atto = j.next();
        			if (atto.getLine().intValue()==1) {
        				newRow.add(new AdditionalTableTO(atto, totalRows+1));
        			}
	        	}
	        	
	        	list.addAll(newRow);
	        	afto.setTableValues(list);
	        }

				        
			RequestDispatcher dispatcher = request.getRequestDispatcher("../do/" + fwd);   
			dispatcher.forward(request, response);

	    } catch(MetaFieldNumericTypeException e){
	    	this.setErrorFormSession(request, e.getMessage(), e.getMetaFieldName(), null, null, null, null, e);
		} catch(Exception e){
	        this.setErrorFormSession(request, "error.generic.showFormError", e);
	    }
		return null;
	}
   	
   	
	public ActionForward clickArtifactCategory(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		try {
			NewArtifactAction naAction = new NewArtifactAction();
			GeneralStrutsForm frm = (GeneralStrutsForm)form;
	        response.setContentType("text/xml");  
	        response.setHeader("Cache-Control", "no-cache");  
	        
	        PrintWriter out = response.getWriter();
	        StringBuffer sb = naAction.getArtifactBody(frm, request);
	        out.println(sb.toString());
	        out.flush();
	        
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.showAllReqForm", e);
		}
		return null;
	}
   	
}