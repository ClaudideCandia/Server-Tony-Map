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
 * Classe che gestisce la comunicazione tra il server e un singolo client.
 * Estende la classe Thread per gestire le operazioni in modo asincrono.
 */
public class serverOneClient extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    /**
     * Costruttore che inizializza il socket e gli stream di input/output.
     *
     * @param s il socket connesso al client
     * @throws IOException in caso di errori durante l'inizializzazione degli stream.
     */
    public serverOneClient(Socket s) throws IOException {
        socket = s;
        in = new ObjectInputStream(socket.getInputStream());
        out = new ObjectOutputStream(socket.getOutputStream());

        // Se una qualsiasi delle chiamate precedenti solleva una
        // eccezione, il processo chiamante è responsabile della
        // chiusura del socket. Altrimenti lo chiuderà il thread.
        start(); // Chiama run()
    }

    /**
     * Riceve il nome della tabella del database selezionata dal client,
     * crea un oggetto di tipo Data e lo restituisce.
     *
     * @param tableselected il nome della tabella selezionata
     * @return l'oggetto Data contenente i dati della tabella specificata
     * @throws IOException se si verificano errori di I/O
     * @throws ClassNotFoundException se non viene trovata la classe richiesta
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
     * Carica un dendrogramma da un file presente sul server.
     *
     * @param filename il nome del file contenente il dendrogramma
     * @throws IOException se si verificano errori di I/O
     * @throws ClassNotFoundException se non viene trovata la classe richiesta
     */
    private void loadDedrogramFromFileOnServer(String filename) throws IOException, ClassNotFoundException {
        System.out.println("Ricevuto: " + filename);
        HierachicalClusterMiner hcm = HierachicalClusterMiner.loaHierachicalClusterMiner(filename);
        out.writeObject(hcm.toString());
    }

    /**
     * Esegue il clustering gerarchico utilizzando i dati forniti.
     *
     * @param data       i dati da clusterizzare
     * @param depth      la profondità del clustering
     * @param modDistance il tipo di calcolo della distanza (1 per SingleLink, 2 per AverageLink)
     * @return il risultato del clustering come HierachicalClusterMiner
     * @throws IOException se si verificano errori di I/O
     * @throws ClassNotFoundException se non viene trovata la classe richiesta
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
     * Mostra al client l'elenco delle tabelle disponibili nel database.
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
     * Metodo principale eseguito dal thread per gestire la comunicazione con il client.
     */
    public void run() {
        String mode;
        do {
            try {
                System.out.println("Aspetto la modalità d'uso del server dal client...");
                mode = (String) in.readObject();
                System.out.println("Ricevuto: " + mode);
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
        } while (!(mode.equals("close")));
    }

    /**
     * Gestisce la modalità di interazione con il database.
     *
     * @throws IOException se si verificano errori di I/O
     * @throws ClassNotFoundException se non viene trovata la classe richiesta
     * @throws NoDataException se non sono presenti dati nella tabella specificata
     */
    public void dbMode() throws IOException, ClassNotFoundException, NoDataException {
        displayTables(); // Invio la lista delle tabelle nel database al client
        System.out.println("Aspetto il nome della tabella da caricare...");
        String tableName = (String) in.readObject();
        System.out.println("Ricevuto: " + tableName);
        if (HomeCheck(tableName)) {
            return;
        }
        System.out.println("Aspetto profondità...");
        int profondita = (int) in.readObject();
        System.out.println("Ricevuto: " + profondita);
        System.out.println("Aspetto modalità di calcolo...");
        int linkMode = (int) in.readObject();
        System.out.println("Ricevuto: " + linkMode);

        Data data = new Data(tableName);
        HierachicalClusterMiner temporaneo = mineDendrogram(data, profondita, linkMode);

        out.writeObject(temporaneo.toString());
        System.out.println("Aspetto messaggio salvataggio...");
        String save = (String) in.readObject();
        System.out.println("Ricevuto: " + save);
        if (save.equals("salva")) {
            System.out.println("Aspetto nome file da memorizzare...");
            String filename = (String) in.readObject();
            System.out.println("Ricevuto: " + filename);
            temporaneo.salva(filename);
        } else if (HomeCheck(save)) return;
    }

    /**
     * Gestisce la modalità di interazione tramite file.
     */
    public void fileMode() {
        try {
            System.out.println("Aspetto nome file da caricare");
            String nomeFile = (String) in.readObject();
            System.out.println("Ricevuto: " + nomeFile);
            loadDedrogramFromFileOnServer(nomeFile);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Controlla se il client desidera tornare alla home.
     *
     * @param str il messaggio ricevuto dal client
     * @return true se il client desidera tornare alla home, false altrimenti
     */
    public Boolean HomeCheck(String str) {
        Boolean check = false;
        if (str.equals("home")) {
            check = true;
        }
        System.out.println("Blocco esecuzione per tornare alla home");
        return check;
    }
}
