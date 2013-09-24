package simulation;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

public class Cache {
	//tiles
	//fragments
	private Map<Integer,Tile> tiles = new HashMap<Integer, Tile>();
	private FixedSizePriorityQueue<Tile> queue= new FixedSizePriorityQueue<Tile>(Predictor.PREDICTION_SIZE,Tile.lodComparator);
	
	
	public int getTileNumber(){
		return tiles.size();
	}
	
	// cache datastructure
	public void addTile(Tile tile){
		this.tiles.put(tile.id, tile);
	}
	
	public Tile getTile(int tileId){
		return this.tiles.get(tileId);
	}
	//insert fragment to that tile in cache
	public void addFragment(Fragment fragm,int tileId){
		Tile tile = tiles.get(tileId);
		tile.addFragment(fragm);
	}

	
}
