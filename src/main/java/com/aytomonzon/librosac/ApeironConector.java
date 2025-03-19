
package com.aytomonzon.librosac;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Clase para la conexion con la BBDD de Apeiron
 * 
 * @author albertoaraguas
 * @version 1.0
 * @since 2025-02-25
 * 
 */
public class ApeironConector{
    
    private  final String URL="jdbc:sqlserver://192.168.0.16:52257;databaseName=apeironDat;sslProtocol=TLSv1";
    private  final String USER= "sa";
    private  final String PASSWORD="apeiron";
    private  Connection conn;


 /**
 *
 * constructor
 * 
 */  
    public ApeironConector() {
 }
       
    
 /**
 *
 * Función para la conexion con la bbdd 
 * @return  true si la conexion es exitosa
 */
public boolean Conectar(){
        try {
           conn = DriverManager.getConnection(URL, USER, PASSWORD); 
            return true;
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos:");
            e.printStackTrace();
            return false;
        }
    }
/**
 *
 * Función que devuelve nombre y direccion
 * @param tipoDoc integer 0 a 4
 * @param Documento String documento identificacion
 * @return  true si la conexion es exitosa
 * @throws java.sql.SQLException Error de ejecucion de consulta
 */
public  String[] getDatosApeiron(int tipoDoc, String Documento) throws SQLException{
    String[] resultado= new String[4];
    
    String sql = "SELECT p.nom,p.cog1,p.cog2, v.tipusVia,v.nom AS nomVia, d.numero,d.pis,d.porta"+
                " FROM dbo.persones p "+
                " JOIN dbo.personesDireccions pd ON p.idPersona = pd.idPersona "+
                " JOIN dbo.direccions d ON pd.idDireccio = d.idDireccio "+
                " JOIN dbo.idVies v ON d.idVia = v.idVia "+
                " WHERE tipusNip=? AND nip = ? AND pd.dataSortida IS NULL";
    
     if (conn != null) {
            PreparedStatement stmt = conn.prepareStatement(sql);
            // Set the parameter for the prepared statement
            stmt.setInt(1, tipoDoc);    
            stmt.setString(2, Documento);
            // Execute the query
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    resultado[0] = rs.getString("nom");
                    resultado[1] = rs.getString("cog1");
                    resultado[2] = rs.getString("cog2");
                    String tv = rs.getString("tipusVia");
                    String nv = rs.getString("nomVia");
                    String num = rs.getString("numero");
                    String pis = rs.getString("pis");
                    String port = rs.getString("porta");
                    resultado[3]= tv+" "+nv+" "+num+" "+pis+" "+port;
                }
                   
            }
            }
     return resultado;
    }
/**
 *
 * Función para la desconexion con la bbdd 
 * @return  true si la desconexion es exitosa
 */
public boolean Desconectar(){
        try {
           conn.close();
           return true;
        } catch (SQLException e) {
            System.out.println("Error al desconectar con la base de datos:");
           return false;
        }
    }
}