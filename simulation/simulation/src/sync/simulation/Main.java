package sync.simulation;

import static sync.simulation.Config.DATABASE_WIDTH;
import static sync.simulation.Config.DATABASE_TILES_NUM;
import static sync.simulation.Config.WORKLOAD_FILE;
import static sync.simulation.Config.UPPER_LEFT_STARTING_POINT;
import static sync.simulation.Config.VIEWPORT_HEIGHT;
import static sync.simulation.Config.VIEWPORT_WIDTH;
import static sync.simulation.Config.WAVES;
import static sync.simulation.Config.CACHE_SIZE;
import static sync.simulation.Config.THINK_TIME;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;




import sync.simulation.Cache;
import sync.simulation.Database;
import sync.simulation.Viewport;
import sync.simulation.events.UserMove;
import sync.simulation.predictor.Node;
import sync.simulation.predictor.Predictor;
import sync.simulation.predictor.PredictorOld;
import sync.simulation.predictor.Tuple;
import util.Util;

public class Main {
	//public static Viewport previousViewport=null;
	public static Database db = new Database();
	public static Cache cache = new Cache();
	//public static Vector<String> moves = Workload.readMoves();
	
	
	public static void main(String args[]){

		

		
		System.out.println("Initializing  Sync Database");
		db.init(DATABASE_TILES_NUM);
		db.clearCache();
		System.out.println("Starting Experiment");
		//cache.warmUp();
		
	
		
		//UserMove userMove;
		Viewport viewport = new Viewport(VIEWPORT_HEIGHT, VIEWPORT_WIDTH,  UPPER_LEFT_STARTING_POINT,null);
		//Workload.init();
		double startTime = System.nanoTime();
		/*if (moves!=null && moves.size()>0){
			System.out.println("Starting Workload Execution "+WORKLOAD_FILE);
		}
		else {
			System.out.println("Making a new Workload "+WORKLOAD_FILE);
		}*/
		int count=0;
		UserMove userMove = new UserMove(viewport);
		System.out.println("Position: "+userMove);
		while (true){
		
		
		//for (int i=0; i<10; i++){	
			
			userMove = Predictor.nextMove(userMove.viewport);
			System.out.println("Position: "+userMove);
			/*if (moves!=null && moves.size()>0){
				userMove = Predictor.nextMoveFromWorkload(userMove,moves);
			}
			else {
				userMove = Predictor.nextMove(viewport);
				
			}*/
			
			//System.out.println("Test the other tree");
			
			
			
			
			
			//cache.cacheTileWithFragmentRange(db.fetchTileWithFragmentRange(userMove.upperLeft, 1, 1, userMove),2,2).data[1] = "dsadasds";
			
			//System.out.println(cache);
			
			//Vector<Node> tilesToPrefetch = Predictor.preparePrefetching(userMove.viewport.upperLeft.createNode(),2,5); 
			UserMove.totoalMoves+=1;
			
			//break;
			//System.exit(0);
			//if (count==0){
			System.out.println("Memory before Move:"+Main.cache.getQueue());
			Node currentNode = userMove.viewport.upperLeft.createNode();
			System.out.println("Current Position we just moved: "+currentNode.point);
			
			
			
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
			System.out.println("Memory after Fetch:"+Main.cache.getQueue());
			System.out.println("Memory Space Used after Fetch "+Main.cache.sizeBeingUsed());
			
			double start = System.currentTimeMillis();
			
			
			//OLD PREDICTOR
//			Vector<Node> list = PredictorOld.prepare(userMove);
//			System.out.println("List To Be Prefetched: "+list);
//			
//			System.out.println("Memory Space Used before Prefetch "+Main.cache.sizeBeingUsed());
//			System.out.println("before thinkTime:"+ userMove.thinkTime);
//			userMove.prefetch(list);
//			Main.cache.updateProbabilities(list,currentNode.point);
//			System.out.println("after thinkTime:"+ userMove.thinkTime);
//			System.out.println("Memory after Prefetch:"+Main.cache.queue);
			
			

	
		//PREDICTOR
		  int wave=1;
			Vector<Node> totalList = new Vector<Node>();
			Double previousProb = null;
			Integer previousFrames = null;
			while (THINK_TIME>0 && wave<=WAVES){
				System.out.println("wave:"+wave);
				Object[] objArray = Predictor.preparePrefetching(currentNode,wave,3,previousFrames,previousProb);
				previousProb=(Double)objArray[1];
				previousFrames= (Integer)objArray[2];
				objArray[2] = previousFrames;
				Vector<Node>list = (Vector<Node>)objArray[0];
				totalList.addAll(list);
				wave++;
				if (list.size()==0)
					continue;
				System.out.println("List To Be Prefetched: "+list);
				
				System.out.println("Memory Space Used before Prefetch "+Main.cache.sizeBeingUsed());
				System.out.println("before thinkTime:"+ userMove.thinkTime);
				Main.cache.updateProbabilities(totalList,currentNode.point);
				userMove.prefetch(list);
				System.out.println("after thinkTime:"+ userMove.thinkTime);
				System.out.println("Memory after Prefetch:"+Main.cache.getQueue());
				
			}
			
		
			System.out.println("#Cache Misses during Fetch in a Move: "+userMove.cacheMissesDuringFetch);
			UserMove.misses.add(userMove.cacheMissesDuringFetch);
			System.out.println("#Disk Fetched Fragments during Move: "+userMove.cacheMisses);
			
			//System.out.println("Memory "+Main.cache.SpaceBeingUsed);
			
			System.out.println("Memory Space Used after Move "+Main.cache.sizeBeingUsed());
			if (Main.cache.getTilesOccupied()!=Main.cache.getQueueSize() ||  Main.cache.sizeBeingUsed()>CACHE_SIZE){
				System.err.println(Main.cache.sizeBeingUsed()+" "+CACHE_SIZE);
				System.err.println("Memory Inconsistency Error");
				break;
			}
			System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			
			boolean isTerminal = userMove.viewport.upperLeft.x == DATABASE_WIDTH-1 && userMove.viewport.upperLeft.y == DATABASE_WIDTH-1;
			if (isTerminal){
				System.out.println("TELOS");
				break;
			}
			//Main.previousViewport = viewport;
			//break;
			//System.out.println(UserMove.totalCacheHits+" "+UserMove.totalCacheMisses);
			//break;
		}
		
		
		System.out.println("All Misses Mean: "+ Util.average(UserMove.misses));
		System.out.println("All Misses Variance: "+ Util.variance(UserMove.misses));
		System.out.println("#Total Moves: "+UserMove.totoalMoves);
		System.out.println("#Total Cache Misses during Fetches: "+UserMove.totalCacheMissesDuringFetch);
		System.out.println("#Total Cache Latency during Fetches: "+UserMove.totalLatencyDuringFetch+" msec");
		System.out.println("#Total Disk Fetched Fragments: "+UserMove.totalCacheMisses);
		System.out.println("#Total Cache Fetched Fragments: "+UserMove.totalCacheHits);
	}
}
