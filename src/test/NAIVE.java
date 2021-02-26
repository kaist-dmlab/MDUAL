package test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import loader.Query;
import loader.Tuple;

public class NAIVE {
	public HashMap<Integer,Query> querySet;
	public int nS;
	public int gcdS;
	public ArrayList<Tuple> allTuples;
	public HashSet<Tuple> outliers;
	
	public NAIVE(int nS, int gcdS) {
		this.nS = nS;
		this.gcdS = gcdS;
		this.allTuples = new ArrayList<Tuple>();
	}
	
	public HashSet<Tuple> findOutlier(ArrayList<Tuple> newSlideTuples, HashMap<Integer,Query> newQuerySet, int itr) {
		int firstSlideID = itr - nS + 1; 
		if(itr > nS) {
			Iterator<Tuple> allTuplesItr = allTuples.iterator();
			while(allTuplesItr.hasNext()) {
				Tuple t = allTuplesItr.next();
				if(t.slideID < firstSlideID) allTuplesItr.remove();
			}
		}
		allTuples.addAll(newSlideTuples);
		
		this.querySet = newQuerySet;
		
		outliers = new HashSet<Tuple>();
		
		for(Tuple candTuple:allTuples) {
			candTuple.outlierQueryIDs.clear();
			
			boolean outlierFlag = false;
			QueryLoop:
			for(Query q:querySet.values()) {
				if((itr+1) % (q.S/gcdS) > 0) continue;
				firstSlideID = itr - q.W/gcdS + 1;
				if(candTuple.slideID < firstSlideID) continue; 
				
				int nn =0;
				for(Tuple otherTuple:allTuples) {
					if(otherTuple.slideID < firstSlideID) continue;
					double dist = distTuple(candTuple, otherTuple);
					if ((candTuple.id != otherTuple.id) && (dist <= q.R)) {
						nn++;
						//if(nn>=q.K) continue QueryLoop;
					}
				}
				if(nn<q.K){
					outlierFlag = true;
					candTuple.outlierQueryIDs.add(q.id);
				}
			}
			if(outlierFlag) outliers.add(candTuple);
		}
		
		return outliers;
	}
	
	public static double distTuple(Tuple t1, Tuple t2) {
		double ss = 0;
		for(int i = 0; i<t1.value.length; i++) { 
			ss += Math.pow((t1.value[i] - t2.value[i]),2);
		}
		 return Math.sqrt(ss);
	}
	
}
