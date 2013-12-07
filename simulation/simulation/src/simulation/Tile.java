package simulation;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


import static simulation.Config.COLORS;
import static simulation.Config.DATABASE_WIDTH;
import static simulation.Config.FRAGMENTS_PER_TILE;
import static simulation.Config.FRAGMENT_SIZE;
import static simulation.Config.TILE_HEIGHT;
import static simulation.Config.TILE_WIDTH;
import static simulation.Config.debug;

public class Tile {

	public Map<Integer,Fragment> fragments = new HashMap<Integer, Fragment>();

	
	
	public int id;
	public int y;
	public int x;
	public Vector<String> data = new Vector<String>(FRAGMENTS_PER_TILE);

	
	
	
	public int lod;
	public double likelihood = -1.0d;
	public Point point; //index
	public boolean cached = false;
	public boolean beingLoaded = true;
	
	public  boolean equals(Object o){
		if (this.point.equals(((Tile)o).point)){
			return true;
		}
		else {
			return false;
		}
	}
	
	
	public static Comparator<Tile> likelihoodComparator = new Comparator<Tile>(){
		@Override
		public int compare(Tile t1, Tile t2) {
			 /*if  (t1.beingLoaded && !t2.beingLoaded){
				 return 1;
			 }
			 else if (t2.beingLoaded && !t1.beingLoaded){
				 return -1;
			 }
			 else if (t1.beingLoaded && t2.beingLoaded){
				 return 0;
			 }
			 else*/ if  (t1.likelihood > t2.likelihood){
		    	 return 1;
		     }
		     else if (t1.likelihood<t2.likelihood){
		    	 return -1;
		     }
		     else {
		    	 return 0;
		     }
		}
	};
	
	
	public Tile(Point point){
		this.point = point;
		this.id = this.point.hashCode();
	}
	
	public Tile(Point point,Vector<String> data){
		this.point = point;
		this.id = this.point.hashCode();
		this.data = data;
	}
	
	public int getFragmentNumber(){
		return fragments.size();
	}
	
	
	public void setCached(boolean cached){
		this.cached = cached;
	}
	
	public boolean isCached(){
		return this.cached;
	}
	
	
	
	

	
	/*public void addFragment(Fragment fragm){
		fragments.put(fragm.num, fragm);	
		this.lod = this.getFragmentNumber();
	}*/
	
	public void removeFragment(int fragmNumber){
		fragments.remove(fragmNumber);
		this.lod = this.getFragmentNumber();
	}
	
	public Fragment getFragment(int fragmNumber){
		return fragments.get(fragmNumber);
	}
	
	
	public boolean containsFragment(int fragmNumber){
		return fragments.containsKey(fragmNumber);
	}
	
	
	public boolean isFull(){ //contains as many fragments as possible
		return fragments.size() == FRAGMENTS_PER_TILE;
		
	}
	
	public String toString(){
		String str;
		if (this.isCached()){
			str = "Tile("+this.point.y+","+this.point.x+",LOD="+this.lod+",Likelihood="+this.likelihood+")";
		}
		else {
			 str = "Tile("+this.point.y+","+this.point.x+")";
		}
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
	
}
