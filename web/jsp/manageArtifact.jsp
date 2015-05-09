<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>
<%@ taglib uri="/WEB-INF/lib/plandora-html" prefix="plandora-html" %>
<%@ taglib uri="/WEB-INF/lib/struts-logic" prefix="logic" %>

<jsp:include page="header.jsp" />

<script language="JavaScript" src="../jscript/tiny_mce/tiny_mce.js" type="text/JavaScript"></script>
<script language="javascript">
	
	tinyMCE.init({
	
		// General options
		mode : "textareas",
		language : <bean:write name="artifactForm" property="lang" filter="false"/>,
		theme : "advanced",
		plugins : "fullscreen,style,wordcount,table,advlist",
		execcommand_callback : "callBackAfterSnipSelect",

		// Theme options
		theme_advanced_buttons1 : "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,formatselect,fontselect,fontsizeselect,snipselect",
		theme_advanced_buttons2 : "tablecontrols,bullist,numlist,|,outdent,indent,|,undo,redo,|,forecolor,backcolor,|,hr,sub,sup",
		theme_advanced_buttons3 : "",	
		theme_advanced_buttons4 : "",		
		theme_advanced_toolbar_location : "top",
		theme_advanced_toolbar_align : "left",
		theme_advanced_statusbar_location : "bottom",
		theme_advanced_snips : <bean:write name="artifactForm" property="snipList" filter="false"/>,
		theme_advanced_resizing : true,

		// Example content CSS (should be your site CSS)
		content_css : "../jscript/tiny_mce/themes/advanced/skins/default/content.css",

		// Style formats
		style_formats : [
			{title : 'Bold text', inline : 'b'},
			{title : 'Red text', inline : 'span', styles : {color : '#ff0000'}},
			{title : 'Red header', block : 'h1', styles : {color : '#ff0000'}},
			{title : 'Example 1', inline : 'span', classes : 'example1'},
			{title : 'Example 2', inline : 'span', classes : 'example2'},
			{title : 'Table styles'},
			{title : 'Table row 1', selector : 'tr', classes : 'tablerow1'}
		],

		formats : {
			alignleft : {selector : 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img', classes : 'left'},
			aligncenter : {selector : 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img', classes : 'center'},
			alignright : {selector : 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img', classes : 'right'},
			alignfull : {selector : 'p,h1,h2,h3,h4,h5,h6,td,th,div,ul,ol,li,table,img', classes : 'full'},
			bold : {inline : 'span', 'classes' : 'bold'},
			italic : {inline : 'span', 'classes' : 'italic'},
			underline : {inline : 'span', 'classes' : 'underline', exact : true},
			strikethrough : {inline : 'del'}
		}			
	}); 


	function callBackAfterSnipSelect(editor_id, elm, command, user_interface, value) {
		if (command=="SnipSelected" && value!="") {
			var w = 450;
			var h = 350;
			<bean:write name="artifactForm" property="snipHtmlDimension" filter="false"/>
			displayMessage("../do/showSnipArtifact?operation=prepareForm&snip=" + value, w, h);					
			return true;
		}
		return false;
	}


	function showEditor(content) {
		tinyMCE.execCommand('mceInsertContent',false,content);
	}

	function callPlanningEditForm(){
		<bean:write name="artifactForm" property="backToCaller" filter="false"/>
	}	
</script>

    
<html:form action="manageArtifact">
	<html:hidden name="artifactForm" property="operation"/>
	<html:hidden name="artifactForm" property="id"/>	
	<html:hidden name="artifactForm" property="projectId"/>
	<html:hidden name="artifactForm" property="planningId"/>	
	<html:hidden name="artifactForm" property="templateId"/>
	<html:hidden name="artifactForm" property="showSaveAsPopup"/>
	
	<!-- from HosterRepositoryForm... -->
	<html:hidden name="artifactForm" property="genericTag"/>
	<html:hidden name="artifactForm" property="onlyFolders"/>
	<html:hidden name="artifactForm" property="multiple"/>
	
		
	<br>

	<table width="98%" border="0" cellspacing="0" cellpadding="0">
	<tr><td width="10">&nbsp;</td><td>
	
	<display:headerfootergrid width="100%" type="HEADER">
		<bean:write name="artifactForm" property="name" />
	</display:headerfootergrid>
		
	<table width="98%" height="70%" border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td colspan="3">&nbsp;</td>
    </tr>

    <tr class="pagingFormBody">
      <td width="10">&nbsp;</td>
      <td class="formBody">

		<div>
			<div>
				<textarea id="body" name="body" rows="25" cols="80" style="width: 100%">
					<bean:write name="artifactForm" property="body"/>
				</textarea>
			</div>
		</div>

      </td>
      <td>&nbsp;</td>
    </tr>
	
    <tr class="gapFormBody">
      <td colspan="3">&nbsp;</td>
    </tr>
	</table>    
	
	<display:headerfootergrid type="FOOTER">
		<table width="98%" border="0" cellspacing="0" cellpadding="0"><tr>
		  <td width="120">
			  <html:button property="save" styleClass="button" onclick="javascript:buttonClick('artifactForm', 'saveAsArtifact');">
				<bean:write name="artifactForm" property="saveLabel" />
			  </html:button>    
		  </td>
		  <td>&nbsp;</td>
		  <logic:present name="artifactForm" property="backToCaller">
			  <td width="130">      
				  <html:button property="gotoPlanningForm" styleClass="button" onclick="javascript:callPlanningEditForm();">
					 <bean:message key="label.artifactTag.backToForm"/>
				  </html:button>    
			  </td>	  
		  </logic:present>
		  
		  <td width="120">
			  <html:button property="backward" styleClass="button" onclick="javascript:buttonClick('artifactForm', 'backward');">
				<bean:message key="button.backward"/>
			  </html:button>    
		  </td>
		</tr></table>
  	</display:headerfootergrid>   
  
  </td><td width="10">&nbsp;</td>
  </tr></table>

</html:form>

<jsp:include page="footer.jsp" />

<!-- End of source-code -->
<script> 
	with(document.forms["artifactForm"]){	
		if (showSaveAsPopup.value=="on" ) {
			showSaveAsPopup.value = "off";
			displayMessage("../do/showBrowseArtifact?operation=prepareForm&path=&projectId=" + projectId.value, 450, 350);		
		}		
	}
</script>