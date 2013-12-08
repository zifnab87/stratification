package simulation;

import static simulation.Config.CACHE_SIZE;
import static simulation.Config.FRAGMENTS_PER_TILE;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;

import simulation.Fragment;
import simulation.Main;
import simulation.Point;
import simulation.Tile;
import simulation.Viewport;
import simulation.events.UserMove;
import simulation.predictor.Node;
import simulation.predictor.Predictor;
import simulation.predictor.Tuple;

public class Cache {
	//public volatile Map<Integer,Tile> tiles = new HashMap<Integer, Tile>();
	//public volatile PriorityBlockingQueue<Tile> queue= new PriorityBlockingQueue<Tile>(10,Tile.likelihoodComparator);
	
	public  Map<Integer,CachedTile> tiles = new HashMap<Integer, CachedTile>();
	//public volatile LinkedList<CachedTile> queue = new PriorityBlockingQueue<CachedTile>(10,Tile.probabilityComparator);
	public TreeSet<CachedTile> queue = new TreeSet<CachedTile>(CachedTile.probabilityComparator);
	
	
	
	
	
	
	
	
	public int SpaceBeingUsed = 0;
	
	public CachedTile cacheFullTile(Tile tile){
		return cacheTileWithFragmentRange(tile, 1, FRAGMENTS_PER_TILE);
	}
	
	public CachedTile cacheFragment(Tile tile, int fragmentNum){
		return cacheTileWithFragmentRange(tile, fragmentNum, fragmentNum);
	}
	
	public CachedTile cacheTileWithFragmentRange(Tile tile,int firstFragment, int lastFragment){
		int spaceNeeded = lastFragment - firstFragment + 1;
		while(!this.hasAvailableSpace(spaceNeeded)){
			int diff = this.makeSpaceAvailable(spaceNeeded,tile.point);
			spaceNeeded -= diff;
		}
				
		String[] fragments = new String[FRAGMENTS_PER_TILE];
		for (int i=firstFragment-1; i<=lastFragment-1;i++){
			fragments[i] = tile.data[i];
		}
		CachedTile toBeCached = new CachedTile((Point)(tile.point.clone()),fragments);
		
		toBeCached.probability = tile.carryingProbability;
		if (!this.tiles.containsKey(toBeCached.id)){
			this.tiles.put(toBeCached.id, toBeCached);
		}
		//it cannot be done with just contains due to equality constraint (it has to be both x,y and probability same) ... :/
		if (!queueContains(toBeCached)){
			this.queue.add(toBeCached);
		}
		
		increaseSpaceUsed(lastFragment-firstFragment+1);
		return toBeCached;
		
	}

	private boolean queueContains(CachedTile toBeCached) {
		boolean found = false;
		Iterator<CachedTile> iter = this.queue.iterator();
		while(iter.hasNext()){
			CachedTile cTile = iter.next();
			if (cTile.id == toBeCached.id){
				found = true;
				break;
			}
		}
		return found;
	}
	
		
	
	
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
	
	/*public void declareOccupied(Point point){
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
	*/

	
	public void refresh(CachedTile tile){
		
		//refresh repositions tile based on the new likelihood
		queue.remove(tile);
		queue.add(tile);
	}
	
	
	
	

	public CachedTile fetchTile(Point point,UserMove caller){
		return this.fetchTile(point.hashCode(),caller);
	}
	
	
	public CachedTile getTile(Point point){
		return this.tiles.get(point.hashCode());
	}
	
	public CachedTile fetchTile(int hash,UserMove caller){
		CachedTile cachedTile = tiles.get(hash);
		if (cachedTile!=null){
			caller.cacheHits += cachedTile.getCachedFragmentsNum();
			UserMove.totalCacheHits+=cachedTile.getCachedFragmentsNum();
		}
		return getTile(hash);
	}
	
	public CachedTile getTile(int hash){
		return this.tiles.get(hash);
	}
	
	
	/*public Fragment fetchFragmentOfTile(int fragmentNumber,Point index,UserMove caller){
		
		caller.cacheHits+=1;
		UserMove.totalCacheHits+=1;
		return getFragmentOfTile(fragmentNumber,index);
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
	}*/
	
	
	
	
	public boolean tileExists(int tileId){
		return tiles.containsKey(tileId);
	}
	
	public boolean tileExists(Point index){
		return tileExists(index.hashCode());
	}
	
	public boolean tileExistsAndFull(int tileId){
		CachedTile tile = getTile(tileId);
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
		CachedTile tile = this.getTile(tileId);
		return (tile!=null) && !tile.isFull();
	}
	
	public boolean tileExistsAndNotFull(Point index){
		return tileExistsAndNotFull(index.hashCode());
	}
	
	public int makeSpaceAvailable(int fragmentsNeeded,Point currentPoint){
		//System.out.println("I need"+fragmentsNeeded);
		CachedTile lessLikelyTile = queue.pollFirst();
		//System.out.println(queue);
		return 0;
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
	
//	public void evictFragmentedTile(Point index){
//		evictFragmentedTile(index.hashCode());
//	}
//	
//	public void evictFragmentedTile(int tileId){
//		//if(this.isFull()){
//			CachedTile tile = this.tiles.get(tileId);
//			if (tileExists(tileId)){
//				int fragmentCount = tile.getCachedFragmentsNum();
//				tiles.remove(tileId);
//				queue.remove(tile);
//				decreaseSpaceUsed(fragmentCount);
//			}
//		//}
//			
//	}
	
//	
//	public void evictFragment(int tileId,int fragmNumber){
//		//if (!this.hasAvailableSpace(availableSpace)){
//			//makeConsistent();
//			CachedTile tile = this.tiles.get(tileId);
//			int fragmCount = tile.getCachedFragmentsNum();
//			if (fragmCount>0){
//				//HOTFIX BECAUSE OF MISSED FRAGMENTS 1,2,3,4,5, 7 6 is missing so we get the max all the time! 
//				int maxFragm = 1;
//				for (int i=1; i<=FRAGMENTS_PER_TILE; i++){
//					if (maxFragm<i && tile.containsFragment(i)){
//						maxFragm = i;
//					}
//				}
//				
//				
//				if (tile.containsFragment(maxFragm)){
//					tile.removeFragment(maxFragm);
//					decreaseSpaceUsed(1);
//				}
//				
//				fragmCount = tile.getCachedFragmentsNum();
//				//in case that was the last fragment of the tile
//				if (fragmCount==0){
//					queue.remove(tile);
//					tiles.remove(tileId);
//				}
//			}
//		//}
//	}
//	public void evictFragment(Point index,int fragmNumber){
//		evictFragment(index.hashCode(),fragmNumber);
//	}
//	
//	
//	public int makeSpaceAvailable(int fragments,Point point){
//		//makeConsistent();
//		CachedTile dontTouchTile = null;
//		double oldLikelihood = -1.0;
//		if (tileExists(point)){
//			dontTouchTile = tiles.get(point.hashCode());
//			oldLikelihood = dontTouchTile.probability;
//			dontTouchTile.probability = 2.0;
//			//refresh(dontTouchTile);
//		}
//		
//		int sizeBefore = this.SpaceBeingUsed;
//		if (fragments==1){
//			CachedTile lessLikelyTile = queue.pollFirst();
//			int fragmNumber = lessLikelyTile.getCachedFragmentsNum();
//			//remove only if the likelihood of the one removed is lower than the one to be inserted
//			//if (lessLikelyTile.likelihood < point.carriedLikeliood){
//
//				this.evictFragment(lessLikelyTile.point, fragmNumber);
//			//}
//			//else {
//			//	System.out.println(lessLikelyTile.likelihood+" "+point.carriedLikeliood);
//			//}
//			
//		}
//		else {
//			System.out.println("aaaa"+queue.size());
//			CachedTile lessLikelyTile = queue.pollFirst();
//			int fragmNumber = lessLikelyTile.getCachedFragmentsNum();
//			for (int i=0; i<fragmNumber; i++){
//				this.evictFragment(lessLikelyTile.point, i);
//			}
//			
//		}
//		int sizeAfter = this.SpaceBeingUsed;
//		if (tileExists(point)){
//			dontTouchTile.probability = oldLikelihood;
//		}
//		//makeConsistent();
//		return sizeBefore - sizeAfter;
//	}
	
	
	/*public void cacheFragment(Tile tile,double carriedLikelihood){
		int counter = 0;
		while(!this.hasAvailableSpace(1)){
			int diff = makeSpaceAvailable(1,new Point());
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
		
	}*/
	
	

	
	/*public void updateAllTileLikelihoods(HashMap<Node,Tuple<Double,Integer>> map){
		Iterator<Tile> it = queue.iterator();
		while (it.hasNext()){
			Tile tile = it.next();
			//updateTileLikelihoodOfIndex(it.next().point,currentViewport);
			if (map.containsKey(new Node(tile.point.y,tile.point.x))){
				Tuple<Double,Integer> tuple = map.get(new Node(tile.point.y,tile.point.x));
				double likelihood = tuple.x;
				int lod = tuple.y;
				tile.likelihood = likelihood;
				if (this.isFull()){
					int oldLOD = tile.lod;
					tile.lod = lod;
					if (oldLOD>lod){
						//evict from that tile, since less information is needed
					}
					else { // lod < oldLOD
						//evict from cache to put information to that tile
					}
					
				}
			}
		}
	}*/
	
	
	/*public void updateAllTileLikelihoods(Viewport currentViewport){
		Iterator<Tile> it = queue.iterator();
		while (it.hasNext()){
			updateTileLikelihoodOfIndex(it.next().point,currentViewport);
		}
		this.makeConsistent();
		
	}*/
	
	/*private void updateTileLikelihoodOfIndex(Point index,Viewport currentViewport){
		Tile tile = tiles.get(index.hashCode());
		if (tileExists(index)){
			double newLikelihood = 1.0d;
			//double newLikelihood =  Predictor.calculateLikelihood(index, currentViewport);
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
	}*/
	
	
	
	
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
		Iterator<CachedTile> iter = queue.iterator();
		while(iter.hasNext()){
			CachedTile tile = iter.next();
		/*	if (tile.lod!=Predictor.likelihoodToLOD(tile.likelihood)){
				
				result+=" INCONSISTENT cached "+tile.lod+" vs based-on-likelihood"+Predictor.likelihoodToLOD(tile.likelihood)+" "+tile+" /INCONSISTENT\n";
				//inconsistent = true;
				//return result;
			}*/
			result+=tile.toString()+": ("+tile.getCachedFragmentsNum()+"): fragments";
			result+=tile.fragmentsToString();
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
	
	//update cache probability based on the prediction tree
	public void updateProbabilities(Vector<Node> list,Point currentPosition){
		Iterator<Integer> mapIter = this.tiles.keySet().iterator();
		while (mapIter.hasNext()){
			CachedTile cTile = this.tiles.get(mapIter.next());
			//IMPORTANT remove before the equality is busted because of change in probability
			this.queue.remove(cTile);
			//we make probabilities zero so only the ones that will be updated by the 
			//prediction tree will have probability less than zero
			//if it is current we give it a probability of 1.0d
			if (currentPosition.equals(cTile.point)){
				cTile.probability = 1.0d;
			}
			else {
				cTile.probability = 0.0d;
			}
			cTile.distance = Predictor.distance(cTile.point, currentPosition);
			if (!queueContains(cTile)){
				this.queue.add(cTile);
			}
		}
		
		
		
		Iterator<Node> iter = list.iterator();
		while(iter.hasNext()){
			Node node = iter.next();
			if (this.tiles.containsKey(node.point.hashCode())){
				CachedTile cTile = this.tiles.get(node.point.hashCode());
				//IMPORTANT remove before the equality is busted because of change in probability
				this.queue.remove(cTile);
				cTile.probability = node.probability;
				//cTile.distance = Predictor.distance(cTile.point, currentPosition);
				//cTile.data = new String[]{"da","dasd",null,null,null,null,null,null};	
				if (!queueContains(cTile)){
					this.queue.add(cTile);
				}
			}	
		}
		
		

		System.out.println("Updated Memory because of Prediction"+this.queue);
	}
}
