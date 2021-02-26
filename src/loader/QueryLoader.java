package loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class QueryLoader {
	private BufferedReader br; 
	private String filePath;
	public int maxW;
	public int gcdS;
	public double minR;
	
	public QueryLoader(String queryset) throws IOException {
		filePath = "querysets/"+queryset+".csv";
		br = new BufferedReader(new FileReader(filePath));
		maxW = Integer.MIN_VALUE;
		gcdS = Integer.MAX_VALUE;
		minR = Integer.MAX_VALUE;
		
		String line = br.readLine();		
		while(line!=null) {
			String[] rawValues = line.split(",");
			double R = Double.parseDouble(rawValues[3]);
			int W = Integer.parseInt(rawValues[5]);
			int S = Integer.parseInt(rawValues[6]);
			if(maxW<W) maxW = W;
			if(gcdS>S) gcdS = S;
			if(minR>R) minR = R;
			line = br.readLine();
		}
	}
	
	public HashMap<Integer,Query> getQuerySet(int curr_itr) throws IOException {
		HashMap<Integer,Query> querySet = new HashMap<Integer,Query>();
		br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();		
		
		while(line!=null) {
			String[] rawValues = line.split(",");
			int id = Integer.parseInt(rawValues[0]); 
			int s_time = Integer.parseInt(rawValues[1]);
			int e_time = Integer.parseInt(rawValues[2]);
			if(s_time <= curr_itr && curr_itr < e_time) {
				double R = Double.parseDouble(rawValues[3]);
				int K = Integer.parseInt(rawValues[4]);
				int W = Integer.parseInt(rawValues[5]);
				int S = Integer.parseInt(rawValues[6]);
				
				Query query = new Query(id, R,K,W,S);
				querySet.put(id, query);
			}
			line = br.readLine();
		}
		return querySet;
	}
	
	public HashMap<Integer,Query> getQuerySetByQID(int fromQID, int numQueries) throws IOException {
		HashMap<Integer,Query> querySet = new HashMap<Integer,Query>();
		br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();		
		
		while(line!=null) {
			String[] rawValues = line.split(",");
			int id = Integer.parseInt(rawValues[0]); 
			if(id >= fromQID && id < fromQID+numQueries) {
				double R = Double.parseDouble(rawValues[3]);
				int K = Integer.parseInt(rawValues[4]);
				int W = Integer.parseInt(rawValues[5]);
				int S = Integer.parseInt(rawValues[6]);
				
				Query query = new Query(id, R,K,W,S);
				querySet.put(id, query);
			}
			line = br.readLine();
		}
		return querySet;
	}
}
