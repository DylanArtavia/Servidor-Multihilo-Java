/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package servidorvotacion;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.UIManager;

/**
 * Punto de entrada de la aplicación del servidor de votación.
 * Su única responsabilidad es preparar el entorno visual y
 * lanzar la ventana principal del sistema.
 *
 * Esta clase no maneja lógica de votación ni conexiones; simplemente
 * inicializa el look and feel y abre la interfaz gráfica.
 */
public class ServidorVotacion {

    public static void main(String[] args) {

        // Se crea la ventana del servidor (no visible aún)
        VentanaServidor vs = new VentanaServidor();

        // Configuración del tema visual FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Error cargando FlatLaf: " + ex.getMessage());
        }

        // Mostrar la interfaz gráfica del servidor
        java.awt.EventQueue.invokeLater(() -> {
            new VentanaServidor().setVisible(true);
        });
    }
}
