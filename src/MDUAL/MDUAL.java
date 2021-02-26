package MDUAL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;

import loader.Query;
import loader.Tuple;

public class MDUAL {
	public double minR, maxR, minR_old, maxR_old;
	public int gcdS;
	public int dim, subDim;
	public boolean subDimFlag;
	public int nS, nW;
	public double[] minValues;
	public double[] dimLength, subDimLength;
	
	public HashMap<ArrayList<Short>,Cell> slideIn, slideOut;
	public HashMap<ArrayList<Short>,Integer> slideDeltaCnt;
	public HashMap<ArrayList<Short>,globalCell> cardGrid, fullDimCardGrid;
	public LinkedList<HashMap<ArrayList<Short>,Cell>> slides; 
	public LinkedList<HashMap<ArrayList<Short>,Integer>> fullDimCellSlidesCnt; 
	public HashSet<Tuple> outliers;

	public HashMap<Integer,Query> querySet;
	
	public boolean maxRChanged, minRChanged;
	
	public MDUAL(int dim, int subDim, int nS, int gcdS, double[] minValues) {
		this.dim = dim;
		this.subDim = subDim;
		this.subDimFlag = dim != subDim;
		this.minR = Double.MAX_VALUE;
		this.maxR = Double.MIN_VALUE;
		this.gcdS = gcdS;
		this.nS = nS;
		this.minValues = minValues;
		
		this.cardGrid = new HashMap<ArrayList<Short>,globalCell>();
		this.fullDimCardGrid = new HashMap<ArrayList<Short>,globalCell>();
		this.fullDimCellSlidesCnt = new LinkedList<HashMap<ArrayList<Short>,Integer>>(); 
		this.slides = new LinkedList<HashMap<ArrayList<Short>,Cell>>();
		this.slideOut = new HashMap<ArrayList<Short>,Cell>();
		this.outliers = new HashSet<Tuple>();
	}

	public HashSet<Tuple> findOutlier(ArrayList<Tuple> newSlideTuples, HashMap<Integer,Query> newQuerySet, int itr) {
		this.querySet = newQuerySet;
		
		if(itr==0) {
			this.updateBasisParams(itr); 
			this.initCellSize();
		}
		this.clearPreviousOutliers();
		this.updateWindow(newSlideTuples,itr);
		this.updateBasisParams(itr); 
		this.findOutlierMain(itr);

		return this.outliers;
	}
	
	public void initCellSize() {
		/* Cell size calculation for all dim*/
		dimLength = new double[dim];
		for(int i = 0;i<dim;i++) dimLength[i] = Math.sqrt(minR*minR/dim);
				
		/* Cell size calculation for sub dim*/
		if (subDimFlag) {
			subDimLength = new double[subDim];
			for(int i = 0;i<subDim;i++) subDimLength[i] = Math.sqrt(minR*minR/subDim);
		} 
	}
	
	public void clearPreviousOutliers() {
		//Clear previous outliers and outlierQueryID;
		Iterator<Tuple> it = outliers.iterator();
		while (it.hasNext()) {
			Tuple outlier = it.next();
			outlier.outlierQueryIDs.clear();
			it.remove();
		}
	}
	
	public void updateBasisParams(int itr) {
		maxRChanged = false;
		minRChanged = false;
		minR_old = minR;
		maxR_old = maxR;
		minR = Double.MAX_VALUE;
		maxR = Double.MIN_VALUE;
		for(Query q:querySet.values()) {
			if(maxR<q.R) maxR = q.R;
			if(minR>q.R) minR = q.R;
		}
		if(itr>0 && minR != minR_old) minRChanged = true;
		if(itr>0 && maxR != maxR_old) maxRChanged = true;
	}

	public void updateWindow(ArrayList<Tuple> slideTuples, int itr) {
		/*Indexing slideIn*/
		slideIn = new HashMap<ArrayList<Short>,Cell>();
		HashMap<ArrayList<Short>,Integer> fullDimCellSlideInCnt = new HashMap<ArrayList<Short>,Integer>();

		for(Tuple t:slideTuples) {
			ArrayList<Short> fullDimCellIdx = new ArrayList<Short>();
			ArrayList<Short> subDimCellIdx = new ArrayList<Short>();
			
			for (int j = 0; j<dim; j++) { 
				short dimIdx = (short) ((t.value[j]-minValues[j])/dimLength[j]);
				fullDimCellIdx.add(dimIdx);
			}
			if (subDimFlag) {
				for (int j = 0; j<subDim; j++) {
					short dimIdx = (short) ((t.value[j]-minValues[j])/subDimLength[j]);
					subDimCellIdx.add(dimIdx);
				}
			}else {
				subDimCellIdx = fullDimCellIdx;
			}

			t.fullDimCellIdx = fullDimCellIdx;
			t.subDimCellIdx = subDimCellIdx;
			
			if(!slideIn.containsKey(subDimCellIdx)) {
				double[] cellCenter = new double[subDim];
				if (subDimFlag) {
					for (int j = 0; j<subDim; j++) cellCenter[j] = minValues[j] + subDimCellIdx.get(j)*subDimLength[j]+subDimLength[j]/2;
				}else {
					for (int j = 0; j<dim; j++) cellCenter[j] = minValues[j] + fullDimCellIdx.get(j)*dimLength[j]+dimLength[j]/2;
				}
				slideIn.put(subDimCellIdx, new Cell(subDimCellIdx, cellCenter, subDimFlag));
			}
						
			if (subDimFlag) {
				slideIn.get(subDimCellIdx).addTupleSubDim(t, dimLength, minValues);
				if(!fullDimCellSlideInCnt.containsKey(fullDimCellIdx)) fullDimCellSlideInCnt.put(fullDimCellIdx, 0);
				fullDimCellSlideInCnt.put(fullDimCellIdx, fullDimCellSlideInCnt.get(fullDimCellIdx)+1);
			}else {
				slideIn.get(subDimCellIdx).addTuple(t);
			}
		}
		slides.add(slideIn);
		if (subDimFlag) fullDimCellSlidesCnt.add(fullDimCellSlideInCnt);
		

		slideDeltaCnt = new HashMap<ArrayList<Short>, Integer>();
		HashSet<ArrayList<Short>> newCellIdices = new HashSet<ArrayList<Short>>();
		
		/* Update slideIn */
		for(ArrayList<Short> key:slideIn.keySet()) {
			int card = slideIn.get(key).getNumTuples();
			if(!cardGrid.containsKey(key)) {
				cardGrid.put(key, new globalCell(key));
				newCellIdices.add(key);				
			}
			cardGrid.get(key).card += card;
			cardGrid.get(key).cardPerSlide.put(itr, card);
			
			slideDeltaCnt.put(key, card);
		}
		
		/* Update full Dim cell window count - slideIn */
		if(subDimFlag) {
			for(ArrayList<Short> key:fullDimCellSlideInCnt.keySet()) {
				int card = fullDimCellSlideInCnt.get(key);
				if(!fullDimCardGrid.containsKey(key)) {
					fullDimCardGrid.put(key, new globalCell(key));
				}
				fullDimCardGrid.get(key).card += card;
				fullDimCardGrid.get(key).cardPerSlide.put(itr, card);	
			}
		}
		
		getNeighCellMap(newCellIdices);
		
		/* Update slideOut */
		if(itr>nS-1) {
			int slideOutID = itr-nS;
			slideOut = slides.poll();
			for(ArrayList<Short> key:slideOut.keySet()) {
				int card = slideOut.get(key).getNumTuples();
				cardGrid.get(key).card -= card;
				cardGrid.get(key).cardPerSlide.remove(slideOutID);
				
				if(cardGrid.get(key).card < 1) {
					for(ArrayList<Short> neighCellIdx: cardGrid.get(key).neighCellMap.keySet()) {
						if(key.equals(neighCellIdx)) continue; 
						cardGrid.get(neighCellIdx).neighCellMap.remove(key);
					}
					cardGrid.remove(key);
				}
				
				if(slideDeltaCnt.containsKey(key)) {
					slideDeltaCnt.put(key, slideDeltaCnt.get(key)-card);
				}else {
					slideDeltaCnt.put(key, card*-1);
				}
			}
			
			/* Update full Dim cell window count - slideOut*/
			if(subDimFlag) {
				HashMap<ArrayList<Short>,Integer> fullDimCellSlideOutCnt = fullDimCellSlidesCnt.poll();
				for(ArrayList<Short> key:fullDimCellSlideOutCnt.keySet()) {					
					int card = fullDimCellSlideOutCnt.get(key);
					fullDimCardGrid.get(key).card -= card;
					fullDimCardGrid.get(key).cardPerSlide.remove(slideOutID);
					
					if(fullDimCardGrid.get(key).card < 1) fullDimCardGrid.remove(key);
				}
			}
		}	

	}	
	
	public void findOutlierMain(int itr) {
		if(minRChanged) {
			initCellSize();
			reIndexCardGrid(itr);
		}
		if(maxRChanged || minRChanged) this.reComputeNeighCellMap();
		
		ArrayList<Query> validQueryIDs = new ArrayList<Query>();
		for(Query q: querySet.values()) {
			if((itr+1) % (q.S/gcdS) == 0) validQueryIDs.add(q); //check if slide condition is met
		}

		//inlier-first
		Collections.sort(validQueryIDs, new Comparator<Query>(){ //Sort by order of smaller W -> smaller R -> larger K 
			@Override
			public int compare(Query q1, Query q2) {
				if(q1.R>q2.R) {
					return 1;
				}else if(q1.R==q2.R){
					if(q1.W>q2.W) return 1;
					else if(q1.W==q2.W){
						if(q1.K<=q2.K) return 1;
						else return -1;
					}else return -1;
				}else return -1;
			}
		});

		//outlier-first
//		Collections.sort(validQueryIDs, new Comparator<Query>(){ //Sort by order of larger W -> larger R -> smaller K 
//			@Override
//			public int compare(Query q1, Query q2) {
//				if(q1.R<q2.R) {
//					return 1;
//				}else if(q1.R==q2.R){
//					if(q1.W<q2.W) return 1;
//					else if(q1.W==q2.W){
//						if(q1.K>=q2.K) return 1;
//						else return -1;
//					}else return -1;
//				}else return -1;
//			}
//		});
		
				
		/* Group-wise coarse processing */ 
		for (ArrayList<Short> cellIdx: cardGrid.keySet()) {
			globalCell gCell = cardGrid.get(cellIdx); 
			ArrayList<Query> ndQueryCands = new ArrayList<Query>(validQueryIDs);
			
			ArrayList<Integer> outlierCellQueryIDs = new ArrayList<Integer>();
			ArrayList<Integer> inlierCellQueryIDs = new ArrayList<Integer>();
			ArrayList<Query> ndQueries = new ArrayList<Query>();
			//Verify outlier/intlier cell query IDs
			while(!ndQueryCands.isEmpty()) {
				Query q = ndQueryCands.iterator().next();
				ndQueryCands.remove(q);
				if(gCell.IndirectOutlierCellQueryIDs.contains(q.id)) {
					outlierCellQueryIDs.add(q.id);
					continue;
				}
				int firstSlideID = itr - q.W/gcdS + 1;
				int cardTotal = gCell.getCardTotal(firstSlideID);
				
				if(!subDimFlag && cardTotal > q.K){ //inlier cell for q
					inlierCellQueryIDs.add(q.id);
					Iterator<Query> ndQueryItr = ndQueryCands.iterator();
					while(ndQueryItr.hasNext()) { //propagation
						Query q2 = ndQueryItr.next();
						if(q2.W >= q.W && q2.R >= q.R && q2.K <= q.K) {
							ndQueryItr.remove();
						}
					}
				}else{ 
					int thredNeighCellCardTotal = 0;
					for(ArrayList<Short> neighCellIdx: gCell.getThredNeighCellsIn(q.R-minR)) thredNeighCellCardTotal += cardGrid.get(neighCellIdx).getCardTotal(firstSlideID);					
					if(!subDimFlag && thredNeighCellCardTotal + cardTotal > q.K){ //inlier cell for q
						inlierCellQueryIDs.add(q.id); 
						Iterator<Query> ndQueryItr = ndQueryCands.iterator();
						while(ndQueryItr.hasNext()) { //propagation
							Query q2 = ndQueryItr.next();
							if(q2.W >= q.W && q2.R >= q.R && q2.K <= q.K) {
								ndQueryItr.remove();
							}
						}
					}else {
						//Get total cards of neighbor cells. 
						//if not sub dim, add cell card since it is not contained in neighbor cells
						int neighCellCardTotal = (subDimFlag? 0: cardTotal);
						for(ArrayList<Short> neighCellIdx: gCell.getThredNeighCellsOut(q.R+minR)) neighCellCardTotal += cardGrid.get(neighCellIdx).getCardTotal(firstSlideID);

						if(neighCellCardTotal <= q.K) { //outlier cell for q
							outlierCellQueryIDs.add(q.id);
							Iterator<Query> ndQueryItr = ndQueryCands.iterator();
							while(ndQueryItr.hasNext()) { //propagation
								Query q2 = ndQueryItr.next();
								if(q2.W <= q.W && q2.R <= q.R && q2.K >= q.K) {
									ndQueryItr.remove();
									outlierCellQueryIDs.add(q2.id);
								}
							}
						}else{
							ndQueries.add(q);
						}
											
						
					}
				}
			}
			
			//Add outlier tuples by outlierCellQueryIDs
			for (int qid: outlierCellQueryIDs) {
				int firstSlideID = itr - querySet.get(qid).W/gcdS + 1;
				int slideID = itr - slides.size();
				for(HashMap<ArrayList<Short>, Cell> slide: slides) {
					slideID++;
					if(slideID < firstSlideID || !slide.containsKey(cellIdx)) continue; //check if slide is inside the query window condition OR contains the cell
					for(Tuple t: slide.get(cellIdx).tuples) {
						t.outlierQueryIDs.add(qid);
						outliers.add(t);
					}
				}
			}
			gCell.ndQueries = ndQueries;
		}
				
		/* Group-wise fine processing */			
		// for each cell 
		cellLoop:
		for (ArrayList<Short> cellIdx: cardGrid.keySet()) {
			globalCell gCell = cardGrid.get(cellIdx); 
			gCell.IndirectOutlierCellQueryIDs.clear();
			
			int ndQueriesMinW = Integer.MAX_VALUE;
			int ndQueriesMaxW = Integer.MIN_VALUE;
			int ndQueriesMaxK = Integer.MIN_VALUE;
			double ndQueriesMaxR = Double.MIN_VALUE;
			if(gCell.ndQueries.isEmpty()) {
				continue cellLoop;
			}else {
				for (Query q: gCell.ndQueries) {
					if(q.K > ndQueriesMaxK) ndQueriesMaxK = q.K;
					if(q.R > ndQueriesMaxR) ndQueriesMaxR = q.R;
					if(q.W < ndQueriesMinW) ndQueriesMinW = q.W;
					if(q.W > ndQueriesMaxW) ndQueriesMaxW = q.W;
				}
			}

			//get candidate outlier tuples
			HashSet<Tuple> candOutlierTuples = new HashSet<Tuple>();
			int minWfirstSlideID = itr - ndQueriesMinW/gcdS + 1;
			int maxWfirstSlideID = itr - ndQueriesMaxW/gcdS + 1;
			int slideID = itr - slides.size();
			for(HashMap<ArrayList<Short>, Cell> slide: slides) {
				slideID++;
				if(slideID < maxWfirstSlideID || !slide.containsKey(cellIdx)) continue;
				
				if(subDimFlag) {
					for (Tuple t:slide.get(cellIdx).tuples) {
						int numNeighInCellMinW = fullDimCardGrid.get(t.fullDimCellIdx).getCardTotal(minWfirstSlideID)-1;
						if(numNeighInCellMinW<ndQueriesMaxK) candOutlierTuples.add(t);
					}
				}else if(gCell.getCardTotal(minWfirstSlideID)-1<ndQueriesMaxK){
					 candOutlierTuples.addAll(slide.get(cellIdx).tuples);
				}
			}
						
			for (Tuple tCand:candOutlierTuples) {
				ArrayList<Query> ndQueryTuple = new ArrayList<>(gCell.ndQueries);
				double ndQueriesMaxRTuple = ndQueriesMaxR;
				
				queryLoop:
				while(!ndQueryTuple.isEmpty()) {
					
					Query q = ndQueryTuple.iterator().next();
					ndQueryTuple.remove(q);
					
					int firstSlideID = itr - q.W/gcdS + 1;
					if(tCand.slideID < firstSlideID) continue queryLoop;
										
					// if subDimFlag true, get fullDimCardPerSlide
					int nn = (subDimFlag ? fullDimCardGrid.get(tCand.fullDimCellIdx).getCardTotal(firstSlideID)-1 : gCell.getCardTotal(firstSlideID)-1);
					slideID = itr - slides.size();
					for(HashMap<ArrayList<Short>, Cell> slide: slides) {
						slideID++;
						if(slideID < firstSlideID) continue;
						for(ArrayList<Short> cellID: slide.keySet()) {
							if(gCell.neighCellMap.containsKey(cellID)
								&& gCell.neighCellMap.get(cellID) < minR + ndQueriesMaxRTuple //Cell-cell adaptive thresholding 
								&& Utils.isNeighborTupleCell(tCand.value, slide.get(cellID).center, 0.5*minR + q.R)){ //Point-cell adaptive thresholding 
									for(Tuple tOther:slide.get(cellID).tuples) {
										if(subDimFlag && tCand.fullDimCellIdx.equals(tOther.fullDimCellIdx)) continue;
										if(Utils.distTuple(tCand, tOther, q.R) <= q.R) {
											nn++;
											if(nn>=q.K) {
												Iterator<Query> ndQueryTupleItr = ndQueryTuple.iterator();
												ndQueriesMaxRTuple = Double.MIN_VALUE;
												while(ndQueryTupleItr.hasNext()) {
													Query q2 = ndQueryTupleItr.next();
													if(q2.W >= q.W && q2.R >= q.R && q2.K <= q.K ) {
														ndQueryTupleItr.remove();
													}else if(q2.R > ndQueriesMaxRTuple) {
														ndQueriesMaxRTuple = q2.R; //set reduced maximal range query
													}
												}
												continue queryLoop;
											}
										}
									}
								}
							}
						}
					

					if(nn<q.K) {
						tCand.outlierQueryIDs.add(q.id);
						outliers.add(tCand);
						
						Iterator<Query> ndQueryItr = ndQueryTuple.iterator();
						ndQueriesMaxRTuple = Double.MIN_VALUE;
						while(ndQueryItr.hasNext()) {
							Query q2 = ndQueryItr.next();
							if(q2.W <= q.W && q2.R <= q.R && q2.K >= q.K) {
								ndQueryItr.remove();
								tCand.outlierQueryIDs.add(q2.id);
							}else if(q2.R > ndQueriesMaxRTuple) {
								ndQueriesMaxRTuple = q2.R; //set reduced maximal range query 
							}
						}
						
					}
				}
			}					

		}				
	}
	
	public void getNeighCellMap(HashSet<ArrayList<Short>> newCellIdices) {
		for(ArrayList<Short> newCellIdx:newCellIdices) {
			globalCell newCell = cardGrid.get(newCellIdx); 
			
			for (ArrayList<Short> candCellIdx:cardGrid.keySet()) {
				globalCell candCell = cardGrid.get(candCellIdx);
				double dist = Utils.getNeighborCellDist(newCellIdx, candCellIdx, minR, minR+maxR);
				if(dist < minR+maxR && (subDimFlag || !newCellIdx.equals(candCellIdx))) {
					newCell.neighCellMap.put(candCellIdx, dist);					
					if(!newCellIdices.contains(candCellIdx)) {
						candCell.neighCellMap.put(newCellIdx,dist);
					}
				}
			}
		}
	}
	
	public void reComputeNeighCellMap() {
		for(ArrayList<Short> cellIdx:cardGrid.keySet()) {
			globalCell cell = cardGrid.get(cellIdx);
			cell.neighCellMap.clear();
			
			for (ArrayList<Short> otherCellIdx:cardGrid.keySet()) {
				if(cell.neighCellMap.containsKey(otherCellIdx)) continue;
				
				double dist = Utils.getNeighborCellDist(cellIdx, otherCellIdx, minR, minR+maxR);
				if(dist < minR+maxR && (subDimFlag || !cellIdx.equals(otherCellIdx))) {
					cell.neighCellMap.put(otherCellIdx, dist);
					cardGrid.get(otherCellIdx).neighCellMap.put(cellIdx, dist);
				}
				
			}
		}
	}
	
	public void reIndexCardGrid(int itr) {
		LinkedList<HashMap<ArrayList<Short>,Cell>> slidesNew = new LinkedList<HashMap<ArrayList<Short>,Cell>>();
		fullDimCellSlidesCnt.clear(); 
		cardGrid.clear();
		fullDimCardGrid.clear();
		
		int slideID = itr - slides.size();
		for(HashMap<ArrayList<Short>,Cell> slide:slides) {
			slideID++;
			HashMap<ArrayList<Short>,Integer> fullDimCellSlideInCnt = new HashMap<ArrayList<Short>,Integer>();
			HashMap<ArrayList<Short>,Cell> slideNew = new HashMap<ArrayList<Short>,Cell>();
			
			/* Indexing slide */
			for(Cell c: slide.values()) {
				for(Tuple t:c.tuples) {
					ArrayList<Short> fullDimCellIdx = new ArrayList<Short>();
					ArrayList<Short> subDimCellIdx = new ArrayList<Short>();
					
					for (int j = 0; j<dim; j++) { 
						short dimIdx = (short) ((t.value[j]-minValues[j])/dimLength[j]);
						fullDimCellIdx.add(dimIdx);
					}
				
					if (subDimFlag) {
						for (int j = 0; j<subDim; j++) {
							short dimIdx = (short) ((t.value[j]-minValues[j])/subDimLength[j]);
							subDimCellIdx.add(dimIdx);
						}
					}else {
						subDimCellIdx = fullDimCellIdx;
					}

					t.fullDimCellIdx = fullDimCellIdx;
					t.subDimCellIdx = subDimCellIdx;
					
					if(!slideNew.containsKey(subDimCellIdx)) {
						double[] cellCenter = new double[subDim];
						if (subDimFlag) {
							for (int j = 0; j<subDim; j++) cellCenter[j] = minValues[j] + subDimCellIdx.get(j)*subDimLength[j]+subDimLength[j]/2;
						}else {
							for (int j = 0; j<dim; j++) cellCenter[j] = minValues[j] + fullDimCellIdx.get(j)*dimLength[j]+dimLength[j]/2;
						}
						slideNew.put(subDimCellIdx, new Cell(subDimCellIdx, cellCenter, subDimFlag));
					}
								
					if (subDimFlag) {
						slideNew.get(subDimCellIdx).addTupleSubDim(t, dimLength, minValues);
						if(!fullDimCellSlideInCnt.containsKey(fullDimCellIdx)) fullDimCellSlideInCnt.put(fullDimCellIdx, 0);
						fullDimCellSlideInCnt.put(fullDimCellIdx, fullDimCellSlideInCnt.get(fullDimCellIdx)+1);
					}else {
						slideNew.get(subDimCellIdx).addTuple(t);
					}
				}				
			}
			
			/* Update slideNew */
			for(ArrayList<Short> key:slideNew.keySet()) {
				int card = slideNew.get(key).getNumTuples();
				if(!cardGrid.containsKey(key)) cardGrid.put(key, new globalCell(key));
				cardGrid.get(key).card += card;
				cardGrid.get(key).cardPerSlide.put(slideID, card);
			}
			
			/* Update full Dim cell window count - slideIn */
			if(subDimFlag) {
				for(ArrayList<Short> key:fullDimCellSlideInCnt.keySet()) {
					int card = fullDimCellSlideInCnt.get(key);
					if(!fullDimCardGrid.containsKey(key)) {
						fullDimCardGrid.put(key, new globalCell(key));
					}
					fullDimCardGrid.get(key).card += card;
					fullDimCardGrid.get(key).cardPerSlide.put(slideID, card);	
				}
			}
			
			slidesNew.add(slideNew);
			if (subDimFlag) fullDimCellSlidesCnt.add(fullDimCellSlideInCnt);
		}
		slides = slidesNew;
		
	}

}