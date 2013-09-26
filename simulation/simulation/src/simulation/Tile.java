package simulation;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import static simulation.Config.TILE_WIDTH;
import static simulation.Config.TILE_HEIGHT;
import static simulation.Config.COLORS;
import static simulation.Config.FRAGMENTS_PER_TILE;
import static simulation.Config.FRAGMENT_SIZE;

public class Tile {

	private Map<Integer,Fragment> fragments = new HashMap<Integer, Fragment>();

	
	
	public int id;
	public byte[][][] pixels;
	private static int tileIdCounter = 0;
	private static int colCounter = 0;
	private static int rowCounter = 0;
	private int lod;
	private double likelihood;
	public Point point; //index
	
	
	
	
	public static Comparator<Tile> likelihoodComparator = new Comparator<Tile>(){
		@Override
		public int compare(Tile t1, Tile t2) {
            return (int) (t1.likelihood - t2.likelihood);
        }
	};
	
	public Tile(Point point){
		this.point = point;
		this.id = this.point.hashCode();
	}
	
	public Tile(Point point, byte[][][] pixels){
		
		this.pixels = pixels;
		this.point = point;
		this.id = this.point.hashCode();
		
	}

	public int getFragmentNumber(){
		return fragments.size();
	}
	
	
	public static Tile randomizer(){
		//int tileId = Tile.tileIdCounter++;
		
		if ((colCounter)%25 == 0 && colCounter!=0){
			rowCounter++;
			colCounter=0;
		}

		byte[][][] pixels = new byte[TILE_HEIGHT][TILE_WIDTH][COLORS];
		for (int i=0; i<FRAGMENTS_PER_TILE; i++){ //8
			Fragment fragm = Fragment.randomizer(i);
			int[] pixelIndexesOfFragment = fragm.getPixelIndexesOfFragment();
			for (int j=0; j<FRAGMENT_SIZE; j++){ 
				int y = pixelIndexesOfFragment[j] % TILE_WIDTH;
				int x = pixelIndexesOfFragment[j] / TILE_WIDTH;
				pixels[y][x] = fragm.getPixel(j);
			}
		}
		return new Tile(new Point(rowCounter,colCounter++),pixels);
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
	
	
	public boolean containsFragment(int fragmNumber){
		return fragments.containsKey(fragmNumber);
	}
	
	
	public boolean isFull(){ //contains as many fragments as possible
		return fragments.size() == FRAGMENTS_PER_TILE;
		
	}

	public void setLikelihood(double likelihood) {
		this.likelihood = likelihood;
		this.lod = Predictor.likelihoodToLOD(likelihood);
	}
	
	public double getLikelihood(){
		return this.likelihood = likelihood;
	}
	
	public int getLOD(){
		return this.lod;
	}
	
	
}
