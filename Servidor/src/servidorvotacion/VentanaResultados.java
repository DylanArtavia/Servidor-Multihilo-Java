/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidorvotacion;

import Votacion.Voto;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.formdev.flatlaf.FlatLightLaf;

/**
 * Ventana encargada de mostrar los resultados finales de la votación.
 * Esta interfaz lee la información almacenada en la blockchain del servidor
 * y genera un conteo total por candidato.
 *
 * Su función es únicamente visual: no modifica datos ni interactúa con
 * el proceso de votación. Se limita a consultar y presentar los resultados.
 * 
 * autor: Dylan
 */
public class VentanaResultados extends JFrame {

    private JTextArea txtResultados;

    /**
     * Construye la ventana encargada de mostrar los resultados.
     * Aplica el tema visual, configura los componentes y presenta
     * un resumen de votos obtenidos desde la blockchain del servidor.
     */
    public VentanaResultados(Servidor servidor) {

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("No se pudo aplicar FlatLightLaf: " + ex.getMessage());
        }

        setTitle("Resultados de Votación");
        setSize(500, 400);setTitle("Resultados de Votación");
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        Font fuente = new Font("Arial", Font.BOLD, 14);

        txtResultados = new JTextArea();
        txtResultados.setEditable(false);
        txtResultados.setFont(fuente);

        JScrollPane scroll = new JScrollPane(txtResultados);
        scroll.setFont(fuente);

        add(scroll);

        mostrarResultados(servidor);
    }

    /**
     * Genera el análisis de los votos y los muestra en pantalla.
     * Si no existen bloques o no hay votos registrados, se indica.
     *
     * Este método recorre toda la blockchain, acumula votos según
     * el candidato y finalmente escribe el resumen en el área de texto.
     */
    private void mostrarResultados(Servidor servidor) {

        txtResultados.setText("");
        txtResultados.append("=== RESULTADOS ===\n\n");

        if (servidor == null || servidor.blockchain.size() == 0) {
            txtResultados.append("No hay bloques minados.\n");
            return;
        }

        Map<String, Integer> conteo = new HashMap<>();

        List<Bloque> bloques = servidor.blockchain.obtenerCadena();
        for (Bloque bloque : bloques) {
            List<Voto> votos = bloque.getVotes();
            if (votos != null) {
                for (Voto v : votos) {
                    String candidato = v.getCandidato();
                    conteo.put(candidato, conteo.getOrDefault(candidato, 0) + 1);
                }
            }
        }

        for (Map.Entry<String, Integer> entry : conteo.entrySet()) {
            txtResultados.append(entry.getKey() + " : " + entry.getValue() + " votos.\n");
        }

        if (conteo.isEmpty()) {
            txtResultados.append("No se han registrado votos aún.\n");
        }
    }
}
