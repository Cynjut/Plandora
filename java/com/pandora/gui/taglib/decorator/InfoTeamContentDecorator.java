package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.PlanningTO;
import com.pandora.ProjectTO;
import com.pandora.RequirementStatusTO;
import com.pandora.ResourceTO;
import com.pandora.RiskStatusTO;
import com.pandora.TaskStatusTO;
import com.pandora.TeamInfoTO;
import com.pandora.UserTO;
import com.pandora.bus.occurrence.EventOccurrence;
import com.pandora.bus.occurrence.IssueOccurrence;
import com.pandora.bus.occurrence.IterationOccurrence;
import com.pandora.bus.occurrence.LessonLearnedOccurrence;
import com.pandora.bus.occurrence.MilestoneOccurrence;
import com.pandora.bus.occurrence.Occurrence;
import com.pandora.bus.occurrence.StrategicObjectivesOccurrence;
import com.pandora.delegate.RepositoryDelegate;
import com.pandora.delegate.RequirementStatusDelegate;
import com.pandora.delegate.TaskStatusDelegate;
import com.pandora.delegate.UserDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.taglib.discussiontopic.DiscussionTopic;
import com.pandora.helper.StringUtil;

public class InfoTeamContentDecorator extends ColumnDecorator {


	@Override
	public String decorate(Object columnValue, String tag) {
		StringBuffer strBuff = new StringBuffer("");
		
		try {
			Object info = getObject();
			String fullname = "";
			if (info instanceof TeamInfoTO) {			
				TeamInfoTO tito = (TeamInfoTO)info;
				UserTO uto = (UserTO)this.getSession().getAttribute(UserDelegate.CURRENT_USER_SESSION);

				strBuff.append("<div onmouseout=\"hideInfoMenu('" + tito.getId() + "');\" onmouseover=\"showInfoMenu('" + tito.getId() + "');\">");
				
		        strBuff.append("<table width=\"100%\" border=\"0\" valign=\"top\" cellspacing=\"0\" cellpadding=\"0\">");
		        
				fullname = tito.getUser().getName();
		        strBuff.append("<tr><td colspan=\"3\" class=\"successfullyMessage\" align=\"left\" valign=\"center\">" + fullname + "</td></tr>"); 

		        strBuff.append("<tr height=\"28\"><td class=\"tableCell\" align=\"center\" valign=\"center\" width=\"30\"><img border=\"0\" src=\"" + this.getTopicImg(tito) + "\"></td>");
		        strBuff.append("<td class=\"tableCell\" align=\"left\" valign=\"center\">" + this.getTopicSubject(tito, uto) + "</td><td width=\"10\">&nbsp;</td></tr>");
		        
		        if (tito.getComment()!=null && !tito.getComment().trim().equals("")) {
		        	//int maxWords = getMaxWords(tag, uto);
		        	String comment = StringUtil.cropWords(tito.getComment(), 70, true);
			        strBuff.append("<tr class=\"tableRowAction\"><td class=\"tableCell\">&nbsp;</td>");
			        strBuff.append("<td align=\"justify\" valign=\"center\">" + comment + "</td><td>&nbsp;</td></tr>");		        
		        }

		        strBuff.append("<tr><td colspan=\"3\">");
	        
		        strBuff.append("<table width=\"100%\" border=\"0\" valign=\"top\" cellspacing=\"0\" cellpadding=\"0\">");
		        strBuff.append("<tr><td width=\"70%\">&nbsp;</td>");
		        strBuff.append("<td width=\"130\" valign=\"top\">");
		        
		        if (tito.getUser()!=null && tito.getUser().getId().equals(uto.getId()) && tito.getType().equals(TeamInfoTO.TYPE_TOPIC)) {
		        	String removeLbl = super.getBundleMessage("label.formForum.remove");
		        	String confirm = super.getBundleMessage("message.formForum.confirmRemoveTopic");
			        strBuff.append("<div style=\"display:none\" id=\"REMOVE_POST_" + tito.getId() + "\">" +
			        		"<img src=\"../images/child.gif\" />" +
			        		"<a href=\"javascript:removePost('resourceHomeForm', '" + tito.getId() + 
			        		"', '" + confirm + "');\" class=\"gridLink\">" + removeLbl + "</a></div></td>");
		        } else {
			        strBuff.append("<img src=\"../images/empty.gif\" /></td>");		        	
		        }

		        String detailsLink = null;
		        if (tito.getType().equals(PlanningTO.PLANNING_TASK) || 
		        		tito.getType().equals(PlanningTO.PLANNING_REQUIREMENT) ||
		        		tito.getType().equals(PlanningTO.PLANNING_OCCURENCE) ||
		        		tito.getType().equals(PlanningTO.PLANNING_RISK)) {
		        	detailsLink = tito.getId(); 
		        } else if (tito.getType().equals("TPC") || tito.getType().equals("ATT")){
		        	detailsLink = tito.getParentId();
		        }

		        strBuff.append("<td width=\"100\" class=\"tableCell\">");
		        if (detailsLink!=null) {
			        strBuff.append("<div style=\"display:none\" id=\"MORE_POST_" + tito.getId() + "\">" +
			        				"<img src=\"../images/child.gif\" />" +
			        				"<a href=\"../do/viewKb?operation=showEntity&id=" + detailsLink + "&projectId=" + tito.getProject().getId() + 
			        				"\" class=\"gridLink\">" + super.getBundleMessage("label.formForum.more") + "</a></div></td>");
			        
			        strBuff.append("<td width=\"160\" class=\"tableCell\"><div style=\"display:none\" id=\"REPLY_POST_" + tito.getId() + "\">" +
			        		"<img src=\"../images/child.gif\" />" +
			        		"<a href=\"javascript:displayMessage('../do/manageResourceHome?operation=replyInfoTeamTopic&planning=" + 
			        		tito.getId() + "',480,150);\" class=\"gridLink\">" + super.getBundleMessage("label.formForum.comment") + "</a></div></td>");
			        
		        } else {
		        	strBuff.append("<img src=\"../images/empty.gif\" /></td>");
			        strBuff.append("<td width=\"160\" class=\"tableCell\">&nbsp;</td>");
		        }
		        
		        strBuff.append("</tr></table>");
		        strBuff.append("</td></tr></table></div>");
			}			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return strBuff.toString();
	}

/*
	private int getMaxWords(String tag, UserTO uto) {
    	int maxWords = 45;
		if (tag!=null) {
			String pref = uto.getPreference().getPreference(tag);
			try {
				maxWords = Integer.parseInt(pref);
			}catch(Exception e){
				e.printStackTrace();
				maxWords = 45;
			}
		}
		return maxWords;
	}
*/

	private String getTopicSubject(TeamInfoTO tito, UserTO uto) throws BusinessException {
		StringBuffer content = new StringBuffer();
		DiscussionTopic disc = new DiscussionTopic();
		UserDelegate udel = new UserDelegate();
		
		String name = tito.getName();
		if (name!=null) {
			name = "<b>" + StringUtil.cropWords(name, 40, true) + " </b>";
		} else {
			name = "";
		}
		
		if (tito.getType().equals(TeamInfoTO.TYPE_ATTACHMENT)) {
			String fileLbl = uto.getBundle().getMessage(uto.getLocale(), "label.myteam.attach.file");
			String linkLbl = uto.getBundle().getMessage(uto.getLocale(), "label.myteam.attach.link");
    		String fileName = "<a class=\"gridLink\" href=\"javascript:downloadAttachment(" + tito.getId() + ");\" border=\"0\">" + tito.getName() + "</a>";
			
			if (tito.getComment()!=null && tito.getComment().indexOf("DOWNLOAD!")>-1) {
				fileLbl = uto.getBundle().getMessage(uto.getLocale(), "label.myteam.attach.download");
			}
			content.append(fileLbl + " " + fileName + " " + linkLbl + " " + this.getParentIdLink(tito));
			
		} else if (tito.getType().equals(TeamInfoTO.TYPE_OCCURRENCE)) {
			if (Occurrence.STATE_START.equals(tito.getStatus())) {
				content.append(uto.getBundle().getMessage(uto.getLocale(), "label.myteam.occ.start"));
			} else if (Occurrence.STATE_START.equals(tito.getStatus())) {
				content.append(uto.getBundle().getMessage(uto.getLocale(), "label.myteam.occ.close"));
			} else {
				content.append(uto.getBundle().getMessage(uto.getLocale(), "label.myteam.occ.update"));
			}
			
			content.append(" ");
			
			if (tito.getParentTopicUser().equals(IterationOccurrence.class.getName())) {
				content.append(uto.getBundle().getMessage(uto.getLocale(), "label.occurrence.iteration"));
			} else if (tito.getParentTopicUser().equals(MilestoneOccurrence.class.getName())) {
				content.append(uto.getBundle().getMessage(uto.getLocale(), "label.occurrence.milestone"));
			} else if (tito.getParentTopicUser().equals(EventOccurrence.class.getName())) {
				content.append(uto.getBundle().getMessage(uto.getLocale(), "label.occurrence.event"));
			} else if (tito.getParentTopicUser().equals(LessonLearnedOccurrence.class.getName())) {
				content.append(uto.getBundle().getMessage(uto.getLocale(), "label.occurrence.lesson"));
			} else if (tito.getParentTopicUser().equals(IssueOccurrence.class.getName())) {
				content.append(uto.getBundle().getMessage(uto.getLocale(), "label.occurrence.issue"));
			} else if (tito.getParentTopicUser().equals(StrategicObjectivesOccurrence.class.getName())) {
				content.append(uto.getBundle().getMessage(uto.getLocale(), "label.occurrence.so"));
			} else {
				content.append(uto.getBundle().getMessage(uto.getLocale(), "label.myteam.occ"));
			}
			
			content.append(" " + name);
			
		} else if (tito.getType().equals(TeamInfoTO.TYPE_REQUIREMENT)) {
			RequirementStatusDelegate rsdel = new RequirementStatusDelegate();
			RequirementStatusTO rsto = rsdel.getObjectById(tito.getStatus());
			
			if (rsto!=null) {
				if (RequirementStatusTO.STATE_MACHINE_PLANNED.toString().equals(rsto.getStateMachineOrder()+"")) {
					content.append(uto.getBundle().getMessage(uto.getLocale(), "label.myteam.req.plan"));
				} else if (RequirementStatusTO.STATE_MACHINE_REFUSE.toString().equals(rsto.getStateMachineOrder()+"")) {
					content.append(uto.getBundle().getMessage(uto.getLocale(), "label.myteam.req.reject"));
				} else if (RequirementStatusTO.STATE_MACHINE_WAITING.toString().equals(rsto.getStateMachineOrder()+"")) {
					content.append(uto.getBundle().getMessage(uto.getLocale(), "label.myteam.req.create"));
				} else if (RequirementStatusTO.STATE_MACHINE_CLOSE.toString().equals(rsto.getStateMachineOrder()+"")) {
					content.append(uto.getBundle().getMessage(uto.getLocale(), "label.myteam.req.close"));
				}				
			}

			name = tito.getName();
			if (name!=null) {
				name = "<b>" + StringUtil.cropWords(name, 8, true) + " </b>";
			}			
			
			content.append(" " + name);

		} else if (tito.getType().equals(TeamInfoTO.TYPE_RISK)) {
			if (RiskStatusTO.MATERIALIZE_RISK_TYPE.toString().equals(tito.getStatus())) {
				content.append(uto.getBundle().getMessage(uto.getLocale(), "label.myteam.risk.materialize"));	
			} else {
				content.append(uto.getBundle().getMessage(uto.getLocale(), "label.myteam.risk.update"));	
			}
			content.append(" " + name);
			
		} else if (tito.getType().equals(TeamInfoTO.TYPE_TASK)) {
			TaskStatusDelegate tsdel = new TaskStatusDelegate();
			TaskStatusTO tsto = tsdel.getTaskStatusObject(new TaskStatusTO(tito.getStatus()));
			
			if (tsto!=null && TaskStatusTO.STATE_MACHINE_CLOSE.toString().equals(tsto.getStateMachineOrder()+"")) {
				content.append(uto.getBundle().getMessage(uto.getLocale(), "label.myteam.task.close"));	
			} else {
				content.append(uto.getBundle().getMessage(uto.getLocale(), "label.myteam.task.update"));	
			}
			content.append(" " + name);
			
		} else if (tito.getType().equals(TeamInfoTO.TYPE_TOPIC)) {			
			String aboutLbl = uto.getBundle().getMessage(uto.getLocale(), "label.myteam.topic.about");
			if (tito.getParentTopic()!=null && !tito.getParentTopic().trim().equals("")) {
				String userLbl = uto.getBundle().getMessage(uto.getLocale(), "label.myteam.topic.reply");
				content.append(userLbl + " <b>" + tito.getParentTopicUser() + "</b> " + aboutLbl);
			} else {
				String comentLbl = uto.getBundle().getMessage(uto.getLocale(), "label.myteam.topic.comment");
				content.append(comentLbl + " " + aboutLbl);	
			}
			content.append(" " + this.getParentIdLink(tito));
			
		} else {
			if (tito.getStatus()!=null) {

				String repFileName = tito.getName();
				if (!tito.getStatus().equals(RepositoryDelegate.ACTION_UPLOAD_FOLDER)) {
		        	ResourceTO rto = new ResourceTO(uto.getId());
		        	if (tito.getProject()!=null) {
		            	rto.setProject(tito.getProject());
		            	rto = udel.getResource(rto);
		            	if (rto!=null && rto.getBoolCanSeeRepository()) {
		            		repFileName = "<a class=\"gridLink\" href=\"../do/showRepositoryViewer?operation=getFile&projectId=" + 
		            					tito.getProject().getId() + "&path=" + tito.getName() + "\" border=\"0\">" +
		            					tito.getName() + "</a>";
		            	}
		        	}					
				}
				
				content.append(uto.getBundle().getMessage(uto.getLocale(), "label.myteam.repository." + tito.getStatus()));
				content.append(" <b>" + repFileName + "</b>");				
			}
		}
		
		if (tito.getProject()!=null && tito.getProject().getName()!=null) {
			String prjLbl = uto.getBundle().getMessage(uto.getLocale(), "label.myteam.project");
			content.append(" " + prjLbl + " <b>" + tito.getProject().getName() + "</b>");
		}
		
		if (tito.getCreationDate()!=null) {
			content.append("  " + disc.getPostTime(uto, tito.getCreationDate()));	
		}
		
				
		return content.toString();
	}


	private String getParentIdLink(TeamInfoTO tito) {
		return "<a class=\"gridLink\" href=\"javascript:loadMindMap(" + tito.getParentId() + ");\" border=\"0\">[#" + tito.getParentId() + "]</a>";
	}

	private String getTopicImg(TeamInfoTO tito) {
		String content = "";
		if (tito.getType().equals(TeamInfoTO.TYPE_ATTACHMENT)) {
			content = "../images/attachment.gif";
		} else if (tito.getType().equals(TeamInfoTO.TYPE_OCCURRENCE)) {
			content = "../images/occurrence.gif";
		} else if (tito.getType().equals(TeamInfoTO.TYPE_REQUIREMENT)) {
			content = "../images/requirem.gif";
		} else if (tito.getType().equals(TeamInfoTO.TYPE_RISK)) {
			content = "../images/risk.gif"; 
		} else if (tito.getType().equals(TeamInfoTO.TYPE_TASK)) {
			content = "../images/newTask.gif";
		} else if (tito.getType().equals(TeamInfoTO.TYPE_TOPIC)) {
			content = "../images/discussion.gif";
		} else {
			content = "../images/artifact.png";
		}
		return content;
	}


	@Override
	public String decorate(Object columnValue) {
		return decorate(columnValue, null);
	}

	
	@Override
	public String contentToSearching(Object columnValue) {
    	String content = "";
		Object info = getObject();
		if (info instanceof TeamInfoTO) {
			ProjectTO pto = ((TeamInfoTO)info).getProject();
			if (pto!=null) {
				content = pto.getName();	
			}
		}
    	return content;

	}
	
	
}
