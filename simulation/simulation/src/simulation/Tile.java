package simulation;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Tile {

	private Map<Integer,Fragment> fragments = new HashMap<Integer, Fragment>();
	private static long seed = 3l;
	//private static Random random = new Random(seed);
	
	public final static int RENDER_TIME = 10;
	public final static int DATABASE_FETCH_TIME = 3000;
	public final static int NETWORK_FETCH_TIME = 6000;
	public final static int WIDTH = 256;
	public final static int HEIGHT = 256;
	public final static int COLORS = 3;
	public int id;
	public int[][][] pixels;
	public static int tileIdCounter = 0;
	
	public Tile(int id, int[][][] pixels){
		this.id = id;
		this.pixels = pixels;
	}

	public int getFragmentNumber(){
		return fragments.size();
	}
	
	
	public static Tile random(){
		int tileId = Tile.tileIdCounter++;
		int[][][] pixels = new int[Tile.HEIGHT][Tile.WIDTH][Tile.COLORS];
		for (int i=0; i<Fragment.FRAGMENTS_PER_TILE; i++){ //8
			Fragment fragm = Fragment.random(i);
			int[] pixelIndexesOfFragment = fragm.getPixelIndexesOfFragment();
			for (int j=0; j<Fragment.FRAGMENT_SIZE; j++){ 
				int y = pixelIndexesOfFragment[j] % Tile.WIDTH;
				int x = pixelIndexesOfFragment[j] / Tile.WIDTH;
				pixels[y][x] = fragm.getPixel(j);
			}
		}
		return new Tile(tileId,pixels);
	}
	
	public void addFragment(Fragment fragm){
		fragments.put(fragm.num, fragm);
		
	}
	
	public Fragment getFragment(int fragmId){
		return fragments.get(fragmId);
	}
	
	public void containsFragment(Fragment fragm){
		fragments.containsKey(fragm.num);
	}
	
	
}
