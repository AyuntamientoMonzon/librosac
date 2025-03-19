
package com.aytomonzon.librosac;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.JOptionPane;

/**
 * Clase que maneja el archivo de config
 * 
 * @author albertoaraguas
 *  @version 1.0
 * @since 2025-02-25
 * 
 */
public class ConfigManager {
    private static final String CONFIG_FILE_PATH = "c:\\LibroCaja\\config.txt";

    /**
    *
    * Constructor de la clase
    */
    public ConfigManager() {
    }
    
    /**
    *
    * Guardar la configuracion en el archivo
    * @param configLines lineas de configurariocn
    */
    public static void saveConfig(List<String> configLines) {
        File configFile = new File(CONFIG_FILE_PATH);
        try {
            FileUtils.writeLines(configFile, configLines);
            JOptionPane.showMessageDialog(null, "Grabación correcta de la configuración", "Correcto", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error acceso al archivo config.txt", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
    *
    * Cargar la configuracion del archivo
    * @return Lista de string
    */
    public static List<String> loadConfig() {
        File configFile = new File(CONFIG_FILE_PATH);
        try {
            return FileUtils.readLines(configFile, "UTF-8");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error acceso al archivo config.txt", "Warning", JOptionPane.WARNING_MESSAGE);
            return null;
        }
    }
}
