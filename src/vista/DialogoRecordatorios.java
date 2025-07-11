package vista;

import modelo.Objetivo;
import modelo.Categoria;
import controlador.ControladorObjetivos;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Diálogo para mostrar recordatorios de objetivos próximos a vencer
 */
public class DialogoRecordatorios extends JDialog {
    
    private ControladorObjetivos controlador;
    private JTable tablaObjetivos;
    private DefaultTableModel modeloTabla;
    private List<Objetivo> objetivosProximos;
    
    /**
     * Constructor del diálogo de recordatorios
     * @param parent Ventana padre
     * @param controlador Controlador de objetivos
     * @param objetivosProximos Lista de objetivos próximos a vencer
     */
    public DialogoRecordatorios(JFrame parent, ControladorObjetivos controlador, List<Objetivo> objetivosProximos) {
        super(parent, "Recordatorio de Objetivos Próximos", true);
        this.controlador = controlador;
        this.objetivosProximos = objetivosProximos;
        
        inicializarComponentes();
        cargarObjetivosProximos();
        
        setSize(700, 400);
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    /**
     * Inicializa los componentes del diálogo
     */
    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        
        // Panel superior con mensaje
        JPanel panelSuperior = new JPanel();
        JLabel lblMensaje = new JLabel("¡Tienes objetivos que están próximos a vencer!");
        lblMensaje.setFont(new Font(lblMensaje.getFont().getName(), Font.BOLD, 14));
        lblMensaje.setForeground(new Color(204, 0, 0));
        panelSuperior.add(lblMensaje);
        add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central con tabla de objetivos
        JPanel panelCentral = new JPanel(new BorderLayout());
        panelCentral.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Crear modelo de tabla
        String[] columnas = {"Nombre", "Categoría", "Progreso", "Fecha Límite", "Días Restantes"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tablaObjetivos = new JTable(modeloTabla);
        tablaObjetivos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaObjetivos.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(tablaObjetivos);
        panelCentral.add(scrollPane, BorderLayout.CENTER);
        add(panelCentral, BorderLayout.CENTER);
        
        // Panel inferior con botones
        JPanel panelInferior = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton btnVerDetalles = new JButton("Ver Detalles");
        btnVerDetalles.addActionListener(e -> verDetallesObjetivo());
        
        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> dispose());
        
        panelInferior.add(btnVerDetalles);
        panelInferior.add(btnCerrar);
        add(panelInferior, BorderLayout.SOUTH);
    }
    
    /**
     * Carga los objetivos próximos a vencer en la tabla
     */
    private void cargarObjetivosProximos() {
        modeloTabla.setRowCount(0);
        LocalDate hoy = LocalDate.now();
        
        for (Objetivo objetivo : objetivosProximos) {
            Categoria categoria = controlador.getControladorCategorias()
                .buscarCategoriaPorId(objetivo.getCategoriaId());
            String nombreCategoria = categoria != null ? categoria.getNombre() : "Sin categoría";
            
            String progreso = objetivo.getProgresoActual() + "/" + objetivo.getMetaFinal() + 
                " (" + String.format("%.1f%%", objetivo.calcularPorcentajeProgreso()) + ")";
            
            long diasRestantes = ChronoUnit.DAYS.between(hoy, objetivo.getFechaLimite());
            String diasRestantesStr = diasRestantes + " día" + (diasRestantes != 1 ? "s" : "");
            
            Object[] fila = {
                objetivo.getNombre(),
                nombreCategoria,
                progreso,
                objetivo.getFechaLimite().toString(),
                diasRestantesStr
            };
            
            modeloTabla.addRow(fila);
        }
        
        // Ajustar anchos de columnas
        tablaObjetivos.getColumnModel().getColumn(0).setPreferredWidth(200); // Nombre
        tablaObjetivos.getColumnModel().getColumn(1).setPreferredWidth(100); // Categoría
        tablaObjetivos.getColumnModel().getColumn(2).setPreferredWidth(100); // Progreso
        tablaObjetivos.getColumnModel().getColumn(3).setPreferredWidth(100); // Fecha Límite
        tablaObjetivos.getColumnModel().getColumn(4).setPreferredWidth(100); // Días Restantes
    }
    
    /**
     * Muestra los detalles del objetivo seleccionado
     */
    private void verDetallesObjetivo() {
        int filaSeleccionada = tablaObjetivos.getSelectedRow();
        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione un objetivo para ver sus detalles", 
                "Selección requerida", 
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String nombreObjetivo = (String) modeloTabla.getValueAt(filaSeleccionada, 0);
        Objetivo objetivoSeleccionado = null;
        
        for (Objetivo objetivo : objetivosProximos) {
            if (objetivo.getNombre().equals(nombreObjetivo)) {
                objetivoSeleccionado = objetivo;
                break;
            }
        }
        
        if (objetivoSeleccionado != null) {
            DialogoObjetivo dialogo = new DialogoObjetivo(
                (JFrame) SwingUtilities.getWindowAncestor(this), 
                controlador, 
                objetivoSeleccionado
            );
            dispose(); // Cerrar este diálogo
            dialogo.setVisible(true);
        }
    }
}