package simulation.events;

import java.util.concurrent.ConcurrentLinkedQueue;

public class EventHandler {
	ConcurrentLinkedQueue<Prefetch> prefetchQueue  = new ConcurrentLinkedQueue<Prefetch>();
	ConcurrentLinkedQueue<Fetch> fetchQueue  = new ConcurrentLinkedQueue<Fetch>();
	ConcurrentLinkedQueue<TileFetch> tileQueue  = new ConcurrentLinkedQueue<TileFetch>(); //whole tile
	ConcurrentLinkedQueue<FragmentedTileFetch> fragmentedTileQueue  = new ConcurrentLinkedQueue<FragmentedTileFetch>(); //fragmented tiles
    
	public void handle(final UserMove event){
       //... handle
    }
    
    public void handle(final Prefetch event){
    	prefetchQueue.add(event);
    }
    
    public void handle(final Fetch event){
    	fetchQueue.add(event);
    }
    
    public void handle(final TileFetch event){
    	tileQueue.add(event);
    }
    
    public void handle(final FragmentedTileFetch event){
    	fragmentedTileQueue.add(event);
    }
    
    
    public void handle(final PrefetchFinished event){
    	prefetchQueue.poll().action();
    }

    
    public void handle(final FetchFinished event){
    	fetchQueue.poll().action();
    }
    
    public void handle(final TileFetchFinished event){
    	tileQueue.poll().action();
    }
    
    public void handle(final FragmentedTileFetchFinished event){
    	fragmentedTileQueue.poll().action();
    }
    
    
    
    
    
}

