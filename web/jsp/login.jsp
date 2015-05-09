<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>
<%@ taglib uri="/WEB-INF/lib/display" prefix="display" %>

<jsp:include page="header.jsp" />

<script language="javascript">
	function doLogin(){
    	with(document.forms["loginForm"]){
        	operation.value= "doLogin";             
            submit();
        }         
	}
</script>

<html:form  action="login">
	<html:hidden name="loginForm" property="operation"/>
	
	<br /><br /><br /><br /><br /><br /><br /><br /><br /><br />
	
	<table border="0" width="250"><tr><td>

	<display:headerfootergrid width="90%" type="HEADER">
		<bean:message key="title.login"/>
	</display:headerfootergrid>
	
	<center>
	<table border="0" cellspacing="0" cellpadding="0">
    <tr class="gapFormBody">
      <td width="40%">&nbsp;</td>
      <td>&nbsp;</td>
    </tr>		
    <tr>
    	<td class="formTitle"><div align="right"><bean:message key="label.user" />:&nbsp;</div></td>
	    <td class="formBody">
   	        <html:text name="loginForm" property="username" maxlength="30" size="8" styleClass="textBox"/>
	    </td>
    </tr>
    <tr>
    	<td class="formTitle"><div align="right"><bean:message key="label.password" />:&nbsp;</div></td>
	    <td class="formBody">
   	        <html:password name="loginForm" property="password" onkeypress="if (event.keyCode==13) doLogin();" maxlength="15" size="8" styleClass="textBox"/>
	    </td>
    </tr>
    <tr class="gapFormBody">
      <td>&nbsp;</td>
      <td>&nbsp;</td>
    </tr>		    
	</table>
	</center>	
	<display:headerfootergrid type="FOOTER">
		<center><input type="button" name="login" value="    OK    " onClick="javascript:doLogin();" class="button"></center>
	</display:headerfootergrid>
	
	
	</td> </tr></table>
	
	
</html:form>

<jsp:include page="footer.jsp" />

<!-- Fim do codigo -->
<script> document.forms["loginForm"].username.focus(); </script>

