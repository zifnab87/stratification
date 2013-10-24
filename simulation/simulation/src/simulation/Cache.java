package simulation;

import static simulation.Config.FRAGMENTS_PER_TILE;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.PriorityBlockingQueue;
//TA 8aria exoun lathos pliroforia mesa 
//ta sizes tis cache einai inconsistent (pali)

public class Cache {
	//tiles
	//fragments
	public Map<Integer,Tile> tiles = new HashMap<Integer, Tile>();
	public PriorityBlockingQueue<Tile> queue= new PriorityBlockingQueue<Tile>(10,Tile.likelihoodComparator);
	private int SpaceBeingUsed = 0;
	
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
	
	
	public void refresh(Tile tile){
		
		//updates the LOD
		int oldLOD = tile.lod;
		
		int newLOD = Predictor.likelihoodToLOD(tile.likelihood);
		System.out.println("oldLOD "+oldLOD+" newLOD"+newLOD);
		tile.lod = newLOD;
		
		//changes fragment number based on LOD
		if (oldLOD > newLOD){
			Vector<Integer> fragmNums = Tile.getFragmentsToBeRemoved(oldLOD, newLOD);
			
			for (int fragmNum : fragmNums){
				evictFragment(tile.id, fragmNum);
			}
			System.out.println("oldLOD "+oldLOD+" newLOD "+newLOD+"newLOD "+tile.getFragmentNumber()+" fragmentsRemoved "+fragmNums);
		}
		
		
		//refresh repositions tile based on the new likelihood
		queue.remove(tile);
		queue.add(tile);
	}
	
	public void cacheFullTile(Tile tile){
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
		Tile tile = this.tiles.get(tileId);
		tiles.remove(tileId);
		queue.remove(tile);
		decreaseSpaceUsed(FRAGMENTS_PER_TILE);
	}*/
	
	public void evictFragmentedTile(Point index){
		evictFragmentedTile(index.hashCode());
	}
	
	public void evictFragmentedTile(int tileId){
		Tile tile = this.tiles.get(tileId);
		int fragmentCount = tile.getFragmentNumber();
		tiles.remove(tileId);
		queue.remove(tile);
		decreaseSpaceUsed(fragmentCount);
		
	}
	
	
	public void evictFragment(int tileId,int fragmNumber){
		Tile tile = this.tiles.get(tileId);
		int fragmCount = tile.getFragmentNumber();
		if (fragmCount>0){
			if(tile.containsFragment(fragmNumber)){
				tile.removeFragment(fragmNumber);
				decreaseSpaceUsed(1);
			}
		}
		fragmCount = tile.getFragmentNumber();
		//in case that was the last fragment of the tile
		if (fragmCount==0){
			queue.remove(tile);
			tiles.remove(tileId);
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
			tile.addFragment(fragm);
			if (tile.likelihood==-1){
				tile.likelihood = likelihood;
				
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
		
	}
	
	private void updateTileLikelihoodOfIndex(Point index,Viewport currentViewport){
		Tile tile = tiles.get(index.hashCode());
		double newLikelihood =  Predictor.calculateLikelihood(index, currentViewport);
		//if likelihood became 0.0 (the only case that LOD=0)
	    // then remove the tile 
		if (newLikelihood == 0.0d){
			
			evictFragmentedTile(index);
		}
		else {
			tile.likelihood = newLikelihood;
			refresh(tile);
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
		Iterator<Tile> iter = queue.iterator();
		Tile head = queue.peek();
		if (head!=null){
			result+="\nHEAD:"+head.toString()+": ("+head.getFragmentNumber()+"): fragments[";
		    for(int index : head.fragments.keySet()){
		    	result+=head.getFragment(index).num+",";
		    }
		    result+="]\n";
		}
		while(iter.hasNext()){
			Tile tile = iter.next();
			result+=tile.toString()+": ("+tile.getFragmentNumber()+"): fragments[";
		    for(int index : tile.fragments.keySet()){
		    	result+=tile.getFragment(index).num+",";
		    }
		    result+="]\n";
		}
		return result;
	}
	

	
}
