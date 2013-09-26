package simulation;

import java.text.DecimalFormat;
import static simulation.Config.FRAGMENTS_PER_TILE;
public class Predictor {
	
	public final static float[] LOD_INTERVALS = lodIntervals(FRAGMENTS_PER_TILE);
	
	public static void getLikelihood(Tile tile){
		float likelihood = 1f;
		tile.setLikelihood(likelihood);
	}
	
	public static int likelihoodToLOD(double likelihood){
		int lod = 1;
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(3);
		float likelihoodf = Float.valueOf(df.format(likelihood));
		for (int i=0; i<LOD_INTERVALS.length-1; i++){
			if (likelihoodf == 0.0d){
				return 1;
			}
			if (likelihoodf>LOD_INTERVALS[i] && likelihoodf<=LOD_INTERVALS[i+1]){
				return lod;
			}
			else {
				lod++;
			}
		}
		return lod;
	}
	
	public static float[] lodIntervals(int fragments){
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(3);
		float[] result = new float[fragments+1];
		result[0] = 0.0f;
	
		float diff = Float.valueOf(df.format(1.0/fragments));
		float previous = 0f;
		for(int i=1; i<result.length-1; i++){
			previous = Float.valueOf(df.format(previous + diff));
			result[i] = previous;
		}
		
		return result;
	}
	
}
