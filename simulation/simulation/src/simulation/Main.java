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
			//for (int i=0; i<4; i++){	
			
			
			/*if (Main.previousViewport!=null){
				Main.cache.updateAllTileLikelihoods(Main.previousViewport);
			}*/
			if (moves!=null && moves.size()>0){
				userMove = Predictor.nextMoveFromWorkload(userMove,moves);
			}
			else {
				userMove = Predictor.nextMove(viewport);
				
			}
			cache.cacheTileWithFragmentRange(db.fetchTileWithFragmentRange(userMove.upperLeft, 1, 1, userMove),2,2).data[1] = "dsadasds";
			
			//System.out.println(cache);
			
			//Vector<Node> tilesToPrefetch = Predictor.preparePrefetching(userMove.viewport.upperLeft.createNode(),2,5); 
			
			
			//break;
			//System.exit(0);
			//if (count==0){
			Vector<Node> list = Predictor.preparePrefetching(userMove.viewport.upperLeft.createNode(),1,5); 
			System.out.println(list);
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
				System.out.println("~~~~~~~~~~~~~~~~~~");
			//}
			count++;
			
			userMove.viewportFetch();
			userMove.prefetch(list);

			
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
		System.out.println(UserMove.totalCacheMisses);
		System.out.println(UserMove.totalCacheHits);
	}
}
