package com.pandora.bus.snip;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import com.pandora.AttachmentTO;
import com.pandora.TransferObject;
import com.pandora.delegate.AttachmentDelegate;
import com.pandora.gui.struts.form.SnipArtifactForm;
import com.pandora.gui.taglib.decorator.AttachmentGridTypeDecorator;
import com.pandora.gui.taglib.form.NoteIcon;
import com.pandora.helper.HtmlUtil;
import com.pandora.helper.SessionUtil;

public class ImageSnipArtifact extends SnipArtifact {

	@Override
	public String getId() {
		return "snip_img";
	}

	@Override
	public String getUniqueName() {
		return "label.artifactTag.snip.addImage";
	}

	@Override
	public int getWidth() {
		return 500;
	}

	@Override
	public int getHeight() {
		return 300;
	}

	
	@Override
	public String refresh(HttpServletRequest request, SnipArtifactForm frm, String command) {
		String response = "";
		
		if (command.equals("opt_url")) {
			response = this.getHtmlFromUrl(request).toString();
			
		} else if (command.equals("opt_prj")) {
			response = this.getHtmlFromProject(request, frm).toString();
		}

		return response;
	}

	
	@Override
	public String submit(HttpServletRequest request, SnipArtifactForm frm) {
		String response = "";
		String optimg = request.getParameter("opt_img");
		if (optimg!=null) {

			if (optimg.equals("opt_url")) {
				String url = request.getParameter("snip_img_url");
				if (url!=null && !url.trim().equals("")) {
					response = "<img src=\"" + url + "\" border=\"0\" longdesc=\"\" />";
				}

			} else if (optimg.equals("opt_prj")) {
				String attId = request.getParameter("snip_img_attID");
				if (attId!=null && !attId.trim().equals("")) {
					String reqUri = request.getRequestURI();	
					reqUri = reqUri.substring(0, reqUri.indexOf("/do"));
					response = "<img src=\"" + SessionUtil.getUri(request) + reqUri + "/file?id=" + attId + "\" border=\"0\" longdesc=\"" + attId + "\" />";	
				}
			}
		}
		
		return response;
	}
	
	
	@Override
	public String getHtmlBody(HttpServletRequest request, SnipArtifactForm frm) {
		StringBuffer sb = new StringBuffer();

		Vector<TransferObject> list = new Vector<TransferObject>();
		list.add(new TransferObject("opt_url", super.getI18nMsg("label.artifactTag.snip.addimg.fromUrl", handler.getLocale())));
		list.add(new TransferObject("opt_prj", super.getI18nMsg("label.artifactTag.snip.addimg.fromPrj", handler.getLocale())));

		sb.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		sb.append("<tr class=\"pagingFormBody\">");
		sb.append("<td width=\"10\">&nbsp;</td>");
		sb.append("<td class=\"formBody\">" + HtmlUtil.getComboBox("opt_img", list, "textBox", "opt_prj", 0, "javascript:comboChoice();", false) + "</td>");				
		sb.append("<td width=\"10\">&nbsp;</td>");
		sb.append("</tr>");
		sb.append("</table>");
		
		sb.append("<table width=\"100%\" height=\"180\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		sb.append("<tr class=\"pagingFormBody\">");
		sb.append("<td width=\"10\">&nbsp;</td>");
		sb.append("<td class=\"formBody\">\n");
		sb.append("<div id=\"SNIP_OPT_HTML\">\n");
		sb.append(this.getHtmlFromProject(request, frm));
		sb.append("</div>\n");

		sb.append("<script>\n");		
		sb.append("  function comboChoice(){\n");
		sb.append("     var opt = document.getElementById('opt_img').value;\n");
		sb.append("     refreshSnip('" + this.getClass().getName() + "', opt, 'SNIP_OPT_HTML');\n");		
		sb.append("  }\n");
		sb.append("</script>\n");
				
		sb.append("</td>");
		sb.append("<td width=\"10\">&nbsp;</td>");
		sb.append("</tr>");
		
		sb.append("<tr class=\"pagingFormBody\" height=\"80%\">");
		sb.append("<td>&nbsp;</td>");
		sb.append("<td>&nbsp;</td>");
		sb.append("<td>&nbsp;</td>");
		sb.append("</tr>");
		
		sb.append("</table>");
		
		return sb.toString();
	}

	private StringBuffer getHtmlFromProject(HttpServletRequest request, SnipArtifactForm frm)  {
		StringBuffer sb = new StringBuffer();
		AttachmentDelegate adel = new AttachmentDelegate();
		AttachmentGridTypeDecorator dec = new AttachmentGridTypeDecorator();
		NoteIcon note = new NoteIcon();
		
		try {
			sb.append("<br>\n");
			
			sb.append("<input type=\"hidden\" value=\"\" id=\"snip_img_attID\" name=\"snip_img_attID\">\n");
			
			sb.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">\n");
			sb.append("<tr>\n");
			sb.append("<td valign=\"top\">\n");
			sb.append("<div id=\"snip_img_att_list\" style=\"width:100%; height:150px; overflow-y: scroll; overflow-x: hidden;\">");
			
			Vector<AttachmentTO> list = adel.getAttachmentByProject(frm.getProjectId());
			if (list!=null) {
				
				sb.append("<table width=\"100%\" class=\"table\" border=\"1\" cellspacing=\"1\" cellpadding=\"2\">");
				sb.append("<tr class=\"tableRowHeader\">");
				sb.append("<th width=\"20\" class=\"tableCellHeader\">&nbsp;</th>");				
				sb.append("<th width=\"20\" class=\"tableCellHeader\">&nbsp;</th>");
				sb.append("<th width=\"40\" class=\"tableCellHeader\">" + super.getI18nMsg("label.artifactTag.snip.addimg.ent", handler.getLocale()) + "</th>");
				sb.append("<th class=\"tableCellHeader\">" + super.getI18nMsg("label.artifactTag.snip.addimg.name", handler.getLocale()) + "</th>");
				sb.append("<th width=\"20\" class=\"tableCellHeader\">&nbsp;</th>");
				sb.append("</tr>");
				
				for (AttachmentTO ato : list) {
					
					if (ato.getType().indexOf("image")>-1 || ato.getType().indexOf("IMAGE")>-1) {						
						sb.append("<tr class=\"tableRowEven\">");
						boolean isSelected = (frm.getId()!=null&&frm.getId().equals(ato.getId()));
						sb.append("<td class=\"tableCell\">" + HtmlUtil.getRadioBox(isSelected, ato.getId(), false, "javascript:imgChoice(" + ato.getId() + ");") + "</td>");
						sb.append("<td class=\"tableCell\">" + dec.getTypeImg(ato) + "</td>");
						sb.append("<td class=\"tableCell\"><center>" + ato.getPlanning().getId() + "</center></td>");
						sb.append("<td class=\"tableCell\">" + ato.getName() + "</td>");
						if (ato.getComment()!=null && !ato.getComment().trim().equals("")) {
							sb.append("<td class=\"tableCell\">" + note.getContent(ato.getComment(), null) + "</td>");	
						} else {
							sb.append("<td class=\"tableCell\">&nbsp;</td>");
						}
						
						sb.append("</tr>");					
					}
					
				}
				sb.append("</table>");
				
				sb.append("<script>\n");		
				sb.append("  function imgChoice(attID){\n");
				sb.append("     var viewer = document.getElementById('snip_img_thumb');\n");
				sb.append("     var choice = document.getElementById('snip_img_attID');\n");
				sb.append("     choice.value = attID;\n");
				
				String reqUri = request.getRequestURI();	
				reqUri = reqUri.substring(0, reqUri.indexOf("/do"));
				sb.append("     viewer.src = '" + SessionUtil.getUri(request) + reqUri + "/file?id=' + attID;\n");
				
				sb.append("  }\n");
				sb.append("</script>\n");
			}
			sb.append("</div>");
			
			sb.append("</td>\n");
			sb.append("<td width=\"10\">&nbsp;</td>\n");			
			sb.append("<td width=\"150\"><img id=\"snip_img_thumb\" width=\"150\" height=\"150\" src=\"../images/empty.gif\" border=\"0\" />" +
					"</td>\n");
			sb.append("</tr></table>\n");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		return sb;
	}

	
	private StringBuffer getHtmlFromUrl(HttpServletRequest request) {
		StringBuffer sb = new StringBuffer();
		sb.append("<br>");
		
		sb.append("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
		sb.append("<tr class=\"pagingFormBody\">");
		sb.append("<td width=\"30\">&nbsp;</td>");
		sb.append("<td class=\"formBody\">" + super.getI18nMsg("label.artifactTag.snip.addimg.url", handler.getLocale()) + "</td>");
		sb.append("</tr>");
		sb.append("<tr class=\"pagingFormBody\">");
		sb.append("<td>&nbsp;</td>");
		sb.append("<td class=\"formBody\">" +  HtmlUtil.getTextBox("snip_img_url", "", false, "", 250, 75) + "</td>");
		sb.append("</tr>");			
		sb.append("</table>");

		return sb;
	}
	
}
