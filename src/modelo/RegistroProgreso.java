package modelo;

import java.time.LocalDate;

public class RegistroProgreso {
    
    private int id;
    private int objetivoId;
    private LocalDate fechaActualizacion;
    private int progresoAnterior;
    private int progresoNuevo;
    private String comentarios;
    
    public RegistroProgreso() {
        this.fechaActualizacion = LocalDate.now();
        this.comentarios = "";
    }
    
    public RegistroProgreso(int id, int objetivoId, LocalDate fechaActualizacion, 
                           int progresoAnterior, int progresoNuevo, String comentarios) {
        this.id = id;
        this.objetivoId = objetivoId;
        this.fechaActualizacion = fechaActualizacion;
        this.progresoAnterior = progresoAnterior;
        this.progresoNuevo = progresoNuevo;
        this.comentarios = comentarios;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getObjetivoId() {
        return objetivoId;
    }
    
    public void setObjetivoId(int objetivoId) {
        this.objetivoId = objetivoId;
    }
    
    public LocalDate getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDate fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
    
    public int getProgresoAnterior() {
        return progresoAnterior;
    }
    
    public void setProgresoAnterior(int progresoAnterior) {
        this.progresoAnterior = progresoAnterior;
    }
    
    public int getProgresoNuevo() {
        return progresoNuevo;
    }
    
    public void setProgresoNuevo(int progresoNuevo) {
        this.progresoNuevo = progresoNuevo;
    }
    
    public String getComentarios() {
        return comentarios;
    }
    
    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }
    
    public int getDiferencia() {
        return progresoNuevo - progresoAnterior;
    }
    
    public boolean esAvance() {
        return progresoNuevo > progresoAnterior;
    }
    
    public boolean esRetroceso() {
        return progresoNuevo < progresoAnterior;
    }
    
    public boolean esSinCambio() {
        return progresoNuevo == progresoAnterior;
    }
    
    @Override
    public String toString() {
        String cambio = "";
        if (esAvance()) {
            cambio = "Avance: +" + getDiferencia();
        } else if (esRetroceso()) {
            cambio = "Retroceso: " + getDiferencia();
        } else {
            cambio = "Sin cambio";
        }
        
        return fechaActualizacion + " - " + cambio + 
               " (" + progresoAnterior + " → " + progresoNuevo + ")";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        RegistroProgreso registro = (RegistroProgreso) obj;
        return id == registro.id;
    }
    
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
}