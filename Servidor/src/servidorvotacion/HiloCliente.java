/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidorvotacion;

/**
 * Maneja la comunicación entre un cliente y el servidor.  
 * Cada instancia de esta clase se ejecuta en un hilo independiente y se encarga
 * de recibir un voto enviado por el cliente, validarlo mediante el servidor
 * principal y devolver una respuesta.
 *
 * El flujo de trabajo básico es:
 * 1. Esperar un objeto enviado por el cliente.
 * 2. Verificar que el objeto recibido sea un voto válido.
 * 3. Registrar el voto en la interfaz del servidor.
 * 4. Enviar un mensaje de confirmación o error al cliente.
 *
 * Autor: Dylan
 */

import Votacion.Voto;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class HiloCliente implements Runnable {

    /* Conexión socket asociada a este cliente. */
    private final Socket socket;

    /* Referencia al servidor principal para validar votos y registrar acciones. */
    private final Servidor servidor;

    /**
     * Crea un nuevo manejador de cliente.
     *
     * @param socket Socket que representa la conexión con el cliente.
     * @param servidor Referencia al servidor principal para delegar la validación.
     */
    public HiloCliente(Socket socket, Servidor servidor) {
        this.socket = socket;
        this.servidor = servidor;
    }

    /**
     * Ciclo principal del hilo.  
     * Lee un objeto enviado por el cliente, lo valida y responde.
     */
    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            // Recibir un objeto desde el cliente
            Object obj = in.readObject();
            if (obj == null) {
                out.writeObject("Error: voto recibido es null");
                out.flush();
                return;
            }

            // Validar tipo del objeto
            if (!(obj instanceof Voto)) {
                out.writeObject("Error: objeto recibido no es un voto válido");
                out.flush();
                return;
            }

            Voto voto = (Voto) obj;

            // Registrar en la interfaz del servidor
            servidor.gui.appendLog("Voto recibido: " + voto);

            // Validar voto y obtener respuesta del servidor
            String respuesta = servidor.validarVotoYAgregar(voto);

            // Enviar respuesta al cliente
            out.writeObject(respuesta);
            out.flush();

        } catch (Exception e) {
            if (servidor != null && servidor.gui != null) {
                servidor.gui.appendLog("Error procesando cliente: " + e.getMessage());
            }
        } finally {
            try {
                if (socket != null) socket.close();
            } catch (Exception ignored) {}
        }
    }
}
