// Import required java libraries 
import java.io.*; 
import javax.servlet.*; 
import javax.servlet.http.*;

// import sql
import java.sql.*;
import java.util.*;

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
	String train = request.getParameter("train");
	String train_ticker = request.getParameter("train_ticker");
	
	if ((q==null || q.equals(""))&&(train==null || train.equals(""))&&(train_ticker==null || train_ticker.equals(""))) {
		out.write("[{\"status\": \"error\", \"data\": null, \"message\": \"Bad Request.\"}]");
	} else if (!(q==null || q.equals(""))) {
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
			sql = q ; // e.g. "SELECT symbol, close_p FROM price"
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList<Double> tickerPrices = null;
			String ticker = null;

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
		} else if (!(train==null || train.equals(""))) {
			if(train.equals("mysql")) {
				MySQLWrapper msw = new MySQLWrapper();
				String msw_results = msw.trainNetwork(train_ticker);
				out.write(msw_results);
			} else if(train.equals("mongo")) {
				MongoWrapper mw = new MongoWrapper();
				String mw_results = mw.trainNetwork();
				out.write(mw_results);
			} else if(train.equals("static")) {
				String s = readFile("/public/MMLS/finance_data.txt");
				out.write(s);
			}

		}
	}

	String readFile(String fileName) throws IOException {
    BufferedReader br = new BufferedReader(new FileReader(fileName));
    try {
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();

        while (line != null) {
            sb.append(line);
            sb.append("\n");
            line = br.readLine();
        }
        return sb.toString();
    } finally {
        br.close();
    }
}
} 
