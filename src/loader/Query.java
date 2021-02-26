package loader;

import java.util.HashSet;

public class Query {
	public int id;
	public double R;
	public int K;
	public int W;
	public int S;
	public HashSet<Tuple> outliers;
	public int nnToFind;
		
	public Query(int id, double R, int K, int W, int S) {
		this.id = id;
		this.R = R;
		this.K = K;
		this.W = W;
		this.S = S;
	}

	@Override
	public boolean equals(Object obj){
		Query _obj = (Query) obj;
		return _obj.id == this.id;
	}
}
