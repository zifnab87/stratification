package ver3.simulation;

import static ver3.simulation.Config.VIEWPORT_WIDTH;
import ver3.simulation.Point;
import ver3.simulation.regions.Region;

public class Viewport extends Region{
	public int width = VIEWPORT_WIDTH;
	public int height = VIEWPORT_WIDTH;

	public Viewport(Point upperLeft){
		super(upperLeft);
	}
	
}
