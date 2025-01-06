package src.distance;

import src.clustering.Cluster;
import src.data.Data;
import src.data.Example;

import java.util.Iterator;

public class SingleLinkDistance implements ClusterDistance {
	public double distance(Cluster c1, Cluster c2, Data d) {
		
		double min=Double.MAX_VALUE;

		Iterator<Integer> i1 = c1.iterator();
		while(i1.hasNext()) {

			Example e1=d.getExample(i1.next());

			Iterator<Integer> i2 = c2.iterator();
			
			while(i2.hasNext()) {
				double distance=e1.distance(d.getExample(i2.next()));
				if (distance<min)				
					min=distance;
			}
		}
		return min;
	}
}
