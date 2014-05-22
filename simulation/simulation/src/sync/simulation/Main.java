package sync.simulation;

import static sync.simulation.Config.DATABASE_WIDTH;
import static sync.simulation.Config.VIEWPORT_HEIGHT;
import static sync.simulation.Config.VIEWPORT_WIDTH;
import static sync.simulation.Config.CACHE_SIZE;
import static sync.simulation.Config.THINK_TIME;
import static sync.simulation.Config.IMPORTANCE_METRIC;
import static sync.simulation.Config.FRAGMENT;
import static sync.simulation.Config.FRAGMENTS_PER_TILE;
import static sync.simulation.Config.RUNS;
import static sync.simulation.Config.MOVES;
import java.util.Random;
import java.util.Vector;






import sync.simulation.Cache;
import sync.simulation.Database;
import sync.simulation.Viewport;
import sync.simulation.events.UserMove;
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
		//db.init();
		db.clearCache();
		Util.debug("Starting Experiment");
		
		
		if (!FRAGMENT){
			UserMove.currentZoomLevel = FRAGMENTS_PER_TILE;
		}
		else{
			UserMove.currentZoomLevel = 1;
		}
		JumpRegion jump = new JumpRegion(Database.points(0,0));
		UserStudiesCombined usc = new UserStudiesCombined();
		UserStudySynthesizer uss = new UserStudySynthesizer();
		
		UserMove current = null;

		//Random rand = new Random();
		for (int w=6; w<=6; w++){
			uss.setWorkload(w);
			System.out.println("workload: "+w);
			db.clearCache();
			cache = new Cache();
			int memStart = 256;
			int memEnd = 256;
			//if (w!=6){
			//	memStart = 512;
			//	memEnd = 512;
			//}
			//else {
		//		memStart = 128;
		//		memEnd = 8192;
			//}
			for (int m=memStart; m<=memEnd; m=m*2){
				CACHE_SIZE = m;
				for (int f=0; f<=1; f++){
					if (f==0){
						FRAGMENT = false;
					}
					else {
						FRAGMENT = true;
					}
					db.clearCache();
					cache = new Cache();
					int weightStart = -5;
					int weightEnd = 5;
//					if (w==1){
//						weightStart = -2;
//						weightEnd = -2;
//					}
//					else if(w==4){
//						weightStart = -1;
//						weightEnd = -1;
//					}
//					else if(w==2){
//						weightStart = 4;
//						weightEnd = 4;
//					}
//					else if(w==5){
//						weightStart = 1;
//						weightEnd = 1;
//					}
//					else if(w==3){
//						weightStart = 3;
//						weightEnd = 3;
//					}
//					else if(w==6){
//						weightStart = -3;
//						weightEnd = -3;
//					}
					
					for (int weight=weightStart; weight<=weightEnd; weight++){
						IMPORTANCE_METRIC = weight;
						System.out.println("EVICTION METRIC:"+weight);
						
						Vector<Run> runs = new Vector<Run>();
						db.clearCache();
						cache = new Cache();
						System.gc();
						Vector<Double> vecForLatency = new Vector<Double>();
						//Vector<Double> vecForMissRatioVariance = new Vector<Double>();
//						vec.add(1.0);
//						vec.add(0.0);
//						vec.add(0.0);
//						vec.add(0.0);
//						System.out.println(Util.variance(vec));
						boolean warmup = true;
						for (int i=0; i<2*RUNS; i++){
							Util.debug("Run: "+i+ " warmup "+warmup);
							
														
							
							Run run = new Run();
							//for warmup
							if (!warmup){
								runs.add(run);
							}
							Random rand = new Random();
							//THINK_TIME = UserMove.nextFromDistribution(rand);
							if (!FRAGMENT){
								UserMove.currentZoomLevel = FRAGMENTS_PER_TILE;
							}
							else{
								UserMove.currentZoomLevel = 1;
							}
							if (current==null || warmup){
								current = new UserMove(db.randomPoint(),run,"pan");
							}
						
							
							Viewport viewport = new Viewport(VIEWPORT_HEIGHT, VIEWPORT_WIDTH,  current.point,null);
							int moves = 0;
							if (warmup){
								moves = 4*MOVES;
							}
							else {
								moves = MOVES;
							}
							for (int j=0; j<moves; j++){ //moves per run	
								//System.out.println("Run"+i+" Move "+j);
								System.gc();
								
								if (j%10==0){
						    		
						    		db.close();
						    	}	
								if (j%100==0){
									//System.out.println("~~~~~~~~~~~~~~~~");
								}
								//System.out.println("%%before"+current.point);
								current = UserStudySynthesizer.whatHappensNext(current);
						    	//System.out.println("%%"+current.movementType+" "+current.point);
						    	if (current.movementType.equals("ignore") || current.movementType.equals("stay")){//Tiles and Zoom Happened
						    		//System.out.println("bika");
						    		
						    		continue;
						    	}
						    	
						    	
						    	current.run = run;
						    	viewport = new Viewport(VIEWPORT_HEIGHT, VIEWPORT_WIDTH,  current.point,null);
						    	//current.viewport = viewport;
						    	jump = new JumpRegion(Database.points(0,0));
						    	current.run.totalMoves+=1;			
								Util.debug("Memory before Move:"+Main.cache.getQueue());
								Util.debug("Current Position we just moved: "+j+" "+current.point+ "move:"+current.movementType+" Zoom: "+UserMove.currentZoomLevel);
								current.viewportFetch();
								Main.updateStatisticsAndCache(current.point);
								Util.debug("Memory after Move:"+Main.cache.getQueue());
								
								//System.out.println(cache);
								Util.debug("Memory after Fetch:"+Main.cache.getQueue());
								Util.debug("Memory Space Used after Fetch "+Main.cache.sizeBeingUsed());
						    
							    //PREDICTOR
								
								Util.debug("Before Prefetch:"+cache);
								current.prefetch(jump, current.point);
								Main.updateStatisticsAndCache(current.point);
								Util.debug("After Prefetch:"+cache);
							
								Util.debug("#Cache Misses during Fetch in a Move: "+current.cacheMissesDuringFetch);
								Util.debug("#Cache Hits during Fetch in a Move: "+current.cacheHitsDuringFetch);
								Util.debug("#Latency during Fetch in a Move: "+current.latencyDuringFetch);
								if (!warmup){
									vecForLatency.add(current.latencyDuringFetch);
								}
								//current.run.misses.add(current.cacheMissesDuringFetch);
								//Util.debug("#Disk Fetched Fragments during Move: "+current.cacheMisses);
								
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
							if (warmup){
								warmup = false;
							}
							else {
								warmup = true;
							}
								
						}
						AverageRun avgRun = new AverageRun(runs);
						Util.debug("Runs: "+runs.size(),true);
						Util.debug("Cache: "+CACHE_SIZE,true);
						//Util.debug("Think Time = "+ THINK_TIME,true);
						Util.debug("Fragments = "+FRAGMENT,true);
						//if (FRAGMENT){
						//	Util.debug("Coverage = "+COVERAGE,true);
						//}
						Util.debug("#Average Moves Number: "+avgRun.totalMoves,true);
						//Util.debug("#Average Total Cache Misses during Fetches: "+avgRun.totalCacheMissesDuringFetch,true);
						//Util.debug("#Average Total Cache Hits during Fetches: "+avgRun.totalCacheHitsDuringFetch,true);
						Util.debug("#Average Total Cache Misses/(Total CH+Total CM) during Fetches: "+(100d*avgRun.totalCacheMissesDuringFetch)/(avgRun.totalCacheMissesDuringFetch+avgRun.totalCacheHitsDuringFetch)+"%");
						Util.debug("#Average Total Latency during Fetches: "+avgRun.totalLatencyDuringFetch+" msec ("+avgRun.totalLatencyDuringFetch/avgRun.totalMoves+")");
						//Util.debug("#Average Total Disk Fetched Fragments: "+avgRun.totalCacheMisses,true);
						//Util.debug("#Average Total Cache Fetched Fragments: "+avgRun.totalCacheHits,true);
						Util.debug(" ",true);
						//System.out.println("!!!!"+vecForLatency);
						Util.debug("data(workload= "+w+", cache="+CACHE_SIZE+", fragments= "+FRAGMENT+", metric="+weight+", missratio="+(100d*avgRun.totalCacheMissesDuringFetch)/(avgRun.totalCacheMissesDuringFetch+avgRun.totalCacheHitsDuringFetch)+"%, movelatency = "+Util.average(vecForLatency)+" msec, Latency Variance = "+Util.variance(vecForLatency)+", Latency Stdev = "+Math.sqrt(Util.variance(vecForLatency))+")",true);
						Util.debug("dataraw("+w+","+CACHE_SIZE+","+FRAGMENT+","+weight+","+(100d*avgRun.totalCacheMissesDuringFetch)/(avgRun.totalCacheMissesDuringFetch+avgRun.totalCacheHitsDuringFetch)+","+avgRun.totalLatencyDuringFetch/avgRun.totalMoves+","+Util.variance(vecForLatency)+","+Math.sqrt(Util.variance(vecForLatency))+")",true);
						Util.debug("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~",true);   	
					}
				}
			}
		}
	}
	
	
	
	
	public static void updateStatisticsAndCache(Point current){
		
		for (int y=0; y<DATABASE_WIDTH; y++){
			for (int x=0; x<DATABASE_WIDTH; x++){
					UserStudiesCombined.tiles[y][x].updateImportance(current);
			}
		}
		Main.cache.updateImportances(current);
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
