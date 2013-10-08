package simulation.monitor;

public class Monitor {
	
	public static int tileCount  = 0;
	public static int fragmentCount = 0;
	public static int datbaseTileFetchCount = 0;
	public static int databaseFragmentFetchCount = 0;
	public static int cacheTileFetchCount = 0;
	public static int cacheFragmentFetchCount = 0;
	
	public synchronized void databaseTileFetch(){
		try {
			Thread.sleep(simulation.Config.DATABASE_TILE_FETCH_TIME);
			datbaseTileFetchCount++;
			tileCount++;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void databaseFragmentFetch(){
		try {
			Thread.sleep(simulation.Config.DATABASE_FRAGMENT_FETCH_TIME);
			databaseFragmentFetchCount++;
			fragmentCount++;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public synchronized void cacheTileFetch(){
		cacheTileFetchCount++;
		tileCount++;
	}
	
	public synchronized void cacheFragmentFetch(){
		cacheFragmentFetchCount++;
		fragmentCount++;
	}
	
	
	
	
	
	
	
	
	
	
}
