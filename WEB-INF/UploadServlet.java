import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.util.JSON;

import java.io.*;
import java.util.zip.*;

@WebServlet("/UploadServlet")
@MultipartConfig(fileSizeThreshold=1024*1024*200,	// 2MB 
				 maxFileSize=1024*1024*1000,		// 10MB
				 maxRequestSize=1024*1024*5000)	// 50MB
public class UploadServlet extends HttpServlet {

	/**
	 * Name of the directory where uploaded files will be saved, relative to
	 * the web application directory.
	 */
	private static final String SAVE_DIR = "uploadFiles";

	String DIAGNOSTIC;
	
	/**
	 * handles file upload
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		// gets absolute path of the web application
		String appPath = request.getServletContext().getRealPath("");
		// constructs path of the directory to save uploaded file
		String savePath = appPath + File.separator + SAVE_DIR;
		
		// creates the save directory if it does not exists
		File fileSaveDir = new File(savePath);
		if (!fileSaveDir.exists()) {
			fileSaveDir.mkdir();
		}
		
		String fullPath = "";
	
		for (Part part : request.getParts()) {
			String fileName = extractFileName(part);
			fullPath = savePath + File.separator + fileName;	
			part.write(fullPath);
		}
		
		String id = idGenerator();
		fullPath = fullPath.replace("//","/");

		System.out.println("fullPath: " + fullPath);
		String[] argv = {fullPath};
		this.UnZip(files);

		request.setAttribute("message", "Upload has been done successfully!\nYour database ID is " + id+ " "+fullPath+"\nDIAGNOSTIC: "+DIAGNOSTIC);
		getServletContext().getRequestDispatcher("/message.jsp").forward(
				request, response);

		MongoClient mc = new MongoClient();
		DB db = mc.getDB("MasterDB");
		DBCollection coll = db.getCollection(id);
		String input = new String(Files.readAllBytes(Paths.get(fullPath)), Charset.defaultCharset());
		DBObject o = (DBObject) JSON.parse(input);
		coll.insert(o);
	}

	/**
	 * Extracts file name from HTTP header content-disposition
	 */
	private String extractFileName(Part part) {
		String contentDisp = part.getHeader("content-disposition");
		String[] items = contentDisp.split(";");
		for (String s : items) {
			if (s.trim().startsWith("filename")) {
				return s.substring(s.indexOf("=") + 2, s.length()-1);
			}
		}
		return "";
	}

	private String idGenerator(){
		return (""+System.currentTimeMillis());
	}

	public void UnZip(String[] argv) {
		final int BUFFER = 2048;
		try {
			BufferedOutputStream dest = null;
			FileInputStream fis = new 
			FileInputStream(argv[0]);
			ZipInputStream zis = new 
			ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry;
		while((entry = zis.getNextEntry()) != null) {
			System.out.println("Extracting: " +entry);
			int count;
			byte data[] = new byte[BUFFER];
			// write the files to the disk
			FileOutputStream fos = new 
			FileOutputStream(entry.getName());
			dest = new 
			BufferedOutputStream(fos, BUFFER);
			while ((count = zis.read(data, 0, BUFFER)) != -1) {
				dest.write(data, 0, count);
			}
			dest.flush();
			dest.close();
		}
			zis.close();
			System.out.println("entry: "+entry.getName());
			DIAGNOSTIC = "ENTRY="+entry.getName();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}



}
