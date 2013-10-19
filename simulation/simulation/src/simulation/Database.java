package simulation;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import simulation.monitor.Monitor;
import static simulation.Config.FRAGMENTS_PER_TILE;
import static simulation.Config.PREFETCH_DISTANCE;

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
					
					if (LOD < FRAGMENTS_PER_TILE){
						for (int fragmNum=1; fragmNum<=LOD; fragmNum++){
							//if fragment doesn't exist request fetch from database;
							if(!index.fragmentNums.contains(fragmNum)){
								index.fragmentNums.add(fragmNum);
							}
						}
					}
					vec.add(index);

				}
				//if tile exists but partial
				else if(!Main.cache.tileExistsAndNotFull(index)){
					Tile cachedPartialTile = Main.cache.getTile(index);
					index.setFragmentNums(fragmentNums);
					int oldLOD = cachedPartialTile.getFragmentNumber();
					if (oldLOD<LOD){
						for (int fragmNum=oldLOD+1; fragmNum<=LOD; fragmNum++){
							//if fragment doesn't exist request fetch from database;
							if(!index.fragmentNums.contains(fragmNum)){
								index.fragmentNums.add(fragmNum);
							}
						}
					}
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
				//if tile exists but partial
				else if(!Main.cache.tileExistsAndNotFull(index)){
					Tile cachedPartialTile = Main.cache.getTile(index);
					index.setFragmentNums(fragmentNums);
					for (int fragmNum=1; fragmNum<=FRAGMENTS_PER_TILE; fragmNum++){
						//if fragment doesn't exist request fetch from database;
						if (!cachedPartialTile.containsFragment(fragmNum)){
							//and not already in there
							if (index.fragmentNums.contains(fragmNum)){
								index.fragmentNums.add(fragmNum);
							}
						}
					}
					vec.add(index);
				}
				
			}
		}
		/*for(Tile tile: vec){
			Main.cache.addTile(tile);
		}*/
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
