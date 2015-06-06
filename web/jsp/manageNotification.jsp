<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<jsp:include page="header.jsp" />

<script language="javascript">
     function remove(argId, argForm, argOperation){
         if ( confirm("<bean:message key="message.confirmRemoveNotification"/>")) {
	         removeWithoutConfirm(argId, argForm, argOperation)
         }
     }
     
     function changeType(){
		 buttonClick("notificationForm", "refreshType");
     }     
</script>

<html:form action="manageNotification">
	<html:hidden name="notificationForm" property="operation"/>
	<html:hidden name="notificationForm" property="id"/>	
	<html:hidden name="notificationForm" property="fieldIdList"/>
	
	<br>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.formNotification"/>
	</display:headerfootergrid>
	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td colspan="6">&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="150" class="formTitle"><bean:message key="label.formNotification.name"/>:&nbsp;</td>
      <td width="170" class="formBody">
        <html:text name="notificationForm" property="name" styleClass="textBox" size="25" maxlength="20"/>
      </td>
      <td width="160">&nbsp;</td>
      <td>&nbsp;</td>
      <td width="10">&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formNotification.description"/>:&nbsp;</td>
      <td class="formBody">
		<html:text name="notificationForm" property="description" styleClass="textBox" size="85" maxlength="100"/>
      </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    </table>
    
    <table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="150" class="formTitle"><bean:message key="label.formNotification.enabled"/>:&nbsp;</td>
      <td width="170" class="formBody">
		<html:select name="notificationForm" property="enableStatus" styleClass="textBox">
			<html:options collection="enableList" property="id" labelProperty="genericTag" filter="false"/>
		</html:select>      
      </td>
      <td width="10">&nbsp;</td>
      <td width="160" class="formTitle"><bean:message key="label.formNotification.type"/>:&nbsp;</td>      
      <td class="formBody">
		<html:select name="notificationForm" property="type" styleClass="textBox" onkeypress="javascript:changeType();" onchange="javascript:changeType();">
			<html:options collection="typeList" property="id" labelProperty="genericTag" filter="false"/>
		</html:select>      
      </td>
      <td width="10">&nbsp;</td>
    </tr>
  	</table>

    <table width="98%" border="0" cellspacing="0" cellpadding="0">    
	    <bean:write name="notificationForm" property="fieldsHtml" filter="false"/>
  	</table>      	

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="150" class="formTitle"><bean:message key="label.formNotification.sql"/>:&nbsp;</td>
      <td class="formBody">
		<html:textarea name="notificationForm" property="textQuery" styleClass="textBox" cols="86" rows="4" />
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="formNotes">
      <td>&nbsp;</td>
      <td>&nbsp;</td>
      <td><div>&nbsp;&nbsp;*&nbsp;<bean:message key="title.formNotification.sql.help"/></div></td>
      <td>&nbsp;</td>
    </tr>
    </table>      	
    
    
	<table width="98%" border="0" cellspacing="0" cellpadding="0">    
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="150" class="formTitle"><bean:message key="label.formNotification.retry"/>:&nbsp;</td>
      <td width="130" class="formBody">
        <html:text name="notificationForm" property="retryNumber" styleClass="textBox" size="10" maxlength="10"/>
      </td>
      <td width="100">&nbsp;</td>      
      <td width="50">&nbsp;</td>
      <td width="100">&nbsp;</td>      
      <td width="50">&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formNotification.periodicity"/>:&nbsp;</td>
      <td class="formBody">
		<html:select name="notificationForm" property="periodicity" styleClass="textBox">
			<html:options collection="periodicityList" property="id" labelProperty="genericTag" filter="false"/>
		</html:select>              
      </td>
      <td class="formTitle"><bean:message key="label.formNotification.hour"/>:&nbsp;</td>      
      <td class="formTitle">
      	<html:text name="notificationForm" property="hour" styleClass="textBox" size="4" maxlength="4"/>
      </td>
      <td class="formTitle"><bean:message key="label.formNotification.minute"/>:&nbsp;</td>      
      <td class="formTitle">
      	<html:text name="notificationForm" property="minute" styleClass="textBox" size="4" maxlength="4"/>
      </td>
      <td>&nbsp;</td>
    </tr>
	<tr class="gapFormBody">
  		<td colspan="8">&nbsp;</td>
	</tr>                
    </table>
  
	<display:headerfootergrid type="FOOTER">  
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">
			  <html:button property="save" styleClass="button" onclick="javascript:buttonClick('notificationForm', 'saveNotification');">
				<bean:write name="notificationForm" property="saveLabel" filter="false"/>
			  </html:button>    
		  </td>
		  <td width="120">
			  <html:button property="new" styleClass="button" onclick="javascript:buttonClick('notificationForm', 'clear');">
				<bean:message key="button.new"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
		  <td width="120">
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('notificationForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>    
		  </td>
		</tr></table>
	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.NotificationList"/>
	</display:headerfootergrid>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>
			<plandora-html:ptable width="100%" name="notificationList" scope="session" pagesize="6" frm="notificationForm">
				  <plandora-html:pcolumn width="15%" property="name" likeSearching="true" title="label.name" />
				  <plandora-html:pcolumn property="description" likeSearching="true" title="label.formNotification.description" />
				  <plandora-html:pcolumn width="15%" property="nextNotification" align="center" title="label.formNotification.nextNotific" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />	
				  <plandora-html:pcolumn width="15%" property="finalDate" align="center" title="label.formNotification.disabledDate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />					  
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator" tag="'notificationForm', 'editNotification'" />
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDeleteDecorator" tag="'notificationForm', 'removeNotification'" />
			</plandora-html:ptable>		
		</td>
	</tr> 
	</table>

	<display:headerfootergrid type="FOOTER">  	
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>		
		<td width="120">      
		  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('notificationForm', 'refresh');">
			<bean:message key="button.refresh"/>
		  </html:button>    
		</td>
		<td class="textBoxOverTitleArea">
		 <html:checkbox property="hideClosedAgents" name="notificationForm" >
		  	<bean:message key="label.formNotification.hideClosed"/>
		 </html:checkbox>
		</td>	   				
		<td>&nbsp;</td>
		</tr></table>  	
	</display:headerfootergrid> 

  </td><td width="10">&nbsp;</td>
  </tr></table>

</html:form>

<jsp:include page="footer.jsp" />

<!-- End of source-code -->
<script> 
	with(document.forms["notificationForm"]){	
		name.focus(); 
	}
</script>  