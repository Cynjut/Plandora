package com.pandora.bus.snip;

import javax.servlet.http.HttpServletRequest;

import com.pandora.UserTO;
import com.pandora.gui.struts.form.SnipArtifactForm;
import com.pandora.helper.SessionUtil;

public class FlowChartSnipArtifact extends SnipArtifact {

	@Override
	public String getId() {
		return "snip_flow_chart";
	}

	@Override
	public String getUniqueName() {
		return "label.artifactTag.snip.addFlowChart";
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
	public String submit(HttpServletRequest request, SnipArtifactForm frm) {
		String response = "";
		return response;
	}
	
	@Override
	public String getHtmlBody(HttpServletRequest request, SnipArtifactForm frm) {
		StringBuffer sb = new StringBuffer();
		UserTO uto = SessionUtil.getCurrentUser(request);
		
		sb.append("<table width=\"100%\" height=\"250\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		sb.append("<tr class=\"pagingFormBody\">");		
		sb.append("<td width=\"300\" class=\"formBody\">\n");
		sb.append("  <canvas id=\"SNIP_FLOW_CANVAS\" width=\"350\" height=\"400\">\n");
		sb.append("  " + super.getI18nMsg("label.artifactTag.snip.addFlow.error", uto.getLocale()) + "\n");
		sb.append("  </canvas>\n");
		sb.append("</td>\n");
		sb.append("<td width=\"10\">&nbsp;</td>");		
		sb.append("<td class=\"formBody\">\n");
		sb.append("  <div id=\"SNIP_FLOW_CONTENT\" style=\"width:300px; height:400px; overflow: scroll;\">\n");
		sb.append("     <textarea id=\"SNIP_FLOW_TEXTAREA\" name=\"SNIP_FLOW_TEXTAREA\" rows=\"400\" cols=\"800\" onkeypress=\"return noEnter(event);\" class=\"textBox\"></textarea>" + "\n");
		sb.append("  </div>\n");
		sb.append("</td>\n");		
		sb.append("</tr>");		
		sb.append("</table>");

		sb.append("<script>\n");		
		sb.append("  function noEnter(e){\n");
		sb.append("     var e  = (e) ? e : ((event) ? event : null);\n");
		sb.append("     if (e.keyCode == 13) {\n");
		sb.append("        var txt = document.getElementById('SNIP_FLOW_TEXTAREA').value;\n");
		sb.append("        lines = txt.split(/\\r\\n|\\r|\\n/);");				
		sb.append("        initCanvas(lines, 450, 400);\n");		
		sb.append("     };\n");
		sb.append("  return true;}\n");
		sb.append("</script>\n");

		return sb.toString();
	}

}
