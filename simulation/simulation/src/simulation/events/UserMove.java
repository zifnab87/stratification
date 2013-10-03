package simulation.events;

import static simulation.Config.VIEWPORT_HEIGHT;
import static simulation.Config.VIEWPORT_WIDTH;
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
	
	public void action() throws Exception{
		System.out.println("UserMove Event");
		
		Main.cache.updateAllTilesLOD(this.newMove);
		this.sendEvent(new Fetch(this.newMove));
		this.sendEvent(new Prefetch(this.newMove));
		
		//Main.db.setViewport(newMove);
		
	}
	
}
