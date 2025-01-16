package src.distance;

import src.clustering.Cluster;
import src.data.Data;
import src.data.Example;

import java.util.Iterator;

/**
 * Classe che implementa l'interfaccia {@link ClusterDistance} e fornisce
 * un metodo per calcolare la distanza tra due cluster utilizzando
 * il criterio della distanza media (Average Linkage).
 */
public class AverageLinkDistance implements ClusterDistance {

    /**
     * Calcola la distanza media tra tutti i punti appartenenti ai due cluster specificati.
     *
     * @param c1 il primo cluster
     * @param c2 il secondo cluster
     * @param d l'insieme dei dati che contiene gli esempi
     * @return la distanza media tra i due cluster
     */
    public double distance(Cluster c1, Cluster c2, Data d) {
        double average = 0;

        // Iteratore per scorrere gli indici degli esempi nel primo cluster
        Iterator<Integer> i1 = c1.iterator();

        while (i1.hasNext()) {
            Example e1 = d.getExample(i1.next()); // Recupera un esempio dal primo cluster
            Iterator<Integer> i2 = c2.iterator(); // Iteratore per il secondo cluster

            while (i2.hasNext()) {
                average += e1.distance(d.getExample(i2.next())); // Somma le distanze tra gli esempi
            }
        }

        // Calcola la media dividendo la somma per il prodotto delle dimensioni dei cluster
        return (average / (c1.getSize() * c2.getSize()));
    }
}

