/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aytomonzon.librosac;

import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.JOptionPane;

/**
 * Esta clase gestiona los usuarios de la base de datos y ademas tiene la logica del login
 * @author albertoaraguas
 * @version 1.0
 * 
 */
public class UsuarioController {
    private final ConectorDB basedatos;
    String sentenciaSQL;
   
    public UsuarioController(ConectorDB basedatos) {
        // Establecer conexión a la base de datos SQLite
        this.basedatos=basedatos;
        
    }

    // Función para agregar un nuevo usuario y contraseña
    public boolean agregarUsuario(String nombre, String password) {
        sentenciaSQL="INSERT INTO usuarios (Nombre, Contraseña) VALUES ( '"+nombre+"','"+password+"')";
        try {
           basedatos.consultaSQL(sentenciaSQL);
           return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al agregar usuario: " + e.getMessage(), "Alerta", JOptionPane.WARNING_MESSAGE, null);
            return false;
        }
    }

    // Función para eliminar un usuario
    public boolean eliminarUsuario(String nombre) {
        sentenciaSQL="DELETE FROM usuarios WHERE nombre = '"+nombre+"'";
        try {
            basedatos.consultaSQL(sentenciaSQL);
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al eliminar el usuario: " + e.getMessage(), "Alerta", JOptionPane.WARNING_MESSAGE, null);
            return false;
        }
    }

    // Función para cambiar la contraseña de un usuario
    public boolean cambiarContrasena(String nombre, String nuevaContrasena) {
       sentenciaSQL="UPDATE usuarios SET Contraseña ='"+nuevaContrasena+"' WHERE Nombre ='"+nombre+"'";
       try {
            basedatos.consultaSQL(sentenciaSQL);
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al modificar la contraseña: " + e.getMessage(), "Alerta", JOptionPane.WARNING_MESSAGE, null);
            return false;
        }
    }
    //formatea imagen icono de la ventana
    public Image prepareImage(String imagePath, int targetWidth, int targetHeight) {
        Image originalImage = Toolkit.getDefaultToolkit().getImage(imagePath);
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        return resultingImage;
    }

    //login al programa devuelve true si coinciden
    public String login() {
        String[] userInput=null;
        try{    
            String[] usuarios = basedatos.getTrabajadores();
            LoginForm log =new LoginForm(usuarios);
            log.setTitle("INTRODUCE LA CONTRASEÑA");
            log.setLocationRelativeTo(null);
            log.setIconImage(prepareImage("C:/librocaja/images/ico.jpg", 32, 32));
            log.setVisible(true);
            while (log.isVisible()){
                try{
                    Thread.sleep(100);
                }catch (InterruptedException e){
                    JOptionPane.showMessageDialog(null, "Error de consistencia", "Alerta", JOptionPane.WARNING_MESSAGE, null);
                    
                }
            }
            userInput=log.DevolverLogin();
        }catch (HeadlessException e){
            JOptionPane.showMessageDialog(null, "Error en base de datos "+e, "Alerta", JOptionPane.WARNING_MESSAGE, null);
            basedatos.CerrarConexion();
        }
       //recuperamos de la base de datos la contraseña y la checkeamos
       try {
            String[] datosAcceso=basedatos.RecuperaAccesoUsuario(userInput[0]);
             if (datosAcceso[1].equalsIgnoreCase(userInput[1]) ){
                return datosAcceso[0];
             }else{
                return "Falso";
             }
            
       } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error de acceso a la base de datos", "Alerta", JOptionPane.WARNING_MESSAGE, null);
            return "Falso";
       }
    }
    
}
