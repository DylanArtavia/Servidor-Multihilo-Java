/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidorvotacion;

/**
 * Clase responsable del proceso de minería en la cadena de bloques.
 * Se encarga de tomar votos pendientes del mempool, agruparlos en bloques
 * de un máximo definido y realizar el cálculo del hash cumpliendo la dificultad.
 *
 * El minador:
 * - Extrae hasta 5 votos del mempool.
 * - Genera el bloque con su información base (ID, fecha, hash previo).
 * - Incrementa el nonce hasta obtener un hash que cumpla con la cantidad
 *   de ceros iniciales establecidos.
 * - Registra el bloque minado en la cadena del servidor.
 *
 * Autor: Dylan
 */

import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import Votacion.Voto;

public class Minador {

    /* Número de ceros iniciales requeridos en el hash para validar la prueba de trabajo. */
    private static final int DIFICULTAD = 4;

    /* Máxima cantidad de votos permitidos dentro de un bloque. */
    private static final int MAX_VOTOS_POR_BLOQUE = 5;

    /**
     * Ejecuta el proceso de minería.  
     * Si hay votos en el mempool, agrupa hasta cinco, calcula el hash válido
     * mediante prueba de trabajo y registra el bloque en la cadena.
     *
     * @param servidor Instancia del servidor que contiene la blockchain y mempool.
     * @param gui Interfaz gráfica usada para mostrar mensajes del proceso.
     */
    public static void minar(Servidor servidor, VentanaServidor gui) {
        try {
            if (servidor.mempool.estaVacio()) {
                gui.appendLog("No hay votos para minar.");
                return;
            }

            List<Voto> votosBloque = new ArrayList<>();
            Voto v;

            // Extrae hasta 5 votos del mempool
            while ((v = servidor.mempool.tomar()) != null && votosBloque.size() < MAX_VOTOS_POR_BLOQUE) {
                votosBloque.add(v);
            }

            int idBloque = servidor.blockchain.size() + 1;
            String fecha = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String hashPrevio = servidor.blockchain.ultimo() == null
                    ? "0".repeat(64)
                    : servidor.blockchain.ultimo().getHash();

            long inicio = System.currentTimeMillis();
            int nonce = 0;
            String hash;
            String prefijo = "0".repeat(DIFICULTAD);

            // Prueba de trabajo: incrementar nonce hasta obtener hash válido
            do {
                nonce++;
                hash = calcularHash(idBloque, fecha, hashPrevio, votosBloque, nonce);
            } while (!hash.startsWith(prefijo));

            long duracion = System.currentTimeMillis() - inicio;

            Bloque bloque = new Bloque(idBloque, fecha, nonce, duracion, votosBloque, hashPrevio, hash);
            servidor.blockchain.agregarBloque(bloque);

            gui.appendLog(
                "Bloque " + idBloque +
                " minado. votos=" + votosBloque.size() +
                " hash=" + hash +
                " duracion=" + duracion + "ms"
            );

        } catch (Exception e) {
            gui.appendLog("Error al minar: " + e.getMessage());
        }
    }

    /**
     * Calcula el hash SHA-256 para la información del bloque.
     * Incluye ID, fecha, hash previo, lista de votos y el nonce.
     *
     * @param id Identificador del bloque.
     * @param fecha Fecha de creación del bloque.
     * @param hashPrevio Hash del bloque anterior en la cadena.
     * @param votos Lista de votos incluidos en el bloque.
     * @param nonce Número que se incrementa para cumplir con la dificultad.
     * @return Hash SHA-256 en formato hexadecimal.
     * @throws Exception Si ocurre un error durante el proceso de digestión.
     */
    private static String calcularHash(int id, String fecha, String hashPrevio, List<Voto> votos, int nonce) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        String datos = id + fecha + hashPrevio + votos.toString() + nonce;
        byte[] bytes = digest.digest(datos.getBytes("UTF-8"));
        StringBuilder sb = new StringBuilder();

        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }

        return sb.toString();
    }
}
