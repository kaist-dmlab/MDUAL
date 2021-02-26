package loader;
import java.util.*;

public class Tuple{
		public int id, slideID;		
		public double[] value;
		public ArrayList<Short> subDimCellIdx, fullDimCellIdx;
		public int nn;
		public HashSet<Integer> outlierQueryIDs;
		
		/** Variables for SOP **/
		public LinkedHashMap<Tuple,Double> LSky; // "old" <points, normalized distance> are placed as "last"
		public int[] layerCount;
		public HashMap<Integer,Boolean> safeDue; // for query id, until when this tuple is safe inlier with respect to the slide id of newest slide

		/** Variables for pMCSKY **/
		public int mc;

		public Tuple(int id, int slideID, double[] value) {
			this.id = id;
			this.slideID = slideID;
			this.value = value;
			this.outlierQueryIDs = new HashSet<Integer>();
			
			// Variables for SOP and pMCSKY
			this.LSky = new LinkedHashMap<Tuple,Double>();
			this.mc=-1;
		}

		@Override
		public boolean equals(Object obj){
			Tuple _obj = (Tuple) obj;
			return _obj.id == this.id;
		}
	}
