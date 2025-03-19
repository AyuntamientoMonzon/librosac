/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.aytomonzon.librosac;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 *
 * @author albertoaraguas
 */
public class ConectorDBTest {
    
   private ConectorDB instance;
    
    @BeforeEach
    public void setUp() {
        instance = new ConectorDB("192.168.5.9:3306"); // Suponiendo que tenga un constructor sin parámetros
        instance.Conectar();
    }
    
    @AfterEach
    public void tearDown() {
        instance.CerrarConexion();
    }
    
    @Test
    public void testConectar() throws SQLException {
        assertTrue(instance.isConected(), "La conexión debería estar activa después de conectar");
    }
    
    @Test
    public void testCerrarConexion()throws SQLException{
        instance.CerrarConexion();
        assertFalse(instance.isConected(), "La conexión debería estar cerrada");
    }
    
   
    
    @Test
    public void testLeerDescripcionesTabla() {
        ArrayList<String> result = instance.leerDescripcionesTabla("libros", "titulo");
        assertNotNull(result, "La lista no debería ser nula");
    }
    
    @Test
    public void testGetTrabajadores() {
        String[] trabajadores = instance.getTrabajadores();
        assertNotNull(trabajadores, "El array de trabajadores no debería ser nulo");
    }
    
    @Test
    public void testUsuariosIntervalo() {
        List<String> result = instance.UsuariosIntervalo("1,2,3", "1", "2024-02-24");
        assertNotNull(result, "La lista de usuarios no debería ser nula");
    }
    
    @Test
    public void testConsulta() {
        DefaultTableModel result = instance.consulta("SELECT * FROM usuarios");
        assertNotNull(result, "El resultado de la consulta no debería ser nulo");
    }
    
}
