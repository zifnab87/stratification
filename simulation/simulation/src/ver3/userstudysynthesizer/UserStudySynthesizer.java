package ver3.userstudysynthesizer;

import java.util.HashMap;
import java.util.Iterator;

import ver3.simulation.Database;
import ver3.simulation.Point;
import ver3.simulation.Viewport;
import ver3.simulation.events.UserMove;
import ver3.simulation.predictor.TileOverall;
import ver3.simulation.predictor.UserStudiesCombined;
import ver3.simulation.regions.JumpRegion;
import static ver3.simulation.Config.DATABASE_WIDTH;
import static ver3.simulation.Config.JUMP_REGION_WIDTH;



public class UserStudySynthesizer {
	static HashMap<Integer,TileOverall> map = new HashMap<Integer,TileOverall>();
	static double jumpProbability = 0.10;
	static double panProbability = 0.5;
	static double zoomProbability = 0.40;
	
	static double upProbability = 0.25;
	static double downProbability = 0.25;
	static double leftProbability = 0.25;
	static double rightProbability = 0.25;
	
	static double zoomMinProbability = 0.10;
	static double zoomMaxProbability = 0.10;
	static double zoomInProbability = 0.40;
	static double zoomOutProbability = 0.40;
	
	
	
	static double numOfUserStudies = 600;
	static double numOfMovesPerStudy = 1000;
	
	
	
	
	public static void updateCounts(UserMove current){
		TileOverall currentTile = map.get(current.point.id);
		currentTile.visitsCounts++;
		currentTile.visitResolutons[UserMove.currentZoomLevel]++;
		
	}
	
	
	public static void main(String args[]){
		
		//initialize all objects in map
		Database db = new Database();
		JumpRegion jump = new JumpRegion(Database.points(0,0));
		UserStudiesCombined usc = new UserStudiesCombined();
		
		
		for (int j=0; j<DATABASE_WIDTH; j++){
			for (int i=0; i<DATABASE_WIDTH; i++){
				Point point = Database.points(j,i);
				map.put(point.id, new TileOverall(point));
			}
		}
		
		
		// Prepare probabilities
		upProbability = panProbability * upProbability;
		downProbability = panProbability * downProbability;
		leftProbability = panProbability * leftProbability;
		rightProbability = panProbability * rightProbability;
		
		zoomInProbability = zoomProbability * zoomInProbability;
		zoomOutProbability = zoomProbability * zoomOutProbability;
		zoomMaxProbability =  zoomProbability * zoomMaxProbability;
		zoomMinProbability =  zoomProbability * zoomMinProbability;
		// Starting point
		System.out.println(db.upperLeft);
		//Viewport viewport = new Viewport(Database.points(0,0));
		
		
		
		for (int i=0; i<numOfUserStudies; i++){
			UserMove current = new UserMove(db.randomPoint());
			System.out.println(current.point+"");
			for (int j=0; j<numOfMovesPerStudy; j++){
				current = whatHappensNext(current);
				//System.out.println(current.point);
			}
		}
		
		
		printUserStudies();
		
		
		
		
		
	}
	
	
	public static UserMove whatHappensNext(UserMove current){
		double rand = Math.random();
		if (rand<=jumpProbability){
			//jump
			JumpRegion jump = new JumpRegion(Database.points(current.point.y-JUMP_REGION_WIDTH/2,current.point.x-JUMP_REGION_WIDTH/2));
			//System.out.println(Database.points(current.point.y-JUMP_REGION_WIDTH/2,current.point.x-JUMP_REGION_WIDTH/2)+"!!!");
			Point jumpPoint = jump.randomPoint();
			while (map.get(jumpPoint.id).jumpToCounts>100){
				jumpPoint = jump.randomPoint(); // remove the jumps from the boundaries of db
			}
			current = current.jumpTo(jumpPoint);
			updateCounts(current);
			TileOverall currentTile = map.get(current.point.id);
			currentTile.jumpToCounts++;
		
		}
		if(rand>jumpProbability && rand<=jumpProbability+upProbability){
			//up
			//System.out.println(current+"@@");
			updateCounts(current);
		}
		else if(rand>jumpProbability+upProbability && rand<=jumpProbability+upProbability+downProbability){
			//down
			current = current.go("down");
			updateCounts(current);
		}
		else if(rand>jumpProbability+upProbability+downProbability && rand<=jumpProbability+upProbability+downProbability+leftProbability){
			//left
			current = current.go("left");
			updateCounts(current);
		}
		else if(rand>jumpProbability+upProbability+downProbability+leftProbability && rand<=jumpProbability+upProbability+downProbability+leftProbability+rightProbability){
			//right
			current = current.go("right");
			updateCounts(current);
		}
		else if(rand>jumpProbability+upProbability+downProbability+leftProbability+rightProbability 
				&& rand<=jumpProbability+upProbability+downProbability+leftProbability+zoomInProbability){
			//zoom in 
			current = current.go("zoomin");
			updateCounts(current);
		}
		else if(rand>jumpProbability+upProbability+downProbability+leftProbability+rightProbability+zoomInProbability 
				&& rand<=jumpProbability+upProbability+downProbability+leftProbability+zoomInProbability+zoomOutProbability){
			//zoom out; 
			current = current.go("zoomout");
			updateCounts(current);
		}
		else if(rand>jumpProbability+upProbability+downProbability+leftProbability+rightProbability+zoomInProbability+zoomOutProbability
				&& rand<=jumpProbability+upProbability+downProbability+leftProbability+zoomInProbability+zoomOutProbability+zoomMaxProbability){
			//zoom in max;
			current = current.go("zoommax");
			updateCounts(current);
		}
		else if(rand>jumpProbability+upProbability+downProbability+leftProbability+rightProbability+zoomInProbability+zoomOutProbability+zoomMaxProbability
				&& rand<=jumpProbability+upProbability+downProbability+leftProbability+zoomInProbability+zoomOutProbability+zoomMaxProbability+zoomMinProbability){
			//zoom out min;
			current = current.go("zoommin");
			updateCounts(current);
		}
		return current;
	}
	
	
	
	public static void printUserStudies(){
		Iterator<Integer> iter = map.keySet().iterator();
		while(iter.hasNext()){
			Integer key = iter.next();
			TileOverall tile = map.get(key);
			for(int i=1; i<tile.visitResolutons.length; i++){
				System.out.print("["+i+"]="+tile.visitResolutons[i]+",");
			}
			System.out.println(tile.jumpToCounts+","+tile.point);
		}
		
		
	}
}
