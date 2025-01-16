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
//
//**********************************************************************************

public class Cluster implements Iterable<Integer>, Cloneable, Serializable{	

	private Set<Integer> clusteredData = new TreeSet<>();

    /**
     * Implementazione del metodo virtuale iterator presente nell'interfaccia Iterable
     * @return Iteratore per la Collection di tipo TreeSet clusteredData
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
	 * Aggiunge un indice al Cluster
	 * @param id indice da aggiungere
	 */
	public void addData(int id) {
		clusteredData.add(id);
	}
		
	/**
	 * Restituisce la dimensione del cluster
	 * @return dimensione del cluster
	 */
	public int getSize() {
		return clusteredData.size();
	}
	
	/**
	 * Implementazione del metodo clone della classe {@code Object}
	 * @return Oggetto clonato
	 * @throws CloneNotSupportedException Se l'oggetto non supporta l'interfaccia Clonable
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
	 * crea un nuovo cluster che è la fusione dei due cluster pre-esistenti
	 * @param c Cluster da fondere col cluster {@code this}
	 * @return Cluster risultante dalla fusione di {@code c & this}
	 */
	public Cluster mergeCluster (Cluster c)
	{
		Cluster newCluster = (Cluster) this.clone();
		Iterator<Integer> i = c.iterator();
		while(i.hasNext()) {
			newCluster.clusteredData.add(i.next());
		}
		return newCluster;
	}
	
	
	public String toString() {		
		String str="";
		Iterator<Integer> i = this.iterator();

		while(i.hasNext()) {
			str+=i.next();
			if(i.hasNext()) {
				str+=",";
			}
		}

		return str;	
	}
	
	public String toString(Data data){
		String str="";
		
		for(Integer id : clusteredData)
			str+="<"+data.getExample(id)+">";				
		
		return str;
		
	}
	


}
