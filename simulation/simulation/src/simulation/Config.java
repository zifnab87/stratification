package simulation;

import simulation.Point;

public class Config {
	
	public static boolean debug = false;
	public static boolean PREFETCH = true;
	
	//USER
	public static int THINK_TIME = 15;

	public static boolean FRAGMENT = false; 
	public final static String WORKLOAD_FILE = "workload_4";
	//CACHE (fragmentcount size)
	public static final int CACHE_SIZE = 64;
	
	//DATABASE 
	public static final int DATABASE_TILES_NUM = 625;//625;
	public static final int DATABASE_WIDTH = 25;//25;
	//VIEWPORT
	public static final Point UPPER_LEFT_STARTING_POINT = new Point(2,2);
	public static final int VIEWPORT_HEIGHT = 1;
	public static final int VIEWPORT_WIDTH = 1;

	//TILE
	public final static int TILE_WIDTH = 32; //256
	public final static int TILE_HEIGHT = 32; //256
	public final static int FRAGMENTS_PER_TILE = 8;
	public final static int COLORS = 3;
	
	//FRAGMENT
	public final static int FRAGMENT_SIZE = (TILE_HEIGHT*TILE_WIDTH)/FRAGMENTS_PER_TILE;
	
	//PREDICTOR
	public static boolean SKIP_PREDICTIONS = false;
	public static double CUTOFF = 4; // take the first CUTTOFF nodes with the highest probability in each predictor expansion 
	public static int WAVES = 1;
	// small numbers -> aggressive
	
	
	
	public final static double SIMULATION_FACTOR = 100d; // unsafe for values >400
	public final static int RENDER_TIME = (int) Math.ceil(10 / SIMULATION_FACTOR);
	public final static int DATABASE_TILE_FETCH_TIME = (int) Math.ceil(3200 / SIMULATION_FACTOR);
	public final static int DATABASE_FRAGMENT_FETCH_TIME = (int) Math.ceil(400 / SIMULATION_FACTOR); 
	public final static int NETWORK_TIME_FETCH_TIME = (int) Math.ceil(2000 / SIMULATION_FACTOR);
	
	public final static int NETWORK_FRAGMENT_FETCH_TIME = (int) Math.ceil(300 / SIMULATION_FACTOR);

	public final static int USER_MOVEMENT_TIME = (int) Math.ceil(2000 /SIMULATION_FACTOR);
	
	public final static long SEED = 3l;
	

	
	public final static int EXPERIMENT_TIME = 60; //seconds
	
	
	
}
