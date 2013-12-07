package simulation;
import static simulation.Config.COLORS;
import static simulation.Config.FRAGMENTS_PER_TILE;
import static simulation.Config.FRAGMENT_SIZE;
import static simulation.Config.SEED;

import java.util.Random;
import java.util.Vector;

public class Fragment {
//	public int num;
//
//	public byte[][] pixels;
//	private static long seed = 3l;
//	private static Random random = new Random(seed);
//	//public static int fragmentIdCounter = 0;
//	
//	
//	public final static int[][] FragmentIndexPositions = initIndexPositions();
//	
//	
//	private final static int[][] initIndexPositions(){
//		 int[][] pixels = new int[FRAGMENTS_PER_TILE][FRAGMENT_SIZE];
//		 /*int count = 0;
//		 for (int i=0; i<FRAGMENTS_PER_TILE; i++){
//			 for (int j=0; j<FRAGMENT_SIZE; j++){
//				 pixels[i][j] = count++;
//			 }
//		 }*/
//		 return pixels;
//	}
//	
//	public Fragment(int fragmentNumber, byte[][] pixels){
//		this.num = fragmentNumber;
//		this.pixels = pixels;
//	}
//	
//	public int[] getPixelIndexesOfFragment(){
//		return FragmentIndexPositions[this.num];
//	}
//	
//	public byte[] getPixel(int pixelIndex){
//		return pixels[pixelIndex];
//	}
//	
//	public static Fragment randomizer(int fragmentNumber){
//		byte[][] pixels = new byte[FRAGMENT_SIZE][COLORS];
//		/*for (int i=0; i<FRAGMENT_SIZE; i++){
//			for (int c=0; c<COLORS; c++){
//				pixels[i][c] = (byte) random.nextInt(255);
//			}
//		}*/
//		return new Fragment(fragmentNumber,pixels);
//	}
//	
//	public static int chooseRandomFromNumbers(Vector<Integer> collection){
//		Random randomGenerator = new Random(SEED);
//		int index = randomGenerator.nextInt(collection.size());
//		return collection.get(index);
//		
//	}
}
