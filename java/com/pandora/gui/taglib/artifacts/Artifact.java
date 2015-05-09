package com.pandora.gui.taglib.artifacts;

import java.util.Iterator;
import java.util.Vector;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.struts.Globals;
import org.apache.struts.util.RequestUtils;
import org.apache.taglibs.display.LookupUtil;

import com.pandora.ProjectTO;
import com.pandora.RepositoryFilePlanningTO;
import com.pandora.RepositoryFileTO;
import com.pandora.ResourceTO;
import com.pandora.UserTO;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.gui.taglib.decorator.ArtifactEditDecorator;
import com.pandora.gui.taglib.decorator.RepositoryEntryLogDecorator;
import com.pandora.gui.taglib.decorator.RepositoryEntryNameDecorator;
import com.pandora.gui.taglib.decorator.RepositoryEntryTypeDecorator;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.LogUtil;

public class Artifact extends TagSupport {

	private static final long serialVersionUID = 1L;

    /** Related form name */
    private String name;
	
    /** The id of data collection from http session. This collection 
     * contain all repository item objects (artifacts) that should be displayed by tag lib. */
    private String collection;

    /** The name of attribute or method that contain the id of the source entity */
    private String property;

    /** The name of attribute or method that contain the id of the source entity project */
    private String projectProperty;

    /** The name of attribute or method that contain the id of the source entity project */
    private String multiple = "on";

    /** The name of attribute or method that contain the id of the source entity project */
    private String onlyFolders = "off";

    
    public int doStartTag() {
        StringBuffer buff = new StringBuffer("&nbsp;");
    	//NoteIcon note = new NoteIcon();
    	RepositoryEntryTypeDecorator imgDec = new RepositoryEntryTypeDecorator();
    	RepositoryEntryNameDecorator nameDec = new RepositoryEntryNameDecorator();
    	ArtifactEditDecorator editDec = new ArtifactEditDecorator();
		RepositoryEntryLogDecorator logDec = new RepositoryEntryLogDecorator(); 
    	UserDelegate udel = new UserDelegate();
    	ProjectDelegate pdel = new ProjectDelegate();
        try {
            JspWriter out = pageContext.getOut();

            //get all artifacts from http session
            Vector<RepositoryFilePlanningTO> list = (Vector<RepositoryFilePlanningTO>)pageContext.getSession().getAttribute(this.getCollection());
            Object entityId = LookupUtil.lookup(pageContext, this.getName(), this.getProperty(), null, false, null, null, null);
            Object projectId = LookupUtil.lookup(pageContext, this.getName(), this.getProjectProperty(), null, false, null, null, null);

            buff = new StringBuffer();
            if (entityId!=null && entityId instanceof String && 
            		projectId!=null && projectId instanceof String) {

            	ProjectTO pto = pdel.getProjectObject(new ProjectTO((String)projectId), true);            	
            	UserTO uto = (UserTO)pageContext.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);
            	ResourceTO rto = new ResourceTO(uto.getId());
            	rto.setProject(pto);
            	rto = udel.getResource(rto);
            	
            	String title = bundle("label.artifactTag.title");
            	buff.append("<tr class=\"formBody\">\n");            
               	buff.append("   <td><img src=\"../images/artifact.png\" " + HtmlUtil.getHint(title) + " border=\"0\" >&nbsp;&nbsp;<b>" + title + "</b></td>\n");
               	buff.append("</tr>\n");
                                
                buff.append("<tr class=\"formBody\">\n");
                buff.append("<td>\n");
                buff.append("<table class=\"table\" width=\"100%\" border=\"1\" bordercolor=\"#10389C\" cellspacing=\"1\" cellpadding=\"2\">\n");

                buff.append("  <tr class=\"rowHighlight\">\n");
                buff.append("       <td colspan=\"4\">");
                buff.append("       <table class=\"table\" width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
                buff.append("       <tr class=\"rowHighlight\"><td>");

                if (rto.getBoolCanSeeRepository() && pto.getRepositoryClass()!=null && !pto.getRepositoryClass().equals("-1")) {
                	String newArtifact = bundle("label.artifactTag.new");
                	buff.append("   <input type=\"button\" name=\"newButton\" " +
                					    "onclick=\"javascript:displayMessage('../do/showNewArtifact?operation=prepareForm&projectId=" + projectId + "&id=" + entityId + "', 600, 400);\" " +
                					    "value=\"" + newArtifact + "\" class=\"button\">\n");                	
                } else {
                	buff.append("&nbsp;");
                }

                buff.append("       </td><td>&nbsp;</td>");
                buff.append("       <td width=\"60\">");
                if (rto.getBoolCanSeeRepository() && pto.getRepositoryClass()!=null && !pto.getRepositoryClass().equals("-1")) {
                	String browse = bundle("label.artifactTag.browse");
                	buff.append("   <input type=\"button\" name=\"browseButton\" " +
                					    "onclick=\"javascript:displayMessage('../do/showRepositoryViewer?operation=browse&path=&projectId=" + projectId + 
                					    "&onlyFolders=" + this.onlyFolders + "&multiple=" + this.multiple + "', 600, 400);\" " +
                					    "value=\"" + browse + "\" class=\"button\">\n");                	
                } else {
                	buff.append("&nbsp;");
                }
                buff.append("       </td></tr>");                    
                buff.append("       </table></td>");
                buff.append("  </tr>");
                buff.append("</table>");
                
                if (list!=null && list.size()>0) {
                	
                	if (list.size()>5) {
                        buff.append("<div id=\"ARTIFACT_LIST_" + entityId + "\" style=\"width:100%; height:107px; overflow-y: scroll; overflow-x: hidden;\">");
                	}
                	
                    buff.append("<table class=\"table\" width=\"100%\" border=\"1\" bordercolor=\"#10389C\" cellspacing=\"1\" cellpadding=\"2\">\n");
                    Iterator<RepositoryFilePlanningTO> i = list.iterator();
                    while(i.hasNext()) {
                    	RepositoryFilePlanningTO rfpto = (RepositoryFilePlanningTO)i.next();
                    	RepositoryFileTO rfto = rfpto.getFile();
                    	rfto.setName(rfto.getPath());
                    	rfto.setPlanning(pto);

                       	imgDec.init(pageContext, null);
                       	imgDec.initRow(rfto, 0, 0);
                       	logDec.init(pageContext, null);
                       	logDec.initRow(rfto, 0, 0);
                       	editDec.init(pageContext, null);
                       	editDec.initRow(rfto, 0, 0);
                       	
                       	String img = imgDec.decorate(new Boolean(false));
                       	String path = nameDec.formatPath(rfto.getPath(), pto.getRepositoryURL());
                       	String log = logDec.decorate(null);
                    	
                    	buff.append("<tr class=\"tableRowEven\">");
                        buff.append("     <td class=\"tableCell\" width=\"5%\" align=\"center\" valign=\"middle\">" + img + "</td>\n");                       
                        buff.append("     <td class=\"tableCell\" align=\"left\" valign=\"top\">" + path + "</td>\n");                        
                    	buff.append("     <td class=\"tableCell\" width=\"5%\" align=\"center\" valign=\"top\">" + log + "</td>\n");                        
                        if (rto.getBoolCanSeeRepository()) {
                           	String editImage = editDec.decorate(rfpto.getEntity().getId());
                        	buff.append("     <td class=\"tableCell\" width=\"5%\" align=\"center\" valign=\"top\"><a href=\"javascript:breakRepositoryEntityLink('" + bundle("label.artifactTag.removelink.confirm") + "', '" + rfpto.getId() + "', '" + rfpto.getEntity().getId() + "');\"><img src=\"../images/linkbreak.png\" border=\"0\"/></a></td>\n");
                        	buff.append("     <td class=\"tableCell\" width=\"5%\" align=\"center\" valign=\"top\">" + editImage + "</td>\n");                        	
                        }
                        buff.append("</tr>");
                    }
                    buff.append("</table>");
                    
                    if (list.size()>5) {
                    	buff.append("</div>");
                    }
                }
                buff.append("</td></tr>");
                
                out.println(buff.toString());                     	
            }
            
        } catch (Exception e) {
            LogUtil.log(this, LogUtil.LOG_ERROR, "Artifacts tag lib error", e);
        }
            
        return SKIP_BODY;
    }    
	
    /*
    private String getFileNote(RepositoryFilePlanningTO rfpto) {
    	StringBuffer response = new StringBuffer("");
    	if (rfpto!=null) {
    		RepositoryFileTO rfto = rfpto.getFile();
    		if (rfto!=null) {
        		response.append("<b>" + bundle("label.formRepository.author") + "</b>: " + rfto.getAuthor() + "<br>");
        		response.append("<b>" + bundle("label.formRepository.size") + "</b>: " + rfto.getFileSize() + "<br>");    			
    		}
    	}
		return response.toString();
	}
	*/


	///////////////////////////////////////////////             
    public String getName() {
        return name;
    }
    public void setName(String newValue) {
        this.name = newValue;
    }

    
    ///////////////////////////////////////////////
    public String getCollection() {
        return collection;
    }
    public void setCollection(String newValue) {
        this.collection = newValue;
    } 

    
    /////////////////////////////////////////////// 
	public String getProperty() {
		return property;
	}
	public void setProperty(String newValue) {
		this.property = newValue;
	}
	
	
    /////////////////////////////////////////////// 
	public String getProjectProperty() {
		return projectProperty;
	}
	public void setProjectProperty(String newValue) {
		this.projectProperty = newValue;
	}	
	
	
	
    /////////////////////////////////////////////// 
	public String getMultiple() {
		return multiple;
	}
	public void setMultiple(String newValue) {
		this.multiple = newValue;
	}


    /////////////////////////////////////////////// 
	public String getOnlyFolders() {
		return onlyFolders;
	}
	public void setOnlyFolders(String newValue) {
		this.onlyFolders = newValue;
	}


	///////////////////////////////////////////////    
    private String bundle(String key){
    	String response = "";
    	try {
    		response = RequestUtils.message(pageContext, null, Globals.LOCALE_KEY, key, null);
		} catch (JspException e) {
			response = "";
			e.printStackTrace();
		}
		return response;
    }
}
