package depr.simulation.events;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.ReentrantLock;

import depr.simulation.Main;
import depr.simulation.monitor.Monitor;
import static depr.simulation.Config.DATABASE_FRAGMENT_FETCH_TIME;
import static depr.simulation.Config.DATABASE_TILE_FETCH_TIME;
import static depr.simulation.Config.USER_MOVEMENT_TIME;


public class EventHandler {
	
	public static ConcurrentLinkedQueue<UserMove> userMoveQueue = new ConcurrentLinkedQueue<UserMove>();
	
	public static ConcurrentLinkedQueue<Prefetch> prefetchQueue  = new ConcurrentLinkedQueue<Prefetch>();
	public static ConcurrentLinkedQueue<Fetch> fetchQueue  = new ConcurrentLinkedQueue<Fetch>();
	
	public static ConcurrentLinkedQueue<TileFetch> tileQueue  = new ConcurrentLinkedQueue<TileFetch>(); //whole tile
	public static ConcurrentLinkedQueue<FragmentedTileFetch> fragmentedTileQueue  = new ConcurrentLinkedQueue<FragmentedTileFetch>(); //fragmented tiles
	
	public static ConcurrentLinkedQueue<TilePrefetch> prefetchTileQueue  = new ConcurrentLinkedQueue<TilePrefetch>(); //whole tile
	public static ConcurrentLinkedQueue<FragmentedTilePrefetch> prefetchFragmentedTileQueue  = new ConcurrentLinkedQueue<FragmentedTilePrefetch>(); //fragmented tiles
	
	public final static ReentrantLock databaseLock = new ReentrantLock();
	//public final static ReentrantLock predatabaseLock = new ReentrantLock();
	public final static ReentrantLock usermovelock = new ReentrantLock();
	
	
	public void lockDatabase(){
		if (!databaseLock.isLocked()){
			databaseLock.lock();
		}
	}
	
	public void unlockDatabase(){
		if (databaseLock.isLocked() && EventHandler.databaseLock.getHoldCount()!=0){
			databaseLock.unlock();
		}
	}
	
	public boolean isDatabaseLocked(){
		return databaseLock.isLocked();
	}
	
	public void lockUser(){
		if (!usermovelock.isLocked()){
			usermovelock.lock();
		}
	}
	
	public void unlockUser(){
		if (usermovelock.isLocked() && EventHandler.usermovelock.getHoldCount()!=0){
			usermovelock.unlock();
		}
	}
	
	public boolean isUserLocked(){
		return usermovelock.isLocked();
	}
	
	public volatile boolean stopAll = false;
	
	ArrayList<Thread> threads = new ArrayList<Thread>();
	
	public EventHandler(){
		
		/* assumptions
		 * fetch and prefetch block each other (only one request in database at a time)
		 * usermove happens only when all the fetches and prefetches have happened
		 * cache can happen anytime
		 */
		
		
		Thread fetchThread = new Thread() { 
			
			public void run() {
				while(!stopAll){
					if (fetchQueue.size()>0){
						try {
								fetchQueue.poll().action();
							
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
							prefetchQueue.poll().action();
							
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
							lockUser();
							if (!isDatabaseLocked()){
								lockDatabase();
								tileQueue.poll().action();
							}
							unlockDatabase();
						}
						else {
							unlockUser();
						}
					
						Thread.sleep(DATABASE_TILE_FETCH_TIME);
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
							lockUser();
							if (!isDatabaseLocked()){
								lockDatabase();
								fragmentedTileQueue.poll().action();
							}
							unlockDatabase();
							//EventHandler.databaseLock.unlock();
						}
						else {
							unlockUser();
						}
					
						Thread.sleep(DATABASE_FRAGMENT_FETCH_TIME);
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
				while(!stopAll){
					try {
						if (prefetchTileQueue.size()>0){
							lockUser();
							if (!isDatabaseLocked()){
								lockDatabase();
								prefetchTileQueue.poll().action();
							}
							unlockDatabase();
						}
						else {
							unlockUser();
						}
						
						Thread.sleep(DATABASE_TILE_FETCH_TIME);
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
				while(!stopAll){
					try {
						if (prefetchFragmentedTileQueue.size()>0){
							lockUser();
							if (!isDatabaseLocked()){
								lockDatabase();
								prefetchFragmentedTileQueue.poll().action();
							}
							unlockDatabase();
						}
						else {
							unlockUser();
						}
						Thread.sleep(DATABASE_TILE_FETCH_TIME);
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		threads.add(prefetchFragmentedTileThread);
		
		
		
		Thread userMoveThread = new Thread() { 
			public void run() {
				while(!stopAll){
					try {
						if (userMoveQueue.size()>0){
							if (!isUserLocked() && tileQueue.size()==0 && 
								fragmentedTileQueue.size()==0 &&
								prefetchFragmentedTileQueue.size()==0 &&
								prefetchTileQueue.size()==0 ){
								lockUser();
								userMoveQueue.poll().action();
								unlockUser();
								//prefetchTileQueue.clear();
								//prefetchFragmentedTileQueue.clear();
							}
						}
						Thread.sleep(100);
						//Thread.sleep(USER_MOVEMENT_TIME);
					} 
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		};
		userMoveThread.setPriority(10);
		fetchTileThread.setPriority(10);
		fetchFragmentedTileThread.setPriority(9);
		prefetchTileThread.setPriority(2);
		prefetchFragmentedTileThread.setPriority(1);
		
		
		//userMoveThread.start();
		//fetchThread.start();
		//prefetchThread.start();
		
		//fetchTileThread.start();
		//fetchFragmentedTileThread.start();
		//prefetchTileThread.start();
		//prefetchFragmentedTileThread.start();
		   
		   
		   
	}
	
	public void handle(final UserMove event){
       userMoveQueue.add(event);
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
    	try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Monitor.display(event.startTime);
		System.out.println("TELOS");
    	System.exit(0);
    }
    
    
    
 

    
    
    
    
    
}

