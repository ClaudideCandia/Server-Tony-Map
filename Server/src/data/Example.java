package src.data;

import src.exceptions.InvalidSizeException;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

//**********************************************************************************
// Interfacce implementate:
// Iterable: Per poter utilizzare L'iteratore sulla collection di tipo List example
// Serializable: Per poter serializzare e quindi salvare su file la classe
//
//**********************************************************************************
public class Example implements Iterable<Double>, Serializable {
    
    /**
     * Collection di valori reali
     */
    private List<Double> example;

    /**
     * Inizializza example una Collection di tipo {@code LinkedList}
     */
    public Example() {
        example = new LinkedList<>(); 
    }

    /**
     * Implementazione del metodo virtuale iterator presente nell'interfaccia Iterable
     * @return Iteratore per la Collection di tipo LinkedList example
     */
    public Iterator<Double> iterator(){
        return example.iterator();
    }

    /**
     * Aggiunge alla collection di tipo List {@code example} il valore passatogli come parametro
     * @param v Valore reale da inserire nella Collection
     */
    public void add(Double v) {
        example.add(v);
    }

    /**
     * Restituisce il valore reale presente nella collection in posizione del parametro passato.
     * @param index Posizione del valore reale all'interno della collection
     * @return Valore reale estrapolato dalla collection
     */
    public Double get(int index) {
        return example.get(index);
    }

    /**
     * Calcola la distanza euclidea tra this.example e newE.example
     * @param newE instanza di example
     * @return restituisce il valore calcolato
     */
    public Double distance(Example newE) {

        Double eucDis = 0.0;

        try {
            
            if(this.example.size() != newE.example.size()) {
                throw new InvalidSizeException("Dimensioni degli esempi differenti: "+this.example.size()+"!="+newE.example.size());
            }
    
            Iterator<Double> i1 = this.iterator();
            Iterator<Double> i2 = newE.iterator();

            while(i1.hasNext()) {
                Double val1 = i1.next();
                Double val2 = i2.next();
                Double diff = val1 - val2;
                eucDis = eucDis + (diff * diff);
            }

        } catch (InvalidSizeException e) {

            System.out.println(e.getMessage());
            System.out.println("La distanza verrà calcolata in base all'esempio di dimensione minore.");

            Iterator<Double> min;
            Iterator<Double> other;

            if(this.example.size() < newE.example.size()) {
                min = this.iterator();
                other = newE.iterator();
            } else {
                min = newE.iterator();
                other = this.iterator();
            }

            while(min.hasNext()) {
                Double val1 = min.next();
                Double val2 = other.next();
                Double diff = val1 - val2;
                eucDis = eucDis + (diff * diff);
            }
            
        }
        
        return eucDis;

    }
    
    /**
     * Polimorfismo ad hoc per il metodo toString della classe Object realizzato per la classe Example
     * @return stringa contenente la rappresentazione in stringa di caratteri sotto forma di {@code [ V0, V1 ... Vn ]} dove Vi sono valori dell'esempio
     */
    public String toString() {
        StringBuilder str = new StringBuilder ("[");
        for (Double val : example) {
            str.append(val);
            if(val != example.getLast()) {
                str.append(",");
            }
        }
        str.append("]");
        return str.toString();
    }
}
