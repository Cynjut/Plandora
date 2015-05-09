package com.pandora.gui.taglib.decorator;

import java.util.Vector;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.LeaderTO;
import com.pandora.ProjectTO;
import com.pandora.RootTO;
import com.pandora.TemplateTO;
import com.pandora.delegate.TaskTemplateDelegate;
import com.pandora.helper.HtmlUtil;

public class ProjectGridByTemplate extends ColumnDecorator {


	public String decorate(Object columnValue) {
		String image = "&nbsp;";
		TaskTemplateDelegate ttdel = new TaskTemplateDelegate();
		
		try {
			ProjectTO pto = (ProjectTO)this.getObject();
			if (pto.getRoleIntoProject().equals(LeaderTO.ROLE_LEADER+"") || 
				    pto.getRoleIntoProject().equals(RootTO.ROLE_ROOT+"")){
				
				Vector list = ttdel.getTemplateListByProject(pto.getId(), false);
				
				if (list!=null && list.size()>0){

				    String altValue = this.getBundleMessage("label.resHome.accept.template");
				    String altDesc = this.getBundleMessage("label.resHome.accept.template.desc");
				    String altSelect = this.getBundleMessage("label.resHome.accept.template.select");
				    String cancelLabel = this.getBundleMessage("button.cancel");
				    String okLabel = this.getBundleMessage("button.ok");

					TemplateTO dummy = new TemplateTO("-1");
					dummy.setGenericTag(altSelect);
					list.add(0, dummy);
					
					String selectHtml = HtmlUtil.getComboBox("TEMPLATE_" + pto.getId(), list, "textBox", null);
					selectHtml = selectHtml.replaceAll("\"", "&quot;");

				    image ="<a href=\"#\" onclick=\"displayStaticMessage('<center><p class=&quot;gridBody&quot; style=&quot;text-align:center&quot;>" + altDesc + "</p>" + 
										selectHtml + "</p><input type=&quot;button&quot; value=&quot;  " + okLabel + "  &quot; class=&quot;button&quot; onclick=&quot;executeTemplateByProject(" + pto.getId() + ");closeMessage();&quot;>&nbsp;&nbsp;&nbsp;" +
										"<input type=&quot;button&quot; value=&quot;" + cancelLabel + "&quot; class=&quot;button&quot; onclick=&quot;closeMessage()&quot;></center>" +
									"', 350, 140);return false;\"> \n";
					image += "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/template.gif\" >";
					image += "</a>";						
				}				
			}
						
		} catch(Exception e) {
			image = "&nbsp;";
		}
		return image;
	}

	public String decorate(Object columnValue, String tag) {
		return decorate(columnValue);
	}

	
	public String contentToSearching(Object columnValue) {
		return columnValue+"";
	}
	
}
