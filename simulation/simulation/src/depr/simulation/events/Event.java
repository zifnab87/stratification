package depr.simulation.events;

import java.lang.reflect.Method;

public abstract class Event {
	final public static EventHandler eventHandler = new EventHandler();
	public abstract void action() throws Exception;
	
	public static void sendEvent (final Object event) throws Exception {
	    final Method method = EventHandler.class.getDeclaredMethod ("handle", new Class[] {event.getClass ()});
	    method.invoke (eventHandler, event);
	}
}
