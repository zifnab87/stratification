package simulation.events;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

import simulation.Main;

public class EventHandler {
	ConcurrentLinkedQueue<Prefetch> prefetchQueue  = new ConcurrentLinkedQueue<Prefetch>();
	ConcurrentLinkedQueue<Fetch> fetchQueue  = new ConcurrentLinkedQueue<Fetch>();
	
	ConcurrentLinkedQueue<TileFetch> tileQueue  = new ConcurrentLinkedQueue<TileFetch>(); //whole tile
	ConcurrentLinkedQueue<FragmentedTileFetch> fragmentedTileQueue  = new ConcurrentLinkedQueue<FragmentedTileFetch>(); //fragmented tiles
	
	ConcurrentLinkedQueue<TilePrefetch> prefetchTileQueue  = new ConcurrentLinkedQueue<TilePrefetch>(); //whole tile
	ConcurrentLinkedQueue<FragmentedTilePrefetch> prefetchFragmentedTileQueue  = new ConcurrentLinkedQueue<FragmentedTilePrefetch>(); //fragmented tiles
	
	public final static ReentrantLock fetchlock = new ReentrantLock();
	public final static ReentrantLock prefetchLock = new ReentrantLock();
	
	public volatile boolean stopAll = false;
	
	ArrayList<Thread> threads = new ArrayList<Thread>();
	
	public EventHandler(){
		
		
		Thread fetchThread = new Thread() { 
			
			public void run() {
				while(!stopAll){
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
		threads.add(fetchThread);
		
		
		Thread prefetchThread = new Thread() { 
			
			public void run() {
				while(!stopAll){
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
		threads.add(prefetchThread);
		//-----------
		
		Thread fetchTileThread = new Thread() { 
			public void run() {
				while(!stopAll){
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
		threads.add(fetchTileThread);
		
		Thread fetchFragmentedTileThread = new Thread(){
			public void run() {
				while(true){
					try {
						if (fragmentedTileQueue.size()>0){
							if (!EventHandler.fetchlock.isLocked()){
								EventHandler.fetchlock.lock();
							}
							fragmentedTileQueue.poll().action();
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
		threads.add(fetchFragmentedTileThread);
		
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
		
		threads.add(prefetchTileThread);
		
		Thread prefetchFragmentedTileThread = new Thread() { 
			public void run() {
				while(true){
					try {
						
						if (!EventHandler.fetchlock.isLocked()){
							if (prefetchFragmentedTileQueue.size()>0){
								prefetchFragmentedTileQueue.poll().action();
						
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
		threads.add(prefetchFragmentedTileThread);
		
		fetchTileThread.setPriority(10);
		fetchFragmentedTileThread.setPriority(9);
		prefetchTileThread.setPriority(2);
		prefetchFragmentedTileThread.setPriority(1);
		
		
		
		fetchThread.start();
		prefetchThread.start();
		
		fetchTileThread.start();
		fetchFragmentedTileThread.start();
		prefetchTileThread.start();
		prefetchFragmentedTileThread.start();
		   
		   
		   
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
    	
    	if (!Main.cache.tileExists(event.pointToFetch)){
    		tileQueue.add(event);
    	}
    	else {
    		
    	}
    }
    
    public void handle(final TilePrefetch event) throws Exception{
    	if (!prefetchTileQueue.contains(event)){
    		prefetchTileQueue.add(event);
    		
    	}
    	else {
    	}
    }
    
    public void handle(final FragmentedTileFetch event){
    	if (!fragmentedTileQueue.contains(event)){
    		fragmentedTileQueue.add(event);
    	}
    }
    
    public void handle(final FragmentedTilePrefetch event){
    	if (!prefetchFragmentedTileQueue.contains(event)){
    		prefetchFragmentedTileQueue.add(event);
    	}
    }
    
    public void handle(final StopAll event){
    	stopAll = true;
    	System.exit(0);
    }
    
    
    
 

    
    
    
    
    
}

