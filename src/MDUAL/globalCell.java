package MDUAL;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import loader.Query;

public class globalCell {
	public ArrayList<Short> cellIdx;
	public HashMap<ArrayList<Short>, Double> neighCellMap;
	public boolean lastUpdated;
	public int card;
	
	public HashMap<Integer, Integer> cardPerSlide; //Slide ID:card
	
	ArrayList<Integer> IndirectOutlierCellQueryIDs = new ArrayList<Integer>();
	ArrayList<Query> ndQueries = new ArrayList<Query>();

	public globalCell(ArrayList<Short> cellIdx){
		this.cellIdx = cellIdx;
		this.card = 0;
		this.neighCellMap = new HashMap<ArrayList<Short>, Double>();
		this.lastUpdated = false;
		
		this.cardPerSlide = new HashMap<Integer, Integer>();
	}
	
	public int getCardTotal(int firstSlideID) {
		int total = 0;
		for(int slideID: cardPerSlide.keySet()) if(slideID >= firstSlideID) total += cardPerSlide.get(slideID);
		return total;
	}
	
	public ArrayList<ArrayList<Short>> getThredNeighCellsOut(double distThred) {
		ArrayList<ArrayList<Short>> thredNeighCells = new ArrayList<ArrayList<Short>>();
		for(ArrayList<Short> neighCellIdx: neighCellMap.keySet()) if(neighCellMap.get(neighCellIdx) < distThred) thredNeighCells.add(neighCellIdx);
		return thredNeighCells;
	}
	
	public ArrayList<ArrayList<Short>> getThredNeighCellsIn(double distThred) {
		ArrayList<ArrayList<Short>> thredNeighCells = new ArrayList<ArrayList<Short>>();
		for(ArrayList<Short> neighCellIdx: neighCellMap.keySet()) if(neighCellMap.get(neighCellIdx) <= distThred) thredNeighCells.add(neighCellIdx);
		return thredNeighCells;
	}

}
