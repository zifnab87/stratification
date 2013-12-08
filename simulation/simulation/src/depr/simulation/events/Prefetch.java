package depr.simulation.events;

import java.util.Vector;

import depr.simulation.Main;
import depr.simulation.Point;
import depr.simulation.Viewport;


public class Prefetch extends Event{
	Viewport viewport;
	public Prefetch(Viewport viewport){
		this.viewport = viewport;
	}
	public void action() throws Exception{
		
		Vector<Point> vec = Main.db.aroundViewportPrefetch(viewport);
		for (Point point : vec){
			//if point is for a tile to be fetch 100%
			if (point.fragmentNums==null){
				Event.sendEvent(new TilePrefetch(point));
			}
			else{
				Event.sendEvent(new FragmentedTilePrefetch(point));
			}
			
		}
	}
	

}