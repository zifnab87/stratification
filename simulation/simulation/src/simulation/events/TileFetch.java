package simulation.events;

import simulation.Main;
import simulation.Point;
import simulation.Tile;
import simulation.monitor.Monitor;
import static simulation.Config.debug;

public class TileFetch extends Event {

	Point pointToFetch;
	public TileFetch(Point pointToFetch){
		this.pointToFetch = pointToFetch;
	}
	
	public void action() throws Exception {
		
		if (!Main.cache.tileExists(this.pointToFetch)){
			Tile tile = Main.db.getTile(this.pointToFetch);
			if (debug){
				System.out.println(this);
				System.out.println(tile);
			}
			if (tile!=null){
				Main.cache.addFullTile(tile);
				if (debug){
					//System.out.println("put in cache");
				}
			}
			Monitor.databaseTileFetch();
		}
		else {
			Monitor.cacheTileFetch();
			if(debug){
				//System.out.println("Tile fetched from Cache! (Fetch)");
			}
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
