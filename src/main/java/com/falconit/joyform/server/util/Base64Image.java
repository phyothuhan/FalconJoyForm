/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.falconit.joyform.server.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;

/**
 *
 * @author User
 */
public class Base64Image {
    
      
    public static void main(String[] args) {
        String imagePath = "D:\\1_Moe_Thant_Oo.jpg";
        System.out.println("=================Encoder Image to Base 64!=================");
        String base64ImageString = encoder(imagePath);
        System.out.println("Base64ImageString = " + base64ImageString);

        System.out.println("=================Decoder Base64ImageString to Image!=================");
        decoder(base64ImageString, "D:\\1_Moe_Thant_Oo_Decode.jpg");

        System.out.println("DONE!");
        
        String date = new SimpleDateFormat("d").format( new java.util.Date() );
        System.out.println("day=" + date);
  }
        public static String encoder( byte imageData[] ) {
            String base64Image = "";
            
            try{
                base64Image = Base64.getEncoder().encodeToString(imageData);
            } catch (Exception e) {
              System.out.println("Image not found" + e);
            }
            return base64Image;
      }
        
    public static String encoder(String imagePath) {
        String base64Image = "";
        File file = new File(imagePath);
        try (FileInputStream imageInFile = new FileInputStream(file)) {
          // Reading a Image file from file system
          byte imageData[] = new byte[(int) file.length()];
          imageInFile.read(imageData);
          base64Image = Base64.getEncoder().encodeToString(imageData);
        } catch (FileNotFoundException e) {
          System.out.println("Image not found" + e);
        } catch (IOException ioe) {
          System.out.println("Exception while reading the Image " + ioe);
        }
        return base64Image;
  }
    
      
    public static void decoder(String base64Image, String pathFile) {
        try (FileOutputStream imageOutFile = new FileOutputStream(pathFile)) {
          // Converting a Base64 String into Image byte array
          byte[] imageByteArray = Base64.getDecoder().decode(base64Image);
          imageOutFile.write(imageByteArray);
        } catch (FileNotFoundException e) {
          System.out.println("Image not found" + e);
        } catch (IOException ioe) {
          System.out.println("Exception while reading the Image " + ioe);
        }
  }
        
    public static byte[] decoder(String base64Image ) {
        byte[] imageByteArray = null;
        try{
          // Converting a Base64 String into Image byte array
          imageByteArray = Base64.getDecoder().decode(base64Image);
        } catch ( Exception e) {
          System.out.println("Image not found" + e);
        }
        return imageByteArray;
  }
}
