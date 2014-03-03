package sync.simulation.regions;

import java.util.TreeSet;

import util.Util;
import sync.simulation.Database;
import sync.simulation.Point;
import static sync.simulation.Config.JUMP_REGION_WIDTH;
import static sync.simulation.Config.DATABASE_WIDTH;

public class JumpRegion extends Region {
	public int width = JUMP_REGION_WIDTH;
	public int height = JUMP_REGION_WIDTH;

	
	private TreeSet<TileOverall> queue;
	
	
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
	
	
	
	public TreeSet<TileOverall> tree(Point current){
		queue = new TreeSet<TileOverall>(TileOverall.comparator);
		for (int y=upperLeft.y; y<Math.min(upperLeft.y+(this.height-1),DATABASE_WIDTH-1); y++){
			for (int x=upperLeft.x; x<Math.min(upperLeft.x+(this.width-1),DATABASE_WIDTH-1); x++){
				UserStudiesCombined.tiles[y][x].updateDistance(current);
				queue.add(UserStudiesCombined.tiles[y][x]);
			}
		}
		return queue;
		
	}
}
