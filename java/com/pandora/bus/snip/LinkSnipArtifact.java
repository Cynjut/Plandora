package com.pandora.bus.snip;

import javax.servlet.http.HttpServletRequest;

import com.pandora.gui.struts.form.SnipArtifactForm;
import com.pandora.helper.HtmlUtil;

public class LinkSnipArtifact extends SnipArtifact {

	@Override
	public String getId() {
		return "snip_lnk";
	}

	@Override
	public String getUniqueName() {
		return "label.artifactTag.snip.addLink";
	}

	@Override
	public int getWidth() {
		return 400;
	}

	@Override
	public int getHeight() {
		return 150;
	}

	
	@Override
	public String submit(HttpServletRequest request, SnipArtifactForm frm) {
		String response = "";
		String linkTo = request.getParameter("snip_link_to");
		String linkNm = request.getParameter("snip_link_name");
		String linkTt = request.getParameter("snip_link_ttip");
				
		if (linkTo!=null && !linkTo.trim().equals("")) {
			
			if (linkNm==null || linkNm.trim().equals("")) {
				linkNm = linkTo;
			}
			
			if (linkTt==null || linkTt.trim().equals("")) {
				linkTt = linkTo;
			}
			String hint = HtmlUtil.getHint(linkTt);
			
			response = "<a href=\"" + linkTo + "\" " + hint + ">" + linkNm + "</a>";
		}
		
		return response;
	}
	
	@Override
	public String getHtmlBody(HttpServletRequest request, SnipArtifactForm frm) {
		StringBuffer sb = new StringBuffer();

		sb.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		sb.append("<tr class=\"pagingFormBody\">");
		sb.append("<td width=\"10\">&nbsp;</td>");
		sb.append("<td width=\"70\" class=\"formTitle\">" + super.getI18nMsg("label.artifactTag.snip.addLink.linkto", handler.getLocale()) + ":&nbsp;</td>");
		sb.append("<td class=\"formBody\">" + HtmlUtil.getTextBox("snip_link_to", "", false, "", 200, 50) + "</td>");
		sb.append("<td width=\"10\">&nbsp;</td>");
		sb.append("</tr>");

		sb.append("<tr class=\"pagingFormBody\">");
		sb.append("<td>&nbsp;</td>");
		sb.append("<td class=\"formTitle\">" + super.getI18nMsg("label.artifactTag.snip.addLink.text", handler.getLocale()) + ":&nbsp;</td>");
		sb.append("<td class=\"formBody\">" + HtmlUtil.getTextBox("snip_link_name", "", false, "", 200, 50) + "</td>");
		sb.append("<td>&nbsp;</td>");
		sb.append("</tr>");

		sb.append("<tr class=\"pagingFormBody\">");
		sb.append("<td>&nbsp;</td>");
		sb.append("<td class=\"formTitle\">" + super.getI18nMsg("label.artifactTag.snip.addLink.tooltip", handler.getLocale()) + ":&nbsp;</td>");
		sb.append("<td class=\"formBody\">" + HtmlUtil.getTextBox("snip_link_ttip", "", false, "", 200, 50) + "</td>");
		sb.append("<td>&nbsp;</td>");
		sb.append("</tr>");
		
		sb.append("</table>");
		
		return sb.toString();
	}

}
