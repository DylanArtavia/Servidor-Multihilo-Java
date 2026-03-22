/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidorvotacion;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una cadena sencilla de bloques utilizada para almacenar información
 * relacionada con el sistema de votación. Funciona como una estructura tipo
 * blockchain básica donde los bloques se agregan de forma secuencial.
 * 
 * La clase incluye sincronización interna para que las operaciones sean seguras
 * cuando varios hilos intenten acceder o modificar la cadena al mismo tiempo.
 * 
 * Funcionalidades principales:
 * - Permite agregar nuevos bloques.
 * - Permite obtener el tamaño total de la cadena.
 * - Permite acceder al último bloque agregado.
 * - Permite obtener una copia completa de todos los bloques.
 * 
 * Esta implementación es simple y no incorpora verificación de integridad,
 * algoritmos de consenso o validación de hashes. Está pensada para proyectos
 * educativos o sistemas controlados.
 * 
 * Autor: Dylan
 */
public class Blockchain implements Serializable {

    private static final long serialVersionUID = 1L;

    /** Lista que almacena todos los bloques en el orden en que fueron añadidos. */
    private final List<Bloque> cadena = new ArrayList<>();

    /**
     * Agrega un bloque al final de la cadena.
     * La operación es sincronizada para evitar conflictos entre hilos.
     *
     * @param b Bloque a agregar
     */
    public synchronized void agregarBloque(Bloque b) {
        cadena.add(b);
    }

    /**
     * Devuelve la cantidad total de bloques almacenados en la cadena.
     *
     * @return Número de bloques
     */
    public synchronized int size() {
        return cadena.size();
    }

    /**
     * Devuelve el último bloque añadido a la cadena.
     * Si la cadena está vacía, devuelve null.
     *
     * @return Último bloque o null si no hay bloques
     */
    public synchronized Bloque ultimo() {
        if (cadena.isEmpty()) return null;
        return cadena.get(cadena.size() - 1);
    }

    /**
     * Devuelve una copia de toda la cadena de bloques.
     * Se devuelve una lista nueva para evitar modificaciones externas
     * sobre la lista interna original.
     *
     * @return Una copia de la cadena de bloques
     */
    public synchronized List<Bloque> obtenerCadena() {
        return new ArrayList<>(cadena);
    }
}
