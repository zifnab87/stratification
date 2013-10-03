package simulation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

public class Cache {
	//tiles
	//fragments
	public Map<Integer,Tile> tiles = new HashMap<Integer, Tile>();
	private PriorityBlockingQueue<Tile> queue= new PriorityBlockingQueue<Tile>(10,Tile.likelihoodComparator);
	
	
	public int getTileNumber(){
		return tiles.size();
	}
	public void updateTileLODwithPos(Point index,Viewport viewport){
		updateTileLODwithId(index.hashCode(),viewport);
	}
	
	public void updateAllTilesLOD(Viewport viewport){
		Set<Integer> tileIds = tiles.keySet();
		for(Integer tileId : tileIds){
			updateTileLODwithId(tileId,viewport);
			
		}
	}
	
	public void updateTileLODwithId(int tileId,Viewport viewport){
		Tile tile = tiles.get(tileId);
		Predictor.getLikelihood(tile,viewport);
		queue.remove(tile);
		queue.add(tile);
	}

	public void addTile(Tile tile){
		this.tiles.put(tile.id, tile);
		this.queue.add(tile);
	}
	
	public void removeTile(Point index){
		removeTile(index.hashCode());
	}
	
	public void removeTile(int tileId){
		Tile tile = this.tiles.remove(tileId);
		queue.remove(tile);
	}
	

	
	public void removeFragment(int tileId,int fragmNumber){
		Tile tile = this.tiles.get(tileId);
		int fragmCount = tile.getFragmentNumber();
		if (fragmCount>0){
			tile.removeFragment(fragmNumber);
		}
	}
	public void removeFragment(Point index,int fragmNumber){
		removeFragment(index.hashCode(),fragmNumber);
	}
	
	public Tile getTile(int y,int x){
		return getTile(new Point(y,x));
	}

	public Tile getTile(Point index){
		return getTile(index.hashCode());
	}
	public Tile getTile(int tileId){
		return this.tiles.get(tileId);
	}
	
	public boolean tileFull(int tileId){
		Tile tile = getTile(tileId);
		if (tile!=null){
			return tile.isFull();
		}
		else {
			return false;
		}
	}
	public boolean tileFull(Point index){
		return tileFull(index.hashCode());
	}
	
	public boolean tileExistsAndFull(int tileId){
		Tile tile = getTile(tileId);
		return (tile!=null) && tile.isFull();
	}
	public boolean tileExistsAndFull(Point index){
		return tileExistsAndFull(index.hashCode());
	}
	
	public boolean tileExistsAndNotFull(int tileId){
		Tile tile = getTile(tileId);
		return (tile!=null) && !tile.isFull();
	}
	public boolean tileExistsAndNotFull(Point index){
		return tileExistsAndFull(index.hashCode());
	}
	
	public boolean tileExists(int tileId){
		return tiles.containsKey(tileId);
	}
	public boolean tileExists(Point index){
		return tileExists(index.hashCode());
	}
	
	public boolean tileContainsFragment(int tileId,int fragmNumber){
		return this.tiles.get(tileId).containsFragment(fragmNumber);
	}

	
	public void addFragment(Fragment fragm,Point point){
		Tile tile = tiles.get(point.hashCode());
		if (tile==null){
			//add a new empty tile if there is not one already //warning with the pixels
			this.addTile(new Tile(new Point(point.y,point.x)));
			tile = tiles.get(point.hashCode());
		}
		tile.addFragment(fragm);
	}
	
	//insert fragment to that tile in cache
	/*public void addFragment(Fragment fragm,int tileId){
		Tile tile = tiles.get(tileId);
		if (tile!=null){
			tile.addFragment(fragm);
		}
		else {
			tile.addTile(new Tile());
		}
	}*/
	
	/*public void addFragment(Fragment fragm,Point index){
		addFragment(fragm,index.hashCode());
	}*/
	
}
