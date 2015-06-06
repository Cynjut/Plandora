package com.pandora.gui.struts.form;

import java.util.Iterator;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionMapping;

import com.pandora.LeaderTO;
import com.pandora.ResourceTO;
import com.pandora.RootTO;
import com.pandora.TaskStatusTO;
import com.pandora.UserTO;
import com.pandora.helper.DateUtil;

/**
 * This class handle the data of Option Form
 */
public class OptionForm extends GeneralStrutsForm {


	private static final long serialVersionUID = 1L;
	
    /** Current user related with option form */
    private UserTO user;
    
    /** Line Number of Task List */
    private String taskNumLine;
    
    /** Line Number of Pending Requirement List */
    private String pendNumLine;
    
    /** Line Number of Requirement List */
    private String requNumLine;
    
    /** Line Number of Project List */
    private String projNumLine;

    private String infoNumLine;
    
    private String topicNumLine;
    
    private boolean showPriorityColor;
    
    /** Maximum Number of words used by grids */
    private String maxNumOfWords;
    
    /** Show/hide ShowRequirement List in 'paging mode' */
    private boolean pagingTOListAll;

    /** Show/hide ShowRequirement List in 'paging mode' */
    private boolean showLockedTasks;
    
    /** The list of task Status objects with the value selected on form*/
    private Vector<TaskStatusTO> taskStatusOrderingList;
    
    /** Maximum Number of 'days ago' used to display into 'My Task' list */
    private String myTaskMaxDaysAgo;

    /** Maximum Number of 'days ago' used to display into 'My Requirement' list */
    private String myReqMaxDaysAgo;
    
    
    /** The new password typed by user */
    private String newPassword;
    
    /** The confirmation of new password typed by user */
    private String newPasswordConfirm;
    
    
    /** The days of task delay to be considered a warning */
    private String warningTaskDays;

    /** The days of task delay to be considered critical */
    private String criticalTaskDays;

    /** List of notification channels */
    private String notifiChannels;

    /** List of notification channels */
    private String occurrenceSources;
    
    /** List of KB classes */    
    private String kbClasses;

    /** List of KB classes */    
    private String kbProgressBar;

    /** The path on file system where the index files must be placed */    
    private String kbIndexFolder;

    /** List of Gadget classes */    
    private String gadgetClasses;

    /** List of Repository classes */    
    private String repositoryClasses;

    /** List of Authentication classes */
    private String authenticationClasses;

    /** List of Calendar Synchronization classes */
    private String calendarSyncClasses;

    /** List of Attachment Converter classes */
    private String converterClasses;
    
    private String overviewProjectClasses;
    
    private String gadgetWidth;
    
    private String ldapHost;
    
    private String ldapPort;
    
    private String ldapUIDRegister;
        
    private String uploadMaxFile;
    
    private String authenticationMode;
    
    private String hideProject;
    
    private String impExpClasses;
    
    private String taskInputFormat;
    
    private String userActionLog;
    
    private String userFullActionLog;
    
    private String newVersionUrl;
    
    private String taskReportUrl;
    
    private String surveyReportUrl;
    
    private String expenseReportUrl;
    
    
    private String shortcutName1;
    private String shortcutURL1;
    private String shortcutIcon1;
    private String shortcutOpen1;

    private String shortcutName2;
    private String shortcutURL2;
    private String shortcutIcon2;
    private String shortcutOpen2;
    
    private String shortcutName3;
    private String shortcutURL3;
    private String shortcutIcon3;
    private String shortcutOpen3;
    
    private String shortcutIcon4;
    private String shortcutName4;
    private String shortcutURL4;
    private String shortcutOpen4;
    
    private String shortcutIcon5;
    private String shortcutName5;
    private String shortcutURL5;
    private String shortcutOpen5;
    
    private String shortcutIcon6;
    private String shortcutName6;
    private String shortcutURL6;
    private String shortcutOpen6;
    
    private String shortcutIcon7;
    private String shortcutName7;
    private String shortcutURL7;
    private String shortcutOpen7;
    
    private String shortcutIcon8;
    private String shortcutName8;
    private String shortcutURL8;
    private String shortcutOpen8;
    
    private String shortcutIcon9;
    private String shortcutName9;
    private String shortcutURL9;
    private String shortcutOpen9;
    
    private String shortcutIcon10;
    private String shortcutName10;
    private String shortcutURL10;
    private String shortcutOpen10;
    
    
    private String currencyLocale;
    
    private String defaultCapacity;
    
    private String artifactMaxFile;
    
    private String artifactExpClasses;
    
    private String snipArtifactClasses;
    
    private String picturePath;
    
    private String maxMetaFieldTimeout;
    
    /**
     * Verify if current user is able to view the options of resource role
     * @return
     */
    public String getCheckResourceRole() {
        String response = "off";
        if (user instanceof ResourceTO || user instanceof LeaderTO) {
            response = "on";
        }
        return response;
    }

    /**
     * Verify if current user is able to view the options of leader role
     * @return
     */
    public String getCheckLeaderRole() {
        String response = "off";
        if (user instanceof LeaderTO) {
            response = "on";
        }
        return response;
    }

    
    /**
     * Verify if current user is able to view the options of root
     * @return
     */
    public String getCheckRootRole() {
        String response = "off";
        if (user instanceof RootTO) {
            response = "on";
        }
        return response;
    }    
    
    ///////////////////////////////////////
    public String getPendNumLine() {
        return pendNumLine;
    }
    public void setPendNumLine(String newValue) {
        this.pendNumLine = newValue;
    }
    
    ///////////////////////////////////////    
    public String getProjNumLine() {
        return projNumLine;
    }
    public void setProjNumLine(String newValue) {
        this.projNumLine = newValue;
    }
    
    
    /////////////////////////////////////// 
	public String getTopicNumLine() {
		return topicNumLine;
	}
	public void setTopicNumLine(String newValue) {
		this.topicNumLine = newValue;
	}
    
    
    ///////////////////////////////////////    
    public String getRequNumLine() {
        return requNumLine;
    }
    public void setRequNumLine(String newValue) {
        this.requNumLine = newValue;
    }
    
    ///////////////////////////////////////    
    public String getTaskNumLine() {
        return taskNumLine;
    }
    public void setTaskNumLine(String newValue) {
        this.taskNumLine = newValue;
    }

    
    ///////////////////////////////////////    
	public String getInfoNumLine() {
		return infoNumLine;
	}
	public void setInfoNumLine(String newValue) {
		this.infoNumLine = newValue;
	}

    
    ///////////////////////////////////////    
    public String getMaxNumOfWords() {
        return maxNumOfWords;
    }
    public void setMaxNumOfWords(String newValue) {
        this.maxNumOfWords = newValue;
    }
    
    ///////////////////////////////////////    
    public boolean getPagingTOListAll() {
        return pagingTOListAll;
    }
    public void setPagingTOListAll(boolean newValue) {
        this.pagingTOListAll = newValue;
    }
    
    
    ///////////////////////////////////////        
    public boolean getShowLockedTasks() {
		return showLockedTasks;
	}
	public void setShowLockedTasks(boolean newValue) {
		this.showLockedTasks = newValue;
	}

	
	///////////////////////////////////////        
    public boolean getShowPriorityColor() {
		return showPriorityColor;
	}
	public void setShowPriorityColor(boolean newValue) {
		this.showPriorityColor = newValue;
	}

	///////////////////////////////////////       
    public Vector<TaskStatusTO> getTaskStatusOrderingList() {
        return taskStatusOrderingList;
    }
    public void setTaskStatusOrderingList(Vector<TaskStatusTO> newValue) {
        this.taskStatusOrderingList = newValue;
    }
    
    ///////////////////////////////////////    
    public void setUser(UserTO newValue) {
        this.user = newValue;
    }

	public String getUserPictureHtml(){
		if (this.user!=null) {
			return "<img width=\"80\" height=\"115\" border=\"0\" src=\"../do/login?operation=getUserPic&id=" +  this.user.getId() + "&ts=" +DateUtil.getNow().toString() + "\" />";	
		} else {
			return "";
		}	  	    
	}    
    
    ///////////////////////////////////////      
    public String getMyReqMaxDaysAgo() {
        return myReqMaxDaysAgo;
    }
    public void setMyReqMaxDaysAgo(String newValue) {
        this.myReqMaxDaysAgo = newValue;
    }
    
    ///////////////////////////////////////      
    public String getMyTaskMaxDaysAgo() {
        return myTaskMaxDaysAgo;
    }
    public void setMyTaskMaxDaysAgo(String newValue) {
        this.myTaskMaxDaysAgo = newValue;
    }

    ///////////////////////////////////////    
    public String getNewPassword() {
        return newPassword;
    }
    public void setNewPassword(String newValue) {
        this.newPassword = newValue;
    }
    
    ///////////////////////////////////////    
    public String getNewPasswordConfirm() {
        return newPasswordConfirm;
    }
    public void setNewPasswordConfirm(String newValue) {
        this.newPasswordConfirm = newValue;
    }
    
    
    ///////////////////////////////////////    
    public String getCriticalTaskDays() {
        return criticalTaskDays;
    }
    public void setCriticalTaskDays(String newValue) {
        this.criticalTaskDays = newValue;
    }
    
    ///////////////////////////////////////    
    public String getWarningTaskDays() {
        return warningTaskDays;
    }
    public void setWarningTaskDays(String newValue) {
        this.warningTaskDays = newValue;
    }
    
    ///////////////////////////////////////        
    public String getTaskInputFormat() {
		return taskInputFormat;
	}
	public void setTaskInputFormat(String newValue) {
		this.taskInputFormat = newValue;
	}

	///////////////////////////////////////    
    public String getNotifiChannels() {
        return notifiChannels;
    }
    public void setNotifiChannels(String newValue) {
        this.notifiChannels = newValue;
    }

    
	///////////////////////////////////////        
    public String getCalendarSyncClasses() {
		return calendarSyncClasses;
	}
	public void setCalendarSyncClasses(String newValue) {
		this.calendarSyncClasses = newValue;
	}


	/////////////////////////////////////// 
	public String getSnipArtifactClasses() {
		return snipArtifactClasses;
	}
	public void setSnipArtifactClasses(String newValue) {
		this.snipArtifactClasses = newValue;
	}
	

	///////////////////////////////////////        	
	public String getConverterClasses() {
		return converterClasses;
	}
	public void setConverterClasses(String newValue) {
		this.converterClasses = newValue;
	}
	
	
	///////////////////////////////////////        
	public String getOverviewProjectClasses() {
		return overviewProjectClasses;
	}
	public void setOverviewProjectClasses(String newValue) {
		this.overviewProjectClasses = newValue;
	}

	
	///////////////////////////////////////    
    public String getKbClasses() {
        return kbClasses;
    }
    public void setKbClasses(String newValue) {
        this.kbClasses = newValue;
    }
    

    ///////////////////////////////////////  
    public String getGadgetClasses() {
		return gadgetClasses;
	}
	public void setGadgetClasses(String newValue) {
		this.gadgetClasses = newValue;
	}


    ///////////////////////////////////////
    public String getRepositoryClasses() {
		return repositoryClasses;
	}
	public void setRepositoryClasses(String newValue) {
		this.repositoryClasses = newValue;
	}
	

	///////////////////////////////////////
	public String getGadgetWidth() {
		return gadgetWidth;
	}
	public void setGadgetWidth(String gadgetWidth) {
		this.gadgetWidth = gadgetWidth;
	}

	
	///////////////////////////////////////        
    public String getKbProgressBar() {
        return kbProgressBar;
    }
    public void setKbProgressBar(String newValue) {
        this.kbProgressBar = newValue;
    }

    
    ///////////////////////////////////////    
    public String getKbIndexFolder() {
        return kbIndexFolder;
    }
    public void setKbIndexFolder(String newValue) {
        this.kbIndexFolder = newValue;
    }

    
    ///////////////////////////////////////    
    public String getOccurrenceSources() {
        return occurrenceSources;
    }
    public void setOccurrenceSources(String newValue) {
        this.occurrenceSources = newValue;
    }
    
    
    ///////////////////////////////////////      
    public String getUploadMaxFile() {
        return uploadMaxFile;
    }
    public void setUploadMaxFile(String newValue) {
        this.uploadMaxFile = newValue;
    }
    
    
    ///////////////////////////////////////    
    public String getLdapHost() {
        return ldapHost;
    }
    public void setLdapHost(String newValue) {
        this.ldapHost = newValue;
    }
    
    
    ///////////////////////////////////////    
    public String getLdapPort() {
        return ldapPort;
    }
    public void setLdapPort(String newValue) {
        this.ldapPort = newValue;
    }
    
    
    ///////////////////////////////////////    
    public String getLdapUIDRegister() {
        return ldapUIDRegister;
    }
    public void setLdapUIDRegister(String newValue) {
        this.ldapUIDRegister = newValue;
    }

    
    ///////////////////////////////////////     
	public String getAuthenticationMode() {
		return authenticationMode;
	}
	public void setAuthenticationMode(String newValue) {
		this.authenticationMode = newValue;
	}
    
    
    ///////////////////////////////////////     	
	public String getHideProject() {
		return hideProject;
	}
	public void setHideProject(String newValue) {
		this.hideProject = newValue;
	}


	///////////////////////////////////////     	
	public String getImpExpClasses() {
		return impExpClasses;
	}
	public void setImpExpClasses(String newValue) {
		this.impExpClasses = newValue;
	}
	
	
	///////////////////////////////////////        
	public String getAuthenticationClasses() {
		return authenticationClasses;
	}
	public void setAuthenticationClasses(String newValue) {
		this.authenticationClasses = newValue;
	}
	

	///////////////////////////////////////  
	public String getUserActionLog() {
		return userActionLog;
	}
	public void setUserActionLog(String newValue) {
		this.userActionLog = newValue;
	}


	///////////////////////////////////////  
	public String getUserFullActionLog() {
		return userFullActionLog;
	}
	public void setUserFullActionLog(String newValue) {
		this.userFullActionLog = newValue;
	}
	
	
	
	///////////////////////////////////////  
	public String getNewVersionUrl() {
		return newVersionUrl;
	}
	public void setNewVersionUrl(String newValue) {
		this.newVersionUrl = newValue;
	}

	
	///////////////////////////////////////  	
	public String getTaskReportUrl() {
		return taskReportUrl;
	}
	public void setTaskReportUrl(String newValue) {
		this.taskReportUrl = newValue;
	}

	
	///////////////////////////////////////
	public String getSurveyReportUrl() {
		return surveyReportUrl;
	}
	public void setSurveyReportUrl(String newValue) {
		this.surveyReportUrl = newValue;
	}
	
	
	///////////////////////////////////////	
	public String getExpenseReportUrl() {
		return expenseReportUrl;
	}
	public void setExpenseReportUrl(String newValue) {
		this.expenseReportUrl = newValue;
	}


	///////////////////////////////////////	
	public String getShortcutName1() {
		return shortcutName1;
	}
	public void setShortcutName1(String newValue) {
		this.shortcutName1 = newValue;
	}

	
	///////////////////////////////////////	
	public String getShortcutURL1() {
		return shortcutURL1;
	}
	public void setShortcutURL1(String newValue) {
		this.shortcutURL1 = newValue;
	}

	
	///////////////////////////////////////	
	public String getShortcutIcon1() {
		return shortcutIcon1;
	}
	public void setShortcutIcon1(String newValue) {
		this.shortcutIcon1 = newValue;
	}

	
	///////////////////////////////////////	
	public String getShortcutOpen1() {
		return shortcutOpen1;
	}
	public void setShortcutOpen1(String newValue) {
		this.shortcutOpen1 = newValue;
	}
	
	///////////////////////////////////////	
	public String getShortcutName2() {
		return shortcutName2;
	}
	public void setShortcutName2(String newValue) {
		this.shortcutName2 = newValue;
	}

	
	///////////////////////////////////////	
	public String getShortcutURL2() {
		return shortcutURL2;
	}
	public void setShortcutURL2(String newValue) {
		this.shortcutURL2 = newValue;
	}

	
	///////////////////////////////////////	
	public String getShortcutIcon2() {
		return shortcutIcon2;
	}
	public void setShortcutIcon2(String newValue) {
		this.shortcutIcon2 = newValue;
	}

	///////////////////////////////////////	
	public String getShortcutOpen2() {
		return shortcutOpen2;
	}
	public void setShortcutOpen2(String newValue) {
		this.shortcutOpen2 = newValue;
	}	
	
	
	///////////////////////////////////////	
	public String getShortcutName3() {
		return shortcutName3;
	}
	public void setShortcutName3(String newValue) {
		this.shortcutName3 = newValue;
	}

	
	///////////////////////////////////////	
	public String getShortcutURL3() {
		return shortcutURL3;
	}
	public void setShortcutURL3(String newValue) {
		this.shortcutURL3 = newValue;
	}

	
	///////////////////////////////////////
	public String getShortcutIcon3() {
		return shortcutIcon3;
	}
	public void setShortcutIcon3(String newValue) {
		this.shortcutIcon3 = newValue;
	}

	
	///////////////////////////////////////	
	public String getShortcutOpen3() {
		return shortcutOpen3;
	}
	public void setShortcutOpen3(String newValue) {
		this.shortcutOpen3 = newValue;
	}
	

	///////////////////////////////////////	
	public String getShortcutName4() {
		return shortcutName4;
	}
	public void setShortcutName4(String newValue) {
		this.shortcutName4 = newValue;
	}

	
	///////////////////////////////////////	
	public String getShortcutURL4() {
		return shortcutURL4;
	}
	public void setShortcutURL4(String newValue) {
		this.shortcutURL4 = newValue;
	}

	
	///////////////////////////////////////
	public String getShortcutIcon4() {
		return shortcutIcon4;
	}
	public void setShortcutIcon4(String newValue) {
		this.shortcutIcon4 = newValue;
	}	
	
	
	///////////////////////////////////////	
	public String getShortcutOpen4() {
		return shortcutOpen4;
	}
	public void setShortcutOpen4(String newValue) {
		this.shortcutOpen4 = newValue;
	}
	
	
	///////////////////////////////////////	
	public String getShortcutName5() {
		return shortcutName5;
	}
	public void setShortcutName5(String newValue) {
		this.shortcutName5 = newValue;
	}

	
	///////////////////////////////////////	
	public String getShortcutURL5() {
		return shortcutURL5;
	}
	public void setShortcutURL5(String newValue) {
		this.shortcutURL5 = newValue;
	}

	
	///////////////////////////////////////
	public String getShortcutIcon5() {
		return shortcutIcon5;
	}
	public void setShortcutIcon5(String newValue) {
		this.shortcutIcon5 = newValue;
	}		

	
	///////////////////////////////////////	
	public String getShortcutOpen5() {
		return shortcutOpen5;
	}
	public void setShortcutOpen5(String newValue) {
		this.shortcutOpen5 = newValue;
	}	
	
	
	///////////////////////////////////////	
	public String getShortcutName6() {
		return shortcutName6;
	}
	public void setShortcutName6(String newValue) {
		this.shortcutName6 = newValue;
	}

	
	///////////////////////////////////////	
	public String getShortcutURL6() {
		return shortcutURL6;
	}
	public void setShortcutURL6(String newValue) {
		this.shortcutURL6 = newValue;
	}

	
	///////////////////////////////////////
	public String getShortcutIcon6() {
		return shortcutIcon6;
	}
	public void setShortcutIcon6(String newValue) {
		this.shortcutIcon6 = newValue;
	}			

	///////////////////////////////////////	
	public String getShortcutOpen6() {
		return shortcutOpen6;
	}
	public void setShortcutOpen6(String newValue) {
		this.shortcutOpen6 = newValue;
	}	
	

	///////////////////////////////////////	
	public String getShortcutName7() {
		return shortcutName7;
	}
	public void setShortcutName7(String newValue) {
		this.shortcutName7 = newValue;
	}

	
	///////////////////////////////////////	
	public String getShortcutURL7() {
		return shortcutURL7;
	}
	public void setShortcutURL7(String newValue) {
		this.shortcutURL7 = newValue;
	}

	
	///////////////////////////////////////
	public String getShortcutIcon7() {
		return shortcutIcon7;
	}
	public void setShortcutIcon7(String newValue) {
		this.shortcutIcon7 = newValue;
	}		
	
	
	///////////////////////////////////////	
	public String getShortcutOpen7() {
		return shortcutOpen7;
	}
	public void setShortcutOpen7(String newValue) {
		this.shortcutOpen7 = newValue;
	}	
	
	
	///////////////////////////////////////	
	public String getShortcutName8() {
		return shortcutName8;
	}
	public void setShortcutName8(String newValue) {
		this.shortcutName8 = newValue;
	}

	
	///////////////////////////////////////	
	public String getShortcutURL8() {
		return shortcutURL8;
	}
	public void setShortcutURL8(String newValue) {
		this.shortcutURL8 = newValue;
	}

	
	///////////////////////////////////////
	public String getShortcutIcon8() {
		return shortcutIcon8;
	}
	public void setShortcutIcon8(String newValue) {
		this.shortcutIcon8 = newValue;
	}			
	
	
	///////////////////////////////////////	
	public String getShortcutOpen8() {
		return shortcutOpen8;
	}
	public void setShortcutOpen8(String newValue) {
		this.shortcutOpen8 = newValue;
	}
	
	///////////////////////////////////////	
	public String getShortcutName9() {
		return shortcutName9;
	}
	public void setShortcutName9(String newValue) {
		this.shortcutName9 = newValue;
	}

	
	///////////////////////////////////////	
	public String getShortcutURL9() {
		return shortcutURL9;
	}
	public void setShortcutURL9(String newValue) {
		this.shortcutURL9 = newValue;
	}

	
	///////////////////////////////////////
	public String getShortcutIcon9() {
		return shortcutIcon9;
	}
	public void setShortcutIcon9(String newValue) {
		this.shortcutIcon9 = newValue;
	}		
	
	
	///////////////////////////////////////	
	public String getShortcutOpen9() {
		return shortcutOpen9;
	}
	public void setShortcutOpen9(String newValue) {
		this.shortcutOpen9 = newValue;
	}	
	
	
	///////////////////////////////////////	
	public String getShortcutName10() {
		return shortcutName10;
	}
	public void setShortcutName10(String newValue) {
		this.shortcutName10 = newValue;
	}

	
	///////////////////////////////////////	
	public String getShortcutURL10() {
		return shortcutURL10;
	}
	public void setShortcutURL10(String newValue) {
		this.shortcutURL10 = newValue;
	}

	
	///////////////////////////////////////
	public String getShortcutIcon10() {
		return shortcutIcon10;
	}
	public void setShortcutIcon10(String newValue) {
		this.shortcutIcon10 = newValue;
	}		
	
	
	///////////////////////////////////////	
	public String getShortcutOpen10() {
		return shortcutOpen10;
	}
	public void setShortcutOpen10(String newValue) {
		this.shortcutOpen10 = newValue;
	}
	
	
	///////////////////////////////////////	
	public String getCurrencyLocale() {
		return currencyLocale;
	}
	public void setCurrencyLocale(String newValue) {
		this.currencyLocale = newValue;
	}

	
	///////////////////////////////////////	
	public String getDefaultCapacity() {
		return defaultCapacity;
	}
	public void setDefaultCapacity(String newValue) {
		this.defaultCapacity = newValue;
	}
	
	
	///////////////////////////////////////		
	public String getArtifactMaxFile() {
		return artifactMaxFile;
	}
	public void setArtifactMaxFile(String newValue) {
		this.artifactMaxFile = newValue;
	}

	
	///////////////////////////////////////	
	public String getArtifactExpClasses() {
		return artifactExpClasses;
	}
	public void setArtifactExpClasses(String newValue) {
		this.artifactExpClasses = newValue;
	}

	
	/////////////////////////////////////////        
	public String getPicturePath() {
		return picturePath;
	}
	public void setPicturePath(String newValue) {
		this.picturePath = newValue;
	}
    
	
	/////////////////////////////////////////     
	public String getMaxMetaFieldTimeout() {
		return maxMetaFieldTimeout;
	}
	public void setMaxMetaFieldTimeout(String newValue) {
		this.maxMetaFieldTimeout = newValue;
	}

	
	
	/**
	 * Validate the form.
	 */
	@SuppressWarnings("unchecked")
	public ActionErrors validate(ActionMapping arg0, HttpServletRequest request) {
		ActionErrors errors = new ActionErrors();

		if (operation.equals("saveOption")){
		    
			//validate the status status values...
	        this.taskStatusOrderingList = new Vector<TaskStatusTO>();
	        Vector<TaskStatusTO> tslist = (Vector<TaskStatusTO>)request.getSession().getAttribute("taskStatusList");
	        Iterator<TaskStatusTO> i = tslist.iterator();
	        while(i.hasNext()){
	            TaskStatusTO tsto = i.next();
	            String statusValue = request.getParameter("tx_" + tsto.getId() + "_value");
	            if (statusValue!=null) {
			        if (!Pattern.matches("[0-9]+", statusValue)) {
			            errors.add("Status", new ActionError("validate.manageOption.invalidTaskStatusOrdering", statusValue) );
			        } else {
			            tsto.setGenericTag(statusValue);
			            this.taskStatusOrderingList.addElement(tsto);	            
			        }	            	
	            }
	        }	        

	        if (!Pattern.matches("[0-9]+", this.taskNumLine)) {
	            errors.add("taskNumLine", new ActionError("validate.manageOption.invalidTaskNulLine") );
	        }

	        if (this.maxMetaFieldTimeout!=null && !Pattern.matches("[0-9]+", this.maxMetaFieldTimeout)) {
	            errors.add("maxMetaFieldTimeout", new ActionError("validate.manageOption.invalidMaxExecMetaField") );
	        }
	        
	        if (!Pattern.matches("[0-9]+", this.gadgetWidth)) {
	            errors.add("gadgetWidth", new ActionError("validate.manageOption.invalidgadgetWidth") );
	        } else {
		        if (Integer.parseInt(this.gadgetWidth)>1000 || Integer.parseInt(this.gadgetWidth)<200) {
		            errors.add("gadgetWidth", new ActionError("validate.manageOption.invalidgadgetWidth") );
		        }
	        }

	        
	        if (!Pattern.matches("[0-9]+", this.pendNumLine)) {
	            errors.add("pendNumLine", new ActionError("validate.manageOption.invalidPendNulLine") );
	        }
	        
	        if (!Pattern.matches("[0-9]+", this.requNumLine)) {
	            errors.add("requNumLine", new ActionError("validate.manageOption.invalidRequNulLine") );
	        }
	
	        if (!Pattern.matches("[0-9]+", this.projNumLine)) {
	            errors.add("projNumLine", new ActionError("validate.manageOption.invalidProjNulLine") );
	        }

	        if (!Pattern.matches("[0-9]+", this.topicNumLine)) {
	            errors.add("topicNumLine", new ActionError("validate.manageOption.invalidTeamNulLine") );
	        }

	        if (!Pattern.matches("[0-9]+", this.infoNumLine)) {
	            errors.add("infoNumLine", new ActionError("validate.manageOption.invalidInfoNulLine") );
	        }
	        
	        if (!Pattern.matches("[0-9]+", this.myReqMaxDaysAgo)) {
	            errors.add("myReqMaxDaysAgo", new ActionError("validate.manageOption.invalidMyReqMaxDaysAgo") );
	        }

	        if (!Pattern.matches("[0-9]+", this.myTaskMaxDaysAgo)) {
	            errors.add("myTaskMaxDaysAgo", new ActionError("validate.manageOption.invalidMyTaskMaxDaysAgo") );
	        }

	        if (!Pattern.matches("[0-9]+", this.warningTaskDays)) {
	            errors.add("warningTaskDays", new ActionError("validate.manageOption.invalidWarningDays") );
	        }

	        if (!Pattern.matches("[0-9]+", this.criticalTaskDays)) {
	            errors.add("criticalTaskDays", new ActionError("validate.manageOption.invalidCriticalDays") );
	        }

	        if (!Pattern.matches("[0-9]+", this.ldapPort) && ldapHost!=null && !ldapHost.trim().equals("") ) {
	            errors.add("ldapPort", new ActionError("validate.manageOption.invalidLdapPort") );
	        }

	        
		} else if (operation.equals("changePassword")){
		
			if (this.newPassword==null || this.newPassword.trim().equals("")){             	
			    errors.add("Password", new ActionError("validate.manageOption.password.mandatory") );
			    
			} else if (!this.newPassword.equals(this.newPasswordConfirm)){
			    errors.add("Password", new ActionError("validate.manageOption.password.match") );
			}
		}
		
		return errors;
	}

	
}
