package simulation.events;

import java.util.Vector;

import simulation.Main;
import simulation.Point;
import simulation.Viewport;

public class Prefetch extends Event{
	Viewport viewport;
	public Prefetch(Viewport viewport){
		this.viewport = viewport;
	}
	public void action() throws Exception{
		
		Vector<Point> vec = Main.db.aroundViewportPrefetch(viewport);
		//System.out.println("Prefetch Event "+vec);
		for (Point point : vec){
			//if point is for a tile to be fetch 100%
			if (point.fragmentNums==null){
				
				this.sendEvent(new TilePrefetch(point));
			}
			else{
				this.sendEvent(new FragmentedTilePrefetch(point));
			}
			
		}
	}
	

}
