<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ page import="com.pandora.gui.struts.form.GeneralStrutsForm"%>

<jsp:include page="header.jsp" />

<%@ page import="com.pandora.PreferenceTO"%>

<script type="text/javascript" src="/jscript/jscolor.js"></script>

<script language="javascript">
     function remove(argId, argForm, argOperation){
         if ( confirm("<bean:message key="message.confirmRemoveUser"/>")) {
	         removeWithoutConfirm(argId, argForm, argOperation)
         }
     }
</script>

<html:form action="manageUser" enctype="multipart/form-data"> 
	<html:hidden name="userForm" property="operation"/>
	<html:hidden name="userForm" property="id"/>
	<html:hidden name="userForm" property="genericTag"/>
	
	<br>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.formUser"/>
	</display:headerfootergrid>
	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td>&nbsp;</td>
      <td>&nbsp; </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="180" class="formTitle"><bean:message key="label.name"/>:&nbsp;</td>
      <td class="formBody">
        <html:text name="userForm" property="name" styleClass="textBox" size="50" maxlength="50"/>
      </td>
      <td width="10">&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.userName"/>:&nbsp;</td>
      <td class="formBody">
        <html:text name="userForm" property="username" styleClass="textBox" size="10" maxlength="30"/>
      </td>
      <td>&nbsp;</td>
    </tr>    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.userCompany"/>:&nbsp;</td>
      <td class="formBody">
	  	<html:select name="userForm" property="userCompanyId" styleClass="textBox">
			<html:options collection="userCompany" property="id" labelProperty="name"/>
		</html:select>
      </td>
      <td>&nbsp;</td>
    </tr>    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.userDepartment"/>:&nbsp;</td>
      <td class="formBody">
	  	<html:select name="userForm" property="userDepartment" styleClass="textBox">
			<html:options collection="departmentList" property="id" labelProperty="name"/>
		</html:select>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.userArea"/>:&nbsp;</td>
      <td class="formBody">
	  	<html:select name="userForm" property="userArea" styleClass="textBox">
			<html:options collection="areaList" property="id" labelProperty="name"/>
		</html:select>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.userFunction"/>:&nbsp;</td>
      <td class="formBody">
	  	<html:select name="userForm" property="userFunction" styleClass="textBox">
			<html:options collection="functionList" property="id" labelProperty="name"/>
		</html:select>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.phone"/>:&nbsp;</td>
      <td class="formBody">
        <html:text name="userForm" property="phone" styleClass="textBox" size="20" maxlength="20"/>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.email"/>:&nbsp;</td>
      <td class="formBody">
        <html:text name="userForm" property="email" styleClass="textBox" size="70" maxlength="70"/>
      </td>
      <td>&nbsp;</td>
    </tr>    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.birth"/>:&nbsp;</td>
      <td class="formBody">
  	  	<plandora-html:calendar name="userForm" property="birth" styleClass="textBox" />
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.color"/>:&nbsp;</td>
      <td class="formBody">
<!-- Color Picker -->
        <html:text name="userForm" property="color" styleClass="textBox" size="10" maxlength="10"/>      
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.authMode"/>:&nbsp;</td>
      <td class="formBody">
			<html:select name="userForm" property="authenticationMode" styleClass="textBox">
				<html:options collection="authModeList" property="id" labelProperty="genericTag"/>
			</html:select>            
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.admission"/>:&nbsp;</td>
      <td class="formBody">
  	  	<plandora-html:calendar name="userForm" property="creationDate" styleClass="textBox" />
      </td>
      <td>&nbsp;</td>
    </tr>    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.userenabled"/>:&nbsp;</td>
      <td class="formBody">
		<html:select name="userForm" property="enableStatus" styleClass="textBox">
			<html:options collection="enableList" property="id" labelProperty="genericTag"/>
		</html:select>      
      </td>
      <td>&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.userpic"/>:&nbsp;</td>
      <td class="formBody">
		  	<logic:equal name="userForm" property="upload" value="true">
				<html:file name="userForm" property="theFile" styleClass="textBox"/>
				<bean:message key="label.userpic.maxSizeWarning"/>
		  	</logic:equal>
		  	<logic:equal name="userForm" property="upload" value="false">
		  		<br /><bean:write name="userForm" property="userPictureHtml" filter="false" /><br />
				<html:button property="clearpic" styleClass="button" onclick="javascript:buttonClick('userForm', 'clearPicture');">
					<bean:message key="label.userpic.clearpic"/>
			    </html:button>    
		  	</logic:equal>
      </td>
      <td>&nbsp;</td>
    </tr>
    
    <tr class="gapFormBody">
      <td>&nbsp;</td>
      <td>&nbsp; </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>      
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.permission"/>:&nbsp;</td>
      <td class="formBody">
      		<table width="30%" border="0" cellspacing="0" cellpadding="0">
      		<bean:write name="userForm" property="permissionListHtmlBody" filter="false" />
      		</table>
      </td>
      <td>&nbsp;</td>
    </tr>
        
    <tr class="gapFormBody">
      <td>&nbsp;</td>
      <td>&nbsp; </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>    
  	</table>
  
	<display:headerfootergrid type="FOOTER">  
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">
			  <html:button property="save" styleClass="button" onclick="javascript:buttonClick('userForm', 'saveUser');">
				<bean:write name="userForm" property="saveLabel" />
			  </html:button>    
		  </td>
		  <td width="120">
			  <html:button property="new" styleClass="button" onclick="javascript:buttonClick('userForm', 'clear');">
				<bean:message key="button.new"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
		  <td width="120">
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('userForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>    
		  </td>
		</tr></table>
	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
  
	<display:headerfootergrid width="100%" type="HEADER">
		&nbsp;
	</display:headerfootergrid>
  
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td width="10">&nbsp;</td>
      <td width="180">:&nbsp;</td>
      <td>&nbsp;</td>
      <td width="10">&nbsp;</td>
    </tr>	
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.password"/>:&nbsp;</td>
      <td class="formBody">
        <html:password name="userForm" property="password" styleClass="textBox" size="10" maxlength="10"/>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.passConfirmation"/>:&nbsp;</td>
      <td class="formBody">
        <html:password name="userForm" property="confirmation" styleClass="textBox" size="10" maxlength="10"/>
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="gapFormBody">
      <td>&nbsp;</td>
      <td>&nbsp; </td>
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>    
  	</table>

	<display:headerfootergrid type="FOOTER">  
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">
			  <logic:equal name="userForm" property="saveMethod" value="<%=GeneralStrutsForm.UPDATE_METHOD%>">
				  <html:button property="savePass" styleClass="button" onclick="javascript:buttonClick('userForm', 'changePassword');">
					<bean:message key="label.changePassword"/>
				  </html:button>    
			  </logic:equal>
		  </td>
		  <td>&nbsp;</td>
		</tr></table>
	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
  
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.userList"/>
	</display:headerfootergrid>
  
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>
			<plandora-html:ptable width="100%" name="userList" scope="session" pagesize="6" frm="userForm">
				  <plandora-html:pcolumn likeSearching="true" property="name" title="label.name" />
				  <plandora-html:pcolumn width="6%" likeSearching="true" sort="1" property="username" title="label.userName" />
				  <plandora-html:pcolumn width="5%" sort="1" property="color" title="label.colorGrid" decorator="com.pandora.gui.taglib.decorator.GridColorDecorator"/>		  
				  <plandora-html:pcolumn width="25%" sort="1" property="email" title="label.email" description="label.formRisk.status.desc" />	
				  <plandora-html:pcolumn width="10%" description="label.formRisk.impact.time" property="phone" title="label.phone" />
				  <plandora-html:pcolumn width="15%" property="finalDate" align="center" title="label.userdisabled" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator" tag="'userForm', 'editUser'" />
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDeleteDecorator" tag="'userForm', 'removeUser'" />
			</plandora-html:ptable>		
		</td>
	</tr> 
	</table>
		 
	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">      
			  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('userForm', 'refresh');">
				<bean:message key="button.refresh"/>
			  </html:button>    
		  </td>		
		  <td class="textBoxOverTitleArea">
	   		<html:checkbox property="hideDisableUsers" name="userForm" >
	   			<bean:message key="label.userform.hidedisable"/>
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
<script> document.forms["userForm"].name.focus(); </script>
