/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.falconit.joyform.server.util;

import java.io.DataOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author User
 */
public class HTTPConnection {
    
        public String connect( String urlAddress, String param ){
            String response = "";
                try {
                    URL url = new URL( urlAddress  );
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("POST");
//                    conn.setRequestProperty("Content-Type", "text/html;charset=UTF-8");
                    conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    conn.setRequestProperty("Accept","application/json");
//                    conn.setRequestProperty("Accept","text/html");
                    conn.setDoOutput(true);
                    conn.setDoInput(true);


                    DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                    os.writeBytes( param );
                    os.flush();
                    os.close();

                    System.out.println(String.valueOf(conn.getResponseCode()));
                    System.out.println( conn.getResponseMessage() );

                    if( conn.getResponseCode()== 200 || conn.getResponseCode() == 201 ) {
                        StringBuilder sb = new StringBuilder();
                        java.io.DataInputStream din = new java.io.DataInputStream(conn.getInputStream());
                        java.io.InputStreamReader reader = new java.io.InputStreamReader(din, "UTF-8");
                        java.io.BufferedReader bf = new java.io.BufferedReader(reader);
                        String line = "";
                        while ((line = bf.readLine()) != null) {
                            if (line == null) break;
                            sb.append(line);
                        }
                        response = sb.toString();
                        //System.out.println("Response:"+ response );
                        din.close();
                    }
                    conn.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            return response;
    }

}
