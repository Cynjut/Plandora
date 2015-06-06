
package com.pandora.gui.struts.action;

import java.util.Iterator;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.FieldValueTO;
import com.pandora.LeaderTO;
import com.pandora.MetaFieldTO;
import com.pandora.OccurrenceFieldTO;
import com.pandora.OccurrenceTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.ReportTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.bus.occurrence.Occurrence;
import com.pandora.bus.occurrence.StrategicObjectivesOccurrence;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.MetaFieldDelegate;
import com.pandora.delegate.OccurrenceDelegate;
import com.pandora.delegate.PreferenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.ReportDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.MetaFieldNumericTypeException;
import com.pandora.gui.struts.form.GeneralStrutsForm;
import com.pandora.gui.struts.form.OccurrenceForm;
import com.pandora.gui.struts.form.ResTaskForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.SessionUtil;

/**
 */
public class OccurrenceAction extends GeneralStrutsAction {
    
    
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		ProjectDelegate pdel = new ProjectDelegate();
		String forward = "showOccurrence";
		try {
			this.clearForm(form, request);
			
			UserTO uto = SessionUtil.getCurrentUser(request);
			OccurrenceForm ofrm = (OccurrenceForm)form;
			ofrm.setSaveMethod(ResTaskForm.INSERT_METHOD, uto);

			ProjectTO pto = pdel.getProjectObject(new ProjectTO(ofrm.getProjectId()), true);
			ofrm.setProject(pto);
			ofrm.setProjectName(pto.getName());

		    String hideClosedOcc = uto.getPreference().getPreference(PreferenceTO.OCC_HIDE_CLOSED);
		    ofrm.setHideClosedOccurrences(hideClosedOcc!=null && hideClosedOcc.equals("on"));
			
		    this.refresh(ofrm, request);
		    this.refreshAuxiliarList(mapping, form, request, response);
		    
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.formRisk.showForm", e);
		}	    
    
		return mapping.findForward(forward);		
	}
	
	
	public ActionForward navigate(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		return mapping.findForward("showOccurrence");
	}

	
	public ActionForward saveKpiLink(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showOccurrence";
		try {	    		    
		    OccurrenceForm ofrm = (OccurrenceForm)form;
		    OccurrenceDelegate odel = new OccurrenceDelegate();
		    Locale loc = SessionUtil.getCurrentLocale(request);
		    odel.saveKpiLink(ofrm.getSelectedKpi(), ofrm.getKpiWeight(), ofrm.getId(), loc);
		    
		    this.refreshKpiPainel(request, ofrm);
		
		} catch(BusinessException e){
			this.setErrorFormSession(request, "error.formOccurrence.showForm", e);
		}	    
		return mapping.findForward(forward);	    
	}

	
	public ActionForward removeKpiLink(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
	    String forward = "showOccurrence";
		try {	    		    
		    OccurrenceForm ofrm = (OccurrenceForm)form;
		    OccurrenceDelegate odel = new OccurrenceDelegate();
		    odel.removeKpiLink(ofrm.getSelectedKpi(), ofrm.getId());
		    this.refreshKpiPainel(request, ofrm);
		} catch(BusinessException e){
		   this.setErrorFormSession(request, "error.formOccurrence.showForm", e);
		}	    
		return mapping.findForward(forward);	    
	}
	
	
	public ActionForward refreshSource(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showOccurrence";
	    this.refreshOccurrenceSourceFields(request, form, null);	    
		return mapping.findForward(forward);	    
	}


	public ActionForward saveOccurrence(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showOccurrence";
		String errorMsg = "error.formOccurrence.showForm";
		String succeMsg = "message.success";
		
		try {
		    OccurrenceForm ofrm = (OccurrenceForm)form;
		    OccurrenceDelegate odel = new OccurrenceDelegate();
		    OccurrenceTO oto = this.getTransferObjectFromActionForm(ofrm, request);
			
		    Occurrence obus = getOccurrenceClass(ofrm.getSource());
		    if (obus!=null) {
				if (ofrm.getSaveMethod().equals(GeneralStrutsForm.INSERT_METHOD)){
				    errorMsg = "error.formOccurrence.insert";
				    succeMsg = "message.insertOccurrence";
				    odel.insertOccurrence(oto);
				    this.clearForm(form, request);
				} else {
				    errorMsg = "error.formOccurrence.update";
				    succeMsg = "message.updateOccurrence";
				    odel.updateOccurrence(oto);
				}
				
				//set success message into http session
				this.clear(mapping, form, request, response );				
				this.setSuccessFormSession(request, succeMsg);				

			    //set the current user connected
				UserTO uto = SessionUtil.getCurrentUser(request);
			    ofrm.setSaveMethod(OccurrenceForm.INSERT_METHOD, uto);
		        
		    } else {
		        this.setErrorFormSession(request, "error.formOccurrence.source.invalid", null);
		    }
			
		} catch(MetaFieldNumericTypeException e){
			this.setErrorFormSession(request, e.getMessage(), e.getMetaFieldName(), null, null, null, null, e);
		} catch(BusinessException e){
		    this.setErrorFormSession(request, errorMsg, e);
		} catch(NullPointerException e){
		    this.setErrorFormSession(request, errorMsg, e);		    
		}	    
		return mapping.findForward(forward);	    
	}

	
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showOccurrence";
	    PreferenceDelegate pdel = new PreferenceDelegate();
		try {
			
			OccurrenceForm ofrm = (OccurrenceForm)form;
			this.refresh(ofrm, request);
			
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    PreferenceTO pto = new PreferenceTO(PreferenceTO.OCC_HIDE_CLOSED, ofrm.getHideClosedOccurrences()?"on":"off", uto);

		    //save the new preference
			uto.getPreference().addPreferences(pto);
			pto.addPreferences(pto);
            pdel.insertOrUpdate(pto);	            
	        
		} catch(BusinessException e){
		   this.setErrorFormSession(request, "error.formOccurrence.showForm", e);
		}
	    
		return mapping.findForward(forward);	    
	}

	
	private void refresh(OccurrenceForm ofrm, HttpServletRequest request){
		try {	    		    
		    OccurrenceDelegate odel = new OccurrenceDelegate();
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    ProjectTO pto = new ProjectTO(ofrm.getProjectId());
		    
		    Vector<OccurrenceTO> oList = odel.getOccurenceList(ofrm.getProjectId(), uto.getId(), ofrm.getHideClosedOccurrences());
		    request.getSession().setAttribute("occurrenceList", oList);
		    
		    UserDelegate udel = new UserDelegate();
		    Vector<LeaderTO> projectLeaders = udel.getLeaderByProject("'" + pto.getId() + "'");
		    Vector<TransferObject> visibilityList = getVisibilityList(projectLeaders, uto, request);
	        request.getSession().setAttribute("visibilityList", visibilityList);
		    	     
	        request.getSession().setAttribute("metaFieldList", new Vector<MetaFieldTO>());
		    
		} catch(BusinessException e){
		   this.setErrorFormSession(request, "error.formOccurrence.showForm", e);
		}
	}

	private Vector<TransferObject> getVisibilityList(Vector<LeaderTO> projectLeaders, UserTO uto, HttpServletRequest request){
		Vector<TransferObject> visibilityList = new Vector<TransferObject>();
		visibilityList.add(new TransferObject("1", this.getBundleMessage(request, "label.public")));
		
		Iterator<LeaderTO> itleader = projectLeaders.iterator();
		while(itleader.hasNext()){
			if(uto.getId().equals(itleader.next().getId())){
				visibilityList.add(new TransferObject("0", this.getBundleMessage(request, "label.private")));
				break;
			}
		}
		return visibilityList;
	}
	
    private void refreshOccurrenceSourceFields(HttpServletRequest request, ActionForm form, OccurrenceTO oto) {
		MetaFieldDelegate mfdel = new MetaFieldDelegate();
		CategoryDelegate cdel = new CategoryDelegate();
   	
		try {
		    StringBuffer html = new StringBuffer();
		    String fieldIdList = "";
		    OccurrenceForm ofrm = (OccurrenceForm)form;
	    	String gap = "", helpContent = "";
	    	
		    Occurrence obus = getOccurrenceClass(ofrm.getSource());  
		    if (obus!=null && obus.getFields()!=null) {
		    	
		    	Locale loc = SessionUtil.getCurrentLocale(request);
		    	String mask = super.getCalendarMask(request);
		    	
		        Iterator<FieldValueTO> i = obus.getFields().iterator();
		        while(i.hasNext()) {
		            FieldValueTO field = i.next();		            
		            String fieldLabel = this.getBundleMessage(request, field.getLabel(), true);

		            if (field.getDomain()!=null) {
		                field.setDomain(super.applyBundle(request, field.getDomain()));
		            }
		            
		            if (field.getGridFields()!=null) {
		            	for (FieldValueTO subField : field.getGridFields()) {
				            if (subField.getDomain()!=null) {
				            	subField.setDomain(super.applyBundle(request, subField.getDomain()));
				            }
						}
		            }

		            String fieldValue = "";
		            Vector<Vector<Object>> fieldTableValues = null;
		            if (oto!=null && oto.getFields()!=null) {
		            	if (field.getType().equals(FieldValueTO.FIELD_TYPE_GRID)) {
		            		fieldTableValues = oto.getFieldTableValueByKey(field.getId(), obus);
		            	} else {
		            		fieldValue = oto.getFieldValueByKey(field.getId(), obus, mask, loc);	
		            	}
		            }			        
			        html.append("<tr class=\"pagingFormBody\"><td width=\"10\">&nbsp;</td>");
			        html.append("<td width=\"150\" class=\"formTitle\">" + fieldLabel + ":&nbsp;</td>");
			        html.append("<td class=\"formBody\">" + this.getHtmlField(field, fieldValue, fieldTableValues, request));
			        html.append("</td><td width=\"10\">&nbsp;</td></tr>\n");
			        
			        if (fieldIdList.trim().length()>0) {
			            fieldIdList = fieldIdList + "|";
			        }
			        fieldIdList = fieldIdList + field.getId();			        
		        }
		    	if (html.length()>0) {
		    	    helpContent = "<tr class=\"formNotes\"><td>&nbsp;</td><td>&nbsp;</td><td colspan=\"2\">" + 
		    	    		this.getBundleMessage(request, obus.getContextHelp(), true) + "</td></tr>";
		    	    gap = "<tr class=\"gapFormBody\"><td colspan=\"4\">&nbsp;</td></tr>";
		    	}
		    	
		    	//set the values to populate the status combo
		    	if (obus.getStatusValues()!=null) {
		    	    Vector<TransferObject> values = super.applyBundle(request, obus.getStatusValues());
			    	ofrm.setStatusComboHtml(HtmlUtil.getComboBox("status", values, "textBox", ofrm.getStatus()));
		    	}
		    	
				request.getSession().setAttribute("metaFieldList", new Vector<MetaFieldTO>());		    
				CategoryTO cto = cdel.getCategoryByName(ofrm.getSource(), CategoryTO.TYPE_OCCURRENCE, ofrm.getProject());
				if (cto!=null && ofrm.getId()!=null) {
					Vector<MetaFieldTO> list = mfdel.getListByProjectAndContainer(ofrm.getId(), cto.getId(), MetaFieldTO.APPLY_TO_OCCURRENCE);
					request.getSession().setAttribute("metaFieldList", list);		    					
				}
				
		    } else {
		        this.setErrorFormSession(request, "error.formOccurrence.source.invalid", null); 
		    }
		    
		    ofrm.setFieldsHtml(gap + html.toString() + helpContent + gap);
		    ofrm.setFieldIdList(fieldIdList);
		    
		} catch(Exception e){
		   this.setErrorFormSession(request, "error.formOccurrence.showForm", e);
		}		    
    }
    
    
    private String getHtmlField(FieldValueTO field, String fieldValue, Vector<Vector<Object>> fieldTableValues, HttpServletRequest request){
    	
    	String[] labels = null;
    	if (field.getGridFields()!=null) {
    		labels = new String[field.getGridFields().size()];
    		for(int i=0; i<field.getGridFields().size(); i++){
    			FieldValueTO gridColumn = field.getGridFields().get(i);
    			labels[i] = this.getBundleMessage(request, gridColumn.getLabel(), true);		
			}
    	}
        
        String[] labelsForCalendar = new String[2];
        labelsForCalendar[0] = this.getBundleMessage(request, "label.calendar.button");
        labelsForCalendar[1] = this.getBundleMessage(request, "calendar.format");
        
        return HtmlUtil.getHtmlField(field, fieldValue, fieldTableValues, "occurrenceForm", labels, labelsForCalendar, null);
    }
    

	public void refreshAuxiliarList(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){

	    try {
            request.getSession().setAttribute("sourceList", this.getSourceList(request, form));
        } catch (BusinessException e) {
            request.getSession().setAttribute("sourceList", new Vector<TransferObject>());
        }
	    this.refreshSource(mapping, form, request, response);
	}

	
    public ActionForward editOccurrence(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showOccurrence";
	    OccurrenceDelegate odel = new OccurrenceDelegate();
		try {
		    OccurrenceForm frm = (OccurrenceForm)form;
		    OccurrenceTO oto = odel.getOccurrenceObject(new OccurrenceTO(frm.getId()));
		    
		    this.getActionFormFromTransferObject(oto, frm, request);
		    
			//set current operation status for Updating	
		    frm.setSaveMethod(OccurrenceForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
		    
	        this.refreshKpiPainel(request, frm);
		    
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formOccurrence.showForm", e);
		}
		
		return mapping.findForward(forward);				
	}

	private void refreshKpiPainel(HttpServletRequest request, OccurrenceForm ofrm) throws BusinessException {
		if (ofrm.getSource()!=null && ofrm.getSource().equals(StrategicObjectivesOccurrence.class.getName())) {
			ReportDelegate rdel = new ReportDelegate();
			Vector<ReportTO> kpiList = rdel.getKpiByOccurrence(ofrm.getId());
			ofrm.setKpiListHtml(this.getIndicatorList(kpiList, request, ofrm.getId()));
			
			Vector<ReportTO> allKpiList = rdel.getListBySource(true, null, ofrm.getProject(), true);

			Vector<ReportTO> closedKpiList = new Vector<ReportTO>();
			Vector<ReportTO> openKpiList = new Vector<ReportTO>();
			ReportTO defaultOpt = new ReportTO("-1");
			defaultOpt.setName(this.getBundleMessage(request, "label.combo.select"));
			openKpiList.add(0, defaultOpt);
			
			Iterator<ReportTO> i = allKpiList.iterator();
			while(i.hasNext()) {
				ReportTO rto = (ReportTO)i.next();
				if (rto.getFinalDate()==null) {
					openKpiList.add(rto);
				} else {
					closedKpiList.add(rto);
				}
			}
			
			request.getSession().setAttribute("openIndicatorsList", openKpiList);
			request.getSession().setAttribute("closeIndicatorsList", closedKpiList);
			
			Vector<TransferObject> kpiWeigthList = new Vector<TransferObject>();
			kpiWeigthList.addElement(new TransferObject("0", "0"));
			kpiWeigthList.addElement(new TransferObject("1", "1"));
			kpiWeigthList.addElement(new TransferObject("2", "2"));
			kpiWeigthList.addElement(new TransferObject("3", "3"));
			kpiWeigthList.addElement(new TransferObject("4", "4"));
			kpiWeigthList.addElement(new TransferObject("5", "5"));
			request.getSession().setAttribute("kpiWeightList", kpiWeigthList);
			
		} else {
			ofrm.setKpiListHtml("");
			request.getSession().removeAttribute("allIndicatorsList");
		}
	}


	public ActionForward removeOccurrence(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
	    String forward = "showOccurrence";
		try {
		    OccurrenceForm ofrm = (OccurrenceForm)form;
		    OccurrenceDelegate odel = new OccurrenceDelegate();
						
     		OccurrenceTO oto = new OccurrenceTO();
			oto.setId(ofrm.getId());
			odel.removeOccurrence(oto);
			
			//clear form and messages
			this.clearForm(ofrm, request);
			
			//set success message into http session
			this.setSuccessFormSession(request, "message.removeOccurrence");
			this.prepareForm(mapping, form, request, response);
		
		} catch(BusinessException e){
		    this.setErrorFormSession(request, "error.formOccurrence.remove", e);
		}
	    
		return mapping.findForward(forward);	    
	}

	
	/**
	 * Clear all values of current form.
	 */
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showOccurrence";		
		this.prepareForm(mapping, form, request, response);
		return mapping.findForward(forward);		
	}
	
	
	private void clearForm(ActionForm form, HttpServletRequest request){
	    OccurrenceForm ofrm = (OccurrenceForm)form;		
	    ofrm.clear();
	    this.clearMessages(request);
	}
	
	
	private Vector<TransferObject> getSourceList(HttpServletRequest request, ActionForm form) throws BusinessException{
	    Vector<TransferObject> response = new Vector<TransferObject>();	    
	    UserDelegate udel = new UserDelegate();
	    UserTO uto = udel.getRoot();
	    String occClasses = uto.getPreference().getPreference(PreferenceTO.OCCURRENCE_BUS_CLASS);
	    OccurrenceForm ofrm = (OccurrenceForm)form;
	    
	    if (occClasses!=null) {
	        String[] classList = occClasses.split(";");
	        if (classList!=null && classList.length>0) {
	            for (int i = 0; i<classList.length; i++) {
	                String classStr = classList[i].trim();
	                Occurrence obus = getOccurrenceClass(classStr);
                    if (obus!=null) {
                    	String label = this.getBundleMessage(request, obus.getUniqueName(), true);
                        if (response.isEmpty()) {
                            ofrm.setSource(classStr);
                            ofrm.setSourceName(label);
                        }
                        response.add(new TransferObject(classStr, label));
                    } else {
                        response.add(new TransferObject("-1", classStr + "-ERR!"));
                    }
	            }
	        }
	    }
	    return response;
	}
	
	
	private Occurrence getOccurrenceClass(String className){
	    Occurrence response = null;
        try {
            Class<?> klass = Class.forName(className);
            response = (Occurrence)klass.newInstance();
        } catch (Exception e) {
            response = null;
        }
        return response;
	}
	
	
	/**
	 * Put data of TransferObject (from DB) into html fields (ActionForm)
	 */
	private void getActionFormFromTransferObject(OccurrenceTO to, OccurrenceForm frm, HttpServletRequest request){
	    frm.setId(to.getId());
	    frm.setSource(to.getSource());
	    
	    String sourceName = "";
	    if (to.getSource()!=null && !to.getSource().trim().equals("")) {
	    	Occurrence obus = getOccurrenceClass(to.getSource());
	    	if (obus!=null) {
	    		sourceName = this.getBundleMessage(request, obus.getUniqueName(), true);	
	    	}
	    }
	    frm.setSourceName(sourceName);
	    
	    frm.setName(to.getName());
	    frm.setProject(to.getProject());
	    frm.setProjectId(to.getProject().getId());
	    frm.setStatus(to.getStatus());
	    frm.setVisible(to.getVisible()?"1":"0");
	    frm.setCreationDate(to.getCreationDate());
	    if (to.getProject()!=null) {
	    	frm.setProjectName(to.getProject().getName());	
	    } else {
	    	frm.setProjectName("");
	    }
	    frm.setAdditionalFields(to.getAdditionalFields());	
	    
	    //set the relationship of occurrences
	    request.getSession().setAttribute("occRelationshipList", to.getRelationList());
	    
	    this.refreshOccurrenceSourceFields(request, frm, to);
	}
	
	
	@SuppressWarnings("rawtypes")
	private OccurrenceTO getTransferObjectFromActionForm(OccurrenceForm frm, HttpServletRequest request) throws MetaFieldNumericTypeException{
	    OccurrenceTO oto = new OccurrenceTO();
	    oto.setId(frm.getId());
	    oto.setSource(frm.getSource());
	    oto.setName(frm.getName());
	    oto.setProject(frm.getProject());
	    oto.setStatus(frm.getStatus());
	    oto.setHandler(SessionUtil.getCurrentUser(request));   
	    oto.setVisible(frm.getVisible().equals("1"));
	    oto.setDescription(frm.getName());
	    oto.setCreationDate(frm.getCreationDate());
	    oto.setLocale(SessionUtil.getCurrentLocale(request));
	    
	    Occurrence obus = getOccurrenceClass(frm.getSource());
	    if (obus!=null) {
	        String label = obus.getStatusLabelByKey(frm.getStatus());
	        label = this.getBundleMessage(request, label, true);
	        oto.setStatusLabel(label);	        
	    }
	    
	    //get the dynamic fields and set into the 'occurrence' entity
	    String fieldStr = frm.getFieldIdList();
	    if (fieldStr!=null) {
		    String[]fields = fieldStr.split("\\|");
		    if (fields!=null && fields.length>0) {
			    for(int i = 0; i< fields.length; i++) {
			        String fieldValue = request.getParameter(fields[i]);
			        if (fieldValue!=null) {
				        OccurrenceFieldTO ofto = new OccurrenceFieldTO();
				        ofto.setId(frm.getId());
				        ofto.setField(fields[i]);
				        ofto.setValue(fieldValue);
				        
				        String type = obus.getType(fields[i]);
				        if (type!=null && type.equals(FieldValueTO.FIELD_TYPE_DATE)){
				        	ofto.setDateValue(DateUtil.getDateTime(fieldValue, oto.getHandler().getCalendarMask(), oto.getLocale()));
				        	
				        } else if (type!=null && type.equals(FieldValueTO.FIELD_TYPE_GRID)){
				        	String linesCountStr = request.getParameter(fields[i] + "_rows");
				        	Vector<Vector<Object>> tableData = new Vector<Vector<Object>>();
				        	FieldValueTO fvto = obus.getField(fields[i]);
				        	if (fvto!=null && linesCountStr!=null && !linesCountStr.trim().equals("")) {
				        		int cols = Integer.parseInt(linesCountStr);
				        		
				        		//get all rows data...
				        		for(int r=0; r<cols; r++) {
				        			Vector<Object> newLine = null;
					        		for (FieldValueTO column : fvto.getGridFields()) {
					        			String cellValStr = request.getParameter(column.getId() + "_" + r);
					        			if (cellValStr!=null) {
					        				if (newLine==null) {
					        					newLine = new Vector<Object>();
					        				}					        				
					        				newLine.add(cellValStr);
					        			}
									}
					        		if (newLine!=null) {
					        			tableData.add(newLine);	
					        		}
				        		}
				        		
				        		//get additional row..
				        		Vector<Object> newLine = new Vector<Object>();
				        		for (FieldValueTO column : fvto.getGridFields()) {
				        			String cellValStr = request.getParameter(column.getId());
				        			if (cellValStr==null) {
				        				cellValStr = "";
				        			}				        			
				        			newLine.add(cellValStr);
								}
				        		tableData.add(newLine);
				        		
				        	}
				        	ofto.setTableValues(tableData);
				        	
				        } else {
				        	ofto.setDateValue(null);
				        }

				        oto.addField(ofto);
			        }
			    }	        		        
		    }
	    }
	    
		@SuppressWarnings("unchecked")
		Vector<MetaFieldTO> metaFieldList = (Vector)request.getSession().getAttribute("metaFieldList");
		super.setMetaFieldValuesFromForm(metaFieldList, request, oto);	    
		if (metaFieldList!=null) {
		    frm.setAdditionalFields(oto.getAdditionalFields());
		}
	    
	    return oto;
	}
	
	
	private String getIndicatorList(Vector<ReportTO> indicators, HttpServletRequest request, String oid){
		StringBuffer response = new StringBuffer("<table class=\"table\" width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"2\">");
		
		if (indicators!=null) {
			Iterator<ReportTO> i = indicators.iterator();
			while(i.hasNext()) {
				ReportTO kpi = i.next();				
				response.append("<tr>\n");
				response.append("<td class=\"tableCell\" align=\"left\" valign=\"center\">" + kpi.getName() + "</td>\n");	
				response.append("<td class=\"tableCell\" width=\"15\" align=\"center\" valign=\"center\">");
				response.append(kpi.getGenericTag());
				response.append("</td>\n");
				response.append("<td class=\"tableCell\" width=\"130\" align=\"center\" valign=\"center\">");
				response.append(super.getBundleMessage(request, "label.manageReport.persp." + kpi.getReportPerspectiveId()));
				response.append("</td>\n");
				response.append("<td class=\"tableCell\" width=\"10\" align=\"left\" valign=\"center\">");
				response.append("   <a href=\"javascript:removeKpiLink('" + kpi.getId() + "', '" + oid + "');\" border=\"0\"><img border=\"0\" src=\"../images/remove.gif\" ></a>");
				response.append("</td>\n");
				response.append("</tr>\n");
			}
		}
		
		response.append("</table>");
		return response.toString();
	}
	
	
	public ActionForward occTableRemoveRow(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
        OccurrenceDelegate odel = new OccurrenceDelegate();		
	    try {
	        OccurrenceForm frm = (OccurrenceForm)form;
	        String gridId = frm.getGenericTag();
	        String[] tokens = gridId.split("_");
	        if (tokens!=null && tokens.length==2) {
	        	int rowToBeRemoved = Integer.parseInt(tokens[1]);
	        	String strRows = request.getParameter(tokens[0] + "_rows");
	        	
			    Occurrence obus = getOccurrenceClass(frm.getSource());
		        FieldValueTO fvto = obus.getField(tokens[0]);

			    if (fvto!=null && strRows!=null && !strRows.trim().equals("") && obus!=null && obus.getFields()!=null) {
			    	int rows = Integer.parseInt(strRows);
			    	if (rows>0) {
					    OccurrenceTO oto = odel.getOccurrenceObject(new OccurrenceTO(frm.getId()));
					    Vector<Vector<Object>> fieldTableValues = new Vector<Vector<Object>>();
					    
					    for(int r=0; r<rows;r++) {
					    	if (r!=rowToBeRemoved) {
						    	Vector<Object> line = new Vector<Object>();
							    for (FieldValueTO column : fvto.getGridFields()) {
							    	String content = request.getParameter(column.getId() + "_" + r);
							    	if (content!=null) {
							    		line.add(content);	
							    	} else {
							    		line.add("");
							    	}
								}
							    fieldTableValues.add(line);					    		
					    	}
					    }
					    
					    OccurrenceFieldTO field = oto.getField(tokens[0]);
					    if (field!=null) {
					    	field.setTableValues(fieldTableValues);			    	
					    }
					    refreshOccurrenceSourceFields(request, frm, oto);
			    	}
			    }
	        }

			RequestDispatcher dispatcher = request.getRequestDispatcher("../do/manageOccurrence?operation=navigate");   
			dispatcher.forward(request, response);
	        
		} catch(Exception e){
	        this.setErrorFormSession(request, "error.generic.showFormError", e);
	    }
		
		return null;
	}
	

	public ActionForward occTableAddRow(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
        OccurrenceDelegate odel = new OccurrenceDelegate();		
	    try {
	        OccurrenceForm frm = (OccurrenceForm)form;
	        String gridId = frm.getGenericTag();

	        String strRows = request.getParameter(gridId + "_rows");
		    Occurrence obus = getOccurrenceClass(frm.getSource());
	        FieldValueTO fvto = obus.getField(gridId);

		    if (fvto!=null && strRows!=null && !strRows.trim().equals("") && obus!=null && obus.getFields()!=null) {
		    	int rows = Integer.parseInt(strRows);
		    	if (rows>0) {
				    OccurrenceTO oto = odel.getOccurrenceObject(new OccurrenceTO(frm.getId()));
				    Vector<Vector<Object>> fieldTableValues = new Vector<Vector<Object>>();
				    
				    for(int r=0; r<rows;r++) {
				    	Vector<Object> line = new Vector<Object>();
					    for (FieldValueTO column : fvto.getGridFields()) {
					    	String content = request.getParameter(column.getId() + "_" + r);
					    	if (content!=null) {
					    		line.add(content);	
					    	} else {
					    		line.add("");
					    	}
						}
					    fieldTableValues.add(line);
				    }
				    
				    //new content line...
				    Vector<Object> newLine = new Vector<Object>();
				    for (FieldValueTO column : fvto.getGridFields()) {
				    	String content = request.getParameter(column.getId());
				    	if (content!=null) {
				    		newLine.add(content);	
				    	} else {
				    		newLine.add("");
				    	}
					}
				    fieldTableValues.add(newLine);

				    if (oto!=null) {
					    OccurrenceFieldTO field = oto.getField(gridId);
					    if (field!=null) {
					    	field.setTableValues(fieldTableValues);			    	
					    }				    	
				    }
				    refreshOccurrenceSourceFields(request, frm, oto);				    
		    	}
		    }

			RequestDispatcher dispatcher = request.getRequestDispatcher("../do/manageOccurrence?operation=navigate");   
			dispatcher.forward(request, response);
	        
		} catch(Exception e){
	        this.setErrorFormSession(request, "error.generic.showFormError", e);
	    }
		return null;
	}
	
}
