/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package com.aytomonzon.librosac;

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
public class HerramientasTest {
    
    public HerramientasTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of convertirTextoADouble method, of class Herramientas.
     */
    @Test
    public void testConvertirTextoADouble() {
        
        Herramientas instance = new Herramientas();

        assertEquals(0.0, instance.convertirTextoADouble(""), 0.001);
        assertEquals(123.45, instance.convertirTextoADouble("123.45"), 0.001);
        assertEquals(123.45, instance.convertirTextoADouble("123,45"), 0.001);
        assertEquals(-5.6, instance.convertirTextoADouble("-5.6"), 0.001);
        assertEquals(0.0, instance.convertirTextoADouble("abc"), 0.001); // Debe devolver 0.0 por error
  
    }

    /**
     * Test of convertirTextoAComa method, of class Herramientas.
     */
    @Test
    public void testConvertirTextoAComa() {
        Herramientas instance = new Herramientas();

        assertEquals("123,45", instance.convertirTextoAComa("123.45"));
        assertEquals("0,0", instance.convertirTextoAComa("0.0"));
        assertEquals("0", instance.convertirTextoAComa(""));
    }

    /**
     * Test of convertirComaAPunto method, of class Herramientas.
     */
    @Test
    public void testConvertirComaAPunto() {
            Herramientas instance = new Herramientas();

        assertEquals("123.45", instance.convertirComaAPunto("123,45"));
        assertEquals("0.0", instance.convertirComaAPunto("0,0"));
        assertEquals("0", instance.convertirComaAPunto(""));
    }

    /**
     * Test of cambiarOrdenFecha method, of class Herramientas.
     */
    @Test
    public void testCambiarOrdenFecha() {
              Herramientas instance = new Herramientas();

        assertEquals("2024/02/24", instance.cambiarOrdenFecha("24/02/2024", true));
        assertEquals("24/02/2024", instance.cambiarOrdenFecha("2024/02/24", false));
        //assertNull(instance.cambiarOrdenFecha("fecha incorrecta", true)); // Manejo de errores
        assertNull(instance.cambiarOrdenFecha("", true)); // Manejo de entrada vacía
        
    }
    
}
