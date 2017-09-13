package ucsd.shoppingApp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import ucsd.shoppingApp.models.AnalyticsModel;

public class AnalyticsDAO {
	
	private static String total_state = "select count(*) from state;";
	
	private static String total_customer = "select count(*) from person;";
	
	private static String total_product_allCategory = "select count(*) from product;";
	
	private static String total_product_category = "select count(*) from product where category_id = ?;";

	
	/** product **/
	private static String product_alphabetical_allCategory = 
			"select pr.product_name, pr.id, sum(pc.quantity*pc.price) as totalSales from "
			+ "product pr left join products_in_cart pc on pr.id = pc.product_id "
			+ "left join shopping_cart sc on pc.cart_id = sc.id and sc.is_purchased = true "
			+ "group by pr.id order by pr.product_name asc offset ? limit 10;";
	
	private static String product_alphabetical_category = 
			" with newProduct as (select product_name as newproduct_name, id as newproduct_id from product where category_id= ? ) " 
			+ "select pr.newproduct_name, pr.newproduct_id, sum(pc.quantity*pc.price) as totalSales from "
			+ "newProduct pr left join products_in_cart pc on pr.newproduct_id = pc.product_id "
			+ "left join shopping_cart sc on pc.cart_id = sc.id and sc.is_purchased = true "
			+ "group by pr.newproduct_name, pr.newproduct_id order by pr.newproduct_name asc offset ? limit 10 ;";
	
	private static String product_topK_allCategory = 
			"select pr.product_name, pr.id, sum(pc.quantity*pc.price) as totalSales from product pr "
			+ "left join products_in_cart pc on pr.id = pc.product_id "
			+ "left join shopping_cart sc on sc.is_purchased = true and sc.id = pc.cart_id "
			+ "group by pr.id order by totalSales desc nulls last offset ? limit 10;";
	
	private static String product_topK_category = 
			" with newProduct as (select product_name as newproduct_name, id as newproduct_id from product where category_id= ? )"		
			+ "select pr.newproduct_name, pr.newproduct_id, sum(pc.quantity*pc.price) as totalSales from "
			+ "newProduct pr left join products_in_cart pc on pr.newproduct_id = pc.product_id "
			+ "left join shopping_cart sc on pc.cart_id = sc.id and sc.is_purchased = true "
			+ "group by pr.newproduct_name, pr.newproduct_id order by totalSales desc offset ? limit 10 ;";
	
	/** state **/
	
	private static String state_alphabetical_allCategory = 
			"select st.state_name, st.id, sum(pc.quantity*pc.price) as totalSales from "
			+ "state st left join person pe on st.id=pe.state_id "
			+ "left join shopping_cart sc on pe.id = sc.person_id and sc.is_purchased = true "
			+ "left join products_in_cart pc on pc.cart_id = sc.id "
			+ "group by st.id order by st.state_name asc offset ? limit 20;";
	
	private static String state_product_alphabetical_allCategory = 
			" with newState as (select st.state_name, st.id from state st "
			+  " order by st.state_name asc offset ? limit 20), " 
					
			+  " newProduct as (select pr.product_name, pr.id from product pr "
			+  " order by pr.product_name asc offset ? limit 10) " 
			
			+  " select st.id, pr.id, sum(pc.quantity * pc.price) as totalSales " 
			+  " from newState st left join person pe on st.id = pe.state_id " 
			+  " left join shopping_cart sc on (sc.is_purchased = true and sc.person_id = pe.id) " 
			+  " left join products_in_cart pc on sc.id = pc.cart_id " 
			+  " join newProduct pr on pc.product_id = pr.id  " 
			+  " group by st.id, pr.id; " ;
	
	private static String state_alphabetical_category = 
			"select st.state_name, st.id, sum(pc.quantity*pc.price) as totalSales from "
			+ "state st left join product pr on pr.category_id = ? "
			+ "left join person pe on st.id=pe.state_id "			
			+ "left join shopping_cart sc on pe.id = sc.person_id and sc.is_purchased = true "
			+ "left join products_in_cart pc on pr.id = pc.product_id and pc.cart_id = sc.id "
			
			+ "group by st.id order by st.state_name asc offset ? limit 20;";
	
	private static String state_product_alphabetical_category = 
			" WITH newState as (select st.state_name, st.id from state st "
			+ "order by st.state_name asc offset ? limit 20)," 
					
			+ " newProduct as (select pr.product_name, pr.id from product pr where pr.category_id = ? "
			+ " order by product_name asc offset ? limit 10) " 
			
			+ " select st.id, pr.id, sum(pc.quantity*pc.price) as totalSales" 
			+ " from newState st left join person pe on st.id = pe.state_id " 
			+ " left join shopping_cart sc on sc.is_purchased=true and sc.person_id=pe.id " 
			+ " left join products_in_cart pc on sc.id = pc.cart_id " 
			+ " inner join newProduct pr on pc.product_id = pr.id  " 
			+ " group by st.id, pr.id;" ;
	
	private static String state_topK_allCategory  = 
			"select st.state_name, st.id, sum(pc.quantity * pc.price) as totalSales from state st "
			+ "left join person pe on st.id = pe.state_id "
			+ "left join shopping_cart sc on sc.person_id = pe.id and sc.is_purchased = true "
			+ "left join products_in_cart pc on pc.cart_id = sc.id "
			+ "group by st.id order by totalSales desc nulls last offset ? limit 20;";
	
	private static String state_product_topK_allCategory = 
			"with newState as (select st.state_name, st.id, sum(pc.quantity*pc.price) as totalSales "
			+ "from state st left join person pe on st.id = pe.state_id "
			+ "left join shopping_cart sc on sc.person_id = pe.id and sc.is_purchased = true " 
			+ "left join products_in_cart pc on pc.cart_id = sc.id "
			+ "group by st.id order by totalSales desc nulls last offset ? limit 20), "
			
			+ " newProduct as (select pr.product_name, pr.id, sum(pc.quantity * pc.price) as totalSales from product pr " 
			+ " left join products_in_cart pc on pr.id = pc.product_id "
			+ " left join shopping_cart sc on sc.is_purchased = true and  sc.id= pc.cart_id " 
			+ " group by pr.id order by totalSales desc nulls last offset ? limit 10) "
			
			+ " select st.id, pr.id, sum(pc.quantity * pc.price) as totalSales" 
			+ " from newState st left join person pe on st.id = pe.state_id " 
			+ " left join shopping_cart sc on sc.is_purchased = true and sc.person_id = pe.id " 
			+ " left join products_in_cart pc on sc.id = pc.cart_id " 
			+ " inner join newProduct pr on pc.product_id = pr.id  " 
			+ " group by st.id, pr.id; ";
	
	private static String state_topK_category = 
			"select st.state_name, st.id, sum(pc.quantity * pc.price) as totalSales from state st "
			+ "left join product pr on pr.category_id =? "
			+ "left join person pe on st.id = pe.state_id "
			+ "left join shopping_cart sc on sc.person_id = pe.id and sc.is_purchased = true "
			+ "left join products_in_cart pc on pc.cart_id = sc.id and pr.id = pc.product_id "
			+ "group by st.state_name, st.id "
			+ "order by totalSales desc nulls last offset ? limit 20;";
	
	private static String state_product_topK_category = 
			"with newState as (select st.state_name, st.id, "
			+ " sum(pc.quantity*pc.price) as totalSales from state st " 
			+ " left join product pr on pr.category_id = ?"
			+ " left join person pe on st.id = pe.state_id "
			+ " left join shopping_cart sc on sc.person_id = pe.id and sc.is_purchased = true "
			+ " left join products_in_cart pc on pc.cart_id = sc.id and pc.product_id = pr.id "
			+ " group by st.state_name, st.id order by totalSales desc nulls last offset ? limit 20), "
			
			+ " newProduct as (select pr.product_name, pr.id, "
			+ " sum(pic.quantity*pic.price) as totalSales from product pr " 
			+ " join shopping_cart sc on (sc.is_purchased=true and pr.category_id= ? ) "
			+ " left outer join products_in_cart pic on (pic.cart_id=sc.id and pr.id=pic.product_id) " 
			+ " group by pr.product_name, pr.id  order by totalSales desc nulls last offset ? limit 10) "
			
			+ " select st.id, pr.id, sum(pc.quantity*pc.price)  " 
			+ " from newState st left join person pe on st.id = pe.state_id " 
			+ " left join shopping_cart sc on sc.is_purchased=true and sc.person_id = pe.id " 
			+ " left join products_in_cart pc on sc.id = pc.cart_id " 
			+ " inner join newProduct pr on pc.product_id = pr.id  " 
			+ " group by st.id, pr.id; " ;
	
	
	/** customer **/
	private static String customer_alphabetical_allCategory = 
			" select pe.person_name, pe.id, sum(pc.quantity*pc.price) as total_price from person pe "
			+ "left join shopping_cart sc on sc.person_id = pe.id and sc.is_purchased = true "
			+ "left join products_in_cart pc on sc.id = pc.cart_id "
			+ "group by pe.id order by pe.person_name asc offset ? limit 20;";
	
	private static String customer_product_alphabetical_allCategory = 
			"with newPerson as (select pe.person_name, pe.id from "
			+ "person pe order by person_name asc offset ? limit 20), "
					
			+ "newProduct as (select pr.product_name, pr.id from product pr "
			+ "order by product_name asc offset ? limit 10) "
			
			+ "select pe.id, pr.id, sum(pc.quantity*pc.price) "
			+ "from newPerson pe left join shopping_cart sc on sc.is_purchased = true and sc.person_id = pe.id "
			+ "left join products_in_cart pc on sc.id = pc.cart_id "
			+ "join newProduct pr on pc.product_id = pr.id "
			+ "group by pe.id, pr.id;";
	
	private static String  customer_alphabetical_category = 
			"select pe.person_name, pe.id, sum(pc.quantity * pc.price) "
			+ "from person pe left join product pr on pr.category_id = ? "
			+ "left join shopping_cart sc on sc.person_id = pe.id and sc.is_purchased = true "
			+ "left join products_in_cart pc on pc.product_id = pr.id and sc.id = pc.cart_id "
			+ "group by pe.id order by pe.person_name asc offset ? limit 20;";
	
	private static String customer_product_alphabetical_category = 
			"with newPerson as (select person_name, id from "
			+ "person order by person_name asc nulls last offset ? limit 20), "
					
			+ "newProduct as (select product_name, id from product where category_id = ? "
			+ "order by product_name asc nulls last offset ? limit 10) "
			
			+ "select pe.id, pr.id, sum(pc.quantity*pc.price) "
			+ "from newPerson pe left join shopping_cart sc on sc.is_purchased = true and sc.person_id = pe.id "
			+ "left join products_in_cart pc on sc.id = pc.cart_id "
			+ "inner join newProduct pr on pc.product_id = pr.id "
			+ "group by pe.id, pr.id;";
	
	
	private static String customer_topK_allCategory=
			"select pe.person_name, pe.id, sum(pc.quantity * pc.price) as totalSales from person pe "
			+ "left join shopping_cart sc on sc.person_id = pe.id and sc.is_purchased = true "
			+ "left join products_in_cart pc on pc.cart_id = sc.id "
			+ "group by pe.id order by totalSales desc nulls last offset ? limit 20;";
	
	private static String customer_product_topK_allCategory = 
			"with newPerson as (select pe.person_name, pe.id, sum(pc.quantity * pc.price) as totalSales "
			+ "from person pe left join shopping_cart sc on sc.person_id = pe.id and sc.is_purchased = true "
			+ "left join products_in_cart pc on  pc.cart_id = sc.id "
			+ "left join product pr on pc.product_id = pr.id "
			+ "group by pe.id order by totalSales desc nulls last offset ? limit 20), "
			
			+ "newProduct as (select pr.product_name, pr.id, sum(pc.quantity*pc.price) as totalSales from product pr "
			+ "left join products_in_cart pc on pr.id = pc.product_id "
			+ "left join shopping_cart sc on sc.is_purchased = true and sc.id = pc.cart_id "
			+ "group by pr.id order by totalSales desc nulls last offset ? limit 10) "
			
			+ " select pe.id, pr.id, sum(pc.quantity*pc.price) " 
			+ " from newPerson pe left join shopping_cart sc on sc.is_purchased = true and sc.person_id = pe.id " 
			+ " left join products_in_cart pc on sc.id = pc.cart_id " 
			+ " inner join newProduct pr on pc.product_id = pr.id " 
			+ " group by pe.id, pr.id;";
	
	private static String customer_topK_category = 
			"select pe.person_name, pe.id, sum(pc.quantity * pc.price) as totalSales from person pe "
			+ "left join product pr on pr.category_id = ? "
			+ "left join shopping_cart sc on sc.person_id = pe.id and sc.is_purchased = true "
			+ "left join products_in_cart pc on pr.id = pc.product_id and pc.cart_id = sc.id "
			+ "group by pe.id order by totalSales desc nulls last offset ? limit 20;";
	
	private static String customer_product_topK_category = 
			"with newPerson as (select pe.person_name, pe.id, sum(pc.quantity * pc.price) as totalSales "
			+ "from person pe left join product pr on pr.category_id = ? "
			+ "left join shopping_cart sc on sc.person_id = pe.id and sc.is_purchased = true "
			+ "left join products_in_cart pc on pr.id = pc.product_id and pc.cart_id = sc.id "
			+ "group by pe.id order by totalSales desc nulls last offset ? limit 20), "
			
			+ "newProduct as (select pr.product_name, pr.id, sum(pc.quantity*pc.price) as totalSales from product pr "
			+ "join products_in_cart pc on  pr.category_id = ? "
			+ "left join shopping_cart sc on pr.id = pc.product_id and sc.id = pc.cart_id and sc.is_purchased = true "
			+ "group by pr.id order by totalSales desc nulls last offset ? limit 10) "
			
			+ " select pe.id, pr.id, sum(pc.quantity*pc.price)  " 
			+ " from newPerson pe "
			+ " left join shopping_cart sc on sc.is_purchased=true and sc.person_id = pe.id " 
			+ " left join products_in_cart pc on sc.id=pc.cart_id  " 
			+ " inner join newProduct pr on pc.product_id = pr.id "
			+ " group by pe.id, pr.id;";
	
	private Connection con;
	
	private ArrayList<Integer> scid = new ArrayList<Integer>();
	private ArrayList<String> scname = new ArrayList<String>();
	private ArrayList<Double> scsum = new ArrayList<Double>();

	private ArrayList<Integer>  pid = new ArrayList<Integer>();
	private ArrayList<String>  pname = new ArrayList<String>();
	private ArrayList<Double>  psum = new ArrayList<Double>();
	double[][] cells = new double[20][10];

	public AnalyticsDAO(Connection con) {
		this.con = con;
	}

	public void setCustomerOrState(ResultSet rs) throws SQLException{
		try{
			while(rs.next()){
				String name = rs.getString(1);
				int id = rs.getInt(2);
				double sum = rs.getDouble(3);
				scid.add(id);
				scname.add(name);
				scsum.add(sum);
			}
		}catch(SQLException e){
			e.printStackTrace();
			throw e;
		}
	}
	
	public void setProduct(ResultSet rs) throws SQLException{
		try{
			while(rs.next()){
				String name = rs.getString(1);
				if(name.length()>=10){
					name=name.substring(0,9);
				}				
				int id = rs.getInt(2);
				double sum = rs.getDouble(3);
				pid.add(id);
				//System.out.println(id+" ");
				pname.add(name);
				psum.add(sum);
			}
			
		}catch(SQLException e){
			e.printStackTrace();
			throw e;
		}
	}
	
	public void setCell(ResultSet rs) throws SQLException{
		try{
			for(int i = 0; i < scid.size(); i++){
				//System.out.print(scid.get(i)+" ");
			}
			//System.out.println("----------------------");
			for(int i = 0; i < pid.size(); i++){
				//System.out.print(pid.get(i)+" ");
			}
			//System.out.println("----------------------");
			while(rs.next()){
				int sc_id= rs.getInt(1);
				int p_id = rs.getInt(2);
				double sum = rs.getDouble(3);
				System.out.println("B: "+sc_id+" "+p_id);
				
				System.out.println("C: "+scid.indexOf(sc_id)+" "+pid.indexOf(p_id));
				cells[scid.indexOf(sc_id)][pid.indexOf(p_id)] = sum;
			}
		}catch(SQLException e){
			e.printStackTrace();
			throw e;
		}
	}
	
	public AnalyticsModel Analysis(String row_dropdown, String order_dropdown, int filter_dropdown, int rowOffset, int columnOffset) throws SQLException {
		System.out.println(row_dropdown + " "+order_dropdown+" "+filter_dropdown+" "+rowOffset+" "+columnOffset);
		Statement stmt = con.createStatement();
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		int totalSC = 0;
		int totalProduct = 0;
		AnalyticsModel analyticsModel;
		
		long start;
		long end;
		long query_time;
		
		//get total state, customer, product
		try{	
			if(row_dropdown.equals("States")){
				
				//total state
				start = System.currentTimeMillis();
				rs = stmt.executeQuery(total_state);
				end = System.currentTimeMillis();
				query_time = end - start;
				System.out.println("Time for total_state "+query_time);
				
				while(rs.next()){
					totalSC = rs.getInt(1);
				}
				
				if (order_dropdown.equals("Alphabetical")) {
					if (filter_dropdown == 0) {
						
						//total product all category
						start = System.currentTimeMillis();
						rs = stmt.executeQuery(total_product_allCategory);
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for total_product_allCategory "+query_time);
						
						while(rs.next()){
							totalProduct = rs.getInt(1);
						}
						
						//state alphabetical all category
						pstmt = con.prepareStatement(state_alphabetical_allCategory);
						pstmt.setInt(1, rowOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for state_alphabetical_allCategory "+query_time);
						
						setCustomerOrState(rs);
						
						//product alphabetical all category
						
						pstmt = con.prepareStatement(product_alphabetical_allCategory);
						pstmt.setInt(1, columnOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for product_alphabetical_allCategory "+query_time);
						
						setProduct(rs);
						
						//state product alphabetical all category cell
						
						pstmt = con.prepareStatement(state_product_alphabetical_allCategory);
						pstmt.setInt(1, rowOffset);
						pstmt.setInt(2, columnOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for state_product_alphabetical_allCategory "+query_time);
						
						setCell(rs);
					} 
					
					
					else if(filter_dropdown != 0){
						
						//total product certain category
						pstmt = con.prepareStatement(total_product_category);
						pstmt.setInt(1, filter_dropdown);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for total_product_category "+query_time);
						
						while(rs.next()){
							totalProduct = rs.getInt(1);
						}
						
						//state alphabetical certain category
						pstmt = con.prepareStatement(state_alphabetical_category);
						pstmt.setInt(1, filter_dropdown);
						pstmt.setInt(2, rowOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for state_alphabetical_category "+query_time);
						
						setCustomerOrState(rs);
						
						//product alphabetical certain category
						pstmt = con.prepareStatement(product_alphabetical_category);
						pstmt.setInt(1, filter_dropdown);
						pstmt.setInt(2, columnOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for product_alphabetical_category "+query_time);
						
						setProduct(rs);
						
						//state product alphabetical certain category cell
						pstmt = con.prepareStatement(state_product_alphabetical_category);
						pstmt.setInt(1, rowOffset);
						pstmt.setInt(2, filter_dropdown);
						pstmt.setInt(3, columnOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for state_product_alphabetical_category "+query_time);
						
						setCell(rs);
						} 
					}
				
				else if(order_dropdown.equals("TopK")){
					if (filter_dropdown == 0) {
						//total product all category
						
						start = System.currentTimeMillis();
						rs = stmt.executeQuery(total_product_allCategory);
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for total_product_allCategory "+query_time);
						
						while(rs.next()){
							totalProduct = rs.getInt(1);
						}
						
						//state topK all category
						pstmt = con.prepareStatement(state_topK_allCategory);
						pstmt.setInt(1, rowOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for state_topK_allCategory "+query_time);
						
						setCustomerOrState(rs);
						
						//product topK all category
						pstmt = con.prepareStatement(product_topK_allCategory);
						pstmt.setInt(1, columnOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for product_topK_allCategory "+query_time);
						
						setProduct(rs);
						
						//state product topK all category cell
						pstmt = con.prepareStatement(state_product_topK_allCategory);
						pstmt.setInt(1, rowOffset);
						pstmt.setInt(2, columnOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for state_product_topK_allCategory "+query_time);
						
						setCell(rs);
					
					}
					
					else if(filter_dropdown != 0){
						
						//total product certain category
						
						pstmt = con.prepareStatement(total_product_category);
						pstmt.setInt(1, filter_dropdown);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for total_product_category "+query_time);
						
						while(rs.next()){
							totalProduct = rs.getInt(1);
						}
						
						//state topK certain category
						pstmt = con.prepareStatement(state_topK_category);
						pstmt.setInt(1, filter_dropdown);
						pstmt.setInt(2, rowOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for state_topK_category "+query_time);
						
						setCustomerOrState(rs);
						
						//product topK certain category
						pstmt = con.prepareStatement(product_topK_category);
						pstmt.setInt(1, filter_dropdown);
						pstmt.setInt(2, columnOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for product_topK_category "+query_time);
						
						setProduct(rs);
						
						//state product topK certain category cell
						pstmt = con.prepareStatement(state_product_topK_category);
						pstmt.setInt(1, filter_dropdown);
						pstmt.setInt(2, rowOffset);
						pstmt.setInt(3, filter_dropdown);
						pstmt.setInt(4, columnOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for state_product_topK_category "+query_time);
						
						setCell(rs);
					}
				}
			}
			
			if(row_dropdown.equals("Customers")){
				//total customer
				
				start = System.currentTimeMillis();
				rs = stmt.executeQuery(total_customer);
				end = System.currentTimeMillis();
				query_time = end - start;
				System.out.println("Time for total_customer "+query_time);
				
				while(rs.next()){
					totalSC = rs.getInt(1);
				}
				
				if (order_dropdown.equals("Alphabetical")) {
					if (filter_dropdown == 0) {
						
						//total product all category
						
						start = System.currentTimeMillis();
						rs = stmt.executeQuery(total_product_allCategory);
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for total_product_allCategory "+query_time);
						
						while(rs.next()){
							totalProduct = rs.getInt(1);
						}
						
						//customer alphabetical all category
						pstmt = con.prepareStatement(customer_alphabetical_allCategory);
						pstmt.setInt(1, rowOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for customer_alphabetical_allCategory "+query_time);
						
						setCustomerOrState(rs);
						
						//product alphabetical all category
						pstmt = con.prepareStatement(product_alphabetical_allCategory);
						pstmt.setInt(1, columnOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for product_alphabetical_allCategory "+query_time);
						
						setProduct(rs);
						
						//customer product alphabetical all category cell
						pstmt = con.prepareStatement(customer_product_alphabetical_allCategory);
						pstmt.setInt(1, rowOffset);
						pstmt.setInt(2, columnOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for customer_product_alphabetical_allCategory "+query_time);
						
						setCell(rs);
					} 
					
					
					else if(filter_dropdown != 0){
						//total product certain category
						pstmt = con.prepareStatement(total_product_category);
						pstmt.setInt(1, filter_dropdown);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for total_product_category "+query_time);
						
						while(rs.next()){
							totalProduct = rs.getInt(1);
						}
						
						//customer alphabetical certain category
						pstmt = con.prepareStatement(customer_alphabetical_category);
						pstmt.setInt(1, filter_dropdown);
						pstmt.setInt(2, rowOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for customer_alphabetical_category "+query_time);
						
						setCustomerOrState(rs);
						
						//product alphabetical certain category
						pstmt = con.prepareStatement(product_alphabetical_category);
						pstmt.setInt(1, filter_dropdown);
						pstmt.setInt(2, columnOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for product_alphabetical_category "+query_time);
						
						setProduct(rs);
						
						//customer product alphabetical certain category cell
						pstmt = con.prepareStatement(customer_product_alphabetical_category);
						pstmt.setInt(1, rowOffset);
						pstmt.setInt(2, filter_dropdown);
						pstmt.setInt(3, columnOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for customer_product_alphabetical_category "+query_time);
						
						setCell(rs);
					} 
				}
				
				else if(order_dropdown.equals("TopK")){
					if (filter_dropdown == 0) {
						//total product all category
						
						start = System.currentTimeMillis();
						rs = stmt.executeQuery(total_product_allCategory);
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for total_product_allCategory "+query_time);
						
						while(rs.next()){
							totalProduct = rs.getInt(1);
						}
						
						//customer topK all category
						pstmt = con.prepareStatement(customer_topK_allCategory);
						pstmt.setInt(1, rowOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for customer_topK_allCategory "+query_time);
						
						setCustomerOrState(rs);
						
						//product topK all category
						pstmt = con.prepareStatement(product_topK_allCategory);
						pstmt.setInt(1, columnOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for product_topK_allCategory "+query_time);
						
						setProduct(rs);
						
						//customer product topK all category cell
						pstmt = con.prepareStatement(customer_product_topK_allCategory);
						pstmt.setInt(1, rowOffset);
						pstmt.setInt(2, columnOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for customer_product_topK_allCategory "+query_time);
						
						setCell(rs);
					}
					
					else if(filter_dropdown != 0){
						//total product certain category
						pstmt = con.prepareStatement(total_product_category);
						pstmt.setInt(1, filter_dropdown);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for total_product_category "+query_time);
						
						while(rs.next()){
							totalProduct = rs.getInt(1);
						}
						
						//customer topK certain category
						pstmt = con.prepareStatement(customer_topK_category);
						pstmt.setInt(1, filter_dropdown);
						pstmt.setInt(2, rowOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for customer_topK_category "+query_time);
						
						setCustomerOrState(rs);
						
						//product topK all certain category
						pstmt = con.prepareStatement(product_topK_category);
						pstmt.setInt(1, filter_dropdown);
						pstmt.setInt(2, columnOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for product_topK_category "+query_time);
						
						setProduct(rs);
						
						//customer product topK certain category cell
						pstmt = con.prepareStatement(customer_product_topK_category);
						pstmt.setInt(1, filter_dropdown);
						pstmt.setInt(2, rowOffset);
						pstmt.setInt(3, filter_dropdown);
						pstmt.setInt(4, columnOffset);
						
						start = System.currentTimeMillis();
						rs = pstmt.executeQuery();
						end = System.currentTimeMillis();
						query_time = end - start;
						System.out.println("Time for customer_product_topK_category "+query_time);
						
						setCell(rs);
					}
				}
			}
			
			
			analyticsModel = new AnalyticsModel(scid, scname, scsum, pid, pname, psum, cells, totalSC,totalProduct);
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
		return analyticsModel;
		}
	}

