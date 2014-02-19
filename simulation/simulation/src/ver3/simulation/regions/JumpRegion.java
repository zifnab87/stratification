package ver3.simulation.regions;

import java.util.TreeSet;

import ver3.simulation.Point;
import ver3.simulation.predictor.TileOverall;
import static ver3.simulation.Config.JUMP_REGION_WIDTH;

public class JumpRegion extends Region {
	public int width = JUMP_REGION_WIDTH;
	public int height = JUMP_REGION_WIDTH;

	
	private TreeSet<TileOverall> queue = new TreeSet<TileOverall>(TileOverall.jumpComparator);
	
	public JumpRegion(Point upperLeft){
		super(upperLeft);
	}
}
