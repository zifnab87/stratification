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
		
		//TODO get rid off the Main.cache.tileExists because it actually calls getTile
		Tile tile = Main.cache.getTile(this.pointToFetch);
		if (tile == null){
			tile = Main.db.getTile(this.pointToFetch);
			System.out.println(this);
			Main.cache.addTile(tile);
		}
		else {
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
