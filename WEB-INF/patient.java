// Import required java libraries 
import java.io.*; 
import javax.servlet.*; 
import javax.servlet.http.*;

// Mongo imports
import java.net.UnknownHostException;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBObject;
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
 
public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException, UnknownHostException { 
// Set response content type  
response.setContentType("text/html"); 
// Actual logic goes here. 
PrintWriter out = response.getWriter(); 
out.println("<h1>" + message + "</h1>"); 

String id = request.getParameter("id");
String type = request.getParameter("type");
if (id==null || type == null) {
	out.write("{''data'': {''type'':''articles'',''id'':''1''}}");
} else {

	JSONArray list = new JSONArray();
	out.write("\nA");
	MongoClient mongoClient;
	//	try{ 
			out.write("\nB");
			mongoClient = new MongoClient();
			out.write("\nC");	
			DB db = mongoClient.getDB("MasterDB");
		//	db.getCollectionInfos();
			out.write("\nD");
			DBCollection coll = db.getCollection(id);
			
			// BasicDBObject document = new BasicDBObject();
   //  			out.write(String.valueOf(db.collectionExists(id)));
			// document.put("user_id", "1");
   //  			System.out.println(coll.insert(document));

			out.write("\nE");
			DBCursor cursor = coll.find();
			out.write("\nF");			

			//out.write(cursor.count());
			int count = cursor.count();
			System.out.println("count:");
			System.out.println(count);
			out.write(Integer.toString(count));


			out.write("\nG");
			if(cursor.hasNext()) {
			 	
				DBObject o = cursor.next();
				
				out.write(((Double)(o.get(type))).toString());
				list.add(((Double)(o.get(type))).toString());
				
				out.write(type);
			}

			out.write(list.toString());
	//	} catch (UnknownHostException ex) {
	//		out.write("ERROR");
	//		StringWriter errors = new StringWriter();
	//		ex.printStackTrace(new PrintWriter(errors));
	//		out.write( errors.toString());
	//	}

	}

}
	 
 
public void destroy() {
 // do nothing. 
} 
} 
