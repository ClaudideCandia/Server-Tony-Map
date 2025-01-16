package src.clustering;

import src.data.Data;
import src.distance.ClusterDistance;
import src.exceptions.InvalidDepthException;

import java.io.*;

//**************************************************************************************************************************
// Interfacce implementate:
// Serializable: Per poter serializzare e quindi salvare su file la classe
// Serializable è stata implementata anche in tutte le classi di cui HierarchicalClusterMiner è composta e fa riferimento
// altrimenti la serializzazione non sarebbe possibile.
//**************************************************************************************************************************

public class HierachicalClusterMiner implements Serializable {
	
	private Dendrogram dendrogram;

	
	
	
	public HierachicalClusterMiner(int depth) {
		dendrogram= new Dendrogram(depth);
	
	}
	
	
	public String toString() {
		return dendrogram.toString();
	}
	
	public String toString(Data data) {
		return dendrogram.toString(data);
	}

	/**
	 * crea il livello base (livello 0) del dendrogramma che 
	 * contiene  l’istanze  di  ClusterSet  che  rappresenta  ogni  esempio  in  un  cluster 
	 * separato; per tutti i livelli successivi del dentrogramma (level>=1 e level < 
	 * dendrogram.getDepth()) costruisce l’istanza di ClusterSet che realizza la fusione dei 
	 * due  cluster  più  vicini  nella  istanza  del  ClusterSet  memorizzata  al  livello  level-1  del 
	 * dendrogramma  (usare  mergeClosestClusters  di  ClusterSet); memorizza l’istanza di 
	 * ClusterSet ottenuta per fusione nel livello level del dendrogramma.
	 * @param data Esempi su cui lavorare
	 * @param distance Algoritmo di distanza tra cluster con cui lavorare
	 */
	public void mine(Data data, ClusterDistance distance) {
		
		try {

			if(dendrogram.getDepth() > data.getNumberOfExample()) {
				throw new InvalidDepthException("profondità maggiore del numero degli esempi: "+dendrogram.getDepth()+" > "+data.getNumberOfExample());
			}
	
			ClusterSet cSet = new ClusterSet(data.getNumberOfExample());
			for(int i=0; i<data.getNumberOfExample(); i++) {
				Cluster c = new Cluster();
				c.addData(i);
				cSet.add(c);
			}

			dendrogram.setClusterSet(cSet, 0);

			for(int i=1; i < dendrogram.getDepth(); i++) {
				cSet = cSet.mergeClosestClusters(distance, data);
				dendrogram.setClusterSet(cSet,i);
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
	 * @param filename Nome del file / Directory da cui leggere l'oggetto.
	 * @return Oggetto recuperato dal file.
	 * @throws FileNotFoundException Lanciata in caso il file specificato non esista.
	 * @throws IOException Lanciata in caso di un'operazione di Input/Output fallita o interrotta
	 * @throws ClassNotFoundException Lanciata quando si tenta di caricare una classe non trovata nel Class Loader di Java.
	 */
	public static HierachicalClusterMiner loaHierachicalClusterMiner(String filename) throws FileNotFoundException,
	IOException, ClassNotFoundException {
		FileInputStream inFile = new FileInputStream(filename + ".HCM");
		ObjectInputStream inStream = new ObjectInputStream(inFile);
		HierachicalClusterMiner loadedHierachicalClusterMiner = (HierachicalClusterMiner) inStream.readObject();
		inStream.close();
		return loadedHierachicalClusterMiner;
	}

	/**
	 * Metodo per il salvataggio su file di un oggetto {@code HierachicalClusterMiner} (Serializzazione)
	 * @param filename Nome del file / Directory su cui scrivere l'oggetto
	 * @throws FileNotFoundException Lanciata in caso il file specificato non esista.
	 * @throws IOException Lanciata in caso di un'operazione di Input/Output fallita o interrotta
	 */
	public void salva(String filename) throws FileNotFoundException, IOException {
		FileOutputStream outFile = new FileOutputStream(filename + ".HCM");
		ObjectOutputStream outStream = new ObjectOutputStream(outFile);
		outStream.writeObject(this);
		outStream.close();
	}
}
