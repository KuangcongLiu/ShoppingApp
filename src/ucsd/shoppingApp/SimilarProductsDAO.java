package ucsd.shoppingApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ucsd.shoppingApp.models.*;


public class SimilarProductsDAO {
	private static String similar_products = 
		"with ma as (select p.id as pid , pr.id as newproductid, s.newquantity*pr.price as newprice "
		+"from person p left outer join product pr on pr.id=pr.id "
		+"left outer join (select sc.person_id as newperson_id, pic.product_id as newproduct_id, sum(pic.quantity) as newquantity "
		+"from shopping_cart sc, products_in_cart pic "
		+"where sc.is_purchased=true "
		+" and sc.id = pic.cart_id "               
		+" group by sc.person_id, pic.product_id) s on p.id=s.newperson_id and s.newproduct_id=pr.id) "

		+"select m1.newproductid as pid1,m2.newproductid as pid2,SUM(m1.newprice*m2.newprice)/(SQRT(SUM(m1.newprice*m1.newprice)*SUM(m2.newprice*m2.newprice))) as cosine "
		+"from ma m1 inner join ma m2 on m1.newproductid<m2.newproductid and m1.pid=m2.pid "
		+"group by m1.newproductid,m2.newproductid "
		+" order by cosine desc nulls last limit 100;";
	private Connection con;
	private ArrayList<Integer> product1 = new ArrayList<Integer>();
	private ArrayList<Integer> product2 = new ArrayList<Integer>();
	private ArrayList<Double> cosineSimilarity = new ArrayList<Double>();
	SimilarProductsModel spModel;
	
	
	public SimilarProductsDAO(Connection con) {
		this.con = con;
	}
	
	public SimilarProductsModel similarProduct() throws SQLException {
		Statement stmt = con.createStatement();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		int totalSC = 0;
		int totalProduct = 0;
		SimilarProductsModel spModel;
		try{
			rs = stmt.executeQuery(similar_products);
			while(rs.next()){
				product1.add(rs.getInt(1));
				product2.add(rs.getInt(2));
				cosineSimilarity.add(rs.getDouble(3));
			}
			
			spModel = new SimilarProductsModel(product1, product2, cosineSimilarity);
		}catch(SQLException e){
			e.printStackTrace();
			throw e;
		}finally{
			if (rs != null) {
				rs.close();
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return spModel;
	}	
}
