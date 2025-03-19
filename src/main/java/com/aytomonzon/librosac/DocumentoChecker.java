/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aytomonzon.librosac;

/**
 * Esta clase valida el DNI, que tenga 8 cifras y la letra
 * @author albertoaraguas
 */
public class DocumentoChecker {
    /**
    * Constructor
    */
    public DocumentoChecker() {
    }
    
    /**
    * Funcion que verifica el numero de digitos y si son numeros y la letra de control
    * @param dni Texto con el DNI
    * @return True si esta ok y false si hay error
    */
    public boolean validarDNI(String dni) {
        // Comprobar que el DNI tiene 9 caracteres
        if (dni == null || dni.length() != 9) {
            return false;
        }
        // Separar los números de la letra
        String numero = dni.substring(0, 8);
        char letra = dni.charAt(8);
        // Comprobar que los primeros 8 caracteres son dígitos
        if (!numero.matches("\\d{8}")) {
            return false;
        }
        // Calcular la letra de control
        int numeroDNI = Integer.parseInt(numero);
        char letraCalculada = calcularLetraDNI(numeroDNI);
        // Comparar la letra calculada con la letra introducida
        return letra == letraCalculada;
    }
    private static char calcularLetraDNI(int numeroDNI) {
        // Array con las letras de control del DNI
        char[] letras = {'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H', 'L', 'C', 'K', 'E', 'T'};
        // Calcular la letra de control
        return letras[numeroDNI % 23];
    }
    /**
    * Funcion que verifica el numero de digitos y si son numeros y la letra de control
    * @param nie Texto con el NIE
    * @return True si esta ok y false si hay error
    */
    public boolean validarNIE(String nie) {
        // Comprobar que el NIE tiene 9 caracteres
        if (nie == null || nie.length() != 9) {
            return false;
        }
        // Comprobar que la primera letra es X, Y o Z
        char letraInicial = nie.charAt(0);
        if (letraInicial != 'X' && letraInicial != 'Y' && letraInicial != 'Z') {
            return false;
        }
        // Separar los números y la letra final
        String numeros = nie.substring(1, 8);
        char letraFinal = nie.charAt(8);
        // Comprobar que los siguientes 7 caracteres son dígitos
        if (!numeros.matches("\\d{7}")) {
            return false;
        }
        // Convertir la letra inicial a un número
        int numeroNIE = convertirLetraANumero(letraInicial, numeros);
        // Calcular la letra final
        char letraFinalCalculada = calcularLetraFinal(numeroNIE);
        // Comparar la letra final calculada con la letra introducida
        return letraFinal == letraFinalCalculada;
    }

    private static int convertirLetraANumero(char letraInicial, String numeros) {
        // Convertir la letra inicial a un número
        int numero = Integer.parseInt(numeros);
        if (letraInicial == 'Y') {
            numero += 10000000; // Para Y
        } else if (letraInicial == 'Z') {
            numero += 20000000; // Para Z
        }
        return numero;
    }

    private static char calcularLetraFinal(int numeroNIE) {
        // Letras para el cálculo del dígito de control
        String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
        return letras.charAt(numeroNIE % 23);
    }
    
    
}
