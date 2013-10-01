package simulation.events;

import static simulation.Config.VIEWPORT_HEIGHT;
import static simulation.Config.VIEWPORT_WIDTH;
import simulation.Main;
import simulation.Point;
import simulation.Viewport;

public class UserMove extends Event {
	Point  upperLeft;
	
	public UserMove(Point upperLeft){
		this.upperLeft = upperLeft;
	}
	
	public void action() throws Exception{
		System.out.println("UserMove Event");
		Viewport newMove = new Viewport(VIEWPORT_HEIGHT,VIEWPORT_WIDTH, upperLeft);
		Main.db.setViewport(newMove);
		this.sendEvent(new Fetch(newMove));
		this.sendEvent(new Prefetch(newMove));
	}
	
}
