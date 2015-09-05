// Import required java libraries 
import java.io.*; 
import javax.servlet.*; 
import javax.servlet.http.*;

// Mongo imports
import java.net.UnknownHostException;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;

// Json imports
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
 
// Extend HttpServlet class 
public class patient extends HttpServlet { 
private String message; 
public void init() throws ServletException { 
// Do required initialization 
message = ""; 
} 
 
public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { 
// Set response content type  
response.setContentType("text/html"); 
// Actual logic goes here. 
PrintWriter out = response.getWriter(); 
out.println("<h1>" + message + "</h1>"); 

out.println(request.getParameter("id"));
out.println(request.getParameter("r"));


String id = request.getParameter("id");
String type = request.getParameter("type");

if (id==null || type == null) {
	out.write("error");
} else {

	JSONArray list = new JSONArray();

	MongoClient mongoClient;
		try {
			mongoClient = new MongoClient();	
			DB db = mongoClient.getDB("MasterDB");
			DBCollection coll = db.getCollection(id+"format");
			
			DBCursor cursor = coll.find();
			while(cursor.hasNext()) {
			    list.add(cursor.next());
			}

			out.write(list.toString());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

	}

}
	 
 
public void destroy() {
 // do nothing. 
} 
} 
