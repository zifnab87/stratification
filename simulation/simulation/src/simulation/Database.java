package simulation;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import static simulation.Config.FRAGMENTS_PER_TILE;

public class Database {
	
	private Map<Integer,Tile> tiles = new HashMap<Integer, Tile>();
	public Viewport viewport;
	
	
	
	
	public Database(){
		
	}
	
	
	public void setViewport(Viewport viewport){
		this.viewport = viewport;
	}
	
	public Vector<Tile> viewportFetch(){
		Point upperLeft = this.viewport.upperLeft;
		Point lowerRight = this.viewport.lowerRight;
		Vector<Tile> vec = new Vector<Tile>();
		for (int y=upperLeft.y; y<=lowerRight.y; y++){
			for (int x=upperLeft.x; y<=lowerRight.x; y++){
				Point index = new Point(y,x);
				//if tile doesn't exist in cache
				if (!Main.cache.tileExists(index)){
					vec.add(getTile(index));
				}
				//if tile exists but partial
				else if(!Main.cache.tileExistsAndNotFull(index)  ){
					Tile fullfillingTile = new Tile(index);
					Tile cachedPartialTile = Main.cache.getTile(index);
					for (int fragmNum=1; fragmNum<=FRAGMENTS_PER_TILE; fragmNum++){
						//if fragment doesn't exist fetch it from database;
						if (!cachedPartialTile.containsFragment(fragmNum)){
							fullfillingTile.addFragment(getFragmentOfTile(fragmNum, index));
						}
					}
					vec.add(fullfillingTile);
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
