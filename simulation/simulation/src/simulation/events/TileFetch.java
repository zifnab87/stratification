package simulation.events;

import simulation.Main;
import simulation.Point;
import simulation.Tile;

public class TileFetch extends Event {

	Point pointToFetch;
	public TileFetch(Point pointToFetch){
		this.pointToFetch = pointToFetch;
	}
	
	public void action() throws Exception {
		System.out.println("TileFetch Event");
		Tile tile = Main.db.getTile(this.pointToFetch);
		//tile render TODO
		
		//tile cache
		Main.cache.addTile(tile);
		
		//this.sendEvent(new TileFetchFinished());
	}

}
