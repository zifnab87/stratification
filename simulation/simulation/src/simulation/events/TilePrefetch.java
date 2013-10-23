package simulation.events;

import simulation.Main;
import simulation.Point;
import simulation.Tile;
import simulation.monitor.Monitor;
import static simulation.Config.debug;

public class TilePrefetch extends Event {

	Point pointToPrefetch;
	public TilePrefetch(Point pointToPrefetch){
		this.pointToPrefetch = pointToPrefetch;
	}
	@Override
	public void action() throws Exception {
		
	
		if (!Main.cache.tileExists(this.pointToPrefetch)){
			Tile tile = Main.db.getTile(this.pointToPrefetch);
			Main.cache.cacheFullTile(tile);
			Monitor.databaseTileFetch();
			if (debug){
				System.out.println(this);
			}
		}
		else {
			Monitor.cacheTileFetch();
			//System.out.println("Tile fetched from Cache! (Prefetch)");
		}

	}
	
	public String toString(){
		return "Tile PreFetch Event for point"+this.pointToPrefetch.toString();
		
	}

}
