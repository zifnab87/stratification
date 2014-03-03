package sync.simulation;






import static sync.simulation.Config.FRAGMENTS_PER_TILE;


public class Tile {

//	public Map<Integer,Fragment> fragments = new HashMap<Integer, Fragment>();

	
	
	public int id;
	public int y;
	public int x;
	String[] data = new String[FRAGMENTS_PER_TILE];

	
	
	
	public double carryingProbability;
	public int carryingDistance;
	
	public Point point; //index
	
	
	public  boolean equals(Object o){
		if (this.point.equals(((Tile)o).point)){
			return true;
		}
		else {
			return false;
		}
	}
	
	
	
	
	
	public Tile(Point point){
		this.point = point;
		this.id = this.point.hashCode();
	}
	
	/*public Tile(Point point,String[] data){
		this.point = point;
		this.id = this.point.hashCode();
		this.data = data;
	}*/
	
	public void setData(String[] data){
		this.data = data;
	}
	
	
	public String dataToString(){
		String str = "";
		for(int i=0; i<data.length; i++){
			str+=data[i]+", ";
		}
		return str;
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
