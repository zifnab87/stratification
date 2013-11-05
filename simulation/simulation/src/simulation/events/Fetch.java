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

	/*	Vector<Point> vec = Main.db.viewportFetch(viewport);
		//System.out.println("Fetch Event "+vec);
		for (Point point : vec){
			//if point is for a tile to be fetch 100%
			if (point.fragmentNums==null){
				Event.sendEvent(new TileFetch(point));
			}
			else{
				Event.sendEvent(new FragmentedTileFetch(point));
			}
			
		}*/
	}
}
