package sync.simulation.regions;


import util.Util;
import sync.simulation.Database;
import sync.simulation.Point;

public abstract class Region { // region of points
	public int width;
	public int height;
	
	public Point upperLeft;
	/*public Point upperRight;
	public Point lowerLeft;
	public Point lowerRight;
	public Point center;*/
	//public String resultOfMovement;
	
	
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
