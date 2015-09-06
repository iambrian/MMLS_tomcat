import java.io.IOException;
import java.io.File;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.OutputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.StringTokenizer;

public class ExtractZipsServlet extends HttpServlet {

   protected void processRequest(HttpServletRequest request, 
      HttpServletResponse response)
   throws ServletException, IOException {    

      final Log log = LogFactory.getLog(ExtractZipsServlet.class);

      log.debug("Extract archives...");

      StringBuffer answer = new StringBuffer();
      int entryStart = 0;
      int entryEnd = 0;
      int endupEntries = 0;
      boolean notAcceptable = false;
      final String[] extensions = {".exe", ".bat", ".sh", 
         ".jar", ".zip", ".rar", ".ace"};
      String extension = "";
      ServletContext servletContext = null;
      String clientEmail = null;                        

      PrintWriter out = response.getWriter();
      response.setContentType("text/xml");
      response.setHeader("Cache-Control", "no-cache"); 
      response.setHeader("Pragma", "No-cache");
      response.setDateHeader("Expires",0);      

      HttpSession session = request.getSession(false);   

      if(session != null) 
      {
         servletContext = request.getSession(false).getServletContext();       
         clientEmail = (String)(session.getAttribute("email"));
      }

      String arhName = request.getParameter("arh_name"); 
      String entryS = request.getParameter("entry_start");
      String entryE = request.getParameter("entry_end");
      String endupentries = request.getParameter("endupentries");
      if(entryS != null) { entryStart = Integer.valueOf(entryS);  }
      if(entryE != null) { entryEnd = Integer.valueOf(entryE); }
      if(endupentries != null) { endupEntries = 
         Integer.valueOf(endupentries); }

      try{   
         if(clientEmail != null)       
         {
            String strDirectory = clientEmail+"DAT";

            // Extract the DAT archives
            File file = new File(servletContext.getRealPath(
               "//WEB-INF//users//"+strDirectory+"//" + arhName));

            if(file.exists())
            {
               if((entryStart != entryEnd)&&(entryEnd != 0))
               {   
                  FileInputStream fileInputStream = new 
                     FileInputStream(file);
                  ZipInputStream zipInputStream = new 
                     ZipInputStream(fileInputStream);

                  int skipentry = 0;
                  int nextentry = entryStart;

                  ZipEntry entry = zipInputStream.getNextEntry();
                  while((skipentry < entryStart)&&(entry != null))
                  { entry = zipInputStream.getNextEntry(); skipentry++; }

                  notAcceptable = false;
                  extension = "";
                  if(entry != null) { extension = 
                      (entry.getName()).substring(
                      (entry.getName()).length() - 4, 
                      (entry.getName()).length()); }               
                  for(int i = 0; i < extensions.length; i++)
                  { if((entry != null) && 
                      (extension.equalsIgnoreCase(extensions[i]))) { 
                       notAcceptable = true; break; } 
                  }

                  do {
                     if(!entry.isDirectory()&&(notAcceptable == false))
                     {
                        String fileName = "";
                        StringTokenizer stringTokenizer = new 
                           StringTokenizer(entry.getName(),"//");
                        while(stringTokenizer.hasMoreTokens()) { 
                           fileName = stringTokenizer.nextToken(); }                   

                        OutputStream outputStream = new 
                           FileOutputStream(servletContext.getRealPath(
                           "//WEB-INF//users//"+strDirectory+"//" + 
                           fileName));

                        byte[] buffer = new byte[1024];
                        int len;
                        while ((len = zipInputStream.read(buffer)) > 0) 
                        {
                           outputStream.write(buffer, 0, len);
                        }

                        try { if(outputStream != null) 
                          {outputStream.close();} 
                        } 
                        catch (Exception ex) { 
                          log.error("I can't close the stream ..."); 
                        } 
                     }

                     try { Thread.sleep(50); } catch (Exception ex) {}
                     entry = zipInputStream.getNextEntry();        
                     nextentry ++;

                     notAcceptable = false;
                     extension = "";
                     if(entry != null) { extension = 
                        (entry.getName()).substring(
                        (entry.getName()).length() - 4, 
                        (entry.getName()).length()); }
                     for(int i = 0; i < extensions.length; i++)
                     { if((entry != null) && 
                          (extension.equalsIgnoreCase(extensions[i]))) { 
                          notAcceptable = true; break; } 
                     }

                     if (((nextentry == entryEnd) && (entryEnd == 
                        endupEntries) && (endupEntries != 0))||
                        (((nextentry + 1) == entryEnd) && 
                        (entryEnd > endupEntries) && 
                        (endupEntries != 0)))
                     {
                        try { if(zipInputStream != null) {
                           zipInputStream.close();} 
                        } 
                        catch (Exception ex) { 
                           log.error("I can't close the stream ..."); 
                        }
                        try { if(fileInputStream != null) { 
                           fileInputStream.close();} 
                        } 
                        catch (Exception ex) { 
                           log.error("I can't close the stream ..."); 
                        }
                        try { 
                           Thread.sleep(100); file.delete(); 
                        } 
                        catch (Exception ex) {
                           log.error("I can't delete this archive " + 
                           file.getName() + "..."); 
                        }
                        break;
                     }

                     try { Thread.sleep(100); } catch (Exception ex) {}

                  } while((nextentry < entryEnd)&&(entry != null));

                  try { 
                     if(zipInputStream != null) {
                        zipInputStream.close();} 
                     } 
                     catch (Exception ex) {
                        log.error("I can't close the stream ..."); 
                     }
                  try { 
                     if(fileInputStream != null) {
                        fileInputStream.close();} 
                  } 
                  catch (Exception ex) { 
                     log.error("I can't close the stream ..."); 
                  }

                  answer.append("<success></success>");

               } else {
                  ZipFile zipFile = new ZipFile(file);        
                  int zipsize = zipFile.size();
                  int chunksize = (int)(zipsize/10);
                  //if there are less than 10 files
                  if(chunksize == 0) { chunksize = 1; } 

                  answer.append("<info><zipsize>" + zipsize + 
                      "</zipsize><chunksize>" + chunksize + 
                      "</chunksize></info>");

                  try{ zipFile.close(); } catch (Exception ex) { 
                     log.error("Unable to close zip file ..."); }
               }
            } else { answer.append(
                "<div style='position:relative;left:0px;" + 
                "top:15px;background-color:red;text-align:center;'>"+
                "<font size='2' face='Arial' color='#ffffff'>"+
                "The last message: "+ "Sorry, but the " + arhName + 
                " doesn't exist (the extract process stops here) ..." + 
                "</font></div>"); }
         } else {
            answer.append(
               "<div style='position:relative;left:0px;"+
               "top:15px;background-color:red;text-align:center;'>"+
               "<font size='2' face='Arial' color='#ffffff'>"+
               "The last message: "+ 
               "Sorry, but you are not logged in ..."+
               "</font></div>");
         }                
         out.println(answer);   
      } 
      catch(Exception e) { 
         log.error("An unexpected error occured ...");
         answer.delete(0, answer.length());
         answer.append(
           "<div style='position:relative;left:0px;top:15px;"+
           "background-color:red;text-align:center;'>"+
           "<font size='2' face='Arial' color='#ffffff'>"+
           "The last message: "+
           "Sorry, but an unexepected error ocurred ..."+
           "</font></div>");
         out.println(answer); 
      } 
      finally { if(out != null) {out.close();} }

   }

   /** Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
   protected void doGet(HttpServletRequest request,
      HttpServletResponse response) throws 
      ServletException, IOException {
      processRequest(request, response);
   }

   /** Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
   protected void doPost(HttpServletRequest request,
      HttpServletResponse response) throws 
      ServletException, IOException {
      processRequest(request, response);
   }

   /** Returns a short description of the servlet.
    */
   public String getServletInfo() {
      return "Short description";
   }
}