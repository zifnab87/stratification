package sync.userstudysynthesizer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;

import sync.simulation.regions.JumpRegion;
import sync.simulation.regions.TileOverall;
import sync.simulation.regions.UserStudiesCombined;
import sync.simulation.Database;
import sync.simulation.Point;
import sync.simulation.events.UserMove;
import util.Util;
import static sync.simulation.Config.DATABASE_WIDTH;
import static sync.simulation.Config.JUMP_REGION_WIDTH;
import static sync.simulation.Config.FRAGMENT;


public class UserStudySynthesizer {
	
	static Point[] jumpPoints = new Point[]{new Point(5,5),new Point(4,5),new Point(6,5),new Point(5,4),new Point(5,6),
											new Point(10,10),new Point(9,10),new Point(11,10),new Point(10,9),new Point(10,11),
											new Point(15,15),new Point(14,15),new Point(16,15),new Point(15,14),new Point(15,16),		
											new Point(20,20),new Point(19,20),new Point(21,20),new Point(20,19),new Point(20,21),
											new Point(5,10),new Point(4,10),new Point(6,10),new Point(5,9),new Point(5,11),
											new Point(5,15),new Point(4,15),new Point(6,15),new Point(5,14),new Point(5,16),
											new Point(5,20),new Point(4,20),new Point(6,20),new Point(5,19),new Point(5,21),
											new Point(10,15),new Point(9,15),new Point(11,15),new Point(10,14),new Point(10,16),
											new Point(10,20),new Point(9,20),new Point(11,20),new Point(10,19),new Point(10,21),	
											new Point(15,20),new Point(14,20),new Point(16,20),new Point(15,19),new Point(15,21),		
	};
	
	static HashMap<Integer,TileOverall> map = new HashMap<Integer,TileOverall>();
	static double jumpProbability = 0.1; //0.1
	static double panProbability = 0.5; // 0.5
	static double zoomProbability = 0.4; // 0.4
	
	static double upProbability = 0.25;
	static double downProbability = 0.25;
	static double leftProbability = 0.25;
	static double rightProbability = 0.25;
	
	static double zoomMinProbability = 0.05;
	static double zoomMaxProbability = 0.05;
	static double zoomInProbability = 0.60;
	static double zoomOutProbability = 0.30;
	
	
	
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
		//if (currentTile.visitsCounts<numOfUserStudies){
			currentTile.visitsCounts++;
		//}
		//if (currentTile.visitResolutions[UserMove.currentZoomLevel]<numOfUserStudies){
			currentTile.visitResolutions[UserMove.currentZoomLevel]++;
		//}
		
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
				current = whatHappensNext(current,true);
				updateCounts(current);
				//System.out.println(current.point);
			}
		}
		
		
		printUserStudies();
		
		
		
		
		
	}
	
	
	public static UserMove whatHappensNext(UserMove current,boolean jumpCount){
		boolean possiblyNotPermitted = false;
		do{
			double rand = Math.random();
			possiblyNotPermitted = false;
			if (rand<=jumpProbability){
				//jump
				Util.debug("jump");
				JumpRegion jump = new JumpRegion(Database.points(current.point.y-JUMP_REGION_WIDTH/2,current.point.x-JUMP_REGION_WIDTH/2));
				//System.out.println(Database.points(current.point.y-JUMP_REGION_WIDTH/2,current.point.x-JUMP_REGION_WIDTH/2)+"!!!");
				Point jumpPoint = null;
				TileOverall currentTile = null;
				
				//find which of the jump points are contained in the jumpRegion
				Vector<Point> contained = new Vector<Point>();
				for (int i=0; i<jumpPoints.length; i++){
					
					if(jump.contains(jumpPoints[i])){
						contained.add(jumpPoints[i]);
					}
				}
				if (contained.size()>0){
					 jumpPoint = contained.get(new Random().nextInt(contained.size()));
				}
				else {
					jumpPoint = jump.randomPoint(); // remove the jumps from the boundaries of db
				}
				
				UserMove previous = current;
				current = current.jumpTo(jumpPoint);
				currentTile = map.get(current.point.id);
					
				
				
				
				//current = current.jumpTo(jumpPoint);
				
				//TileOverall currentTile = map.get(current.point.id);
				//System.out.println("bgika"+map.get(jumpPoint.id).jumpToCounts);
				//if (currentTile.jumpToCounts<60){
				if (jumpCount){
					currentTile.jumpToCounts++;
				}
				//}
				//else {
				//	current = previous;
				//}
				
			
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
				possiblyNotPermitted = true;
				
			}
			else if(rand>jumpProbability+upProbability+downProbability+leftProbability+rightProbability+zoomInProbability 
					&& rand<=jumpProbability+upProbability+downProbability+leftProbability+rightProbability+zoomInProbability+zoomOutProbability){
				//zoom out; 
				current = current.go("zoomout");
				possiblyNotPermitted = true;
				
			}
			else if(rand>jumpProbability+upProbability+downProbability+leftProbability+rightProbability+zoomInProbability+zoomOutProbability
					&& rand<=jumpProbability+upProbability+downProbability+leftProbability+rightProbability+zoomInProbability+zoomOutProbability+zoomMaxProbability){
				//zoom in max;
				current = current.go("zoommax");
				possiblyNotPermitted = true;
				
			}
			else {
				
				current = current.go("zoommin");
				possiblyNotPermitted = true;
			}
		}
		while(/*possiblyNotPermitted && !FRAGMENT*/ 1!=1);
			
		/*else if(rand>jumpProbability+upProbability+downProbability+leftProbability+rightProbability+zoomInProbability+zoomOutProbability+zoomMaxProbability
				&& rand<=jumpProbability+upProbability+downProbability+leftProbability+rightProbability+zoomInProbability+zoomOutProbability+zoomMaxProbability+zoomMinProbability){
			//zoom out min;
			current = current.go("zoommin");
			
		}*/
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
