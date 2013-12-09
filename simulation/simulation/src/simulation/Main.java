package simulation;

import static depr.simulation.Config.DATABASE_WIDTH;
import static simulation.Config.DATABASE_TILES_NUM;
import static simulation.Config.WORKLOAD_FILE;
import static simulation.Config.UPPER_LEFT_STARTING_POINT;


import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;




import simulation.Cache;
import simulation.Database;
import simulation.Viewport;
import simulation.events.UserMove;
import simulation.monitor.Workload;
import simulation.predictor.Node;
import simulation.predictor.Predictor;
import simulation.predictor.Tuple;

public class Main {
	//public static Viewport previousViewport=null;
	public static Database db = new Database();
	public static Cache cache = new Cache();
	public static Vector<String> moves = Workload.readMoves();
	
	public static void main(String args[]){

		

		
		System.out.println("Initializing Database");
		db.init(DATABASE_TILES_NUM);
		db.clearCache();
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
		int count=0;
		UserMove userMove = null;
		while (true){
		//for (int i=0; i<; i++){	
			
			
			/*if (Main.previousViewport!=null){
				Main.cache.updateAllTileLikelihoods(Main.previousViewport);
			}*/
			if (moves!=null && moves.size()>0){
				userMove = Predictor.nextMoveFromWorkload(userMove,moves);
			}
			else {
				userMove = Predictor.nextMove(viewport);
				
			}
			//cache.cacheTileWithFragmentRange(db.fetchTileWithFragmentRange(userMove.upperLeft, 1, 1, userMove),2,2).data[1] = "dsadasds";
			
			//System.out.println(cache);
			
			//Vector<Node> tilesToPrefetch = Predictor.preparePrefetching(userMove.viewport.upperLeft.createNode(),2,5); 
			
			
			//break;
			//System.exit(0);
			//if (count==0){
			System.out.println("Memory before Move:"+Main.cache.queue);
			Node currentNode = userMove.viewport.upperLeft.createNode();
			System.out.println("Current Position we just moved: "+currentNode.point);
			
			//System.out.println("Udadasd Memory "+Main.cache.queue);
			
				//Main.cache.updateAllTileLikelihoods(map);
				//Iterator<Node> iter = list.iterator();
//				while (iter.hasNext()){
//					Node node = iter.next();
//					double likelihood = tuple.x;
//					int lod = tuple.y;
//					System.out.println("~"+key);
//					System.out.println("~"+likelihood);
//					System.out.println("~"+lod);
//				}
				
			//}
			count++;
			userMove.viewportFetch();
			System.out.println("Memory after Fetch:"+Main.cache.queue);
			
			
			double start = System.currentTimeMillis();
			
			
			
			
			/*Vector<Node> list2 = Predictor.preparePrefetching(currentNode,2,2); 
			System.out.println("List2 To Be Prefetched: "+list2);
			Vector<Node> list3 = Predictor.preparePrefetching(currentNode,3,3); 
			System.out.println("List3 To Be Prefetched: "+list3);
			double total = System.currentTimeMillis() - start;
			System.out.println("Prediction:"+total+" msecs");*/
	
			int wave=1;
			while (userMove.thinkTime>0 && wave<=5){
				System.out.println("wave"+wave);
				Vector<Node> list = Predictor.preparePrefetching(currentNode,wave,5);
				wave++;
				if (list.size()==0)
					continue;
				System.out.println("List To Be Prefetched: "+list);
				
				System.out.println("Memory Space Used before Prefetch "+Main.cache.SpaceBeingUsed);
				System.out.println("before thinkTime:"+ userMove.thinkTime);
				userMove.prefetch(list);
				Main.cache.updateProbabilities(list,currentNode.point);
				System.out.println("after thinkTime:"+ userMove.thinkTime);
				System.out.println("Memory after Prefetch:"+Main.cache.queue);
				
			}
			
			
			/*userMove.prefetch(list2);
			userMove.prefetch(list3);*/
			
			System.out.println("#Cache Misses during Move: "+userMove.cacheMissesDuringFetch);
			System.out.println("#Disk Fetched Fragments during Move: "+userMove.cacheMisses);
			
			//System.out.println("Memory "+Main.cache.SpaceBeingUsed);
			
			System.out.println("Memory Space Used after Prefetch "+Main.cache.SpaceBeingUsed);
			if (Main.cache.tiles.size()!=Main.cache.queue.size()){
				System.err.println("Error");
				break;
			}
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			
			boolean isTerminal = userMove.viewport.upperLeft.x == DATABASE_WIDTH-1 && userMove.viewport.upperLeft.y == DATABASE_WIDTH-1;
			if (isTerminal || moves.size()==0){
				System.out.println("TELOS");
				break;
			}
			//Main.previousViewport = viewport;
			//break;
			//System.out.println(UserMove.totalCacheHits+" "+UserMove.totalCacheMisses);
			//break;
		}
		System.out.println("#Total Cache Misses during Fetches: "+UserMove.totalCacheMissesDuringFetch);
		System.out.println("#Total Disk Fetched Fragments: "+UserMove.totalCacheMisses);
		System.out.println("#Total Cache Fetched Fragments: "+UserMove.totalCacheHits);
	}
}
