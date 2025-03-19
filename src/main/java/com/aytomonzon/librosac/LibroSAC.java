

package com.aytomonzon.librosac;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.IOException;

/**
 * Esta clase Lanza la aplicacion
 * @author albertoaraguas
 * @version  1.0
 */
public class LibroSAC {
    /**
    * Constructor
    */
    public LibroSAC() {
    }
    
    /**
    *
    * Función inicial de la aplicacion 
     * @param args especificacion de argumentos 
     * @throws java.io.IOException excepcion  controlada
     * @throws java.lang.InterruptedException excepcion controlada
    */
    public static void main(String[] args) throws IOException, InterruptedException {
     
        Marco myWindow = new Marco();
        // Centrar el JFrame en la pantalla
        myWindow.setLocationRelativeTo(null);
        myWindow.setTitle("Libro de Caja SAC");
        myWindow.setIconImage(prepareImage("C:/librocaja/images/ico.jpg", 32, 32));
        myWindow.setVisible(true);
        
    }
    
    
    /**
    * Funcion que ajusta la imagen del icono de la aplicacion
    * @param imagePath ruta del icono
    * @param targetWidth ancho del icono
    * @param targetHeight altura del icono
    * @return image formateada
    */
    private static Image prepareImage(String imagePath, int targetWidth, int targetHeight) {
        Image originalImage = Toolkit.getDefaultToolkit().getImage(imagePath);
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        return resultingImage;
    }
}
