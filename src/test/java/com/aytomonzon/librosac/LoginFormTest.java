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
public class LoginFormTest {
    
     private LoginForm instance;
    
    @BeforeEach
    public void setUp() {
        instance = new LoginForm();
    }
    
    @AfterEach
    public void tearDown() {
        instance = null;
    }

    /**
     * Test del método DevolverLogin de la clase LoginForm.
     */
    @Test
    public void testDevolverLogin() {
        System.out.println("DevolverLogin");
        
        String[] result = instance.DevolverLogin();
        
        assertNotNull(result, "El resultado no debe ser nulo");
        assertEquals(2, result.length, "El array debe contener exactamente 2 elementos (usuario y contraseña)");
    }
    
}
