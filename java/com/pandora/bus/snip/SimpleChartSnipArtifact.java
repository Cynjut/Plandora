package com.pandora.bus.snip;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.pandora.TransferObject;
import com.pandora.gui.struts.form.SnipArtifactForm;
import com.pandora.helper.HtmlUtil;

public class SimpleChartSnipArtifact extends SnipArtifact {

	@Override
	public String getId() {
		return "snip_simple_chart";
	}

	@Override
	public String getUniqueName() {
		return "label.artifactTag.snip.addSimpleChart";
	}

	@Override
	public int getWidth() {
		return 700;
	}

	@Override
	public int getHeight() {
		return 500;
	}

	
	@Override
	public String refresh(HttpServletRequest request, SnipArtifactForm frm, String command) {
		String response = "";
		
		if (command.equals("opt_chart")) {
			
		} else if (command.equals("opt_chart")) {
			response = this.getChartPreview(request, frm).toString();
		}

		return response;
	}	
	
	@Override
	public String submit(HttpServletRequest request, SnipArtifactForm frm) {
		String response = "";
		return response;
	}

	
	@Override
	public String getHtmlBody(HttpServletRequest request, SnipArtifactForm frm) {
		StringBuffer sb = new StringBuffer();

		Vector<TransferObject> list = new Vector<TransferObject>();
		list.add(new TransferObject("opt_pie", super.getI18nMsg("label.artifactTag.snip.addSimpleChart.pie", handler.getLocale())));
		list.add(new TransferObject("opt_bar", super.getI18nMsg("label.artifactTag.snip.addSimpleChart.bar", handler.getLocale())));
		list.add(new TransferObject("opt_org", super.getI18nMsg("label.artifactTag.snip.addSimpleChart.org", handler.getLocale())));

		sb.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		sb.append("<tr class=\"pagingFormBody\">");
		sb.append("<td width=\"10\">&nbsp;</td>");
		sb.append("<td class=\"formBody\">" + HtmlUtil.getComboBox("opt_chart", list, "textBox", "opt_pie", 0, "javascript:comboChoice();", false) + "</td>");				
		sb.append("<td width=\"10\">&nbsp;</td>");
		sb.append("</tr>");
		sb.append("</table></br>");

		sb.append("<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");

		sb.append("<tr class=\"pagingFormBody\">");
		sb.append("<td width=\"250\">" + this.getPropertiesFields() + "</td>");
		sb.append("<td rowspan=\"2\" class=\"formBody\" width=\"400\">");
		sb.append("<div id=\"SNIP_OPT_HTML\">\n");
		sb.append(this.getChartPreview(request, frm));
		sb.append("</div>\n");
		sb.append("</td>\n");
		sb.append("</tr>\n");
		
		sb.append("<tr class=\"pagingFormBody\">");
		sb.append("<td>" + this.getSourceFields() + "</td>");
		sb.append("</tr>\n");
		
		
		return sb.toString();
	}

	
	private StringBuffer getSourceFields() {
		StringBuffer sb = new StringBuffer();

		return sb;
	}

	
	private StringBuffer getPropertiesFields() {
		StringBuffer sb = new StringBuffer();

		sb.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		
		sb.append("<tr class=\"pagingFormBody\">");
		sb.append("<td width=\"80\" class=\"formTitle\">Legenda:&nbsp;</td>");
		sb.append("<td colspan=\"3\" class=\"formBody\">" + HtmlUtil.getTextBox("opt_leg", "", false, null, 50, 30) + "&nbsp;</td>");
		sb.append("</tr>\n");

		sb.append("<tr class=\"pagingFormBody\">");
		sb.append("<td width=\"80\" class=\"formTitle\">Width:&nbsp;</td>");
		sb.append("<td width=\"70\" class=\"formBody\">" + HtmlUtil.getTextBox("opt_wdt", "", false, null, 5, 8) + "&nbsp;</td>");
		sb.append("<td width=\"80\" class=\"formTitle\">Height:&nbsp;</td>");
		sb.append("<td width=\"70\" class=\"formBody\">" + HtmlUtil.getTextBox("opt_hgt", "", false, null, 5, 8) + "&nbsp;</td>");
		sb.append("</tr>\n");

		sb.append("</table>\n");

		return sb;
	}

	
	private String getChartPreview(HttpServletRequest request,
			SnipArtifactForm frm) {
		return "";
	}
}

