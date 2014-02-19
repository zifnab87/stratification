package ver3.simulation.regions;


import ver3.simulation.Point;
import ver3.simulation.predictor.UserStudiesCombined;

public class Region { // region of points
	public int width = 1;
	public int height = 1;
	
	public Point upperLeft;
	public Point upperRight;
	public Point lowerLeft;
	public Point lowerRight;
	public Point center;
	//public String resultOfMovement;
	
	public Point[][] points = new Point[width][height];

	public Region(Point upperLeft){
		this.upperLeft = upperLeft;

		this.upperRight = UserStudiesCombined.tiles[upperLeft.y][upperLeft.x + width-1].point;
		this.lowerRight = UserStudiesCombined.tiles[upperRight.y + height-1][upperRight.x].point;
		this.lowerLeft = UserStudiesCombined.tiles[upperLeft.y + height-1][upperLeft.x].point;
		this.center = UserStudiesCombined.tiles[Integer.valueOf((int) (upperLeft.y+height/2.0))][Integer.valueOf((int) (upperLeft.x+width/2.0)) ].point;
		
		for (int y=upperLeft.y; y<=lowerRight.y; y++){
			for (int x=upperLeft.x; x<=lowerRight.x; x++){
				points[y][x] = UserStudiesCombined.tiles[y][x].point; //database provides the points so we don't have so many of them
			}
		}
	}
	
	public boolean contains(Point point){
		return contains(point.y,point.x);
	}
	
	public boolean contains(int y,int x){
		if (x>=upperLeft.x && x<=upperRight.x && y>=upperLeft.y && y<=lowerLeft.y){
			return true;
		}
		else
			return false;
	}	
}
