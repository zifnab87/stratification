package simulation;

import static simulation.Config.VIEWPORT_HEIGHT;
import static simulation.Config.VIEWPORT_WIDTH;
import static simulation.Config.UPPER_LEFT_STARTING_POINT;
import static simulation.Config.DATABASE_TILES_NUM;
import simulation.events.EventHandler;

import java.lang.reflect.Method;


public class Main {
	
	public static Database db = new Database();
	public static Cache cache = new Cache();
	public static Viewport viewport = new Viewport(VIEWPORT_HEIGHT,VIEWPORT_WIDTH,UPPER_LEFT_STARTING_POINT);
	
	
	public void sendEvent (final EventHandler handler, final Object event) throws Exception {
	    final Method method = EventHandler.class.getDeclaredMethod ("handle", new Class[] {event.getClass ()});
	    method.invoke (handler, event);
	}
	
	public static void main(String args[]){
		
		
		
		db.setViewport(viewport);
		db.init(DATABASE_TILES_NUM);
		db.viewportFetch();
		
		
		
		
		System.out.println(cache.tiles.size());
		System.out.println(((Tile)(cache.tiles.get(new Point(0,0).hashCode()))));
		//System.out.println("init done");
		//System.out.println(Predictor.likelihoodToLOD(1.0d));
		//Predictor.constantTrain(db);
		//Predictor.spiralTrain(db);
		//System.out.println("train done");
		//System.out.println(db.tiles.get(new Point(1,0).hashCode()).getLikelihood());
		//System.out.println(db.tiles.get(new Point(0,24).hashCode()).getLikelihood());
		
		//System.out.println(db.tiles.get(new Point(1,0).hashCode()).getLOD());
		//System.out.println(db.tiles.get(new Point(0,24).hashCode()).getLOD());
		Viewport view = new Viewport(4, 3, new Point(3,2));
		
		System.out.println(Predictor.distance(new Point(1,3),new Point(10,10)));
	}
	

		
		
		
		//user moved
		//take current position
		//calculate tiles in viewport
		//fetch them 100% (if they are not available in the cache) (less % if there are partially available in cache)
		//render them
		//update LOD of the cache and make the ones just fetched have likelihood = 1 (protected)
		//remove as many as needed for the fetched that have the lowest LOD if cache is full
		//caclulate the tiles of prefetch
		//prefetch them % based in their LOD
		//remove as many as needed for the prefetched that have the lowest LOD if cache is full
		//adjust prefetching diameter and protrusion direction and length
		//evaluating latency = network + database + render, cache hit, prediction hit, average time between user moves
		//protrusion length could be based on speed of user
		
		
	
}
