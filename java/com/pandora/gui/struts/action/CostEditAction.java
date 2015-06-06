package com.pandora.gui.struts.action;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.CostInstallmentTO;
import com.pandora.CostStatusTO;
import com.pandora.CostTO;
import com.pandora.ExpenseTO;
import com.pandora.LeaderTO;
import com.pandora.MetaFieldTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.CostDelegate;
import com.pandora.delegate.CostStatusDelegate;
import com.pandora.delegate.MetaFieldDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.exception.MetaFieldNumericTypeException;
import com.pandora.gui.struts.form.CostEditForm;
import com.pandora.helper.DateUtil;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.SessionUtil;
import com.pandora.helper.StringUtil;

public class CostEditAction extends GeneralStrutsAction {

	@SuppressWarnings("rawtypes")
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		String forward = "showCostEdit";
		ProjectDelegate pdel = new ProjectDelegate();
		CategoryDelegate cdel = new CategoryDelegate();
		CostDelegate codel = new CostDelegate();
		CostStatusDelegate csdel = new CostStatusDelegate();
		UserDelegate udel = new UserDelegate();
		MetaFieldDelegate mfdel = new MetaFieldDelegate();
		
		try {
			CostEditForm frm = (CostEditForm)form;
			
			Vector<ProjectTO> projectList = new Vector<ProjectTO>();
			ProjectTO pto = pdel.getProjectObject(new ProjectTO(frm.getProjectId()), true);
			projectList.add(pto);
			
			Vector<ProjectTO> childProject = pdel.getAllProjectsByParent(pto, true);
			projectList.addAll(childProject);
			request.getSession().setAttribute("costProjectList", projectList);

			Vector<CategoryTO> categoryList = cdel.getCategoryListByType(CategoryTO.TYPE_COST, pto, false);
			request.getSession().setAttribute("costCategoryList", categoryList);

			UserTO uto = SessionUtil.getCurrentUser(request);
			Vector<TransferObject> accCodeList = new Vector<TransferObject>();
			accCodeList.add(new TransferObject("", ""));
			accCodeList.addAll(codel.getAccountCodesByLeader(uto));
			request.getSession().setAttribute("accountCodeList", accCodeList);
			
			String metaFieldProjectId = frm.getProjectId();
			
			if (frm.getEditCostId()!=null && !frm.getEditCostId().trim().equals("")){
				
				CostTO cto = null;
				if (frm.getUsedByExpenseForm()!=null && frm.getUsedByExpenseForm().equalsIgnoreCase("on")) {
					HashMap expenseItems = (HashMap) request.getSession().getAttribute("expenseItemHash");
					if (expenseItems!=null) {
						cto = (CostTO)expenseItems.get(frm.getEditCostId());	
					}
				} else {
					cto = codel.getCost(new CostTO(frm.getEditCostId()));
				}
				this.getActionFormFromTransferObject(cto, frm, request);
				if (cto!=null){
					metaFieldProjectId = cto.getProject().getId();	
				}

			} else {
				frm.clear();
				Vector<CostInstallmentTO> costInstallment = new Vector<CostInstallmentTO>();
				
				if (frm.getUsedByExpenseForm()!=null && frm.getUsedByExpenseForm().equalsIgnoreCase("on")) {
					
					CostStatusTO expStatus = null;
	            	LeaderTO eto = new LeaderTO(uto.getId());
	            	eto.setProject(new ProjectTO(frm.getProjectId()));
	            	eto = udel.getLeader(eto);
	            	if (eto!=null) {
	            		//if the leader is open a expense, the status must be set to 'approved' already 
	            		expStatus = csdel.getCostStatusByState(CostStatusTO.STATE_MACHINE_BUDGETED);	
	            	} else {
	            		expStatus = csdel.getCostStatusByState(CostStatusTO.STATE_MACHINE_WAITING);
	            	}
					
					CostInstallmentTO newInst = new CostInstallmentTO();
					newInst.setInstallmentNum(new Integer("1"));
					newInst.setCost(null);
					newInst.setCostStatus(expStatus);
					newInst.setValue(new Long("0"));
					
					//set into the session the last installment due date to make the fulfil more confortable to user..
					Timestamp defDueDt = DateUtil.getNow();
					if (request.getSession().getAttribute("lastInstallmentDueDate")!=null) {
						defDueDt = (Timestamp)request.getSession().getAttribute("lastInstallmentDueDate");	
					}
					newInst.setDueDate(DateUtil.getDate(defDueDt, true));
					
					costInstallment.add(newInst);
				}
				
				request.getSession().setAttribute("costInstallmentHash", costInstallment);				
			}
			this.refreshInstallment(frm, request);

			Vector<MetaFieldTO> mflist = mfdel.getListByProjectAndContainer(metaFieldProjectId, null, MetaFieldTO.APPLY_TO_COST);
			request.getSession().setAttribute("metaFieldCostList", new Vector<MetaFieldTO>());
			if (mflist!=null) {
				request.getSession().setAttribute("metaFieldCostList", mflist);
			}
			
		} catch (Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}

		return mapping.findForward(forward);
	}
		
	
	@SuppressWarnings({ "unchecked", "rawtypes" })	
	public ActionForward saveCost(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		CostDelegate cdel = new CostDelegate();
		CategoryDelegate ctdel = new CategoryDelegate();
		String errorMessage = null;
		CostEditForm frm = (CostEditForm)form;
		
		try {
			CostTO cto = new CostTO();
			errorMessage = this.validateBeforeSave(request, frm);
			if (errorMessage==null) {
				Vector<CostInstallmentTO> list = (Vector<CostInstallmentTO>) request.getSession().getAttribute("costInstallmentHash");
				if (list!=null && list.size()>0) {
					
					cto.setAccountCode(frm.getAccountCode());
					cto.setCategory(ctdel.getCategory(new CategoryTO(frm.getCategoryId())));
					cto.setCreationDate(DateUtil.getNow());
					cto.setDescription(frm.getDescription());
					cto.setFinalDate(null);
					cto.setInstallments(list);
					cto.setName(frm.getName());
					cto.setProject(new ProjectTO(frm.getProjectId()));
					cto.setId(frm.getEditCostId());
					
					this.retrieveMetaFieldValue(request, frm, cto);
					
					if (frm.getUsedByExpenseForm()!=null && frm.getUsedByExpenseForm().equalsIgnoreCase("on")) {
						HashMap expenseItems = (HashMap) request.getSession().getAttribute("expenseItemHash");
						if (expenseItems==null) {
							expenseItems = new HashMap<String,CostTO>();	
						}
						
						if (frm.getEditCostId()==null || frm.getEditCostId().trim().equals("")) {
							cto.setId("NEW_" + expenseItems.size());
						}
					
						expenseItems.remove(cto.getId());
						expenseItems.put(cto.getId(), cto);
						request.getSession().setAttribute("expenseItemHash", expenseItems);
						
					} else {

						UserTO uto = SessionUtil.getCurrentUser(request);

						if (frm.getEditCostId()!=null && !frm.getEditCostId().trim().equals("")) {
							cto.setId(frm.getEditCostId());
							
							//set the identification of approver/refuser...
							CostTO dbct = cdel.getCost(cto);
							ExpenseTO exto = dbct.getExpense();
							if (exto!=null) {
								exto.setRefuserAprroverId(uto.getId());	
							}
							cto.setExpense(exto);
							
							cdel.updateCost(cto);
						} else {
							
							//set the identification of approver/refuser...
							ExpenseTO exto = new ExpenseTO();
							exto.setRefuserAprroverId(uto.getId());
							cto.setExpense(exto);
							
							cto.setId(null);
							cdel.insertCost(cto);
						}											
					}
					
					//set into the session the last installment due date to make the fulfil more confortable to user..
					request.getSession().removeAttribute("lastInstallmentDueDate");
					CostInstallmentTO lastInstalmment = list.get(list.size()-1);
					if (lastInstalmment.getDueDate()!=null) {
						request.getSession().setAttribute("lastInstallmentDueDate", lastInstalmment.getDueDate());
					}
					
				}				
			}
			
            if (errorMessage!=null) {
            	this.setErrorFormSession(request, errorMessage, null);
            }
			
		} catch(MetaFieldNumericTypeException e){
			this.setErrorFormSession(request, e.getMessage(), e.getMetaFieldName(), null, null, null, null, e);
		} catch (Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
		return mapping.findForward(frm.getForwardAfterSave());
	}

	
	@SuppressWarnings("unchecked")
	private String validateBeforeSave(HttpServletRequest request, CostEditForm frm) {
		String response = null;
		
		if (frm.getName()==null || frm.getName().trim().equals("")) {
			response = "validate.cost.blankName";
		}
		
		Vector<MetaFieldTO> metaFieldList = (Vector<MetaFieldTO>)request.getSession().getAttribute("metaFieldCostList");
		if (metaFieldList!=null) {
			for (MetaFieldTO mfto : metaFieldList) {
				String val = request.getParameter("META_DATA_" + mfto.getId());
				try {
					val = URLEncoder.encode(val, "UTF-8");
					val = val.replaceAll("%C2%A0", "");
				} catch(Exception e){
					e.printStackTrace();
				}
				
				if (mfto.isMandatory() && (val==null || val.trim().equals(""))) {
					response = "validate.cost.invalidMeta";
				}
			}
		}

		Vector<CostInstallmentTO> list = (Vector<CostInstallmentTO>) request.getSession().getAttribute("costInstallmentHash");
		if (list==null || list.size()==0) {
			response = "validate.cost.blankInst";
		} else {
			for (CostInstallmentTO cito : list) {				
				if (cito.getDueDate()==null) {
					response = "validate.cost.invalidDueDt";
				}
				if (cito.getCostStatus()==null) {
					response = "validate.cost.invalidStat";
				}
				if (cito.getValue()==null) {
					response = "validate.cost.blankValue";					
				} else if (cito.getValue().longValue()<=0) {
					response = "validate.cost.invalidValue";
				}
			}
		}
		
		return response;
	}


	@SuppressWarnings("unchecked")
	public ActionForward addInstallment(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		CostStatusDelegate csdel = new CostStatusDelegate();
		try {
			CostEditForm frm = (CostEditForm)form;
			Vector<CostInstallmentTO> list = (Vector<CostInstallmentTO>) request.getSession().getAttribute("costInstallmentHash");
			if (list!=null) {
				CostStatusTO initialStatus = csdel.getCostStatusByState(CostStatusTO.STATE_MACHINE_BUDGETED);
				
				Timestamp refDate = DateUtil.getDate(DateUtil.getNow(), true);
				
				//get the last installment and increment a new month at due date...
				if (list.size()>0) {
					CostInstallmentTO lastInst = (CostInstallmentTO)list.get(list.size()-1);
					if (lastInst.getDueDate()!=null) {
						refDate = DateUtil.getChangedDate(lastInst.getDueDate(), Calendar.MONTH, 1);
					}
				}
				
				CostInstallmentTO newInst = new CostInstallmentTO();
				newInst.setInstallmentNum(new Integer(list.size()+1));
				newInst.setCost(null);
				newInst.setCostStatus(initialStatus);
				newInst.setDueDate(refDate);
				newInst.setValue(new Long("0"));
				list.add(newInst);
				
				request.getSession().removeAttribute("costInstallmentHash");
				request.getSession().setAttribute("costInstallmentHash", list);
			}
						
			StringBuffer sb = this.refreshInstallment(frm, request);
			response.setContentType("text/xml");  
            response.setHeader("Cache-Control", "no-cache");  
            PrintWriter out = response.getWriter();
            out.println(sb.toString());  
            out.flush();
			
		} catch (Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
	    return null;
	}

	
	@SuppressWarnings("unchecked")
	public ActionForward removeInstallment(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		try {
			CostEditForm frm = (CostEditForm)form;
			Vector<CostInstallmentTO> list = (Vector<CostInstallmentTO>) request.getSession().getAttribute("costInstallmentHash");
			if (list!=null) {
				int removeIdx = Integer.parseInt(frm.getRemoveInstId());
				list.remove(removeIdx-1);
				request.getSession().removeAttribute("costInstallmentHash");
				request.getSession().setAttribute("costInstallmentHash", list);
			}

			StringBuffer sb = this.refreshInstallment(frm, request);
			response.setContentType("text/xml");  
            response.setHeader("Cache-Control", "no-cache");  
            PrintWriter out = response.getWriter();
            out.println(sb.toString());  
            out.flush();
			
		} catch (Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
	    return null;
	}
	
	
	@SuppressWarnings("unchecked")
	public ActionForward changeInstallment(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		try {
			String errorMessage = null;
			CostEditForm frm = (CostEditForm)form;
			Vector<CostInstallmentTO> list = (Vector<CostInstallmentTO>) request.getSession().getAttribute("costInstallmentHash");
			if (list!=null) {
				UserTO uto = SessionUtil.getCurrentUser(request);
				
				int changeIdx = Integer.parseInt(frm.getChangeInstId());
				CostInstallmentTO cito = list.get(changeIdx-1);
				if (frm.getChangeInstType().equals("STAT")) {
					cito.setCostStatus(new CostStatusTO(frm.getChangeInstValue()));
					
				} else if (frm.getChangeInstType().equals("DUEDT")) {
					Timestamp ts = DateUtil.getDateTime(frm.getChangeInstValue(), uto.getCalendarMask(), uto.getLocale());
					if (ts!=null) {
						ts = DateUtil.getDate(ts, true);
						cito.setDueDate(ts);	
					} else {
						errorMessage = super.getBundleMessage(request, "validate.cost.invalidDueDt");
					}

				} else if (frm.getChangeInstType().equals("VAL")) {
					if (frm.getChangeInstValue()==null || frm.getChangeInstValue().trim().equals("")) {
						errorMessage = super.getBundleMessage(request, "validate.cost.blankValue");
					} else {
						if (StringUtil.checkIsFloat(frm.getChangeInstValue(), uto.getLocale())) {
							cito.setValue(StringUtil.getStringToCents(frm.getChangeInstValue(), uto.getLocale()));
						} else {
							errorMessage = super.getBundleMessage(request, "validate.cost.invalidValue");
						}						
					}
				}

	            if (errorMessage==null) {
					request.getSession().removeAttribute("costInstallmentHash");
					request.getSession().setAttribute("costInstallmentHash", list);					            	
	            }
			}

			response.setContentType("text/xml");  
            response.setHeader("Cache-Control", "no-cache");  
            PrintWriter out = response.getWriter();
            if (errorMessage!=null) {
            	out.println(errorMessage);	
            }
            out.flush();
			
		} catch (Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
	    return null;
	}

	
	public ActionForward changeProject(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){
		CategoryDelegate cdel = new CategoryDelegate();
		try {
			CostEditForm frm = (CostEditForm)form;			
			Vector<CategoryTO> categoryList = cdel.getCategoryListByType(CategoryTO.TYPE_COST, new ProjectTO(frm.getProjectId()), false);
			request.getSession().setAttribute("costCategoryList", categoryList);
			
			String comboContent = HtmlUtil.getComboBox("categoryId", categoryList, "textBox", frm.getCategoryId());
            response.setContentType("text/xml");  
            response.setHeader("Cache-Control", "no-cache");  
            PrintWriter out = response.getWriter();
            out.println(comboContent);  
            out.flush();
			
		} catch (Exception e){
			this.setErrorFormSession(request, "error.generic.showFormError", e);
		}
	    return null;
	}


	@SuppressWarnings("unchecked")
	private StringBuffer refreshInstallment(CostEditForm frm, HttpServletRequest request) throws BusinessException{
		StringBuffer sb = new StringBuffer();
		StringBuffer sbValid = new StringBuffer();
		CostStatusDelegate csdel = new CostStatusDelegate();
		UserDelegate udel = new UserDelegate();

		Vector<CostInstallmentTO> list = (Vector<CostInstallmentTO>) request.getSession().getAttribute("costInstallmentHash");
		if (list!=null) {
			UserTO uto = SessionUtil.getCurrentUser(request);
            String remove = super.getBundleMessage(request, "label.costedit.removeInst");
            
            Locale loc = udel.getCurrencyLocale();
			NumberFormat nf = NumberFormat.getCurrencyInstance(loc);
			String cs = nf.getCurrency().getSymbol(loc);
            
			String selectedStatus = "";
			Vector<CostStatusTO> statusList = null;   
            if (frm.getUsedByExpenseForm()!=null && frm.getUsedByExpenseForm().equalsIgnoreCase("on")) {
            	LeaderTO eto = new LeaderTO(uto.getId());
            	eto.setProject(new ProjectTO(frm.getProjectId()));
            	eto = udel.getLeader(eto);
            	if (eto!=null) {
            		//if the leader is open a expense, the status must be set to 'approved' already 
            		statusList = csdel.getCostStatusList(CostStatusTO.STATE_MACHINE_BUDGETED);	
            	} else {
            		statusList = csdel.getCostStatusList(CostStatusTO.STATE_MACHINE_WAITING);
            	}
            } else {
            	statusList = csdel.getCostStatusList(CostStatusTO.STATE_MACHINE_WAITING);	
            }
            
            if (statusList!=null && statusList.size()>0) {
            	selectedStatus = ((CostStatusTO)statusList.get(0)).getId();	
            }
            
            
            int idx = 1;
			sb.append("<table width=\"100%\" border=\"0\" cellspacing=\"1\" cellpadding=\"0\">");			
			for (CostInstallmentTO cito: list) {
				
				String key = idx+"";
				cito.setInstallmentNum(new Integer(idx));
            	if (cito.getCostStatus()!=null) {
            		selectedStatus = cito.getCostStatus().getId();
            	}	            	
        		
	            String dueDate = DateUtil.getDate(cito.getDueDate(), uto.getCalendarMask(), uto.getLocale());
	            boolean isDisabled = false;
	            boolean isDisabledText = false;
	            frm.setStateMachinePaidOrCanceled("off");	            
	            
	            if (frm.getUsedByExpenseForm()!=null && cito.getCostStatus()!=null && frm.getUsedByExpenseForm().equalsIgnoreCase("on")) {
	            	isDisabled = true;
	            	
	            	 // When the status machine is paid or canceled the others fields this table must not be editable.
		            if(cito.getCostStatus() != null && cito.getCostStatus().getStateMachineOrder() != null){	            
		            	if( cito.getCostStatus().getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_PAID) 
		            			|| cito.getCostStatus().getStateMachineOrder().equals(CostStatusTO.STATE_MACHINE_CANCELED)){
		            		isDisabledText = true;
		            		// disabling labels when status machine is 'paid' or 'canceled'
		            		frm.setStateMachinePaidOrCanceled("on");
		            	} 
		            }
	            	
	            }

	            double val = cito.getValue().doubleValue() / 100;
	            String valStr = StringUtil.getDoubleToString(val, "0.00" , uto.getLocale());
	            String statusCombo = HtmlUtil.getComboBox("status_" + key, statusList, "textBox", selectedStatus, 0, 
		            		"javascript:changeInstallment('status_" + key+ "', '" + key + "', 'STAT')", isDisabled);
	            
				sb.append("<tr class=\"pagingFormBody\" height=\"20\"><td width=\"100\">&nbsp;</td>\n");
				sb.append("<td class=\"capCell\" width=\"20\"><center>" + key.toString() + "</center></td>\n");				
				sb.append("<td class=\"capCell\" width=\"80\"><center>" + HtmlUtil.getTextBox("due_date_" + key, dueDate, isDisabledText, "onblur=\"javascript:changeInstallment('due_date_" + key+ "', '" + key + "', 'DUEDT');\"", 10, 10) + "</center></td>\n");
				sb.append("<td class=\"capCell\"><center>" + statusCombo + "</center></td>\n");
				sb.append("<td class=\"capCell\" width=\"100\"><center>" + cs + "&nbsp;" + HtmlUtil.getTextBox("value_" + key, valStr, isDisabledText, "onblur=\"javascript:changeInstallment('value_" + key+ "', '" + key + "', 'VAL');\"",10, 10) + "</center></td>\n");
				
				if (frm.getUsedByExpenseForm()!=null && frm.getUsedByExpenseForm().equalsIgnoreCase("on")) {
					sb.append("<td width=\"20\">&nbsp;</td>\n");
				} else {
					sb.append("<td class=\"capCell\" width=\"20\"><center><a href=\"javascript:removeInstallment('" + key + "');\" border=\"0\"><img border=\"0\" title=\"" + remove + "\" alt=\"" + remove + "\" src=\"../images/remove.gif\" ></a></center></td>\n");	
				}				
				sb.append("</tr>\n");				
				idx++;
				
				sbValid.append("if (value_" + key + ".value == ''){\n");
				sbValid.append("   alert('" + super.getBundleMessage(request, "validate.cost.blankValue") + "');\n");
				sbValid.append("   return false;\n");
				sbValid.append("} else if (value_" + key + ".value=='0'){\n");
				sbValid.append("   alert('" + super.getBundleMessage(request, "validate.cost.invalidValue") + "');\n");
				sbValid.append("   return false;\n");				
				sbValid.append("} else if (!isCurrency(value_" + key + ".value)){\n");
				sbValid.append("   alert('" + super.getBundleMessage(request, "validate.resCapacity.invalidValue") + "');\n");
				sbValid.append("   return false;\n");
				sbValid.append("}\n");
				
			}
			sb.append("</table>");
			frm.setInstallmentHtmlBody(sb.toString());
			frm.setInstallmentHtmlValidation(sbValid.toString());
		}
		return sb;
	}

	
	private void getActionFormFromTransferObject(CostTO to, CostEditForm frm, HttpServletRequest request){
		if (to!=null) {
		    frm.setAccountCode(to.getAccountCode());
		    frm.setCategoryId(to.getCategory().getId());
		    frm.setDescription(to.getDescription());
		    frm.setName(to.getName());
		    frm.setEditCostId(to.getId());
		    frm.setProjectId(to.getProject().getId());
		    frm.setRemoveInstId(null);
		    frm.setAdditionalFields(to.getAdditionalFields());
		    request.getSession().setAttribute("costInstallmentHash", to.getInstallments());			
		}
	}
	
	
	@SuppressWarnings("unchecked")
	private void retrieveMetaFieldValue(HttpServletRequest request, ActionForm form, CostTO cto) throws MetaFieldNumericTypeException {
		Vector<MetaFieldTO> metaFieldList = (Vector<MetaFieldTO>)request.getSession().getAttribute("metaFieldCostList");
		super.setMetaFieldValuesFromForm(metaFieldList, request, cto);	    
		if (metaFieldList!=null) {
			CostEditForm frm = (CostEditForm)form;
			frm.setAdditionalFields(cto.getAdditionalFields());
		}
	}

	
}


