/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.falconit.joyform.server.servlet;

import com.falconit.joyform.server.util.Base64Image;
import com.falconit.joyform.server.util.HTTPConnection;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 *
 * @author User
 */
public class FileUploadServlet extends HttpServlet {

    public static final String FACE_DETECTION_URL = "http://localhost:8090/autofacecentral";
    public static final String UPLOAD_PATH = "/form-service";
    public static final String FILE_TYPE_DOC = "doc";
    public static final String FILE_TYPE_IMAGE = "img";
    public static final String FILE_TYPE_FACIAL = "facial";
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String unitval = request.getParameter("unitval");
        String type = request.getParameter("type");
        
        if( type == null || type.isEmpty())
            type = FILE_TYPE_DOC;
        
        if( unitval == null || unitval.isEmpty())
            unitval = System.nanoTime() +"";
        
        if (! ServletFileUpload.isMultipartContent(request)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Not a multipart request");
            return;
        }
        
        ServletFileUpload upload = new ServletFileUpload(); // from Commons

        try {
            FileItemIterator iter = upload.getItemIterator(request);

            if (iter.hasNext()) {
                FileItemStream fileItem = iter.next( );

                //ServletOutputStream out = response.getOutputStream();
                response.setBufferSize(32768);

                InputStream in = fileItem.openStream();
                // The destination of your uploaded files.
                String realPath = getServletContext().getRealPath( UPLOAD_PATH );
                File rootFile = new File( realPath );
                if( !rootFile.exists() ) rootFile.mkdir();
 
                File file = new File(  rootFile, unitval + "-" + fileItem.getName() );
                OutputStream outputStream = new FileOutputStream( file );

                int length = 0;
                byte[] bytes = new byte[1024];

                while ((length = in.read(bytes)) != -1) {
                    outputStream.write(bytes, 0, length);
                }

                outputStream.close();
                
                response.setContentType("text/html;charset=UTF-8");
                //response.setContentLength( (length > 0 && length <= Integer.MAX_VALUE) ? (int) length : 0);

                
                PrintWriter out = response.getWriter();
                
                if( type.equals( FILE_TYPE_FACIAL ) ){
                    
                    JSONObject obj = new JSONObject( );
                    obj.put("service_name", "face_detect" );
                    String url = FACE_DETECTION_URL + "/api";
                    String image64 = Base64Image.encoder( file.getAbsolutePath() );
                    obj.put( "image", image64 );
                    String resp = new HTTPConnection( ).connect( url, obj.toJSONString() );
                    obj = (JSONObject) new JSONParser( ).parse( resp );
                    
                    if( obj.get("detect").toString().equalsIgnoreCase("true") ){
                        out.print( "OK" );
                    }else{
                        out.print( "No face" );
                    }
                    
                    
                    /*
                    boolean detected = detectFace( file );
                    System.out.println(detected);
                    if( detected ){
                        out.print( "OK" );
                    } else{
                        out.print( "No face" );
                    }
                    */
                }
                //out.flush();
                //in.close();
                //out.close();
            }
        } catch(Exception caught) {
            throw new RuntimeException(caught);
        }
    }
    
    /*
    private boolean detectFace( File file ){
        
        FaceDetection detect = new FaceDetection( );
        opencv_core.Mat testImage = imread( file.getAbsolutePath(), IMREAD_ANYCOLOR );
        java.util.List<opencv_core.Mat> capture = detect.caffemodel( testImage, false, false );
        System.out.println("Size="+capture.size());
        
        if( capture.size() > 0){
            return true;
        }
        else{
            return false;
        }
        
    }
    */

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
