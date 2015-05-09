<%@ taglib uri="/WEB-INF/lib/struts-html" prefix="html" %>
<%@ taglib uri="/WEB-INF/lib/struts-bean" prefix="bean" %>

		</td>
		<td width="2">&nbsp;</td>			
	</tr>		
	<script>javascript:showHide('errorDetail');</script>
	
<%@ page import="com.pandora.UserTO"%>
<%@ page import="com.pandora.PreferenceTO"%>
<%@ page import="com.pandora.delegate.UserDelegate"%>
		<tr height="22">
			<td background="../images/footer_backg.png">&nbsp;</td>
			<td background="../images/footer_backg.png" class="footerNote">
				<bean:message key="footer.copyright" />&nbsp;<bean:message key="footer.version" />
			</td>
			<td background="../images/footer_backg.png">&nbsp;</td>
		</tr>
	</table>
		
	</BODY>
</html>