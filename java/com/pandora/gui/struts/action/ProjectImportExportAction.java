package com.pandora.gui.struts.action;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Vector;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.FieldValueTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.SystemSingleton;
import com.pandora.delegate.ImportExportDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.ProjectImportExportForm;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.LogUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.imp.ImportBUS;
import com.pandora.imp.ImportExportBUS;

/**
 * This class handles all the requests from JSPs that
 * concern Export process.
 */
public class ProjectImportExportAction extends GeneralStrutsAction {

	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
		String forward = "showForm";
		
		try {
		    ProjectImportExportForm gfrm = (ProjectImportExportForm)form;
		    
		    ProjectDelegate pdel = new ProjectDelegate();
		    ProjectTO pto = pdel.getProjectObject(new ProjectTO(gfrm.getProjectId()), true);
		    gfrm.setProjectName(pto.getName());
		    
		    this.refresh(mapping, form, request, response );
		    		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}

		return mapping.findForward(forward);		
	}
	
	
	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		return mapping.findForward("showForm");
	}
	
	
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showForm";
		
		try {		
			Vector<TransferObject> optionList = new Vector<TransferObject>();
			
	        String[] list = this.getClassList();	        
	        if (list!=null && list.length>0) {
	            for (int i = 0; i<list.length; i++) {
	                String klass = list[i].trim();
	                ImportExportBUS bus = ImportExportBUS.getClass(klass);
                    if (bus!=null) {
            			String value1 = this.getBundleMessage(request, bus.getLabel(), true);
            			optionList.addElement(new TransferObject(bus.getClass().getName(), value1));
                    }
	            }
	        }	        
	        this.select(mapping, form, request, response); //load fields to the default value
	        
			request.getSession().setAttribute("optionList", optionList);
			
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		
		return mapping.findForward(forward);		
	}

	    
	public ActionForward generate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		ActionForward forward = mapping.findForward("showForm");
	    StringBuffer buffer = new StringBuffer();
		String fileName = "";
		String contentType = "";
		String encoding = SystemSingleton.getInstance().getDefaultEncoding();
		
		try {
			ProjectDelegate pdel = new ProjectDelegate();		    
			ImportExportDelegate impExpDeleg = new ImportExportDelegate();
		    ProjectImportExportForm gfrm = (ProjectImportExportForm)form;
		    
		    //update the fields selected value (if necessary)
		    Vector<FieldValueTO> fillinFields = this.getFieldsWithValues(gfrm, request);
		    if (fillinFields!=null) {
			    ProjectTO pto = pdel.getProjectObject(new ProjectTO(gfrm.getProjectId()), false);
			    String option = gfrm.getImportExportOption();
			    ImportExportBUS klass = ImportExportBUS.getClass(option);

			    UserTO uto = SessionUtil.getCurrentUser(request);
			    klass.setHandler(uto);
			    
			    if (klass.getType().equals(ImportBUS.TYPE_IMPORT)) {
			    	
			    	if (gfrm.getImportExternalFile()!=null) {
			    		boolean validationPass = false;
			    		boolean processPass = false;
			    		try {
			    			impExpDeleg.validateImportFile(klass, gfrm.getImportExternalFile().getInputStream(), pto, fillinFields);
			    			validationPass = true;
			    		} catch(Exception e) {
			    			LogUtil.log(this, LogUtil.LOG_ERROR, "A error occurs on file import process", e);
			    			this.setErrorFormSession(request, "validate.importExport.invalidFile", e);	
			    		}
			    		
			    		if (validationPass) {
				    		try {
				    			impExpDeleg.importFile(klass, gfrm.getImportExternalFile().getInputStream(), pto, fillinFields);
				    			processPass = true;
				    		} catch(Exception e) {
				    			LogUtil.log(this, LogUtil.LOG_ERROR, "A error occurs on file import process", e);
				    			this.setErrorFormSession(request, "validate.importExport.failed", e);	
				    		}
				    		
				    		if (processPass) {
				    			this.setSuccessFormSession(request, "validate.importExport.imp.success");	
				    		}
			    		}

				    	
			    	} else {
			    		this.setErrorFormSession(request, "validate.importExport.invalidFile", null);
			    	}
			    	
			    } else {
				    fileName = impExpDeleg.getExportFileName(klass, pto);  
				    contentType = impExpDeleg.getExportContentType(klass);
				    encoding = impExpDeleg.getEncoding(klass);
				    
				    //perform export procedures...
				    try {
					    buffer.append(impExpDeleg.getExportHeader(klass, pto, fillinFields));
					    buffer.append(impExpDeleg.getExportBody(klass, pto, SessionUtil.getCurrentUser(request), fillinFields));
					    buffer.append(impExpDeleg.getExportFooter(klass, pto, fillinFields));
					    forward = null;
				    } catch(Exception e) {
				    	LogUtil.log(this, LogUtil.LOG_ERROR, "A error occurs on file export process", e);
				    	this.setErrorFormSession(request, "validate.importExport.exp.failed", e);
				    	buffer = null;
				    }
			    }
		    }
		    
		    
		}catch(Exception e){
			LogUtil.log(this, LogUtil.LOG_ERROR, "A error occurs on file import/export process", e);
		
		} finally{
			//put response content into Standard output
			if (buffer!=null && !buffer.toString().equals("")) {
				ServletOutputStream sos;
				try {		    
					sos = response.getOutputStream();
					response.setContentType(contentType);
					response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
					response.setContentLength(buffer.length() + 1000);
					Writer wout = new OutputStreamWriter(sos, encoding);
					wout.write(buffer.toString());
					wout.flush();
					wout.close();
					sos.close();				
				} catch (IOException e2) {
					LogUtil.log(this, LogUtil.LOG_ERROR, "A error occurs on file export process", e2);
				}				
			}
		}
				
		return forward;
	}
	
	
	private Vector<FieldValueTO> getFieldsWithValues(ProjectImportExportForm gfrm, HttpServletRequest request) throws Exception{
		Vector<FieldValueTO> fieldList = new Vector<FieldValueTO>();
		
		String option = gfrm.getImportExportOption();
        String[] list = this.getClassList();	        
        if (list!=null && list.length>0) {
            for (int i = 0; i<list.length; i++) {
                String klass = list[i].trim();
                ImportExportBUS bus = ImportExportBUS.getClass(klass);
                if (bus!=null && option.equals(klass)) {
                	bus.setHandler(SessionUtil.getCurrentUser(request));
                    if (bus.getFields()!=null) {
                    	Vector<FieldValueTO> fields = bus.getFields();
                    	for (int j=0; j<fields.size(); j++) {
                    		FieldValueTO field = (FieldValueTO)bus.getFields().elementAt(j);
                    		String value = request.getParameter(field.getId());
                    		if (value!=null && !value.trim().equals("")) {
    	                		field.setCurrentValue(value);
    	                		fieldList.add(field);	                			
                    		} else {
                    		    this.setErrorFormSession(request, "validate.importExport.blankField", null);
                    		    fieldList = null;
                    		    break;
                    		}
                    	}
                    	break;
                    }                	
                }
            }
        }
		
		return fieldList;
	}
	
	
	public ActionForward select(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		String forward = "showForm";
		try {		
			StringBuffer html = new StringBuffer();
			ProjectImportExportForm gfrm = (ProjectImportExportForm)form;
				        
	        //set a default value of option (case option is null or empty)
	        String option = gfrm.getImportExportOption();
	        if (option==null || option.equals("")){
		        String[] list = this.getClassList();
		        if (list.length > 0) {
		        	option = list[0].trim();
		        }	        	
	        }
	        
	        String[] list = this.getClassList();	        
	        if (list!=null && list.length>0) {
	            for (int i = 0; i<list.length; i++) {
	                String klass = list[i].trim();
	                ImportExportBUS bus = ImportExportBUS.getClass(klass);
	                if (bus!=null && option.equals(klass)) {
	                	
	                	bus.setHandler(SessionUtil.getCurrentUser(request));
	                	
	                	if (bus.getType().equals(ImportBUS.TYPE_IMPORT)) {
        			        html.append("<tr class=\"pagingFormBody\"><td width=\"10\">&nbsp;</td>");
        			        html.append("<td width=\"150\" class=\"formTitle\">" + super.getBundleMessage(request, "label.importExport.browse") + ":&nbsp;</td>");
        			        html.append("<td class=\"formBody\">" + HtmlUtil.getTextBox("importExternalFile", "", false, null, 255, 80, "file" ));
        			        html.append("</td><td width=\"10\">&nbsp;</td></tr>\n");	                		
	                	}
	                	
	                	if (bus.getFields()!=null && bus.getFields().size()>0) {
	            	        String[] labelsForCalendar = new String[2];
	            	        labelsForCalendar[0] = this.getBundleMessage(request, "label.calendar.button");
	            	        labelsForCalendar[1] = this.getBundleMessage(request, "calendar.format");

	            	        String[] labelsForBoolean = new String[2];
	            	        labelsForBoolean[0] = this.getBundleMessage(request, "label.yes");
	            	        labelsForBoolean[1] = this.getBundleMessage(request, "label.no");
	        		    	
	        		        for (int j = 0; j < bus.getFields().size(); j++) {
	        			        FieldValueTO field = (FieldValueTO)bus.getFields().elementAt(j);
	        			        html.append("<tr class=\"pagingFormBody\"><td width=\"10\">&nbsp;</td>");
	        			        html.append("<td width=\"150\" class=\"formTitle\">" + super.getBundleMessage(request, field.getLabel(), true) + ":&nbsp;</td>");
	        			        html.append("<td class=\"formBody\">" + HtmlUtil.getHtmlField(field, "", "projectImportExportForm", labelsForBoolean, labelsForCalendar, null));
	        			        html.append("</td><td width=\"10\">&nbsp;</td></tr>\n");
	        		        }	                		
	                	}
	                	
        		        break;
	                }
	            }
	        }
		    
	        gfrm.setFieldsHtml(html.toString());		    
			
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return mapping.findForward(forward);		
	}
	
	
	private String[] getClassList() throws Exception {
		String[] list = null;

		//get the root user from data base
        UserDelegate udel = new UserDelegate();
        UserTO root = udel.getRoot();
        PreferenceTO pref = root.getPreference();
        String classesList = pref.getPreference(PreferenceTO.IMP_EXP_BUS_CLASS);
	    if (classesList!=null) {
	        list = classesList.split(";");
	    }        			
		return list;
	}

    
}
