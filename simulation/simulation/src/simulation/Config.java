package simulation;

public class Config {
	
	public static boolean debug = false;
	
	public static boolean PREFETCH = true;
	public static boolean FRAGMENT = true; 
	//DATABASE 
	public static final int DATABASE_TILES_NUM = 625;//625;
	public static final int DATABASE_WIDTH = 25;//25;
	//VIEWPORT
	public static final Point UPPER_LEFT_STARTING_POINT = new Point(0,0);
	public static final int VIEWPORT_HEIGHT = 3;
	public static final int VIEWPORT_WIDTH = 4;

	//TILE
	public final static int TILE_WIDTH = 256;
	public final static int TILE_HEIGHT = 256;
	public final static int FRAGMENTS_PER_TILE = 8;
	public final static int COLORS = 3;
	
	//FRAGMENT
	public final static int FRAGMENT_SIZE = (TILE_HEIGHT*TILE_WIDTH)/FRAGMENTS_PER_TILE;
	
	//PREDICTOR
	public static int PREFETCH_DISTANCE = 1;  
	public static int PROBABILITY_CUTOFF = 20;
	
	
	public final static double SIMULATION_FACTOR = 1000d;
	public final static int RENDER_TIME = (int) Math.ceil(10 / SIMULATION_FACTOR);
	public final static int DATABASE_TILE_FETCH_TIME = (int) Math.ceil(3300 / SIMULATION_FACTOR);
	public final static int DATABASE_FRAGMENT_FETCH_TIME = (int) Math.ceil(475 / SIMULATION_FACTOR); 
	public final static int NETWORK_TIME_FETCH_TIME = (int) Math.ceil(2000 / SIMULATION_FACTOR);
	
	public final static int NETWORK_FRAGMENT_FETCH_TIME = (int) Math.ceil(300 / SIMULATION_FACTOR);

	public final static int USER_MOVEMENT_TIME = (int) Math.ceil(2000 /SIMULATION_FACTOR);
	
	public final static long SEED = 3l;
	
	public final static int EXPERIMENT_TIME = 60; //seconds
	
}
