package simulation;

import java.util.Comparator;
import java.util.Vector;
import static simulation.Config.FRAGMENTS_PER_TILE;

public class CachedTile extends Tile {

	
	//public int cachedFragments=0; //number of fragments it has cached
	public double probability;
	
	public int getCachedFragmentsNum(){
		int count = 0;
		for(int i=0; i<FRAGMENTS_PER_TILE; i++){
			if (data[i]!=null){
				count++;
			}
			else {
				break;
			}
		}
		return count;
	}

	public CachedTile(Point point,String[] data) {
		super(point);
		// TODO Auto-generated constructor stub
		this.data = data;
	}
	
	public void addFragment(Tile tile,int fragmNumber){
		
		if (!this.containsFragment(fragmNumber)){
			this.data[fragmNumber - 1] = tile.data[fragmNumber - 1];
			//this.cachedFragments++;
		}
	}
	
	
	public void removeFragment(int fragmNumber){
		if (this.containsFragment(fragmNumber)){
			this.data[fragmNumber - 1] = null;
			//this.cachedFragments--;
		}
	}
	
	
	public boolean containsFragment(int fragmNumber){
		return this.data[fragmNumber - 1] == null;
	}
	
	public boolean isFull(){ 
		return getCachedFragmentsNum() == FRAGMENTS_PER_TILE;
	}
	
	public String fragmentsToString(){
		String str ="[";
		for (int i=0; i<FRAGMENTS_PER_TILE; i++){
			if (this.data[i]!=null){
				str+=(i+1)+",";
			}
			else {
				str+="null,";
			}
		}
		str+="]";
		return str;
	}
	
	
	
	public static Vector<Integer> getMissingFragmentIdsTillLOD(int oldLOD,int newLOD){
		Vector<Integer>  fragmentIds = new Vector<Integer>();
		if (oldLOD<newLOD && oldLOD <= FRAGMENTS_PER_TILE && newLOD <= FRAGMENTS_PER_TILE){
			for (int fragmNum=oldLOD+1; fragmNum<=newLOD; fragmNum++){
				fragmentIds.add(fragmNum);
			}
		}
		return fragmentIds;
	}
	
	public static Vector<Integer> getAllFragmentIds(){
		return getMissingFragmentIdsTillLOD(0,FRAGMENTS_PER_TILE);
	}
	
	public static Vector<Integer> getMissingFragmentIdsTillFull(int oldLOD){
		return getMissingFragmentIdsTillLOD(oldLOD, FRAGMENTS_PER_TILE);
	}
	
	public static Vector<Integer> getFragmentsToBeRemoved(int oldLOD, int newLOD){
		Vector<Integer>  fragmentIds = new Vector<Integer>();
		if (newLOD<oldLOD && newLOD>=0){
			for (int fragmNum=newLOD+1; fragmNum<=oldLOD; fragmNum++){
				fragmentIds.add(fragmNum);
			}
		}
		return fragmentIds;
	}
	
	
	public String toString(){
		String str;
		str = "CachedTile("+this.point.y+","+this.point.x+",lod="+this.getCachedFragmentsNum()+",Probability="+this.probability+")";
		return str;
	}
	
	
	
	public static Comparator<CachedTile> probabilityComparator = new Comparator<CachedTile>(){
		@Override
		public int compare(CachedTile t1, CachedTile t2) {
		    if (t1.probability > t2.probability ){
		    	return -1;
		    }
		    else if (t1.probability < t2.probability  ){
		    	return 1;
		    }
		    else if (t1.probability == t2.probability &&
		    		 t1.id < t2.id ) {
		    	return -1;
		    }
		    else if (t1.probability == t2.probability &&
		    		 t1.id > t2.id ) {
		    	return 1;
		    }
		    else {
		    	return 0;
		    }
		}
	};
	
	
}
