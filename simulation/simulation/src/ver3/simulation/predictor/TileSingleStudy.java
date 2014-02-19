package ver3.simulation.predictor;
import static ver3.simulation.Config.FRAGMENTS_PER_TILE;
import ver3.simulation.Point;
public class TileSingleStudy {
	//MAX zoomLevel that the Tile was visited
	double maxZoomLevel;
	//times the User jumped to
	int timesUserJumpedTo;
	final Point point;
	
	
	public TileSingleStudy(Point point){
		this.point = point;
	}
}
