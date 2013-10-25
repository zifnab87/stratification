package simulation;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import simulation.monitor.Monitor;
import static simulation.Config.FRAGMENTS_PER_TILE;
import static simulation.Config.PREFETCH_DISTANCE;
import static simulation.Config.FRAGMENT;

public class Database {
	
	public Map<Integer,Tile> tiles = new HashMap<Integer, Tile>();
	public Viewport viewport;
	
	
	
	
	public Database(){
		
	}
	
	
	public void setViewport(Viewport viewport){
		this.viewport = viewport;
	}
	
	public Vector<Point> aroundViewportPrefetch(Viewport viewport){
		Point upperLeft = viewport.upperLeft;
		Point lowerRight = viewport.lowerRight;
		Vector<Point> vec = new Vector<Point>();

		for (int y=upperLeft.y-PREFETCH_DISTANCE; y<=lowerRight.y+PREFETCH_DISTANCE; y++){
			for (int x=upperLeft.x-PREFETCH_DISTANCE; x<=lowerRight.x+PREFETCH_DISTANCE; x++){
				if (x<0 || y<0) continue;
				//don't put anything that is in the viewport
				if (y>=upperLeft.y && y<=lowerRight.y && x>=upperLeft.x && x<=lowerRight.x){
					continue;
				}
				Vector<Integer> fragmentsNeeded;
				Point index = new Point(y,x);
				int LOD;
				double likelihood;
				likelihood = Predictor.calculateLikelihood(index, viewport);
				if (FRAGMENT){
					
					LOD = Predictor.likelihoodToLOD(likelihood);
				}
				else {
					LOD = FRAGMENTS_PER_TILE;
				}
				
				index.carriedLikeliood = likelihood;
				//index.LOD = LOD;
				//System.out.println(LOD);
				//if tile doesn't exist in cache
				//if (index.equals(new Point(5,2)) ){
					//System.out.println("!!!!!"+index.carriedLikeliood+" "+index);
					//System.out.println(Main.cache.tileExistsAndNotFull(index));
					//System.out.println(Main.cache.tileExists(index));
					//System.out.println(Main.cache.getTile(index).getFragmentNumber());
				//}
				if (LOD == FRAGMENTS_PER_TILE) {// the tile is needed to be full
					
					if (Main.cache.tileExistsAndFull(index)){
						Monitor.cacheTileFetch();

					}
					else if(Main.cache.tileExistsAndNotFull(index)){
						//find what's missing
						Tile cachedPartialTile = Main.cache.getTile(index);
						int cachedLOD = cachedPartialTile.lod;
						fragmentsNeeded = Tile.getMissingFragmentIdsTillFull(cachedLOD);
						index.setFragmentNums(fragmentsNeeded);
						
						//System.out.println("!!!2!!!"+index.carriedLikeliood+" "+index);
						vec.add(index);
						//that many were cached
						int cachedFragmentsNum = FRAGMENTS_PER_TILE - fragmentsNeeded.size();
						for (int i=1; i<cachedLOD; i++){
							Monitor.cacheFragmentFetch();
						}

					}
					else { //tile doesn't exist in Cache
						vec.add(index); // full Database Fetch
					}
				}
				else if (LOD>0 && LOD<FRAGMENTS_PER_TILE){ //the tile doesn't need to be full
					if (Main.cache.tileExists(index)){
						Tile cachedPartialTile = Main.cache.getTile(index);
						int cachedLOD = cachedPartialTile.lod;
						if (cachedLOD <= LOD){
							fragmentsNeeded = Tile.getMissingFragmentIdsTillLOD(cachedLOD,LOD);
							index.setFragmentNums(fragmentsNeeded);
							//System.out.println("!!!3!!!"+index.carriedLikeliood+" "+index);
							vec.add(index);
						}
						else {// cachedLOD > LOD THIS SHOULDN'T HAPPEN WHEN we update the cache with each User Move
							
						}
						for (int i=1; i<=LOD; i++){
							Monitor.cacheFragmentFetch();
						}
					}
					else { //Tile doesn't exist and it is partially needed from Database
						fragmentsNeeded = Tile.getMissingFragmentIdsTillLOD(0, LOD);
						index.setFragmentNums(fragmentsNeeded);
						//System.out.println("!!!4!!!"+index.carriedLikeliood+" "+index);
						vec.add(index);
					}
				}
			}				
		}
		return vec;
	}
	
	public Vector<Point> viewportFetch(Viewport viewport){
		Point upperLeft = viewport.upperLeft;
		Point lowerRight = viewport.lowerRight;
		Vector<Point> vec = new Vector<Point>();
		Vector<Integer> fragmentsNeeded = null;
		for (int y=upperLeft.y; y<=lowerRight.y; y++){
			for (int x=upperLeft.x; x<=lowerRight.x; x++){
				Point index = new Point(y,x);
				//if tile doesn't exist in cache
				if (!Main.cache.tileExists(index)){
					vec.add(index);  // full Database Fetch
				}
				//if tile partially exists request missing fragments
				
				else if(Main.cache.tileExistsAndNotFull(index)){
					Tile cachedPartialTile = Main.cache.getTile(index);
					int cachedLOD = cachedPartialTile.lod;
					fragmentsNeeded = Tile.getMissingFragmentIdsTillFull(cachedLOD);
					index.setFragmentNums(fragmentsNeeded);
					vec.add(index);
					for (int i=1; i<=cachedLOD; i++){
						Monitor.cacheFragmentFetch();
					}
				}
				else { // tileExistsAndFull == true
					Monitor.cacheTileFetch();
				}
				
			}
		}

		return vec;
	}
	
	public void init(int numTiles){
		for (int i=0; i<numTiles; i++){
			Tile tile = Tile.randomizer();
			putTile(tile);
		}
	}
	
	public void putTile(Tile tile){
		tiles.put(tile.id, tile);
	}
	
	public boolean tileExists(Point index){
		return tiles.containsKey(index.hashCode());
	}
	
	public Tile getTile(Point index){
		Tile tile = tiles.get(index.hashCode());
		return tile;		
	}
	
	
	public Fragment getFragmentOfTile(int fragmentNumber,Point index){
		if (tileExists(index)){
			Tile tile = getTile(index);
		
			return tile.getFragment(fragmentNumber);
		}
		else {
			System.out.println("tile Doesn't exist for fragment");
			return null;
		}
	}
	
	/*public Fragment getFragmentOfTile(int fragmentNumber,int tileId){
		Tile tile = getTile(tileId);
		if (tile!=null){
			return tile.getFragment(fragmentNumber);
		}
		return null;
	}*/
}
