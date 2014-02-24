package ver3.simulation.events;

import static ver3.simulation.Config.THINK_TIME;
import ver3.simulation.Point;
//import sync.simulation.Run;
import ver3.simulation.Viewport;
import static ver3.simulation.Config.FRAGMENTS_PER_TILE;

public class UserMove {
	public Point  point;
	public Viewport viewport;
	public String movementType;
	
	public int cacheHits = 0;
	public int cacheMisses = 0;
	

	public double cacheMissesDuringFetch = 0;
	public int cacheHitsDuringFetch = 0;
	public double latencyDuringFetch = 0;
	
	public int thinkTime = THINK_TIME;
	
	
	public static int currentZoomLevel = 1;
	
	public UserMove(Point point){
		this.point = point;
	}
	
	
	
	public UserMove jumpTo(Point point){
		return new UserMove(this.point);
	}
	
	
	public UserMove go(String move){
		if (move.equals("up")){
			System.out.println("up");
			UserMove newU = new UserMove(this.point.goUp());
			
			return newU;
			
		}
		else if (move.equals("right")){
			return new UserMove(this.point.goRight());
		}
		else if (move.equals("down")){
			return new UserMove(this.point.goDown());
		}
		else if (move.equals("left")){
			return new UserMove(this.point.goLeft());
		}
		else if (move.equals("zoomin")){
			
			currentZoomLevel+=1;
			
			if (currentZoomLevel>FRAGMENTS_PER_TILE){
				currentZoomLevel = FRAGMENTS_PER_TILE;
			}
			return this;
		}
		else if (move.equals("zoomout")){
			currentZoomLevel-=1;
			
			if (currentZoomLevel<1){
				currentZoomLevel = 1;
			}
			return this;
		}
		else if(move.equals("zoommax")){
			currentZoomLevel = FRAGMENTS_PER_TILE;
			return this;
		}
		else if(move.equals("zoommin")){
			currentZoomLevel = 1;
			return this;
		}
		else {
			return null;
		}
	}
	
	
	
	//public Run run;
}
