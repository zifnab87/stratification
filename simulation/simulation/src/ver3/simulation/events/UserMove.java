package ver3.simulation.events;

import static ver3.simulation.Config.THINK_TIME;
import ver3.simulation.Point;
//import sync.simulation.Run;
import ver3.simulation.Viewport;

public class UserMove {
	public Point  upperLeft;
	public Viewport viewport;
	public String movementType;
	
	public int cacheHits = 0;
	public int cacheMisses = 0;
	

	public double cacheMissesDuringFetch = 0;
	public int cacheHitsDuringFetch = 0;
	public double latencyDuringFetch = 0;
	
	public int thinkTime = THINK_TIME;
	
	public Run run;
}
