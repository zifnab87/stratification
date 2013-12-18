package simulation;

import static simulation.Config.CACHE_SIZE;
import static simulation.Config.FRAGMENTS_PER_TILE;
import static simulation.Config.FRAGMENT_SIZE;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;

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
	
	public void warmUp(){
		
		int start = 10000;
		int count = 0;
		while(CACHE_SIZE!=this.sizeBeingUsed()){
			String data[] = new String[FRAGMENTS_PER_TILE];
			
			for(int j=0; j<Math.min(FRAGMENTS_PER_TILE,(CACHE_SIZE-this.sizeBeingUsed())); j++){
				String datum = "[";
				for (int pixel=0; pixel<FRAGMENT_SIZE; pixel++){
					int red = new Random().nextInt(255);
					int green = new Random().nextInt(255);
					int blue = new Random().nextInt(255);
					datum += "["+red+","+green+","+blue+"],";
				}
				datum +="]";
				data[j]=datum;
			}
			Point point  = new Point((start+count),(start+count),true);
			CachedTile cTile = new CachedTile(point, data);
			cTile.distance =  10000;
			tiles.put(point.hashCode(), cTile);
			queue.add(cTile);
			//increaseSpaceUsed(FRAGMENTS_PER_TILE);
			count++;
		}
		
	}
	
//	public CachedTile cacheFullTile(Tile tile){
//		return cacheTileWithFragmentRange(tile, 1, FRAGMENTS_PER_TILE);
//	}
	
	/*public CachedTile cacheFragment(Tile tile, int fragmentNum){
		return cacheTileWithFragmentRange(tile, fragmentNum, fragmentNum);
	}*/
	
	
	
	public CachedTile cacheTileWithFragmentRange(Tile tile,int firstFragment, int lastFragment){
		int spaceNeeded = lastFragment - firstFragment + 1;
		while(!this.hasAvailableSpace(spaceNeeded)){
			int diff = this.makeSpaceAvailable(spaceNeeded,tile.point);
			spaceNeeded -= diff;
		}
		System.out.println("spaceNeeded"+spaceNeeded);
		int index = tile.point.hashCode();
		CachedTile toBeCached = this.tiles.get(index);
	
		String[] fragments = new String[FRAGMENTS_PER_TILE];
		for (int i=firstFragment-1; i<=lastFragment-1;i++){
			//if (fragments[i]== null){
				fragments[i] = tile.data[i];
			//}
		}
		if (toBeCached!=null){
			//already stored data..
			
			for (int i=0; i <toBeCached.data.length; i++){
				if (toBeCached.data[i]!=null){
					fragments[i]=toBeCached.data[i];
				}
			}
			
		}
		
		
		
		if (toBeCached==null){
			toBeCached = new CachedTile((Point)(tile.point.clone()),fragments);
			this.tiles.put(index,toBeCached);
			increaseSpaceUsed(spaceNeeded);
		}
		//it cannot be done with just contains due to equality constraint (it has to be both x,y and probability same) ... :/
		if (!queueContains(toBeCached) && tiles.containsKey(toBeCached.point.hashCode())){
			toBeCached = new CachedTile((Point)(tile.point.clone()),fragments);
			//System.out.println(toBeCached.fragmentsToString());
			toBeCached.probability = tile.carryingProbability;
			this.queue.add(toBeCached);
		}
		else {
			//System.out.println("bika");
			CachedTile cTile = this.tiles.get(index);
			//System.out.println(cTile);
			//System.out.println(this.queue.size());
			queueRemove(cTile);
			//System.out.println(this.queue.size());
			System.out.println(cTile.fragmentsToString());
			toBeCached = new CachedTile((Point)(tile.point.clone()),fragments);
			toBeCached.probability = tile.carryingProbability;
			System.out.println(toBeCached.fragmentsToString());
			if (!queueContains(toBeCached) && tiles.containsKey(toBeCached.point.hashCode())){
				this.queue.add(toBeCached);
			}
		}
		
	
		
		
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
	
	private void queueRemove(CachedTile toBeCached){
		Iterator<CachedTile> iter = this.queue.iterator();
		while(iter.hasNext()){
			CachedTile cTile = iter.next();
			if (cTile.id == toBeCached.id){
				iter.remove();
			}
		}
	}
	
		
	
	
	public boolean isFull(){

		if (sizeBeingUsed() >= CACHE_SIZE ){
			return true;
		}
		else {
			return false;
		}
	}
	
	public int howManyTiles(){
		return tiles.size();
	}
	
	public int sizeBeingUsed() {
		Iterator<Integer> iterKeys = this.tiles.keySet().iterator();
		int total = 0;
		while(iterKeys.hasNext()){
			CachedTile cTile = this.tiles.get(iterKeys.next());
			int fragm = cTile.getCachedFragmentsNum();
			total+=fragm;
		}
		return total;
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
		double start = System.currentTimeMillis();
		CachedTile cTile = this.getTile(point.hashCode());
		double total = System.currentTimeMillis() - start;
		System.out.println("Cache: "+total+" msecs");
		return cTile;
	}
	
	public CachedTile fetchTile(int hash,UserMove caller){
		CachedTile cachedTile = tiles.get(hash);
		if (cachedTile!=null){
			caller.cacheHits += cachedTile.getCachedFragmentsNum();
			UserMove.totalCacheHits+=cachedTile.getCachedFragmentsNum();
		}
		return getTile(hash);
	}
	
	private CachedTile getTile(int hash){
		return this.tiles.get(hash);
	}
	
	
	
	
	
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
		Iterator<CachedTile> iter = this.queue.iterator();
		int evictedFragments = 0;
		while(iter.hasNext() && evictedFragments<fragmentsNeeded){
			CachedTile lessLikelyTile = iter.next();
			//if it is the same that we are currentlyPrefetching 
			if (lessLikelyTile.point.equals(currentPoint)){
				System.err.println("ELEOS to idio");
			}
			CachedTile toBeCached = this.tiles.get(currentPoint.hashCode());
			if (toBeCached!=null){ //if already cached 
				if (toBeCached.probability < lessLikelyTile.probability){  //and has less probability than the anything in the cache
					System.err.println("ELEOS cache degradation");
				
				}
			}
			int fragmentsEvicted = evictTile(lessLikelyTile,iter);
			evictedFragments+=fragmentsEvicted;
			
			
		}
		//System.out.println(queue);
		return evictedFragments;
	}
	
	
	
	
	public int evictTile(CachedTile cTile,Iterator<CachedTile> iter){
		this.tiles.remove(cTile.point.hashCode());
		iter.remove();
		int numFragmentsCached = cTile.getCachedFragmentsNum();
		decreaseSpaceUsed(numFragmentsCached);
		return numFragmentsCached;
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
		if ((CACHE_SIZE-sizeBeingUsed())>=fragments){
			
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
			queueRemove(cTile);
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
			if (!queueContains(cTile) && this.tiles.containsKey(cTile.point.hashCode())){
				this.queue.add(cTile);
			}
		}
		
		
		
		Iterator<Node> iter = list.iterator();
		while(iter.hasNext()){
			Node node = iter.next();
			if (this.tiles.containsKey(node.point.hashCode())){
				
				CachedTile cTile = this.tiles.get(node.point.hashCode());
				//IMPORTANT remove before the equality is busted because of change in probability
				//this.queue.remove(cTile);
				queueRemove(cTile);
				cTile.probability = node.probability;
				//cTile.distance = Predictor.distance(cTile.point, currentPosition);
				//cTile.data = new String[]{"da","dasd",null,null,null,null,null,null};	
				if (!queueContains(cTile) && this.tiles.containsKey(cTile.point.hashCode()) ){
					this.queue.add(cTile);
				}
			}	
		}
		
		

		System.out.println("Updated Memory because of Prediction"+this.queue);
	}
}
