package simulation;

import static depr.simulation.Config.DATABASE_WIDTH;
import static simulation.Config.DATABASE_TILES_NUM;
import static simulation.Config.WORKLOAD_FILE;

import java.util.LinkedList;
import java.util.Vector;



import simulation.Cache;
import simulation.Database;
import simulation.Viewport;
import simulation.events.UserMove;
import simulation.monitor.Workload;
import simulation.predictor.Node;
import simulation.predictor.Predictor;

public class Main {
	public static Viewport previousViewport=null;
	public static Database db = new Database();
	public static Cache cache = new Cache();
	public static Vector<String> moves = Workload.readMoves();
	
	public static void main(String args[]){

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
		int count=0;
		UserMove userMove = null;
		while (true){
			//for (int i=0; i<4; i++){	
			
			
			if (Main.previousViewport!=null){
				Main.cache.updateAllTileLikelihoods(Main.previousViewport);
			}
			if (moves!=null && moves.size()>0){
				userMove = Predictor.nextMoveFromWorkload(userMove,moves);
			}
			else {
				userMove = Predictor.nextMove(viewport);
				
			}
			
		//	if (debug){
			
		//	}
			if (count==0){
				//System.out.println("UserMove "+userMove);
				LinkedList<Node> list = Predictor.normalize(Predictor.createPredictorTree(userMove, 0.05));
				
				System.out.println(list);
				Vector<Node> vec = Predictor.regularize(list);
				System.out.println(vec);
				//Node.sortDesc(vec);
				//System.out.println(vec);
				//System.out.println(node);
				//System.out.println(node.left);
			}
			count++;
			
			userMove.viewportFetch();
			//userMove.prefetch();
			
			boolean isTerminal = userMove.viewport.upperLeft.x == DATABASE_WIDTH-1 && userMove.viewport.upperLeft.y == DATABASE_WIDTH-1;
			if (isTerminal || moves.size()==0){
				System.out.println("TELOS");
				break;
			}
			Main.previousViewport = viewport;
			
			//System.out.println(UserMove.totalCacheHits+" "+UserMove.totalCacheMisses);
		}
	}
}
