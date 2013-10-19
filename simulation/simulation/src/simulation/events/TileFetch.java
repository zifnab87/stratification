package simulation.events;

import simulation.Main;
import simulation.Point;
import simulation.Tile;
import simulation.monitor.Monitor;

public class TileFetch extends Event {

	Point pointToFetch;
	public TileFetch(Point pointToFetch){
		this.pointToFetch = pointToFetch;
	}
	
	public void action() throws Exception {
		
		//TODO get rid off the Main.cache.tileExists because it actually calls getTile
		if (!Main.cache.tileExists(this.pointToFetch)){
			Tile tile = Main.db.getTile(this.pointToFetch);
			
			System.out.println(this);
			System.out.println(tile);
			if (tile!=null){
				Main.cache.addTile(tile);
				System.out.print("put in cache");
			}
			Monitor.databaseTileFetch();
		}
		else {
			Monitor.cacheTileFetch();
			System.out.println("Tile cached! (Fetch)");
		}
		//tile render TODO
		
		//tile cache
		
	}
	
	public String toString(){
		return "TileFetch Event for point"+this.pointToFetch.toString();
		
	}
	
	public boolean equals(Object o){
		System.out.println(this.pointToFetch+" vs "+((TileFetch)o).pointToFetch+"  "+this.pointToFetch.equals(((TileFetch)o).pointToFetch));
		if (this.pointToFetch.equals(((TileFetch)o).pointToFetch)){
			return true;
		}
		else {
			return false;
		}
		
	}

}
