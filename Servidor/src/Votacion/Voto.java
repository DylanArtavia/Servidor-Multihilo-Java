/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Votacion;

import java.io.Serializable;

/**
 * Representa un voto emitido dentro del sistema de votación.
 * Cada voto almacena la información esencial del proceso:
 * el votante que participa, el token que valida su autenticidad,
 * el candidato seleccionado y la fecha/hora exacta en la que se registró.
 *
 * Implementa {@code Serializable} para permitir guardar y recuperar
 * los votos desde archivos o flujos de datos sin perder su estructura.
 */
/**
 *
 * @author Dylan
 */

public class Voto implements Serializable {

    private String idVotante;
    private String token;
    private String candidato;
    private String fechaHora;

    /**
     * Construye un nuevo voto con todos los datos requeridos.
     *
     * @param idVotante  identificador único del votante que emite el voto
     * @param token      código o token que valida que el voto es legítimo
     * @param candidato  nombre del candidato elegido por el votante
     * @param fechaHora  momento en el que se registró el voto, usualmente
     *                   expresado como fecha y hora en formato legible
     */
    public Voto(String idVotante, String token, String candidato, String fechaHora) {
        this.idVotante = idVotante;
        this.token = token;
        this.candidato = candidato;
        this.fechaHora = fechaHora;
    }

    /**
     * Obtiene el identificador del votante.
     *
     * @return el ID del votante que emitió el voto
     */
    public String getIdVotante() {
        return idVotante;
    }

    /**
     * Devuelve el token asociado al voto.
     * Este token sirve para validar la autenticidad del voto.
     *
     * @return el token del voto
     */
    public String getToken() {
        return token;
    }

    /**
     * Obtiene el nombre del candidato seleccionado.
     *
     * @return el candidato por el cual se votó
     */
    public String getCandidato() {
        return candidato;
    }

    /**
     * Recupera la fecha y hora en que se registró el voto.
     *
     * @return la fecha y hora del registro del voto
     */
    public String getFechaHora() {
        return fechaHora;
    }

    /**
     * Devuelve una representación textual del voto,
     * útil para depuración o visualización simple.
     *
     * @return cadena con el formato "idVotante votó por candidato (fechaHora)"
     */
    @Override
    public String toString() {
        return idVotante + " votó por " + candidato + " (" + fechaHora + ")";
    }
}
