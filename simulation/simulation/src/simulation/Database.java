package simulation;

import java.util.HashMap;
import java.util.Map;

import simulation.events.UserMove;

import simulation.Fragment;
import simulation.Point;
import simulation.Tile;
import simulation.Viewport;
import static simulation.Config.FRAGMENTS_PER_TILE;

public class Database {
	public Map<Integer,Tile> tiles = new HashMap<Integer, Tile>();
	public Viewport viewport;
	
	
	
	
	public Database(){
		
	}
	
	//initial viewport
	public void setViewport(Viewport viewport){
		this.viewport = viewport;
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
	
	public boolean tileExists(Point index){
		return tiles.containsKey(index.hashCode());
	}
	
	private Tile getTile(Point index){
		Tile tile = tiles.get(index.hashCode());
		return tile;		
	}
	
	public Tile fetchTile(Point index,UserMove caller){
		
		caller.cacheMisses+=FRAGMENTS_PER_TILE;
		UserMove.totalCacheMisses+=FRAGMENTS_PER_TILE;
		return getTile(index);
	}
	
	
	
	private Fragment getFragmentOfTile(int fragmentNumber,Point index){
		
		if (tileExists(index)){
			Tile tile = getTile(index);
		
			return tile.getFragment(fragmentNumber);
		}
		else {
			System.out.println("tile Doesn't exist for fragment");
			return null;
		}
	}
	
	public Fragment fetchFragmentOfTile(int fragmentNumber,Point index,UserMove caller){
		
		caller.cacheMisses+=1;
		UserMove.totalCacheMisses+=1;
		return getFragmentOfTile(fragmentNumber,index);
	}
	
}
