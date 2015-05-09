<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>

<jsp:include page="header.jsp" />

<script language="javascript">

	function remove(argId, argForm, argOperation){
    	with(document.forms["categoryForm"]){	
			if ( confirm("<bean:message key="message.category.confirmRemove"/>")) {
	        	removeWithoutConfirm(argId, argForm, argOperation);
        	}    	
		}
	}
	
	function changeType(){
		with(document.forms["categoryForm"]){
			if (type.value=='0') { 
    			isBillableTask.className='textBox';
    			isBillableTask.disabled = "";
    			
    			isDefectTask.className='textBox';
    			isDefectTask.disabled = "";
    			
    			isTestingTask.className='textBox';
    			isTestingTask.disabled = "";

    			isDevelopingTask.className='textBox';
    			isDevelopingTask.disabled = "";
    			
			} else {
    			isBillableTask.className='textBoxDisabled';
	   			isBillableTask.disabled = "true";
	   			isBillableTask.value = "";
	   			
	   			isDefectTask.className='textBoxDisabled';
	   			isDefectTask.disabled = "true";
	   			isDefectTask.value = "";			

	   			isTestingTask.className='textBoxDisabled';
	   			isTestingTask.disabled = "true";
	   			isTestingTask.value = "";
	   			
	   			isDevelopingTask.className='textBoxDisabled';
	   			isDevelopingTask.disabled = "true";
	   			isDevelopingTask.value = "";
			}
		}	
    }     
		
</script>


<html:form action="manageCategory">
  	<html:hidden name="categoryForm" property="operation"/>
	<html:hidden name="categoryForm" property="id"/>
	
	<br>
		
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="title.category"/>
		</display:headerfootergrid>
	
	  
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
		<tr class="pagingFormBody">
		  <td width="10">&nbsp;</td>
		  <td width="100">&nbsp; </td>
		  <td>&nbsp;</td>
		  <td width="10">&nbsp;</td>
		</tr>
		<tr class="pagingFormBody">
		  <td>&nbsp;</td>    
		  <td class="formTitle"><bean:message key="label.category.name"/>:&nbsp;</td>
		  <td class="formBody">
			<html:text name="categoryForm" property="name" styleClass="textBox" size="30" maxlength="50"/>
		  </td>
		  <td>&nbsp;</td>
		</tr>
		<tr class="pagingFormBody">
		  <td>&nbsp;</td>
		  <td class="formTitle"><bean:message key="label.category.description"/>:&nbsp;</td>
		  <td class="formBody">
			<html:text name="categoryForm" property="description" styleClass="textBox" size="50" maxlength="100"/>
		  </td>
		  <td>&nbsp;</td>
		</tr>
		<tr class="pagingFormBody">
		  <td>&nbsp;</td>    
		  <td class="formTitle"><bean:message key="label.category.type"/>:&nbsp;</td>
		   <td class="formBody">
				<html:select name="categoryForm" property="type" styleClass="textBox" onkeypress="javascript:changeType();" onchange="javascript:changeType();">
					<html:options collection="typeList" property="id" labelProperty="genericTag"/>
				</html:select>
		   </td>       	   
		  <td>&nbsp;</td>
		</tr>
		<tr class="pagingFormBody">
		  <td>&nbsp;</td>
		  <td class="formTitle"><bean:message key="label.category.order"/>:&nbsp;</td>
		  <td class="formBody">
			<html:text name="categoryForm" property="positionOrder" styleClass="textBox" size="4" maxlength="2"/>
		  </td>
		  <td>&nbsp;</td>
		</tr>

		<tr class="pagingFormBody">
		  <td>&nbsp;</td>
		  <td class="formTitle"><bean:message key="label.category.project"/>:&nbsp;</td>
		  <td class="formBody">
			<html:select name="categoryForm" property="projectId" styleClass="textBox">
				<html:options collection="projectList" property="id" labelProperty="name"/>
			</html:select>
		  </td>
		  <td>&nbsp;</td>
		</tr>
		<tr class="pagingFormBody">
		  <td>&nbsp;</td>    
		  <td class="formTitle">&nbsp;</td>
		   <td class="formBody">
			   <html:checkbox property="isBillableTask" name="categoryForm" ><bean:message key="label.category.billable"/></html:checkbox>	           
		   </td>       	   
		  <td>&nbsp;</td>
		</tr>
		<tr class="pagingFormBody">
		  <td>&nbsp;</td>    
		  <td class="formTitle">&nbsp;</td>
		   <td class="formBody">
			   <html:checkbox property="isDefectTask" name="categoryForm" ><bean:message key="label.category.isDefect"/></html:checkbox>	           
		   </td>       	   
		  <td>&nbsp;</td>
		</tr>
		<tr class="pagingFormBody">
		  <td>&nbsp;</td>    
		  <td class="formTitle">&nbsp;</td>
		   <td class="formBody">
			   <html:checkbox property="isTestingTask" name="categoryForm" ><bean:message key="label.category.isTesting"/></html:checkbox>	           
		   </td>       	   
		  <td>&nbsp;</td>
		</tr>
		<tr class="pagingFormBody">
		  <td>&nbsp;</td>    
		  <td class="formTitle">&nbsp;</td>
		   <td class="formBody">
			   <html:checkbox property="isDevelopingTask" name="categoryForm" ><bean:message key="label.category.isDevelopment"/></html:checkbox>	           
		   </td>       	   
		  <td>&nbsp;</td>
		</tr>	    	    	    
		<tr class="pagingFormBody">
		  <td>&nbsp;</td>    
		  <td class="formTitle">&nbsp;</td>
		   <td class="formBody">
			   <html:checkbox property="isHidden" name="categoryForm" ><bean:message key="label.category.isHidden"/></html:checkbox>	           
		   </td>       	   
		  <td>&nbsp;</td>
		</tr>	        	    	        
		<tr class="pagingFormBody">
		  <td width="10" colspan="4">&nbsp;</td>
		</tr>    
		</table>
	
		<display:headerfootergrid type="FOOTER">	
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120">
				  <html:button property="save" styleClass="button" onclick="javascript:buttonClick('categoryForm', 'saveCategory');">
					<bean:write name="categoryForm" property="saveLabel" />
				  </html:button>    
			  </td>
			  <td width="120">
				  <html:button property="new" styleClass="button" onclick="javascript:buttonClick('categoryForm', 'clear');">
					<bean:message key="button.new"/>
				  </html:button>    
			  </td>
			  <td>&nbsp;</td>
			  <td width="120">
				  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('categoryForm', 'backward');">
					<bean:message key="button.backward"/>
				  </html:button>    
			  </td>
			</tr></table>
		</display:headerfootergrid> 
		
		<div>&nbsp;</div>
		
		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="label.category.TitleList"/>
		</display:headerfootergrid>
	    
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
		<tr class="formBody">
			<td>
				<display:table border="1" width="100%" name="categoryList" scope="session" pagesize="13" requestURI="../do/manageCategory?operation=prepareForm">
					  <display:column width="20%" likeSearching="true" property="name" align="center" title="label.category.name" />
					  <display:column property="description" likeSearching="true" title="label.category.description" />					  
					  <display:column width="15%" align="center" comboFilter="true" property="id" title="label.category.project" decorator="com.pandora.gui.taglib.decorator.CategoryProjectDecorator" />
					  <display:column width="20%" property="type" align="center" title="label.category.type" decorator="com.pandora.gui.taglib.decorator.CategoryTypeDecorator"/>
					  <display:column width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridEditDecorator" tag="'categoryForm', 'editCategory'" />
					  <display:column width="2%" property="id" align="center" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.GridDeleteDecorator" tag="'categoryForm', 'removeCategory'" />						  
				</display:table>		
			</td>
		</tr> 
		</table>

		<display:headerfootergrid type="FOOTER">			
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120">      
				  <html:button property="refresh" styleClass="button" onclick="javascript:buttonClick('categoryForm', 'refresh');">
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

<script language="javascript">
	changeType();
</script>