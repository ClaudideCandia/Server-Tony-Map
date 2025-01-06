package src.distance;

import src.clustering.Cluster;
import src.data.Data;

public interface ClusterDistance {
		double distance(Cluster c1, Cluster c2, Data d);
}
