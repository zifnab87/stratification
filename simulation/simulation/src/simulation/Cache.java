package simulation;

import static simulation.Config.FRAGMENTS_PER_TILE;
import static simulation.Config.debug;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import simulation.monitor.Monitor;


public class Cache {
	//tiles
	//fragments
	public Map<Integer,Tile> tiles = new HashMap<Integer, Tile>();
	private PriorityBlockingQueue<Tile> queue= new PriorityBlockingQueue<Tile>(10,Tile.likelihoodComparator);
	
	
	public int howManyTiles(){
		return tiles.size();
	}
	
	public void cacheFullTile(Tile tile){
		Tile tileClone = Tile.copyTile(tile);
		tileClone.setCached(true);
		tileClone.likelihood = 1.0d;
		tileClone.lod = FRAGMENTS_PER_TILE;
		this.tiles.put(tileClone.id, tileClone);
		this.queue.add(tileClone);
		for (int i=1; i<=FRAGMENTS_PER_TILE; i++){
			cacheFragment(new Fragment(i,null),tileClone.point,tileClone.likelihood);
		}
	}
	
	public Tile getTile(Tile tile){
		return this.tiles.get(tile.id);
	}
	
	public Tile getTile(Point point){
		return this.tiles.get(point.hashCode());
	}
	
	public Tile getTile(int hash){
		return this.tiles.get(hash);
	}
	
	public boolean tileExists(int tileId){
		return tiles.containsKey(tileId);
	}
	
	public boolean tileExists(Point index){
		return tileExists(index.hashCode());
	}
	
	public boolean tileExistsAndFull(int tileId){
		Tile tile = getTile(tileId);
		if (tile != null){
			return tile.isFull();
		}
		else {
			return false;
		}
	}
	public boolean tileExistsAndFull(Point index){
		return tileExistsAndFull(index.hashCode());
	}
	
	public boolean tileExistsAndNotFull(int tileId){
		Tile tile = getTile(tileId);
		return (tile!=null) && !tile.isFull();
	}
	
	public boolean tileExistsAndNotFull(Point index){
		return tileExistsAndNotFull(index.hashCode());
	}
	

	public void evictTile(Point index){
		evictTile(index.hashCode());
	}
	
	public void evictTile(int tileId){
		Tile tile = this.tiles.remove(tileId);
		queue.remove(tile);
	}
	
	
	
	public void evictFragment(int tileId,int fragmNumber){
		Tile tile = this.tiles.get(tileId);
		int fragmCount = tile.getFragmentNumber();
		if (fragmCount>0){
			tile.removeFragment(fragmNumber);
		}
	}
	public void evictFragment(Point index,int fragmNumber){
		evictFragment(index.hashCode(),fragmNumber);
	}
	
	public void cacheFragment(Fragment fragm,Point point,double likelihood){
		
		Tile tile = tiles.get(point.hashCode());
		if (tile==null){
			//add a new empty tile if there is not one already //warning with the pixels
			this.addTile(new Tile(new Point(point.y,point.x)));
			tile = tiles.get(point.hashCode());
		}
		if (tile!=null && fragm!=null){
			//TODO FIX SO THAT IS NOT HAPPENING ALL THE TIME
			tile.addFragment(fragm);
			tile.likelihood = likelihood;
			tile.lod = Predictor.likelihoodToLOD(likelihood);
			
		}
	}
	
	
	
	private void addTile(Tile tile){
		Tile tileClone = Tile.copyTile(tile);
		tileClone.setCached(true);
		this.tiles.put(tileClone.id, tileClone);
		this.queue.add(tileClone);
		
	}
	
	
	public void updateAllTilesLOD(Viewport viewport){
		Iterator<Tile> it = queue.iterator();
		while (it.hasNext()){
			updateTileLODwithId(it.next().id,viewport);
		}
		
	}
	
	public synchronized void updateTileLODwithId(int tileId,Viewport viewport){
		Tile tile = tiles.get(tileId);
		
		//tile.setLOD(viewport);
		queue.remove(tile);
		queue.add(tile);
	}
	
	
	
	public String toString(){
		String result="";
		for(Tile tile : queue){
			result+=tile.toString()+": ("+tile.getFragmentNumber()+"): fragments[";
		    for(int index : tile.fragments.keySet()){
		    	result+=tile.getFragment(index).num+",";
		    }
		    result+="]\n";
		}
		return result;
	}
	/*
	public int getTileNumber(){
		return tiles.size();
	}
	public void updateTileLODwithPos(Point index,Viewport viewport){
		updateTileLODwithId(index.hashCode(),viewport);
	}
	
	public void putMissingFragments(Point index) {
		Tile cachedPartialTile = Main.cache.getTile(index);
		int added=0;
		for (int fragmNum=1; fragmNum<=FRAGMENTS_PER_TILE; fragmNum++){
			//if fragment doesn't exist request fetch from database;
			if (!cachedPartialTile.containsFragment(fragmNum)){
				//and not already in there
				if (!index.fragmentNums.contains(fragmNum)){
					index.fragmentNums.add(fragmNum);
					added++;
				}
			}
		}
		int fragmentsExisted = FRAGMENTS_PER_TILE - added;
		for (int j=0; j<fragmentsExisted; j++){
			Monitor.cacheFragmentFetch();
			if (debug){
				System.out.println("Fragment fetched from Cache! (UserMove)");
			}
		}
	}
	
	public void fulfillLODfromScratch(Point index,int LOD){
		Tile cachedPartialTile = Main.cache.getTile(index);
		
		if (LOD < FRAGMENTS_PER_TILE){
			for (int fragmNum=1; fragmNum<=LOD; fragmNum++){
				//if fragment doesn't exist request fetch from database;
				if(!index.fragmentNums.contains(fragmNum)){
					index.fragmentNums.add(fragmNum);
				}
			}
		}
	}
	
	public void fullfillLODfromOldLOD(Point index,int LOD){
		Tile cachedPartialTile = Main.cache.getTile(index);
		int oldLOD = cachedPartialTile.getFragmentNumber();
		if (oldLOD<LOD){
			for (int fragmNum=oldLOD+1; fragmNum<=LOD; fragmNum++){
				//if fragment doesn't exist request fetch from database;
				if(!index.fragmentNums.contains(fragmNum)){
					index.fragmentNums.add(fragmNum);
				}
			}
		}
		int fragmentsExisted = oldLOD;
		for (int j=0; j<fragmentsExisted; j++){
			if (debug){
				System.out.println("Fragment fetched from Cache! (UserMove)");
			}
			Monitor.cacheFragmentFetch();
		}
	}
	
	public void updateAllTilesLOD(Viewport viewport){
		Iterator<Tile> it = queue.iterator();
		while (it.hasNext()){
			updateTileLODwithId(it.next().id,viewport);
		}
		
	}
	
	public synchronized void updateTileLODwithId(int tileId,Viewport viewport){
		Tile tile = tiles.get(tileId);
		
		//tile.setLOD(viewport);
		queue.remove(tile);
		queue.add(tile);
	}

	private void addTile(Tile tile){
		Tile tileClone = Tile.copyTile(tile);
		tileClone.setCached(true);
		this.tiles.put(tileClone.id, tileClone);
		this.queue.add(tileClone);
		
	}
	public void addFullTile(Tile tile){
		Tile tileClone = Tile.copyTile(tile);
		tileClone.setCached(true);
		this.tiles.put(tileClone.id, tileClone);
		this.queue.add(tileClone);
		for (int i=0; i<FRAGMENTS_PER_TILE; i++){
			addFragment(new Fragment(i,null),tileClone.point);
		}
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
		if (tile!=null && fragm!=null){
			tile.addFragment(fragm);
		}
	}
	*/
	

	
}
