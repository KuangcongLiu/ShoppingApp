<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page import="java.sql.Connection, ucsd.shoppingApp.ConnectionManager, ucsd.shoppingApp.*"%>
<%@ page import="ucsd.shoppingApp.models.* , java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>
	<%
		Connection con = ConnectionManager.getConnection();
		CategoryDAO categoryDao = new CategoryDAO(con);
		List<CategoryModel> category_list = categoryDao.getCategories();
		con.close();
	%>
     <form action="salesAnalytics.jsp" method="POST" >
		<table>
			<td>
				<select name="row_dropdown" id="row_dropdown">
				<%if(request.getParameter("row_dropdown")!=null && request.getParameter("row_dropdown").equals("States")) {%>
					<option value="States" >States</option>
					<option value="Customers">Customers</option>					
				<%}
				else{%>
					<option value="Customers">Customers</option>
					<option value="States" >States</option>
					<%}%>
				</select>
			</td>

			<td>
				<select name="order_dropdown" id="order_dropdown">
				<%if(request.getParameter("order_dropdown")!=null && request.getParameter("order_dropdown").equals("TopK")) {%>
					<option value="TopK">TopK</option>
					<option value="Alphabetical">Alphabetical</option>					
			    <%}
				else{%>
				    <option value="Alphabetical">Alphabetical</option>
					<option value="TopK">TopK</option>
			    <%}%>
				</select>
			</td>

			<td>
				<select name="filter_dropdown" id="filter_dropdown">
				<%if(request.getParameter("filter_dropdown")!=null && !request.getParameter("filter_dropdown").equals("0")) {
					for (CategoryModel cat : category_list) {
							if(request.getParameter("filter_dropdown").equals(cat.getId()+"")){
								out.println("<option value=\"" + cat.getId() + "\">" + cat.getCategoryName() + "</option>");
								
							}							
						}					    
					%>
					<option value="0">All Categories</option>
					<%
						for (CategoryModel cat : category_list) {
							if(!request.getParameter("filter_dropdown").equals(cat.getId()+"")){
								out.println("<option value=\"" + cat.getId() + "\">" + cat.getCategoryName() + "</option>");
							}							
						}					    				
				}
				else{%>
				   <option value="0">All Categories</option>
					<%  
						for (CategoryModel cat : category_list) {
							out.println("<option value=\"" + cat.getId() + "\">" + cat.getCategoryName() + "</option>");
						}
					%>
				<%}%>
				</select>
			</td>
		
	        <input type="hidden" name="rowOffset" value="0">
			<input type="hidden" name="columnOffset" value="0"> 
			<td><input type="submit" name="action" value="Run Query" ></td>
		
		</table>
	</form>

</body>
</html>