package com.pandora.bus.snip;

import javax.servlet.http.HttpServletRequest;

import com.pandora.gui.struts.form.SnipArtifactForm;
import com.pandora.helper.HtmlUtil;

public class TableSnipArtifact extends SnipArtifact {

	@Override
	public String getId() {
		return "snip_table";
	}

	@Override
	public String getUniqueName() {
		return "label.artifactTag.snip.addTable";
	}

	@Override
	public int getWidth() {
		return 430;
	}

	@Override
	public int getHeight() {
		return 170;
	}	
	
	@Override
	public String submit(HttpServletRequest request, SnipArtifactForm frm) {
		
		int line = 1;
		int cols = 3;
		int padd = 0;
		int spc = 0;
		int brd = 1;
		
		String lineStr = request.getParameter("snip_table_line");
		line = super.getIntValue(lineStr, 1, 1, 999);
		
		String colsStr = request.getParameter("snip_table_cols");
		cols = super.getIntValue(colsStr, 3, 1, 999);
		
		String paddStr = request.getParameter("snip_table_padd");
		padd = super.getIntValue(paddStr, 1, 1, 999);
		
		String spcStr = request.getParameter("snip_table_spc");
		spc = super.getIntValue(spcStr, 3, 1, 999);
		
		String brdWdt = request.getParameter("snip_table_brd");
		brd = super.getIntValue(brdWdt, 1, 0, 20);

		String bordercolor = request.getParameter("snip_table_brdcl");
		String bgcolor = request.getParameter("snip_table_bg");

		String response = "<table";
		
		if (bgcolor!=null && !bgcolor.equals("-1")) {
			response = response + " bgcolor=\"#" + bgcolor + "\"";
		}
		
		if (bordercolor!=null && !bordercolor.equals("-1")) {
			response = response + " bordercolor=\"#" + bordercolor + "\"";
		}
		
		response = response + " border=\"" + brd + "\" cellspacing=\"" + spc + "\" cellpadding=\"" + padd + "\">";
		for (int i = 0; i< line; i++) {
			response = response + "<tr>";
			for (int j = 0; j< cols; j++) {
				response = response + "<td>&nbsp;</td>";	
			}
			response = response + "</tr>";
		}
		
		return response;
	}
	

	@Override
	public String getHtmlBody(HttpServletRequest request, SnipArtifactForm frm) {
		StringBuffer sb = new StringBuffer();

		sb.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		sb.append("<tr class=\"pagingFormBody\">");
		sb.append("<td width=\"10\">&nbsp;</td>");
		sb.append("<td width=\"130\" class=\"formTitle\">" + super.getI18nMsg("label.artifactTag.snip.addTable.rows", handler.getLocale()) + ":&nbsp;</td>");
		sb.append("<td class=\"formBody\">" + HtmlUtil.getTextBox("snip_table_line", "1", false, "", 3, 6) + "</td>");
		sb.append("<td width=\"130\" class=\"formTitle\">" + super.getI18nMsg("label.artifactTag.snip.addTable.cols", handler.getLocale()) + ":&nbsp;</td>");
		sb.append("<td class=\"formBody\">" + HtmlUtil.getTextBox("snip_table_cols", "3", false, "", 2, 6) + "</td>");
		sb.append("<td width=\"10\">&nbsp;</td>");
		sb.append("</tr>");

		sb.append("<tr class=\"pagingFormBody\">");
		sb.append("<td>&nbsp;</td>");
		sb.append("<td class=\"formTitle\">" + super.getI18nMsg("label.artifactTag.snip.addTable.padd", handler.getLocale()) + ":&nbsp;</td>");
		sb.append("<td class=\"formBody\">" + HtmlUtil.getTextBox("snip_table_padd", "0", false, "", 3, 6) + "</td>");
		sb.append("<td class=\"formTitle\">" + super.getI18nMsg("label.artifactTag.snip.addTable.spc", handler.getLocale()) + ":&nbsp;</td>");
		sb.append("<td class=\"formBody\">" + HtmlUtil.getTextBox("snip_table_spc", "0", false, "", 2, 6) + "</td>");
		sb.append("<td>&nbsp;</td>");
		sb.append("</tr>");

		sb.append("<tr class=\"pagingFormBody\">");
		sb.append("<td>&nbsp;</td>");
		sb.append("<td class=\"formTitle\">" + super.getI18nMsg("label.artifactTag.snip.addTable.border", handler.getLocale()) + ":&nbsp;</td>");
		sb.append("<td class=\"formBody\">" + HtmlUtil.getTextBox("snip_table_brd", "1", false, "", 2, 6) + "</td>");
		sb.append("<td class=\"formTitle\">" + super.getI18nMsg("label.artifactTag.snip.addTable.bordercl", handler.getLocale()) + ":&nbsp;</td>");
		sb.append("<td class=\"formBody\">" + HtmlUtil.getColorComboBox("snip_table_brdcl", "textBoxSmall", 0, null, null, request) + "</td>");
		sb.append("<td>&nbsp;</td>");
		sb.append("</tr>");

		sb.append("<tr class=\"pagingFormBody\">");
		sb.append("<td>&nbsp;</td>");
		sb.append("<td class=\"formTitle\">" + super.getI18nMsg("label.artifactTag.snip.addTable.backg", handler.getLocale()) + ":&nbsp;</td>");
		sb.append("<td class=\"formBody\">" + HtmlUtil.getColorComboBox("snip_table_bg", "textBoxSmall", 0, null, null, request) + "</td>");
		sb.append("<td>&nbsp;</td>");
		sb.append("<td>&nbsp;</td>");		
		sb.append("</tr>");
		
		sb.append("</table>");
		
		return sb.toString();
	}
	
}
