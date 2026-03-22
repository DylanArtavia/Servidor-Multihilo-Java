/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidorvotacion;

/**
 * Atiende a un cliente conectado al servidor.  
 * Esta clase se ejecuta en un hilo independiente y se encarga de recibir un
 * voto enviado por el cliente, delegar su validación al servidor principal
 * y registrar en la interfaz si el voto fue aceptado o rechazado.
 *
 * Funciona así:
 * 1. Lee desde el socket un objeto recibido por el cliente.
 * 2. Interpreta ese objeto como un voto.
 * 3. Envía el voto al servidor para su validación y registro.
 * 4. Muestra en el log del servidor el resultado del procesamiento.
 *
 * Autor: Dylan
 */

import Votacion.Voto;
import java.io.ObjectInputStream;
import java.net.Socket;

public class ManejadorCliente extends Thread {

    /* Conexión con el cliente que envía el voto. */
    private Socket socket;

    /* Referencia al servidor principal para validar y registrar votos. */
    private Servidor servidor;

    /**
     * Crea un manejador para un cliente específico.
     *
     * @param socket Socket asociado al cliente.
     * @param servidor Referencia al servidor que controla la lógica del sistema.
     */
    public ManejadorCliente(Socket socket, Servidor servidor) {
        this.socket = socket;
        this.servidor = servidor;
    }

    /**
     * Proceso principal que ejecuta el hilo.  
     * Lee el voto enviado, solicita al servidor su validación y registra el
     * resultado en la interfaz gráfica.
     */
    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // Recibir el voto enviado por el cliente
            Voto voto = (Voto) in.readObject();

            // Validar el voto mediante el servidor
            String respuesta = servidor.validarVotoYAgregar(voto);

            // Mostrar resultado en el log
            if (respuesta.startsWith("Voto aceptado")) {
                servidor.gui.appendLog("Voto registrado: " + voto);
            } else {
                servidor.gui.appendLog("Voto rechazado: " + voto + " Motivo: " + respuesta);
            }

        } catch (Exception e) {
            System.err.println("Error recibiendo voto: " + e.getMessage());
        } finally {
            // Cerrar conexión con el cliente
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (Exception ignored) {}
        }
    }
}
