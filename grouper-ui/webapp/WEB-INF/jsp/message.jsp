<%-- @annotation@
		 Standard tile used in baseDef which appears above the content space
		 and renders any Message object assigned to the request attribute key
		 'message'
--%><%--
  @author Gary Brown.
  @version $Id: message.jsp,v 1.5 2008-07-20 21:18:43 mchyzer Exp $
--%>
<%@include file="/WEB-INF/jsp/include.jsp"%>
<grouper:recordTile key="Not dynamic" tile="${requestScope['javax.servlet.include.servlet_path']}">
<%-- included at least from body.jsp --%>
<div class="grouperMessage <c:out value="${message.containerId}"/>"  >

<%-- print out prefix --%>
<grouper:message key="message.${message.containerId}" />

<!--message-->
<grouper:message bundle="${nav}" key="${message.text}">
<c:forEach var="arg" items="${message.args}">
   <grouper:param value="${arg}"/>
</c:forEach>
</grouper:message>
<!--/message-->
</div>
<p>&nbsp;</p>
</grouper:recordTile>