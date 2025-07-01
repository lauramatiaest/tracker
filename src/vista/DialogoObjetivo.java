package vista;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import controlador.ControladorObjetivos;
import modelo.Objetivo;
import modelo.Categoria;

public class DialogoObjetivo extends JDialog {
    
    private ControladorObjetivos controlador;
    private Objetivo objetivoEditar;
    private boolean esEdicion;
    
    private JTextField txtNombre;
    private JTextArea txtDescripcion;
    private JSpinner spnMetaFinal;
    private JTextField txtFechaLimite;
    private JComboBox<Categoria> cmbCategoria;
    private JSpinner spnPrioridad;
    private JButton btnGuardar;
    private JButton btnCancelar;
    
    private boolean resultado;
    
    public DialogoObjetivo(JFrame parent, ControladorObjetivos controlador) {
        super(parent, "Nuevo Objetivo", true);
        this.controlador = controlador;
        this.esEdicion = false;
        this.resultado = false;
        inicializarComponentes();
        configurarVentana();
    }
    
    public DialogoObjetivo(JFrame parent, ControladorObjetivos controlador, Objetivo objetivo) {
        super(parent, "Editar Objetivo", true);
        this.controlador = controlador;
        this.objetivoEditar = objetivo;
        this.esEdicion = true;
        this.resultado = false;
        inicializarComponentes();
        cargarDatosObjetivo();
        configurarVentana();
    }
    
    private void inicializarComponentes() {
        setLayout(new BorderLayout());
        
        JPanel panelPrincipal = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Nombre
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panelPrincipal.add(new JLabel("Nombre:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtNombre = new JTextField(20);
        panelPrincipal.add(txtNombre, gbc);
        
        // Descripción
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panelPrincipal.add(new JLabel("Descripción:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0; gbc.weighty = 0.3;
        txtDescripcion = new JTextArea(3, 20);
        txtDescripcion.setLineWrap(true);
        txtDescripcion.setWrapStyleWord(true);
        JScrollPane scrollDesc = new JScrollPane(txtDescripcion);
        panelPrincipal.add(scrollDesc, gbc);
        
        // Meta Final
        gbc.gridx = 0; gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0; gbc.weighty = 0.0;
        panelPrincipal.add(new JLabel("Meta Final:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        spnMetaFinal = new JSpinner(new SpinnerNumberModel(100, 1, 10000, 1));
        panelPrincipal.add(spnMetaFinal, gbc);
        
        // Fecha Límite
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panelPrincipal.add(new JLabel("Fecha Límite:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        txtFechaLimite = new JTextField();
        txtFechaLimite.setToolTipText("Formato: YYYY-MM-DD (Opcional)");
        panelPrincipal.add(txtFechaLimite, gbc);
        
        // Categoría
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panelPrincipal.add(new JLabel("Categoría:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        cmbCategoria = new JComboBox<>();
        cargarCategorias();
        panelPrincipal.add(cmbCategoria, gbc);
        
        // Prioridad
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        panelPrincipal.add(new JLabel("Prioridad:"), gbc);
        
        gbc.gridx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        spnPrioridad = new JSpinner(new SpinnerNumberModel(1, 1, 5, 1));
        panelPrincipal.add(spnPrioridad, gbc);
        
        add(panelPrincipal, BorderLayout.CENTER);
        
        // Panel de botones
        JPanel panelBotones = new JPanel(new FlowLayout());
        btnGuardar = new JButton(esEdicion ? "Actualizar" : "Crear");
        btnCancelar = new JButton("Cancelar");
        
        btnGuardar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarObjetivo();
            }
        });
        
        btnCancelar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        panelBotones.add(btnGuardar);
        panelBotones.add(btnCancelar);
        add(panelBotones, BorderLayout.SOUTH);
    }
    
    private void cargarCategorias() {
        cmbCategoria.removeAllItems();
        for (Categoria categoria : controlador.getControladorCategorias().obtenerTodasLasCategorias()) {
            cmbCategoria.addItem(categoria);
        }
    }
    
    private void cargarDatosObjetivo() {
        if (objetivoEditar != null) {
            txtNombre.setText(objetivoEditar.getNombre());
            txtDescripcion.setText(objetivoEditar.getDescripcion());
            spnMetaFinal.setValue(objetivoEditar.getMetaFinal());
            
            if (objetivoEditar.getFechaLimite() != null) {
                txtFechaLimite.setText(objetivoEditar.getFechaLimite().toString());
            }
            
            // Seleccionar categoría
            Categoria categoria = controlador.getControladorCategorias()
                .buscarCategoriaPorId(objetivoEditar.getCategoriaId());
            if (categoria != null) {
                cmbCategoria.setSelectedItem(categoria);
            }
            
            spnPrioridad.setValue(objetivoEditar.getPrioridad());
        }
    }
    
    private void guardarObjetivo() {
        if (!validarCampos()) {
            return;
        }
        
        String nombre = txtNombre.getText().trim();
        String descripcion = txtDescripcion.getText().trim();
        int metaFinal = (Integer) spnMetaFinal.getValue();
        String fechaLimite = txtFechaLimite.getText().trim();
        Categoria categoriaSeleccionada = (Categoria) cmbCategoria.getSelectedItem();
        int prioridad = (Integer) spnPrioridad.getValue();
        
        if (categoriaSeleccionada == null) {
            JOptionPane.showMessageDialog(this, 
                "Debe seleccionar una categoría", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean exito;
        if (esEdicion) {
            exito = controlador.actualizarObjetivo(
                objetivoEditar.getId(),
                nombre,
                descripcion,
                metaFinal,
                fechaLimite.isEmpty() ? null : fechaLimite,
                categoriaSeleccionada.getId(),
                prioridad
            );
        } else {
            exito = controlador.crearObjetivo(
                nombre,
                descripcion,
                metaFinal,
                fechaLimite.isEmpty() ? null : fechaLimite,
                categoriaSeleccionada.getId(),
                prioridad
            );
        }
        
        if (exito) {
            resultado = true;
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error al " + (esEdicion ? "actualizar" : "crear") + " el objetivo", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validarCampos() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "El nombre es obligatorio", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            txtNombre.requestFocus();
            return false;
        }
        
        if ((Integer) spnMetaFinal.getValue() <= 0) {
            JOptionPane.showMessageDialog(this, 
                "La meta final debe ser mayor a 0", 
                "Error de Validación", 
                JOptionPane.ERROR_MESSAGE);
            spnMetaFinal.requestFocus();
            return false;
        }
        
        String fechaTexto = txtFechaLimite.getText().trim();
        if (!fechaTexto.isEmpty()) {
            try {
                LocalDate fecha = LocalDate.parse(fechaTexto);
                if (fecha.isBefore(LocalDate.now())) {
                    JOptionPane.showMessageDialog(this, 
                        "La fecha límite no puede ser anterior a hoy", 
                        "Error de Validación", 
                        JOptionPane.ERROR_MESSAGE);
                    txtFechaLimite.requestFocus();
                    return false;
                }
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(this, 
                    "Formato de fecha inválido. Use YYYY-MM-DD", 
                    "Error de Validación", 
                    JOptionPane.ERROR_MESSAGE);
                txtFechaLimite.requestFocus();
                return false;
            }
        }
        
        return true;
    }
    
    private void configurarVentana() {
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(450, 400);
        setLocationRelativeTo(getParent());
        setResizable(false);
        
        // Focus inicial
        txtNombre.requestFocus();
    }
    
    public boolean getResultado() {
        return resultado;
    }
}