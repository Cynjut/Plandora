package com.pandora.bus.snip;

import javax.servlet.http.HttpServletRequest;

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
		return 300;
	}

	@Override
	public int getHeight() {
		return 150;
	}
	
	@Override
	public String submit(HttpServletRequest request) {
		
		int line = 1;
		int cols = 3;
		
		String lineStr = request.getParameter("new_table_line");
		String colsStr = request.getParameter("new_table_cols");
		if (lineStr!=null && !lineStr.trim().equals("")) {
			line = Integer.parseInt(lineStr);
		}
		if (colsStr!=null && !colsStr.trim().equals("")) {
			cols = Integer.parseInt(colsStr);
		}
		
		if (line<1 || line > 999) {
			line = 1;
		}
		if (cols<1 || cols > 99) {
			line = 1;
		}
		
		String response = "<table border=\"1\">";
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
	public String getHtmlBody(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();

		sb.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		sb.append("<tr class=\"pagingFormBody\">");
		sb.append("<td width=\"10\">&nbsp;</td>");
		sb.append("<td width=\"100\" class=\"formTitle\">" + super.getI18nMsg("label.artifactTag.snip.addTable.rows", null) + ":&nbsp;</td>");
		sb.append("<td class=\"formBody\">" + HtmlUtil.getTextBox("new_table_line", "1", false, "", 3, 6) + "</td>");
		sb.append("<td width=\"10\">&nbsp;</td>");
		sb.append("</tr>");
		sb.append("<tr class=\"pagingFormBody\">");
		sb.append("<td>&nbsp;</td>");
		sb.append("<td class=\"formTitle\">" + super.getI18nMsg("label.artifactTag.snip.addTable.cols", null) + ":&nbsp;</td>");
		sb.append("<td class=\"formBody\">" + HtmlUtil.getTextBox("new_table_cols", "3", false, "", 2, 6) + "</td>");
		sb.append("<td>&nbsp;</td>");
		sb.append("</tr>");		
		sb.append("</table>");
		
		return sb.toString();
	}
	
}
