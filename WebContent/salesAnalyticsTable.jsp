<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page
	import="java.sql.Connection, ucsd.shoppingApp.ConnectionManager, ucsd.shoppingApp.*"%>
<%@ page import="ucsd.shoppingApp.models.* , java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
</head>
<body>

<%
long start = System.currentTimeMillis();
AnalyticsModel analyticsModel;
String row_dropdown = "Customers";
String order_dropdown = "Alphabetical";
int filter_dropdown = 0;
int rowOffset = 0;
int columnOffset = 0;
String action = request.getParameter("action");
if (action != null) {
	row_dropdown = request.getParameter("row_dropdown");
	order_dropdown = request.getParameter("order_dropdown");
	filter_dropdown = Integer.parseInt(request.getParameter("filter_dropdown").toString());
	rowOffset = Integer.parseInt(request.getParameter("rowOffset").toString());
	columnOffset = Integer.parseInt(request.getParameter("columnOffset").toString());
}
   
  
Connection con = ConnectionManager.getConnection();	
HashMap<Integer, String> stateMap=StateDAO.getStates(con);

AnalyticsDAO analyticsDAO = new AnalyticsDAO(con);
analyticsModel = analyticsDAO.Analysis(row_dropdown, order_dropdown, filter_dropdown, rowOffset, columnOffset);
con.close();

ArrayList<Integer> scid = analyticsModel.getScid();
ArrayList<String> scname = analyticsModel.getScname();
ArrayList<Double> scsum = analyticsModel.getScsum();
ArrayList<Integer> pid = analyticsModel.getPid();
ArrayList<String> pname = analyticsModel.getPname();
ArrayList<Double> psum = analyticsModel.getPsum();
double [][] cells = analyticsModel.getCells();
int totalSC = analyticsModel.getTotalSC();
int totalProduct = analyticsModel.getTotalProduct();
%>


<table align="center">
	<thead>
		<tr>
			<th></th>
			<%for (int i = 0; i < pname.size(); i++) {%>
   				<th scope="col"><B><%= pname.get(i) %> (<%= psum.get(i) %>)</B></th>
   			<%}%>
		</tr>
	</thead>
	<tbody>
		<%for(int i = 0; i < scname.size(); i++){%>
		<tr>
			<td scope="row"><B><%= scname.get(i) %> (<%= scsum.get(i) %>)</B></td>
			<%for(int j=0;j<pname.size();j++){ %>
				<td><%= cells[i][j] %></td>
			<%}%>
		</tr>
		<%}%>
	</tbody>
</table>

<%if(columnOffset + 10 < totalProduct){
%>
	<form action="salesAnalytics.jsp" method="POST">
		<input type="hidden" name="row_dropdown" value="<%=row_dropdown%>">
		<input type="hidden" name="order_dropdown" value="<%=order_dropdown%>">
		<input type="hidden" name="filter_dropdown" value="<%=filter_dropdown%>">
		<input type="hidden" name="rowOffset" value="<%=rowOffset%>">
		<input type="hidden" name="columnOffset" value="<%=columnOffset + 10%>">
		<input type="hidden" name="action" value="Next 10 Products">
		<input type="submit" name="Next 10 Products" value="Next 10 Products">
		
	</form>
<%}%>
<%if(rowOffset + 20 < totalSC){ %>
<form action="salesAnalytics.jsp" method="POST">
	<input type="hidden" name="row_dropdown" value="<%=row_dropdown%>">
	<input type="hidden" name="order_dropdown" value="<%=order_dropdown%>">
	<input type="hidden" name="filter_dropdown" value="<%=filter_dropdown%>">
	<input type="hidden" name="rowOffset" value="<%=rowOffset + 20%>">
	<input type="hidden" name="columnOffset" value="<%=columnOffset%>">
	<input type="hidden" name="action" value="Next 20 Customers or States">
	<input type="submit" name="Next 20 Customers or States" value="Next 20 Customers or States">
</form>
<%
long end = System.currentTimeMillis();
long time= end-start;
System.out.println("running time of the overall jsp:"+time);
}%>



</body>
</html>