package util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class GestorArchivos {
    
    public GestorArchivos() {
        crearDirectoriosDatos();
    }
    
    public List<String> leerArchivo(String rutaArchivo) throws IOException {
        List<String> lineas = new ArrayList<>();
        Path path = Paths.get(rutaArchivo);
        
        if (!Files.exists(path)) {
            crearArchivo(rutaArchivo);
            return lineas;
        }
        
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                lineas.add(linea);
            }
        }
        
        return lineas;
    }
    
    public void escribirArchivo(String rutaArchivo, List<String> lineas) throws IOException {
        Path path = Paths.get(rutaArchivo);
        
        crearDirectorioSiNoExiste(path.getParent());
        
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (String linea : lineas) {
                writer.write(linea);
                writer.newLine();
            }
        }
    }
    
    public void agregarLineaArchivo(String rutaArchivo, String linea) throws IOException {
        Path path = Paths.get(rutaArchivo);
        
        if (!Files.exists(path)) {
            crearArchivo(rutaArchivo);
        }
        
        try (BufferedWriter writer = Files.newBufferedWriter(path, 
                java.nio.file.StandardOpenOption.APPEND)) {
            writer.write(linea);
            writer.newLine();
        }
    }
    
    public boolean existeArchivo(String rutaArchivo) {
        return Files.exists(Paths.get(rutaArchivo));
    }
    
    public void crearArchivo(String rutaArchivo) throws IOException {
        Path path = Paths.get(rutaArchivo);
        crearDirectorioSiNoExiste(path.getParent());
        
        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }
    
    public void eliminarArchivo(String rutaArchivo) throws IOException {
        Path path = Paths.get(rutaArchivo);
        if (Files.exists(path)) {
            Files.delete(path);
        }
    }
    
    public long obtenerTamanoArchivo(String rutaArchivo) throws IOException {
        Path path = Paths.get(rutaArchivo);
        if (Files.exists(path)) {
            return Files.size(path);
        }
        return 0;
    }
    
    public void crearCopiaSeguridad(String rutaArchivo) throws IOException {
        if (!existeArchivo(rutaArchivo)) {
            return;
        }
        
        String rutaCopia = rutaArchivo + ".backup";
        Path archivoOriginal = Paths.get(rutaArchivo);
        Path archivoCopia = Paths.get(rutaCopia);
        
        Files.copy(archivoOriginal, archivoCopia, 
                  java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }
    
    public boolean validarFormatoArchivo(String rutaArchivo, String separador) {
        try {
            List<String> lineas = leerArchivo(rutaArchivo);
            for (String linea : lineas) {
                if (!linea.trim().isEmpty() && !linea.contains(separador)) {
                    return false;
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }
    
    public int contarLineasArchivo(String rutaArchivo) throws IOException {
        if (!existeArchivo(rutaArchivo)) {
            return 0;
        }
        
        List<String> lineas = leerArchivo(rutaArchivo);
        int contador = 0;
        for (String linea : lineas) {
            if (!linea.trim().isEmpty()) {
                contador++;
            }
        }
        return contador;
    }
    
    private void crearDirectoriosDatos() {
        try {
            crearDirectorioSiNoExiste(Paths.get("datos"));
        } catch (IOException e) {
            System.err.println("Error creando directorio de datos: " + e.getMessage());
        }
    }
    
    private void crearDirectorioSiNoExiste(Path directorio) throws IOException {
        if (directorio != null && !Files.exists(directorio)) {
            Files.createDirectories(directorio);
        }
    }
}