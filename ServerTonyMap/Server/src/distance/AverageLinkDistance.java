package src.distance;

import src.clustering.Cluster;
import src.data.Data;
import src.data.Example;

import java.util.Iterator;

/**
 * Implementazione della metrica di distanza basata sull'Average Linkage.
 * Calcola la distanza media tra tutte le coppie di esempi appartenenti a due cluster distinti.
 */
public class AverageLinkDistance implements ClusterDistance {

    /**
     * Calcola la distanza media tra due cluster utilizzando gli esempi presenti nel dataset.
     * <p>
     * La distanza è calcolata come la somma delle distanze tra ogni coppia di esempi
     * (uno appartenente al primo cluster e l'altro al secondo cluster), divisa per il prodotto
     * del numero di esempi nei due cluster.
     * </p>
     *
     * @param c1 Primo cluster.
     * @param c2 Secondo cluster.
     * @param d Dataset {@link Data} contenente gli esempi.
     * @return La distanza media tra i due cluster.
     */
    public double distance(Cluster c1, Cluster c2, Data d) {

        double average = 0;

        Iterator<Integer> i1 = c1.iterator();

        // Itera su tutti gli esempi del primo cluster
        while (i1.hasNext()) {

            Example e1 = d.getExample(i1.next());
            Iterator<Integer> i2 = c2.iterator();

            // Calcola la distanza con ogni esempio del secondo cluster
            while (i2.hasNext()) {
                average += e1.distance(d.getExample(i2.next()));
            }
        }
        // Restituisce la distanza media
        return (average / (c1.getSize() * c2.getSize()));
    }
}
