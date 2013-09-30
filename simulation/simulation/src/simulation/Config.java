package simulation;

public class Config {
	
	//DATABASE 
	public static final int DATABASE_TILES_NUM = 100;//625;
	public static final int DATABASE_WIDTH = 10;//25;
	//VIEWPORT
	public static final Point UPPER_LEFT_STARTING_POINT = new Point(0,0);
	public static final int VIEWPORT_HEIGHT = 3;
	public static final int VIEWPORT_WIDTH = 4;

	//TILE
	public final static int TILE_WIDTH = 256;
	public final static int TILE_HEIGHT = 256;
	public final static int FRAGMENTS_PER_TILE = 800;
	public final static int COLORS = 3;
	
	//FRAGMENT
	public final static int FRAGMENT_SIZE = (TILE_HEIGHT*TILE_WIDTH)/FRAGMENTS_PER_TILE;
	
	//PREDICTOR
	public static int PREFETCH_DISTANCE = 10;  
	
	
	
	public final static int RENDER_TIME = 10;
	public final static int DATABASE_FETCH_TIME = 3000;
	public final static int NETWORK_FETCH_TIME = 6000;


	
	public final static long SEED = 3l;
}
