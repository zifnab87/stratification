package simulation;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import static simulation.Config.FRAGMENTS_PER_TILE;

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
		Vector<Integer> fragmentNums = new Vector<Integer>();
		for (int y=upperLeft.y-5; y<lowerRight.y+5; y++){
			for (int x=upperLeft.x-5; x<lowerRight.x+5; x++){
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
							index.fragmentNums.add(fragmNum);
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
		
		/*for(Tile tile: vec){
			Main.cache.addTile(tile);
		}*/
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
							index.fragmentNums.add(fragmNum);
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
	
	public boolean tileExists(int tileId){
		return tiles.containsKey(tileId);
	}
	
	public Tile getTile(Point index){
		Tile tile = tiles.get(index.hashCode());
		return tile;		
	}
	
	public Tile getTile(int tileId){
		Tile tile = tiles.get(tileId);
		return tile;		
	}
	
	public Fragment getFragmentOfTile(int fragmentNumber,Point index){
		
		return getFragmentOfTile(fragmentNumber,index.hashCode());
	}
	
	public Fragment getFragmentOfTile(int fragmentNumber,int tileId){
		Tile tile = getTile(tileId);
		return tile.getFragment(fragmentNumber);
	}
}
