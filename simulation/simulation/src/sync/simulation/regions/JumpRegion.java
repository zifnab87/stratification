package sync.simulation.regions;

import java.util.TreeSet;

import util.Util;
import sync.simulation.Cache;
import sync.simulation.CachedTile;
import sync.simulation.Database;
import sync.simulation.Point;
import sync.simulation.Main;
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
		CachedTile worstCachedTile = Main.cache.getWorst(current);
		queue = new TreeSet<TileOverall>(TileOverall.comparator);
		Main.cache.updateImportances(current);
		for (int y=upperLeft.y; y<=Math.min(upperLeft.y+(this.height-1),DATABASE_WIDTH-1); y++){
			for (int x=upperLeft.x; x<=Math.min(upperLeft.x+(this.width-1),DATABASE_WIDTH-1); x++){
				UserStudiesCombined.tiles[y][x].updateImportance(current);
				// while the cache is getting fuller we still need things that are worse than the worst tile..
				
				
				if (!Main.cache.isFull() || UserStudiesCombined.tiles[y][x].totalImportance>worstCachedTile.totalImportance){
					
					//if (current.y!=y && current.x!=x || 
						//!Main.cache.tileExists(Database.points(y,x)) 
						//||  (Main.cache.tileExists(Database.points(y,x)) && Main.cache.queueFind(Database.points(y,x).id).getCachedFragmentsNum()<UserStudiesCombined.tiles[y][x].howManyFragments(current))
					   //){ 
						
						
						
						//don't prefetch current,
						//prefetch what is not in the cache or is in the cache with less fragments
						
						/*if (Main.cache.tileExists(Database.points(y,x)) && Main.cache.queueFind(Database.points(y,x).id).getCachedFragmentsNum()<UserStudiesCombined.tiles[y][x].howManyFragments()){
							System.out.println("y:"+y+" x:"+x+" "+Main.cache.queueFind(Database.points(y,x).id).getCachedFragmentsNum()+" < "+UserStudiesCombined.tiles[y][x].howManyFragments());
						}*/
						queue.add(UserStudiesCombined.tiles[y][x]);
					//}
				}
			}
		}
		return queue;
		
	}
	
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
