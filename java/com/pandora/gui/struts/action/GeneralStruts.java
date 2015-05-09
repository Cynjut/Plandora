package com.pandora.gui.struts.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionServlet;

import com.pandora.CustomerTO;
import com.pandora.LeaderTO;
import com.pandora.ResourceTO;
import com.pandora.RootTO;
import com.pandora.UserTO;
import com.pandora.exception.BusinessException;
import com.pandora.helper.LogUtil;
import com.pandora.helper.SessionUtil;

/**
 * This class checks if the current user was connected before actions calling
 */
public class GeneralStruts extends ActionServlet {

	private static final long serialVersionUID = 1L;

	/**
	 * Constructor
	 */
	public GeneralStruts() {
		super();
	}

	/**
	 * Re-implemented the http method, to redirect any URL to the login page in case of no user logged.
	 */
	public void process(HttpServletRequest request, HttpServletResponse response)
		throws IOException, ServletException {

		try{	
			UserTO uto = SessionUtil.getCurrentUser(request);
			
			//Check if the User is in the session and if user role has permission to use the form
			if (request.getPathInfo()!=null && this.checkGrants(uto, request)){

				//TODO: obter o UID do form checar se ele esta contido em uma hash de IDs. 
				//Se estiver, mostra mensagem de erro, senao, cria uma nova UID, insere na hash e continua o fluxo.
				//System.out.println("form uid:" + request.getParameter("uid"));
				//String uid = SessionUtil.getUID(request.getSession().getId());
				//request.setAttribute("uid", uid);
				
				
				SessionUtil.addEvent(uto, request.getPathInfo());
				super.process(request, response);
				
			} else {
				LogUtil.log(this, LogUtil.LOG_WARN, "(PLANdora.GeneralStruts): Invalid attempt to access action: [" + 
							request.getPathInfo() + "] [" +	(uto!=null?uto.getId()+"]":"null]"));
				response.sendRedirect("../do/login?operation=prepareLogin");
			}
		} catch (Exception e) {
		    LogUtil.log(this, LogUtil.LOG_ERROR, "(PLANdora.GeneralStruts): error" , e);
		    e.printStackTrace();
		}
	}

	
	/**
	 * Check if current user has a role appropriate for the form requested.
	 */
	private boolean checkGrants(UserTO uto, HttpServletRequest request) throws BusinessException{
		boolean response = false;

		try {
		    String frmPath = request.getPathInfo().toLowerCase();
		    if (frmPath.equalsIgnoreCase("/login") || frmPath.equalsIgnoreCase("/connectorlistener") || 
		    		frmPath.equalsIgnoreCase("/calendarSync") || frmPath.equalsIgnoreCase("/flashfreemind.css") || 
		    		frmPath.equalsIgnoreCase("/csshover.htc") || frmPath.equalsIgnoreCase("/expressInstall") || frmPath.equalsIgnoreCase("/FlexPaperViewer") ||
		    		(frmPath.equalsIgnoreCase("/showSurvey") && (request.getParameter("operation").equals("anonymous") || request.getParameter("operation").equals("anonyanswer")))){
		        response = true;
		    } else {
			    if (uto!=null){
				    if (uto instanceof RootTO) {
				        response = ( frmPath.equalsIgnoreCase("/manageuser") || frmPath.equalsIgnoreCase("/manageproject") || 
				                	 frmPath.equalsIgnoreCase("/managedb")  || frmPath.equalsIgnoreCase("/managearea") || 
				                	 frmPath.equalsIgnoreCase("/depart") || frmPath.equalsIgnoreCase("/category") || 
				                	 frmPath.equalsIgnoreCase("/managemetafield") || frmPath.equalsIgnoreCase("/managecategory") ||
				                	 frmPath.equalsIgnoreCase("/managereport") || frmPath.equalsIgnoreCase("/managenotification") ||  
				                	 frmPath.equalsIgnoreCase("/manageoption") || frmPath.equalsIgnoreCase("/manageoccurrence") ||
				                	 frmPath.equalsIgnoreCase("/managehistoccurrence") || frmPath.equalsIgnoreCase("/viewkb") ||
				                	 frmPath.equalsIgnoreCase("/managerisk") || frmPath.equalsIgnoreCase("/managehistrisk") ||
				                	 frmPath.equalsIgnoreCase("/manageattachment") || frmPath.equalsIgnoreCase("/downloadAttachment") ||
				                	 frmPath.equalsIgnoreCase("/manageSurvey") || frmPath.equalsIgnoreCase("/manageCustomForm") ||
				                	 frmPath.equalsIgnoreCase("/viewMindMap") || frmPath.equalsIgnoreCase("/visorFreemind")|| frmPath.equalsIgnoreCase("/open-flash-chart") ||
				                	 frmPath.equalsIgnoreCase("/manageMetaForm") || frmPath.equalsIgnoreCase("/showSurvey") ||
				                	 frmPath.equalsIgnoreCase("/showGadgetProperty") || 
				                	 frmPath.equalsIgnoreCase("/showRepositoryViewer") || frmPath.equalsIgnoreCase("/manageTemplate") ||
				                	 frmPath.equalsIgnoreCase("/showAgilePanel") || frmPath.equalsIgnoreCase("/showAgilePanelReq") ||
				                	 frmPath.equalsIgnoreCase("/showAgilePanelTask") || frmPath.equalsIgnoreCase("/showRepositoryViewerCustomer") ||
				                	 frmPath.equalsIgnoreCase("/manageInvoice") || frmPath.equalsIgnoreCase("/showSurveyQuestion") ||
				                	 frmPath.startsWith("/repositoryfileviewer") || frmPath.equalsIgnoreCase("/showInvoiceItem") ||
				                	 frmPath.equalsIgnoreCase("/manageHistInvoice") || frmPath.equalsIgnoreCase("/manageGadget") ||
				                	 frmPath.equalsIgnoreCase("/showNewArtifact") || frmPath.equalsIgnoreCase("/manageArtifact") ||
				                	 frmPath.equalsIgnoreCase("/showBrowseArtifact") || frmPath.equalsIgnoreCase("/showResCapacityPanel") ||
				                	 frmPath.equalsIgnoreCase("/showResCapacityEdit") || frmPath.equalsIgnoreCase("/showCostPanel") ||
				                	 frmPath.equalsIgnoreCase("/showCostEdit") || frmPath.equalsIgnoreCase("/manageExpense") ||
				                	 frmPath.equalsIgnoreCase("/repositoryUpload") || frmPath.equalsIgnoreCase("/showSnipArtifact"));
				        
				    } else if (uto instanceof LeaderTO) { 
				        response = ( frmPath.equalsIgnoreCase("/managehistrequest") || frmPath.equalsIgnoreCase("/manageproject") || 
				                	 frmPath.equalsIgnoreCase("/manageresourcehome")  || frmPath.equalsIgnoreCase("/refuse")  || 
				                	 frmPath.equalsIgnoreCase("/showallrequirement")  || frmPath.equalsIgnoreCase("/managetask")  || 
				                	 frmPath.equalsIgnoreCase("/searchPlanning")  || frmPath.equalsIgnoreCase("/ganttviewer") || 
				                	 frmPath.equalsIgnoreCase("/managehisttask") || frmPath.equalsIgnoreCase("/managerestask") || 
				                	 frmPath.equalsIgnoreCase("/managecustrequest") || frmPath.equalsIgnoreCase("/viewreport") || 
				                	 frmPath.equalsIgnoreCase("/manageoption") || frmPath.equalsIgnoreCase("/showalltask") ||
				                	 frmPath.equalsIgnoreCase("/projectimportexport") || frmPath.equalsIgnoreCase("/viewbsc") || frmPath.equalsIgnoreCase("/viewBSCPanel") ||
				                	 frmPath.equalsIgnoreCase("/manageoccurrence") || frmPath.equalsIgnoreCase("/managehistoccurrence") ||
				                	 frmPath.equalsIgnoreCase("/viewkb") || frmPath.equalsIgnoreCase("/managerisk") || 
				                	 frmPath.equalsIgnoreCase("/managehistrisk")  || frmPath.equalsIgnoreCase("/manageattachment") ||
				                	 frmPath.equalsIgnoreCase("/manageHistAttach") || frmPath.equalsIgnoreCase("/downloadAttachment") || 
				                	 frmPath.equalsIgnoreCase("/manageCustomForm") || frmPath.equalsIgnoreCase("/viewMindMap") || 
				                	 frmPath.equalsIgnoreCase("/visorFreemind") || frmPath.equalsIgnoreCase("/applyTaskTemplate") || frmPath.equalsIgnoreCase("/open-flash-chart") ||
				                	 frmPath.equalsIgnoreCase("/manageDiscussion") || frmPath.equalsIgnoreCase("/manageSurvey") ||
				                	 frmPath.equalsIgnoreCase("/showSurvey") || frmPath.equalsIgnoreCase("/showGadgetProperty") || 
				                	 frmPath.equalsIgnoreCase("/showRepositoryViewer") ||
				                	 frmPath.equalsIgnoreCase("/showAgilePanel") || frmPath.equalsIgnoreCase("/showAgilePanelReq") ||
				                	 frmPath.equalsIgnoreCase("/showAgilePanelTask") || frmPath.equalsIgnoreCase("/showRepositoryViewerCustomer") ||
				                	 frmPath.equalsIgnoreCase("/manageInvoice") || frmPath.equalsIgnoreCase("/showSurveyQuestion") ||
				                	 frmPath.startsWith("/repositoryfileviewer") || frmPath.equalsIgnoreCase("/showInvoiceItem") ||
				                	 frmPath.equalsIgnoreCase("/manageHistInvoice") || frmPath.equalsIgnoreCase("/manageInvoice") ||
				                	 frmPath.equalsIgnoreCase("/manageGadget") || frmPath.equalsIgnoreCase("/showNewArtifact") || 
				                	 frmPath.equalsIgnoreCase("/manageArtifact") || frmPath.equalsIgnoreCase("/showBrowseArtifact") ||
				                	 frmPath.equalsIgnoreCase("/showResCapacityPanel") || frmPath.equalsIgnoreCase("/showResCapacityEdit") ||
				                	 frmPath.equalsIgnoreCase("/showCostPanel") || frmPath.equalsIgnoreCase("/showCostEdit") ||
				                	 frmPath.equalsIgnoreCase("/manageExpense") || frmPath.equalsIgnoreCase("/repositoryUpload") ||
				                	 frmPath.equalsIgnoreCase("/showSnipArtifact"));
				    } else if (uto instanceof ResourceTO) { 
				        response = ( frmPath.equalsIgnoreCase("/managehistrequest") || frmPath.equalsIgnoreCase("/manageresourcehome") || 
				                	 frmPath.equalsIgnoreCase("/ganttviewer") || frmPath.equalsIgnoreCase("/managerestask") || 
				                	 frmPath.equalsIgnoreCase("/viewreport") || frmPath.equalsIgnoreCase("/searchPlanning") ||
				                	 frmPath.equalsIgnoreCase("/managehisttask") || frmPath.equalsIgnoreCase("/managecustrequest") || 
				                	 frmPath.equalsIgnoreCase("/manageoption") || frmPath.equalsIgnoreCase("/projectimportexport") ||
				                	 frmPath.equalsIgnoreCase("/viewkb") || frmPath.equalsIgnoreCase("/manageattachment") ||
				                	 frmPath.equalsIgnoreCase("/downloadAttachment")|| frmPath.equalsIgnoreCase("/manageCustomForm") ||
				                	 frmPath.equalsIgnoreCase("/viewMindMap") || frmPath.equalsIgnoreCase("/visorFreemind") || frmPath.equalsIgnoreCase("/open-flash-chart") ||
				                	 frmPath.equalsIgnoreCase("/applyTaskTemplate") || frmPath.equalsIgnoreCase("/manageDiscussion") ||
				                	 frmPath.equalsIgnoreCase("/showSurvey") || frmPath.equalsIgnoreCase("/showGadgetProperty") ||
				                	 frmPath.equalsIgnoreCase("/showRepositoryViewer") || frmPath.equalsIgnoreCase("/showAgilePanel") ||
				                	 frmPath.equalsIgnoreCase("/showAgilePanelReq") || frmPath.equalsIgnoreCase("/showAgilePanelTask") ||
				                	 frmPath.equalsIgnoreCase("/showRepositoryViewerCustomer") || frmPath.equalsIgnoreCase("/showalltask") ||
				                	 frmPath.startsWith("/repositoryfileviewer") || frmPath.equalsIgnoreCase("/showInvoiceItem") ||
				                	 frmPath.equalsIgnoreCase("/manageHistInvoice") || frmPath.equalsIgnoreCase("/manageInvoice") ||
				                	 frmPath.equalsIgnoreCase("/manageGadget") || frmPath.equalsIgnoreCase("/showNewArtifact") ||
				                	 frmPath.equalsIgnoreCase("/manageArtifact")|| frmPath.equalsIgnoreCase("/showBrowseArtifact") ||
				                	 frmPath.equalsIgnoreCase("/showCostPanel") || frmPath.equalsIgnoreCase("/showCostEdit") ||
				                	 frmPath.equalsIgnoreCase("/manageExpense") || frmPath.equalsIgnoreCase("/repositoryUpload") || 
				                	 frmPath.equalsIgnoreCase("/showSnipArtifact")
				                	 );
				    } else if (uto instanceof CustomerTO) { 
				        response = ( frmPath.equalsIgnoreCase("/managecustrequest") || frmPath.equalsIgnoreCase("/manageattachment") || 
				                	 frmPath.equalsIgnoreCase("/managehistrequest") || frmPath.equalsIgnoreCase("/manageoption") ||
				                	 frmPath.equalsIgnoreCase("/downloadAttachment")|| frmPath.equalsIgnoreCase("/manageCustomForm") || 
				                	 frmPath.equalsIgnoreCase("/viewMindMap") || frmPath.equalsIgnoreCase("/visorFreemind") || 
				                	 frmPath.equalsIgnoreCase("/searchPlanning") || frmPath.equalsIgnoreCase("/viewreport") ||
				                	 frmPath.equalsIgnoreCase("/manageDiscussion") || frmPath.equalsIgnoreCase("/showSurvey") ||
				                	 frmPath.equalsIgnoreCase("/showRepositoryViewer") || frmPath.equalsIgnoreCase("/showRepositoryViewerCustomer") ||
				                	 frmPath.startsWith("/repositoryfileviewer"));
				    }

			    }
		    }			
		} catch (Exception e) {
		    System.out.println(e.getCause());
		}
	    
	    return response;
	}
}
