<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@page
	import="java.sql.Connection, ucsd.shoppingApp.*, ucsd.shoppingApp.models.*, java.util.*, com.google.gson.*,java.io.*, org.json.simple.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>new sales analytics</title>
<script>
	function refresh() {
		
		var xmlHttp = new XMLHttpRequest();

		var responseHandler = function() {
			if (xmlHttp.readyState == 4 && xmlHttp.status == 200){
			
			var responseDoc = xmlHttp.responseText;
			var response = eval('(' + responseDoc + ')');

			var oldStateName = response[0];
			var oldStateSum = response[1];
			var oldProductName = response[2];
			var oldProductSum = response[3];
			var oldCells = response[4];
			var purpleProductName = response[5];
			var purpleTextSum = response[6];
			var purpleTextName = response[7];
			var redStateSum = response[8];
			var redProductSum = response[9];
			var newCells = response[10];
			
			console.log(oldStateSum[0]);
			console.log(redStateSum[0]);
				
			
			//red state
			for (var i = 0; i < oldStateSum.length; i++) {
				var stateSum = oldStateSum[i];
				var stateName = oldStateName[i].split(' ').join('');
				var redstateSum = redStateSum[i];
				var cell = document.getElementById(stateName);

				if (stateSum != redstateSum) {
					console.log("red state");					
					cell.style.color = "#FF0000";
					cell.innerHTML = oldStateName[i]+" ("+redstateSum+")";
				}
				else{
					cell.style.color = "#000000";
				}
			}

			//red product
			for (var i = 0; i < oldProductSum.length; i++) {
				var productSum = oldProductSum[i];
				var productName = oldProductName[i];
				var redproductSum = redProductSum[i];
				var cell = document.getElementById(productName);

				if (productSum !=redproductSum) {
					console.log("red product");
					cell.style.color = "#FF0000";
					cell.innerHTML = oldProductName[i] +" ("+redproductSum+")";
				}
				else{
					cell.style.color = "#000000";
				}
			}

			//red cell
			for (var i = 0; i < oldStateSum.length; i++) {
				for (var j = 0; j < oldProductSum.length; j++) {	
					var stateName = oldStateName[i].split(' ').join('');
					var productName = oldProductName[j];
					var newCellsValue = newCells[i][j];
					var oldCellsValue = oldCells[i][j];

					var cell = document.getElementById(stateName + productName);				

					if ( newCellsValue != oldCellsValue) {
						console.log("red cell");
						cell.style.color = "#FF0000";
						cell.innerHTML = newCellsValue;
					}
					else{
						cell.style.color = "#000000";
					}
				}
			}

			//purple col header
			for (var i = 0; i < oldProductName.length; i++) {
				var productName = oldProductName[i];
				var purpleSum = redProductSum[i];
				for(var j = 0; j < purpleProductName.length; j++){
					var purpleName = purpleProductName[j];			
					var cell1 = document.getElementById(productName);	
					if(productName.localeCompare(purpleName)==0){
						console.log("purple header");
						cell1.style.color = "#800080";
						cell1.innerHTML = productName+" ("+purpleSum+")";
						
						for(var k = 0; k < oldStateName.length; k++){
							var cell2 = document.getElementById(oldStateName[k].split(' ').join('')+productName);
							cell2.style.color = "#800080";
						}
						break;
					}
					/* else{
						cell1.style.color = "#000000";
						
						for(var k = 0; k < oldStateName.length; k++){
							var cell3 = document.getElementById(oldStateName[k].split(' ').join('')+productName);
							cell3.style.color = "#000000";
						}
					} */
				}
						
			}
			
		
			//purple text 
			var purple = document.getElementById("purpleText");
			var content = "";
			
			if(purpleTextName.length!=0){
				for (var i = 0; i < purpleTextName.length; i++) {
					console.log("purple text");
                	purple.style.color="#800080";
                	content=content+"\n"+purpleTextName[i]+" ("+purpleTextSum[i]+")";          
				}
				purple.innerHTML=content;
			}				
			else{
				purple.innerHTML="";
			}
					
		}

	}
		xmlHttp.onreadystatechange = responseHandler;
		var url = "./AnalyticsDAO3";	
		xmlHttp.open("GET", url, true);
		xmlHttp.send(null);
}
</script>
</head>
<body>
	<%
		int filter_dropdown = 0;
		String action = request.getParameter("action");
		if (action != null) {
			filter_dropdown = Integer.parseInt(request.getParameter("filter_dropdown").toString());
		}
	

		Connection con = ConnectionManager.getConnection();
		AnalyticsDAO3 analyticsDAO = new AnalyticsDAO3(con);
		AnalyticsModel2 analyticsModel = analyticsDAO.Run(filter_dropdown);

		CategoryDAO categoryDao = new CategoryDAO(con);
		List<CategoryModel> category_list = categoryDao.getCategories();
		
		ArrayList<String> oldStateName = analyticsModel.getOldStateName();
		ArrayList<Double> oldStateSum = analyticsModel.getOldStateSum();
		ArrayList<String> oldProductName = analyticsModel.getOldProductName();
		ArrayList<Double> oldProductSum = analyticsModel.getOldProductSum();
		double[][] oldCells = analyticsModel.getOldCells();		
		ArrayList<Integer> oldProductId = analyticsModel.getOldProductId();
		ArrayList<Integer> oldStateId = analyticsModel.getOldStateId();
		
		session=request.getSession();
		session.setAttribute("oldStateName", oldStateName);
		session.setAttribute("oldStateSum", oldStateSum);
		session.setAttribute("oldProductName", oldProductName);
		session.setAttribute("oldProductSum", oldProductSum);
		session.setAttribute("oldCells", oldCells);
		session.setAttribute("oldProductId", oldProductId);
		session.setAttribute("oldStateId", oldStateId);
		session.setAttribute("filter_dropdown", filter_dropdown);
		
	%>
<table id = "purpleText">
</table>
	<button id="refresh" class="topLeft" onclick="refresh();">refresh</button>

	<form action="newSalesAnalytics.jsp" method="POST">
		<table>
			<tbody>
				<tr>
					<td><select name="filter_dropdown">
							<%
								if (request.getParameter("filter_dropdown") != null
										&& !request.getParameter("filter_dropdown").equals("0")) {
									for (CategoryModel cat : category_list) {
										if (request.getParameter("filter_dropdown").equals(cat.getId() + "")) {
											out.println("<option value=\"" + cat.getId() + "\">" + cat.getCategoryName() + "</option>");

										}
									}
							%>
							<option value="0">All Categories</option>
							<%
								for (CategoryModel cat : category_list) {
										if (!request.getParameter("filter_dropdown").equals(cat.getId() + "")) {
											out.println("<option value=\"" + cat.getId() + "\">" + cat.getCategoryName() + "</option>");
										}
									}
								} else {
							%>
							<option value="0">All Categories</option>
							<%
								for (CategoryModel cat : category_list) {
										out.println("<option value=\"" + cat.getId() + "\">" + cat.getCategoryName() + "</option>");
									}
							%>
							<%
								}
							%>
					</select></td>
					<td><input type="submit" name="action" value="Run Query"></td>
				</tr>
			</tbody>
		</table>
	</form>
	<table align="center">
		<thead>
			<tr>
				<th></th>
				<%
					for (int i = 0; i < oldProductName.size(); i++) {
				%>
				<th scope="col" id=<%=oldProductName.get(i)%>><B><%=oldProductName.get(i)%>
						(<%=oldProductSum.get(i)%>)</B></th>
				<%
					}
				%>
			</tr>
		</thead>
		<tbody>
			<%
				for (int i = 0; i < oldStateName.size(); i++) {
			%>
			<tr>
				<td scope="row" id=<%=oldStateName.get(i).replaceAll("\\s+","")%>><B><%=oldStateName.get(i)%>
						(<%=oldStateSum.get(i)%>)</B></td>
				<%
					for (int j = 0; j < oldProductName.size(); j++) {
				%>
				<td id=<%=oldStateName.get(i).replaceAll("\\s+","") + oldProductName.get(j)%>><%=oldCells[i][j]%></td>
				<%
					}
				%>
			</tr>
			<%
				}
			%>
		</tbody>
	</table>
</body>
</html>