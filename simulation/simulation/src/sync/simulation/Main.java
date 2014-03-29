package sync.simulation;

import static sync.simulation.Config.DATABASE_WIDTH;
import static sync.simulation.Config.DATABASE_TILES_NUM;
import static sync.simulation.Config.UPPER_LEFT_STARTING_POINT;
import static sync.simulation.Config.VIEWPORT_HEIGHT;
import static sync.simulation.Config.VIEWPORT_WIDTH;
import static sync.simulation.Config.WAVES;
import static sync.simulation.Config.CACHE_SIZE;
import static sync.simulation.Config.THINK_TIME;
import static sync.simulation.Config.FRAGMENT;
import static sync.simulation.Config.RUNS;
import static sync.simulation.Config.CONTIG_FRAGM_IN_SINGLE_QUERY;
import static sync.simulation.Config.JUMP_REGION_WIDTH;
import static sync.simulation.Config.COVERAGE;
import java.sql.SQLException;
import java.util.Random;
import java.util.Vector;






import sync.simulation.Cache;
import sync.simulation.Database;
import sync.simulation.Viewport;
import sync.simulation.events.UserMove;
import sync.simulation.monitor.Workload;
import sync.simulation.predictor.Node;
import sync.simulation.predictor.Predictor;
import sync.simulation.predictor.PredictorOld;
import sync.simulation.predictor.Tuple;
import sync.simulation.regions.JumpRegion;
import sync.simulation.regions.UserStudiesCombined;
import sync.userstudysynthesizer.UserStudySynthesizer;
import util.Util;

public class Main {
	//public static Viewport previousViewport=null;
	public static Database db = new Database();
	public static Cache cache = new Cache();
	
	
	
	public static void main(String args[]){

		
//		Vector<Double> vec = new Vector<Double>();
//		vec.add(1.0);
//		vec.add(0.0);
//		vec.add(0.0);
//		vec.add(0.0);
//		System.out.println(Util.variance(vec));
		
		
		//db.init();
		//System.exit(9);
		Util.debug("Initializing  Sync Database");
		//db.init(DATABASE_TILES_NUM);
		db.clearCache();
		Util.debug("Starting Experiment");
		//cache.warmUp();
		
		
		//Database db = new Database();
		JumpRegion jump = new JumpRegion(Database.points(0,0));
		UserStudiesCombined usc = new UserStudiesCombined();
		UserStudySynthesizer uss = new UserStudySynthesizer();
		

		//Random rand = new Random();
		for (int m=256; m<=256; m=m*2){
			CACHE_SIZE = m;
			for (int f=0; f<=1; f++){
				for (int w=-1; w<=1; w++){
					System.out.println("EVICTION METRIC:"+w);
					Vector<Run> runs = new Vector<Run>();
					if (f==0){
						FRAGMENT = true;
					}
					else {
						FRAGMENT = false;
					}
					
					for (int i=0; i<=RUNS+1; i++){
						
						System.gc();
						if (i==0){
							
							db.clearCache();
							cache = new Cache();
						}
						
					
						Run run = new Run();
						//for warmup
						if (i>0){
							runs.add(run);
						}
						Random rand = new Random();
						//THINK_TIME = UserMove.nextFromDistribution(rand);
						UserMove current = new UserMove(db.randomPoint());
						System.out.println("Run"+i);
						UserMove.currentZoomLevel = 1;
						Viewport viewport = new Viewport(VIEWPORT_HEIGHT, VIEWPORT_WIDTH,  current.point,null);
						//Predictor predictor = new Predictor(run);
						for (int j=0; j<400	; j++){ //moves per run	
					    	
							System.gc();
							
							if (j%10==0){
					    		
					    		db.close();
					    	}	
							if (j%100==0){
								System.out.println("~~~~~~~~~~~~~~~~");
							}
					    	current = UserStudySynthesizer.whatHappensNext(current);
					    	if (current==null){//Tiles and Zoom Happened
					    		continue;
					    	}
					    	current.run = run;
					    	viewport = new Viewport(VIEWPORT_HEIGHT, VIEWPORT_WIDTH,  current.point,null);
					    	current.viewport = viewport;
					    	jump = new JumpRegion(Database.points(0,0));
					    	current.run.totalMoves+=1;			
							Util.debug("Memory before Move:"+Main.cache.getQueue(),true);
							Util.debug("Current Position we just moved: "+current.point+ " Zoom: "+UserMove.currentZoomLevel);
							current.viewportFetch();
							Util.debug("Memory after Move:"+Main.cache.getQueue(),true);
							Main.cache.updateImportances(current.point);
							//System.out.println(cache);
							Util.debug("Memory after Fetch:"+Main.cache.getQueue(),true);
							Util.debug("Memory Space Used after Fetch "+Main.cache.sizeBeingUsed());
					    
						   //PREDICTOR
							
							//System.out.println("Before:"+cache);
							current.prefetch(jump, current.point);
							Main.cache.updateImportances(current.point);
							//System.out.println("After:"+cache);
						
							Util.debug("#Cache Misses during Fetch in a Move: "+current.cacheMissesDuringFetch);
							current.run.misses.add(current.cacheMissesDuringFetch);
							Util.debug("#Disk Fetched Fragments during Move: "+current.cacheMisses);
							
							//Util.debug("Memory "+Main.cache.SpaceBeingUsed);
							
							//Util.debug("Memory Space Used after Move "+Main.cache.sizeBeingUsed());
							/*if (Main.cache.getTilesOccupied()!=Main.cache.getQueueSize() ||  Main.cache.sizeBeingUsed()>CACHE_SIZE){
								System.err.println(Main.cache.sizeBeingUsed()+" "+Main.cache.getTilesOccupied()+" "+Main.cache.getQueueSize()+" "+CACHE_SIZE);
								System.err.println("Memory Inconsistency Error");
								break;
							}*/
							Util.debug("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			//					
						
							/*Util.debug("#Cache Misses during Fetch in a Move: "+current.cacheMissesDuringFetch);
							current.run.misses.add(current.cacheMissesDuringFetch);
							Util.debug("#Disk Fetched Fragments during Move: "+current.cacheMisses);
							
							//Util.debug("Memory "+Main.cache.SpaceBeingUsed);
							
							Util.debug("Memory Space Used after Move "+Main.cache.sizeBeingUsed());
							if (Main.cache.getTilesOccupied()!=Main.cache.getQueueSize() ||  Main.cache.sizeBeingUsed()>CACHE_SIZE){
								System.err.println(Main.cache.sizeBeingUsed()+" "+CACHE_SIZE);
								System.err.println("Memory Inconsistency Error");
								break;
							}
							Util.debug("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");*/
							
							
							
							//Util.debug("Space Being Used: "+cache.sizeBeingUsed(),true);
							//Util.debug("Run: "+runs.size(),true);
							Util.debug("All Misses Mean: "+ Util.average(run.misses));
							Util.debug("All Misses Variance: "+ Util.variance(run.misses));
							Util.debug("#Total Moves: "+run.totalMoves);
							Util.debug("#Total Cache Misses during Fetches: "+run.totalCacheMissesDuringFetch);
							Util.debug("#Total Latency during Fetches: "+run.totalLatencyDuringFetch+" msec");
							Util.debug("#Total Disk Fetched Fragments: "+run.totalCacheMisses);
							Util.debug("#Total Cache Fetched Fragments: "+run.totalCacheHits);
							Util.debug("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
							
						}
							
					}
					AverageRun avgRun = new AverageRun(runs);
					Util.debug("Runs: "+runs.size(),true);
					Util.debug("Cache: "+CACHE_SIZE,true);
					//Util.debug("Think Time = "+ THINK_TIME,true);
					Util.debug("Fragments = "+FRAGMENT,true);
					if (FRAGMENT){
						Util.debug("Coverage = "+COVERAGE,true);
					}
					Util.debug("#Average Moves Number: "+avgRun.totalMoves,true);
					Util.debug("#Average Total Cache Misses during Fetches: "+avgRun.totalCacheMissesDuringFetch,true);
					Util.debug("#Average Total Cache Hits during Fetches: "+avgRun.totalCacheHitsDuringFetch,true);
					Util.debug("#Average Total Cache Misses/(Total CH+Total CM) during Fetches: "+(100d*avgRun.totalCacheMissesDuringFetch)/(avgRun.totalCacheMissesDuringFetch+avgRun.totalCacheHitsDuringFetch)+"%",true);
					Util.debug("#Average Total Latency during Fetches: "+avgRun.totalLatencyDuringFetch+" msec",true);
					//Util.debug("#Average Total Disk Fetched Fragments: "+avgRun.totalCacheMisses,true);
					//Util.debug("#Average Total Cache Fetched Fragments: "+avgRun.totalCacheHits,true);
					Util.debug("~~~~~~~~~~~~~~~~~~~~~~~~~~~~",true);   	
				}
			}
		}
	}
}
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
//		for (int k=1; k<=1; k++){
//			if (k==0){
//				FRAGMENT = true;
//				CONTIG_FRAGM_IN_SINGLE_QUERY = false;
//			}
//			else {
//				FRAGMENT = false;
//				CONTIG_FRAGM_IN_SINGLE_QUERY = true;
//			}
//			Vector<Run> runs = new Vector<Run>();
//			for (int i=0; i<=RUNS+5; i++){
//				System.gc();
//				if (i==0){
//					db.clearCache();
//					cache = new Cache();
//				}
//				Run run = new Run();
//				//for warmup
//				if (i>5){
//					runs.add(run);
//				}
//				
//				THINK_TIME = UserMove.nextFromDistribution(rand);
//				//System.out.println(sumGausian/nGaussian);
//				
//				Predictor predictor = new Predictor(run);
//				//UserMove userMove;
//				Viewport viewport = new Viewport(VIEWPORT_HEIGHT, VIEWPORT_WIDTH,  UPPER_LEFT_STARTING_POINT,null);
//				//Workload.init();
//				double startTime = System.nanoTime();
//				/*if (moves!=null && moves.size()>0){
//					Util.debug("Starting Workload Execution "+WORKLOAD_FILE);
//				}
//				else {
//					Util.debug("Making a new Workload "+WORKLOAD_FILE);
//				}*/
//				int count=0;
//				
//				//System.out.println(current.point+"");
//				
//					
//					//System.out.println(current.point);
//				}
//
//				//UserMove userMove = new UserMove(viewport,run);
//				//Util.debug("Position: "+userMove);
//			    // Vector<String> moves = Workload.readMoves();
//			    int isFinalCounter = 0;
//			    UserMove current = new UserMove(db.randomPoint());
//			    for (int j=0; j<500; j++){
//				
//			    	
//			    	current = UserStudySynthesizer.whatHappensNext(current);
//					
//					Util.debug("Position: "+current);
//					//if (moves!=null && moves.size()>0){
//					//userMove = predictor.nextMove(userMove.viewport);
//					//userMove = predictor.nextMoveFromWorkload(userMove,moves);
//					//}
//					//else {
//					//	userMove = Predictor.nextMove(viewport);
//						
//				//	}
//					
//					//Util.debug("Test the other tree");
//					
//					
//					
//					
//					
//					//cache.cacheTileWithFragmentRange(db.fetchTileWithFragmentRange(userMove.upperLeft, 1, 1, userMove),2,2).data[1] = "dsadasds";
//					
//					//Util.debug(cache);
//					
//					//Vector<Node> tilesToPrefetch = Predictor.preparePrefetching(userMove.viewport.upperLeft.createNode(),2,5); 
//					current.run.totalMoves+=1;
//					
//					Util.debug("Memory before Move:"+Main.cache.getQueue());
//					Node currentNode = null;//userMove.viewport.upperLeft.createNode();
//					Util.debug("Current Position we just moved: "+currentNode.point);
//					
//					
//					
//
//	
//					//count++;
//					current.viewportFetch();
//					Util.debug("Memory after Fetch:"+Main.cache.getQueue());
//					Util.debug("Memory Space Used after Fetch "+Main.cache.sizeBeingUsed());
//					
//					double start = System.currentTimeMillis();
//					
//					
//					//OLD PREDICTOR
//		//			Vector<Node> list = PredictorOld.prepare(userMove);
//		//			Util.debug("List To Be Prefetched: "+list);
//		//			
//		//			Util.debug("Memory Space Used before Prefetch "+Main.cache.sizeBeingUsed());
//		//			Util.debug("before thinkTime:"+ userMove.thinkTime);
//		//			userMove.prefetch(list);
//		//			Main.cache.updateProbabilities(list,currentNode.point);
//		//			Util.debug("after thinkTime:"+ userMove.thinkTime);
//		//			Util.debug("Memory after Prefetch:"+Main.cache.queue);
//					
//					
//		
//			
//				//PREDICTOR
//				  int wave=1;
//					Vector<Node> totalList = new Vector<Node>();
//					Double previousProb = null;
//					Integer previousFrames = null;
//					while (THINK_TIME>0 && wave<=WAVES){
//						Util.debug("wave:"+wave);
//						Object[] objArray = predictor.preparePrefetching(currentNode,wave,3,previousFrames,previousProb);
//						previousProb=(Double)objArray[1];
//						previousFrames= (Integer)objArray[2];
//						objArray[2] = previousFrames;
//						Vector<Node>list = (Vector<Node>)objArray[0];
//						totalList.addAll(list);
//						wave++;
//						if (list.size()==0)
//							continue;
//						Util.debug("List To Be Prefetched: "+list);
//						
//						Util.debug("Memory Space Used before Prefetch "+Main.cache.sizeBeingUsed());
//						Util.debug("before thinkTime:"+ current.thinkTime);
//						Main.cache.updateProbabilities(totalList,currentNode.point);
//						current.prefetch(list);
//						Util.debug("after thinkTime:"+ current.thinkTime);
//						Util.debug("Memory after Prefetch:"+Main.cache.getQueue());
//						
//					}
//					
//				
//					Util.debug("#Cache Misses during Fetch in a Move: "+current.cacheMissesDuringFetch);
//					current.run.misses.add(current.cacheMissesDuringFetch);
//					Util.debug("#Disk Fetched Fragments during Move: "+current.cacheMisses);
//					
//					//Util.debug("Memory "+Main.cache.SpaceBeingUsed);
//					
//					Util.debug("Memory Space Used after Move "+Main.cache.sizeBeingUsed());
//					if (Main.cache.getTilesOccupied()!=Main.cache.getQueueSize() ||  Main.cache.sizeBeingUsed()>CACHE_SIZE){
//						System.err.println(Main.cache.sizeBeingUsed()+" "+CACHE_SIZE);
//						System.err.println("Memory Inconsistency Error");
//						break;
//					}
//					Util.debug("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//					
//					
//					//count 10 times that is on a border of the database
//					//boolean isTerminal = userMove.viewport.upperLeft.x == DATABASE_WIDTH-1 && userMove.viewport.upperLeft.y == DATABASE_WIDTH-1;
//					boolean isInBoundary = current.viewport.upperLeft.x == DATABASE_WIDTH-1 || current.viewport.upperLeft.x == 0 || userMove.viewport.upperLeft.y == DATABASE_WIDTH-1 || userMove.viewport.upperLeft.y == 0;
//					
//					if (isInBoundary){
//						isFinalCounter++;
//					}
//					
//					if (isFinalCounter>10){
//						Util.debug("END");
//						break;
//					}
//				}
//				//Util.debug("Space Being Used: "+cache.sizeBeingUsed(),true);
//				//Util.debug("Run: "+runs.size(),true);
//				Util.debug("All Misses Mean: "+ Util.average(run.misses));
//				Util.debug("All Misses Variance: "+ Util.variance(run.misses));
//				Util.debug("#Total Moves: "+run.totalMoves);
//				Util.debug("#Total Cache Misses during Fetches: "+run.totalCacheMissesDuringFetch);
//				Util.debug("#Total Latency during Fetches: "+run.totalLatencyDuringFetch+" msec");
//				Util.debug("#Total Disk Fetched Fragments: "+run.totalCacheMisses);
//				Util.debug("#Total Cache Fetched Fragments: "+run.totalCacheHits);
//				Util.debug("~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
//			}
//			
//			AverageRun avgRun = new AverageRun(runs);
//			Util.debug("Runs: "+runs.size(),true);
//			//Util.debug("Think Time = "+ THINK_TIME,true);
//			Util.debug("Fragments = "+FRAGMENT,true);
//			Util.debug("#Average Moves Number: "+avgRun.totalMoves,true);
//			Util.debug("#Average Total Cache Misses during Fetches: "+avgRun.totalCacheMissesDuringFetch,true);
//			Util.debug("#Average Total Cache Hits during Fetches: "+avgRun.totalCacheHitsDuringFetch,true);
//			Util.debug("#Average Total Cache Misses/(Total CH+Total CM) during Fetches: "+(100d*avgRun.totalCacheMissesDuringFetch)/(avgRun.totalCacheMissesDuringFetch+avgRun.totalCacheHitsDuringFetch)+"%",true);
//			Util.debug("#Average Total Latency during Fetches: "+avgRun.totalLatencyDuringFetch+" msec",true);
//			//Util.debug("#Average Total Disk Fetched Fragments: "+avgRun.totalCacheMisses,true);
//			//Util.debug("#Average Total Cache Fetched Fragments: "+avgRun.totalCacheHits,true);
//			Util.debug("~~~~~~~~~~~~~~~~~~~~~~~~~~~~",true);
//			
//		}
//		try {
//			Main.db.conn.close();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//}
