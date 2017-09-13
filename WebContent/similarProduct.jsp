<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="java.sql.Connection, ucsd.shoppingApp.*"%>
<%@ page import="ucsd.shoppingApp.models.SimilarProductsModel, java.util.*"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>similar products</title>
<style>
thead {color:green;}
tbody {color:blue;}
table, th, td {
    border: 1px solid black;
}
</style>
</head>
<body>
	<%
	Connection con = ConnectionManager.getConnection();
	SimilarProductsDAO similarProducts = new SimilarProductsDAO(con);
	SimilarProductsModel spModel = similarProducts.similarProduct();
	
	ArrayList<Integer> product1 = spModel.getProduct1();
	ArrayList<Integer> product2 = spModel.getProduct2();
	System.out.println(product2.size());
	ArrayList<Double> cosineSimilarity = spModel.getCosineSimilarity();
	%>
	<table align="center">
		<thead>
			<tr>
				<th>product1</th>
				<th>product2</th>
				<th>cosine similarity</th>
			</tr>
		</thead>
		<tbody>
			
			<%for(int i = 0; i < product1.size(); i++){%>
				<tr>
					<td><%=product1.get(i)%></td>
					<td><%=product2.get(i)%></td>
					<td><%=cosineSimilarity.get(i)%></td>
				</tr>
			<%}%>
			
		</tbody>
</table>
</body>
</html>