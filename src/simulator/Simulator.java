package simulator;

import loader.Query;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import MDUAL.MDUAL;
import loader.DataLoader;
import loader.QueryLoader;
import loader.Tuple;

public class Simulator {
	public DataLoader dLoader;
	public QueryLoader qLoader;
	double allTimeSum;
	double allMemSum;
	public MemoryThread memThread;
	public String dataset;
	public String queryset;
	public int maxW;
	public int gcdS;
	public int nS;
	
	public Simulator(String dataset, String queryset) throws IOException {
		 dLoader = new DataLoader(dataset);
		 qLoader = new QueryLoader(queryset);
		 this.dataset = dataset;
		 this.queryset = queryset;
		 this.gcdS = qLoader.gcdS;
		 this.nS = qLoader.maxW/qLoader.gcdS;
		 memThread = new MemoryThread();		 
	}
	
	@SuppressWarnings("deprecation")
	public void run(int nW, int numQueries, double changedQRatio) throws IOException {
		MDUAL MDUAL = new MDUAL(dLoader.dim, dLoader.subDim, nS, gcdS, dLoader.getMinValues());
		
		memThread.start();
		int numWin = 0;
		int numChangedQueries = (int)(numQueries*changedQRatio);
		int totalOutliers = 0;
		int totalOutQueires = 0;
		
		for (int itr = 0; itr < nW+nS-1; itr++) {
			HashMap<Integer,Query> newQuerySet = qLoader.getQuerySetByQID(itr*numChangedQueries, numQueries);
			
			if (newQuerySet.isEmpty()) break;
			ArrayList<Tuple> newSlideTuples = dLoader.getNewSlideTuples(itr, gcdS);
			if (newSlideTuples.isEmpty()) break;
			HashSet<Tuple> outliers = new HashSet<Tuple>();
			
			long startTime = Measure.getCPUTime();
			outliers = MDUAL.findOutlier(newSlideTuples, newQuerySet, itr);

			long endTime = Measure.getCPUTime();
			long memory = Measure.getMemory();
			
			if(itr>=nS-1) {
				//System.out.println("At window " +(itr-nS+1)+", itr " +itr+"\t"+"# queries: "+newQuerySet.size()+"\t"+"# outliers: "+outliers.size());
				totalOutliers += outliers.size();
				for(Tuple t:outliers) totalOutQueires += t.outlierQueryIDs.size();
				numWin++;
				allTimeSum += (endTime-startTime)/1000000d; //CPU Time (ms)
				allMemSum += memory;
			}else {
				//System.out.println("Before first window(slide "+itr+")");
			}
		}

		System.out.println(String.format("%-10s %10s %10.1f %10.2f %10.1f %10.1f %10d %10d", dataset, queryset, changedQRatio, allTimeSum/numWin, allMemSum/numWin, memThread.maxMemory, totalOutliers/numWin,  totalOutQueires/numWin));
		memThread.stop();
	}
}
