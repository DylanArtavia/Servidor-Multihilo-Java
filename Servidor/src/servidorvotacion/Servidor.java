/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servidorvotacion;

import java.io.File;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import Votacion.Voto;

/**
 * Clase principal del servidor de votación.
 * Su función es administrar las conexiones entrantes, recibir votos,
 * mantener la blockchain, el mempool y los registros de votantes.
 *
 * Además se encarga de guardar y cargar el estado desde disco para
 * permitir que el servidor pueda apagarse y volver a levantarse sin
 * perder información.
 */
public class Servidor {

    // Tokens válidos por ID de votante
    public ConcurrentHashMap<String, String> tokens = new ConcurrentHashMap<>();

    // Conjunto de IDs que ya tuvieron derecho a emitir su voto
    public Set<String> votedIds = ConcurrentHashMap.newKeySet();

    // Comunicación de red
    public java.net.ServerSocket servidor;
    public boolean activo = false;
    public ExecutorService pool = Executors.newCachedThreadPool();

    // Estructuras principales del sistema
    public MemPool mempool = new MemPool();
    public Blockchain blockchain = new Blockchain();

    // Interfaz gráfica del servidor
    public VentanaServidor gui;

    // Archivos donde se guarda el estado
    private final File FILE_VOTERS = new File("voters.dat");
    private final File FILE_VOTED = new File("votedIds.dat");
    private final File FILE_BLOCKCHAIN = new File("blockchain.bin");
    private final File FILE_MEMPOOL = new File("mempool.bin");

    /**
     * Constructor principal. Recibe la ventana GUI y de inmediato
     * intenta restaurar el estado previo desde disco.
     */
    public Servidor(VentanaServidor gui) {
        this.gui = gui;
        loadState();
    }

    /**
     * Inicia el servidor en el puerto 5000 y queda escuchando
     * conexiones de manera continua. Cada cliente conectado es
     * manejado en un hilo separado.
     */
    public void iniciar() {
        try {
            servidor = new java.net.ServerSocket(5000);
            activo = true;
            gui.appendLog("Servidor iniciado en puerto 5000.");

            Thread t = new Thread(() -> {
                while (activo) {
                    try {
                        java.net.Socket cliente = servidor.accept();
                        gui.appendLog("Cliente conectado: " + cliente.getRemoteSocketAddress());
                        pool.execute(new HiloCliente(cliente, this));
                    } catch (Exception e) {
                        if (activo) gui.appendLog("Error al aceptar cliente: " + e.getMessage());
                    }
                }
            });

            t.start();

        } catch (Exception e) {
            gui.appendLog("No se pudo iniciar servidor: " + e.getMessage());
        }
    }

    /**
     * Detiene el servidor, guarda el estado actual y cierra
     * todos los hilos y conexiones.
     */
    public void detener() {
        try {
            activo = false;
            if (servidor != null) servidor.close();
            gui.appendLog("Servidor detenido.");
            saveState();
            pool.shutdownNow();
        } catch (Exception e) {
            gui.appendLog("Error al detener servidor: " + e.getMessage());
        }
    }

    /**
     * Carga la blockchain, el mempool, los tokens y los IDs de votantes
     * desde los archivos del sistema. También reconstruye información
     * a partir de los bloques si es necesario.
     */
    private void loadState() {

        // Blockchain
        try {
            if (FILE_BLOCKCHAIN.exists()) {
                Blockchain bc = PersistenceUtil.loadObject(FILE_BLOCKCHAIN, Blockchain.class);
                if (bc != null) {
                    this.blockchain = bc;
                    gui.appendLog("Blockchain cargada: bloques = " + blockchain.size());
                }
            }
        } catch (Exception e) {
            gui.appendLog("No se pudo cargar blockchain: " + e.getMessage());
        }

        // MemPool
        try {
            if (FILE_MEMPOOL.exists()) {
                MemPool mp = PersistenceUtil.loadObject(FILE_MEMPOOL, MemPool.class);
                if (mp != null) {
                    this.mempool = mp;
                    gui.appendLog("MemPool restaurado: tamaño = " + mp.tamaño());
                }
            }
        } catch (Exception e) {
            gui.appendLog("No se pudo cargar mempool: " + e.getMessage());
        }

        // Tokens de votante
        try {
            if (FILE_VOTERS.exists()) {
                HashMap<String,String> loaded = PersistenceUtil.loadObject(FILE_VOTERS, HashMap.class);
                if (loaded != null) {
                    this.tokens = new ConcurrentHashMap<>(loaded);
                    gui.appendLog("Voters (tokens) cargados: " + tokens.size());
                }
            }
        } catch (Exception e) {
            gui.appendLog("No se pudo cargar voters: " + e.getMessage());
        }

        // IDs que ya votaron
        try {
            if (FILE_VOTED.exists()) {
                Set<String> loaded = PersistenceUtil.loadObject(FILE_VOTED, Set.class);
                if (loaded != null) {
                    this.votedIds = ConcurrentHashMap.newKeySet();
                    this.votedIds.addAll(loaded);
                    gui.appendLog("VotedIds cargados: " + votedIds.size());
                }
            }
        } catch (Exception e) {
            gui.appendLog("No se pudo cargar votedIds: " + e.getMessage());
        }

        // Reconstrucción desde blockchain
        try {
            for (Bloque b : blockchain.obtenerCadena()) {
                if (b.getVotes() == null) continue;

                for (Voto v : b.getVotes()) {
                    votedIds.add(v.getIdVotante());
                    tokens.putIfAbsent(v.getIdVotante(), v.getToken());
                }
            }
        } catch (Exception e) {
            gui.appendLog("Error reconstruyendo datos: " + e.getMessage());
        }
    }

    /**
     * Guarda a disco la blockchain, el mempool, los tokens y
     * los IDs de votantes. Todo usando guardado atómico.
     */
    public void saveState() {
        try {
            PersistenceUtil.saveObjectAtomic(blockchain, FILE_BLOCKCHAIN);
            gui.appendLog("Blockchain guardada.");
        } catch (Exception e) {
            gui.appendLog("Error guardando blockchain: " + e.getMessage());
        }

        try {
            PersistenceUtil.saveObjectAtomic(mempool, FILE_MEMPOOL);
            gui.appendLog("MemPool guardado.");
        } catch (Exception e) {
            gui.appendLog("Error guardando mempool: " + e.getMessage());
        }

        try {
            PersistenceUtil.saveObjectAtomic(new HashMap<>(tokens), FILE_VOTERS);
            gui.appendLog("Voters (tokens) guardados.");
        } catch (Exception e) {
            gui.appendLog("Error guardando voters: " + e.getMessage());
        }

        try {
            PersistenceUtil.saveObjectAtomic(new java.util.HashSet<>(votedIds), FILE_VOTED);
            gui.appendLog("VotedIds guardados.");
        } catch (Exception e) {
            gui.appendLog("Error guardando votedIds: " + e.getMessage());
        }
    }

    /**
     * Verifica que un voto sea válido y, si todo está en orden,
     * lo agrega al mempool. Se revisa el token, el ID y si el votante
     * ya ha participado anteriormente. Responde un mensaje para el cliente.
     */
    public synchronized String validarVotoYAgregar(Voto voto) {

        if (voto == null) return "Error: voto vacío";

        String id = voto.getIdVotante();
        if (id == null || id.isBlank()) return "Error: ID inválido";

        // Rechazar si ya votó
        if (votedIds.contains(id)) {
            gui.appendLog("Rechazado - votante ya registrado: " + id);
            return "Error: Este votante ya emitió un voto.";
        }

        // Registrar token del votante si es nuevo
        if (!tokens.containsKey(id)) {
            tokens.put(id, voto.getToken());
            gui.appendLog("Registrado token para votante: " + id);
        } else {
            // Si ya existe un token, debe coincidir
            String known = tokens.get(id);
            if (voto.getToken() == null || !known.equals(voto.getToken())) {
                gui.appendLog("Rechazado - token inválido para: " + id);
                return "Error: Token inválido.";
            }
        }

        // Marcar a este ID como que ya votó
        votedIds.add(id);

        // Enviar voto al mempool
        mempool.agregar(new Voto(
                voto.getIdVotante(),
                voto.getToken(),
                voto.getCandidato(),
                voto.getFechaHora()
        ));

        gui.appendLog("Voto agregado a mempool: " + id + " -> " + voto.getCandidato());

        return "Voto aceptado y agregado al mempool.";
    }
}
