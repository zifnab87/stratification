package simulation.monitor;

public class Monitor {
	
	public static int tileCount  = 0;
	public static int fragmentCount = 0;
	public static int datbaseTileFetchCount = 0;
	public static int databaseFragmentFetchCount = 0;
	public static int cacheTileFetchCount = 0;
	public static int cacheFragmentFetchCount = 0;
	
	public static void display(double starttime){
		System.out.println("fragmentCount "+ fragmentCount);
		System.out.println("databaseFragmentFetchCount "+databaseFragmentFetchCount);
		System.out.println("cacheFragmentFetchCount "+cacheFragmentFetchCount);
		System.out.println("tileCount "+ tileCount);
		System.out.println("databaseTileFetchCount "+datbaseTileFetchCount);
		System.out.println("cacheTileFetchCount "+cacheTileFetchCount);
		double endtime = (System.nanoTime() - starttime)/1000000000d;
		System.out.println("Running time: "+endtime);

	}
	
	
	public static synchronized void databaseTileFetch(){
		try {
			Thread.sleep(simulation.Config.DATABASE_TILE_FETCH_TIME);
			datbaseTileFetchCount++;
			tileCount++;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static synchronized void databaseTilePrefetch(){
		databaseTileFetch();
	}
	
	public static synchronized void databaseFragmentFetch(){
		try {
			Thread.sleep(simulation.Config.DATABASE_FRAGMENT_FETCH_TIME);
			databaseFragmentFetchCount++;
			fragmentCount++;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static synchronized void databaseFragmentPrefetch(){
		databaseFragmentFetch();
	}
	
	public static synchronized void cacheTileFetch(){
		cacheTileFetchCount++;
		tileCount++;
	}
	
	public static synchronized void cacheFragmentFetch(){
		cacheFragmentFetchCount++;
		fragmentCount++;
	}
	
	
	
	
	
	
	
	
	
	
}
