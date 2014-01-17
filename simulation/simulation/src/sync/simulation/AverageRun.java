package sync.simulation;

import java.util.Vector;

public class AverageRun {
	public double totalCacheMissesDuringFetch = 0;
	public double totalLatencyDuringFetch = 0;
	public double totalCacheMisses = 0;
	public double totalCacheHits = 0;
	public double totalMoves = 0;
	
	public double minLatency = Integer.MAX_VALUE;
	public double maxLatency = Integer.MIN_VALUE;
	
	
	public AverageRun(Vector<Run> runs){
		int size = runs.size();
		for(int i=0; i<size; i++){
			
			if (runs.get(i).totalLatencyDuringFetch < minLatency){
				minLatency = runs.get(i).totalLatencyDuringFetch;
			}
			if (runs.get(i).totalLatencyDuringFetch > maxLatency){
				maxLatency = runs.get(i).totalLatencyDuringFetch;
			}
			
			totalCacheMissesDuringFetch+=runs.get(i).totalCacheMissesDuringFetch;
			totalLatencyDuringFetch+=runs.get(i).totalLatencyDuringFetch;
			totalCacheMisses+=runs.get(i).totalCacheMisses;
			totalCacheHits+=runs.get(i).totalCacheHits;
			totalMoves+=runs.get(i).totalMoves;
			
			
		}
		
		totalCacheMissesDuringFetch = totalCacheMissesDuringFetch/size*1.0d;
		totalLatencyDuringFetch = totalLatencyDuringFetch/size*1.0d;
		totalCacheMisses = totalCacheMisses/size*1.0d;
		totalCacheHits = totalCacheHits/size*1.0d;
		totalMoves = totalMoves/size*1.0d;
	}
}
