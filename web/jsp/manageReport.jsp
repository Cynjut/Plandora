<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<jsp:include page="header.jsp" />

<script language="javascript">

	function remove(argId, argForm, argOperation){
    	with(document.forms["reportForm"]){	
	      <logic:equal name="reportForm" property="isKpiForm" value="on">	      
				if ( confirm("<bean:message key="message.confirmCloseReport"/>")) {
	      </logic:equal>
	      <logic:equal name="reportForm" property="isKpiForm" value="off">	      
				if ( confirm("<bean:message key="message.confirmCloseReport.Report"/>")) {
	      </logic:equal>		      
	        	removeWithoutConfirm(argId, argForm, argOperation);
        	}    	
		}
	}
	
     function changeProject(){
		 buttonClick("reportForm", "refreshCategory");
     }     
	
	
</script>

<html:form  action="manageReport">

	<html:hidden name="reportForm" property="operation"/>
	<html:hidden name="reportForm" property="id"/>
	<html:hidden name="reportForm" property="lastExecution"/>
	
	<br>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>

		<display:headerfootergrid width="100%" type="HEADER">
		      <logic:equal name="reportForm" property="isKpiForm" value="on">	      
		          <bean:message key="title.manageReport"/>	
		      </logic:equal>
		      <logic:equal name="reportForm" property="isKpiForm" value="off">	      
		          <bean:message key="title.manageReport.Report"/>
		      </logic:equal>		      
		</display:headerfootergrid>
		
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="gapFormBody">
	      <td width="10">&nbsp;</td>
	      <td width="150">&nbsp; </td>
	      <td width="195">&nbsp;</td>
		  <td width="100">&nbsp; </td>
		  <td>&nbsp; </td>		  
	      <td width="10">&nbsp;</td>
	    </tr>
	    <tr class="pagingFormBody">
	      <td>&nbsp;</td>
	      <td class="formTitle"><bean:message key="label.manageReport.name"/>:&nbsp;</td>
	      <td colspan="3" class="formBody">
	        <html:text name="reportForm" property="name" styleClass="textBox" size="89" maxlength="100"/>
	      </td>
	      <td>&nbsp;</td>
	    </tr>
        <logic:equal name="reportForm" property="isKpiForm" value="on">
		    <tr class="pagingFormBody">	
	    	   <td>&nbsp;</td>
	       	   <td class="formTitle"><bean:message key="label.manageReport.type"/>:&nbsp;</td>    
		       <td class="formBody">
			  		<html:select name="reportForm" property="type" styleClass="textBox">
			             <option value="1"><bean:message key="label.manageReport.type.1"/></option>		  		
					</html:select>
	       	   </td>
	       	   <td class="formTitle"><bean:message key="label.manageReport.persp"/>:&nbsp;</td>
			   <td class="formBody">
			   		<html:select name="reportForm" property="reportPerspectiveId" styleClass="textBox">
						<html:options collection="perspectiveList" property="id" labelProperty="genericTag"/>
					</html:select>
	       	   </td>       	   
	           <td>&nbsp;</td>
		    </tr>
        </logic:equal>	    
	    <tr class="pagingFormBody">
	      <td>&nbsp;</td>
	      <td class="formTitle"><bean:message key="label.manageReport.sql"/>:&nbsp;</td>
	      <td colspan="3" class="formBody">
    	     <html:textarea name="reportForm" property="sqlStement" styleClass="textBox" cols="86" rows="5" />
	      </td>
	      <td>&nbsp;</td>
	    </tr>
	    <logic:equal name="reportForm" property="isKpiForm" value="on">
		    <tr class="formNotes">
		      <td>&nbsp;</td>
		      <td>&nbsp;</td>
		      <td colspan="3"><div>&nbsp;&nbsp;**&nbsp;<bean:message key="label.manageReport.sql.help"/></div></td>
		      <td>&nbsp;</td>
		    </tr>
	    </logic:equal>

	    <tr class="pagingFormBody">	
    	   <td>&nbsp;</td>
       	   <td class="formTitle"><bean:message key="label.manageReport.project"/>:&nbsp;</td>    
	       <td class="formBody">
		  		<html:select name="reportForm" property="projectId" styleClass="textBox" onkeypress="javascript:changeCategory();" onchange="javascript:changeCategory();">
		             <html:options collection="projectList" property="id" labelProperty="name"/>
				</html:select>
       	   </td> 
	       <logic:equal name="reportForm" property="isKpiForm" value="on">
	       	   <td class="formTitle"><bean:message key="label.manageReport.execHour"/>:&nbsp;</td>    
		       <td class="formBody">
			  		<html:select name="reportForm" property="executionHour" styleClass="textBox">
			             <html:options collection="hoursList" property="id" labelProperty="genericTag"/>		  		
					</html:select>
	       	   </td>
	       </logic:equal>
	       <logic:equal name="reportForm" property="isKpiForm" value="off">
       		   <td>&nbsp;</td>
		       <td>&nbsp;</td>
	       </logic:equal>       	   
           <td>&nbsp;</td>
	    </tr>	    
        <logic:equal name="reportForm" property="isKpiForm" value="on">	    
		    <tr class="pagingFormBody">	
	    	   <td>&nbsp;</td>
	       	   <td class="formTitle"><bean:message key="label.manageReport.dataType"/>:&nbsp;</td>    
		       <td class="formBody">
			  		<html:select name="reportForm" property="dataType" styleClass="textBox">
			             <html:options collection="dataTypeList" property="id" labelProperty="genericTag"/>		  		
					</html:select>
	       	   </td>
	           <td colspan="3">&nbsp;</td>
		    </tr>	    
		    <tr class="pagingFormBody">	
	    	   <td>&nbsp;</td>
	       	   <td class="formTitle"><bean:message key="label.manageReport.goal"/>:&nbsp;</td>    
		       <td class="formBody">
		       		<html:text name="reportForm" property="goal" styleClass="textBox" size="10" maxlength="15"/>
	       	   </td>
	       	   <td class="formTitle"><bean:message key="label.manageReport.tolerance"/>:&nbsp;</td>       
		       <td class="formBody">
			  		<html:select name="reportForm" property="toleranceScale" styleClass="textBox">
			             <html:options collection="toleranceScaleList" property="id" labelProperty="genericTag"/>		  		
					</html:select>
					&nbsp;
		       		<html:text name="reportForm" property="tolerance" styleClass="textBox" size="6" maxlength="15"/>
		       		&nbsp;
			  		<html:select name="reportForm" property="toleranceUnit" styleClass="textBox">
			             <html:options collection="toleranceUnitList" property="id" labelProperty="genericTag"/>		  		
					</html:select>
	       	   </td>	       	   
	           <td>&nbsp;</td>
		    </tr>	    
	 	</logic:equal>		    	    
        <logic:equal name="reportForm" property="isKpiForm" value="off">	    
		    <tr class="pagingFormBody">	
	    	   <td>&nbsp;</td>
	       	   <td class="formTitle"><bean:message key="label.manageReport.filename"/>:&nbsp;</td>    
		       <td class="formBody">
		       		<html:text name="reportForm" property="reportFileName" styleClass="textBox" size="89" maxlength="100"/>
	       	   </td>
	           <td colspan="3">&nbsp;</td>
		    </tr>	    
	 	</logic:equal>		 	 	   	    
	    <tr class="pagingFormBody">	
    	   <td>&nbsp;</td>
       	   <td class="formTitle"><bean:message key="label.manageReport.category"/>:&nbsp;</td>    
	       <td class="formBody">
		  		<html:select name="reportForm" property="categoryId" styleClass="textBox">
		             <html:options collection="categoryList" property="id" labelProperty="name"/>
				</html:select>
       	   </td> 
       	   <td colspan="2">&nbsp;</td>
           <td>&nbsp;</td>
	    </tr>	    
        <logic:equal name="reportForm" property="isKpiForm" value="off">
	        <tr class="pagingFormBody">	
    	    <td>&nbsp;</td>
       	    <td class="formTitle"><bean:message key="label.manageReport.role"/>:&nbsp;</td>
		    <td class="formBody">
				<html:select name="reportForm" property="profile" styleClass="textBox">
			        <html:options collection="profileList" property="id" labelProperty="genericTag"/>		  		
				</html:select>
	       	</td>
       	    <td colspan="2">&nbsp;</td>			
            <td>&nbsp;</td>			
			</tr>
	    </logic:equal>       	   
	 	
	    <tr class="gapFormBody">
	      <td colspan="6">&nbsp;</td>
	    </tr>	    
	    </table>

        <logic:equal name="reportForm" property="isKpiForm" value="off">
			<table width="98%" border="0" cellspacing="0" cellpadding="0">   	    
		    <tr class="formNotes">
	    	  <td><div>&nbsp;&nbsp;*&nbsp;<bean:message key="label.viewReport.note"/></div></td>
	    	</tr>	    
	    	<tr class="gapFormBody">
	      		<td>&nbsp;</td>
		    </tr>	        		    	
		  	</table>
		  	
			<table width="98%" border="0" cellspacing="0" cellpadding="0">   	    
		    <tr class="formNotes">
	    	  <td colspan="2"><div>&nbsp;&nbsp;**&nbsp;<bean:message key="label.viewReport.sqlNote"/></div></td>
	    	</tr>	    
		    <tr class="formNotes">
	    	  <td width="200" align="right" class="formNotes"><li><b><bean:message key="label.viewReport.sqlNote.0"/></b></li></td>
	    	  <td class="code">&nbsp;&nbsp;<bean:message key="label.viewReport.sqlNote.0.exp"/></td>
	    	</tr>	    
		    <tr class="formNotes">
	    	  <td align="right" class="formNotes"><li><b><bean:message key="label.viewReport.sqlNote.1"/></b></li></td>
	    	  <td class="code">&nbsp;&nbsp;<bean:message key="label.viewReport.sqlNote.1.exp"/></td>
	    	</tr>	    
		    <tr class="formNotes">
	    	  <td align="right" class="formNotes"><li><b><bean:message key="label.viewReport.sqlNote.2"/></b></li></td>
	    	  <td class="code">&nbsp;&nbsp;<bean:message key="label.viewReport.sqlNote.2.exp"/></td>
	    	</tr>	    
		    <tr class="formNotes">
	    	  <td align="right" class="formNotes"><li><b><bean:message key="label.viewReport.sqlNote.3"/></b></li></td>
	    	  <td class="code">&nbsp;&nbsp;<bean:message key="label.viewReport.sqlNote.3.exp"/></td>
	    	</tr>	    
		    <tr class="formNotes">
	    	  <td align="right" class="formNotes"><li><b><bean:message key="label.viewReport.sqlNote.4"/></b></li></td>
	    	  <td class="code">&nbsp;&nbsp;<bean:message key="label.viewReport.sqlNote.4.exp"/></td>
	    	</tr>	    	    	
	    	<tr class="gapFormBody">
	      		<td colspan="2">&nbsp;</td>
		    </tr>	        	
		  	</table>

			<table width="98%" border="0" cellspacing="0" cellpadding="0">   	    
		    <tr class="formNotes">
	    	  <td colspan="2"><div>&nbsp;&nbsp;&nbsp;<bean:message key="label.viewReport.sqlNote.hint"/></div></td>
	    	</tr>	    
		    <tr class="formNotes">
		      <td width="40">&nbsp;</td>
	    	  <td class="formNotes">
	    	  	<li><bean:message key="label.viewReport.sqlNote.hint.1"/></li>
	    	  	<li><bean:message key="label.viewReport.sqlNote.hint.2"/></li>
	    	  	<li><bean:message key="label.viewReport.sqlNote.hint.3"/></li>
	    	  	<li><bean:message key="label.viewReport.sqlNote.hint.4"/></li>
	    	  	<li><bean:message key="label.viewReport.sqlNote.hint.5"/></li>
	    	  	<li><bean:message key="label.viewReport.sqlNote.hint.6"/></li>	    	  	
	    	  </td>
	    	</tr>	    
	    	<tr class="gapFormBody">
	      		<td colspan="2">&nbsp;</td>
		    </tr>	        	
		  	</table>
	 	</logic:equal>

		<display:headerfootergrid type="FOOTER">
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120">
				  <html:button property="save" styleClass="button" onclick="javascript:buttonClick('reportForm', 'saveReport');">
					<bean:write name="reportForm" property="saveLabel" />
				  </html:button>    
			  </td>
			  <td width="120">
				  <html:button property="new" styleClass="button" onclick="javascript:buttonClick('reportForm', 'clear');">
					<bean:message key="button.new"/>
				  </html:button>    
			  </td>
			  <td>&nbsp;</td>
			  <td width="120">
				  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('reportForm', 'backward');">
					<bean:message key="button.backward"/>
				  </html:button>    
			  </td>
			</tr></table>
		</display:headerfootergrid> 
	
		<div>&nbsp;</div>
	
		<display:headerfootergrid width="100%" type="HEADER">
		      <logic:equal name="reportForm" property="isKpiForm" value="on">	      
			      <bean:message key="title.manageReport.reportList"/>
		      </logic:equal>
		      <logic:equal name="reportForm" property="isKpiForm" value="off">
			      <bean:message key="title.manageReport.Report.reportList"/>
		      </logic:equal>	      
		</display:headerfootergrid>
	    
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formBody">
			<td>
				<display:table border="1" width="100%" name="reportList" scope="session" pagesize="10">
					  <display:column property="name" likeSearching="true" title="label.manageReport.name" />
					  <display:column width="15%" likeSearching="true" property="project.name" align="center" title="label.manageReport.project" />
					  <display:column width="15%" likeSearching="true" property="category.name" align="center" title="label.manageReport.category" />					  
				      <logic:equal name="reportForm" property="isKpiForm" value="on">	      
   						  <display:column width="10%" property="goal" align="center" title="label.manageReport.goal" />
						  <display:column width="15%" property="lastExecution" align="center" title="label.manageReport.lastExec" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />
						  <display:column width="15%" property="finalDate" align="center" title="label.manageReport.disabled" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />
				      </logic:equal>
					  <display:column width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator" tag="'reportForm', 'editReport'" />
					  <display:column width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDeleteDecorator" tag="'reportForm', 'closeReport'" />						  
				</display:table>		
			</td>
		</tr> 
		</table>

		<display:headerfootergrid type="FOOTER">		
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120">      
				  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('reportForm', 'refresh');">
					<bean:message key="button.refresh"/>
				  </html:button>    
			  </td>
			  <td>&nbsp;</td>
			</tr></table>  	
		</display:headerfootergrid>
		
	</td><td width="20">&nbsp;</td>
				
  	</tr></table>
</html:form>

<jsp:include page="footer.jsp" />

<!-- End of source-code -->
<script> 
   	with(document.forms["reportForm"]){	
   		name.focus();	
	}
</script>