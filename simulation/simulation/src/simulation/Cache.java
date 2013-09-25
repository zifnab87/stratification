package simulation;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

public class Cache {
	//tiles
	//fragments
	private Map<Integer,Tile> tiles = new HashMap<Integer, Tile>();
	private PriorityBlockingQueue<Tile> queue= new PriorityBlockingQueue<Tile>(Predictor.PREDICTION_SIZE,Tile.lodComparator);
	
	
	public int getTileNumber(){
		return tiles.size();
	}
	
	public void updateTileLODwithId(int tileId){
		Tile tile = tiles.get(tileId);
		Predictor.getLikelihood(tile);
		queue.remove(tile);
		queue.add(tile);
	}
	
	public void updateAllTilesLOD(){
		Set<Integer> tileIds = tiles.keySet();
		for(Integer tileId : tileIds){
			updateTileLODwithId(tileId);
			
		}
	}
	
	// cache datastructure
	public void addTile(Tile tile){
		this.tiles.put(tile.id, tile);
	}
	
	public int removeTile(int tileId){
		Tile tile = this.tiles.get(tileId);
		int fragmCount = tile.getFragmentNumber();
		return fragmCount;
	}
	
	public void removeFragment(int tileId,int fragmNumber){
		Tile tile = this.tiles.get(tileId);
		int fragmCount = tile.getFragmentNumber();
		if (fragmCount>0){
			tile.removeFragment(fragmNumber);
		}
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
