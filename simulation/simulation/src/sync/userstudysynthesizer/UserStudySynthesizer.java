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
import static sync.simulation.Config.FRAGMENTS_PER_TILE;


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
	
	
	static double jumpProbability = 0.4; //0.1
	static double panProbability = 0.2; // 0.5
	static double zoomProbability = 0.4; // 0.4
	
	
	
	static double deterministicPanProbability = 0.75;
	static double randomPanProbability = 0.25;
	
	static double upProbability = 0.25;
	static double downProbability = 0.25;
	static double leftProbability = 0.25;
	static double rightProbability = 0.25;
	
	
	static double deterministicZoomProbability = 0.75;
	static double randomZoomProbability = 0.25;
	
	static double zoomInProbability = 0.45;
	static double zoomOutProbability = 0.45;
	static double zoomJumpProbability = 0.10;
	
	
	
	public static UserMove whatHappensNext(UserMove current){
		boolean possiblyNotPermitted = false;
		possiblyNotPermitted = false;
		double rand = Math.random();
		if (rand<=panProbability){ //PAN
			double rand2 = Math.random();
			if (rand2<=randomPanProbability){ //PAN -> RANDOM PAN
				double rand3 = Math.random();
				if(rand3 <= upProbability){
					//up
					current = current.go("up");

				}
				else if(rand3 > upProbability && rand3 <= upProbability+downProbability){
					//down
					current = current.go("down");
					
				}
				else if(rand3 > upProbability+downProbability && rand3 <= upProbability+downProbability+leftProbability){
					//left
					current = current.go("left");
					
				}
				else if(rand3 > upProbability+downProbability+leftProbability && rand3 <= upProbability+downProbability+leftProbability+rightProbability){
					//right
					current = current.go("right");
					
				}
			}
			else { // PAN -> DETERMINISTIC PAN (choose based on which has the most popularity)
				String[] moves = new String[]{"up","down","left","right"};
				double max = -10;
				String maximizingMove = "";
				for(int i=0; i<moves.length; i++){
					current = current.go(moves[i]);
					//current.point.y
					double newVal = UserStudiesCombined.popularities[current.point.y][current.point.x];
					if (newVal>max){
						max = newVal;
						maximizingMove = moves[i];
					}
				}
				
				current = current.go(maximizingMove);
			
			}
		}
		else if(rand>panProbability && rand<=panProbability+zoomProbability){ //ZOOM
			double rand2 = Math.random();
			if (rand2<=randomZoomProbability){ //ZOOM -> RANDOM ZOOM
				double rand3 = Math.random();
				 if(rand3 <= zoomInProbability){
					//zoom in 
					current = current.go("zoomin");
					possiblyNotPermitted = true;
					
				}
				else if(rand3 > zoomInProbability 
						&& rand3 <= zoomInProbability+zoomOutProbability){
					//zoom out; 
					current = current.go("zoomout");
					possiblyNotPermitted = true;
					
				}
				else if(rand3 > zoomInProbability+zoomOutProbability
						&& rand3 <= zoomInProbability+zoomOutProbability+zoomJumpProbability){
					//zoom jump;
					current = current.go("zoomjump");
					possiblyNotPermitted = true;
					
				}
			}
			else { //ZOOM -> DETERMINISTIC ZOOM
				int fragments = (int)Math.floor(FRAGMENTS_PER_TILE*UserStudiesCombined.popularities[current.point.y][current.point.x]);
				if (fragments < 0 ){
					UserMove.currentZoomLevel = 0;
				}
				else if (fragments>FRAGMENTS_PER_TILE){
					UserMove.currentZoomLevel = FRAGMENTS_PER_TILE;
				}
				
				UserMove.currentZoomLevel = fragments;
				
				possiblyNotPermitted = true;
			}
		}
		else if(rand>panProbability+zoomProbability && rand <=1.0d){ //JUMP
			// take 40 random jump points
			// get the one that maximizes the popularity
			JumpRegion jump = new JumpRegion(Database.points(0,0));
			Point maximizingJumpPoint = null;
			double max = -10;
			for (int i=0; i<40; i++){
				Point point = Database.points(Util.randInt(0, DATABASE_WIDTH-1),Util.randInt(0, DATABASE_WIDTH-1));
				current = current.jumpTo(point);
				double newVal = UserStudiesCombined.popularities[current.point.y][current.point.x];
				if (newVal>max){
					max = newVal;
					maximizingJumpPoint = point;
				}
			}
			
			current = current.jumpTo(maximizingJumpPoint);
		}
		else if (possiblyNotPermitted && !FRAGMENT){
			current = null; //if a zoom happened and we are on Tiles mode
		}
		//System.out.println("current"+current.point+" "+rand);
		return current;
	}
}
