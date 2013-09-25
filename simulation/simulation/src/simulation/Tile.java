package simulation;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

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
	public byte[][][] pixels;
	private static int tileIdCounter = 0;
	private static int colCounter = 0;
	private static int rowCounter = 0;
	public int lod;
	public float likelihood;
	
	public int x;
	public int y;
	
	public static Comparator<Tile> lodComparator = new Comparator<Tile>(){
		@Override
		public int compare(Tile t1, Tile t2) {
            return (int) (t1.lod - t2.lod);
        }
	};
	
	public Tile(int y, int x, byte[][][] pixels){
		
		this.pixels = pixels;
		this.x = x;
		this.y = y;
		this.id = ((y+"-"+x).hashCode());
		System.out.println(this.id);
		
	}

	public int getFragmentNumber(){
		return fragments.size();
	}
	
	
	public static Tile random(){
		//int tileId = Tile.tileIdCounter++;
		
		if ((colCounter)%25 == 0 && colCounter!=0){
			rowCounter++;
			colCounter=0;
		}

		byte[][][] pixels = new byte[Tile.HEIGHT][Tile.WIDTH][Tile.COLORS];
		for (int i=0; i<Fragment.FRAGMENTS_PER_TILE; i++){ //8
			Fragment fragm = Fragment.random(i);
			int[] pixelIndexesOfFragment = fragm.getPixelIndexesOfFragment();
			for (int j=0; j<Fragment.FRAGMENT_SIZE; j++){ 
				int y = pixelIndexesOfFragment[j] % Tile.WIDTH;
				int x = pixelIndexesOfFragment[j] / Tile.WIDTH;
				pixels[y][x] = fragm.getPixel(j);
			}
		}
		return new Tile(rowCounter,colCounter++,pixels);
	}
	
	public void addFragment(Fragment fragm){
		fragments.put(fragm.num, fragm);	
	}
	
	public void removeFragment(int fragmNumber){
		fragments.remove(fragmNumber);
	}
	
	public Fragment getFragment(int fragmNumber){
		return fragments.get(fragmNumber);
	}
	
	public void containsFragment(Fragment fragm){
		fragments.containsKey(fragm.num);
	}
	
	
}
