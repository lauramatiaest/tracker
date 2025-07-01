import javax.swing.SwingUtilities;
import controlador.ControladorObjetivos;
import vista.VentanaPrincipal;

public class Main {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                ControladorObjetivos controlador = new ControladorObjetivos();
                VentanaPrincipal ventanaPrincipal = new VentanaPrincipal(controlador);
                ventanaPrincipal.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }
}