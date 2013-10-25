package simulation;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import static simulation.Config.FRAGMENTS_PER_TILE;
import static simulation.Config.PREFETCH_DISTANCE;
import static simulation.Config.PROBABILITY_CUTOFF;
import static simulation.Config.VIEWPORT_HEIGHT;
import static simulation.Config.VIEWPORT_WIDTH;
import static simulation.Config.UPPER_LEFT_STARTING_POINT;

public class Predictor {
	
	public final static float[] LOD_INTERVALS = lodIntervals(FRAGMENTS_PER_TILE);
	public static Map<Integer,Double> likelihoods = new HashMap<Integer, Double>();
	
	

	public static double distance(Viewport viewport, Point p){
		double distanceUpperLeft = distance(viewport.upperLeft,p);
		double distanceUpperRight = distance(viewport.upperRight,p);
		double distanceLowerLeft = distance(viewport.lowerLeft,p);
		double distanceLowerRight = distance(viewport.lowerRight,p);
		double dist = Math.min(Math.min(distanceUpperLeft,distanceUpperRight),Math.min(distanceLowerLeft,distanceLowerRight));
		return dist;
	}
	
	public static double distance(Point a, Point b){
		double dist = Math.sqrt(Math.pow(a.y-b.y,2)+Math.pow(a.x-b.x,2));
		return Math.floor(dist);
	}
	public static double distance(Tile tile,Viewport viewport){
		Point currentCenterIndex = viewport.center;
		Point tileIndex = tile.point;
		return distance(currentCenterIndex,tileIndex);
	}
	
	/*public static void trainDatabase(Database db){
		Predictor.constantTrain(db);
		Predictor.spiralTrain(db);
	}*/
	
	
	
	public static Viewport nextMove(Viewport viewport,Vector<String> moves){
		if (viewport == null){
			return new Viewport(VIEWPORT_HEIGHT,VIEWPORT_WIDTH,UPPER_LEFT_STARTING_POINT,null);
		}
		String move = moves.remove(0);
		if (move.equals("up")){
			return viewport.goUp();
		}
		else if (move.equals("right")){
			return viewport.goRight();
		}
		else if (move.equals("down")){
			return viewport.goDown();
		}
		else if (move.equals("left")){
			return viewport.goLeft();
		}
		else {
			
			return null;
		}
	}
	
	
	public static Viewport nextMove(Viewport viewport){
		if (viewport == null){
			return new Viewport(VIEWPORT_HEIGHT,VIEWPORT_WIDTH,UPPER_LEFT_STARTING_POINT,null);
		}
		double random = Math.random();
		if (random<=0.1d){
			return viewport.goUp();
		}
		else if (random>0.1d && random<=0.4d){
			return viewport.goRight();
		}
		else if (random>0.40d && random<=0.9d){
			return viewport.goDown();
		}
		else {
			return viewport.goLeft();
		}
	}
	
	public static int calculateLOD(Point point,Viewport viewport){
		double likelihood = Predictor.calculateLikelihood(point,viewport);
		int lod = Predictor.likelihoodToLOD(likelihood);
		return lod;
	}
	
	public static double calculateLikelihood(Point index,Viewport viewport){
		String horizontal = null;
		String vertical = null;
		if (viewport.contains(index)){
			return 1.0;
		}
		
		if (index.y < viewport.upperLeft.y){
			vertical="u";
		}
		else if(index.y >= viewport.upperLeft.y && index.y <= viewport.lowerLeft.y){
			vertical="c";
		}
		else {
			vertical="b";
		}
		
		if (index.x < viewport.upperLeft.x){
			horizontal = "l";
		}
		else if( index.x >= viewport.upperLeft.x && index.x <= viewport.upperRight.x){
			horizontal = "c";
		}
		else {
			horizontal = "r";
		}
		
		String position = vertical + horizontal;
		double probability = 0.0d;
		if (position.equals("cc")){
			return 1.0d;
		}
		
		if (position.equals("ul")){
			probability = 0.1d;
			//System.out.println("ul");
		}
		else if(position.equals("uc")){
			probability = 0.1d;
			//System.out.println("uc");
		}
		else if(position.equals("ur")){
			probability = 0.2d;
			//System.out.println("ur");
		}
		else if(position.equals("cl")){
			probability = 0.1d;
			//System.out.println("cl");
		}
		else if(position.equals("cr")){
			probability = 0.3d;
			//System.out.println("cr");
		}
		else if(position.equals("bl")){
			probability = 0.3d;
			//System.out.println("bl");
		}
		else if(position.equals("bc")){
			probability = 0.5d;
			//System.out.println("bc");
		}
		else if(position.equals("br")){
			probability = 0.4d;
			//System.out.println("br");
		}
		//System.out.println("probability "+probability);
		
		double distance = distance(viewport, index);
		double likelihood;
		//System.out.println("distance "+distance);
		if (PROBABILITY_CUTOFF >= distance){
			likelihood = (4*probability + 6*(PROBABILITY_CUTOFF-distance+1)/(PROBABILITY_CUTOFF*1.0d))/10d;
		}
		else {
			likelihood = 0;
		}
		//System.out.println("likelihood "+likelihood);
		return likelihood; 
	}
	
	/*public static void calculateAndSetLikelihood(Tile tile,Viewport viewport){	
		tile.likelihood = Predictor.calculateLikelihood(tile.point,viewport);
	}*/
	

	public static int likelihoodToLOD(double likelihood){
		int lod = 1;
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(3);
		float likelihoodf = Float.valueOf(df.format(likelihood));
		for (int i=0; i<LOD_INTERVALS.length-1; i++){
			if (likelihoodf == 0.0d){
				return 0;
			}
			if (likelihoodf>LOD_INTERVALS[i] && likelihoodf<=LOD_INTERVALS[i+1]){
				return lod;
			}
			else {
				lod++;
			}
		}
		return Math.min(lod,FRAGMENTS_PER_TILE);
	}
	
	public static void likelihoodToLOD(Tile tile){
		tile.lod = Predictor.likelihoodToLOD(tile.likelihood);
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
