package sync.simulation.regions;

import java.util.TreeSet;

import util.Util;
import sync.simulation.Database;
import sync.simulation.Point;
import static sync.simulation.Config.JUMP_REGION_WIDTH;

public class JumpRegion extends Region {
	public int width = JUMP_REGION_WIDTH;
	public int height = JUMP_REGION_WIDTH;

	
	private TreeSet<TileOverall> queue = new TreeSet<TileOverall>(TileOverall.jumpComparator);
	
	
	//public Point[][] points = new Point[height][width];

	public JumpRegion(Point upperLeft){
		this.upperLeft = upperLeft;
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
