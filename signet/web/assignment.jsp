<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<!--
  $Id: assignment.jsp,v 1.21 2005-10-20 21:28:27 acohen Exp $
  $Date: 2005-10-20 21:28:27 $
  
  Copyright 2004 Internet2 and Stanford University.  All Rights Reserved.
  Licensed under the Signet License, Version 1,
  see doc/license.txt in this distribution.
-->

<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <meta name="robots" content="noindex, nofollow" />
    <title>
      Signet
    </title>
    <link href="styles/signet.css" rel="stylesheet" type="text/css" />
    <script language="JavaScript" type="text/javascript" src="scripts/signet.js">
    </script>
  </head>
  <body>

<%@ page import="java.text.DateFormat" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.SortedSet" %>
<%@ page import="java.util.TreeSet" %>

<%@ page import="edu.internet2.middleware.subject.Subject" %>
<%@ page import="edu.internet2.middleware.signet.PrivilegedSubject" %>
<%@ page import="edu.internet2.middleware.signet.Subsystem" %>
<%@ page import="edu.internet2.middleware.signet.Category" %>
<%@ page import="edu.internet2.middleware.signet.Assignment" %>
<%@ page import="edu.internet2.middleware.signet.Function" %>
<%@ page import="edu.internet2.middleware.signet.tree.TreeNode" %>
<%@ page import="edu.internet2.middleware.signet.Signet" %>
<%@ page import="edu.internet2.middleware.signet.Limit" %>
<%@ page import="edu.internet2.middleware.signet.LimitValue" %>

<%@ page import="edu.internet2.middleware.signet.ui.Common" %>

<% 
  Signet signet
     = (Signet)
         (request.getSession().getAttribute("signet"));
         
  Assignment currentAssignment
    = (Assignment)
         (request.getSession().getAttribute("currentAssignment"));
         
  Subject grantee
    = signet.getSubject
        (currentAssignment.getGrantee().getSubjectTypeId(),
         currentAssignment.getGrantee().getSubjectId());
  Subject grantor
  	= signet.getSubject
  		(currentAssignment.getGrantor().getSubjectTypeId(),
  		 currentAssignment.getGrantor().getSubjectId());
  Subject proxy
  	= signet.getSubject
  		(currentAssignment.getProxy().getSubjectTypeId(),
  		 currentAssignment.getProxy().getSubjectId());
         
  boolean canUse = currentAssignment.canUse();
  boolean canGrant = currentAssignment.canGrant();
         
  DateFormat dateFormat = DateFormat.getDateInstance();
%>
    <div class="section">
      <h2>Assignment details</h2>
			
      <table>
    
      <tr>
        <td class="label">
          Granted to:
        </td>
        <td>
          <%=grantee.getName()%>
        </td>
      </tr>
    
    
    
      <tr>
      	<td class="label">Type:</td>
      	<td><%=currentAssignment.getFunction().getSubsystem().getName()%></td>
     	</tr>
      <tr>
      	<td class="label">Privilege:</td>
      	<td><p><span class="category"><%=currentAssignment.getFunction().getCategory().getName()%></span> : <span class="function"><%=currentAssignment.getFunction().getName()%></span></p>
      	    <p><%=currentAssignment.getFunction().getHelpText()%></p></td>
      </tr>
      <tr>
        <td class="label">
          Scope:
        </td>
        <td>
          <%=currentAssignment.getScope().getName()%>
        </td>
      </tr>

<%
  Limit[] limits
  	= Common.getLimitsInDisplayOrder
        (currentAssignment.getFunction().getLimits());
  LimitValue[] limitValues = Common.getLimitValuesInDisplayOrder(currentAssignment);
  for (int limitIndex = 0; limitIndex < limits.length; limitIndex++)
  {
    Limit limit = limits[limitIndex];
%>
      <tr>
        <td class="label">
          <%=limit.getName()%>:
        </td>
        <td>
<%
  StringBuffer strBuf = new StringBuffer();
  int limitValuesPrinted = 0;
  for (int limitValueIndex = 0;
       limitValueIndex < limitValues.length;
       limitValueIndex++)
  {
    LimitValue limitValue = limitValues[limitValueIndex];
    if (limitValue.getLimit().equals(limit))
    {
      strBuf.append((limitValuesPrinted++ > 0) ? ", " : "");
      strBuf.append(limitValue.getDisplayValue());
    }
  }
%>
          <%=strBuf%>
        </td>
      </tr>
<%
  }
%>

      <tr>
        <td class="label">
          Extensibility:
        </td>
        <td>
          <%=canUse?"can use":""%><%=(canUse && canGrant ? ", " : "")%><%=canGrant?"can grant":""%>
        </td>
      </tr>
      <tr>
      	<td class="label"> Effective on: </td>
      	<td><%=dateFormat.format(currentAssignment.getEffectiveDate())%> </td>
     	</tr>

  <tr>
        <td class="label">
          Granted by:
        </td>
        <td>
          <%=(proxy==null ? "" : (proxy.getName() + " acting as ")) + grantor.getName()%>
        </td>
      </tr>
      
    </table>
  </div>
	
  </body>
</html>