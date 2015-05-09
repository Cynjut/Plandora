package com.pandora.gui.struts.action;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.pandora.PreferenceTO;
import com.pandora.RootTO;
import com.pandora.TransferObject;
import com.pandora.UserTO;
import com.pandora.delegate.MetaFormDelegate;
import com.pandora.delegate.SurveyDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.form.LoginForm;
import com.pandora.helper.PosterUtil;
import com.pandora.helper.SessionUtil;

/**
 * This class handle the actions performed into login form 
 */
public class LoginAction extends GeneralStrutsAction{

	/**
	 * Shows the login form
	 */
	public ActionForward prepareLogin(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response){	
		String forward = "showLogin";
		
		LoginForm frm = (LoginForm) form;
		frm.setUsername("");
		frm.setPassword("");
		
		this.clearMessages(request);
		return mapping.findForward(forward);
	}
	

	/**
	 * Authenticate user
	 */
	public ActionForward doLogin(ActionMapping mapping, ActionForm form,
								 HttpServletRequest request, HttpServletResponse response){	
		MetaFormDelegate mfrmDel = new MetaFormDelegate();
		SurveyDelegate sDel = new SurveyDelegate();
		String forward = "showLogin";
		try {    
		    //create a new User TO with informations of username/password
		    this.clearMessages(request);
		    LoginForm frm = (LoginForm) form;
		    UserTO uto = new UserTO();
		    uto.setUsername(frm.getUsername());
		    uto.setPassword(frm.getPassword());

		    //authenticate user!
		    UserDelegate deleg = new UserDelegate();
		    uto = deleg.authenticateUser(uto, false);
		    if (uto!=null){
		        
		        UserTO childUser = deleg.getUserTopRole(uto);
		        childUser.setPreference(uto.getPreference());
		        childUser.setBundle(getResources(request));
		        
		        childUser.setLanguage(request.getLocale().getLanguage());
		        childUser.setCountry(request.getLocale().getCountry());
		        
		        //update the user with the newest information about locale
		        deleg.updateUser(childUser);
		        
		        //...store current user into http session
		        request.getSession().removeAttribute(UserDelegate.CURRENT_USER_SESSION);
		        request.getSession().setAttribute(UserDelegate.CURRENT_USER_SESSION, childUser);

				Vector v = mfrmDel.getMetaFormList();
				request.getSession().setAttribute("metaFormList", v);			

			    request.getSession().removeAttribute(ShowSurveyAction.PARTIAL_ANSWERS);
				request.getSession().removeAttribute(UserDelegate.USER_SURVEY_LIST);
				Vector vs = sDel.getSurveyListByUser(childUser, false);
				if (vs!=null && vs.size()>0) {
					request.getSession().setAttribute(UserDelegate.USER_SURVEY_LIST, vs);					
				}

		        //go to...
		        forward = "home";

		    } else {
		        setErrorFormSession(request, "error.authentication", null);
		    }

		} catch (BusinessException e) {
			setErrorFormSession(request, "error.authentication", e);
		}	
		return mapping.findForward(forward);
	}
	
	/**
	 * 
	 */
	public ActionForward doLogout(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){	
	    String forward = "showLogin";

	    this.clearMessages(request);
	    LoginForm frm = (LoginForm) form;
	    frm.setPassword("");
	    frm.setUsername("");
	    
	    request.getSession().removeAttribute(UserDelegate.CURRENT_USER_SESSION);
	    request.getSession().invalidate();
	    
	    return mapping.findForward(forward);
	}

	
	/**
	 * This method is used to redirect to appropriate form depending of users role. 
	 */
	public ActionForward resolveForward(ActionMapping mapping, ActionForm form,
			 HttpServletRequest request, HttpServletResponse response){	

		String forward = "showLogin";
		
		//get user from http session
		UserTO uto = SessionUtil.getCurrentUser(request);
		
		//check the new version of Plandora
		if (uto.getUsername().equals(RootTO.ROOT_USER)) {
			this.getNextPlandoraVersion(request, uto);
		}
		
		if (uto!=null){		
		    forward = uto.getForwardLogin();
		} else {
		    forward = "showLogin";		    
		}

		return mapping.findForward(forward);
	}
	
	
	private void getNextPlandoraVersion(HttpServletRequest request, UserTO uto){		
		try {
			String currentVersion = super.getBundleMessage(request, "footer.version");			
			String url = uto.getPreference().getPreference(PreferenceTO.NEW_VERSION_URL);
			if (url!=null && !url.equals("")) {
				
				TransferObject to = (TransferObject)request.getSession().getAttribute(PreferenceTO.NEW_VERSION_URL);
				if (to==null || (to!=null && to.getGenericTag().equals(""))) {	
					PosterUtil poster = new PosterUtil();
					poster.setURL(url + "?cv=" + currentVersion + "&lg=" + uto.getLocale().toString());
					poster.openGet();
					String response = "";
					if(poster.readLine()){
						response = poster.getLine();
						if (response.length()>50) {
							response = response.substring(0, 50);
						}
					}
					if (response!=null && !response.equals("") && !response.equals(currentVersion)) {
						TransferObject urlTo = new TransferObject(response, response);
						request.getSession().setAttribute(PreferenceTO.NEW_VERSION_URL, urlTo);
					}
				}
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}
}
