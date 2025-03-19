
package com.aytomonzon.librosac;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.JOptionPane;

/**
 * Esta clase es una colección de funciones utiles por otras clases
 * @author albertoaraguas
 * @version 1.0
 * 
 */
public class Herramientas {

    /**
    *
    * Constructor de la clase
    */
    public Herramientas() {
    }
    
    /**
    *
    * conversor de texto a numero racional
    * @param texto con el numero en formato string
    * @return el numero racional en formato double
    */  
    public double convertirTextoADouble(String texto) {
        // Reemplaza las comas por puntos
         if (texto.isEmpty()) {
            texto = "0";
        }
        String textoConPuntos = texto.replace(',', '.');
        // Intenta convertir el texto modificado a double
        try {
            return Double.parseDouble(textoConPuntos);
        } catch (NumberFormatException e) {
           JOptionPane.showMessageDialog(null, "Error conversion de texto a double", "Warning", JOptionPane.WARNING_MESSAGE);
            return 0.0; // O algún valor por defecto
        }
    
    }
    
    /**
    *
    * conversor de texto con punto a texto con coma
    * @param texto el texto con punto
    * @return el texto con coma
    */  
    public String convertirTextoAComa(String texto) {
        // Reemplaza los puntos por comas
        if (texto.isEmpty()) {
            texto = "0";
        }
        String textoConComas = texto.replace('.', ',');
        return textoConComas;
    }
    
    /**
    *
    * conversor de texto con coma a texto con punto
    * @param texto el texto con coma
    * @return el texto con punto
    */  
    public String convertirComaAPunto(String texto) {
        // Reemplaza los puntos por comas
        if (texto.isEmpty()) {
            texto = "0";
        }
        String textoConComas = texto.replace(',', '.');
        return textoConComas;
    }
    
    /**
    *
    * conversor de formato fecha
    * @param fechaOriginal con la fecha
    * @param sentido de tipo booleano con el sentido de conversión
    * @return el texto modificado
    */
    public String cambiarOrdenFecha(String fechaOriginal,boolean sentido) {
        // Define el formato de fecha original y nuevo
        SimpleDateFormat formatoOriginal, formatoNuevo;
        if (sentido){
             formatoOriginal = new SimpleDateFormat("dd/MM/yyyy");
             formatoNuevo= new SimpleDateFormat("yyyy/MM/dd");
        }else{
             formatoNuevo = new SimpleDateFormat("dd/MM/yyyy");
             formatoOriginal = new SimpleDateFormat("yyyy/MM/dd");
        }
        try {
            // Parsea la fecha original y luego la formatea en el nuevo formato
            Date fecha = formatoOriginal.parse(fechaOriginal);
            return formatoNuevo.format(fecha);
        } catch (ParseException e) {
            // Manejo de errores de análisis de fecha
            JOptionPane.showMessageDialog(null, "Error formato fechas", "Warning", JOptionPane.WARNING_MESSAGE);
            return null; // O alguna indicación de error
        }
    }
        
}