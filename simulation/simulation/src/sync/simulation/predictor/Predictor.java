package sync.simulation.predictor;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import sync.simulation.Point;
import sync.simulation.Tile;
import sync.simulation.Viewport;
import sync.simulation.events.UserMove;

import static sync.simulation.Config.FRAGMENTS_PER_TILE;
import static sync.simulation.Config.UPPER_LEFT_STARTING_POINT;
import static sync.simulation.Config.VIEWPORT_HEIGHT;
import static sync.simulation.Config.VIEWPORT_WIDTH;
import static sync.simulation.Config.THINK_TIME;
import static sync.simulation.Config.CUTOFF;
import static sync.simulation.Config.FRAGMENT;

public class Predictor {
	
	//public final static float[] LOD_INTERVALS = lodIntervals(FRAGMENTS_PER_TILE);
	public static Map<Integer,Double> likelihoods = new HashMap<Integer, Double>();
	
	
	private static HashMap<Integer,Node> createPredictorTree(Node node,int totalWaves){
		node.probability = 1d;
		LinkedList<Node> listNodes = createPredictorTreeHelper(node,1);
		HashMap<Integer,Node> toReturn = new HashMap<Integer,Node>();
		for(Node n : listNodes){
			if (node.equals(n)){
				continue;
			}
			if (!toReturn.containsKey(n.hashCode())){
				toReturn.put(n.hashCode(), n);
			}
			else {
				Node stored = toReturn.get(n.hashCode());
				if (stored.waveNum == n.waveNum && stored.probability < n.probability){
					toReturn.put(n.hashCode(), n);
				}
			}
		}
		
		
		for (int wave = 2; wave<=totalWaves; wave++){
			listNodes = createPredictorTreeHelper(listNodes,wave);
			for(Node n : listNodes){
				if (node.equals(n)){
					continue;
				}
				if (!toReturn.containsKey(n.hashCode())){
					toReturn.put(n.hashCode(), n);
				}
				else {
					Node stored = toReturn.get(n.hashCode());
					if (stored.waveNum == n.waveNum && stored.probability < n.probability){
						toReturn.put(n.hashCode(), n);
					}
				}
			}
		}
		
		return toReturn;
		
	}
	
	
	private static LinkedList<Node> createPredictorTreeHelper(Node node,int wave){
		LinkedList<Node> input = new LinkedList<Node>();
		input.add(node);
		return createPredictorTreeHelper(input,wave);
	}
	
	
	
	private static LinkedList<Node> deriveOrder(HashMap<Integer,Node> map ,int wave){
		//filter all the other waves
		Iterator<Integer> iter = map.keySet().iterator();
		LinkedList<Node> list = new LinkedList<Node>();
		while(iter.hasNext()){
		Integer hash = iter.next();
			Node node = map.get(hash);
			if (node.waveNum == wave){
				list.add(node);
			}
		}
		// make a list 
		
		
		Node.sortDesc(list);
		return  list;
	}
	
	private static Object[] deriveFragmentNumbers(LinkedList<Node> list,Integer previousFrames,Double previousProb){
		if (previousFrames==null){
			previousFrames = 8;
		}
		Vector<Node> vec = new Vector<Node>();
		Iterator<Node> iter = list.iterator();
		int count=0;
		while(iter.hasNext() && count<CUTOFF){
			Node node = iter.next();
			if (!FRAGMENT || count==0){
				previousFrames = 8;
				previousProb = node.probability;
			}
			else {
				previousFrames = Math.min((int) Math.ceil((node.probability/previousProb)*previousFrames),FRAGMENTS_PER_TILE);
			}
			//System.out.println(previousFrames);
			if (previousFrames>0){
				previousProb = node.probability;
				node.fragmentsNeeded = previousFrames;
				vec.add(node);
			}
			else {
				System.out.println("rejected");
			}
			count++;
		}
		
		Object[] objArray = new Object[3];
		objArray[0] = vec;
		objArray[1] = previousProb;
		objArray[2] = previousFrames;
		return objArray;
	}

	
	public static Object[] preparePrefetching(Node node,int waveNeeded,int maxWaveNum,Integer previousFrames,Double previousProb){
		
		assert(waveNeeded<=maxWaveNum);
		node.probability = 1.0; //root
		HashMap<Integer,Node> list = Predictor.createPredictorTree(node,maxWaveNum); 
		LinkedList<Node> orderedWave = Predictor.deriveOrder(list,waveNeeded);
		return Predictor.deriveFragmentNumbers(orderedWave, previousFrames, previousProb);
		//Vector<Node> fragmNums = ((Vector<Node>)Predictor.deriveFragmentNumbers(orderedWave, previousFrames, previousProb)[0]);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static double distance(Viewport viewport, Point p){
		double distanceUpperLeft = distance(viewport.upperLeft,p);
		double distanceUpperRight = distance(viewport.upperRight,p);
		double distanceLowerLeft = distance(viewport.lowerLeft,p);
		double distanceLowerRight = distance(viewport.lowerRight,p);
		double dist = Math.min(Math.min(distanceUpperLeft,distanceUpperRight),Math.min(distanceLowerLeft,distanceLowerRight));
		return dist;
	}
	
	public static int distance(Point a, Point b){
		double dist = Math.sqrt(Math.pow(a.y-b.y,2)+Math.pow(a.x-b.x,2));
		return (int)Math.ceil(dist);
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
	
	
	
	public static UserMove nextMoveFromWorkload(UserMove previousMove,Vector<String> moves){
		if (previousMove == null){
			return new UserMove(new Viewport(VIEWPORT_HEIGHT,VIEWPORT_WIDTH,UPPER_LEFT_STARTING_POINT,null));
		}
		String move = moves.remove(0);
		return  previousMove.nextMove(move);			
	}
	

	
	
	public static UserMove nextMove(Viewport viewport){
		if (viewport == null){
			return new UserMove(new Viewport(VIEWPORT_HEIGHT,VIEWPORT_WIDTH,UPPER_LEFT_STARTING_POINT,null));
		}
		double random = Math.random();
		if (random<=0.1d){
			return new UserMove(viewport).goUp();
		}
		else if (random>0.1d && random<=0.4d){
			return new UserMove(viewport).goRight();
		}
		else if (random>0.40d && random<=0.9d){
			return new UserMove(viewport).goDown();
		}
		else {
			return new UserMove(viewport).goLeft();
		}
	}
	


	
	
	public static LinkedList<Node> createPredictorTreeHelper(LinkedList<Node> listNodes,int wave){
//		PERFECT
		double upLikelihood = 0.1;
		double downLikelihood = 0.5;
		double leftLikelihood = 0.1;
		double rightLikelihood = 0.3;
		//HORRIBLE
//		double upLikelihood = 0.25;
//		double downLikelihood = 0.25;
//		double leftLikelihood = 0.25;
//		double rightLikelihood = 0.25;
		
		LinkedList<Node> toReturn = new LinkedList<Node>();
		for(Node node : listNodes){
			Point newPoint = node.point;
			//up
			Point tempPoint = newPoint.goUp();
			int newX = tempPoint.x;
			int newY = tempPoint.y;
			Node newNode = new Node(node,newY,newX,upLikelihood*node.probability); 
			newNode.waveNum = wave;
			toReturn.addLast(newNode);
			
			//down 
			newPoint = node.point;
			tempPoint = newPoint.goDown();
			newX = tempPoint.x;
			newY = tempPoint.y;
			newNode = new Node(node,newY,newX,downLikelihood*node.probability);
			newNode.waveNum = wave;
			toReturn.addLast(newNode);
			
			//left 
			newPoint = node.point;
			tempPoint = newPoint.goLeft();
			newX = tempPoint.x;
			newY = tempPoint.y;
			newNode = new Node(node,newY,newX,leftLikelihood*node.probability);
			newNode.waveNum = wave;
			toReturn.addLast(newNode);
			
			//right
			newPoint = node.point;
			tempPoint = newPoint.goRight();
			newX = tempPoint.x;
			newY = tempPoint.y;
			newNode = new Node(node,newY,newX,rightLikelihood*node.probability);
			newNode.waveNum = wave;
			toReturn.addLast(newNode);
		}
		
		return toReturn;
	}
	
	
	
	
	
	
	

	/*public static int calculateLOD(Point point,Viewport viewport){
		double likelihood = Predictor.calculateLikelihood(point,viewport);
		int lod = Predictor.likelihoodToLOD(likelihood);
		return lod;
	}*/
	
	/*public static double calculateLikelihood(Point index,Viewport viewport){
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
			probability = 0.01d;//0.1d;
			//System.out.println("ul");
		}
		else if(position.equals("uc")){
			probability = 0.1d;
			//System.out.println("uc");
		}
		else if(position.equals("ur")){
			probability = 0.03d;//0.2d;
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
			probability = 0.05d;//0.3d;
			//System.out.println("bl");
		}
		else if(position.equals("bc")){
			probability = 0.5d;
			//System.out.println("bc");
		}
		else if(position.equals("br")){
			probability = 0.15d;//0.4d;
			//System.out.println("br");
		}
		//System.out.println("probability "+probability);
		
		double distance = distance(viewport, index);
		double likelihood;
		//System.out.println("distance "+distance);
		if (PROBABILITY_CUTOFF >= distance){
			
			//likelihood = (1.0*probability + 0.0*(PROBABILITY_CUTOFF-distance+1)/(PROBABILITY_CUTOFF*1.0d))/10d;
			//if (Main.cache.SpaceBeingUsed/Config.CACHE_SIZE*1.0d > 0.8d){ // more than 80% memory usage
				//likelihood = (9*probability + 1*(PROBABILITY_CUTOFF-distance+1)/(PROBABILITY_CUTOFF*1.0d))/10d;
				likelihood = Math.pow(probability,distance);
				if (likelihood <0.0000001){
					likelihood = 0;
				}
			//}
			//else {
				//likelihood = probability;
				//likelihood = 1.0;
			//}
					
		}
		else {
			likelihood = 0;
		}
		//System.out.println("likelihood "+likelihood);
		return likelihood; 
	}*/
	
	/*public static void calculateAndSetLikelihood(Tile tile,Viewport viewport){	
		tile.likelihood = Predictor.calculateLikelihood(tile.point,viewport);
	}*/
	

	/*public static int likelihoodToLOD(double likelihood){
//		int lod = 1;
//		DecimalFormat df = new DecimalFormat();
//		df.setMaximumFractionDigits(3);
//		float likelihoodf = Float.valueOf(df.format(likelihood));
//		for (int i=0; i<LOD_INTERVALS.length-1; i++){
//			if (likelihoodf == 0.0d){
//				return 0;
//			}
//			if (likelihoodf>LOD_INTERVALS[i] && likelihoodf<=LOD_INTERVALS[i+1]){
//				return lod;
//			}
//			else {
//				lod++;
//			}
//		}
//		return Math.min(lod,FRAGMENTS_PER_TILE);
		return Math.min((int)(Math.ceil(likelihood*THINK_TIME)),FRAGMENTS_PER_TILE);
		
	}*/
	
	/*public static void likelihoodToLOD(Tile tile){
		tile.lod = Predictor.likelihoodToLOD(tile.likelihood);
	}*/

	
	
	/*private static float[] lodIntervals(int fragments){
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
	}*/
	
	
}
