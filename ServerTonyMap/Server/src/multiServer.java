package src;

import java.io.*;
import java.net.*;

/**
 * La classe {@code multiServer} rappresenta un server multi-threaded che accetta connessioni da parte dei client.
 * <p>
 * Il server utilizza una porta specifica (8080 di default) e crea un thread dedicato per ogni client connesso
 * utilizzando la classe {@link serverOneClient}.
 * </p>
 */
public class multiServer {

    /**
     * Porta su cui il server è in ascolto per le connessioni dei client.
     */
    static final int PORT = 8080;

    /**
     * Metodo principale che avvia il server.
     *
     * @param args Argomenti della riga di comando (non utilizzati in questa implementazione).
     * @throws IOException Se si verifica un errore durante l'avvio del server o la gestione delle connessioni.
     */
    public static void main(String[] args) throws IOException {
            ServerSocket s = new ServerSocket(PORT);
            System.out.println("Server Avviato");
            try {
                while (true) {
                    // Si blocca finché non si verifica una connessione:
                    Socket socket = s.accept();
                    try {
                        new serverOneClient(socket); // Gestisce il client connesso in un thread separato
                    } catch (IOException e) {
                        // Se fallisce, chiude il socket,
                        // altrimenti il thread gestirà la chiusura:
                        socket.close();
                    }
                }
            } finally {
                s.close(); // Chiude il ServerSocket
            }
    }
}
