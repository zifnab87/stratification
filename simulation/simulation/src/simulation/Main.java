package simulation;

import static simulation.Config.VIEWPORT_HEIGHT;
import static simulation.Config.VIEWPORT_WIDTH;
import static simulation.Config.UPPER_LEFT_STARTING_POINT;
import static simulation.Config.DATABASE_TILES_NUM;
import static simulation.Config.USER_MOVEMENT_TIME;
import simulation.events.EventHandler;
import simulation.events.UserMove;

import java.lang.reflect.Method;


public class Main {
	
	public static Database db = new Database();
	public static Cache cache = new Cache();
	public static Viewport next;
	public static Viewport preivous;
	
	

	
	public static void main(String args[]) throws Exception{
		
		int a = 476;
		double b = 476d / 1000;
		int c = (int) Math.ceil(b);
		System.out.println(c);
		
		//db.setViewport(viewport);
		db.init(DATABASE_TILES_NUM);
		Predictor.trainDatabase(db);
		//System.out.println("usermove");
		
		Thread userMovevent = new Thread() { 
					
			public void run() {
				for (int i=0; i<10; i++){
					UserMove usermove = new UserMove(new Point(0,i));
					Viewport previous = next;
					Viewport next = usermove.newMove;
					try {
						usermove.action();
						Thread.sleep(USER_MOVEMENT_TIME);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		};
		userMovevent.run();
		
		
		//db.viewportFetch();
		
		
		
		
		System.out.println(cache.tiles.size());
		System.out.println(((Tile)(cache.tiles.get(new Point(0,0).hashCode()))));
		//System.out.println("init done");
		//System.out.println(Predictor.likelihoodToLOD(1.0d));

		//System.out.println("train done");
		//System.out.println(db.tiles.get(new Point(1,0).hashCode()).getLikelihood());
		//System.out.println(db.tiles.get(new Point(0,24).hashCode()).getLikelihood());
		
		//System.out.println(db.tiles.get(new Point(1,0).hashCode()).getLOD());
		//System.out.println(db.tiles.get(new Point(0,24).hashCode()).getLOD());
		//Viewport view = new Viewport(4, 3, new Point(3,2));
		
		//System.out.println(Predictor.distance(new Point(1,3),new Point(10,10)));
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
