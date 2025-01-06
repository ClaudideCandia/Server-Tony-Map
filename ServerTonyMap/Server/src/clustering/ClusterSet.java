package src.clustering;

import src.data.Data;
import src.distance.ClusterDistance;
import src.exceptions.ImpossibleClusterMerge;

import java.io.Serializable;

//**********************************************************************************
// Interfacce implementate:
// Serializable: Per poter serializzare e quindi salvare su file la classe
//
//**********************************************************************************

class ClusterSet implements Serializable {

	private Cluster C[];
	private int lastClusterIndex=0;
	
	public ClusterSet(int k){
		C=new Cluster[k];
	}
	
	public void add(Cluster c){
		for(int j=0;j<lastClusterIndex;j++)
			if(c==C[j]) // to avoid duplicates
				return;
		C[lastClusterIndex]=c;
		lastClusterIndex++;
	}
	
	public Cluster get(int i){
		return C[i];
	}
	
	
	
	public String toString(){
		String str="";
		for(int i=0;i<C.length;i++){
			if (C[i]!=null){
				str+="cluster"+i+":"+C[i]+"\n";
		
			}
		}
		return str;
		
	}

	
	public String toString(Data data){
		String str="";
		for(int i=0;i<C.length;i++){
			if (C[i]!=null){
				str+="cluster"+i+":"+C[i].toString(data)+"\n";
		
			}
		}
		return str;
		
	}

	/**
	 * determina la coppia di cluster più simili (usando il metodo distance 
	 * di ClusterDistance e li fonde in unico cluster; crea una nuova istanza di ClusterSet 
	 * che contiene tutti i cluster dell’oggetto this a meno dei due cluster fusi al posto dei 
	 * quali inserisce il cluster risultante dalla fusione (nota bene l’oggetto ClusterSet 
	 * risultante memorizza un numero di cluster che è pari al numero di cluster 
	 * memorizzato nell’oggetto this meno 1).
	 * @param distance oggetto per il calcolo della distanza tra cluster
	 * @param data oggetto istanza che rappresenta il dataset in cui si sta calcolando l’oggetto istanza di ClusterSet
	 * @return nuova istanza di ClusterSet se possibile unire due Cluster, altrimenti {@code this}
	 */
	public ClusterSet mergeClosestClusters(ClusterDistance distance, Data data) {
		try {

			if(lastClusterIndex == 1) {
				throw new ImpossibleClusterMerge("impossibile unire dei cluster, ne è presente solo uno.");
			}
			
			ClusterSet newClusterSet = new ClusterSet(lastClusterIndex-1);
			double minDistance = Double.MAX_VALUE;
			int closestC1 = 0;
			int closestC2 = 0;
			boolean inserted = false;

			for(int i = 0; i < lastClusterIndex - 1; i++) {
				for(int j = i + 1; j < lastClusterIndex; j++) {
					double tmpDistance = distance.distance(C[i], C[j], data);
					if(tmpDistance < minDistance) {
						minDistance = tmpDistance;
						closestC1 = i;
						closestC2 = j;
					}
				}
			}

			Cluster newCluster = (Cluster) C[closestC1].clone();
			newCluster = newCluster.mergeCluster(C[closestC2]);

			for(int i = 0; i < lastClusterIndex; i++) {
				if(i != closestC1 && i != closestC2) {
					newClusterSet.add(C[i]);
				} else if (!inserted){
					newClusterSet.add(newCluster);
					inserted = true;
				}
			}

			return newClusterSet;

		} catch (ImpossibleClusterMerge e) {

			System.out.println(e.getMessage());

			return this;

		}
	}

}
