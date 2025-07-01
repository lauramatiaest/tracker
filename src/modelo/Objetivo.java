package modelo;

import java.time.LocalDate;

public class Objetivo {
    
    private int id;
    private String nombre;
    private String descripcion;
    private int progresoActual;
    private int metaFinal;
    private LocalDate fechaInicio;
    private LocalDate fechaLimite;
    private int categoriaId;
    private int prioridad;
    private String estado;
    
    public Objetivo() {
        this.fechaInicio = LocalDate.now();
        this.progresoActual = 0;
        this.prioridad = 1;
        this.estado = "Activo";
    }
    
    public Objetivo(int id, String nombre, String descripcion, int progresoActual, 
                   int metaFinal, LocalDate fechaInicio, LocalDate fechaLimite, 
                   int categoriaId, int prioridad, String estado) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.progresoActual = progresoActual;
        this.metaFinal = metaFinal;
        this.fechaInicio = fechaInicio;
        this.fechaLimite = fechaLimite;
        this.categoriaId = categoriaId;
        this.prioridad = prioridad;
        this.estado = estado;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
    public String getDescripcion() {
        return descripcion;
    }
    
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    
    public int getProgresoActual() {
        return progresoActual;
    }
    
    public void setProgresoActual(int progresoActual) {
        this.progresoActual = progresoActual;
    }
    
    public int getMetaFinal() {
        return metaFinal;
    }
    
    public void setMetaFinal(int metaFinal) {
        this.metaFinal = metaFinal;
    }
    
    public LocalDate getFechaInicio() {
        return fechaInicio;
    }
    
    public void setFechaInicio(LocalDate fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    
    public LocalDate getFechaLimite() {
        return fechaLimite;
    }
    
    public void setFechaLimite(LocalDate fechaLimite) {
        this.fechaLimite = fechaLimite;
    }
    
    public int getCategoriaId() {
        return categoriaId;
    }
    
    public void setCategoriaId(int categoriaId) {
        this.categoriaId = categoriaId;
    }
    
    public int getPrioridad() {
        return prioridad;
    }
    
    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
    
    public double calcularPorcentajeProgreso() {
        if (metaFinal == 0) return 0.0;
        return (double) progresoActual / metaFinal * 100.0;
    }
    
    public boolean estaCompletado() {
        return "Completado".equals(estado) || progresoActual >= metaFinal;
    }
    
    public boolean estaVencido() {
        if (fechaLimite == null || estaCompletado()) return false;
        return LocalDate.now().isAfter(fechaLimite);
    }
    
    public long diasRestantes() {
        if (fechaLimite == null) return -1;
        return LocalDate.now().until(fechaLimite).getDays();
    }
    
    @Override
    public String toString() {
        return nombre + " (" + progresoActual + "/" + metaFinal + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Objetivo objetivo = (Objetivo) obj;
        return id == objetivo.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}