package com.pandora.gui.taglib.decorator;

import org.apache.taglibs.display.ColumnDecorator;

import com.pandora.RiskHistoryTO;
import com.pandora.RiskTO;
import com.pandora.TransferObject;
import com.pandora.helper.HtmlUtil;


public class RiskHistoryGridStatusDecorator extends ColumnDecorator {

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object)
     */
    public String decorate(Object columnValue) {
        return decorate(columnValue, null);    	
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#decorate(java.lang.Object, java.lang.String)
     */
    public String decorate(Object columnValue, String tag) {
		String response = "&nbsp;";
		
		TransferObject to = (TransferObject)this.getObject();
		if (to!=null && tag!=null){
			
			if (tag.equals("PROB") && this.getProbability(to)!=null ) {
				response = this.getBundleMessage("label.formRisk.probability." + this.getProbability(to));
				
			} else if (tag.equals("IMPA") && this.getImpact(to)!=null) {
				response = this.getBundleMessage("label.formRisk.impact." + this.getImpact(to));

			} else if (tag.equals("TEND") && this.getTendency(to)!=null) {
				response = this.getBundleMessage("label.formRisk.tendency." + this.getTendency(to));

			} else if (tag.equals("TYPE") && this.getType(to)!=null) {
				response = this.getBundleMessage("label.formRisk.type." + this.getType(to));
				
			} else if (tag.equals("I_COST")) {
				if (this.getImpact(to, tag)) {
					String altValue = this.getBundleMessage("label.formRisk.impact.cost");
					response = "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/tick.png\" >";	
				}
				
			} else if (tag.equals("I_QUAL")) {
				if (this.getImpact(to, tag)) {
					String altValue = this.getBundleMessage("label.formRisk.impact.qual");
					response = "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/tick.png\" >";	
				}

			} else if (tag.equals("I_SCOP")) {
				if (this.getImpact(to, tag)) {
					String altValue = this.getBundleMessage("label.formRisk.impact.scop");
					response = "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/tick.png\" >";	
				}
				
			} else if (tag.equals("I_TIME")) {
				if (this.getImpact(to, tag)) {
					String altValue = this.getBundleMessage("label.formRisk.impact.time");
					response = "<img border=\"0\" " + HtmlUtil.getHint(altValue) + " src=\"../images/tick.png\" >";	
				}
			}			
		}
		return response;
    }

    /* (non-Javadoc)
     * @see org.apache.taglibs.display.ColumnDecorator#contentToSearching(java.lang.Object)
     */
    public String contentToSearching(Object columnValue) {
    	return columnValue+"";
    }
	
    
    private String getProbability(TransferObject to){
    	String response = null;
    	if (to instanceof RiskHistoryTO) {
    		response = ((RiskHistoryTO)to).getProbability();
    	} else if (to instanceof RiskTO) {
    		response = ((RiskTO)to).getProbability();
    	}
    	return response;
    }
    
    
    private String getImpact(TransferObject to){
    	String response = null;    	
    	if (to instanceof RiskHistoryTO) {
    		response = ((RiskHistoryTO)to).getImpact();
    	} else if (to instanceof RiskTO) {
    		response = ((RiskTO)to).getImpact();
    	}
    	return response;
    }


    private String getTendency(TransferObject to){
    	String response = null;
    	if (to instanceof RiskHistoryTO) {
    		response = ((RiskHistoryTO)to).getTendency();
    	} else if (to instanceof RiskTO) {
    		response = ((RiskTO)to).getTendency();
    	}
    	return response;    	
    }
    

    private Integer getType(TransferObject to){
    	Integer response = null;
    	if (to instanceof RiskHistoryTO) {
    		response = ((RiskHistoryTO)to).getRiskType();
    	} else if (to instanceof RiskTO) {
    		response = ((RiskTO)to).getRiskType();
    	}
    	return response;    	
    }
    
    
    private boolean getImpact(TransferObject to, String tag) {
    	boolean response = false;
    	
    	if (tag.equals("I_COST")) {
        	if (to instanceof RiskHistoryTO) {
        		response = ((RiskHistoryTO)to).getCostImpact();
        	} else if (to instanceof RiskTO) {
        		response = ((RiskTO)to).getCostImpact();
        	}
        	
    	} else if (tag.equals("I_QUAL")) {
        	if (to instanceof RiskHistoryTO) {
        		response = ((RiskHistoryTO)to).getQualityImpact();
        	} else if (to instanceof RiskTO) {
        		response = ((RiskTO)to).getQualityImpact();
        	}    		
    	
    	} else if (tag.equals("I_SCOP")) {
        	if (to instanceof RiskHistoryTO) {
        		response = ((RiskHistoryTO)to).getScopeImpact();
        	} else if (to instanceof RiskTO) {
        		response = ((RiskTO)to).getScopeImpact();
        	}    		    		
    		
    	} else if (tag.equals("I_TIME")) {
        	if (to instanceof RiskHistoryTO) {
        		response = ((RiskHistoryTO)to).getTimeImpact();
        	} else if (to instanceof RiskTO) {
        		response = ((RiskTO)to).getTimeImpact();
        	}    		    		
    	}
    	return response;    	
    }
    
    
}
