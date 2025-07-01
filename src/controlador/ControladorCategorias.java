package controlador;

import modelo.Categoria;
import util.GestorArchivos;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class ControladorCategorias {
    
    private List<Categoria> categorias;
    private GestorArchivos gestorArchivos;
    private static final String ARCHIVO_CATEGORIAS = "datos/categorias.txt";
    
    public ControladorCategorias() {
        this.categorias = new ArrayList<>();
        this.gestorArchivos = new GestorArchivos();
        cargarCategorias();
        inicializarCategoriasDefecto();
    }
    
    public boolean agregarCategoria(String nombre, String descripcion, String color) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        
        if (existeCategoria(nombre)) {
            return false;
        }
        
        int nuevoId = obtenerSiguienteId();
        Categoria nuevaCategoria = new Categoria(
            nuevoId, 
            nombre.trim(), 
            descripcion != null ? descripcion.trim() : "",
            color != null ? color : "#3498db",
            LocalDate.now()
        );
        
        categorias.add(nuevaCategoria);
        guardarCategorias();
        return true;
    }
    
    public boolean actualizarCategoria(int id, String nombre, String descripcion, String color) {
        Categoria categoria = buscarCategoriaPorId(id);
        if (categoria == null) {
            return false;
        }
        
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        
        if (!categoria.getNombre().equals(nombre.trim()) && existeCategoria(nombre)) {
            return false;
        }
        
        categoria.setNombre(nombre.trim());
        categoria.setDescripcion(descripcion != null ? descripcion.trim() : "");
        categoria.setColor(color != null ? color : "#3498db");
        
        guardarCategorias();
        return true;
    }
    
    public boolean eliminarCategoria(int id) {
        Categoria categoria = buscarCategoriaPorId(id);
        if (categoria == null) {
            return false;
        }
        
        categorias.remove(categoria);
        guardarCategorias();
        return true;
    }
    
    public List<Categoria> obtenerTodasLasCategorias() {
        return new ArrayList<>(categorias);
    }
    
    public Categoria buscarCategoriaPorId(int id) {
        for (Categoria categoria : categorias) {
            if (categoria.getId() == id) {
                return categoria;
            }
        }
        return null;
    }
    
    public Categoria buscarCategoriaPorNombre(String nombre) {
        if (nombre == null) return null;
        
        for (Categoria categoria : categorias) {
            if (categoria.getNombre().equalsIgnoreCase(nombre.trim())) {
                return categoria;
            }
        }
        return null;
    }
    
    public boolean existeCategoria(String nombre) {
        return buscarCategoriaPorNombre(nombre) != null;
    }
    
    public int obtenerCantidadCategorias() {
        return categorias.size();
    }
    
    private int obtenerSiguienteId() {
        int maxId = 0;
        for (Categoria categoria : categorias) {
            if (categoria.getId() > maxId) {
                maxId = categoria.getId();
            }
        }
        return maxId + 1;
    }
    
    private void cargarCategorias() {
        try {
            List<String> lineas = gestorArchivos.leerArchivo(ARCHIVO_CATEGORIAS);
            for (String linea : lineas) {
                if (!linea.trim().isEmpty()) {
                    Categoria categoria = parsearCategoria(linea);
                    if (categoria != null) {
                        categorias.add(categoria);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error cargando categorías: " + e.getMessage());
        }
    }
    
    private void guardarCategorias() {
        try {
            List<String> lineas = new ArrayList<>();
            for (Categoria categoria : categorias) {
                lineas.add(formatearCategoria(categoria));
            }
            gestorArchivos.escribirArchivo(ARCHIVO_CATEGORIAS, lineas);
        } catch (Exception e) {
            System.err.println("Error guardando categorías: " + e.getMessage());
        }
    }
    
    private Categoria parsearCategoria(String linea) {
        try {
            String[] partes = linea.split("\\|");
            if (partes.length >= 4) {
                int id = Integer.parseInt(partes[0]);
                String nombre = partes[1];
                String descripcion = partes[2];
                String color = partes[3];
                LocalDate fechaCreacion = partes.length > 4 ? 
                    LocalDate.parse(partes[4]) : LocalDate.now();
                
                return new Categoria(id, nombre, descripcion, color, fechaCreacion);
            }
        } catch (Exception e) {
            System.err.println("Error parseando categoría: " + linea);
        }
        return null;
    }
    
    private String formatearCategoria(Categoria categoria) {
        return categoria.getId() + "|" +
               categoria.getNombre() + "|" +
               categoria.getDescripcion() + "|" +
               categoria.getColor() + "|" +
               categoria.getFechaCreacion().toString();
    }
    
    private void inicializarCategoriasDefecto() {
        if (categorias.isEmpty()) {
            agregarCategoria("Personal", "Objetivos personales y de crecimiento", "#e74c3c");
            agregarCategoria("Trabajo", "Objetivos profesionales y laborales", "#3498db");
            agregarCategoria("Salud", "Objetivos de salud y bienestar", "#2ecc71");
            agregarCategoria("Educación", "Objetivos de aprendizaje y estudio", "#f39c12");
        }
    }
}