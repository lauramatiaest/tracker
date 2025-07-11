package util;

import modelo.Objetivo;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * Servicio para gestionar recordatorios de objetivos próximos a vencer
 */
public class ServicioRecordatorios {
    
    private Timer timer;
    private List<Consumer<List<Objetivo>>> observadores;
    private int diasAnticipacion;
    private boolean notificacionesMostradas;
    
    /**
     * Constructor del servicio de recordatorios
     * @param diasAnticipacion Días de anticipación para mostrar recordatorios
     */
    public ServicioRecordatorios(int diasAnticipacion) {
        this.timer = new Timer(true); // Daemon timer
        this.observadores = new ArrayList<>();
        this.diasAnticipacion = diasAnticipacion;
        this.notificacionesMostradas = false;
    }
    
    /**
     * Inicia el servicio de recordatorios
     * @param periodoRevision Periodo en milisegundos entre revisiones
     */
    public void iniciar(long periodoRevision) {
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                verificarRecordatorios();
            }
        }, 0, periodoRevision);
    }
    
    /**
     * Detiene el servicio de recordatorios
     */
    public void detener() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }
    
    /**
     * Agrega un observador para recibir notificaciones de recordatorios
     * @param observador Función que recibe la lista de objetivos próximos a vencer
     */
    public void agregarObservador(Consumer<List<Objetivo>> observador) {
        if (observador != null) {
            observadores.add(observador);
        }
    }
    
    /**
     * Elimina un observador
     * @param observador Observador a eliminar
     */
    public void eliminarObservador(Consumer<List<Objetivo>> observador) {
        if (observador != null) {
            observadores.remove(observador);
        }
    }
    
    /**
     * Verifica si hay objetivos próximos a vencer y notifica a los observadores
     * @param objetivos Lista de objetivos a verificar
     */
    public void verificarRecordatorios(List<Objetivo> objetivos) {
        if (objetivos == null || objetivos.isEmpty()) {
            return;
        }
        
        List<Objetivo> objetivosProximos = filtrarObjetivosProximos(objetivos);
        
        if (!objetivosProximos.isEmpty()) {
            notificarObservadores(objetivosProximos);
        }
        
        // Marcar que ya se mostraron notificaciones
        notificacionesMostradas = !objetivosProximos.isEmpty();
    }
    
    /**
     * Método interno para verificar recordatorios (usado por el timer)
     */
    private void verificarRecordatorios() {
        // Este método es llamado por el timer y debe ser implementado
        // por la clase que use este servicio
    }
    
    /**
     * Filtra los objetivos próximos a vencer según los días de anticipación configurados
     * @param objetivos Lista completa de objetivos
     * @return Lista de objetivos próximos a vencer
     */
    public List<Objetivo> filtrarObjetivosProximos(List<Objetivo> objetivos) {
        List<Objetivo> objetivosProximos = new ArrayList<>();
        LocalDate hoy = LocalDate.now();
        
        for (Objetivo objetivo : objetivos) {
            // Solo considerar objetivos activos con fecha límite
            if ("Activo".equals(objetivo.getEstado()) && objetivo.getFechaLimite() != null) {
                long diasRestantes = ChronoUnit.DAYS.between(hoy, objetivo.getFechaLimite());
                
                // Si está dentro del rango de anticipación y no ha vencido
                if (diasRestantes >= 0 && diasRestantes <= diasAnticipacion) {
                    objetivosProximos.add(objetivo);
                }
            }
        }
        
        return objetivosProximos;
    }
    
    /**
     * Notifica a todos los observadores sobre los objetivos próximos a vencer
     * @param objetivosProximos Lista de objetivos próximos a vencer
     */
    private void notificarObservadores(List<Objetivo> objetivosProximos) {
        for (Consumer<List<Objetivo>> observador : observadores) {
            observador.accept(objetivosProximos);
        }
    }
    
    /**
     * Verifica si ya se mostraron notificaciones
     * @return true si ya se mostraron notificaciones, false en caso contrario
     */
    public boolean isNotificacionesMostradas() {
        return notificacionesMostradas;
    }
    
    /**
     * Reinicia el estado de notificaciones mostradas
     */
    public void reiniciarNotificaciones() {
        this.notificacionesMostradas = false;
    }
    
    /**
     * Obtiene los días de anticipación configurados
     * @return Días de anticipación
     */
    public int getDiasAnticipacion() {
        return diasAnticipacion;
    }
    
    /**
     * Establece los días de anticipación para mostrar recordatorios
     * @param diasAnticipacion Días de anticipación
     */
    public void setDiasAnticipacion(int diasAnticipacion) {
        this.diasAnticipacion = diasAnticipacion;
    }
}