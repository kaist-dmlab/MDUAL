package MDUAL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import loader.Tuple;

public class Cell {
	public ArrayList<Short> cellIdx;
	public HashMap<ArrayList<Short>,Cell> childCells;
	public HashSet<Tuple> tuples;
	double[] center;
	
	public Cell(ArrayList<Short> cellIdx, double[] dimLength, double[] minValues){
		this.cellIdx = cellIdx;
		this.tuples = new HashSet<Tuple>();
		this.center = new double[dimLength.length];
		for (int j = 0; j<dimLength.length; j++) this.center[j] = minValues[j] + cellIdx.get(j)*dimLength[j]+dimLength[j]/2;
	}
	
	public Cell(ArrayList<Short> cellIdx,  double[] cellCenter){
		this.cellIdx = cellIdx;
		this.tuples = new HashSet<Tuple>();
		this.center = cellCenter;
	}
	
	public Cell(ArrayList<Short> cellIdx, double[] cellCenter, Boolean subDimFlag){
		this.cellIdx = cellIdx;
		this.center = cellCenter;
		this.tuples = new HashSet<Tuple>();
		if(subDimFlag) this.childCells = new HashMap<ArrayList<Short>,Cell>();
	}
	
	public int getNumTuples() {
		return this.tuples.size();
	}
	public void addTupleSubDim(Tuple t, double[] dimLength, double[] minValues) {
		this.tuples.add(t);
		if(!this.childCells.containsKey(t.fullDimCellIdx))
			this.childCells.put(t.fullDimCellIdx, new Cell(t.fullDimCellIdx, dimLength, minValues));
		this.childCells.get(t.fullDimCellIdx).addTuple(t);
	}
	
	public void addTuple(Tuple t) {
		this.tuples.add(t);
	}
}
	