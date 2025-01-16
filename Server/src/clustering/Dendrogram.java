package src.clustering;

import src.data.Data;

import java.io.Serializable;
//**********************************************************************************
// Interfacce implementate:
// Serializable: Per poter serializzare e quindi salvare su file la classe
//
//**********************************************************************************
public class Dendrogram implements Serializable {
    /**Rappresentazione dei livelli del dendrogramma */
    private ClusterSet tree[];

    /**
     * crea un vettore di dimensione depth con cui inizializza tree.
     * @param depth profondità del dendrogramma
     */
    public Dendrogram(int depth) {
        tree = new ClusterSet[depth];
    }

    /**
     * memorizza c nella posizione level di tree
     * @param c nuovo ClusterSet da memorizzare
     * @param level livello del dendrogramma dove memorizzare il ClusterSet
     */
    public void setClusterSet(ClusterSet c, int level) {
        tree[level] = c;
    }

    /**
     * restituisce il ClusterSet del dendrogramma ad un determinato livello
     * @param level livello del dendrogramma
     * @return ClusterSet al livello level
     */
    public ClusterSet getClusterSet(int level) {
        return tree[level];
    }

    /**
     * restituisce la profondità del dendrogramma
     * @return profondità del dendrogramma
     */
    public int getDepth() {
        return tree.length;
    }

    public String toString() {
        String v="";
        for (int i=0;i<tree.length;i++)
            v+=("level"+i+":\n"+tree[i]+"\n");
        return v;
    }

    public String toString(Data data) {
        String v="";
        for (int i=0;i<tree.length;i++)
            v+=("level"+i+":\n"+tree[i].toString(data)+"\n");
        return v;
    }
}
