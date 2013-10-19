package simulation.events;

import static simulation.Config.VIEWPORT_HEIGHT;
import static simulation.Config.VIEWPORT_WIDTH;
import static simulation.Config.DATABASE_WIDTH;
import static simulation.Config.PREFETCH;
import simulation.Main;
import simulation.Point;
import simulation.Viewport;

public class UserMove extends Event {
	Point  upperLeft;
	public Viewport newMove;
	
	public UserMove(Point upperLeft){
		this.upperLeft = upperLeft;
		this.newMove = new Viewport(VIEWPORT_HEIGHT,VIEWPORT_WIDTH, upperLeft);
	}
	
	public UserMove(Viewport viewport){
		this.upperLeft = viewport.upperLeft;
		this.newMove = viewport;
	}
	
	public void action() throws Exception{
		boolean isTerminal = newMove.upperLeft.x == DATABASE_WIDTH-1 && newMove.upperLeft.y == DATABASE_WIDTH-1;
		if (isTerminal){
			Event.sendEvent(new StopAll());
		}
		System.out.println("UserMove Event"+this.newMove.upperLeft);
		
		Main.cache.updateAllTilesLOD(this.newMove);
		Event.sendEvent(new Fetch(this.newMove));
		if (PREFETCH){
			Event.sendEvent(new Prefetch(this.newMove));
		}
		//Main.db.setViewport(newMove);
		
	}
	
}
