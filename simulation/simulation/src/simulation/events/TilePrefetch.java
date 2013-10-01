package simulation.events;

import simulation.Main;
import simulation.Point;
import simulation.Tile;

public class TilePrefetch extends Event {

	Point pointToPrefetch;
	public TilePrefetch(Point pointToPrefetch){
		this.pointToPrefetch = pointToPrefetch;
	}
	@Override
	public void action() throws Exception {
		System.out.println("TilePrefetch Event");
		Tile tile = Main.db.getTile(this.pointToPrefetch);
		
		//tile cache
		if (tile!=null){
			Main.cache.addTile(tile);
		}

	}

}
