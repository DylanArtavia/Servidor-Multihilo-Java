/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidorvotacion;

/**
 * Maneja una cola segura de votos pendientes.  
 * Esta estructura almacena temporalmente los votos recibidos por el servidor
 * antes de que sean procesados y agregados a un bloque.  
 * Usa una cola concurrente para permitir acceso desde múltiples hilos sin riesgo
 * de corrupción de datos.
 *
 * Funcionalidades principales:
 * - Agregar votos a la cola.
 * - Retirar votos para procesarlos.
 * - Consultar si hay votos pendientes.
 * - Obtener una copia de todos los votos actualmente en espera.
 *
 * Autor: Dylan
 */

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import Votacion.Voto;

public class MemPool implements Serializable {

    /* Cola concurrente donde se almacenan los votos pendientes. */
    private final ConcurrentLinkedQueue<Voto> cola = new ConcurrentLinkedQueue<>();

    /**
     * Agrega un voto a la memoria temporal si no es nulo.
     *
     * @param v Voto recibido por el servidor.
     */
    public void agregar(Voto v) {
        if (v != null) {
            cola.add(v);
        }
    }

    /**
     * Extrae y devuelve el próximo voto pendiente.
     * Si la cola está vacía, devuelve null.
     *
     * @return Voto listo para procesar o null.
     */
    public Voto tomar() {
        return cola.poll();
    }

    /**
     * Indica si no hay votos en espera.
     *
     * @return true si la cola está vacía.
     */
    public boolean estaVacio() {
        return cola.isEmpty();
    }

    /**
     * Devuelve la cantidad de votos pendientes.
     *
     * @return Número de elementos en la cola.
     */
    public int tamaño() {
        return cola.size();
    }

    /**
     * Devuelve una lista nueva con todos los votos almacenados.  
     * Esto evita exponer directamente la cola interna.
     *
     * @return Lista con los votos pendientes.
     */
    public List<Voto> obtenerTodos() {
        return new ArrayList<>(cola);
    }
}
