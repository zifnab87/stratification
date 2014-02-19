package ver3.simulation;
import static ver3.simulation.Config.FRAGMENTS_PER_TILE;
import  ver3.simulation.Point;
import ver3.simulation.Tile;

public class Tile {
	final public int id;
	final public int y;
	final public int x;
	final public Point point; //index
	
	Fragment[] fragments = new Fragment[FRAGMENTS_PER_TILE];
	
	public Tile(Point point){
		this.point = point;
		this.id = this.point.hashCode();
		this.y = point.y;
		this.x = point.x;
	}
	
	public Tile(Point point,String[] data){
		this(point);
		if (data.length == fragments.length){
			for (int i=0; i<data.length; i++){
				Fragment frag = new Fragment(data[i],i,this);
				addFragment(frag,i);
			}
		}
	}
	
	
	public void addFragment(Fragment fragment,int position){
		if (position<fragments.length && position>0){
			fragments[position] = fragment;
		}
	}
	
	
	
	
	
	public  boolean equals(Object o){
		if (this.point.equals(((Tile)o).point)){
			return true;
		}
		else {
			return false;
		}
	}
	
	public String dataToString(){
		String str = "";
		for(int i=0; i<fragments.length; i++){
			str+=fragments[i].data+", ";
		}
		return str;
	}
	
	public String[] data(){
		String[] data = new String[FRAGMENTS_PER_TILE];
		for (int i=0; i<fragments.length; i++){
			data[i] = fragments[i].data;
		}
		return data;
	}
	
	public String toString(){
		String str;
		str = "Tile("+this.point.y+","+this.point.x+")";
		return str;
	}
	
	
}
