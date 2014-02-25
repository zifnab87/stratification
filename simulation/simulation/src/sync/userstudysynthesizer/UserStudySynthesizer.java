package sync.userstudysynthesizer;

import java.util.HashMap;
import java.util.Iterator;

import sync.simulation.regions.JumpRegion;
import sync.simulation.regions.TileOverall;
import sync.simulation.regions.UserStudiesCombined;
import sync.simulation.Database;
import sync.simulation.Point;
import sync.simulation.Viewport;
import sync.simulation.events.UserMove;
import static sync.simulation.Config.DATABASE_WIDTH;
import static sync.simulation.Config.JUMP_REGION_WIDTH;



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
	
	
	public UserStudySynthesizer(){
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
	}
	
	
	public static void updateCounts(UserMove current){
		TileOverall currentTile = map.get(current.point.id);
		if (currentTile.visitsCounts<numOfUserStudies){
			currentTile.visitsCounts++;
		}
		if (currentTile.visitResolutions[UserMove.currentZoomLevel]<numOfUserStudies){
			currentTile.visitResolutions[UserMove.currentZoomLevel]++;
		}
		
	}
	
	
	public static void main(String args[]){
		
		//initialize all objects in map
		Database db = new Database();
		JumpRegion jump = new JumpRegion(Database.points(0,0));
		UserStudiesCombined usc = new UserStudiesCombined();
		
		
		UserStudySynthesizer uss = new UserStudySynthesizer();
		
		
		
		// Starting point
		//System.out.println(db.upperLeft);
		//Viewport viewport = new Viewport(Database.points(0,0));
		
		
		
		for (int i=0; i<numOfUserStudies; i++){
			UserMove current = new UserMove(db.randomPoint());
			//System.out.println(current.point+"");
			for (int j=0; j<numOfMovesPerStudy; j++){
				current = whatHappensNext(current);
				updateCounts(current);
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
			Point jumpPoint = null;
			TileOverall currentTile = null;
			
			jumpPoint = jump.randomPoint(); // remove the jumps from the boundaries of db
			UserMove previous = current;
			current = current.jumpTo(jumpPoint);
			currentTile = map.get(current.point.id);
				
			
			
			
			//current = current.jumpTo(jumpPoint);
			
			//TileOverall currentTile = map.get(current.point.id);
			//System.out.println("bgika"+map.get(jumpPoint.id).jumpToCounts);
			if (currentTile.jumpToCounts<60){
				currentTile.jumpToCounts++;
			}
			else {
				current = previous;
			}
			
		
		}
		else if(rand>jumpProbability && rand<=jumpProbability+upProbability){
			//up
			//System.out.println(current+"@@");
			current = current.go("up");
			
		}
		else if(rand>jumpProbability+upProbability && rand<=jumpProbability+upProbability+downProbability){
			//down
			current = current.go("down");
			
		}
		else if(rand>jumpProbability+upProbability+downProbability && rand<=jumpProbability+upProbability+downProbability+leftProbability){
			//left
			current = current.go("left");
			
		}
		else if(rand>jumpProbability+upProbability+downProbability+leftProbability && rand<=jumpProbability+upProbability+downProbability+leftProbability+rightProbability){
			//right
			current = current.go("right");
			
		}
		else if(rand>jumpProbability+upProbability+downProbability+leftProbability+rightProbability 
				&& rand<=jumpProbability+upProbability+downProbability+leftProbability+zoomInProbability){
			//zoom in 
			current = current.go("zoomin");
			
		}
		else if(rand>jumpProbability+upProbability+downProbability+leftProbability+rightProbability+zoomInProbability 
				&& rand<=jumpProbability+upProbability+downProbability+leftProbability+zoomInProbability+zoomOutProbability){
			//zoom out; 
			current = current.go("zoomout");
			
		}
		else if(rand>jumpProbability+upProbability+downProbability+leftProbability+rightProbability+zoomInProbability+zoomOutProbability
				&& rand<=jumpProbability+upProbability+downProbability+leftProbability+zoomInProbability+zoomOutProbability+zoomMaxProbability){
			//zoom in max;
			current = current.go("zoommax");
			
		}
		else if(rand>jumpProbability+upProbability+downProbability+leftProbability+rightProbability+zoomInProbability+zoomOutProbability+zoomMaxProbability
				&& rand<=jumpProbability+upProbability+downProbability+leftProbability+zoomInProbability+zoomOutProbability+zoomMaxProbability+zoomMinProbability){
			//zoom out min;
			current = current.go("zoommin");
			
		}
		return current;
	}
	//process the resolutions to be cumulative percentiles
	public static void process(){
		Iterator<Integer> iter = map.keySet().iterator();
		while (iter.hasNext()){
			Integer key = iter.next();
			TileOverall tile = map.get(key);
			int sum=0;
			for(int i=1; i<tile.visitResolutions.length; i++){
				sum+=tile.visitResolutions[i];
			}
			if (sum!=0){
				for(int i=1; i<tile.visitResolutions.length; i++){
					tile.visitResolutions[i] = tile.visitResolutions[i]/(1.0*sum);
				}
				for(int i=2; i<tile.visitResolutions.length; i++){
					tile.visitResolutions[i] = tile.visitResolutions[i-1]+tile.visitResolutions[i];
				}
			
			}
			
		}
	}
	
	public static void printUserStudies(){
		process();
		Iterator<Integer> iter = map.keySet().iterator();
		while(iter.hasNext()){
			Integer key = iter.next();
			TileOverall tile = map.get(key);
			int y = tile.point.y;
			int x = tile.point.x;
			System.out.print("tiles["+y+"]["+x+"] = new TileOverall(Database.points("+y+","+x+"),new double[]{0,");
			for(int i=1; i<tile.visitResolutions.length; i++){
				System.out.print(tile.visitResolutions[i]);
				if (i!=tile.visitResolutions.length-1){//not last
					System.out.print(",");
				}
				
			}
			System.out.println("},"+tile.jumpToCounts+","+tile.visitsCounts+");");
		}
		
		
	}
}
