package sync.simulation;

import static sync.simulation.Config.CACHE_SIZE;
import static sync.simulation.Config.FRAGMENTS_PER_TILE;
import static sync.simulation.Config.FRAGMENT_SIZE;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.TreeSet;
import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;

import sync.simulation.Main;
import sync.simulation.Point;
import sync.simulation.Tile;
import sync.simulation.Viewport;
import sync.simulation.events.UserMove;
import sync.simulation.predictor.Node;
import sync.simulation.predictor.Predictor;

import sync.simulation.predictor.Tuple;
import sync.simulation.regions.UserStudiesCombined;
import util.Util;

public class Cache {
	//public volatile Map<Integer,Tile> tiles = new HashMap<Integer, Tile>();
	//public volatile PriorityBlockingQueue<Tile> queue= new PriorityBlockingQueue<Tile>(10,Tile.likelihoodComparator);
	
	public  Map<Integer,CachedTile> tiles = new HashMap<Integer, CachedTile>();
	//public volatile LinkedList<CachedTile> queue = new PriorityBlockingQueue<CachedTile>(10,Tile.probabilityComparator);
	public TreeSet<CachedTile> queue = new TreeSet<CachedTile>(CachedTile.probabilityComparator);
	
	
	public TreeSet<CachedTile> getQueue(){
		return queue;
	}
	
	
	public int getTilesOccupied(){
		return tiles.size();
	}
	
	public int getQueueSize(){
		return queue.size();
	}
	
	
	public int SpaceBeingUsed = 0;
	
//	public void warmUp(){
//		
//		int start = 10000;
//		int count = 0;
//		while(CACHE_SIZE!=this.sizeBeingUsed()){
//			String data[] = new String[FRAGMENTS_PER_TILE];
//			
//			for(int j=0; j<Math.min(FRAGMENTS_PER_TILE,(CACHE_SIZE-this.sizeBeingUsed())); j++){
//				String datum = "[";
//				for (int pixel=0; pixel<FRAGMENT_SIZE; pixel++){
//					int red = new Random().nextInt(255);
//					int green = new Random().nextInt(255);
//					int blue = new Random().nextInt(255);
//					datum += "["+red+","+green+","+blue+"],";
//				}
//				datum +="]";
//				data[j]=datum;
//			}
//			Point point  = new Point((start+count),(start+count),true);
//			CachedTile cTile = new CachedTile(point, data);
//			cTile.distance =  10000;
//			tiles.put(point.hashCode(), cTile);
//			queue.add(cTile);
//			//increaseSpaceUsed(FRAGMENTS_PER_TILE);
//			count++;
//		}
//		
//	}
	
//	public CachedTile cacheFullTile(Tile tile){
//		return cacheTileWithFragmentRange(tile, 1, FRAGMENTS_PER_TILE);
//	}
	
	/*public CachedTile cacheFragment(Tile tile, int fragmentNum){
		return cacheTileWithFragmentRange(tile, fragmentNum, fragmentNum);
	}*/
	
	
	
	public CachedTile cacheTileWithFragmentRange(Tile tile,Point current,int firstFragment, int lastFragment){
		int spaceNeeded = lastFragment - firstFragment + 1;
		while(!this.hasAvailableSpace(spaceNeeded)){
			Util.debug("space still Needed "+spaceNeeded);
			int diff = this.makeSpaceAvailable(spaceNeeded,tile.point);
			spaceNeeded -= diff;
			
		}
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
			//Util.debug(toBeCached.fragmentsToString());
			UserStudiesCombined.tiles[toBeCached.point.y][toBeCached.point.x].updateImportance(current);
			toBeCached.totalImportance = UserStudiesCombined.tiles[toBeCached.point.y][toBeCached.point.x].totalImportance;
			this.queue.add(toBeCached);
		}
		else {
		
			CachedTile cTile = queueFind(index);
			//Util.debug(cTile);
			//Util.debug(this.queue.size());
			queueRemove(cTile);
			//Util.debug(this.queue.size());
			Util.debug(cTile.fragmentsToString());
			toBeCached = new CachedTile((Point)(tile.point.clone()),fragments);
			UserStudiesCombined.tiles[toBeCached.point.y][toBeCached.point.x].updateImportance(current);
			toBeCached.totalImportance = UserStudiesCombined.tiles[toBeCached.point.y][toBeCached.point.x].totalImportance;
			Util.debug(toBeCached.fragmentsToString());
			if (!queueContains(toBeCached) && tiles.containsKey(toBeCached.point.hashCode())){
				this.queue.add(toBeCached);
			}
		}
		
	
		
		
		return toBeCached;
		
	}
	private CachedTile queueFind(int hashCode) {
		boolean found = false;
		Iterator<CachedTile> iter = this.queue.iterator();
		while(iter.hasNext()){
			CachedTile cTile = iter.next();
			if (cTile.id == hashCode){
				found = true;
				return cTile;
			}
		}
		return null;
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
		if (this.tiles.size()!=this.queue.size()){
			System.err.println("memory inconsistency");
		}
		/*Iterator<Integer> iterKeys = this.tiles.keySet().iterator();
		int total = 0;
		while(iterKeys.hasNext()){
			CachedTile cTile = this.tiles.get(iterKeys.next());
			int fragm = cTile.getCachedFragmentsNum();
			total+=fragm;
		}*/
		int total = 0;
		Iterator<CachedTile> iter = this.queue.iterator();
		while (iter.hasNext()){
			//CachedTile cTile = this.tiles.get(iter.next().point.hashCode());
			CachedTile cTile = iter.next();
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
		Util.debug("Cache: "+total+" msecs");
		return cTile;
	}
	
	public CachedTile fetchTile(int hash,UserMove caller){
		CachedTile cachedTile = queueFind(hash);
		if (cachedTile!=null){
			caller.cacheHits += cachedTile.getCachedFragmentsNum();
			caller.run.totalCacheHits+=cachedTile.getCachedFragmentsNum();
		}
		return getTile(hash);
	}
	
	private CachedTile getTile(int hash){
		return queueFind(hash);
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
		//Util.debug("I need"+fragmentsNeeded);
		Iterator<CachedTile> iter = this.queue.iterator();
		int evictedFragments = 0;
		boolean prob1 = false;
		boolean prob2 = false;
		while(/*iter.hasNext() &&*/ evictedFragments<fragmentsNeeded){
			CachedTile lessLikelyTile = this.getWorst();// iter.next();
			//if it is the same that we are currentlyPrefetching 
			if (lessLikelyTile.point.equals(currentPoint)){
				//System.err.println("ELEOS to idio");
				prob1 = true;
			}
			CachedTile toBeCached = queueFind(currentPoint.hashCode());
			if (toBeCached!=null){ //if already cached 
				if (toBeCached.totalImportance < lessLikelyTile.totalImportance){  //and has less probability than the anything in the cache
					System.err.println("ELEOS cache degradation");
					prob2 = true;
				
				}
			}
			if (prob1){
				System.err.println("ELEOS to idio");
			}
			if (prob2){
				System.err.println("ELEOS cache degradation");
			}
			
			
			if (prob1 || prob2) {
				prob1 = false;
				prob2 = false;
				//continue;
				
			}
			
			int fragmentsEvicted = evictTile(lessLikelyTile,iter);
			updateImportances(currentPoint);
			evictedFragments+=fragmentsEvicted;
			
			
		}
		//Util.debug(queue);
		return evictedFragments;
	}
	
	
	
	
	public int evictTile(CachedTile cTile,Iterator<CachedTile> iter){
		
		this.tiles.remove(cTile.point.hashCode());
		queueRemove(cTile);
		//iter.remove();
		int numFragmentsCached = cTile.getCachedFragmentsNum();
		Util.debug("----------Evicted:"+ cTile.point+" it had:"+numFragmentsCached+"-------------");
		decreaseSpaceUsed(numFragmentsCached);
		return numFragmentsCached;
	}
	
	
	


	
	
	
	public String toString(){
		String result="[";
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
			result+=tile.fragmentsToString()+",\n";
		}
		result+="]\n";
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
	
	
	
	
	
	public void updateImportances(Point currentPosition){
		int size0 = this.sizeBeingUsed();
		Iterator<Integer> mapIter = this.tiles.keySet().iterator();
		while (mapIter.hasNext()){
			CachedTile cTile = queueFind(mapIter.next());
			String[] data = cTile.data;
			
			//IMPORTANT remove before the equality is busted because of change in probability
			
			queueRemove(cTile);
			//we make probabilities zero so only the ones that will be updated by the 
			//prediction tree will have probability less than zero
			//if it is current we give it a probability of 1.0d
			
			if (currentPosition.equals(cTile.point)){
				cTile.totalImportance = 1000000;
				cTile.distance = 0;
				//continue;
			}
			else {
				UserStudiesCombined.tiles[cTile.point.y][cTile.point.x].updateImportance(currentPosition);
				cTile.totalImportance = UserStudiesCombined.tiles[cTile.point.y][cTile.point.x].totalImportance;
			
			}
			cTile.distance = Point.distance(cTile.point, currentPosition);
			if (!queueContains(cTile) && this.tiles.containsKey(cTile.point.hashCode())){
				this.queue.add(cTile);
			}
		}
		
		int size1 = this.sizeBeingUsed();
		
		if (size0!=size1){
			System.err.println("Memory size altered from Prediction");
		}

		Util.debug("Updated Memory because of Prediction "+this.queue);
		Util.debug("Memory Size because of Prediction "+this.sizeBeingUsed());
	}
	
	
	public CachedTile getWorst(){
		return this.queue.first();
	}
	
	/*
	
	
	//update cache probability based on the prediction tree
	public void updateProbabilities(Vector<Node> list,Point currentPosition){
		int size0 = this.sizeBeingUsed();
		Iterator<Integer> mapIter = this.tiles.keySet().iterator();
		while (mapIter.hasNext()){
			CachedTile cTile = queueFind(mapIter.next());
			String[] data = cTile.data;
//			if (currentPosition.equals(cTile.point)){
//				
//				continue;
//			}
			
			//IMPORTANT remove before the equality is busted because of change in probability
			
			queueRemove(cTile);
			//we make probabilities zero so only the ones that will be updated by the 
			//prediction tree will have probability less than zero
			//if it is current we give it a probability of 1.0d
			if (currentPosition.equals(cTile.point)){
				cTile.totalImportance = 1.0d;
				
			}
			else {
				cTile.totalImportance = 0.0d;
			}
			cTile.distance = Point.distance(cTile.point, currentPosition);
			if (!queueContains(cTile) && this.tiles.containsKey(cTile.point.hashCode())){
				this.queue.add(cTile);
			}
		}
		int size1 = this.sizeBeingUsed();
		//from the predictor
		Iterator<Node> iter = list.iterator();
		while(iter.hasNext()){
			Node node = iter.next();
			if (this.tiles.containsKey(node.point.hashCode())){
				
				CachedTile cTile = queueFind(node.point.hashCode());//this.tiles.get(node.point.hashCode());
				//IMPORTANT remove before the equality is busted because of change in probability;
				queueRemove(cTile);
				cTile.totalImportance = node.probability;
				if (!queueContains(cTile) && this.tiles.containsKey(cTile.point.hashCode()) ){
					this.queue.add(cTile);
				}
			}	
		}
		int size2 = this.sizeBeingUsed();
		
		if (size0!=size1 || size0!=size2 || size1!=size2){
			System.err.println("Memory size altered from Prediction");
		}

		Util.debug("Updated Memory because of Prediction "+this.queue);
		Util.debug("Memory Size because of Prediction "+this.sizeBeingUsed());
	}
	
	*/
}
