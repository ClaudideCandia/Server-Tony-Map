package src.clustering;

import src.data.Data;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

//**********************************************************************************
// Interfacce implementate:
// Iterable: Per poter utilizzare L'iteratore sulla collection di tipo Set clusterdata
// Clonable: Per poter creare una copia non superficiale dell'oggetto
// Serializable: Per poter serializzare e quindi salvare su file la classe
//**********************************************************************************

/**
 * La classe Cluster rappresenta un insieme di indici interi (clusteredData)
 * implementata utilizzando una struttura dati {@link TreeSet} per garantire
 * l'unicità e l'ordinamento naturale degli elementi.
 * <p>
 * La classe implementa le seguenti interfacce:
 * <ul>
 * <li>{@link Iterable}: per permettere l'iterazione sugli elementi del cluster.</li>
 * <li>{@link Cloneable}: per creare una copia non superficiale del cluster.</li>
 * <li>{@link Serializable}: per abilitare la serializzazione dell'oggetto.</li>
 * </ul>
 */
public class Cluster implements Iterable<Integer>, Cloneable, Serializable {

	/**
	 * Insieme degli indici che appartengono al cluster.
	 * Utilizza un {@link TreeSet} per garantire l'unicità e l'ordinamento.
	 */
	private Set<Integer> clusteredData = new TreeSet<>();

	/**
	 * Implementazione del metodo virtuale {@code iterator} presente nell'interfaccia {@link Iterable}.
	 *
	 * @return Iteratore per la Collection di tipo {@link TreeSet} clusteredData.
	 */
	public Iterator<Integer> iterator() {
		return clusteredData.iterator();
	}

	//*************************************************************************
	// L'implementazione del metodo addData è stata sostituita dal metodo add
	// presente nell'interfaccia Set e implementato in TreeSet
	// Poiché rispecchia tutte le proprietà che necessita il tipo di dato
	// Tra cui: l'unicità degli elementi
	//*************************************************************************

	/**
	 * Aggiunge un indice al Cluster.
	 *
	 * @param id Indice da aggiungere.
	 */
	public void addData(int id) {
		clusteredData.add(id);
	}

	/**
	 * Restituisce la dimensione del cluster.
	 *
	 * @return Dimensione del cluster.
	 */
	public int getSize() {
		return clusteredData.size();
	}

	/**
	 * Implementazione del metodo {@code clone} della classe {@link Object}.
	 *
	 * @return Oggetto clonato.
	 * @throws CloneNotSupportedException Se l'oggetto non supporta l'interfaccia {@link Cloneable}.
	 */
	public Object clone() {
		try {
			Cluster cloned = (Cluster) super.clone();
			cloned.clusteredData = new TreeSet<>(this.clusteredData);
			return cloned;
		} catch (CloneNotSupportedException e) {
			System.out.println("Errore di clonazione, restituzione di un nuovo oggetto null");
			return null;
		}
	}

	/**
	 * Crea un nuovo cluster che è la fusione dei due cluster pre-esistenti.
	 *
	 * @param c Cluster da fondere con il cluster {@code this}.
	 * @return Cluster risultante dalla fusione di {@code c} e {@code this}.
	 */
	public Cluster mergeCluster(Cluster c) {
		Cluster newCluster = (Cluster) this.clone();
		Iterator<Integer> i = c.iterator();
		while (i.hasNext()) {
			newCluster.clusteredData.add(i.next());
		}
		return newCluster;
	}

	/**
	 * Restituisce una rappresentazione testuale del cluster.
	 *
	 * @return Stringa contenente gli indici del cluster separati da virgole.
	 */
	public String toString() {
		String str = "";
		Iterator<Integer> i = this.iterator();

		while (i.hasNext()) {
			str += i.next();
			if (i.hasNext()) {
				str += ",";
			}
		}

		return str;
	}

	/**
	 * Restituisce una rappresentazione testuale del cluster basata sui dati associati.
	 *
	 * @param data Oggetto {@link Data} che fornisce i dettagli per ogni indice del cluster.
	 * @return Stringa contenente i dettagli degli indici del cluster.
	 */
	public String toString(Data data) {
		String str = "";

		for (Integer id : clusteredData) {
			str += "<" + data.getExample(id) + ">";
		}

		return str;
	}
}
