package test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import MDUAL.MDUAL;
import loader.DataLoader;
import loader.Query;
import loader.QueryLoader;
import loader.Tuple;

public class autoTester {
	public static void main(String[] args) throws IOException {
		String dataset = "FC";
		String queryset = dataset+"_Q10";
		int nW = 50;
		
		DataLoader dLoader = new DataLoader(dataset);
		QueryLoader qLoader = new QueryLoader(queryset);
		int nS = qLoader.maxW/qLoader.gcdS;
		int gcdS = qLoader.gcdS;

		MDUAL MDUAL = new MDUAL(dLoader.dim, dLoader.subDim, nS, gcdS, dLoader.getMinValues());
		NAIVE NAIVE = new NAIVE(nS, gcdS);
		
		for (int itr = 0; itr < nW+nS-1; itr++) {
			HashMap<Integer,Query> newQuerySet = qLoader.getQuerySetByQID(itr*5,10);
			if (newQuerySet.isEmpty()) break;
			ArrayList<Tuple> newSlideTuples_NETSPlus = dLoader.getNewSlideTuples(itr, qLoader.gcdS);
			ArrayList<Tuple> newSlideTuples_NAIVE = dLoader.getNewSlideTuples(itr, qLoader.gcdS);
			if (newSlideTuples_NETSPlus.isEmpty()) break;

			HashSet<Tuple> outliers_MDUAL = MDUAL.findOutlier(newSlideTuples_NETSPlus, newQuerySet, itr);
			HashSet<Tuple> outliers_NAIVE = NAIVE.findOutlier(newSlideTuples_NAIVE, newQuerySet, itr);
			
			HashMap<Integer, HashSet<Integer>> outliers = new HashMap<Integer, HashSet<Integer>>();
			for(Tuple o:outliers_NAIVE) outliers.put(o.id,o.outlierQueryIDs);
			for(Tuple o:outliers_MDUAL) {
				if(outliers.containsKey(o.id)) {
					for(int qid:o.outlierQueryIDs) {
						Boolean removed = outliers.get(o.id).remove(qid);
						if(!removed) System.out.println("NETS+ outlier "+o.id+" having false qid "+qid);
					}
					if(outliers.get(o.id).isEmpty()) {
						 outliers.remove(o.id);
					}else {
						System.out.println("NETS+ missed "+outliers.get(o.id).size()+" qids");
					}
				}else {
					System.out.println("NETS+ returned false outlier "+o.id);
				}
			}
			if(outliers.size() == 0) {
				System.out.println("Itr "+itr+ " clear!");
			}else {
				System.out.println("At itr "+itr+ ", NETS missed "+outliers.keySet());
			}
		}
		
	}
}
