package simulation;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Tile {
	public final static int RENDER_TIME = 10;
	public final static int DATABASE_FETCH_TIME = 3000;
	public final static int NETWORK_FETCH_TIME = 6000;
	public final static int WIDTH = 256;
	public final static int HEIGHT = 256;
	public final static int COLORS = 3;
	public int id;
	public int[][][] pixels;
	private Map<Integer,Fragment> fragments = new HashMap<Integer, Fragment>();
	private static long seed = 3l;
	private static Random random = new Random(seed);
	public static int tileCounter = 0;
	
	public Tile(int id, int[][][] pixels){
		this.id = id;
		this.pixels = pixels;
	}

	public int getFragmentNumber(){
		return fragments.size();
	}
	
	
	public static Tile random(){
		int tileId = Tile.tileCounter++;
		int[][][] pixels = new int[Tile.HEIGHT][Tile.WIDTH][Tile.COLORS];
		for(int i=0; i<Tile.HEIGHT; i++){
			for(int j=0; j<Tile.WIDTH; j++){
				for (int w=0; w<Tile.COLORS; w++){
					pixels[i][j][w] = random.nextInt(255);
				}
			}
		}
		return new Tile(tileId,pixels);
	}
	
	public void addFragment(Fragment fragm){
		fragments.put(fragm.id, fragm);
	}
	
	public Fragment getFragment(int fragmId){
		return fragments.get(fragmId);
	}
	
	public void containsFragment(Fragment fragm){
		fragments.containsKey(fragm.id);
	}
	
	
}
