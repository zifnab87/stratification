package sync.simulation;

import sync.simulation.monitor.Distribution;


public class Config {
	
	public static boolean DEBUG = false;
	public  static int RUNS = 2;
	public static int WARMUP = 1;
	public static boolean PREFETCH = true;
	
	//=============== USER =================================================
	public static int THINK_TIME = 2048;
	public static Distribution DISTRIBUTION = new Distribution(0.25,0.25,0.25,0.25);
	//public static Distribution DISTRIBUTION = new Distribution(0.01,0.33,0.33,0.33);
	//public static Distribution DISTRIBUTION = new Distribution(0.1,0.5,0.1,0.3);
	//public static Distribution DISTRIBUTION = new Distribution(0.0,0.5,0.25,0.25);
	
	//public static Distribution DISTRIBUTION = new Distribution(0.0,0.5,0.0,0.5);
	//public static Distribution DISTRIBUTION = new Distribution(0.0,0.9,0.0,0.1);
	//public static Distribution DISTRIBUTION = new Distribution(0.0,1,0.0,0.0);
	public static String USER_THINK_DISTR = "NegativeExponential";
	public static boolean FRAGMENT = true 	 ; 
	public  static String WORKLOAD_FILE = "workload_4";
	//CACHE (fragmentcount size)
	public static  int CACHE_SIZE = 8192;

	//========== DATABASE =================================================
	public static  int DATABASE_TILES_NUM = 625;//625;
	public static  int DATABASE_WIDTH = 25;//25;
	public static  boolean CONTIG_FRAGM_IN_SINGLE_QUERY = true;
	
	//========== VIEWPORT ==================================================
	
	//public static  Point UPPER_LEFT_STARTING_POINT = new Point(12,12);
	//public static  Point UPPER_LEFT_STARTING_POINT = new Point(2,12);
	public static  Point UPPER_LEFT_STARTING_POINT = new Point(2,2);
	//public static  Point UPPER_LEFT_STARTING_POINT = new Point(2,12);
	//public static  Point UPPER_LEFT_STARTING_POINT = new Point(2,2);
	//public static  Point UPPER_LEFT_STARTING_POINT = new Point(2,2);
	//public static  Point UPPER_LEFT_STARTING_POINT = new Point(2,2);
	public static  int VIEWPORT_HEIGHT = 1;
	public static  int VIEWPORT_WIDTH = 1;

	//============= PREFETCH REGION ===============================================
	
	public static int PREFETCH_REGION_WIDTH = 3;
	
	//============= JUMP REGION ===================================================
	
	public static int JUMP_REGION_WIDTH = 25;
	
	
	
	
	//========== TILE ======================================================
	public  static int TILE_WIDTH = 256; //256
	public  static int TILE_HEIGHT = 256; //256
	public  static int FRAGMENTS_PER_TILE = 16;
	public  static int COLORS = 3;
	
	//========== FRAGMENT ===================================================
	public  static int FRAGMENT_SIZE = (TILE_HEIGHT*TILE_WIDTH)/FRAGMENTS_PER_TILE;
	public static double COVERAGE = 0.9; // percentage of the cummulative distribution to prefetch fragments
	
	//========== PREDICTOR ==================================================
	public static double JUMP_WEIGHT_FACTOR = 0.0;
	public static double DISTANCE_WEIGHT_FACTOR = 0.0;
	public static int IMPORTANCE_METRIC = 0;
	
	public static boolean SKIP_PREDICTIONS = false;
	public static double CUTOFF = 30; // take the first CUTTOFF nodes with the highest probability in each predictor expansion 
	public static int WAVES = 10;
	// small numbers -> aggressive
	
	
	
	/*public  static double SIMULATION_FACTOR = 100d; // unsafe for values >400
	public  static int RENDER_TIME = (int) Math.ceil(10 / SIMULATION_FACTOR);
	public  static int DATABASE_TILE_FETCH_TIME = (int) Math.ceil(3200 / SIMULATION_FACTOR);
	public  static int DATABASE_FRAGMENT_FETCH_TIME = (int) Math.ceil(400 / SIMULATION_FACTOR); 
	public  static int NETWORK_TIME_FETCH_TIME = (int) Math.ceil(2000 / SIMULATION_FACTOR);
	
	public  static int NETWORK_FRAGMENT_FETCH_TIME = (int) Math.ceil(300 / SIMULATION_FACTOR);

	public  static int USER_MOVEMENT_TIME = (int) Math.ceil(2000 /SIMULATION_FACTOR);
	
	public  static long SEED = 3l;
	

	
	public  static int EXPERIMENT_TIME = 60; //seconds*/
	
	
	
	
}
