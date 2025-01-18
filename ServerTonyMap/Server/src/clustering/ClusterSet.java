package src.clustering;

import src.data.Data;
import src.distance.ClusterDistance;
import src.exceptions.ImpossibleClusterMerge;

import java.io.Serializable;

//**********************************************************************************
// Interfacce implementate:
// Serializable: Per poter serializzare e quindi salvare su file la classe
//**********************************************************************************

/**
 * La classe ClusterSet gestisce un insieme di cluster ({@link Cluster}) e fornisce metodi per
 * aggiungere cluster, accedere ai cluster memorizzati e determinare la coppia di cluster più
 * simili per unirli.
 * <p>
 * Questa classe implementa l'interfaccia {@link Serializable} per consentire la
 * serializzazione e il salvataggio su file.
 */
class ClusterSet implements Serializable {

	/**
	 * Array di oggetti {@link Cluster} che rappresenta l'insieme dei cluster.
	 */
	private Cluster[] C;

	/**
	 * Indice dell'ultimo cluster aggiunto.
	 */
	private int lastClusterIndex = 0;

	/**
	 * Costruttore della classe ClusterSet.
	 *
	 * @param k Numero massimo di cluster che possono essere memorizzati.
	 */
	public ClusterSet(int k) {
		C = new Cluster[k];
	}

	/**
	 * Aggiunge un cluster al ClusterSet se non è già presente.
	 *
	 * @param c Cluster da aggiungere.
	 */
	public void add(Cluster c) {
		for (int j = 0; j < lastClusterIndex; j++) {
			if (c == C[j]) { // Evita duplicati
				return;
			}
		}
		C[lastClusterIndex] = c;
		lastClusterIndex++;
	}

	/**
	 * Restituisce il cluster memorizzato in una determinata posizione.
	 *
	 * @param i Indice del cluster da recuperare.
	 * @return Cluster nella posizione specificata.
	 */
	public Cluster get(int i) {
		return C[i];
	}

	/**
	 * Restituisce una rappresentazione testuale dei cluster memorizzati.
	 *
	 * @return Stringa contenente i dettagli dei cluster.
	 */
	public String toString() {
		String str = "";
		for (int i = 0; i < C.length; i++) {
			if (C[i] != null) {
				str += "cluster" + i + ":" + C[i] + "\n";
			}
		}
		return str;
	}

	/**
	 * Restituisce una rappresentazione testuale dei cluster basata sui dati associati.
	 *
	 * @param data Oggetto {@link Data} che fornisce i dettagli per ogni cluster.
	 * @return Stringa contenente i dettagli dei cluster basati sui dati.
	 */
	public String toString(Data data) {
		String str = "";
		for (int i = 0; i < C.length; i++) {
			if (C[i] != null) {
				str += "cluster" + i + ":" + C[i].toString(data) + "\n";
			}
		}
		return str;
	}

	/**
	 * Determina la coppia di cluster più simili utilizzando il metodo {@code distance} di
	 * {@link ClusterDistance} e li fonde in un unico cluster.
	 *
	 * @param distance Oggetto per il calcolo della distanza tra cluster.
	 * @param data Oggetto {@link Data} che rappresenta il dataset corrente.
	 * @return Nuova istanza di ClusterSet con i cluster fusi, oppure {@code this} se non è
	 *         possibile eseguire la fusione.
	 */
	public ClusterSet mergeClosestClusters(ClusterDistance distance, Data data) {
		try {

			if (lastClusterIndex == 1) {
				throw new ImpossibleClusterMerge("Impossibile unire dei cluster, ne è presente solo uno.");
			}

			ClusterSet newClusterSet = new ClusterSet(lastClusterIndex - 1);
			double minDistance = Double.MAX_VALUE;
			int closestC1 = 0;
			int closestC2 = 0;
			boolean inserted = false;

			for (int i = 0; i < lastClusterIndex - 1; i++) {
				for (int j = i + 1; j < lastClusterIndex; j++) {
					double tmpDistance = distance.distance(C[i], C[j], data);
					if (tmpDistance < minDistance) {
						minDistance = tmpDistance;
						closestC1 = i;
						closestC2 = j;
					}
				}
			}

			Cluster newCluster = (Cluster) C[closestC1].clone();
			newCluster = newCluster.mergeCluster(C[closestC2]);

			for (int i = 0; i < lastClusterIndex; i++) {
				if (i != closestC1 && i != closestC2) {
					newClusterSet.add(C[i]);
				} else if (!inserted) {
					newClusterSet.add(newCluster);
					inserted = true;
				}
			}

			return newClusterSet;

		} catch (ImpossibleClusterMerge e) {

			System.out.println(e.getMessage());

			return this;

		}
	}
}

