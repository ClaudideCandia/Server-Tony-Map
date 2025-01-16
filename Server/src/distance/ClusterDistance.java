package src.distance;

import src.clustering.Cluster;
import src.data.Data;

/**
 * Interfaccia che definisce il contratto per calcolare la distanza tra due cluster.
 * Le implementazioni di questa interfaccia devono fornire un metodo per calcolare
 * la distanza utilizzando una specifica metrica o criterio.
 */
public interface ClusterDistance {

	/**
	 * Calcola la distanza tra due cluster utilizzando i dati forniti.
	 *
	 * @param c1 il primo cluster
	 * @param c2 il secondo cluster
	 * @param d l'insieme dei dati che contiene gli esempi appartenenti ai cluster
	 * @return la distanza calcolata tra i due cluster
	 */
	double distance(Cluster c1, Cluster c2, Data d);
}

