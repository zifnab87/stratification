package simulation.events;

import simulation.Main;
import simulation.Point;
import simulation.Tile;
import simulation.monitor.Monitor;

public class TilePrefetch extends Event {

	Point pointToPrefetch;
	public TilePrefetch(Point pointToPrefetch){
		this.pointToPrefetch = pointToPrefetch;
	}
	@Override
	public void action() throws Exception {
		System.out.println(this);
		Tile tile = Main.db.getTile(this.pointToPrefetch);
		if (tile == null){
			tile = Main.db.getTile(this.pointToPrefetch);
			Main.cache.addTile(tile);
			Monitor.databaseTileFetch();
		}
		else {
			Monitor.cacheTileFetch();
			System.out.println("Tile cached! (Prefetch)");
		}

	}
	
	public String toString(){
		return "Tile PreFetch Event for point"+this.pointToPrefetch.toString();
		
	}

}
