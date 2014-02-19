package ver3.simulation.regions;

import java.util.TreeSet;

import ver3.simulation.CachedTile;
import ver3.simulation.Point;
import ver3.simulation.predictor.TileOverall;
import static ver3.simulation.Config.PREFETCH_REGION_WIDTH;

public class PrefetchRegion extends Region{

	public int width = PREFETCH_REGION_WIDTH;
	public int height = PREFETCH_REGION_WIDTH;

	private TreeSet<TileOverall> queue = new TreeSet<TileOverall>(TileOverall.importanceComparator);
	
	public PrefetchRegion(Point upperLeft){
		super(upperLeft);
	}
}

