package ver3.simulation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

import ver3.simulation.CachedTile;

public class Cache {
	
	
	private  Map<Integer,CachedTile> tiles = new HashMap<Integer, CachedTile>();
	private TreeSet<CachedTile> queue;
	
	
	
	private TreeSet<CachedTile> updateQueue(){
		this.queue = new TreeSet<CachedTile>(CachedTile.importanceComparator);
		Iterator<Integer> iter = tiles.keySet().iterator();
		while(iter.hasNext()){
			this.queue.add(tiles.get(iter.next()));
		}
		return this.queue;
	}
	
	/*private CachedTile queueFind(int hashCode) {
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
	}*/
	
	
}
