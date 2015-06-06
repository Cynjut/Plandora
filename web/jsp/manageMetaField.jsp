<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<jsp:include page="header.jsp" />

<script language="javascript">
     function remove(argId, argForm, argOperation){
         if ( confirm("<bean:message key="message.confirmRemoveMetaField"/>")) {
	         removeWithoutConfirm(argId, argForm, argOperation)
         }
     }
     
     function changeProject(){
		 buttonClick("metaFieldForm", "refreshProject");
     }

     function changeApplyCombo(){
		 buttonClick("metaFieldForm", "refreshApplyCombo");
     }
     
    function disabledCheck(){
         with(document.forms["metaFieldForm"]){
         	if (isDisabledRequest.value=="on"){
         		isDisabledRequest.value = "";
         	} else {
         		isDisabledRequest.value = "on";
         	}
         }             
    }
</script>

<html:form  action="manageMetaField">
	<html:hidden name="metaFieldForm" property="operation"/>
	<html:hidden name="metaFieldForm" property="id"/>	
	<html:hidden name="metaFieldForm" property="isDisabledRequest"/>	
	<html:hidden name="metaFieldForm" property="isDisableCategory"/>		
	<html:hidden name="metaFieldForm" property="isDisableProject"/>		
	
	<br>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.formMetaField"/>
	</display:headerfootergrid>
	
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="170" class="formTitle"><bean:message key="label.formMetaField.name"/>:&nbsp;</td>
      <td class="formBody">
        <html:text name="metaFieldForm" property="name" styleClass="textBox" size="25" maxlength="20"/>
      </td>
      <td width="10">&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formMetaField.type"/>:&nbsp;</td>
      <td class="formBody">
	  	<html:select name="metaFieldForm" property="type" styleClass="textBox">
			<html:options collection="typeList" property="id" labelProperty="genericTag" filter="false"/>
		</html:select>
      </td>
      <td>&nbsp;</td>
    </tr>  
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formMetaField.applyTo"/>:&nbsp;</td>
      <td class="formBody">
	  	<html:select name="metaFieldForm" property="applyTo" styleClass="textBox" onkeypress="javascript:changeApplyCombo();" onchange="javascript:changeApplyCombo();">
			<html:options collection="applyToList" property="id" labelProperty="genericTag" filter="false"/>
		</html:select>
      </td>
      <td>&nbsp;</td>
    </tr>       
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formMetaField.project"/>:&nbsp;</td>
      <td class="formBody">
	  	<html:select name="metaFieldForm" property="projectId" styleClass="textBox" onkeypress="javascript:changeProject();" onchange="javascript:changeProject();">
			<html:options collection="projectList" property="id" labelProperty="name" filter="false"/>
		</html:select>
      </td>
      <td>&nbsp;</td>	  
    </tr>       
    <tr class="pagingFormBody">
	  <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formMetaField.category"/>:&nbsp;</td>
      <td class="formBody">
		  	<html:select name="metaFieldForm" property="categoryId" styleClass="textBox">
				<html:options collection="categoryList" property="id" labelProperty="name" filter="false"/>
			</html:select>
      </td>
      <td>&nbsp;</td>      
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formMetaField.domain"/>:&nbsp;</td>
      <td class="formBody">
      	<html:textarea name="metaFieldForm" property="domain" styleClass="textBox" cols="85" rows="5" />
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle"><bean:message key="label.formMetaField.help"/>:&nbsp;</td>
      <td class="formBody">
      	<html:textarea name="metaFieldForm" property="helpContent" styleClass="textBox" cols="85" rows="2" />
      </td>
      <td>&nbsp;</td>
    </tr>
    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="170" class="formTitle"><bean:message key="label.formMetaField.order"/>:&nbsp;</td>
      <td class="formBody">
        <html:text name="metaFieldForm" property="order" styleClass="textBox" size="5" maxlength="2"/>
      </td>
      <td width="10">&nbsp;</td>
    </tr>   

    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td width="170" class="formTitle"><bean:message key="label.formMetaField.ismandatory"/>:&nbsp;</td>
      <td class="formBody">
        <html:checkbox name="metaFieldForm" property="isMandatory" styleClass="textBox"/>
      </td>
      <td width="10">&nbsp;</td>
    </tr>  
	<html:hidden name="metaFieldForm" property="isMandatory" value="0"/>
	 
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle">&nbsp;</td>
      <td class="formBody">
	    <html:checkbox name="metaFieldForm" property="isQualifier" styleClass="textBox"/><bean:message key="label.formMetaField.isqualifier"/>
      </td>
      <td>&nbsp;</td>
    </tr>    
	<html:hidden name="metaFieldForm" property="isQualifier" value="0"/>
	    
    <tr class="pagingFormBody">
      <td>&nbsp;</td>
      <td class="formTitle">&nbsp;</td>
      <td class="formBody">
	    <input type="checkbox" name="isDisabled" onclick="javascript:disabledCheck();"/><bean:message key="label.formMetaField.disabled"/>
      </td>
      <td>&nbsp;</td>
    </tr>    
    <tr class="gapFormBody">
      <td colspan="4">&nbsp;</td>
    </tr>    
  	</table>
  
	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">
			  <html:button property="save" styleClass="button" onclick="javascript:buttonClick('metaFieldForm', 'saveMetaField');">
				<bean:write name="metaFieldForm" property="saveLabel" filter="false"/>
			  </html:button>    
		  </td>
		  <td width="120">
			  <html:button property="new" styleClass="button" onclick="javascript:buttonClick('metaFieldForm', 'clear');">
				<bean:message key="button.new"/>
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
		  <td width="120">
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('metaFieldForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>    
		  </td>
		</tr></table>
	</display:headerfootergrid> 
	
	<div>&nbsp;</div>
  
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:message key="title.MetaFieldList"/>
	</display:headerfootergrid>
  
	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr class="formBody">
		<td>
			<plandora-html:ptable width="100%" name="metaFieldList" scope="session" pagesize="8" frm="metaFieldForm">
				  <plandora-html:pcolumn property="name" likeSearching="true" title="label.name" />
				  <plandora-html:pcolumn width="20%" align="center" property="type" comboFilter="true" title="label.formMetaField.type" decorator="com.pandora.gui.taglib.decorator.GridMetaFieldDecorator" />
				  <plandora-html:pcolumn width="20%" align="center" property="applyTo" comboFilter="true" title="label.formMetaField.applyTo" decorator="com.pandora.gui.taglib.decorator.GridMetaFieldApplyToDecorator" />
				  <plandora-html:pcolumn width="20%" likeSearching="true" align="center" comboFilter="true" property="project.name" title="label.formMetaField.project" />
				  <plandora-html:pcolumn width="15%" property="disableDate" align="center" title="label.formMetaField.disabledDate" decorator="com.pandora.gui.taglib.decorator.GridDateDecorator" tag="2;2" />
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator" tag="'metaFieldForm', 'editMetaField'" />
				  <plandora-html:pcolumn width="2%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDeleteDecorator" tag="'metaFieldForm', 'removeMetaField'" />
			</plandora-html:ptable>		
		</td>
	</tr> 
	</table>

	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		<td width="120">      
		  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('metaFieldForm', 'refresh');">
		<bean:message key="button.refresh"/>
		  </html:button>    
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
	with(document.forms["metaFieldForm"]){	
		name.focus(); 
		isDisabled.checked=(isDisabledRequest.value=="on");	
		categoryId.disabled=(isDisableCategory.value=="on");	
		projectId.disabled=(isDisableProject.value=="on");
	}
</script>