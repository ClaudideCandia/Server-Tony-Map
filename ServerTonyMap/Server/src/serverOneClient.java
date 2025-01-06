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
public class serverOneClient extends Thread {
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

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
     * Ricevuto il nome della taballa del DB da cui prendere gli esempi,
     * crea l'oggetto di tipo Data e lo restituisce
     * @return Data presi dal Database e dalla tabella specificata dal client
     * @throws IOException Ecccezione di I/O
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
     * Carica il dendrogramma da file sul server
     * @throws IOException Eccezione di I/O
     * @throws ClassNotFoundException Eccezione di classe non trovata
     */
    private void loadDedrogramFromFileOnServer(String filename) throws IOException, ClassNotFoundException {

        System.out.println("Ricevuto: " + filename);
        
        HierachicalClusterMiner hcm = HierachicalClusterMiner.loaHierachicalClusterMiner(filename);
        out.writeObject(hcm.toString());
    }

    /**
     * Esegue il clustering gerarchico
     * @param data Data da clusterizzare
     * @throws IOException Eccezione di I/O
     * @throws ClassNotFoundException Eccezione di classe non trovata
     */
    private HierachicalClusterMiner mineDendrogram(Data data,int depth, int modDistance) throws IOException, ClassNotFoundException {
        HierachicalClusterMiner hcm = new HierachicalClusterMiner(depth);
            if(modDistance == 1) {
                ClusterDistance distance = new SingleLinkDistance();
                hcm.mine(data, distance);
            } else if(modDistance == 2) {
                ClusterDistance distance = new AverageLinkDistance();
                hcm.mine(data, distance);
            }
    return hcm;
    }

    /**
     * Mostra le tabelle presenti nel database
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

//nuovo run
    public void run() {
        String mode;
        do {
        try {
            System.out.println("aspetto la modalità d'uso del server dal client...");
            mode = (String) in.readObject();
            System.out.println("ricevuto : " + mode);
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

            if (mode.equals("DataBase")) {
                try {
                    dbMode();
                } catch (IOException | NoDataException | ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }else if (mode.equals("File")){
                fileMode();
            }
        }while(!(mode.equals("close")));
    }
    
 public void dbMode() throws IOException, ClassNotFoundException, NoDataException {
     //invio la lista delle tabelle nel db al client
     displayTables();
     //ricevo il nome della tabella selezionata dal client
     System.out.println("aspetto il nome della tabella da caricare...");
     String tableName = (String) in.readObject();
     System.out.println("ricevuto : "+tableName);
     if(HomeCheck(tableName)){
         return;
     }
     //ricevo la profondità
     System.out.println("aspetto profondità...");
     int profondita = (int) in.readObject();
     System.out.println("ricevuto : "+profondita);

     //ricevo la modalità di calcolo della distanza
     System.out.println("aspetto modalità di calcolo...");
     int linkMode = (int) in.readObject();
     System.out.println("ricevuto : "+linkMode);


     //creo l'oggetto data da clusterizzare
     Data data = new Data(tableName);
     //clusterizzo il data scelto da client
     HierachicalClusterMiner temporaneo = mineDendrogram(data, profondita, linkMode);

     //invio il risultato al client
     out.writeObject(temporaneo.toString());
     //ricevo messaggio di salvataggio dal client
     System.out.println("aspetto messaggio salvataggio...");
     String save = (String) in.readObject();
     System.out.println("ricevuto : "+save);
     if (save.equals("salva")) {
         //da client si vuole salvare il risultato della clusterizzazione
         //richiedo il nome del file
         System.out.println("aspetto nome file da memorizzare...");
         String filename = (String) in.readObject();
         System.out.println("ricevuto : "+filename);
         temporaneo.salva(filename);
     }else if (HomeCheck(save)) return;
 }
 public void fileMode(){
     try {
         //ricevo nome file da caricare
         System.out.println("aspetto nome file da caricare");
         String nomeFile = (String) in.readObject();
         System.out.println("ricevuto : "+nomeFile);
         loadDedrogramFromFileOnServer(nomeFile);
     } catch (IOException | ClassNotFoundException e) {
         throw new RuntimeException(e);
     }
 }
public Boolean HomeCheck(String str){
        Boolean check = false;
        if(str.equals("home")){
            check=true;
        }
        System.out.println("blocco esecuzione per tornare alla home");
        return check;
}



}