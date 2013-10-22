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

		for (int y=upperLeft.y-PREFETCH_DISTANCE; y<lowerRight.y+PREFETCH_DISTANCE; y++){
			for (int x=upperLeft.x-5; x<lowerRight.x+5; x++){
				if (x<0 || y<0) continue;
				Vector<Integer> fragmentNums = new Vector<Integer>();
				Point index = new Point(y,x);
				index.fragmentNums = fragmentNums;
				int LOD = Predictor.getLOD(index,viewport);
				//if tile doesn't exist in cache
				if (!Main.cache.tileExists(index)){
					
					if (FRAGMENT){
						Main.cache.fulfillLODfromScratch(index,LOD);
					}
					vec.add(index);

				}
				//if tile partially exists in cache
				else if(!Main.cache.tileExistsAndNotFull(index)){
					index.setFragmentNums(fragmentNums);
					Main.cache.fullfillLODfromOldLOD(index, LOD);
					vec.add(index);
				}
			}
		}
		//remove what is in the viewport
		for (int y=upperLeft.y; y<lowerRight.y; y++){
			for (int x=upperLeft.x; x<lowerRight.x; x++){
				Point index = new Point(y,x);
				vec.remove(index);
			}
		}
		return vec;
	}
	
	public Vector<Point> viewportFetch(Viewport viewport){
		Point upperLeft = viewport.upperLeft;
		Point lowerRight = viewport.lowerRight;
		Vector<Point> vec = new Vector<Point>();
		Vector<Integer> fragmentNums = new Vector<Integer>();
		for (int y=upperLeft.y; y<lowerRight.y; y++){
			for (int x=upperLeft.x; x<lowerRight.x; x++){
				Point index = new Point(y,x);
				//if tile doesn't exist in cache
				if (!Main.cache.tileExists(index)){
					vec.add(index);
				}
				//if tile partially exists request missing fragments
				else if(!Main.cache.tileExistsAndNotFull(index) && !FRAGMENT){
					index.setFragmentNums(fragmentNums);
					Main.cache.putMissingFragments(index);
					vec.add(index);
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
