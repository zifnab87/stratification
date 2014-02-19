package ver3.simulation;
import static ver3.simulation.Config.DATABASE_WIDTH;
public class Database {
	public static Point[][] points = new Point[DATABASE_WIDTH][DATABASE_WIDTH];
	
	public Database(){
		for (int y=0; y<points.length; y++){
			for (int x=0; x<points[0].length; x++){
				points[y][x] = new Point(y,x);
			}
		}
	}
	
}
