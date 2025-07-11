package vista;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import controlador.ControladorObjetivos;
import modelo.Objetivo;
import modelo.Categoria;
import modelo.RegistroProgreso;
import util.ServicioRecordatorios;

public class VentanaPrincipal extends JFrame {
    
    private ControladorObjetivos controlador;
    private JTable tablaObjetivos;
    private DefaultTableModel modeloTabla;
    private JComboBox<String> cmbFiltroCategoria;
    private JComboBox<String> cmbFiltroEstado;
    private JLabel lblResumen;
    private JProgressBar barraProgreso;
    
    private JButton btnNuevo;
    private JButton btnEditar;
    private JButton btnEliminar;
    private JButton btnActualizarProgreso;
    private JButton btnVerHistorial;
    private JButton btnActualizar;
    
    // Componentes para recordatorios
    private ServicioRecordatorios servicioRecordatorios;
    private JMenuItem itemRecordatorios;
    private JButton btnRecordatorios;
    private JLabel lblRecordatorios;
    
    public VentanaPrincipal(ControladorObjetivos controlador) {
        this.controlador = controlador;
        inicializarComponentes();
        configurarVentana();
        actualizarTabla();
        actualizarResumen();
        inicializarServicioRecordatorios();
    }
    
    /**
     * Inicializa el servicio de recordatorios
     */
    private void inicializarServicioRecordatorios() {
        // Crear servicio de recordatorios con 7 días de anticipación
        servicioRecordatorios = new ServicioRecordatorios(7);
        
        // Agregar observador para mostrar notificaciones
        servicioRecordatorios.agregarObservador(this::mostrarRecordatorios);
        
        // Iniciar el servicio con verificación cada 5 minutos (300000 ms)
        // Para pruebas, se puede reducir a 10 segundos (10000 ms)
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    List<Objetivo> objetivos = controlador.obtenerTodosLosObjetivos();
                    servicioRecordatorios.verificarRecordatorios(objetivos);
                });
            }
        }, 2000, 300000); // 2 segundos de retraso inicial, luego cada 5 minutos
    }
    
    /**
     * Muestra los recordatorios de objetivos próximos a vencer
     * @param objetivosProximos Lista de objetivos próximos a vencer
     */
    private void mostrarRecordatorios(List<Objetivo> objetivosProximos) {
        if (objetivosProximos != null && !objetivosProximos.isEmpty()) {
            // Actualizar indicador visual
            if (lblRecordatorios != null) {
                lblRecordatorios.setText("¡" + objetivosProximos.size() + " objetivo(s) próximo(s) a vencer!");
                lblRecordatorios.setVisible(true);
            }
            
            // Mostrar diálogo de recordatorios al iniciar
            if (!servicioRecordatorios.isNotificacionesMostradas()) {
                DialogoRecordatorios dialogo = new DialogoRecordatorios(this, controlador, objetivosProximos);
                dialogo.setVisible(true);
            }
        } else {
            // Ocultar indicador si no hay objetivos próximos
            if (lblRecordatorios != null) {
                lblRecordatorios.setVisible(false);
            }
        }
    }
    
    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        
        // Panel superior - Filtros y controles
        JPanel panelSuperior = crearPanelSuperior();
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central - Tabla
        JPanel panelCentral = crearPanelTabla();
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior - Botones y resumen
        JPanel panelInferior = crearPanelInferior();
        add(panelInferior, BorderLayout.SOUTH);
        
        // Configurar eventos
        configurarEventos();
    }
    
    private JPanel crearPanelSuperior() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Filtros"));
        
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        // Filtro por categoría
        panelFiltros.add(new JLabel("Categoría:"));
        cmbFiltroCategoria = new JComboBox<>();
        cmbFiltroCategoria.addItem("Todas");
        cargarCategoriasEnFiltro();
        panelFiltros.add(cmbFiltroCategoria);
        
        // Filtro por estado
        panelFiltros.add(new JLabel("Estado:"));
        cmbFiltroEstado = new JComboBox<>();
        cmbFiltroEstado.addItem("Todos");
        cmbFiltroEstado.addItem("Activo");
        cmbFiltroEstado.addItem("Completado");
        panelFiltros.add(cmbFiltroEstado);
        
        // Botón actualizar
        btnActualizar = new JButton("Actualizar");
        panelFiltros.add(btnActualizar);
        
        panel.add(panelFiltros, BorderLayout.WEST);
        
        // Panel de recordatorios
        JPanel panelRecordatorios = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Etiqueta de recordatorios
        lblRecordatorios = new JLabel("¡Objetivos próximos a vencer!");
        lblRecordatorios.setForeground(new Color(204, 0, 0));
        lblRecordatorios.setFont(new Font(lblRecordatorios.getFont().getName(), Font.BOLD, 12));
        lblRecordatorios.setVisible(false); // Inicialmente oculto
        panelRecordatorios.add(lblRecordatorios);
        
        // Botón de recordatorios
        btnRecordatorios = new JButton("Ver Recordatorios");
        btnRecordatorios.addActionListener(e -> mostrarDialogoRecordatorios());
        panelRecordatorios.add(btnRecordatorios);
        
        panel.add(panelRecordatorios, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel crearPanelTabla() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Objetivos"));
        
        // Crear modelo de tabla
        String[] columnas = {"ID", "Nombre", "Categoría", "Progreso", "Meta", "%", "Estado", "Fecha Límite"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaObjetivos = new JTable(modeloTabla);
        tablaObjetivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaObjetivos.getTableHeader().setReorderingAllowed(false);
        
        // Configurar anchos de columnas
        tablaObjetivos.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        tablaObjetivos.getColumnModel().getColumn(1).setPreferredWidth(200); // Nombre
        tablaObjetivos.getColumnModel().getColumn(2).setPreferredWidth(100); // Categoría
        tablaObjetivos.getColumnModel().getColumn(3).setPreferredWidth(80);  // Progreso
        tablaObjetivos.getColumnModel().getColumn(4).setPreferredWidth(80);  // Meta
        tablaObjetivos.getColumnModel().getColumn(5).setPreferredWidth(60);  // %
        tablaObjetivos.getColumnModel().getColumn(6).setPreferredWidth(100); // Estado
        tablaObjetivos.getColumnModel().getColumn(7).setPreferredWidth(100); // Fecha
        
        // Renderer personalizado para la columna de porcentaje
        tablaObjetivos.getColumnModel().getColumn(5).setCellRenderer(new ProgressCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(tablaObjetivos);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel crearPanelInferior() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        
        btnNuevo = new JButton("Nuevo Objetivo");
        btnEditar = new JButton("Editar");
        btnEliminar = new JButton("Eliminar");
        btnActualizarProgreso = new JButton("Actualizar Progreso");
        btnVerHistorial = new JButton("Ver Historial");
        
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnActualizarProgreso.setEnabled(false);
        btnVerHistorial.setEnabled(false);
        
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEditar);
        panelBotones.add(btnEliminar);
        panelBotones.add(btnActualizarProgreso);
        panelBotones.add(btnVerHistorial);
        
        panel.add(panelBotones, BorderLayout.CENTER);
        
        // Panel de resumen
        JPanel panelResumen = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lblResumen = new JLabel("Objetivos: 0 | Completados: 0 | Activos: 0");
        panelResumen.add(lblResumen);
        
        panel.add(panelResumen, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void configurarEventos() {
        // Selección en tabla
        tablaObjetivos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                boolean seleccionado = tablaObjetivos.getSelectedRow() != -1;
                btnEditar.setEnabled(seleccionado);
                btnEliminar.setEnabled(seleccionado);
                btnActualizarProgreso.setEnabled(seleccionado);
                btnVerHistorial.setEnabled(seleccionado);
            }
        });
        
        // Doble clic en tabla para editar
        tablaObjetivos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && tablaObjetivos.getSelectedRow() != -1) {
                    editarObjetivo();
                }
            }
        });
        
        // Botones
        btnNuevo.addActionListener(e -> nuevoObjetivo());
        btnEditar.addActionListener(e -> editarObjetivo());
        btnEliminar.addActionListener(e -> eliminarObjetivo());
        btnActualizarProgreso.addActionListener(e -> actualizarProgreso());
        btnVerHistorial.addActionListener(e -> verHistorial());
        btnActualizar.addActionListener(e -> {
            actualizarTabla();
            actualizarResumen();
        });
        
        // Filtros
        cmbFiltroCategoria.addActionListener(e -> filtrarTabla());
        cmbFiltroEstado.addActionListener(e -> filtrarTabla());
    }
    
    private void cargarCategoriasEnFiltro() {
        List<Categoria> categorias = controlador.getControladorCategorias().obtenerTodasLasCategorias();
        for (Categoria categoria : categorias) {
            cmbFiltroCategoria.addItem(categoria.getNombre());
        }
    }
    
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        List<Objetivo> objetivos = controlador.obtenerTodosLosObjetivos();
        
        for (Objetivo objetivo : objetivos) {
            Categoria categoria = controlador.getControladorCategorias()
                .buscarCategoriaPorId(objetivo.getCategoriaId());
            String nombreCategoria = categoria != null ? categoria.getNombre() : "Sin categoría";
            
            double porcentaje = objetivo.calcularPorcentajeProgreso();
            String fechaLimite = objetivo.getFechaLimite() != null ? 
                objetivo.getFechaLimite().toString() : "Sin límite";
            
            Object[] fila = {
                objetivo.getId(),
                objetivo.getNombre(),
                nombreCategoria,
                objetivo.getProgresoActual(),
                objetivo.getMetaFinal(),
                String.format("%.1f%%", porcentaje),
                objetivo.getEstado(),
                fechaLimite
            };
            
            modeloTabla.addRow(fila);
        }
    }
    
    private void filtrarTabla() {
        String categoriaFiltro = (String) cmbFiltroCategoria.getSelectedItem();
        String estadoFiltro = (String) cmbFiltroEstado.getSelectedItem();
        
        modeloTabla.setRowCount(0);
        List<Objetivo> objetivos = controlador.obtenerTodosLosObjetivos();
        
        for (Objetivo objetivo : objetivos) {
            boolean incluir = true;
            
            // Filtro por categoría
            if (!"Todas".equals(categoriaFiltro)) {
                Categoria categoria = controlador.getControladorCategorias()
                    .buscarCategoriaPorId(objetivo.getCategoriaId());
                if (categoria == null || !categoria.getNombre().equals(categoriaFiltro)) {
                    incluir = false;
                }
            }
            
            // Filtro por estado
            if (incluir && !"Todos".equals(estadoFiltro)) {
                if (!objetivo.getEstado().equals(estadoFiltro)) {
                    incluir = false;
                }
            }
            
            if (incluir) {
                Categoria categoria = controlador.getControladorCategorias()
                    .buscarCategoriaPorId(objetivo.getCategoriaId());
                String nombreCategoria = categoria != null ? categoria.getNombre() : "Sin categoría";
                
                double porcentaje = objetivo.calcularPorcentajeProgreso();
                String fechaLimite = objetivo.getFechaLimite() != null ? 
                    objetivo.getFechaLimite().toString() : "Sin límite";
                
                Object[] fila = {
                    objetivo.getId(),
                    objetivo.getNombre(),
                    nombreCategoria,
                    objetivo.getProgresoActual(),
                    objetivo.getMetaFinal(),
                    String.format("%.1f%%", porcentaje),
                    objetivo.getEstado(),
                    fechaLimite
                };
                
                modeloTabla.addRow(fila);
            }
        }
        
        actualizarResumen();
    }
    
    private void actualizarResumen() {
        List<Objetivo> objetivos = controlador.obtenerTodosLosObjetivos();
        int total = objetivos.size();
        int completados = controlador.obtenerObjetivosCompletados().size();
        int activos = controlador.obtenerObjetivosActivos().size();
        
        lblResumen.setText(String.format("Objetivos: %d | Completados: %d | Activos: %d", 
            total, completados, activos));
    }
    
    private void nuevoObjetivo() {
        DialogoObjetivo dialogo = new DialogoObjetivo(this, controlador);
        dialogo.setVisible(true);
        
        if (dialogo.getResultado()) {
            actualizarTabla();
            actualizarResumen();
            JOptionPane.showMessageDialog(this, 
                "Objetivo creado exitosamente", 
                "Éxito", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void editarObjetivo() {
        int filaSeleccionada = tablaObjetivos.getSelectedRow();
        if (filaSeleccionada == -1) return;
        
        int id = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
        Objetivo objetivo = controlador.buscarObjetivoPorId(id);
        
        if (objetivo != null) {
            DialogoObjetivo dialogo = new DialogoObjetivo(this, controlador, objetivo);
            dialogo.setVisible(true);
            
            if (dialogo.getResultado()) {
                actualizarTabla();
                actualizarResumen();
                JOptionPane.showMessageDialog(this, 
                    "Objetivo actualizado exitosamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void eliminarObjetivo() {
        int filaSeleccionada = tablaObjetivos.getSelectedRow();
        if (filaSeleccionada == -1) return;
        
        int id = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
        String nombre = (String) modeloTabla.getValueAt(filaSeleccionada, 1);
        
        int confirmacion = JOptionPane.showConfirmDialog(this,
            "¿Está seguro de eliminar el objetivo '" + nombre + "'?\nEsta acción no se puede deshacer.",
            "Confirmar Eliminación",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirmacion == JOptionPane.YES_OPTION) {
            if (controlador.eliminarObjetivo(id)) {
                actualizarTabla();
                actualizarResumen();
                JOptionPane.showMessageDialog(this, 
                    "Objetivo eliminado exitosamente", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error al eliminar el objetivo", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void actualizarProgreso() {
        int filaSeleccionada = tablaObjetivos.getSelectedRow();
        if (filaSeleccionada == -1) return;
        
        int id = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
        Objetivo objetivo = controlador.buscarObjetivoPorId(id);
        
        if (objetivo != null) {
            String input = JOptionPane.showInputDialog(this,
                "Ingrese el nuevo progreso para '" + objetivo.getNombre() + "':\n" +
                "Progreso actual: " + objetivo.getProgresoActual() + "/" + objetivo.getMetaFinal(),
                "Actualizar Progreso",
                JOptionPane.QUESTION_MESSAGE);
            
            if (input != null && !input.trim().isEmpty()) {
                try {
                    int nuevoProgreso = Integer.parseInt(input.trim());
                    
                    String comentario = JOptionPane.showInputDialog(this,
                        "Comentario (opcional):",
                        "Comentario del Progreso",
                        JOptionPane.QUESTION_MESSAGE);
                    
                    if (controlador.actualizarProgreso(id, nuevoProgreso, comentario)) {
                        actualizarTabla();
                        actualizarResumen();
                        JOptionPane.showMessageDialog(this, 
                            "Progreso actualizado exitosamente", 
                            "Éxito", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Error al actualizar el progreso.\nVerifique que el valor esté entre 0 y " + objetivo.getMetaFinal(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, 
                        "Ingrese un número válido", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void verHistorial() {
        int filaSeleccionada = tablaObjetivos.getSelectedRow();
        if (filaSeleccionada == -1) return;
        
        int id = (Integer) modeloTabla.getValueAt(filaSeleccionada, 0);
        Objetivo objetivo = controlador.buscarObjetivoPorId(id);
        
        if (objetivo != null) {
            List<RegistroProgreso> historial = controlador.obtenerHistorialProgreso(id);
            
            if (historial.isEmpty()) {
                JOptionPane.showMessageDialog(this, 
                    "No hay historial de progreso para este objetivo", 
                    "Historial Vacío", 
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            // Crear ventana de historial
            JDialog dialogoHistorial = new JDialog(this, "Historial de Progreso - " + objetivo.getNombre(), true);
            dialogoHistorial.setLayout(new BorderLayout());
            
            // Crear tabla de historial
            String[] columnasHistorial = {"Fecha", "Progreso Anterior", "Progreso Nuevo", "Diferencia", "Comentarios"};
            DefaultTableModel modeloHistorial = new DefaultTableModel(columnasHistorial, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            for (RegistroProgreso registro : historial) {
                Object[] fila = {
                    registro.getFechaActualizacion().toString(),
                    registro.getProgresoAnterior(),
                    registro.getProgresoNuevo(),
                    (registro.getProgresoNuevo() - registro.getProgresoAnterior() >= 0 ? "+" : "") + 
                    (registro.getProgresoNuevo() - registro.getProgresoAnterior()),
                    registro.getComentarios()
                };
                modeloHistorial.addRow(fila);
            }
            
            JTable tablaHistorial = new JTable(modeloHistorial);
            JScrollPane scrollHistorial = new JScrollPane(tablaHistorial);
            
            dialogoHistorial.add(scrollHistorial, BorderLayout.CENTER);
            
            JButton btnCerrar = new JButton("Cerrar");
            btnCerrar.addActionListener(e -> dialogoHistorial.dispose());
            JPanel panelBoton = new JPanel(new FlowLayout());
            panelBoton.add(btnCerrar);
            dialogoHistorial.add(panelBoton, BorderLayout.SOUTH);
            
            dialogoHistorial.setSize(600, 400);
            dialogoHistorial.setLocationRelativeTo(this);
            dialogoHistorial.setVisible(true);
        }
    }
    
    private void configurarVentana() {
        setTitle("Tracker de Objetivos Personales");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        setMinimumSize(new Dimension(800, 500));
        
        // Icono de la aplicación (opcional)
        try {
            setIconImage(Toolkit.getDefaultToolkit().getImage("icon.png"));
        } catch (Exception e) {
            // Si no encuentra el icono, continúa sin él
        }
        
        // Crear barra de menú
        crearBarraMenu();
    }
    
    private void crearBarraMenu() {
        JMenuBar barraMenu = new JMenuBar();
        
        // Menú Archivo
        JMenu menuArchivo = new JMenu("Archivo");
        
        JMenuItem itemNuevo = new JMenuItem("Nuevo Objetivo");
        itemNuevo.setAccelerator(KeyStroke.getKeyStroke("ctrl N"));
        itemNuevo.addActionListener(e -> nuevoObjetivo());
        
        JMenuItem itemActualizar = new JMenuItem("Actualizar");
        itemActualizar.setAccelerator(KeyStroke.getKeyStroke("F5"));
        itemActualizar.addActionListener(e -> {
            actualizarTabla();
            actualizarResumen();
        });
        
        JMenuItem itemSalir = new JMenuItem("Salir");
        itemSalir.setAccelerator(KeyStroke.getKeyStroke("ctrl Q"));
        itemSalir.addActionListener(e -> System.exit(0));
        
        menuArchivo.add(itemNuevo);
        menuArchivo.addSeparator();
        menuArchivo.add(itemActualizar);
        menuArchivo.addSeparator();
        menuArchivo.add(itemSalir);
        
        // Menú Editar
        JMenu menuEditar = new JMenu("Editar");
        
        JMenuItem itemEditar = new JMenuItem("Editar Objetivo");
        itemEditar.setAccelerator(KeyStroke.getKeyStroke("ctrl E"));
        itemEditar.addActionListener(e -> editarObjetivo());
        
        JMenuItem itemEliminar = new JMenuItem("Eliminar Objetivo");
        itemEliminar.setAccelerator(KeyStroke.getKeyStroke("DELETE"));
        itemEliminar.addActionListener(e -> eliminarObjetivo());
        
        JMenuItem itemProgreso = new JMenuItem("Actualizar Progreso");
        itemProgreso.setAccelerator(KeyStroke.getKeyStroke("ctrl U"));
        itemProgreso.addActionListener(e -> actualizarProgreso());
        
        menuEditar.add(itemEditar);
        menuEditar.add(itemEliminar);
        menuEditar.addSeparator();
        menuEditar.add(itemProgreso);
        
        // Menú Ver
        JMenu menuVer = new JMenu("Ver");
        
        JMenuItem itemHistorial = new JMenuItem("Ver Historial");
        itemHistorial.setAccelerator(KeyStroke.getKeyStroke("ctrl H"));
        itemHistorial.addActionListener(e -> verHistorial());
        
        JMenuItem itemEstadisticas = new JMenuItem("Estadísticas");
        itemEstadisticas.addActionListener(e -> mostrarEstadisticas());
        
        itemRecordatorios = new JMenuItem("Recordatorios");
        itemRecordatorios.setAccelerator(KeyStroke.getKeyStroke("ctrl R"));
        itemRecordatorios.addActionListener(e -> mostrarDialogoRecordatorios());
        
        menuVer.add(itemHistorial);
        menuVer.add(itemEstadisticas);
        menuVer.addSeparator();
        menuVer.add(itemRecordatorios);
        
        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        
        JMenuItem itemAcerca = new JMenuItem("Acerca de");
        itemAcerca.addActionListener(e -> mostrarAcercaDe());
        
        menuAyuda.add(itemAcerca);
        
        barraMenu.add(menuArchivo);
        barraMenu.add(menuEditar);
        barraMenu.add(menuVer);
        barraMenu.add(menuAyuda);
        
        setJMenuBar(barraMenu);
    }
    
    private void mostrarEstadisticas() {
        List<Objetivo> objetivos = controlador.obtenerTodosLosObjetivos();
        int total = objetivos.size();
        int completados = controlador.obtenerObjetivosCompletados().size();
        int activos = controlador.obtenerObjetivosActivos().size();
        
        double porcentajeCompletados = total > 0 ? (double) completados / total * 100 : 0;
        
        StringBuilder stats = new StringBuilder();
        stats.append("=== ESTADÍSTICAS GENERALES ===\n\n");
        stats.append("Total de objetivos: ").append(total).append("\n");
        stats.append("Objetivos completados: ").append(completados).append("\n");
        stats.append("Objetivos activos: ").append(activos).append("\n");
        stats.append("Porcentaje de éxito: ").append(String.format("%.1f%%", porcentajeCompletados)).append("\n\n");
        
        // Estadísticas por categoría
        stats.append("=== POR CATEGORÍA ===\n");
        List<Categoria> categorias = controlador.getControladorCategorias().obtenerTodasLasCategorias();
        for (Categoria categoria : categorias) {
            List<Objetivo> objetivosCategoria = controlador.obtenerObjetivosPorCategoria(categoria.getId());
            int totalCategoria = objetivosCategoria.size();
            int completadosCategoria = 0;
            for (Objetivo obj : objetivosCategoria) {
                if ("Completado".equals(obj.getEstado())) {
                    completadosCategoria++;
                }
            }
            
            if (totalCategoria > 0) {
                double porcentajeCategoria = (double) completadosCategoria / totalCategoria * 100;
                stats.append(categoria.getNombre()).append(": ")
                     .append(completadosCategoria).append("/").append(totalCategoria)
                     .append(" (").append(String.format("%.1f%%", porcentajeCategoria)).append(")\n");
            }
        }
        
        JTextArea areaTexto = new JTextArea(stats.toString());
        areaTexto.setEditable(false);
        areaTexto.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        
        JScrollPane scroll = new JScrollPane(areaTexto);
        scroll.setPreferredSize(new Dimension(400, 300));
        
        JOptionPane.showMessageDialog(this, scroll, "Estadísticas", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarAcercaDe() {
        String mensaje = "Tracker de Objetivos Personales\n" +
                        "Versión 1.0\n\n" +
                        "Aplicación para gestionar y hacer seguimiento\n" +
                        "de objetivos personales.\n\n" +
                        "Desarrollado en Java con Swing\n" +
                        "Patrón MVC implementado\n\n" +
                        "© 2024 - Código Original Propio";
        
        JOptionPane.showMessageDialog(this, mensaje, "Acerca de", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Muestra el diálogo de recordatorios con los objetivos próximos a vencer
     */
    private void mostrarDialogoRecordatorios() {
        List<Objetivo> objetivosProximos = controlador.obtenerObjetivosProximosVencer();
        
        if (objetivosProximos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "No hay objetivos próximos a vencer en los próximos " + 
                servicioRecordatorios.getDiasAnticipacion() + " días.",
                "Sin Recordatorios",
                JOptionPane.INFORMATION_MESSAGE);
        } else {
            DialogoRecordatorios dialogo = new DialogoRecordatorios(this, controlador, objetivosProximos);
            dialogo.setVisible(true);
            
            // Marcar como mostradas para evitar duplicados
            servicioRecordatorios.reiniciarNotificaciones();
        }
    }
    
    // Clase interna para renderizar barras de progreso en la tabla
    private class ProgressCellRenderer extends JProgressBar implements TableCellRenderer {
        
        public ProgressCellRenderer() {
            setStringPainted(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            String texto = value.toString();
            try {
                double porcentaje = Double.parseDouble(texto.replace("%", ""));
                setValue((int) porcentaje);
                setString(texto);
                
                // Colores según el progreso
                if (porcentaje >= 100) {
                    setBackground(new Color(76, 175, 80)); // Verde
                } else if (porcentaje >= 75) {
                    setBackground(new Color(255, 193, 7)); // Amarillo
                } else if (porcentaje >= 50) {
                    setBackground(new Color(255, 152, 0)); // Naranja
                } else {
                    setBackground(new Color(244, 67, 54)); // Rojo
                }
                
            } catch (NumberFormatException e) {
                setValue(0);
                setString("0%");
            }
            
            return this;
        }
    }
}