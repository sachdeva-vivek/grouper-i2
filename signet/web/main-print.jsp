<!--
  $Id: main-print.jsp,v 1.7 2005-02-09 22:00:35 acohen Exp $
  $Date: 2005-02-09 22:00:35 $
  
  Copyright 2004 Internet2 and Stanford University.  All Rights Reserved.
  Licensed under the Signet License, Version 1,
  see doc/license.txt in this distribution.
-->

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
  <head>
    <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
    <meta name="robots" content="noindex, nofollow" />
    <title>
      Signet
    </title>
		
    <link href="styles/signet.css" rel="stylesheet" type="text/css" />
    <script language="JavaScript" type="text/javascript" src="scripts/signet.js"></script>
  </head>

  <body>

<%@ page import="java.text.DateFormat" %>
<%@ page import="java.util.Iterator" %>
<%@ page import="java.util.Map" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.SortedSet" %>
<%@ page import="java.util.TreeSet" %>

<%@ page import="edu.internet2.middleware.signet.Signet" %>
<%@ page import="edu.internet2.middleware.signet.PrivilegedSubject" %>
<%@ page import="edu.internet2.middleware.signet.Subsystem" %>
<%@ page import="edu.internet2.middleware.signet.Category" %>
<%@ page import="edu.internet2.middleware.signet.Assignment" %>
<%@ page import="edu.internet2.middleware.signet.Function" %>
<%@ page import="edu.internet2.middleware.signet.Status" %>
  
<% 
  Signet signet
     = (Signet)
         (request.getSession().getAttribute("signet"));
         
   PrivilegedSubject loggedInPrivilegedSubject
     = (edu.internet2.middleware.signet.PrivilegedSubject)
         (request.getSession().getAttribute("loggedInPrivilegedSubject"));
         
   DateFormat dateFormat = DateFormat.getDateInstance();
%>

    <form name="form1" method="post" action="">
      <div id="Header">
        <div id="Logo">
          <img src="images/KITN.gif" width="216" height="60" alt="logo" />
        </div>
      </div>
      <div id="Layout"> 
       <a href="Start.do"><img src="images/icon_arrow_left.gif" class="icon" />return</a>
       <h1>
          Privileges you have granted</h1>
  	    
        <table class="full">            
          <tr>
            <td nowrap="nowrap">
              <b>
                Person
              </b>
            </td>
            <td nowrap="nowrap">
              <b>
                Privilege
              </b>
            </td>
            <td nowrap="nowrap"><b>Scope</b></td>
            <td nowrap="nowrap">
              <b>
                Limits
              </b>
            </td>
            <td nowrap="nowrap">
              <b>
                Status
              </b>
            </td>
            <td nowrap="nowrap">
              <b>
                Granted
              </b>
            </td>
          </tr>
	        
	  
<%
  Set assignmentSet
    = new TreeSet
        (loggedInPrivilegedSubject.getAssignmentsGranted(Status.ACTIVE, null));
  Iterator assignmentIterator = assignmentSet.iterator();
  while (assignmentIterator.hasNext())
  {
    Assignment assignment = (Assignment)(assignmentIterator.next());
    PrivilegedSubject grantee = assignment.getGrantee();
    Subsystem subsystem = assignment.getFunction().getSubsystem();
    Function function = assignment.getFunction();
    Category category = function.getCategory();
%>
	        
          <tr>
            <td>
              <%=grantee.getName()%>
            </td>
            <td>
              <%=subsystem.getName()%> : <%=category.getName()%> :
              <span>
                <%=assignment.getFunction().getName()%>
              </span>
            </td>
            <td>&nbsp;</td>
            <td>
              <span>
                <span class="dropback">
                   
                </span> <br />
                <span class="dropback">
                   
                </span>
                 
              </span>
            </td>
            <td>
<%=
  assignment.getStatus().getName()
  + (assignment.isGrantOnly()==false?", can use":"")
  + (assignment.isGrantable()?", can grant":"")
%>
            </td>
            <td nowrap="nowrap">
<%=
  // dateFormat.format(assignment.getCreateDateTime())
  ""
%>
            </td>
          </tr>
  	
<% 
  }
%>
            
        </table>
        <jsp:include page="footer.jsp" flush="true" />	
      </div>
    </form>
  </body>
</html>
