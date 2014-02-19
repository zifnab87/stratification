package ver3.simulation;

public class Fragment {
	final String data;
	final int zoomLevel;
	final Tile parent; 
	
	public Fragment(String data,int zoomLevel,Tile parent){
		this.data = data;
		this.zoomLevel = zoomLevel;
		this.parent = parent;
	}
	
	
}
