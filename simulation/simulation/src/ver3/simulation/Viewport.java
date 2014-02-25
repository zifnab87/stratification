package ver3.simulation;

import static sync.simulation.Config.VIEWPORT_WIDTH;
import sync.simulation.regions.Region;
import sync.simulation.regions.UserStudiesCombined;
import util.Util;
import sync.simulation.Point;

public class Viewport extends Region{
	public int width = VIEWPORT_WIDTH;
	public int height = VIEWPORT_WIDTH;

	
	public Point[][] points = new Point[height][width];
	
	public Viewport(Point upperLeft){
		this.upperLeft = upperLeft;

		/*this.upperRight = UserStudiesCombined.tiles[upperLeft.y][upperLeft.x + width-1].point;
		this.lowerRight = UserStudiesCombined.tiles[upperRight.y + height-1][upperRight.x].point;
		this.lowerLeft = UserStudiesCombined.tiles[upperLeft.y + height-1][upperLeft.x].point;
		this.center = UserStudiesCombined.tiles[Integer.valueOf((int) (upperLeft.y+height/2.0))][Integer.valueOf((int) (upperLeft.x+width/2.0)) ].point;
		*/
		
		for (int y=upperLeft.y; y<=upperLeft.y+height-1; y++){
			for (int x=upperLeft.x; x<=upperLeft.x+width-1; x++){
				points[y][x] = Database.points(y,x); //database provides the points so we don't have so many of them
			}
		}
		
	}
	
	public Point randomPoint(){
		int ymin = upperLeft.y;
		int xmin = upperLeft.x;
		int ymax = ymin + (height - 1);
		int xmax = xmin + (width - 1);
		
		int yrand = Util.randInt(ymin,ymax);
		int xrand = Util.randInt(xmin,xmax);
		Point point = Database.points(yrand,xrand);
		return point;
		
	}
	
}
