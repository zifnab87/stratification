package simulation;

public class Config {
	public static final Point UPPER_LEFT_STARTING_POINT = new Point(0,0);
	public static final int VIEWPORT_HEIGHT = 4;
	public static final int VIEWPORT_WIDTH = 3;

	//TILE
	public final static int TILE_WIDTH = 256;
	public final static int TILE_HEIGHT = 256;
	public final static int FRAGMENTS_PER_TILE = 6;
	
	//FRAGMENT
	public final static int FRAGMENT_SIZE = (TILE_HEIGHT*TILE_WIDTH)/FRAGMENTS_PER_TILE;
	
	
	public final static int RENDER_TIME = 10;
	public final static int DATABASE_FETCH_TIME = 3000;
	public final static int NETWORK_FETCH_TIME = 6000;

	public final static int COLORS = 3;
	
	public final static long SEED = 3l;
}
