package simulation;

import java.util.HashMap;
import java.util.Map;

public class Database {
	
	
	private Map<Integer,Tile> tiles = new HashMap<Integer, Tile>();
	
	public Database(){
		
	}
	
	
	public void init(int numTiles){
		for (int i=0; i<numTiles; i++){
			Tile tile = Tile.random();
			putTile(tile);
		}
	}
	
	public void putTile(Tile tile){
		tileExists(tile.id);
		tiles.put(tile.id, tile);
	}
	
	public void tileExists(int tileId){
		if (tiles.containsKey(tileId)){
			System.out.println("error");
		}
	}
	
	public Tile getTile(int tileId){
		Tile tile = tiles.get(tileId);
		return tile;
				
			
	}
	
	
	
	public Fragment getFragmentOfTile(int FragmentNumber,int tileId){
		Tile tile = tiles.get(tileId);
		return tile.getFragment(FragmentNumber);
	}
}
