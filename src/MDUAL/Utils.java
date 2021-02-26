package MDUAL;

import java.util.ArrayList;

import loader.Tuple;

public class Utils {
	
	public Utils() {}
	
	public static double distTuple(Tuple t1, Tuple t2) {
		double ss = 0;
		for(int i = 0; i<t1.value.length; i++) { 
			ss += Math.pow((t1.value[i] - t2.value[i]),2);
		}
		 return Math.sqrt(ss);
	}
	
	public static double distTuple(Tuple t1, Tuple t2, double threshold) {
		double ss = 0;
		double ss_thred = Math.pow(threshold, 2);
		for(int i = 0; i<t1.value.length; i++) { 
			ss += Math.pow((t1.value[i] - t2.value[i]),2);
			if(ss > ss_thred) return Double.MAX_VALUE;
		}
		 return Math.sqrt(ss);
	}
	
	public static boolean isNeighborTuple(Tuple t1, Tuple t2, double threshold) {
		double ss = 0;
		threshold *= threshold;
		for(int i = 0; i<t1.value.length; i++) { 
			ss += Math.pow((t1.value[i] - t2.value[i]),2);
			if(ss>threshold) return false;
		}
		return true;
	}

	public static boolean isNeighborTupleCell(double[] v1, double[] v2, double threshold) {
		double ss = 0;
		threshold *= threshold;
		for(int i = 0; i<v2.length; i++) { 
			ss += Math.pow((v1[i] - v2[i]),2);
			if(ss > threshold) return false;
		}
		 return true;
	}
	
	public static double getNeighborCellDist(ArrayList<Short> c1, ArrayList<Short> c2, double minR, double threshold) {
		double ss = 0;
		for(int k = 0; k<c1.size(); k++) {
			ss += Math.pow((c1.get(k) - c2.get(k)),2);
			if (ss/c1.size()*minR*minR >= threshold*threshold) return Double.MAX_VALUE;
		}
		 return Math.sqrt(ss/c1.size())*minR;
	}
	
	public static boolean isNeighborCell(ArrayList<Short> c1, ArrayList<Short> c2, double minR, double threshold) {
		double ss = 0;
		for(int k = 0; k<c1.size(); k++) {
			ss += Math.pow((c1.get(k) - c2.get(k)),2);
			if (ss/c1.size()*minR*minR >= threshold*threshold) return false;
		}
		 return true;
	}

}
