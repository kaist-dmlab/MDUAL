package simulator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import loader.Query;

public class QueryGenerator {
	private static BufferedWriter br; 
	public static int numQueries;
	public static int defaultW;
	public static int gcdS;
	public static int defaultK;
	public static double defaultR;
	public static int nW;
	public static int nItr;
	public static int variationTimes;
	public static String dataset;
	
	public static void main(String[] args) throws IOException {
		defaultR = 0.5;
		numQueries = 100;
		defaultW = 10000;
		gcdS = 500;
		defaultK = 50;
		nW = 10;
		nItr = defaultW/gcdS+nW;
		variationTimes = 5;
				
		String queryset = generate(numQueries, nW, new String[] {"R","K","S","W"});
		String filePath = "querysets/"+queryset+".csv";
		
		BufferedReader brReader = new BufferedReader(new FileReader(filePath));
		String line = brReader.readLine();
		while(line != null) {
			System.out.println(line);
			line = brReader.readLine();
		}

	}

	public QueryGenerator(String dataset, int defaultW, int gcdS, int defaultK, int variationTimes) throws IOException {		
		QueryGenerator.dataset = dataset;
		QueryGenerator.defaultW = defaultW;
		QueryGenerator.gcdS = gcdS;
		QueryGenerator.defaultK = defaultK;
		QueryGenerator.variationTimes = variationTimes;
		
		switch(dataset) {
			case "STK":
				QueryGenerator.defaultR = 0.5;
				break;				
			case "TAO":
				QueryGenerator.defaultR = 1.5;
				break;
			case "HPC":
				QueryGenerator.defaultR = 10;
				break;
			case "GAS":
				QueryGenerator.defaultR = 1.5; 
				break;
			case "EM":
				QueryGenerator.defaultR = 115; 
				break;
			case "FC":
				QueryGenerator.defaultR = 525; 
				break;
		}
	}
	
	public static String generate(int numQueries, int nW, String[] varyingParams) throws IOException {
		String queryset = QueryGenerator.dataset+"_Q"+numQueries;
		String filePath = "querysets/"+queryset+".csv";
		
		File file = new File(filePath);
		if(file.exists()) file.delete();
		
		QueryGenerator.br = new BufferedWriter(new FileWriter(filePath));
		QueryGenerator.nW = nW;
		QueryGenerator.nItr = defaultW*variationTimes/gcdS+nW;;
		double R = defaultR;
		int K = defaultK;
		int S = gcdS;
		int W = defaultW;
				
		String defaultQuery = "0,0,"+nItr+","+defaultR+","+defaultK+","+defaultW+","+gcdS;
		br.write(defaultQuery);
		br.newLine();
		
		for(int i = 1 ; i < numQueries*(nItr); i ++) {
			int startTime = 0;
			int endTime = nItr;
			//int startTime = (stationarity? 0 : (int) (maxW/gcdS+Math.random()*nW/2)-1);
			//int endTime = (stationarity? nItr : startTime + (int) (maxW/gcdS+Math.random()*nW/2)+1);
			for(String param: varyingParams) {
				switch(param) {
					case "R":
						R = Math.round((1+Math.random()*(variationTimes-1))*defaultR*100)/100d;
						break;
					case "K":
						K = defaultK*(int)(1+Math.random()*variationTimes);	
						break;
					case "S":
						S = gcdS*(int) (1+Math.random()*variationTimes);
						break;
					case "W":
						W = S+S*(int) (Math.random()*((defaultW*variationTimes)/S));
						break;
				}
			}
			String query = i+","+startTime+","+endTime+","+R+","+K+","+W+","+S;
			br.write(query);
			br.newLine();
		}
		br.close();
		return queryset;
	}

	public Query generateOne(int qID, String[] varyingParams) {
		double R = defaultR;
		int K = defaultK;
		int S = gcdS;
		int W = defaultW;
		for(String param: varyingParams) {
			switch(param) {
				case "R":
					R = Math.round((1+Math.random())*defaultR*100)/100d;
					break;
				case "K":
					K = (int)(1+Math.random()*defaultK*2);	
					break;
				case "S":
					S = gcdS*(int) (1+Math.random()*4);
					break;
				case "W":
					W = S+S*(int) (Math.random()*(defaultW/S));
					break;
			}
		}
		Query q = new Query(qID, R, K, W, S);
		return q;		
	}
}
