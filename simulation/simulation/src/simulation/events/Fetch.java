package simulation.events;

import java.util.Vector;

import simulation.Main;
import simulation.Point;
import simulation.Viewport;

public class Fetch extends Event{
	Viewport viewport;
	public Fetch(Viewport viewport){
		this.viewport = viewport;
	}
	public void action() throws Exception{
		System.out.println("Fetch Event");
		Vector<Point> vec = Main.db.viewportFetch(viewport);
		for (Point point : vec){
			//if point is for a tile to be fetch 100%
			if (point.fragmentNums==null){
				this.sendEvent(new TileFetch(point));
			}
			else{
				this.sendEvent(new FragmentedTileFetch(point));
			}
			
		}
	}
}
