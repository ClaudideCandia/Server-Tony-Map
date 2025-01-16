package src.distance;

import src.clustering.Cluster;
import src.data.Data;
import src.data.Example;

import java.util.Iterator;

/**
 * Classe che implementa l'interfaccia {@link ClusterDistance} e fornisce
 * un metodo per calcolare la distanza tra due cluster utilizzando il criterio
 * della distanza minima (Single Linkage).
 */
public class SingleLinkDistance implements ClusterDistance {

	/**
	 * Calcola la distanza minima tra tutti i punti appartenenti ai due cluster specificati.
	 *
	 * @param c1 il primo cluster
	 * @param c2 il secondo cluster
	 * @param d l'insieme dei dati che contiene gli esempi
	 * @return la distanza minima tra i due cluster
	 */
	public double distance(Cluster c1, Cluster c2, Data d) {
		double min = Double.MAX_VALUE;

		// Iteratore per scorrere gli indici degli esempi nel primo cluster
		Iterator<Integer> i1 = c1.iterator();
		while (i1.hasNext()) {
			Example e1 = d.getExample(i1.next()); // Recupera un esempio dal primo cluster

			// Iteratore per scorrere gli indici degli esempi nel secondo cluster
			Iterator<Integer> i2 = c2.iterator();
			while (i2.hasNext()) {
				// Calcola la distanza tra gli esempi
				double distance = e1.distance(d.getExample(i2.next()));

				// Aggiorna il valore minimo se la distanza corrente è inferiore
				if (distance < min) {
					min = distance;
				}
			}
		}
		return min; // Restituisce la distanza minima trovata
	}
}

