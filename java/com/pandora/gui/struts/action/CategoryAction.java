package com.pandora.gui.struts.action;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.CategoryTO;
import com.pandora.PreferenceTO;
import com.pandora.ProjectTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.CategoryDelegate;
import com.pandora.delegate.PreferenceDelegate;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.gui.struts.form.CategoryForm;
import com.pandora.gui.struts.form.GeneralStrutsForm;
import com.pandora.helper.SessionUtil;


/**
 * This class handle the actions performed into Category form
 */
public class CategoryAction extends GeneralStrutsAction{
    
	CategoryDelegate cdel = new CategoryDelegate();
	
	/**
	 * Shows the Category form
	 */
	public ActionForward prepareForm(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response){
		String forward = "showCategory";
	    CategoryForm frm = (CategoryForm)form;
	    this.clearForm(form, request);
	    
	    UserTO uto = SessionUtil.getCurrentUser(request);
	    String hideClosed = uto.getPreference().getPreference(PreferenceTO.CATEGORY_HIDE_CLOSED);
	    frm.setHideClosedCategory(hideClosed!=null && hideClosed.equals("on"));
	    
	    this.refresh(mapping, form, request, response);
		return mapping.findForward(forward);
	}

	
	/**
	 * Clear all values of current form.
	 */
	private void clearForm(ActionForm form, HttpServletRequest request){
	    CategoryForm catfrm = (CategoryForm)form;
	    catfrm.clear();
		
		//set current operation status for Insertion
	    catfrm.setSaveMethod(CategoryForm.INSERT_METHOD, SessionUtil.getCurrentUser(request));
	}

	
	/**
	 * Get a category object from data base and set into form 
	 */
	public ActionForward editCategory(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showCategory";
	    
	    try {
	        CategoryForm cfrm = (CategoryForm)form;

			//clear messages of form
			this.clearMessages(request);
	        
			//set current operation status for Updating	
			cfrm.setSaveMethod(CategoryForm.UPDATE_METHOD, SessionUtil.getCurrentUser(request));
	        
			//create a category object used such a filter
			CategoryTO filter = new CategoryTO();
			filter.setId(cfrm.getId());
			
			//get a specific category from data base
			CategoryTO cto = cdel.getCategory(filter);
			
			//put the data (from DB) into html fields
			this.getActionFormFromTransferObject(cto, cfrm, request);
			
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.prepareEditCategoryForm", e);		    
	    }

	    return mapping.findForward(forward);
	}

	
	/**
	 * Insert or Update data of Category into data base
	 */
	public ActionForward saveCategory(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showCategory";
		String errorMsg = "error.prepareEditCategoryForm";
		String succeMsg = "message.success";
		
		try {
		    CategoryForm cfrm = (CategoryForm)form;

			//create an CategoryTO object based on html fields
			CategoryTO cto = this.getTransferObjectFromActionForm(cfrm, request);
					
			if (cfrm.getSaveMethod().equals(GeneralStrutsForm.INSERT_METHOD)){
			    errorMsg = "error.insertCategoryForm";
			    succeMsg = "message.category.insert";
			    cdel.insertCategory(cto);
			    this.clearForm(form, request);
			} else {
			    errorMsg = "error.updateCategoryForm";
			    succeMsg = "message.category.update";
			    cdel.updateCategory(cto);
			}
			
			//set success message into http session
			this.setSuccessFormSession(request, succeMsg);
			
			//refresh lists on form...
			this.refresh(mapping, form, request, response);
			
		} catch(Exception e){
		    this.setErrorFormSession(request, errorMsg, e);		    
		}
		return mapping.findForward(forward);		
	}

	
    /**
     * Remove a category from data base
	 */
	public ActionForward removeCategory(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showCategory";
		try {
			CategoryForm cfrm = (CategoryForm)form;
						
			//create an CategoryTO object based on html fields
			CategoryTO cto = new CategoryTO();
			cto.setId(cfrm.getId());
			
			//remove the category!
			cdel.removeCategory(cto);

			//clear form and messages
			this.clearMessages(request);
			this.clearForm(cfrm, request);

			//set success message into http session
			this.setSuccessFormSession(request, "message.category.remove");
			
			//refresh lists on form...
			this.refresh(mapping, form, request, response);
		
		} catch(Exception e){
		    this.setErrorFormSession(request, "error.removeCategoryForm", e);
		}
		return mapping.findForward(forward);		
	}

	
	/**
	 * Clear form after html reset button 
	 */
	public ActionForward clear(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response) {
	    String forward = "showCategory";
	    this.clearForm(form, request);
		this.clearMessages(request);
		return mapping.findForward(forward);		
	}
	
	
    /**
	 * Get all data of category from data base and refresh the combo boxes
	 * of form. 
	 */	
	public ActionForward refresh(ActionMapping mapping, ActionForm form,
	        	HttpServletRequest request, HttpServletResponse response){
	    String forward = "showCategory";
	    CategoryDelegate catDel = new CategoryDelegate();
	    PreferenceDelegate pfdel = new PreferenceDelegate();
	    
	    try {
			CategoryForm frm = (CategoryForm)form;
	    	
			//get all Projects from data base and put into http session (to be displayed by combo)
			ProjectTO dummy = new ProjectTO("-1");
			dummy.setName(this.getBundleMessage(request, "label.category.anyProject"));
	        
			ProjectDelegate pdel = new ProjectDelegate();
			Vector<ProjectTO> prjList = pdel.getProjectList();
			prjList.insertElementAt(dummy, 0);
			request.getSession().setAttribute("projectList", prjList);
		
		    //save the new preference
		    UserTO uto = SessionUtil.getCurrentUser(request);
		    PreferenceTO pto = new PreferenceTO(PreferenceTO.CATEGORY_HIDE_CLOSED, frm.getHideClosedCategory()?"on":"off", uto);
			uto.getPreference().addPreferences(pto);
			pto.addPreferences(pto);
			pfdel.insertOrUpdate(pto);
			
		    //create a list of categories
	        Vector<CategoryTO> catList = catDel.getCategoryList(frm.getHideClosedCategory());
		    request.getSession().setAttribute("categoryList", catList);
		    
		    //create a list of types
		    Vector<TransferObject> typeList = new Vector<TransferObject>();
		    for (int i = 0; i<=10; i++) {
		        TransferObject to = new TransferObject();
		        to.setId(i+"");
		        to.setGenericTag(this.getBundleMessage(request, "label.category.type." + i));
		        typeList.addElement(to);
		    }	    
			request.getSession().setAttribute("typeList", typeList);
					
		} catch(Exception e){
		    this.setErrorFormSession(request, "message.category.showForm", e);
		}
		
		return mapping.findForward(forward);
	}


    private CategoryTO getTransferObjectFromActionForm(CategoryForm cfrm, HttpServletRequest request) {
        CategoryTO response = new CategoryTO();
        response.setId(cfrm.getId());
        response.setType(new Integer(cfrm.getType()));
        response.setDescription(cfrm.getDescription());
        response.setName(cfrm.getName());
        response.setIsBillable(new Boolean(cfrm.getIsBillableTask()));
        response.setIsDefect(new Boolean(cfrm.getIsDefectTask()));
        response.setIsTesting(new Boolean(cfrm.getIsTestingTask()));
        response.setIsDevelopingTask(new Boolean(cfrm.getIsDevelopingTask()));
        response.setIsHidden(new Boolean(cfrm.getIsHidden()));
        
        if (cfrm.getPositionOrder()!=null) {
        	response.setPositionOrder(Integer.parseInt(cfrm.getPositionOrder()));	
        } else {
        	response.setPositionOrder(0);
        }
        
        response.setProject(null);
        if (!cfrm.getProjectId().equals("-1")) {
            response.setProject(new ProjectTO(cfrm.getProjectId()));    
        }
        
        return response;
    }
	
    private void getActionFormFromTransferObject(CategoryTO cto, CategoryForm cfrm, HttpServletRequest request) {
        cfrm.setId(cto.getId());
        cfrm.setName(cto.getName());
        cfrm.setDescription(cto.getDescription());
        if (cto.getType()!=null) {
            cfrm.setType(cto.getType().toString());    
        } else {
            cfrm.setType(CategoryTO.TYPE_TASK.toString());
        }
        if (cto.getProject()!=null) {
            cfrm.setProjectId(cto.getProject().getId());    
        } else {
            cfrm.setProjectId("-1");
        }
        if (cto.getIsBillable()!=null) {
        	cfrm.setIsBillableTask(cto.getIsBillable().booleanValue());
        } else {
        	cfrm.setIsBillableTask(false);
        }
        if (cto.getIsDefect()!=null) {
        	cfrm.setIsDefectTask(cto.getIsDefect().booleanValue());
        } else {
        	cfrm.setIsDefectTask(false);
        }
        if (cto.getIsTesting()!=null) {
        	cfrm.setIsTestingTask(cto.getIsTesting().booleanValue());
        } else {
        	cfrm.setIsTestingTask(false);
        }
        if (cto.getIsDevelopingTask()!=null) {
        	cfrm.setIsDevelopingTask(cto.getIsDevelopingTask().booleanValue());
        } else {
        	cfrm.setIsDevelopingTask(false);
        }

        if (cto.getIsHidden()!=null) {
        	cfrm.setIsHidden(cto.getIsHidden().booleanValue());
        } else {
        	cfrm.setIsHidden(false);
        }

        cfrm.setPositionOrder(cto.getPositionOrder()+"");
    }    
}