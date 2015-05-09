package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

/**
 */
public class KbContentDecorator extends ColumnDecorator {
 
    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        String response = "";
        //StringBuffer strBuff = new StringBuffer(""); 
        /*
        try {
            KbDelegate kbdel = new KbDelegate();            
            KbDocumentTO kbDoc = (KbDocumentTO)this.getObject();
            Document doc = kbDoc.getDoc();
        
            String kbType = "?";
            String id = doc.get(KbIndex.KB_ID);
            KbIndex klass = kbdel.getKbByUniqueName(doc.get(KbIndex.KB_TYPE));
            if (klass!=null) {
                kbType = this.getBundleMessage(klass.getContextLabel(), true);
            }
            String content = doc.get(KbIndex.KB_CONTENT);
			*/
            /*
            strBuff.append("<table width=\"100%\" border=\"1\" cellspacing=\"0\" cellpadding=\"0\">");
            strBuff.append("<tr>");
            strBuff.append("<td class=\"tableCell\" width=\"5%\" align=\"center\" valign=\"top\"><img src=\"" + HtmlUtil.getEntityIcon("1") + "\" " + HtmlUtil.getHint(kbType) + " border=\"0\"></td>\n");
            strBuff.append("<td class=\"tableCell\" width=\"300\">" + id + "</td>\n");
            strBuff.append("<td class=\"tableCell\" >" + kbDoc.getProject().getName() + "</td>\n");
            strBuff.append("<td class=\"tableCell\" width=\"300\">" + DateUtil.getDateTime(kbDoc.getCreationDate(), this.getCurrentLocale(), 2, 2) + "</td>\n");
            strBuff.append("</tr>");

            strBuff.append("<tr>");
            strBuff.append("<td class=\"tableCell\" colspan=\"3\">" + content + "</td>");
            strBuff.append("</tr>");
            
            strBuff.append("</table>");
            */
            /*
	        response = response.concat("<table width=\"100%\" border=\"0\" cellspacing=\"0\" cellpadding=\"0\">");
	        response = response.concat("<tr");
	        response = response.concat("<td class=\"tableCell\" width=\"100\"><B>" + id + "</B></td>");
	        response = response.concat("<td class=\"tableCell\"><B>" + kbType + "</B></td>");
	        response = response.concat("<td class=\"tableCell\" width=\"300\"><B>" + kbDoc.getProject().getName() + "</B></td>");
	        response = response.concat("</tr>");

	        response = response.concat("<tr>");        
	        response = response.concat("<td class=\"tableCell\" colspan=\"3\">" + content + "</td>");
	        response = response.concat("</tr>");
	        response = response.concat("<tr>");        
	        response = response.concat("<td class=\"tableCell\" colspan=\"3\">&nbsp;</td>");
	        response = response.concat("</tr>");

	        response = response.concat("</table>");
            
	        //response = strBuff.toString();

        } catch (Exception e) {
            response = "&nbsp;"; 
        }
	        */
        return response;
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
        return decorate(columnValue);
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }
    
}
