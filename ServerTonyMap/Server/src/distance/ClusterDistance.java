package src.distance;

import src.clustering.Cluster;
import src.data.Data;

/**
 * Interfaccia per definire una metrica di distanza tra due cluster.
 * <p>
 * Ogni implementazione di questa interfaccia deve fornire un metodo per calcolare
 * la distanza tra due cluster basandosi su un dataset {@link Data}.
 * </p>
 */
public interface ClusterDistance {

	/**
	 * Calcola la distanza tra due cluster specificati utilizzando un dataset di riferimento.
	 *
	 * @param c1 Primo cluster.
	 * @param c2 Secondo cluster.
	 * @param d Dataset {@link Data} contenente gli esempi associati ai cluster.
	 * @return La distanza calcolata tra i due cluster.
	 */
	double distance(Cluster c1, Cluster c2, Data d);
}
