package simulation.events;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class EventHandler {
	ConcurrentLinkedQueue<Prefetch> prefetchQueue  = new ConcurrentLinkedQueue<Prefetch>();
	ConcurrentLinkedQueue<Fetch> fetchQueue  = new ConcurrentLinkedQueue<Fetch>();
	ConcurrentLinkedQueue<TileFetch> tileQueue  = new ConcurrentLinkedQueue<TileFetch>(); //whole tile
	ConcurrentLinkedQueue<FragmentedTileFetch> fragmentedTileQueue  = new ConcurrentLinkedQueue<FragmentedTileFetch>(); //fragmented tiles
	
	public final static ReentrantLock fetchlock = new ReentrantLock();
	public final static ReentrantLock prefetchLock = new ReentrantLock();
	public EventHandler(){
		
		
		
		
		Thread fetchThread = new Thread() { 
			
			public void run() {
				while(true){
					if (fetchQueue.size()>=1){
						try {
								//EventHandler.lock.lock();
								fetchQueue.poll().action();
								//EventHandler.lock.unlock();
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
								e.printStackTrace();
						}
					}
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
		};
		
		Thread tileThread = new Thread() { 
			public void run() {
				while(true){
					if (tileQueue.size()>=1){
						try {
							if (!EventHandler.fetchlock.isLocked()){
								EventHandler.fetchlock.lock();
							}
							tileQueue.poll().action();
							//EventHandler.fetchlock.unlock();
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
								e.printStackTrace();
						}
					}
					else {
						if (EventHandler.fetchlock.getHoldCount()!=0 && EventHandler.fetchlock.isLocked()){
							EventHandler.fetchlock.unlock();
							
						}
					}
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		
		Thread prefetchTileThread = new Thread() { 
			public void run() {
				while(true){
					try {
						if (!EventHandler.fetchlock.isLocked()){
							System.out.println("prefetch");
							//System.out.println("unlock");
							Thread.sleep(1000);
						}
						
						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		};
		tileThread.setPriority(10);
		prefetchTileThread.setPriority(1);
		fetchThread.start();
		tileThread.start();
		prefetchTileThread.start();
		   
		   
		   
		   
	}
	
	public void handle(final UserMove event){
       //... handle
    }
    
    public void handle(final Prefetch event){
    	prefetchQueue.add(event);
    }
    
    public void handle(final Fetch event) throws Exception{
    	fetchQueue.add(event);
    	if (fetchQueue.size()==1){
    		event.action();
    	}
    }
    
    public void handle(final TileFetch event) throws Exception{
    	tileQueue.add(event);
    	if (tileQueue.size()==1){
    		event.action();
    	}
    }
    
    public void handle(final FragmentedTileFetch event){
    	fragmentedTileQueue.add(event);
    }
    
 

    
    
    
    
    
}

