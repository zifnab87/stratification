package simulation;

import java.util.Random;

public class Fragment {
	public int num;
	public final static int FRAGMENTS_PER_TILE = 8;
	public final static int FRAGMENT_SIZE = (Tile.HEIGHT*Tile.WIDTH)/Fragment.FRAGMENTS_PER_TILE;
	public byte[][] pixels;
	private static long seed = 3l;
	private static Random random = new Random(seed);
	//public static int fragmentIdCounter = 0;
	
	
	public final static int[][] FragmentIndexPositions = initIndexPositions();
	
	
	private final static int[][] initIndexPositions(){
		 int[][] pixels = new int[FRAGMENTS_PER_TILE][FRAGMENT_SIZE];
		 int count = 0;
		 for (int i=0; i<FRAGMENTS_PER_TILE; i++){
			 for (int j=0; j<FRAGMENT_SIZE; j++){
				 pixels[i][j] = count++;
			 }
		 }
		 return pixels;
	}
	
	public Fragment(int fragmentNumber, byte[][] pixels){
		this.num = fragmentNumber;
		this.pixels = pixels;
	}
	
	public int[] getPixelIndexesOfFragment(){
		return FragmentIndexPositions[this.num];
	}
	
	public byte[] getPixel(int pixelIndex){
		return pixels[pixelIndex];
	}
	
	public static Fragment random(int fragmentNumber){
		byte[][] pixels = new byte[FRAGMENT_SIZE][Tile.COLORS];
		for (int i=0; i<FRAGMENT_SIZE; i++){
			for (int c=0; c<Tile.COLORS; c++){
				pixels[i][c] = (byte) random.nextInt(255);
			}
		}
		return new Fragment(fragmentNumber,pixels);
	}
}
