package simulation.events;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

public class EventHandler {
	ConcurrentLinkedQueue<Prefetch> prefetchQueue  = new ConcurrentLinkedQueue<Prefetch>();
	ConcurrentLinkedQueue<Fetch> fetchQueue  = new ConcurrentLinkedQueue<Fetch>();
	
	ConcurrentLinkedQueue<TileFetch> tileQueue  = new ConcurrentLinkedQueue<TileFetch>(); //whole tile
	ConcurrentLinkedQueue<FragmentedTileFetch> fragmentedTileQueue  = new ConcurrentLinkedQueue<FragmentedTileFetch>(); //fragmented tiles
	
	ConcurrentLinkedQueue<TilePrefetch> prefetchTileQueue  = new ConcurrentLinkedQueue<TilePrefetch>(); //whole tile
	ConcurrentLinkedQueue<FragmentedTilePrefetch> prefetchFragmentedTileQueue  = new ConcurrentLinkedQueue<FragmentedTilePrefetch>(); //fragmented tiles
	
	public final static ReentrantLock fetchlock = new ReentrantLock();
	public final static ReentrantLock prefetchLock = new ReentrantLock();
	
	public EventHandler(){
		
		
		Thread fetchThread = new Thread() { 
			
			public void run() {
				while(true){
					if (fetchQueue.size()>0){
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
		
		Thread prefetchThread = new Thread() { 
			
			public void run() {
				while(true){
					if (prefetchQueue.size()>0){
						try {
								//EventHandler.lock.lock();
							prefetchQueue.poll().action();
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
					try {
						if (tileQueue.size()>0){
							if (!EventHandler.fetchlock.isLocked()){
								EventHandler.fetchlock.lock();
							}
							tileQueue.poll().action();
							//EventHandler.fetchlock.unlock();
						}
						else {
							if (EventHandler.fetchlock.getHoldCount()!=0 && EventHandler.fetchlock.isLocked()){
								EventHandler.fetchlock.unlock();
								
							}
						}
					
						Thread.sleep(100);
					} 
					catch (Exception e) {
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
							if (prefetchTileQueue.size()>0){
								prefetchTileQueue.poll().action();
						
							}
						}
						Thread.sleep(100);
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		tileThread.setPriority(10);
		prefetchTileThread.setPriority(1);
		prefetchThread.start();
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
    }
    
    public void handle(final TileFetch event) throws Exception{
    	tileQueue.add(event);
    }
    
    public void handle(final TilePrefetch event) throws Exception{
    	prefetchTileQueue.add(event);
    }
    
    public void handle(final FragmentedTileFetch event){
    	fragmentedTileQueue.add(event);
    }
    
    public void handle(final FragmentedTilePrefetch event){
    	prefetchFragmentedTileQueue.add(event);
    }
    
    
    
 

    
    
    
    
    
}

