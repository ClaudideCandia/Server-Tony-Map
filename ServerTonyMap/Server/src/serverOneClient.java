package src;

import java.io.*;
import java.net.*;
import java.util.List;
import src.clustering.HierachicalClusterMiner;
import src.data.Data;
import src.database.DbAccess;
import src.database.TableData;
import src.distance.*;
import src.exceptions.*;

/**
 * La classe serverOneClient gestisce una connessione con un client e consente
 * l'esecuzione di operazioni di clustering gerarchico sui dati ricevuti o caricati
 * da un database o un file. La classe estende Thread per permettere l'esecuzione
 * asincrona della comunicazione con il client.
 *
 * Gestisce diverse modalità di interazione:
 * - "DataBase": interazione con un database per recuperare dati da una tabella,
 *   eseguire il clustering e inviare i risultati al client.
 * - "File": carica un dendrogramma da file e lo invia al client.
 *
 * La comunicazione avviene tramite flussi di input e output serializzati.
 */
public class serverOneClient extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    /**
     * Costruttore della classe serverOneClient che inizializza il socket,
     * l'ObjectInputStream e l'ObjectOutputStream.
     * Avvia il thread per la gestione della connessione con il client.
     *
     * @param s Il socket per la connessione con il client
     * @throws IOException Se si verifica un errore di I/O durante l'inizializzazione
     */
    public serverOneClient(Socket s) throws IOException {
        socket = s;
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());

        // se una qualsiasi delle chiamate precedenti solleva una
        // eccezione, il processo chiamante è responsabile della
        // chiusura del socket. Altrimenti lo chiuderà il thread
        start(); // Chiama run()
    }

    /**
     * Riceve il nome della tabella del DB dal client e crea un oggetto Data.
     * Ripete il tentativo finché non carica correttamente i dati.
     *
     * @param tableselected Il nome della tabella selezionata dal client
     * @return Un oggetto Data contenente i dati della tabella
     * @throws IOException Se si verifica un errore di I/O
     * @throws ClassNotFoundException Se non viene trovata la classe Data
     */
    private Data receiveDataClient(String tableselected) throws IOException, ClassNotFoundException {
        Data datas = null;
        boolean loadedData = false;

        do {
            try {
                datas = new Data(tableselected);
                loadedData = true;
            } catch (NoDataException e) {
                System.out.println(e.getMessage());
            }
        } while (!loadedData);
        return datas;
    }

    /**
     * Carica il dendrogramma da un file sul server e lo invia al client.
     *
     * @param filename Il nome del file contenente il dendrogramma
     * @throws IOException Se si verifica un errore di I/O
     * @throws ClassNotFoundException Se non viene trovata la classe HierachicalClusterMiner
     */
    private void loadDedrogramFromFileOnServer(String filename) throws IOException, ClassNotFoundException {
        System.out.println("Ricevuto: " + filename);

        HierachicalClusterMiner hcm = HierachicalClusterMiner.loaHierachicalClusterMiner(filename);
        out.writeObject(hcm.toString());
    }

    /**
     * Esegue il clustering gerarchico sui dati ricevuti.
     *
     * @param data I dati da clusterizzare
     * @param depth La profondità del clustering
     * @param modDistance Il tipo di distanza da utilizzare per il clustering
     * @return Un oggetto HierachicalClusterMiner che rappresenta il dendrogramma risultante
     * @throws IOException Se si verifica un errore di I/O
     * @throws ClassNotFoundException Se non viene trovata la classe richiesta
     */
    private HierachicalClusterMiner mineDendrogram(Data data, int depth, int modDistance) throws IOException, ClassNotFoundException {
        HierachicalClusterMiner hcm = new HierachicalClusterMiner(depth);
        if (modDistance == 1) {
            ClusterDistance distance = new SingleLinkDistance();
            hcm.mine(data, distance);
        } else if (modDistance == 2) {
            ClusterDistance distance = new AverageLinkDistance();
            hcm.mine(data, distance);
        }
        return hcm;
    }

    /**
     * Mostra le tabelle disponibili nel database e le invia al client.
     */
    private void displayTables() {
        DbAccess db = new DbAccess();
        TableData tb = new TableData(db);
        List<String> tables = tb.getTablesName();
        try {
            out.writeObject(tables);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Eseguito dal thread, gestisce la comunicazione con il client per ricevere le modalità d'uso
     * e indirizzare l'utente alla modalità corretta (Database o File).
     */
    public void run() {
        String mode;
        try{
        do {
            try {
                System.out.println("aspetto la modalità d'uso del server dal client...");
                mode = (String) in.readObject();
                System.out.println("ricevuto : " + mode);
                if(mode.equals("Close")){
                    throw new ClientDisconnectedException("Il Client si è disconnesso");
                }
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            if (mode.equals("DataBase")) {
                try {
                    dbMode();
                } catch (IOException | NoDataException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else if (mode.equals("File")) {
                fileMode();
            }
        } while (true);
        }catch (ClientDisconnectedException e){
            System.out.println(e.getMessage());
        }finally{closeConnection();}
    }

    /**
     * Gestisce la modalità di interazione con il client per l'accesso e la gestione dei dati nel database.
     *
     * @throws IOException Se si verifica un errore di I/O
     * @throws ClassNotFoundException Se non viene trovata la classe richiesta
     * @throws NoDataException Se i dati non sono disponibili
     */
    public void dbMode() throws IOException, ClassNotFoundException, NoDataException, ClientDisconnectedException {
        // invio la lista delle tabelle nel db al client
        displayTables();
        // ricevo il nome della tabella selezionata dal client
        System.out.println("aspetto il nome della tabella da caricare...");
        String tableName = (String) in.readObject();
        System.out.println("ricevuto : " + tableName);
        if (HomeCheck(tableName)) {
            return;
        } else if (tableName.equals("Close")) {
            throw new ClientDisconnectedException("Client disconnesso in fase DB");
        }
        // ricevo la profondità
        System.out.println("aspetto profondità...");
        int profondita = (int) in.readObject();
        System.out.println("ricevuto : " + profondita);

        // ricevo la modalità di calcolo della distanza
        System.out.println("aspetto modalità di calcolo...");
        int linkMode = (int) in.readObject();
        System.out.println("ricevuto : " + linkMode);

        // creo l'oggetto data da clusterizzare
        Data data = new Data(tableName);
        // clusterizzo il data scelto da client
        HierachicalClusterMiner temporaneo = mineDendrogram(data, profondita, linkMode);

        // invio il risultato al client
        out.writeObject(temporaneo.toString());
        // ricevo messaggio di salvataggio dal client
        System.out.println("aspetto messaggio salvataggio...");
        String save = (String) in.readObject();
        System.out.println("ricevuto : " + save);
        if (save.equals("salva")) {
            // da client si vuole salvare il risultato della clusterizzazione
            // richiedo il nome del file
            System.out.println("aspetto nome file da memorizzare...");
            String filename = (String) in.readObject();
            System.out.println("ricevuto : " + filename);
            temporaneo.salva(filename);
        } else if (HomeCheck(save)){ return;
            } else if (save.equals("Close")) {
            throw new ClientDisconnectedException("Client disconnesso in fase DB");
        }
    }

    /**
     * Gestisce la modalità di interazione con il client per caricare un dendrogramma da file.
     */
    public void fileMode() throws ClientDisconnectedException {
        try {
            // ricevo nome file da caricare
            System.out.println("aspetto nome file da caricare");
            String nomeFile = (String) in.readObject();
            System.out.println("ricevuto : " + nomeFile);
            if(HomeCheck(nomeFile)){
                return;
            }else if(nomeFile.equals("Close")){
                throw new ClientDisconnectedException("Client disconnesso in modalità file");
            }
            loadDedrogramFromFileOnServer(nomeFile);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Controlla se la stringa passata corrisponde al valore "home".
     *
     * @param str La stringa da verificare
     * @return true se la stringa è "home", false altrimenti
     */
    public Boolean HomeCheck(String str) {
        Boolean check = false;
        if (str.equals("home")) {
            check = true;
        }
        System.out.println("blocco esecuzione per tornare alla home");
        return check;
    }

    private void closeConnection() {
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            System.out.println("Connessione con il client chiusa correttamente.");
        } catch (IOException e) {
            System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }

}