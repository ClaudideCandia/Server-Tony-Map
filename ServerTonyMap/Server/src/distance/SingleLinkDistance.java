package src.distance;

import src.clustering.Cluster;
import src.data.Data;
import src.data.Example;

import java.util.Iterator;

/**
 * Implementazione dell'interfaccia {@link ClusterDistance} che calcola la distanza
 * tra due cluster utilizzando la strategia "Single Link".
 * <p>
 * La distanza "Single Link" è definita come la distanza minima tra un esempio
 * del primo cluster e un esempio del secondo cluster.
 * </p>
 */
public class SingleLinkDistance implements ClusterDistance {

	/**
	 * Calcola la distanza "Single Link" tra due cluster specificati utilizzando un dataset di riferimento.
	 *
	 * @param c1 Primo cluster.
	 * @param c2 Secondo cluster.
	 * @param d Dataset {@link Data} contenente gli esempi associati ai cluster.
	 * @return La distanza minima calcolata tra un esempio del primo cluster e un esempio del secondo cluster.
	 */
	@Override
	public double distance(Cluster c1, Cluster c2, Data d) {

		double min = Double.MAX_VALUE;

		Iterator<Integer> i1 = c1.iterator();
		while (i1.hasNext()) {

			Example e1 = d.getExample(i1.next());

			Iterator<Integer> i2 = c2.iterator();
			while (i2.hasNext()) {
				double distance = e1.distance(d.getExample(i2.next()));
				if (distance < min)
					min = distance;
			}
		}
		return min;
	}
}
