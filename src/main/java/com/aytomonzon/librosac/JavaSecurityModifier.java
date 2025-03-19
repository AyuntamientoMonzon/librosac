/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.aytomonzon.librosac;

import java.io.*;
import java.nio.file.*;
import java.util.List;

public class JavaSecurityModifier {

    public JavaSecurityModifier() {
     String javaSecurityPath = System.getProperty("java.home") +
                                  File.separator + "conf" + File.separator +
                                  "security" + File.separator + "java.security";

        if (modifyJavaSecurityFile(javaSecurityPath)) {
            System.out.println("TLSv1 y TLSv1.1 eliminados correctamente de java.security.");
        } else {
            System.out.println("No se realizaron cambios o hubo un error.");
        }
    }
    
    public static boolean modifyJavaSecurityFile(String filePath) {
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                System.err.println("El archivo java.security no fue encontrado.");
                return false;
            }

            // Leer el archivo
            List<String> lines = Files.readAllLines(file.toPath());
            boolean modified = false;

            // Crear una nueva lista con las modificaciones
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.startsWith("jdk.tls.disabledAlgorithms")) {
                    String updatedLine = line.replace("TLSv1,", "")
                                             .replace("TLSv1.1,", "")
                                             .replace(",TLSv1", "")
                                             .replace(",TLSv1.1", "");

                    if (!updatedLine.equals(line)) {
                        lines.set(i, updatedLine);
                        modified = true;
                    }
                    break;  // Solo modificamos la primera coincidencia
                }
            }

            // Guardar los cambios si se modificó algo
            if (modified) {
                Files.write(file.toPath(), lines, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING);
                return true;
            }
        } catch (IOException e) {
            System.err.println("Error al modificar el archivo: " + e.getMessage());
        }
        return false;
    }
}

