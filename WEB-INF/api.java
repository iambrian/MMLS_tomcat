// Import required java libraries 
import java.io.*; 
import javax.servlet.*; 
import javax.servlet.http.*;


// import sql
import java.sql.*;
import java.util.*;

// import json
// import net.sf.json.JSONArray;
// import net.sf.json.JSONObject;

// Extend HttpServlet class 
public class api extends HttpServlet { 

	private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	private static final String DB_URL = "jdbc:mysql://localhost/stockDB";

	private static final String USER = "root";
	private static final String PASS = "4thegalaxytabs";
	private String message; 

	public void init() throws ServletException {} 
	 
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
	// Set response content type  
	response.setContentType("application/json"); 
	PrintWriter out = response.getWriter(); 

	String q = request.getParameter("q");
	
	if (q==null || q.equals("")) {
		out.write("[{\"status\": \"error\", \"data\": null, \"message\": \"Bad Request. Query cannot be null.\"}]");
	} else {
		out.write(q);

		Connection conn = null;
		Statement stmt = null;

		Map<String, Double[]>trainingData = new HashMap<String, Double[]>(); 

		try{
			Class.forName("com.mysql.jdbc.Driver");
			System.out.println("Connecting to database...");
			conn = DriverManager.getConnection(DB_URL,USER,PASS);
			System.out.println("Creating statement...");
			stmt = conn.createStatement();
			String sql;
			sql = q ;// e.g. "SELECT symbol, close_p FROM price"
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList<Double> tickerPrices = null;
			String ticker = null;

			// out.write(ResultSetConverter.convert(rs).toString());
			out.write(Convertor.convertToJSON(rs).toString());

			rs.close();
			stmt.close();
			conn.close();

		} catch(SQLException se){
			//Handle errors for JDBC
			se.printStackTrace();

			StringWriter errors = new StringWriter();
			se.printStackTrace(new PrintWriter(errors));
			out.write(errors.toString());
		}catch(Exception e){
			//Handle errors for Class.forName
			e.printStackTrace();

			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			out.write(errors.toString());
		}finally{
			//finally block used to close resources
			try{
				if(stmt!=null)
					stmt.close();
			}catch(SQLException se2){
				StringWriter errors = new StringWriter();
				se2.printStackTrace(new PrintWriter(errors));
				out.write(errors.toString());
			}// nothing we can do
			try{
				if(conn!=null)
					conn.close();
			}catch(SQLException se){
				se.printStackTrace();

				StringWriter errors = new StringWriter();
				se.printStackTrace(new PrintWriter(errors));
				out.write(errors.toString());
			}//end finally try
		}//end try
		}
	}




} 
