package ver3.simulation.regions;


import util.Util;
import ver3.simulation.Database;
import ver3.simulation.Point;
import ver3.simulation.predictor.UserStudiesCombined;

public abstract class Region { // region of points
	public int width;
	public int height;
	
	public Point upperLeft;
	/*public Point upperRight;
	public Point lowerLeft;
	public Point lowerRight;
	public Point center;*/
	//public String resultOfMovement;
	
	abstract public Point randomPoint();
	
	public boolean contains(Point point){
		return contains(point.y,point.x);
	}
	
	public boolean contains(int y,int x){
		if (x>=upperLeft.x && x<=upperLeft.x +(width-1) && y>=upperLeft.y && y<=upperLeft.y+(height-1)){
			return true;
		}
		else
			return false;
	}	
	
	
}
