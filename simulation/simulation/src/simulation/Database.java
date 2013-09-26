package simulation;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Database {
	
	private Map<Integer,Tile> tiles = new HashMap<Integer, Tile>();
	public Viewport viewport;
	
	
	
	
	public Database(){
		
	}
	
	
	public void setViewport(Viewport viewport){
		this.viewport = viewport;
	}
	
	public Vector<Tile> viewportFetch(){
		Point upperLeft = this.viewport.upperLeft;
		Point lowerRight = this.viewport.lowerRight;
		Vector<Tile> vec = new Vector<Tile>();
		for (int y=upperLeft.y; y<=lowerRight.y; y++){
			for (int x=upperLeft.x; y<=lowerRight.x; y++){
				Point index = new Point(y,x);
				if (!Main.cache.tileExists(index)){
					vec.add(getTile(index));
				}
			}
		}
		return vec;
	}
	
	public void init(int numTiles){
		for (int i=0; i<numTiles; i++){
			Tile tile = Tile.randomizer();
			putTile(tile);
		}
	}
	
	public void putTile(Tile tile){
		tiles.put(tile.id, tile);
	}
	
	public boolean tileExists(int tileId){
		return tiles.containsKey(tileId);
	}
	
	public Tile getTile(Point index){
		Tile tile = tiles.get(index.hashCode());
		return tile;		
	}
	
	
	
	public Fragment getFragmentOfTile(int FragmentNumber,int tileId){
		Tile tile = tiles.get(tileId);
		return tile.getFragment(FragmentNumber);
	}
}
