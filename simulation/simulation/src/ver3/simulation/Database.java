package ver3.simulation;
import static ver3.simulation.Config.DATABASE_WIDTH;
import static ver3.simulation.Config.JUMP_REGION_WIDTH;
import util.Util;
import ver3.simulation.predictor.UserStudiesCombined;
import ver3.simulation.regions.Region;
public class Database extends Region {
	public static Point[][] points = new Point[DATABASE_WIDTH][DATABASE_WIDTH];
	
	public int width = DATABASE_WIDTH;
	public int height = DATABASE_WIDTH;
	
	public Database(){
		
		
		for (int y=0; y<points.length; y++){
			for (int x=0; x<points[0].length; x++){
				points[y][x] = new Point(y,x);
			}
		}

		this.upperLeft = points(0,0);
	}
	
	
	
	public static Point points(int y,int x){
		if (x < 0) {
			x = 0;
		}
		else if(x > DATABASE_WIDTH-1){
			x = DATABASE_WIDTH-1;
		}
		if (y < 0) {
			y = 0;
		}
		else if(y > DATABASE_WIDTH-1){
			y = DATABASE_WIDTH-1;
		}

		return points[y][x];
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
