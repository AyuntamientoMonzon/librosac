/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aytomonzon.librosac;
import java.io.* ;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;


/**
 * Clase que comunica con la selladora
 * la configuracion del puerto com debe ser 9600, datos 8 bits sin paridad y sin control de flujo
 * @author albertoaraguas
 * @version 1.0
 */

public final class SerialSender {
    private static String numeroPuertoCom;

    public static void setNumeroPuertoCom(String numeroPuertoCom) {
        SerialSender.numeroPuertoCom = numeroPuertoCom;
    }

    public  static void imprimir(String s){
        try {
            FileWriter fw = new FileWriter("COM"+numeroPuertoCom+":");
            PrintWriter pw = new PrintWriter(fw);
            
           
            int i, len = s.length();

            for (i = 0; len > 80; i += 80) {
                pw.print(s.substring(i, i + 80));
                pw.print("\r\n");
                len -= 80;
            }

            if (len > 0) {
                pw.print(s.substring(i));
                pw.print("\r\n");
            }
             pw.flush();
            pw.close();
            } catch (IOException e) {
                System.out.println(e);
            }
    }
    
    
     public static void LiberarPapel(String puerto) {
       
       try {
           OutputStream outputStream = null;
           File fw = new File("COM"+puerto+":");
           outputStream = new FileOutputStream(fw);
           //el caracter ESC 23 (27 113)es para liberar el papel     
           byte[] bytesToSend = { 27,113 }; 
           outputStream.write(bytesToSend);
           outputStream.close();
       } catch (Exception e) {
                System.err.println(e);
       }
 
     }
     
    public static void SetCom(String nums){
     try {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "mode com"+nums+" 9600,n,8,1");
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("Exit Code: " + exitCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }
    
}