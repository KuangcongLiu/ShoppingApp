<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.sql.Connection, ucsd.shoppingApp.ConnectionManager, ucsd.shoppingApp.*"%>
<%@ page import="ucsd.shoppingApp.models.* , java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Sales Analytics</title>
<style>
thead {color:green;}
tbody {color:blue;}
table, th, td {
    border: 1px solid black;
}
</style>
</head>
<body>

<%if(session.getAttribute("roleName") != null) {
		String role = session.getAttribute("roleName").toString();

		if("owner".equalsIgnoreCase(role) == true){%>
			<table cellspacing="5">
				<tr>
					<td valign="top"> <jsp:include page="./menu.jsp"></jsp:include></td>
					<td>
					 <table>
						<tr><h3>Hello <%= session.getAttribute("personName") %></h3></tr>
						<tr><td><h3>Sales Analytics</h3></td></tr>
						
						<% if (request.getAttribute("error")!=null && Boolean.parseBoolean(request.getAttribute("error").toString())) {%>
							<h4 style="color:red"> Error : <%= request.getAttribute("errorMsg").toString()%></h4> 
						<%}		
						session.setAttribute("Next10", "false");
						session.setAttribute("Next20", "false");
						if(request.getParameter("Next 10 Products")!=null && request.getParameter("Next 10 Products").equals("Next 10 Products"))  {
							session.setAttribute("Next10", "true");
							
						}					
						if(request.getParameter("Next 20 Customers or States")!=null && request.getParameter("Next 20 Customers or States").equals("Next 20 Customers or States"))  {
							session.setAttribute("Next20", "true");							
						}						
						 if (request.getAttribute("message")!=null) {%>
							<h4> Message : <%= request.getAttribute("message").toString()%></h4> 
						<%}%>
						
						<%if(!session.getAttribute("Next10").equals("true") && !session.getAttribute("Next20").equals("true")){						  
						%>
						<tr>						
						<jsp:include page="salesAnalyticsBar.jsp" />
						</tr>
						<%} %>
						
						<tr>
						<jsp:include page="salesAnalyticsTable.jsp" />
						</tr>
						
						
					  </table> 	
					</td>
				</tr>
			</table>	
	<%
		} 
		else { %>
			<h3>This page is available to owners only</h3>
		<%
		}
	}
	else { %>
			<h3>Please <a href = "./login.jsp">login</a> before viewing the page</h3>
	<%} %>

</body>
</html>