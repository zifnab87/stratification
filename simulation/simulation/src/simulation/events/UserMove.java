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
		Main.db.setViewport(new Viewport(VIEWPORT_HEIGHT,VIEWPORT_WIDTH, upperLeft));
		this.sendEvent(new Fetch(Main.viewport));
	}
	
}
