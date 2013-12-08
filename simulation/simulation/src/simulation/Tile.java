package simulation;

import java.util.Comparator;




import static simulation.Config.FRAGMENTS_PER_TILE;


public class Tile {

//	public Map<Integer,Fragment> fragments = new HashMap<Integer, Fragment>();

	
	
	public int id;
	public int y;
	public int x;
	String[] data = new String[FRAGMENTS_PER_TILE];

	
	
	
	//public int lod;
	
	public Point point; //index
	
	
	public  boolean equals(Object o){
		if (this.point.equals(((Tile)o).point)){
			return true;
		}
		else {
			return false;
		}
	}
	
	
	public static Comparator<CachedTile> probabilityComparator = new Comparator<CachedTile>(){
		@Override
		public int compare(CachedTile t1, CachedTile t2) {
			 /*if  (t1.beingLoaded && !t2.beingLoaded){
				 return 1;
			 }
			 else if (t2.beingLoaded && !t1.beingLoaded){
				 return -1;
			 }
			 else if (t1.beingLoaded && t2.beingLoaded){
				 return 0;
			 }
			 else*/ if  (t1.probability > t2.probability){
		    	 return 1;
		     }
		     else if (t1.probability<t2.probability){
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
	
	public Tile(Point point,String[] data){
		this.point = point;
		this.id = this.point.hashCode();
		this.data = data;
	}
	
	/*public int getFragmentNumber(){
		return fragments.size();
	}*/
	
	

	
	
	

	
	/*public void addFragment(Fragment fragm){
		fragments.put(fragm.num, fragm);	
		this.lod = this.getFragmentNumber();
	}*/
	
	/*public void removeFragment(int fragmNumber){
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
		
	}*/
	
	public String toString(){
		String str;
		str = "Tile("+this.point.y+","+this.point.x+")";
		return str;
	}

	
}
