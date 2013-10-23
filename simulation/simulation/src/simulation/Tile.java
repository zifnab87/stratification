package simulation;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import simulation.monitor.Monitor;

import static simulation.Config.TILE_WIDTH;
import static simulation.Config.TILE_HEIGHT;
import static simulation.Config.COLORS;
import static simulation.Config.FRAGMENTS_PER_TILE;
import static simulation.Config.FRAGMENT_SIZE;
import static simulation.Config.DATABASE_WIDTH;
import static simulation.Config.debug;

public class Tile {

	private Map<Integer,Fragment> fragments = new HashMap<Integer, Fragment>();

	
	
	public int id;
	public byte[][][] pixels;
	private static int tileIdCounter = 0;
	private static int colCounter = 0;
	private static int rowCounter = 0;
	public int lod;
	public double likelihood;
	public Point point; //index
	public boolean cached = false;
	
	
	public static Comparator<Tile> likelihoodComparator = new Comparator<Tile>(){
		@Override
		public int compare(Tile t1, Tile t2) {
		     return (int) (t1.likelihood - t2.likelihood);
		}
	};
	
	public static Tile copyTile(Tile tile){
		Tile newTile = new Tile(tile.point,tile.pixels);
		newTile.likelihood = tile.likelihood;
		newTile.lod = tile.lod;
		return newTile; 
	}
	
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
	
	
	public void setCached(boolean cached){
		this.cached = cached;
	}
	
	public boolean isCached(){
		return this.cached;
	}
	
	public void setPixels(byte[][][] pixels){
		this.pixels = pixels;
	}
	
	
	
	
	public static Tile randomizer(){
		//int tileId = Tile.tileIdCounter++;
		
		if ((colCounter)%DATABASE_WIDTH == 0 && colCounter!=0){
			rowCounter++;
			colCounter=0;
		}

		byte[][][] pixels = new byte[TILE_HEIGHT][TILE_WIDTH][COLORS];
		Tile toReturn = new Tile(new Point(rowCounter,colCounter++));
		for (int i=1; i<=FRAGMENTS_PER_TILE; i++){ //8
			Fragment fragm = Fragment.randomizer(i);
			//int[] pixelIndexesOfFragment = fragm.getPixelIndexesOfFragment();
			/*for (int j=0; j<FRAGMENT_SIZE; j++){ 
				int y = pixelIndexesOfFragment[j] % TILE_WIDTH;
				int x = pixelIndexesOfFragment[j] / TILE_WIDTH;
				pixels[y][x] = fragm.getPixel(j);
			}*/
			toReturn.addFragment(fragm);
			
		}
		toReturn.setPixels(pixels);
		return toReturn;
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
	
	public String toString(){
		String str;
		if (this.isCached()){
			str = "Tile("+this.point.y+","+this.point.x+",LOD="+this.lod+",Likelihood="+this.likelihood+")";
		}
		else {
			 str = "Tile("+this.point.y+","+this.point.x+")";
		}
		return str;
	}
	
	public static Vector<Integer> getMissingFragmentIdsTillLOD(int oldLOD,int newLOD){
		Vector<Integer>  fragmentIds = new Vector<Integer>();
		if (oldLOD<newLOD && oldLOD <= FRAGMENTS_PER_TILE && newLOD <= FRAGMENTS_PER_TILE){
			for (int fragmNum=oldLOD+1; fragmNum<=newLOD; fragmNum++){
				fragmentIds.add(fragmNum);
			}
		}
		return fragmentIds;
	}
	
	public static Vector<Integer> getAllFragmentIds(){
		return getMissingFragmentIdsTillLOD(0,FRAGMENTS_PER_TILE);
	}
	
	public static Vector<Integer> getMissingFragmentIdsTillFull(int oldLOD){
		return getMissingFragmentIdsTillLOD(oldLOD, FRAGMENTS_PER_TILE);
	}
	
	
}
