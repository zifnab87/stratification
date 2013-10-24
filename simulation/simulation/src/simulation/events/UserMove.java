package simulation.events;

import static simulation.Config.VIEWPORT_HEIGHT;
import static simulation.Config.VIEWPORT_WIDTH;
import static simulation.Config.DATABASE_WIDTH;
import static simulation.Config.PREFETCH;
import static simulation.Config.debug;
import simulation.monitor.Workload;
import simulation.Main;
import simulation.Point;
import simulation.Viewport;
import simulation.monitor.Monitor;

public class UserMove extends Event {
	Point  upperLeft;
	public Viewport newMove;
	public String movementType;
	
	public UserMove(Viewport viewport){
		this.upperLeft = viewport.upperLeft;
		this.newMove = viewport;
		this.movementType = viewport.resultOfMovement;
	}
	
	public void action() throws Exception{
		
		Workload.writeMove(this);
		if (debug){
			System.out.println("Cache:"+Main.cache);
		}
		boolean isTerminal = newMove.upperLeft.x == DATABASE_WIDTH-1 && newMove.upperLeft.y == DATABASE_WIDTH-1;
		if (isTerminal){
			Event.sendEvent(new StopAll(Main.startTime));
		}
		if (debug){
			System.out.println("UserMove Event"+this.newMove.upperLeft);
		}
		Main.cache.updateAllTilesLOD(this.newMove);
		Event.sendEvent(new Fetch(this.newMove));
		if (PREFETCH){
			Event.sendEvent(new Prefetch(this.newMove));
		}
		Monitor.userMove();
		//Main.db.setViewport(newMove);
		
	}
	
}
