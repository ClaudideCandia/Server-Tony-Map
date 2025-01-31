package src.clustering;

import src.data.Data;
import src.distance.ClusterDistance;
import src.exceptions.InvalidDepthException;

import java.io.*;
import java.nio.file.Paths;

//**************************************************************************************************************************
// Interfacce implementate:
// Serializable: Per poter serializzare e quindi salvare su file la classe
// Serializable è stata implementata anche in tutte le classi di cui HierarchicalClusterMiner è composta e fa riferimento
// altrimenti la serializzazione non sarebbe possibile.
//**************************************************************************************************************************

/**
 * La classe {@code HierachicalClusterMiner} implementa un algoritmo di clustering gerarchico
 * rappresentato tramite un dendrogramma. Consente di eseguire il clustering dei dati,
 * nonché di salvare e caricare l'oggetto su/da file mediante serializzazione.
 */
public class HierachicalClusterMiner implements Serializable {

	/**
	 * Oggetto {@link Dendrogram} che rappresenta la struttura del clustering gerarchico.
	 */
	private Dendrogram dendrogram;

	/**
	 * Costruttore della classe {@code HierachicalClusterMiner} che inizializza il dendrogramma
	 * con una profondità specificata.
	 *
	 * @param depth Profondità massima del dendrogramma.
	 */
	public HierachicalClusterMiner(int depth) {
		dendrogram = new Dendrogram(depth);
	}

	/**
	 * Restituisce una rappresentazione testuale del dendrogramma.
	 *
	 * @return Stringa rappresentante il dendrogramma.
	 */
	public String toString() {
		return dendrogram.toString();
	}

	/**
	 * Restituisce una rappresentazione testuale del dendrogramma basata sui dati.
	 *
	 * @param data Oggetto {@link Data} che fornisce informazioni aggiuntive per la rappresentazione.
	 * @return Stringa rappresentante il dendrogramma con i dati.
	 */
	public String toString(Data data) {
		return dendrogram.toString(data);
	}

	/**
	 * Esegue il clustering dei dati forniti creando livelli successivi del dendrogramma.
	 * <p>
	 * Il primo livello (livello 0) contiene un cluster separato per ogni esempio.
	 * I livelli successivi sono costruiti fondendo i due cluster più vicini fino a raggiungere
	 * la profondità specificata del dendrogramma.
	 *
	 * @param data Esempi su cui lavorare.
	 * @param distance Algoritmo di distanza tra cluster con cui lavorare.
	 */
	public void mine(Data data, ClusterDistance distance) {

		try {

			if (dendrogram.getDepth() > data.getNumberOfExample()) {
				throw new InvalidDepthException("profondità maggiore del numero degli esempi: " + dendrogram.getDepth() + " > " + data.getNumberOfExample());
			}

			ClusterSet cSet = new ClusterSet(data.getNumberOfExample());
			for (int i = 0; i < data.getNumberOfExample(); i++) {
				Cluster c = new Cluster();
				c.addData(i);
				cSet.add(c);
			}

			dendrogram.setClusterSet(cSet, 0);

			for (int i = 1; i < dendrogram.getDepth(); i++) {
				cSet = cSet.mergeClosestClusters(distance, data);
				dendrogram.setClusterSet(cSet, i);
			}

		} catch (InvalidDepthException e) {

			System.out.println(e.getMessage());
			System.out.println("Ricostruisco il dendrogramma col numero massimo di livelli possibili:");

			dendrogram = new Dendrogram(data.getNumberOfExample());

			this.mine(data, distance);

		}
	}

	//**********************************************************************************************************************
	// Metodi per Serializzazione e de-Serializzazione
	// ObjectInputStream e ObjectOutStream: stream di Manipolazione che utilizzati congiutamente a un
	// OutputStream e un InputStream in questo caso File, permettono lo stream dei dati in maniera binaria Da e Verso il File.
	//
	//**********************************************************************************************************************

	/**
	 * Metodo per il recupero di un oggetto {@code HierachicalClusterMiner} da un file. (de-Serializzazione)
	 *
	 * @param filename Nome del file / Directory da cui leggere l'oggetto.
	 * @return Oggetto recuperato dal file.
	 * @throws FileNotFoundException Lanciata in caso il file specificato non esista.
	 * @throws IOException Lanciata in caso di un'operazione di Input/Output fallita o interrotta.
	 * @throws ClassNotFoundException Lanciata quando si tenta di caricare una classe non trovata nel Class Loader di Java.
	 */
	public static HierachicalClusterMiner loadHierachicalClusterMiner(String filename)
			throws FileNotFoundException, IOException, ClassNotFoundException {

		// Percorso relativo alla directory del progetto
		String directory = Paths.get("").toAbsolutePath() + File.separator + "FileDir";

		// Percorso completo del file
		String filePath = directory + File.separator + filename;

		// Lettura dell'oggetto dal file
		try (ObjectInputStream inStream = new ObjectInputStream(new FileInputStream(filePath))) {
			return (HierachicalClusterMiner) inStream.readObject();
		}
	}
	/**
	 * Metodo per il salvataggio su file di un oggetto {@code HierachicalClusterMiner} (Serializzazione).
	 *
	 * @param filename Nome del file / Directory su cui scrivere l'oggetto.
	 * @throws FileNotFoundException Lanciata in caso il file specificato non esista.
	 * @throws IOException Lanciata in caso di un'operazione di Input/Output fallita o interrotta.
	 */
	public void salva(String filename) throws IOException {
		// Percorso relativo alla directory del progetto
		String directory = Paths.get("").toAbsolutePath() + File.separator + "FileDir";

		// Creazione della directory se non esiste
		File dir = new File(directory);
		if (!dir.exists()) {
			dir.mkdirs(); // Crea la directory e eventuali sottodirectory
		}

		// Percorso completo del file con estensione corretta
		String filePath = directory + File.separator + filename + ".HCM";

		// Scrittura dell'oggetto nel file
		try (ObjectOutputStream outStream = new ObjectOutputStream(new FileOutputStream(filePath))) {
			outStream.writeObject(this);
			System.out.println("Oggetto serializzato in: " + filePath);
		}
	}


}

