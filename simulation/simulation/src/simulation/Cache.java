package simulation;

import static simulation.Config.FRAGMENTS_PER_TILE;
import static simulation.Config.CACHE_SIZE;
import static simulation.Config.FRAGMENT;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import static simulation.Config.debug;

public class Cache {
	//tiles
	//fragments
	public volatile Map<Integer,Tile> tiles = new HashMap<Integer, Tile>();
	public volatile PriorityBlockingQueue<Tile> queue= new PriorityBlockingQueue<Tile>(10,Tile.likelihoodComparator);
	public int SpaceBeingUsed = 0;
	
	
	public boolean isFull(){

		if (SpaceBeingUsed >= CACHE_SIZE ){
			return true;
		}
		else {
			return false;
		}
	}
	
	public int howManyTiles(){
		return tiles.size();
	}
	
	public int sizeBeingUsed(){
		return SpaceBeingUsed;
	}
	
	public synchronized void increaseSpaceUsed(int numOfFragments){
		this.SpaceBeingUsed = this.SpaceBeingUsed + numOfFragments;
	}
	
	public synchronized void decreaseSpaceUsed(int numOfFragments){
		this.SpaceBeingUsed = this.SpaceBeingUsed - numOfFragments;
	}
	
	public void declareOccupied(Point point){
		if (tileExists(point)){
			Tile tile = getTile(point);
			tile.beingLoaded =true;
		}
	}
	
	public void declareOccupied(Tile tile){
		declareOccupied(tile.point);
	}
	
	public void undeclareOccupied(Point point){
		if (tileExists(point)){
			Tile tile = getTile(point);
			tile.beingLoaded = false;
		}
	}
	
	public void undeclareOccupied(Tile tile){
		undeclareOccupied(tile.point);
	}
	
	public void makeConsistent(){
		//changes fragment number based on LOD
		Iterator<Tile> it = queue.iterator();
		while (it.hasNext()){
			
			Tile tile = it.next();
			//int oldLOD = tile.lod;
			int newLOD = Predictor.likelihoodToLOD(tile.likelihood);
			tile.lod = newLOD;		

		}
		
	}
	
	public void refresh(Tile tile){
		
		//refresh repositions tile based on the new likelihood
		queue.remove(tile);
		queue.add(tile);
	}
	
	public void cacheFullTile(Tile tile){
		int counter = 0;
		while(!this.hasAvailableSpace(FRAGMENTS_PER_TILE)){
			int diff = makeSpaceAvailable(FRAGMENTS_PER_TILE,tile.point);
			/*if (diff==0){
				counter++;
			}
			if (counter>5){
				break;
			}*/
		}
		Tile tileClone = Tile.copyTile(tile);
		tileClone.setCached(true);
		tileClone.likelihood = 1.0d;
		tileClone.lod = FRAGMENTS_PER_TILE;
		if (!this.tiles.containsKey(tileClone.id)){
			this.tiles.put(tileClone.id, tileClone);
		}
		if (!this.queue.contains(tileClone)){
			this.queue.add(tileClone);
		}
		
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
	

	/*public void evictFullTile(Point index){
		evictFullTile(index.hashCode());
		
	}
	
	public void evictFullTile(int tileId){
		//if (this.isFull()){
			Tile tile = this.tiles.get(tileId);
			tiles.remove(tileId);
			queue.remove(tile);
			decreaseSpaceUsed(FRAGMENTS_PER_TILE);
		//}
	}*/
	
	public void evictFragmentedTile(Point index){
		evictFragmentedTile(index.hashCode());
	}
	
	public void evictFragmentedTile(int tileId){
		//if(this.isFull()){
			Tile tile = this.tiles.get(tileId);
			if (tileExists(tileId)){
				int fragmentCount = tile.getFragmentNumber();
				tiles.remove(tileId);
				queue.remove(tile);
				decreaseSpaceUsed(fragmentCount);
			}
		//}
			
	}
	
	
	public void evictFragment(int tileId,int fragmNumber){
		//if (!this.hasAvailableSpace(availableSpace)){
			makeConsistent();
			Tile tile = this.tiles.get(tileId);
			int fragmCount = tile.getFragmentNumber();
			if (fragmCount>0){
				//HOTFIX BECAUSE OF MISSED FRAGMENTS 1,2,3,4,5, 7 6 is missing so we get the max all the time! 
				int maxFragm = 0;
				for (int i=1; i<=FRAGMENTS_PER_TILE; i++){
					if (maxFragm<i && tile.containsFragment(i)){
						maxFragm = i;
					}
				}
				
				
				if (tile.containsFragment(maxFragm)){
					tile.removeFragment(maxFragm);
					decreaseSpaceUsed(1);
				}
				
				fragmCount = tile.getFragmentNumber();
				//in case that was the last fragment of the tile
				if (fragmCount==0){
					queue.remove(tile);
					tiles.remove(tileId);
				}
			}
			makeConsistent();
		//}
	}
	public void evictFragment(Point index,int fragmNumber){
		evictFragment(index.hashCode(),fragmNumber);
	}
	
	
	public int makeSpaceAvailable(int fragments,Point point){
		makeConsistent();
		Tile dontTouchTile = null;
		double oldLikelihood = -1.0;
		if (tileExists(point)){
			dontTouchTile = tiles.get(point.hashCode());
			oldLikelihood = dontTouchTile.likelihood;
			dontTouchTile.likelihood = 2.0;
			refresh(dontTouchTile);
		}
		
		int sizeBefore = this.SpaceBeingUsed;
		if (fragments==1){
			Tile lessLikelyTile = queue.peek();
			int fragmNumber = lessLikelyTile.getFragmentNumber();
			
			evictFragment(lessLikelyTile.point, fragmNumber);
		}
		else {
			Tile lessLikelyTile = queue.peek();
			int fragmNumber = lessLikelyTile.getFragmentNumber();
			for (int i=0; i<fragmNumber; i++){
				evictFragment(lessLikelyTile.point, i);
			}
			
		}
		int sizeAfter = this.SpaceBeingUsed;
		if (tileExists(point)){
			dontTouchTile.likelihood = oldLikelihood;
		}
		makeConsistent();
		return sizeBefore - sizeAfter;
	}
	
	
	public void cacheFragment(Fragment fragm,Point point,double carriedLikelihood){
		int counter = 0;
		while(!this.hasAvailableSpace(1)){
			int diff = makeSpaceAvailable(1,point);
			System.out.println(this.SpaceBeingUsed);
			System.out.println(this);

			
		}
		
		Tile tile = tiles.get(point.hashCode());
		if (tile==null){
			//add a new empty tile if there is not one already //warning with the pixels
			this.addTile(new Tile(new Point(point.y,point.x)));
			tile = tiles.get(point.hashCode());
		}
		if (tile!=null && fragm!=null){
			tile.addFragment(fragm);
			//HOTFIX

			if (tile.likelihood == -1){
				tile.likelihood = carriedLikelihood;
			}
			//tile.lod = tile.getFragmentNumber();
			refresh(tile);
			
			increaseSpaceUsed(1);
			
		}
		
	}
	
	
	
	private void addTile(Tile tile){
		Tile tileClone = Tile.copyTile(tile);
		tileClone.setCached(true);
		if (!this.tiles.containsKey(tile.id)){
			this.tiles.put(tileClone.id, tileClone);
		}
		if (!this.queue.contains(tile)){
			this.queue.add(tileClone);
		}
		
	}
	
	
	public void updateAllTileLikelihoods(Viewport currentViewport){
		Iterator<Tile> it = queue.iterator();
		while (it.hasNext()){
			updateTileLikelihoodOfIndex(it.next().point,currentViewport);
		}
		this.makeConsistent();
		
	}
	
	private void updateTileLikelihoodOfIndex(Point index,Viewport currentViewport){
		Tile tile = tiles.get(index.hashCode());
		if (tileExists(index)){
			double newLikelihood =  Predictor.calculateLikelihood(index, currentViewport);
			//if likelihood became 0.0 (the only case that LOD=0)
		    // then remove the tile 
			if (newLikelihood == 0.0d && this.isFull()){
				
				evictFragmentedTile(index);
			}
			else {
				tile.likelihood = newLikelihood;
				refresh(tile);
			}
		}
	}
	
	
	
	public String toString(){
		String result="";
		/*for(Tile tile : queue){
			result+=tile.toString()+": ("+tile.getFragmentNumber()+"): fragments[";
		    for(int index : tile.fragments.keySet()){
		    	result+=tile.getFragment(index).num+",";
		    }
		    result+="]\n";
		}*/
		if (Main.cache.tiles.size()!=Main.cache.queue.size()){
			
			result+=" INCONSISTENT SIZES tiles/queue"+ Main.cache.tiles.size()+" vs "+Main.cache.queue.size();
		}
		Iterator<Tile> iter = queue.iterator();
		while(iter.hasNext()){
			Tile tile = iter.next();
			if (tile.lod!=Predictor.likelihoodToLOD(tile.likelihood)){
				
				result+=" INCONSISTENT cached "+tile.lod+" vs based-on-likelihood"+Predictor.likelihoodToLOD(tile.likelihood)+" "+tile+" /INCONSISTENT\n";
				//inconsistent = true;
				//return result;
			}
			result+=tile.toString()+": ("+tile.getFragmentNumber()+"): fragments[";
			Iterator<Integer> fragmIter = tile.fragments.keySet().iterator();
			while(fragmIter.hasNext()){
		    	result+=tile.getFragment(fragmIter.next()).num+",";
		    }
		    result+="]\n";
		}
		return result;
	}
	
	public boolean hasAvailableSpace(int fragments){
		if ((CACHE_SIZE-SpaceBeingUsed)>=fragments){
			return true;
		}
		else {
			return false;
		}
	}

	
}
