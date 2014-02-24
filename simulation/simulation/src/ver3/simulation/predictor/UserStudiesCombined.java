package ver3.simulation.predictor;

import java.util.Vector;

import ver3.simulation.Database;
import ver3.simulation.Point;
import static ver3.simulation.Config.DATABASE_WIDTH;

public class UserStudiesCombined {
	Vector<UserStudy> vec = new Vector<UserStudy>();
	
	public static TileOverall[][] tiles = new TileOverall[DATABASE_WIDTH][DATABASE_WIDTH];
	// go through all the UserStudies and create the TileOverall for each of the tiles 
	public UserStudiesCombined(){
		for (int y=0; y<tiles.length; y++){
			for (int x=0; x<tiles[0].length; x++){
				Point point = Database.points(y,x);
				tiles[y][x] = new TileOverall(point);
				//extra information in that TileOverall
			}
		}
	}
	
	
}
