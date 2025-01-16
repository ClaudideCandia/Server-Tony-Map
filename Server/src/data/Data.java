package src.data;

import src.Keyboard;
import src.database.DbAccess;
import src.database.TableData;
import src.exceptions.EmptySetException;
import src.exceptions.MissingNumberException;
import src.exceptions.NoDataException;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

//**********************************************************************************
// Interfacce implementate:
// Iterable: Per poter utilizzare L'iteratore sulla collection di tipo List data
// Serializable: Per poter serializzare e quindi salvare su file la classe
//
//**********************************************************************************

public class Data implements Iterable<Example>, Serializable{
    /** Dataset */
    private List<Example> data = new ArrayList<>();

    /**
     * Costruttore parametrizzato di un oggetto Data con Esempi letti dal Database
     * @param tableName Nome della tabella da interrogare da cui leggere gli esempi
     * @throws NoDataException
     */
    public Data(String tableName) throws NoDataException{
        DbAccess db = new DbAccess();
        TableData tb = new TableData(db);
        try {
            data = tb.getDistinctTransazioni(tableName);

        } catch (SQLException e) {

            throw new NoDataException("Errore di connessione al Db");

        } catch (EmptySetException e) {

            throw new NoDataException("Errore nel contenuto della tabella.");

        } catch (MissingNumberException e) {

            throw new NoDataException("Errore, la tabella non contiene valori numerici.");

        }
    }

    /**
     * Implementazione del metodo virtuale iterator presente nell'interfaccia Iterable
     * @return Iteratore per la Collection di tipo ArrayList data
     */
    public Iterator<Example> iterator(){
        return data.iterator();
    }

    /**
     * Restituisce il numero di esempi nel Dataset
     * @return numero di esempi memorizzati in data
     */
    public int getNumberOfExample() {
        return data.size();
    }

    /**
     * Restituisce l'esempio nel dataset in posizione passata come parametro
     * @param exampleIndex indice di un esempio memorizzato in data
     * @return l’esempio memorizzato in nella posizione passata
     */
    public Example getExample(int exampleIndex) {
        return data.get(exampleIndex);
    }

    /**
     * Restituisce la matrice triangolare superiore delle distanze
     * @return matrice triangolare superiore delle distanze Euclidee calcolate tra gli esempi memorizzati in data.
     * Tale matrice va avvalorata usando il metodo distance di Example
     */
    public double[][] distance () {
        double [][] distanceMatrix = new double[getNumberOfExample()][getNumberOfExample()];
        for(int i = 0; i < getNumberOfExample(); i++) {
            for(int j = i; j < getNumberOfExample(); j++) {
                distanceMatrix[i][j] = this.getExample(i).distance(getExample(j));
            }
        }
        return distanceMatrix;
    }

    /**
     * Polimorfismo ad hoc per il metodo toString della classe Object realizzato per la classe Data.
     * Crea una stringa in cui memorizza gli esempi memorizzati nell’attributo data, opportunamente enumerati.
     * Restituisce tale stringa
     * @return stringa che modella lo stato dell'oggetto
     */
    public String toString() {
        StringBuilder str = new StringBuilder();

        Iterator<Example> i = iterator();
        int tag = 0; //Utilizzato per stampare il numero ordinale

        while(i.hasNext()) {
            str.append(tag);
            str.append(":");
            str.append(i.next());
            str.append("\n");
            tag++;
        }
        return str.toString();
    }

    //-----------------------------------------------------------------------------------------------------------------
    //
    //  Di conseguenza alla task "Eliminare il metodo (costruttore) Data() della classe" ho dovuto anche modificare
    //  il metodo main della classe Data in modo da utilizzare l'unico costruttore possibile che prende gli esempi dal
    //  Db
    //
    //-----------------------------------------------------------------------------------------------------------------

    public static void main(String args[]){
		Data trainingSet = null;
		boolean loadedData = false;
		do { 

			try {

				System.out.print("Inserisci il nome della tabella da cui recuperare gli Esempi: ");
				String tableName = Keyboard.readString();
				trainingSet = new Data(tableName);
				loadedData = true;

			} catch (NoDataException e) {
	
				System.out.println(e.getMessage());
				
			}
	
		} while (!loadedData);
		System.out.println(trainingSet);
		double [][] distancematrix=trainingSet.distance();
		System.out.println("Distance matrix:\n");
		for(int i=0;i<distancematrix.length;i++) {
			for(int j=0;j<distancematrix.length;j++)
				System.out.print(distancematrix[i][j]+"\t");
			System.out.println("");
		}
	}
}
