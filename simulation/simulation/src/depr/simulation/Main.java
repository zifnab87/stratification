package depr.simulation;

import static depr.simulation.Config.DATABASE_TILES_NUM;
import static depr.simulation.Config.DATABASE_WIDTH;
import static depr.simulation.Config.WORKLOAD_FILE;
import static depr.simulation.Config.debug;


import java.io.File;
import java.nio.file.Files;
import java.util.Vector;

import depr.simulation.events.Event;
import depr.simulation.events.EventHandler;
import depr.simulation.events.FragmentedTileFetch;
import depr.simulation.events.FragmentedTilePrefetch;
import depr.simulation.events.StopAll;
import depr.simulation.events.TileFetch;
import depr.simulation.events.TilePrefetch;
import depr.simulation.events.UserMove;
import depr.simulation.monitor.Monitor;
import depr.simulation.monitor.Workload;




public class Main {
	public static Vector<String> workload = null;
	public static Database db = new Database();
	public static Cache cache = new Cache();
	public static Vector<String> moves = Workload.readMoves();
	public static Viewport previousViewport=null;

	public static double startTime;
	
	
	
	public static void main(String args[]) throws Exception{
		
		System.out.println("Initializing Database");
		db.init(DATABASE_TILES_NUM);
		System.out.println("Starting Experiment");
		//UserMove userMove;
		Viewport viewport = null;
		//Workload.init();
		double startTime = System.nanoTime();
		if (moves!=null && moves.size()>0){
			System.out.println("Starting Workload Execution "+WORKLOAD_FILE);
		}
		else {
			System.out.println("Making a new Workload "+WORKLOAD_FILE);
		}
		
		while (true){
		//for (int i=0; i<4; i++){	
			if (Main.previousViewport!=null){
				Main.cache.updateAllTileLikelihoods(Main.previousViewport);
			}
			if (moves!=null && moves.size()>0){
				viewport = Predictor.nextMove(viewport,moves);
			}
			else {
				viewport = Predictor.nextMove(viewport);
				
			}
		//	if (debug){
				System.out.println("UserMove "+viewport.upperLeft);
		//	}
			
			
			Monitor.userMove();
			if (debug){
				if (Main.previousViewport!=null){
					String cache = Main.cache.toString();
					System.out.println("Cache After Move"+Main.previousViewport.upperLeft+":"+cache);
				}
			}
			System.out.println("Cache size being used: "+Main.cache.sizeBeingUsed());
			Vector<Point> fetch = Main.db.viewportFetch(viewport);
			Vector<Point> prefetch = Main.db.aroundViewportPrefetch(viewport);
			
			for(Point p : fetch){
				if (p.fragmentNums==null){
					new TileFetch(p).action();
				}
				else{
					new FragmentedTileFetch(p).action();
				}
				
			}
			
			for(Point p : prefetch){
				if (p.fragmentNums==null){
					new TilePrefetch(p).action();
				}
				else{
					new FragmentedTilePrefetch(p).action();
				}
			}
			Monitor.writeToFile();
			
			
			boolean isTerminal = viewport.upperLeft.x == DATABASE_WIDTH-1 && viewport.upperLeft.y == DATABASE_WIDTH-1;
			if (isTerminal || moves.size()==0){
				Monitor.display(startTime);
				System.out.println("TELOS");
				break;
			}
			Main.previousViewport = viewport;
			//System.out.println("@@@@@@"+ObjectSizeFetcher.getObjectSize(Main.cache));
			//System.out.println("@@@@@@"+ObjectSizeFetcher.getObjectSize(Main.db));
			
			Thread.sleep(100);
			
			//Main.cache.makeConsistent();
			
			//System.out.println("before Consistent"+Main.cache);

		}
	}
	
	/*public static void main(String args[]) throws Exception{
		
		//db.setViewport(viewport);
		System.out.println("Initializing Database");
		db.init(DATABASE_TILES_NUM);
		System.out.println("Starting Experiment");
		//Predictor.trainDatabase(db);
		//System.out.println("usermove");
		
		startTime = System.nanoTime();
		
		Thread userMovement = new Thread() { 
			public void run() {
				UserMove userMove;
				Viewport viewport = null;
				//Workload.init();
				if (moves!=null && moves.size()>0){
					System.out.println("Starting Workload Execution "+WORKLOAD_FILE);
				}
				else {
					System.out.println("Making a new Workload "+WORKLOAD_FILE);
				}
				while (true){
				//for (int i=0; i<19;i++){
					if (EventHandler.userMoveQueue.size()==0){
						
						if (moves!=null && moves.size()>0){
							viewport = Predictor.nextMove(viewport,moves);
							Main.previousViewport = viewport;
						}
						else {
							viewport = Predictor.nextMove(viewport);
							Main.previousViewport = viewport;
						}
						//Main.cache.makeConsistent();
						//Main.cache.updateAllTileLikelihoods(Main.previousViewport);
						//System.out.println("before Consistent"+Main.cache);
						//Main.cache.makeConsistent();
						Main.cache.makeConsistent();
						String cache = Main.cache.toString();
						
						System.out.println("Cache:"+cache);
						if (cache.contains("INCONSISTENT")){
							System.exit(-1);
						}
						System.out.println("cachesize"+Main.cache.tiles.size()+" "+Main.cache.queue.size());
						
						//System.out.println("Cache:"+Main.cache);
						userMove = new UserMove(viewport);
						try {
							
							Event.sendEvent(userMove);
							Thread.sleep(100);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
					}
				}
				
				
			}
		};
		userMovement.run();
		
		
	
		Thread.sleep(10000);
		try {
			Event.sendEvent(new StopAll(startTime));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	

		
		
		
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
