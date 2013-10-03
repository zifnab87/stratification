package simulation;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static simulation.Config.FRAGMENTS_PER_TILE;
import static simulation.Config.PREFETCH_DISTANCE;
public class Predictor {
	
	public final static float[] LOD_INTERVALS = lodIntervals(FRAGMENTS_PER_TILE);
	public static Map<Integer,Double> likelihoods = new HashMap<Integer, Double>();
	
	


	
	public static double distance(Point a, Point b){
		double dist = Math.sqrt(Math.pow(a.y-b.y,2)+Math.pow(a.x-b.x,2));
		return Math.floor(dist);
	}
	public static double distance(Tile tile,Viewport viewport){
		Point currentCenterIndex = viewport.center;
		Point tileIndex = tile.point;
		return distance(currentCenterIndex,tileIndex);
	}
	
	public static void trainDatabase(Database db){
		Predictor.constantTrain(db);
		Predictor.spiralTrain(db);
	}
	
	public static double getLikelihood(Tile tile,Viewport viewport){
		double tileStaticLikelihood = getLikelihood(tile.point,viewport);
		//double distance = distance(tile);
		// 40% static likelihood
		// 60% distance
		// min of (max_distance - distance + viewport_width )/max_distance and 0
		//return (4*tileStaticLikelihood + 6*Math.min((PREFETCH_DISTANCE-distance)/PREFETCH_DISTANCE,0d))/10d;		
		return tileStaticLikelihood;
	}
	
	public static double getLikelihood(int y,int x,Viewport viewport){
		return Predictor.getLikelihood(new Point(y,x),viewport);
	}
	//CURRENT Viewport to determine the distance
	public static double getLikelihood(Point index,Viewport viewport){
		if (!viewport.contains(index)){
			return Predictor.getLikelihood(index.hashCode(),viewport);
		}
		else {
			return 1.0d;
		}
	}
	
	private static double getLikelihood(int index,Viewport viewport){
		Double likelihood = Predictor.likelihoods.get(index);
		if (likelihood!=null){
			return Predictor.likelihoods.get(index);
		}
		else {
			return 0.0d;
		}
		
	}
	
	public static int getLOD(Point index,Viewport viewport){
		return Predictor.getLOD(index.hashCode(),viewport);
	}
	
	public static int getLOD(int index,Viewport viewport){
		return Predictor.likelihoodToLOD(Predictor.getLikelihood(index,viewport));
	}
	
	
	
	
	
	private static int likelihoodToLOD(double likelihood){
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
		return Math.min(lod,FRAGMENTS_PER_TILE-1);
	}
	
	private static float[] lodIntervals(int fragments){
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
	private static void constantTrain(Database db){
		Set<Integer> keys = db.tiles.keySet();
		for(Integer key: keys){
			Random r = new Random();
			double randomValue = 0.0d + (0.25d - 0.0d) * r.nextDouble();
			likelihoods.put(key, randomValue);
		}
	}
	
	private static void spiralTrain(Database db){
		int width = simulation.Config.DATABASE_WIDTH;
		boolean horizontal = true;
		int length = width;
		boolean firsttime = true;
		boolean incrementing = true;
		boolean flipflop = false;
		int i = 0;
		int j = 0;
		while(length>=2){
			for(int x=0; x<length-1; x++){
				if (horizontal){
					if (incrementing){
						i++;
					}
					else { //decrementing
						i--;
					}
				}
				else {
					if (incrementing){
						j++;
					}
					else { //decrementing
						j--;
						
					}
				}
				Random r = new Random();
				double randomValue = 0.25d + (1.0d - 0.25d) * r.nextDouble();
				likelihoods.put(new Point(j,i).hashCode(), randomValue);
			}
			if (flipflop){
				incrementing = !incrementing;
			}
			flipflop=!flipflop;
			if (!firsttime){
				length--;
			}
			else {
				firsttime = false;
			}
			horizontal = !horizontal;
		}
				
	}
	
}
