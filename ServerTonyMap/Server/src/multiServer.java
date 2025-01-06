package src;

import java.io.*;
import java.net.*;

public class multiServer {
    static final int PORT = 8080;

    public static void main(String[] args) throws IOException {
        ServerSocket s = new ServerSocket(PORT);
        System.out.println("Server Avviato");
        try {
            while(true) {
            // Si blocca finchè non si verifica una connessione:
            Socket socket = s.accept();
            try {
                new serverOneClient(socket);
                } catch(IOException e) {
            // Se fallisce chiude il socket,
            // altrimenti il thread la chiuderà:
            socket.close();
            }
        }
    } finally {
        s.close();
        }
    }
}
