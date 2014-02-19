package ver3.simulation;

import java.util.Comparator;

import ver3.simulation.CachedTile;
import ver3.simulation.events.UserMove;
import ver3.simulation.predictor.TileOverall;
import ver3.simulation.predictor.UserStudiesCombined;

public class CachedTile extends Tile {

	
	double totalImportance;//importance indicator
	final TileOverall tileStatistic;
	
	public CachedTile(Point point,UserMove currentPosition) {
		super(point);
		this.tileStatistic = UserStudiesCombined.tiles[point.y][point.x];
		UserStudiesCombined.tiles[point.y][point.x].updateImportance(currentPosition);
		this.totalImportance = UserStudiesCombined.tiles[point.y][point.x].totalImportance;
	}
	
	public void updateImportance(UserMove currentPosition){
		UserStudiesCombined.tiles[point.y][point.x].updateImportance(currentPosition);
		this.totalImportance = UserStudiesCombined.tiles[point.y][point.x].totalImportance;
	}
	
	
}
