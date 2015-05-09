<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>

<jsp:include page="header.jsp" />

<script language="javascript">
     function clearIndex(argForm, argOperation){
         if ( confirm("<bean:message key="error.manageOption.resetKb.confirmation"/>")) {
	         callAction("", argForm, argOperation);
         }
     }
          
     function check_PROJ_HIDE(id){
     }
</script>

						
<html:form  action="manageOption">

	<html:hidden name="optionForm" property="operation"/>
	
	<table width="100%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>

		<notEmpty name="metaFormList" scope="session">
			<plandora-html:metaform collection="metaFormList" styleTitle="formTitle" styleBody="formBody" />
			<div>&nbsp;</div>
		</notEmpty>
		
		<display:headerfootergrid width="100%" type="HEADER">
			<bean:message key="title.manageOption"/>
		</display:headerfootergrid>
			
		<table width="98%" border="0" cellspacing="0" cellpadding="0">
	    <tr class="pagingFormBody">
	      <td width="40">&nbsp;</td>
	      <td>&nbsp;</td>
	      <td width="100">&nbsp;</td>
	    </tr>
	    <tr class="pagingFormBody">
	      <td colspan="3">&nbsp;</td>
	    </tr>
	    <tr class="pagingFormBody">
	      <td colspan="3">&nbsp;</td>
	    </tr>
		
	    <tr class="pagingFormBody">
	      <td>&nbsp;</td>
	      <td>
	      
				<!-- CUSTOMER'S OPTIONS -->
				
				<table width="100%" border="0" cellspacing="0" cellpadding="0">
			    <tr>
			      <td width="10">&nbsp;</td>
			      <td><a class="successfullyMessage" href="javascript:showHide('customerPanel');"><bean:message key="title.manageOption.Customer"/></a><hr><br/></td>
			      <td width="20">&nbsp;</td>
			    </tr>
			  	</table>
				
				<div id="customerPanel">
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
					<tr class="gapFormBody">
					  <td width="10">&nbsp;</td>
					  <td width="320">&nbsp; </td>
					  <td width="80">&nbsp;</td>
					  <td width="150">&nbsp; </td>
					  <td>&nbsp;</td>		  
					  <td width="10">&nbsp;</td>
					</tr>
					<tr class="pagingFormBody">
					  <td>&nbsp;</td>
					  <td class="formTitle"><bean:message key="label.manageOption.RequListNumLine"/>:&nbsp;</td>
					  <td class="formBody">
						<html:text name="optionForm" property="requNumLine" styleClass="textBox" size="3" maxlength="3"/>
					  </td>	      
					  <td class="formTitle">&nbsp;</td>
					  <td class="formBody">&nbsp;</td>	      
					  <td>&nbsp;</td>
					</tr>
					<tr class="pagingFormBody">
					  <td>&nbsp;</td>
					  <td class="formTitle"><bean:message key="label.manageOption.GridNumWords"/>:&nbsp;</td>
					  <td class="formBody">
						<html:text name="optionForm" property="maxNumOfWords" styleClass="textBox" size="4" maxlength="4"/>
					  </td>	      
					  <td class="formTitle">&nbsp;</td>
					  <td class="formBody">&nbsp;</td>	      
					  <td>&nbsp;</td>
					</tr>			    
					<tr class="pagingFormBody">
					  <td>&nbsp;</td>
					  <td class="formTitle"><bean:message key="label.manageOption.myReqDaysAgo"/>:&nbsp;</td>
					  <td class="formBody">
						<html:text name="optionForm" property="myReqMaxDaysAgo" styleClass="textBox" size="3" maxlength="3"/>
						<bean:message key="label.days"/>
					  </td>	      
					  <td class="formTitle">&nbsp;</td>
					  <td class="formBody">&nbsp;</td>	      
					  <td>&nbsp;</td>
					</tr>
					<tr class="pagingFormBody">
						<td>&nbsp;</td>
						<td>&nbsp;</td>
						<td colspan="3" class="formBody"><html:checkbox property="showPriorityColor" name="optionForm"/><bean:message key="label.manageOption.RequList.showPriorityColor"/></td>				    
						<td>&nbsp;</td>
					</tr>				    
					<tr class="pagingFormBody">
					  <td>&nbsp;</td>
					  <td class="formTitle"><bean:message key="label.password"/>:&nbsp;</td>
					  <td class="formBody">
						<logic:equal name="optionForm" property="authenticationMode" value="com.pandora.bus.auth.SystemAuthentication">
							<html:password name="optionForm" property="newPassword" styleClass="textBox" size="8" maxlength="30"/>
						</logic:equal>
						<logic:notEqual name="optionForm" property="authenticationMode" value="com.pandora.bus.auth.SystemAuthentication">
							<html:password name="optionForm" property="newPassword" styleClass="textBoxDisabled" disabled="true" size="8" maxlength="30"/>
						</logic:notEqual>			      	
					  </td>	      
					  <td class="formTitle"><bean:message key="label.passConfirmation"/>:&nbsp;</td>
					  <td class="formBody">
						<logic:equal name="optionForm" property="authenticationMode" value="com.pandora.bus.auth.SystemAuthentication">
							<html:password name="optionForm" property="newPasswordConfirm" styleClass="textBox" size="8" maxlength="30"/>
						</logic:equal>
						<logic:notEqual name="optionForm" property="authenticationMode" value="com.pandora.bus.auth.SystemAuthentication">
							<html:password name="optionForm" property="newPasswordConfirm" styleClass="textBoxDisabled" disabled="true" size="8" maxlength="30"/>
						</logic:notEqual>			      	
						<logic:equal name="optionForm" property="authenticationMode" value="com.pandora.bus.auth.SystemAuthentication">
							<html:button property="savePass" styleClass="button" onclick="javascript:buttonClick('optionForm', 'changePassword');">
								<bean:message key="title.manageOption.savePass"/>
							</html:button>
						</logic:equal>
					  </td>	      
					  <td>&nbsp;</td>
					</tr>
					<tr class="pagingFormBody">
					  <td>&nbsp;</td>
					  <td>&nbsp;</td>
					  <td class="formTitle" colspan="4">
						<logic:notEqual name="optionForm" property="authenticationMode" value="com.pandora.bus.auth.SystemAuthentication">
							</br><bean:message key="label.manageOption.authMode"/>
						</logic:notEqual>			      
					  </td>
					</tr>			    			    			    
					<tr class="pagingFormBody">
					  <td>&nbsp;</td>
					  <td>&nbsp;</td>
					  <td>&nbsp;</td>
					  <td>&nbsp;</td>
					  <td>&nbsp;</td>
					  <td>&nbsp;</td>
					</tr>			    
					</table>
				</div>
				<!-- END OF CUSTOMER'S OPTIONS -->
				
				<!-- RESOURCE'S OPTIONS -->
				<logic:equal name="optionForm" property="checkResourceRole" value="on">

					<table width="100%" border="0" cellspacing="0" cellpadding="0">
				    <tr>
				      <td width="10">&nbsp;</td>
				      <td><a class="successfullyMessage" href="javascript:showHide('resourcePanel');"><bean:message key="title.manageOption.Resource"/></a><hr><br/></td>
				      <td width="20">&nbsp;</td>
				    </tr>
				  	</table>
		      		
		      		<div id="resourcePanel">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr class="gapFormBody">
						  <td width="10">&nbsp;</td>
						  <td width="320">&nbsp; </td>
						  <td width="80">&nbsp;</td>
						  <td width="300">&nbsp; </td>
						  <td>&nbsp;</td>		  
						  <td width="10">&nbsp;</td>
						</tr>
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.TaskListNumLine"/>:&nbsp;</td>
						  <td class="formBody">
							<html:text name="optionForm" property="taskNumLine" styleClass="textBox" size="3" maxlength="3"/>
						  </td>
						  <td class="formTitle">&nbsp;</td>
						  <td class="formBody">
							&nbsp;
						  </td>
						  <td>&nbsp;</td>
						</tr>
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.ProjListNumLine"/>:&nbsp;</td>
						  <td class="formBody">
							<html:text name="optionForm" property="projNumLine" styleClass="textBox" size="3" maxlength="3"/>
						  </td>			      
						  <td class="formTitle">&nbsp;</td>
						  <td class="formBody">
							&nbsp;
						  </td>
						  <td>&nbsp;</td>
						</tr>
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.inputTaskFormat"/>:&nbsp;</td>
						  <td class="formBody">
						  		<html:select name="optionForm" property="taskInputFormat" styleClass="textBox">
						             <html:options collection="taskInputFormatList" property="id" labelProperty="genericTag"/>
								</html:select>
						  </td>			      
						  <td class="formTitle">&nbsp;</td>
						  <td class="formBody">
							&nbsp;
						  </td>
						  <td>&nbsp;</td>
						</tr>
						
						<tr class="pagingFormBody">
						  <td colspan="6">&nbsp;</td>
						</tr>				     																		
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.shortcuts"/>:&nbsp;</td>
						  <td class="formBody" colspan="2">
						  		<table  class="table" width="100%" border="1" bordercolor="#10389C" cellspacing="1" cellpadding="2">						  		
						  			<tr class="tableRowHeader">
						  				<th align="center" width="100" class="tableCellHeader"><bean:message key="label.manageOption.shortcuts.title"/></th>
										<th align="center" width="100" class="tableCellHeader"><bean:message key="label.manageOption.shortcuts.url"/></th>
										<th align="center" width="70" class="tableCellHeader"><bean:message key="label.manageOption.shortcuts.icon"/></th>						  		
						  			</tr>
						  			<tr class="tableRowOdd">
										<td class="tableCell" align="left" valign="top">
						  					<html:text name="optionForm" property="shortcutName1" styleClass="textBox" size="35" maxlength="50"/>
						  				</td>
						  				<td class="tableCell" align="left" valign="top">
						  					<html:text name="optionForm" property="shortcutURL1" styleClass="textBox" size="35" maxlength="300"/>
						  				</td>
										<td class="tableCell" align="left" valign="top">
											<html:select name="optionForm" property="shortcutIcon1" styleClass="textBox">
											    <html:options collection="shorcutIconList" property="id" labelProperty="genericTag"/>
											</html:select>
										</td>
						  			</tr>
						  			<tr class="tableRowEven">
						  				<td class="tableCell" align="left" valign="top">
						  					<html:text name="optionForm" property="shortcutName2" styleClass="textBox" size="35" maxlength="50"/>
						  				</td>
						  				<td class="tableCell" align="left" valign="top">
						  					<html:text name="optionForm" property="shortcutURL2" styleClass="textBox" size="35" maxlength="300"/>
						  				</td>
										<td class="tableCell" align="left" valign="top">
											<html:select name="optionForm" property="shortcutIcon2" styleClass="textBox">
											    <html:options collection="shorcutIconList" property="id" labelProperty="genericTag"/>
											</html:select>
										</td>
						  			</tr>
						  			<tr class="tableRowOdd">
						  				<td class="tableCell" align="left" valign="top">
						  					<html:text name="optionForm" property="shortcutName3" styleClass="textBox" size="35" maxlength="50"/>
						  				</td>
						  				<td class="tableCell" align="left" valign="top">
						  					<html:text name="optionForm" property="shortcutURL3" styleClass="textBox" size="35" maxlength="300"/>
						  				</td>
										<td class="tableCell" align="left" valign="top">
											<html:select name="optionForm" property="shortcutIcon3" styleClass="textBox">
											    <html:options collection="shorcutIconList" property="id" labelProperty="genericTag"/>
											</html:select>
										</td>
						  			</tr>
						  		</table>
						  </td>			      
						  <td class="formBody">&nbsp;</td>
						  <td>&nbsp;</td>
						</tr>						
						<tr class="pagingFormBody">
						  <td colspan="6">&nbsp;</td>
						</tr>				     												
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.myTaskDaysAgo"/>:&nbsp;</td>
						  <td class="formBody">
							<html:text name="optionForm" property="myTaskMaxDaysAgo" styleClass="textBox" size="3" maxlength="3"/>
							<bean:message key="label.days"/>				        
						  </td>			      
						  <td class="formTitle">&nbsp;</td>
						  <td class="formBody">
							&nbsp;
						  </td>
						  <td>&nbsp;</td>
						</tr>
   						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.gadgetWidth"/>:&nbsp;</td>
						  <td class="formBody">
							<html:text name="optionForm" property="gadgetWidth" styleClass="textBox" size="4" maxlength="4"/>&nbsp;pixels
						  </td>			      
						  <td class="formTitle">&nbsp;</td>
						  <td class="formBody">
							&nbsp;
						  </td>
						  <td>&nbsp;</td>
						</tr>				     
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.taskListOrdering"/>:&nbsp;</td>
						  <td colspan="2" class="formBody">
								<display:table border="1" width="300" name="taskStatusList" scope="session">
									<display:column align="center" property="name" title="label.manageTask.taskStatus" />
									<display:column align="center" width="30%" property="genericTag" title="label.manageOption.ordering" decorator="com.pandora.gui.taglib.decorator.GridTextBoxDecorator" />
								</display:table>
						  </td>			      
						  <td class="formBody">
							&nbsp;
						  </td>
						  <td>&nbsp;</td>
						</tr>
						<tr class="pagingFormBody">
						  <td colspan="6">&nbsp;</td>
						</tr>				     												
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.criticalDays"/>:&nbsp;</td>
						  <td class="formBody">
							<html:text name="optionForm" property="criticalTaskDays" styleClass="textBox" size="3" maxlength="3"/>
							<bean:message key="label.days"/>				        
						  </td>			      
						  <td class="formTitle">&nbsp;</td>
						  <td class="formBody">
							&nbsp;
						  </td>
						  <td>&nbsp;</td>
						</tr>				    
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.warningDays"/>:&nbsp;</td>
						  <td class="formBody">
							<html:text name="optionForm" property="warningTaskDays" styleClass="textBox" size="3" maxlength="3"/>
							<bean:message key="label.days"/>				        
						  </td>			      
						  <td class="formTitle">&nbsp;</td>
						  <td class="formBody">
							&nbsp;
						  </td>
						  <td>&nbsp;</td>
						</tr>
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.hideProject"/>:&nbsp;</td>
						  <td class="formBody" colspan="2">
								<div style="width:300px; height:150px; overflow-x:hidden; overflow-y:scroll; border:1px">
									<display:table border="1" width="100%" name="projectList" scope="session">
										  <display:column property="name" title="label.name" />	
										  <display:column width="4%" property="id" title="grid.title.empty" decorator="com.pandora.gui.taglib.decorator.HideProjectDecorator" />
									</display:table>						
								</div>										
						  </td>			      
						  <td class="formBody">&nbsp;</td>
						  <td>&nbsp;</td>
						</tr>
						
						<tr class="gapFormBody">
						  <td>&nbsp;</td>
						  <td>&nbsp; </td>
						  <td>&nbsp;</td>
						  <td>&nbsp; </td>
						  <td>&nbsp;</td>
						  <td>&nbsp;</td>
						</tr>
						</table>
					</div>
					<script>javascript:showHide('resourcePanel');</script>
				    
				</logic:equal>
				<!-- END OF RESOURCE'S OPTIONS -->

			    
				<!-- LEADER'S OPTIONS -->
				<logic:equal name="optionForm" property="checkLeaderRole" value="on">
				
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
				    <tr>
						<td width="10">&nbsp;</td>
						<td><a class="successfullyMessage" href="javascript:showHide('leaderPanel');"><bean:message key="title.manageOption.Leader"/></a><hr><br/></td>					  
						<td width="20">&nbsp;</td>
				    </tr>
				  	</table>
		      		
		      		<div id="leaderPanel">
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr class="gapFormBody">
						  <td width="10">&nbsp;</td>
						  <td width="320">&nbsp; </td>
						  <td width="80">&nbsp;</td>
						  <td width="300">&nbsp; </td>
						  <td>&nbsp;</td>		  
						  <td width="10">&nbsp;</td>
						</tr>
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.PendListNumLine"/>:&nbsp;</td>
						  <td class="formBody">
							<html:text name="optionForm" property="pendNumLine" styleClass="textBox" size="3" maxlength="3"/>
						  </td>	      			      
						  <td class="formTitle">&nbsp;</td>
						  <td class="formBody">
							&nbsp;
						  </td>
						  <td>&nbsp;</td>
						</tr>
						
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td colspan="3" class="formTitle"><html:checkbox property="pagingTOListAll" name="optionForm"/><bean:message key="label.manageOption.setPagingTOListAll"/></td>
						  <td>&nbsp;</td>
						  <td>&nbsp;</td>
						</tr>				    
						<tr class="gapFormBody">
						  <td>&nbsp;</td>
						  <td>&nbsp;</td>
						  <td>&nbsp;</td>
						  <td>&nbsp;</td>
						</tr>
						</table>
					</div>
					<script>javascript:showHide('leaderPanel');</script>
				    
				</logic:equal>
				<!-- END OF LEADER'S OPTIONS -->


				<!-- ROOT'S OPTIONS -->
				<logic:equal name="optionForm" property="checkRootRole" value="on">
				
					<table width="100%" border="0" cellspacing="0" cellpadding="0">
				    <tr>
						<td width="10">&nbsp;</td>
						<td><a class="successfullyMessage" href="javascript:showHide('rootPanel');"><bean:message key="title.manageOption.Root"/></a><hr><br/></td>					  
						<td width="20">&nbsp;</td>
				    </tr>
				  	</table>
					
		      		<div id="rootPanel">										
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr class="gapFormBody">
						  <td width="10">&nbsp;</td>
						  <td width="320">&nbsp; </td>
						  <td>&nbsp;</td>		  
						  <td width="10">&nbsp;</td>
						</tr>
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.uploadMaxSize"/>:&nbsp;</td>
						  <td class="formBody">
							<html:text name="optionForm" property="uploadMaxFile" styleClass="textBox" size="10" maxlength="15"/>
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.artifactMaxSize"/>:&nbsp;</td>
						  <td class="formBody">
							<html:text name="optionForm" property="artifactMaxFile" styleClass="textBox" size="10" maxlength="15"/>
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>												
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.currency"/>:&nbsp;</td>
						  <td class="formBody">
							<html:text name="optionForm" property="currencyLocale" styleClass="textBox" size="8" maxlength="5"/>
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.newVersionUrl"/>:&nbsp;</td>
						  <td class="formBody">
							<html:text name="optionForm" property="newVersionUrl" styleClass="textBox" size="40" maxlength="200"/> <bean:message key="label.manageOption.newVersionUrl.note"/>
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.taskReportUrl"/>:&nbsp;</td>
						  <td class="formBody">
							<html:text name="optionForm" property="taskReportUrl" styleClass="textBox" size="85" maxlength="200"/>
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.surveyReportUrl"/>:&nbsp;</td>
						  <td class="formBody">
							<html:text name="optionForm" property="surveyReportUrl" styleClass="textBox" size="85" maxlength="200"/>
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.expenseReportUrl"/>:&nbsp;</td>
						  <td class="formBody">
							<html:text name="optionForm" property="expenseReportUrl" styleClass="textBox" size="85" maxlength="200"/>
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>						
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.lastAction"/>:&nbsp;</td>
						  <td class="formBody">
							<html:textarea name="optionForm" property="userActionLog" styleClass="textBox" cols="85" rows="4" />
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>										    			
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.fullAction"/>:&nbsp;</td>
						  <td class="formBody">
							<html:textarea name="optionForm" property="userFullActionLog" styleClass="textBox" cols="85" rows="6" />
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>									
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.occurrSource"/>:&nbsp;</td>
						  <td class="formBody">
							<html:textarea name="optionForm" property="occurrenceSources" styleClass="textBox" cols="85" rows="3" />
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.notifClass"/>:&nbsp;</td>
						  <td class="formBody">
							<html:textarea name="optionForm" property="notifiChannels" styleClass="textBox" cols="85" rows="3" />				        
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>				    
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.impexpClass"/>:&nbsp;</td>
						  <td class="formBody">
							<html:textarea name="optionForm" property="impExpClasses" styleClass="textBox" cols="85" rows="3" />				        
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>				    
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.gadgetClass"/>:&nbsp;</td>
						  <td class="formBody">
							<html:textarea name="optionForm" property="gadgetClasses" styleClass="textBox" cols="85" rows="3" />				        
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>				    
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.repositoryClass"/>:&nbsp;</td>
						  <td class="formBody">
							<html:textarea name="optionForm" property="repositoryClasses" styleClass="textBox" cols="85" rows="3" />				        
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>		
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.artifactClass"/>:&nbsp;</td>
						  <td class="formBody">
							<html:textarea name="optionForm" property="artifactExpClasses" styleClass="textBox" cols="85" rows="3" />				        
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>				    
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.snipArtifactClass"/>:&nbsp;</td>
						  <td class="formBody">
							<html:textarea name="optionForm" property="snipArtifactClasses" styleClass="textBox" cols="85" rows="3" />				        
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>				    
								    
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.authenticationClass"/>:&nbsp;</td>
						  <td class="formBody">
							<html:textarea name="optionForm" property="authenticationClasses" styleClass="textBox" cols="85" rows="3" />				        
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>				    
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.calSyncClass"/>:&nbsp;</td>
						  <td class="formBody">
							<html:textarea name="optionForm" property="calendarSyncClasses" styleClass="textBox" cols="85" rows="3" />				        
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>				    
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.converterClass"/>:&nbsp;</td>
						  <td class="formBody">
							<html:textarea name="optionForm" property="converterClasses" styleClass="textBox" cols="85" rows="3" />				        
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>				    
						<tr class="gapFormBody">
						  <td>&nbsp;</td>
						  <td>&nbsp;</td>
						  <td>&nbsp;</td>
						  <td>&nbsp;</td>
						  <td>&nbsp;</td>
						</tr>
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.kbFolder"/>:&nbsp;</td>
						  <td class="formBody">
							<html:text name="optionForm" property="kbIndexFolder" styleClass="textBox" size="30" maxlength="255"/>
							<bean:message key="label.manageOption.kbFolder.warning"/>
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>				    
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.kbClass"/>:&nbsp;</td>
						  <td class="formBody">
							<html:textarea name="optionForm" property="kbClasses" styleClass="textBox" cols="85" rows="3" />				        
						  </td>	      			      
						  <td>&nbsp;</td>
						</tr>
						</table>
						
						<table width="100%" border="0" cellspacing="0" cellpadding="0">
						  <bean:write name="optionForm" property="kbProgressBar" filter="false"/>					      				    
						<tr class="pagingFormBody">
						  <td width="10">&nbsp;</td>
						  <td width="320">&nbsp;</td>
						  <td width="270" class="formBody">
							  <html:button property="resetIndex" styleClass="button" onclick="javascript:clearIndex('optionForm', 'resetIndex');">
								<bean:message key="label.manageOption.kb.reset"/>
							  </html:button>    
						  </td>
						  <td>&nbsp;</td>		      
						  <td width="10">&nbsp;</td>
						</tr>

						<table width="100%" border="0" cellspacing="0" cellpadding="0">
						<tr class="gapFormBody">
						  <td width="10">&nbsp;</td>
						  <td width="320">&nbsp;</td>
						  <td>&nbsp;</td>
						  <td width="10">&nbsp;</td>
						</tr>				    				    				    
											
						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.ldapServerPort"/>:&nbsp;</td>
						  <td class="formBody">
								<html:text name="optionForm" property="ldapHost" styleClass="textBox" size="16" maxlength="30"/>&nbsp;:&nbsp;
								<html:text name="optionForm" property="ldapPort" styleClass="textBox" size="5" maxlength="10"/>									  
						  </td>		  
						  <td>&nbsp;</td>
						</tr>

						<tr class="pagingFormBody">
						  <td>&nbsp;</td>
						  <td class="formTitle"><bean:message key="label.manageOption.ldapUID" />:&nbsp;</td>
						  <td class="formBody">
								<html:text name="optionForm" property="ldapUIDRegister" styleClass="textBox" size="40" maxlength="70"/>
								<bean:message key="label.manageOption.ldapUID.hint"/>
						  </td>		  
						  <td>&nbsp;</td>
						</tr>
											
						<tr class="gapFormBody">
						  <td colspan="4">&nbsp;</td>
						</tr>				    
						
						</table>
					</div>
					<script>javascript:showHide('rootPanel');</script>
				    

				</logic:equal>
				<!-- END OF ROOT'S OPTIONS -->
				
	      <td>&nbsp;</td>
	      </tr>
		  <tr class="pagingFormBody"><td colspan="3">&nbsp;</td> </tr>
		  <tr class="pagingFormBody"><td colspan="3">&nbsp;</td> </tr>
		  <tr class="pagingFormBody"><td colspan="3">&nbsp;</td> </tr>
   	      </table>
	    	      

		<display:headerfootergrid type="FOOTER">
			<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
			  <td width="120">
				  <html:button property="save" styleClass="button" onclick="javascript:buttonClick('optionForm', 'saveOption');">
					<bean:write name="optionForm" property="saveLabel" />
				  </html:button>    
			  </td>
			  <td>&nbsp;</td>
			  <td width="120">
				  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('optionForm', 'backward');">
					<bean:message key="button.backward"/>
				  </html:button>    
			  </td>
			</tr></table>
		</display:headerfootergrid> 
		
		
	</td><td width="20">&nbsp;</td>
				
  	</tr></table>
</html:form>

<jsp:include page="footer.jsp" />