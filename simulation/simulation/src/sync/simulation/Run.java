package sync.simulation;

import java.util.Vector;


//a sequence of UserMoves
public class Run {
	public int totalCacheMissesDuringFetch = 0;
	public int totalCacheHitsDuringFetch = 0;
	public double totalLatencyDuringFetch = 0;
	public int totalCacheMisses = 0;
	public int totalCacheHits = 0;
	public int totalMoves = 0;
	public  Vector<Double> misses = new Vector<Double>();
	
	public static int count = 0;
	
	public Run(){
		count++;
	}
}

