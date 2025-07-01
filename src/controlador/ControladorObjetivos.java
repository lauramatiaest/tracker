package controlador;

import modelo.Objetivo;
import modelo.Categoria;
import modelo.RegistroProgreso;
import util.GestorArchivos;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

public class ControladorObjetivos {
    
    private List<Objetivo> objetivos;
    private ControladorCategorias controladorCategorias;
    private List<RegistroProgreso> registrosProgreso;
    private GestorArchivos gestorArchivos;
    private static final String ARCHIVO_OBJETIVOS = "datos/objetivos.txt";
    private static final String ARCHIVO_PROGRESO = "datos/progreso.txt";
    
    public ControladorObjetivos() {
        this.objetivos = new ArrayList<>();
        this.registrosProgreso = new ArrayList<>();
        this.gestorArchivos = new GestorArchivos();
        this.controladorCategorias = new ControladorCategorias();
        cargarDatos();
    }
    
    public boolean crearObjetivo(String nombre, String descripcion, int metaFinal, 
                                String fechaLimite, int categoriaId, int prioridad) {
        if (nombre == null || nombre.trim().isEmpty() || metaFinal <= 0) {
            return false;
        }
        
        if (controladorCategorias.buscarCategoriaPorId(categoriaId) == null) {
            return false;
        }
        
        int nuevoId = obtenerSiguienteId();
        LocalDate fechaLim = null;
        
        try {
            if (fechaLimite != null && !fechaLimite.trim().isEmpty()) {
                fechaLim = LocalDate.parse(fechaLimite);
            }
        } catch (Exception e) {
            return false;
        }
        
        Objetivo nuevoObjetivo = new Objetivo(
            nuevoId,
            nombre.trim(),
            descripcion != null ? descripcion.trim() : "",
            0,
            metaFinal,
            LocalDate.now(),
            fechaLim,
            categoriaId,
            prioridad,
            "Activo"
        );
        
        objetivos.add(nuevoObjetivo);
        guardarObjetivos();
        return true;
    }
    
    public boolean actualizarObjetivo(int id, String nombre, String descripcion, 
                                    int metaFinal, String fechaLimite, int categoriaId, int prioridad) {
        Objetivo objetivo = buscarObjetivoPorId(id);
        if (objetivo == null || nombre == null || nombre.trim().isEmpty() || metaFinal <= 0) {
            return false;
        }
        
        if (controladorCategorias.buscarCategoriaPorId(categoriaId) == null) {
            return false;
        }
        
        LocalDate fechaLim = null;
        try {
            if (fechaLimite != null && !fechaLimite.trim().isEmpty()) {
                fechaLim = LocalDate.parse(fechaLimite);
            }
        } catch (Exception e) {
            return false;
        }
        
        objetivo.setNombre(nombre.trim());
        objetivo.setDescripcion(descripcion != null ? descripcion.trim() : "");
        objetivo.setMetaFinal(metaFinal);
        objetivo.setFechaLimite(fechaLim);
        objetivo.setCategoriaId(categoriaId);
        objetivo.setPrioridad(prioridad);
        
        guardarObjetivos();
        return true;
    }
    
    public boolean actualizarProgreso(int objetivoId, int nuevoProgreso, String comentario) {
        Objetivo objetivo = buscarObjetivoPorId(objetivoId);
        if (objetivo == null || nuevoProgreso < 0 || nuevoProgreso > objetivo.getMetaFinal()) {
            return false;
        }
        
        int progresoAnterior = objetivo.getProgresoActual();
        objetivo.setProgresoActual(nuevoProgreso);
        
        if (nuevoProgreso >= objetivo.getMetaFinal()) {
            objetivo.setEstado("Completado");
        } else if (objetivo.getEstado().equals("Completado") && nuevoProgreso < objetivo.getMetaFinal()) {
            objetivo.setEstado("Activo");
        }
        
        RegistroProgreso registro = new RegistroProgreso(
            obtenerSiguienteIdProgreso(),
            objetivoId,
            LocalDate.now(),
            progresoAnterior,
            nuevoProgreso,
            comentario != null ? comentario.trim() : ""
        );
        
        registrosProgreso.add(registro);
        guardarDatos();
        return true;
    }
    
    public boolean eliminarObjetivo(int id) {
        Objetivo objetivo = buscarObjetivoPorId(id);
        if (objetivo == null) {
            return false;
        }
        
        objetivos.remove(objetivo);
        registrosProgreso.removeIf(registro -> registro.getObjetivoId() == id);
        guardarDatos();
        return true;
    }
    
    public List<Objetivo> obtenerTodosLosObjetivos() {
        return new ArrayList<>(objetivos);
    }
    
    public List<Objetivo> obtenerObjetivosPorCategoria(int categoriaId) {
        List<Objetivo> objetivosFiltrados = new ArrayList<>();
        for (Objetivo objetivo : objetivos) {
            if (objetivo.getCategoriaId() == categoriaId) {
                objetivosFiltrados.add(objetivo);
            }
        }
        return objetivosFiltrados;
    }
    
    public List<Objetivo> obtenerObjetivosActivos() {
        List<Objetivo> objetivosActivos = new ArrayList<>();
        for (Objetivo objetivo : objetivos) {
            if ("Activo".equals(objetivo.getEstado())) {
                objetivosActivos.add(objetivo);
            }
        }
        return objetivosActivos;
    }
    
    public List<Objetivo> obtenerObjetivosCompletados() {
        List<Objetivo> objetivosCompletados = new ArrayList<>();
        for (Objetivo objetivo : objetivos) {
            if ("Completado".equals(objetivo.getEstado())) {
                objetivosCompletados.add(objetivo);
            }
        }
        return objetivosCompletados;
    }
    
    public Objetivo buscarObjetivoPorId(int id) {
        for (Objetivo objetivo : objetivos) {
            if (objetivo.getId() == id) {
                return objetivo;
            }
        }
        return null;
    }
    
    public List<RegistroProgreso> obtenerHistorialProgreso(int objetivoId) {
        List<RegistroProgreso> historial = new ArrayList<>();
        for (RegistroProgreso registro : registrosProgreso) {
            if (registro.getObjetivoId() == objetivoId) {
                historial.add(registro);
            }
        }
        return historial;
    }
    
    public ControladorCategorias getControladorCategorias() {
        return controladorCategorias;
    }
    
    public double calcularPorcentajeProgreso(int objetivoId) {
        Objetivo objetivo = buscarObjetivoPorId(objetivoId);
        if (objetivo == null || objetivo.getMetaFinal() == 0) {
            return 0.0;
        }
        return (double) objetivo.getProgresoActual() / objetivo.getMetaFinal() * 100.0;
    }
    
    public List<Objetivo> obtenerObjetivosProximosVencer() {
        List<Objetivo> proximosVencer = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        LocalDate proximaSemana = hoy.plusDays(7);
        
        for (Objetivo objetivo : objetivos) {
            if ("Activo".equals(objetivo.getEstado()) && 
                objetivo.getFechaLimite() != null &&
                !objetivo.getFechaLimite().isBefore(hoy) &&
                !objetivo.getFechaLimite().isAfter(proximaSemana)) {
                proximosVencer.add(objetivo);
            }
        }
        return proximosVencer;
    }
    
    private int obtenerSiguienteId() {
        int maxId = 0;
        for (Objetivo objetivo : objetivos) {
            if (objetivo.getId() > maxId) {
                maxId = objetivo.getId();
            }
        }
        return maxId + 1;
    }
    
    private int obtenerSiguienteIdProgreso() {
        int maxId = 0;
        for (RegistroProgreso registro : registrosProgreso) {
            if (registro.getId() > maxId) {
                maxId = registro.getId();
            }
        }
        return maxId + 1;
    }
    
    private void cargarDatos() {
        cargarObjetivos();
        cargarProgreso();
    }
    
    private void guardarDatos() {
        guardarObjetivos();
        guardarProgreso();
    }
    
    private void cargarObjetivos() {
        try {
            List<String> lineas = gestorArchivos.leerArchivo(ARCHIVO_OBJETIVOS);
            for (String linea : lineas) {
                if (!linea.trim().isEmpty()) {
                    Objetivo objetivo = parsearObjetivo(linea);
                    if (objetivo != null) {
                        objetivos.add(objetivo);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error cargando objetivos: " + e.getMessage());
        }
    }
    
    private void guardarObjetivos() {
        try {
            List<String> lineas = new ArrayList<>();
            for (Objetivo objetivo : objetivos) {
                lineas.add(formatearObjetivo(objetivo));
            }
            gestorArchivos.escribirArchivo(ARCHIVO_OBJETIVOS, lineas);
        } catch (Exception e) {
            System.err.println("Error guardando objetivos: " + e.getMessage());
        }
    }
    
    private void cargarProgreso() {
        try {
            List<String> lineas = gestorArchivos.leerArchivo(ARCHIVO_PROGRESO);
            for (String linea : lineas) {
                if (!linea.trim().isEmpty()) {
                    RegistroProgreso registro = parsearProgreso(linea);
                    if (registro != null) {
                        registrosProgreso.add(registro);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error cargando progreso: " + e.getMessage());
        }
    }
    
    private void guardarProgreso() {
        try {
            List<String> lineas = new ArrayList<>();
            for (RegistroProgreso registro : registrosProgreso) {
                lineas.add(formatearProgreso(registro));
            }
            gestorArchivos.escribirArchivo(ARCHIVO_PROGRESO, lineas);
        } catch (Exception e) {
            System.err.println("Error guardando progreso: " + e.getMessage());
        }
    }
    
    private Objetivo parsearObjetivo(String linea) {
        try {
            String[] partes = linea.split("\\|");
            if (partes.length >= 9) {
                int id = Integer.parseInt(partes[0]);
                String nombre = partes[1];
                String descripcion = partes[2];
                int progresoActual = Integer.parseInt(partes[3]);
                int metaFinal = Integer.parseInt(partes[4]);
                LocalDate fechaInicio = LocalDate.parse(partes[5]);
                LocalDate fechaLimite = partes[6].isEmpty() ? null : LocalDate.parse(partes[6]);
                int categoriaId = Integer.parseInt(partes[7]);
                int prioridad = Integer.parseInt(partes[8]);
                String estado = partes.length > 9 ? partes[9] : "Activo";
                
                return new Objetivo(id, nombre, descripcion, progresoActual, metaFinal,
                                  fechaInicio, fechaLimite, categoriaId, prioridad, estado);
            }
        } catch (Exception e) {
            System.err.println("Error parseando objetivo: " + linea);
        }
        return null;
    }
    
    private String formatearObjetivo(Objetivo objetivo) {
        return objetivo.getId() + "|" +
               objetivo.getNombre() + "|" +
               objetivo.getDescripcion() + "|" +
               objetivo.getProgresoActual() + "|" +
               objetivo.getMetaFinal() + "|" +
               objetivo.getFechaInicio().toString() + "|" +
               (objetivo.getFechaLimite() != null ? objetivo.getFechaLimite().toString() : "") + "|" +
               objetivo.getCategoriaId() + "|" +
               objetivo.getPrioridad() + "|" +
               objetivo.getEstado();
    }
    
    private RegistroProgreso parsearProgreso(String linea) {
        try {
            String[] partes = linea.split("\\|");
            if (partes.length >= 5) {
                int id = Integer.parseInt(partes[0]);
                int objetivoId = Integer.parseInt(partes[1]);
                LocalDate fecha = LocalDate.parse(partes[2]);
                int progresoAnterior = Integer.parseInt(partes[3]);
                int progresoNuevo = Integer.parseInt(partes[4]);
                String comentarios = partes.length > 5 ? partes[5] : "";
                
                return new RegistroProgreso(id, objetivoId, fecha, progresoAnterior, 
                                          progresoNuevo, comentarios);
            }
        } catch (Exception e) {
            System.err.println("Error parseando progreso: " + linea);
        }
        return null;
    }
    
    private String formatearProgreso(RegistroProgreso registro) {
        return registro.getId() + "|" +
               registro.getObjetivoId() + "|" +
               registro.getFechaActualizacion().toString() + "|" +
               registro.getProgresoAnterior() + "|" +
               registro.getProgresoNuevo() + "|" +
               registro.getComentarios();
    }
}