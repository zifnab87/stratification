package sync.simulation;

import sync.simulation.Point;
import sync.simulation.monitor.Distribution;

public class Config {
	
	public static boolean DEBUG = false;
	public  static int RUNS = 10;
	public static boolean PREFETCH = true;
	
	//USER
	public static int THINK_TIME = 32;
	public static Distribution DISTRIBUTION = new Distribution(0.1,0.5,0.1,0.3);

	public static boolean FRAGMENT = true; 
	public  static String WORKLOAD_FILE = "workload_4";
	//CACHE (fragmentcount size)
	public static  int CACHE_SIZE = 64;
	
	//DATABASE 
	public static  int DATABASE_TILES_NUM = 625;//625;
	public static  int DATABASE_WIDTH = 25;//25;
	public static  boolean CONTIG_FRAGM_IN_SINGLE_QUERY = false;
	//VIEWPORT
	public static  Point UPPER_LEFT_STARTING_POINT = new Point(2,2);
	public static  int VIEWPORT_HEIGHT = 1;
	public static  int VIEWPORT_WIDTH = 1;

	//TILE
	public  static int TILE_WIDTH = 32; //256
	public  static int TILE_HEIGHT = 32; //256
	public  static int FRAGMENTS_PER_TILE = 8;
	public  static int COLORS = 3;
	
	//FRAGMENT
	public  static int FRAGMENT_SIZE = (TILE_HEIGHT*TILE_WIDTH)/FRAGMENTS_PER_TILE;
	
	//PREDICTOR
	public static boolean SKIP_PREDICTIONS = false;
	public static double CUTOFF = 30; // take the first CUTTOFF nodes with the highest probability in each predictor expansion 
	public static int WAVES = 1;
	// small numbers -> aggressive
	
	
	
	public  static double SIMULATION_FACTOR = 100d; // unsafe for values >400
	public  static int RENDER_TIME = (int) Math.ceil(10 / SIMULATION_FACTOR);
	public  static int DATABASE_TILE_FETCH_TIME = (int) Math.ceil(3200 / SIMULATION_FACTOR);
	public  static int DATABASE_FRAGMENT_FETCH_TIME = (int) Math.ceil(400 / SIMULATION_FACTOR); 
	public  static int NETWORK_TIME_FETCH_TIME = (int) Math.ceil(2000 / SIMULATION_FACTOR);
	
	public  static int NETWORK_FRAGMENT_FETCH_TIME = (int) Math.ceil(300 / SIMULATION_FACTOR);

	public  static int USER_MOVEMENT_TIME = (int) Math.ceil(2000 /SIMULATION_FACTOR);
	
	public  static long SEED = 3l;
	

	
	public  static int EXPERIMENT_TIME = 60; //seconds
	

	
	
	
}
