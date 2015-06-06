package com.pandora.gui.struts.action.projectpanel;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.pandora.ProjectTO;
import com.pandora.RiskTO;
import com.pandora.UserTO;
import com.pandora.delegate.ProjectDelegate;
import com.pandora.delegate.RiskDelegate;
import com.pandora.exception.BusinessException;
import com.pandora.gui.struts.action.ProjectPanelAction;
import com.pandora.gui.struts.form.ProjectPanelForm;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.SessionUtil;

public class RiskInfoProjectPanel extends ProjectPanelAction {
	
	private final static int IMPACT_TYPE_TIME  = 1;
	private final static int IMPACT_TYPE_SCOP  = 2;
	private final static int IMPACT_TYPE_COST  = 3;
	private final static int IMPACT_TYPE_QUALI = 4;
	
	private final static int RISK_TYPE_PROBAB = 10;
	private final static int RISK_TYPE_IMPACT = 11;
	private final static int RISK_TYPE_TREND  = 12;
	
	@Override
	public String getPanelId() {
		return "RISK_INFO";
	}

	@Override
	public String getPanelTitle() {
		return "title.projectPanelForm.risk";
	}
	
	@Override
	public String renderPanel(HttpServletRequest request,
			ProjectPanelForm frm) throws BusinessException {
		StringBuffer sb = new StringBuffer("");
		ProjectDelegate pdel = new ProjectDelegate();
		RiskDelegate rdel = new RiskDelegate();
		
		if (frm.getProjectId()!=null && !frm.getProjectId().trim().equals("")) {
			ProjectTO pto = new ProjectTO(frm.getProjectId());
			pto = pdel.getProjectObject(pto, true);
			UserTO uto = SessionUtil.getCurrentUser(request);

			sb.append("<table width=\"95%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
			sb.append("<tr><td width=\"150\">&nbsp;</td><td>");
			
			sb.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"1\">\n");
			sb.append("<tr class=\"tableRowHeader\" height=\"20\">" +
					"<td width=\"40\">&nbsp;</b></td>" +
           		    "<td><b>" + super.getBundleMessage(request, "label.formRisk.name") + "</b></td>" +
           		    "<td width=\"180\"><b><center>" + super.getBundleMessage(request, "label.formRisk.category") + "</center></b></td>" +
           		    "<td width=\"40\"><b><center>" + super.getBundleMessage(request, "label.formRisk.type") + "</center></b></td>" +
           		    "<td width=\"150\"><b>" + super.getBundleMessage(request, "label.formRisk.responsible") + "</b></td>" +
           		    "<td width=\"40\"><b><center>" + super.getBundleMessage(request, "label.formRisk.probability.short") + "</center></b></td>" +
           		    "<td width=\"40\"><b><center>" + super.getBundleMessage(request, "label.formRisk.impact.short") + "</center></b></td>" +
           		    "<td width=\"40\"><b><center>" + super.getBundleMessage(request, "label.formRisk.tendency.short") + "</center></b></td>" +
           		    "<td colspan=\"4\"><b><center>" + super.getBundleMessage(request, "label.formRisk.impact.title") + "</center></b></td>" +
           		    "<td width=\"140\"><b><center>" + super.getBundleMessage(request, "label.formRisk.status") + "</center></b></td>" +
           		  "</tr>");
			
			Vector<RiskTO> list = rdel.getRiskList(frm.getProjectId(), uto.getId());
			if (list!=null && list.size()>0) {
				for (RiskTO rto : list) {
					sb.append("<tr class=\"tableRowOdd\" height=\"20\">" +
							"<td>" + rto.getId() + "</td>" +
		           		    "<td>" + rto.getName() + "</td>" +
		           		    "<td><center>" + rto.getCategory().getName() + "</center></td>" +
		           		    "<td><center>" + this.getRiskTypeIcon(rto.getRiskType()+"", uto) + "</center></td>" +
		           		    "<td>" + rto.getResponsible() + "</td>" +
		           		    "<td><center>" + this.getIcon( RISK_TYPE_PROBAB, rto.getProbability(), uto) + "</center></td>" +
		           		    "<td><center>" + this.getIcon( RISK_TYPE_IMPACT, rto.getImpact(), uto) + "</center></td>" +
		           		    "<td><center>" + this.getIcon( RISK_TYPE_TREND, rto.getTendency(), uto) + "</center></td>" +		           		    
		           		    "<td width=\"20\">" + this.getImpactIcon(IMPACT_TYPE_COST, rto.getCostImpact(), uto) + "</td>" +
		           		    "<td width=\"20\">" + this.getImpactIcon(IMPACT_TYPE_TIME, rto.getTimeImpact(), uto) + "</td>" +
		           		    "<td width=\"20\">" + this.getImpactIcon(IMPACT_TYPE_SCOP, rto.getScopeImpact(), uto) + "</td>" +
		           		    "<td width=\"20\">" + this.getImpactIcon(IMPACT_TYPE_QUALI, rto.getQualityImpact(), uto) + "</td>" +
		           		    "<td><center>" + rto.getStatus().getName() + "</center></td>" +
		           		  "</tr>");				
				}				
			} else {
				sb.append("<tr class=\"tableRowOdd\" height=\"20\"><td colspan=\"13\"><center>" + uto.getBundle().getMessage("basic.msg.empty_list", uto.getLocale()) + "</center></td></tr>");				
			}
			
			sb.append("</td></tr></table></table>\n");  	
		}
		
		return sb.toString();

	}

	private String getImpactIcon(int impactType, boolean val, UserTO uto) {
		String response = "&nbsp;";
		String hint = "";
		
		if (val) {
			if (impactType==IMPACT_TYPE_COST) {
				hint = "label.formRisk.impact.cost";
			} else if (impactType==IMPACT_TYPE_QUALI) {
				hint = "label.formRisk.impact.qual";
			} else if (impactType==IMPACT_TYPE_SCOP) {
				hint = "label.formRisk.impact.scop";
			} else if (impactType==IMPACT_TYPE_TIME) {
				hint = "label.formRisk.impact.time";
			}
			
			String altValue = uto.getBundle().getMessage(hint, uto.getLocale());
			response = "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/tick.png\" >";
		}
		
		return response;
	}
	
	private String getRiskTypeIcon(String val, UserTO uto) {
		String response = "&nbsp;";
		
		if (val!=null && !val.trim().equals("")) {

			String img = "";
			if (val.equals("0")) {
				img = "bomb.png";
			} else if (val.equals("1")) {
				img = "fav-task.gif";
			}
			
			if (!img.trim().equals("")) {
				String altValue = uto.getBundle().getMessage("label.formRisk.type." + val, uto.getLocale());
				response = "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/" + img + "\" >";				
			}
		}
		
		return response;
	}
	
	
	private String getIcon(int type, String val, UserTO uto) {
		String response = "&nbsp;";
		String hint = "";
		String img = "smile_2.gif";
		
		if (val.equals("1")) {
			img = "smile_3.gif";
		}
		
		if (val!=null && !val.trim().equals("")) {
			if (type==RISK_TYPE_PROBAB) {
				hint = "label.formRisk.probability." + val;
				if (val.equals("5") || val.equals("4")) {
					img = "smile_1.gif";
				}				
			} else if (type==RISK_TYPE_IMPACT) {
				hint = "label.formRisk.impact." + val;
				if (val.equals("5") || val.equals("4")) {
					img = "smile_1.gif";
				}
			} else if (type==RISK_TYPE_TREND) {
				hint = "label.formRisk.tendency." + val;
				if (val.equals("3")) {
					img = "smile_1.gif";
				}
			}
			String altValue = uto.getBundle().getMessage(hint, uto.getLocale());
			response = "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/" + img + "\" >";
		}
		
		return response;		
	}
}
