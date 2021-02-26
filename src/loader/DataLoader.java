package loader;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class DataLoader {
	private double[] minValues;
	private double[] maxValues;
	private BufferedReader br; 
	String filePath;
	private ArrayList<Integer> priorityList;
	public int dim;
	public int subDim;
	
	public DataLoader(String dataset) throws IOException {
		filePath = "datasets/"+dataset+".csv";
		br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();
		String[] rawValues = line.split(",");
		dim = rawValues.length;
		subDim = (dim > 15 ? 3 : rawValues.length); //default sub-dimensionality of high-dim(>15) data set is 3 (i.e., cells are created by the three dimensionalities).
		minValues = new double[dim];
		maxValues = new double[dim];
		priorityList = new ArrayList<Integer>();
		
		for(int i = 0; i < dim; i++) {
			minValues[i] = Double.MAX_VALUE;
			maxValues[i] = Double.MIN_VALUE;
			priorityList.add(i);
		}
		while(line!=null) {
			rawValues = line.split(",");
			for (int i = 0; i < dim; i++) {
				double value = Double.parseDouble(rawValues[i]);
				if(minValues[i]>value) minValues[i] = value;
				if(maxValues[i]<value) maxValues[i] = value;
			}
			line = br.readLine();
		}
	}
	
	public ArrayList<Tuple> getNewSlideTuples(int itr, int S) throws IOException {
		ArrayList<Tuple> newSlide = new ArrayList<Tuple>();
		br = new BufferedReader(new FileReader(filePath));
		String line = br.readLine();
		int tid = 0;
		
		while(line!=null) {
			if(tid>=itr*S) {
				String[] rawValues = line.split(",");
				double[] value = new double[dim];
				for(int i = 0; i<dim; i++) value[i] = Double.parseDouble(rawValues[priorityList.get(i)]);

				Tuple tuple = new Tuple(tid, itr, value);
				newSlide.add(tuple);
			}
			tid++;
			if(tid==(itr+1)*S) break;
			line = br.readLine();
		}
		return newSlide;
	}
	
	public double[] getMinValues() {
		return minValues;
	}
	
	public double[] getMaxValues() {
		return maxValues;
	}
	
}
