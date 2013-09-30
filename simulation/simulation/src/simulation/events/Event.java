package simulation.events;

import java.lang.reflect.Method;

public abstract class Event {
	final EventHandler eventHandler = new EventHandler();
	public abstract void action() throws Exception;
	
	public void sendEvent (final Object event) throws Exception {
	    final Method method = EventHandler.class.getDeclaredMethod ("handle", new Class[] {event.getClass ()});
	    method.invoke (this.eventHandler, event);
	}
}
