package ucsd.shoppingApp;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import ucsd.shoppingApp.models.AnalyticsModel2;

/**
 * Servlet implementation class AnalyticsDAO3
 */
@WebServlet("/AnalyticsDAO3")
public class AnalyticsDAO3 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    private String state_topK_allCategory = "select st.id, st.state_name, coalesce(ps.totalSales,0) from state st, precomputeState ps "
			+ "where st.id = ps.state_id and ps.category_id = 0 " + "order by totalSales desc nulls last;";

	private String state_topK_category = "select st.id, st.state_name, coalesce(ps.totalSales,0) from state st, precomputeState ps "
			+ "where st.id = ps.state_id and ps.category_id = ? " + "order by totalSales desc nulls last;";

	private String product_topK_allCategory = "select pr.id, pr.product_name, coalesce(pp.totalSales,0) from product pr, precomputeProduct pp "
			+ "where pr.id = pp.product_id " + "order by totalSales desc nulls last limit 50;";

	private String product_topK_category = "select pr.id, pr.product_name, coalesce(pp.totalSales,0) from product pr, precomputeProduct pp "
			+ "where pr.id = pp.product_id and pr.category_id = ? " + "order by totalSales desc nulls last limit 50;";

	private String state_product_topK_category = "select pcc.totalSales from precomputeCell pcc "
			+ "where pcc.state_id = ? and pcc.product_id = ?;";

	private String update_product = "update precomputeProduct pp set totalSales = pp.totalSales + t.totalSales "
			+ " from ( select log.product_id, coalesce(sum(log.totalSales),0) as totalSales" + " FROM log log  "
			+ " GROUP BY log.product_id) t  " + " WHERE pp.product_id = t.product_id;";

	private String update_state_category = "update precomputeState ps set totalSales = ps.totalSales + t.totalSales "
			+ " from ( select log.state_id, log.category_id, coalesce(sum(log.totalSales),0) as totalSales" + " FROM log log  "
			+ " GROUP BY log.state_id, log.category_id) t  "
			+ " where t.state_id = ps.state_id and t.category_id = ps.category_id;";

	private String update_state_allCategory = "update precomputeState ps set totalSales = ps.totalSales + t.totalSales "
			+ " from ( select log.state_id, coalesce(sum(log.totalSales),0) as totalSales" + " FROM log log  "
			+ " GROUP BY log.state_id) t " + " where t.state_id = ps.state_id and 0 = ps.category_id;";
    
	private String check_cell_exists = "select distinct pcc.state_id, pcc.product_id "
			+ " from log log, precomputeCell pcc "
			+ " where log.state_id=pcc.state_id and log.product_id=pcc.product_id; ";
	
	private String check_cell_notexists = "select t.state_id, t.product_id, coalesce (sum(l.totalSales),0) "
			+ " from (select distinct log.state_id, log.product_id, pcc.totalSales from log log left join precomputeCell pcc "
			+ " on log.state_id=pcc.state_id and log.product_id=pcc.product_id) t, log l "
			+ " where t.totalSales is null and l.state_id=t.state_id and l.product_id=t.product_id  "
			+ " group by t.state_id, t.product_id;";
	
	private String insert_cell= "insert into precomputeCell(state_id, product_id, totalSales) values (?,?,?); ";
	
	private String update_cell = "update precomputeCell pcc set totalSales = pcc.totalSales + t.totalSales "
			+ " from ( select log.state_id, log.product_id, coalesce(sum(log.totalSales),0) as totalSales FROM log log  "
			+ " GROUP BY log.state_id, log.product_id) t "
			+ " where t.state_id = pcc.state_id and t.product_id = pcc.product_id and pcc.state_id=? and pcc.product_id=?;";

	private String red_product_sum = "select totalSales" + " from precomputeProduct" + " where product_id = ?";

	private String red_state_sum = "select totalSales" + " from precomputeState" + " where state_id = ? and category_id = ? ";

	private Connection con;

	private ArrayList<String> newStateName = new ArrayList<String>();
	private ArrayList<Double> newStateSum = new ArrayList<Double>();
	private ArrayList<Integer> newStateId = new ArrayList<Integer>();
	private ArrayList<String> newProductName = new ArrayList<String>();
	private ArrayList<Double> newProductSum = new ArrayList<Double>();
	private ArrayList<Integer> newProductId = new ArrayList<Integer>();
	double[][] newCells = new double[56][50];

	private ArrayList<String> oldStateName = new ArrayList<String>();
	private ArrayList<Double> oldStateSum = new ArrayList<Double>();
	private ArrayList<Integer> oldStateId = new ArrayList<Integer>();
	private ArrayList<String> oldProductName = new ArrayList<String>();
	private ArrayList<Double> oldProductSum = new ArrayList<Double>();
	private ArrayList<Integer> oldProductId = new ArrayList<Integer>();
	double[][] oldCells = new double[56][50];

	private ArrayList<String> purpleProductName = new ArrayList<String>();
	private ArrayList<Double> purpleTextSum = new ArrayList<Double>();
	private ArrayList<String> purpleTextName = new ArrayList<String>();

	private ArrayList<Double> redStateSum = new ArrayList<Double>();
	private ArrayList<Double> redProductSum = new ArrayList<Double>();

	public AnalyticsDAO3(Connection con) {
		this.con = con;
	}
    public AnalyticsDAO3() {
        super();
    }

	public void updatePrecompute() throws SQLException {
		
		Statement stmt = null;
		PreparedStatement pstmt = null;
		ResultSet rs=null;
		stmt = con.createStatement();
		try {
			stmt.executeUpdate(update_product);
			stmt.executeUpdate(update_state_category);
			stmt.executeUpdate(update_state_allCategory);
			
			rs=stmt.executeQuery(check_cell_exists);			
			//precompute exists log
			while(rs.next()){
				int state_id=rs.getInt(1);
				int product_id=rs.getInt(2);
				pstmt = con.prepareStatement(update_cell);
				pstmt.setInt(1,state_id);
				pstmt.setInt(2,product_id);
				pstmt.executeUpdate();	
			}
			
			rs=stmt.executeQuery(check_cell_notexists);
			//precompute not exists log
			while(rs.next()){
				int state_id=rs.getInt(1);
				int product_id=rs.getInt(2);
				double totalSales=rs.getDouble(3);
				pstmt = con.prepareStatement(insert_cell);
				pstmt.setInt(1,state_id);
				pstmt.setInt(2,product_id);
				pstmt.setDouble(3,totalSales);
				pstmt.executeUpdate();	
			}
					
			stmt.executeUpdate("truncate table log");
			con.commit();
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (stmt != null) {
				try {
					stmt.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public void setState(ResultSet rs, String mode) throws SQLException {
		try {
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				double sum = rs.getDouble(3);
				if (mode.equals("new")) {
					newStateId.add(id);
					newStateName.add(name);
					newStateSum.add(sum);
				} else if (mode.equals("old")) {
					oldStateId.add(id);
					oldStateName.add(name);
					oldStateSum.add(sum);

				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public void setProduct(ResultSet rs, String mode) throws SQLException {
		try {
			while (rs.next()) {
				int id = rs.getInt(1);
				String name = rs.getString(2);
				if (name.length() >= 10) {
					name = name.substring(0, 9);
				}
				double sum = rs.getDouble(3);
				if (mode.equals("new")) {
					newProductId.add(id);
					newProductName.add(name);
					newProductSum.add(sum);
				} else if (mode.equals("old")) {
					oldProductId.add(id);
					oldProductName.add(name);
					oldProductSum.add(sum);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		}
	}

	public String refresh(int filter_dropdown, ArrayList<String> oldStateName, ArrayList<Double> oldStateSum, 
			ArrayList<String> oldProductName, ArrayList<Double> oldProductSum, ArrayList<Integer> oldProductId, 
			ArrayList<Integer> oldStateId, double[][] oldCells) throws SQLException {
		ResultSet rs = null;
		PreparedStatement pstmt = null;

		newStateId = new ArrayList<Integer>();
		newStateName = new ArrayList<String>();
		newStateSum = new ArrayList<Double>();
		newProductId = new ArrayList<Integer>();
		newProductName = new ArrayList<String>();
		newProductSum = new ArrayList<Double>();		
		purpleProductName = new ArrayList<String>();
		purpleTextSum = new ArrayList<Double>();
		purpleTextName = new ArrayList<String>();
		redStateSum = new ArrayList<Double>();
		redProductSum = new ArrayList<Double>();
		newCells = new double[56][50];
		
		
		this.oldStateName = oldStateName ;
		this.oldStateSum = oldStateSum;
		this.oldProductName= oldProductName;
		this.oldProductSum=oldProductSum;
		this.oldProductId=oldProductId;
		this.oldStateId=oldStateId;
		this.oldCells=oldCells;
	
		updatePrecompute();
		String alls;
		try {
			if (filter_dropdown == 0) {

				pstmt = con.prepareStatement(state_topK_allCategory);
				rs = pstmt.executeQuery();
				setState(rs, "new");

				pstmt = con.prepareStatement(product_topK_allCategory);
				rs = pstmt.executeQuery();
				setProduct(rs, "new");

				// set cell
				for (int i = 0; i < oldStateName.size(); i++) {
					for (int j = 0; j < oldProductName.size(); j++) {
						pstmt = con.prepareStatement(state_product_topK_category);
						pstmt.setInt(1, oldStateId.get(i));
						pstmt.setInt(2, oldProductId.get(j));
						rs = pstmt.executeQuery();
						while (rs.next()) {
							newCells[i][j] = rs.getInt(1);
						}
					}
				}
			}

			else if (filter_dropdown != 0) {
				pstmt = con.prepareStatement(state_topK_category);
				pstmt.setInt(1, filter_dropdown);
				rs = pstmt.executeQuery();
				setState(rs, "new");

				pstmt = con.prepareStatement(product_topK_category);
				pstmt.setInt(1, filter_dropdown);
				rs = pstmt.executeQuery();
				setProduct(rs, "new");

				for (int i = 0; i < oldStateName.size(); i++) {
					for (int j = 0; j < oldProductName.size(); j++) {
						pstmt = con.prepareStatement(state_product_topK_category);
						pstmt.setInt(1, oldStateId.get(i));
						pstmt.setInt(2, oldProductId.get(j));
						rs = pstmt.executeQuery();
						while (rs.next()) {
							newCells[i][j] = rs.getInt(1);
						}
					}
				}
			}
			
			for (int i = 0; i < oldProductName.size(); i++) {
				boolean found = false;
				for (int j = 0; j < newProductName.size(); j++) {
					if (oldProductId.get(i).equals(newProductId.get(j))) {
						found = true;
						break;
					}
				}
				if (found == false) {
					purpleProductName.add(oldProductName.get(i));
				}
			}

			// purple text
			
			for (int i = 0; i < newProductName.size(); i++) {
				boolean found = false;
				for (int j = 0; j < oldProductName.size(); j++) {
					if (oldProductId.get(j).equals(newProductId.get(i))) {
						found = true;
						break;
					}
				}
				if (found == false) {
					purpleTextName.add(newProductName.get(i));
					purpleTextSum.add(newProductSum.get(i));
				}
			}

			// red product
			for (int i = 0; i < oldProductName.size(); i++) {
				pstmt = con.prepareStatement(red_product_sum);
				pstmt.setInt(1, oldProductId.get(i));
				rs = pstmt.executeQuery();
				while (rs.next()) {
					redProductSum.add(rs.getDouble(1));
				}
			}

			// red state
			for (int i = 0; i < oldStateId.size(); i++) {
				pstmt = con.prepareStatement(red_state_sum);
				pstmt.setInt(1, oldStateId.get(i));
				pstmt.setInt(2, filter_dropdown);
				rs = pstmt.executeQuery();
				while (rs.next()) {
					redStateSum.add(rs.getDouble(1));
				}
			}
			
			String s1 = (new Gson()).toJson(oldStateName);
			String s2 = (new Gson()).toJson(oldStateSum);
			String s3 = (new Gson()).toJson(oldProductName);
			String s4 = (new Gson()).toJson(oldProductSum);
			String s5 = (new Gson()).toJson(oldCells);
			String s6 = (new Gson()).toJson(purpleProductName);
			String s7 = (new Gson()).toJson(purpleTextSum);
			String s8 = (new Gson()).toJson(purpleTextName);
			String s9 = (new Gson()).toJson(redStateSum);
			String s10 = (new Gson()).toJson(redProductSum);
			String s11 = (new Gson()).toJson(newCells);
			
			alls = "[" + s1 + "," + s2 + "," + s3 + "," + s4 + "," + s5 + "," + s6 + "," + s7 + "," + s8 + "," + s9
					+ "," + s10 + "," + s11 + "]";
			

		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
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
		return alls;
	}

	public AnalyticsModel2 Run(int filter_dropdown) throws SQLException {
		ResultSet rs = null;
		PreparedStatement pstmt = null;
		AnalyticsModel2 analyticsModel;
		updatePrecompute();

		try {
			if (filter_dropdown == 0) {
				pstmt = con.prepareStatement(state_topK_allCategory);
				rs = pstmt.executeQuery();
				setState(rs, "old");
				pstmt = con.prepareStatement(product_topK_allCategory);
				rs = pstmt.executeQuery();
				setProduct(rs, "old");

				for (int i = 0; i < oldStateId.size(); i++) {
					for (int j = 0; j < oldProductId.size(); j++) {
						pstmt = con.prepareStatement(state_product_topK_category);
						pstmt.setInt(1, oldStateId.get(i));
						pstmt.setInt(2, oldProductId.get(j));
						rs = pstmt.executeQuery();
						while (rs.next()) {
							oldCells[i][j] = rs.getInt(1);
						}
					}
				}
			} else if (filter_dropdown != 0) {
				pstmt = con.prepareStatement(state_topK_category);
				pstmt.setInt(1, filter_dropdown);
				rs = pstmt.executeQuery();
				setState(rs, "old");

				pstmt = con.prepareStatement(product_topK_category);
				pstmt.setInt(1, filter_dropdown);
				rs = pstmt.executeQuery();
				setProduct(rs, "old");

				for (int i = 0; i < oldStateId.size(); i++) {
					for (int j = 0; j < oldProductId.size(); j++) {
						pstmt = con.prepareStatement(state_product_topK_category);
						pstmt.setInt(1, oldStateId.get(i));
						pstmt.setInt(2, oldProductId.get(j));
						rs = pstmt.executeQuery();
						while (rs.next()) {
							oldCells[i][j] = rs.getInt(1);
						}
					}
				}
			}
			analyticsModel = new AnalyticsModel2(oldStateName, oldStateSum, oldProductName, oldProductSum, oldCells, oldStateId, oldProductId);
		} catch (SQLException e) {
			e.printStackTrace();
			throw e;
		} finally {
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		if(con == null)
			con = ConnectionManager.getConnection();
		
		//System.out.println();
		HttpSession session=request.getSession();
		int filter_dropdown  = (int) session.getAttribute("filter_dropdown");
		
		
		ArrayList<String> oldStateName=(ArrayList<String>)session.getAttribute("oldStateName");
		ArrayList<Double> oldStateSum=(ArrayList<Double>)session.getAttribute("oldStateSum");
		ArrayList<String> oldProductName=(ArrayList<String>)session.getAttribute("oldProductName");
		ArrayList<Double> oldProductSum=(ArrayList<Double>)session.getAttribute("oldProductSum");
		ArrayList<Integer> oldProductId=(ArrayList<Integer>)session.getAttribute("oldProductId");
		ArrayList<Integer> oldStateId=(ArrayList<Integer>)session.getAttribute("oldStateId");
		double[][] oldCells= (double[][]) session.getAttribute("oldCells");
		
			
		String alls = "";
		try {
			alls = refresh(filter_dropdown, oldStateName, oldStateSum, oldProductName, oldProductSum, oldProductId, oldStateId, oldCells);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		session.setAttribute("oldStateSum", redStateSum);
		session.setAttribute("oldProductSum", redProductSum);
		session.setAttribute("oldCells", newCells);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(alls);
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
