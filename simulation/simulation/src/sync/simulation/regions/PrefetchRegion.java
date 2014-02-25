package sync.simulation.regions;

import java.util.TreeSet;

import util.Util;
import sync.simulation.CachedTile;
import sync.simulation.Database;
import sync.simulation.Point;
import static sync.simulation.Config.PREFETCH_REGION_WIDTH;

public class PrefetchRegion extends Region{

	public int width = PREFETCH_REGION_WIDTH;
	public int height = PREFETCH_REGION_WIDTH;

	private TreeSet<TileOverall> queue = new TreeSet<TileOverall>(TileOverall.importanceComparator);
	
	public Point[][] points = new Point[width][height];

	public PrefetchRegion(Point upperLeft){
		this.upperLeft = upperLeft;

//		this.upperRight = UserStudiesCombined.tiles[upperLeft.y][upperLeft.x + width-1].point;
//		this.lowerRight = UserStudiesCombined.tiles[upperRight.y + height-1][upperRight.x].point;
//		this.lowerLeft = UserStudiesCombined.tiles[upperLeft.y + height-1][upperLeft.x].point;
//		this.center = UserStudiesCombined.tiles[Integer.valueOf((int) (upperLeft.y+height/2.0))][Integer.valueOf((int) (upperLeft.x+width/2.0)) ].point;
		
		for (int y=upperLeft.y; y<=upperLeft.y+(height-1); y++){
			for (int x=upperLeft.x; x<=upperLeft.x+(width-1); x++){
				points[y][x] = UserStudiesCombined.tiles[y][x].point; //database provides the points so we don't have so many of them
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
		System.out.println(yrand+" "+xrand);
		System.out.println(point+"POINT");
		return point;
		
	}
}

