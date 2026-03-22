/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidorvotacion;

import java.io.Serializable;
import java.util.List;
import Votacion.Voto;

/**
 * Representa un bloque dentro de la cadena de bloques utilizada por el sistema
 * de votación. Cada bloque almacena un conjunto de votos y los datos necesarios
 * para mantener la integridad entre bloques consecutivos.
 * 
 * Un bloque contiene:
 * - Un identificador único
 * - La fecha en que fue creado
 * - El nonce utilizado durante el proceso de minado
 * - El tiempo que tardó en generarse el bloque
 * - Una lista de votos incluidos dentro del bloque
 * - El hash del bloque anterior
 * - El hash propio del bloque
 * 
 * Esta clase es completamente serializable para permitir su almacenamiento
 * y transmisión por red entre cliente y servidor.
 * 
 * Autor: Dylan
 */
public class Bloque implements Serializable {

    private static final long serialVersionUID = 1L;

    /* Identificador único del bloque dentro de la cadena. */
    private int id;

    /* Fecha de creación del bloque en formato de texto. */
    private String fecha;

    /* Valor utilizado en el proceso de minado para validar el hash. */
    private int nonce;

    /* Tiempo que tomó generar el bloque, medido en milisegundos. */
    private long duracion;

    /* Conjunto de votos incluidos dentro de este bloque. */
    private List<Voto> votos;

    /* Hash correspondiente al bloque inmediatamente anterior. */
    private String hashPrevio;

    /* Hash propio del bloque, calculado después del minado. */
    private String hash;

    /**
     * Crea un nuevo bloque con los datos indicados.
     *
     * @param id Identificador del bloque
     * @param fecha Fecha en que se generó el bloque
     * @param nonce Nonce encontrado durante el minado
     * @param duracion Duración total del proceso de minado
     * @param votos Lista de votos incluidos en el bloque
     * @param hashPrevio Hash del bloque anterior
     * @param hash Hash final del bloque actual
     */
    public Bloque(int id, String fecha, int nonce, long duracion, List<Voto> votos,
                  String hashPrevio, String hash) {
        this.id = id;
        this.fecha = fecha;
        this.nonce = nonce;
        this.duracion = duracion;
        this.votos = votos;
        this.hashPrevio = hashPrevio;
        this.hash = hash;
    }

    /** Devuelve el identificador del bloque. */
    public int getId() { return id; }

    /** Devuelve la fecha de creación del bloque. */
    public String getFecha() { return fecha; }

    /** Devuelve el nonce usado durante el minado. */
    public int getNonce() { return nonce; }

    /** Devuelve el tiempo total que tardó en generarse el bloque. */
    public long getDuracion() { return duracion; }

    /** Devuelve la lista de votos almacenados en el bloque. */
    public List<Voto> getVotes() { return votos; }

    /** Devuelve el hash del bloque anterior. */
    public String getHashPrevio() { return hashPrevio; }

    /** Devuelve el hash final del bloque actual. */
    public String getHash() { return hash; }

    @Override
    public String toString() {
        return "Bloque{id=" + id + ", votos=" +
               (votos == null ? 0 : votos.size()) +
               ", hash=" + hash + "}";
    }
}
