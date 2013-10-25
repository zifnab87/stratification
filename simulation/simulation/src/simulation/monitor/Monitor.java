package simulation.monitor;

import static simulation.Config.PREFETCH;
import static simulation.Config.FRAGMENT;
import static simulation.Config.DATABASE_FRAGMENT_FETCH_TIME;
import static simulation.Config.DATABASE_TILE_FETCH_TIME;
import static simulation.Config.SIMULATION_FACTOR;
import static simulation.Config.FRAGMENTS_PER_TILE;
import static simulation.Config.WORKLOAD_FILE;
import static simulation.Config.CACHE_SIZE;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import simulation.Main;

public class Monitor {
	
	public static int tileCount  = 0;
	public static int fragmentCount = 0;
	public static int datbaseTileFetchCount = 0;
	public static int cacheTotalFragmentCount = 0; //the tiles are being converted to fragments
	public static int databaseTotalFramgnetCount = 0; //  >>
	public static int databaseFragmentFetchCount = 0;
	public static int cacheTileFetchCount = 0;
	public static int cacheFragmentFetchCount = 0;
	public static int userMoves = 0;
	
	public static String outputFileName(){
		String filename = WORKLOAD_FILE;

		filename += "_"+CACHE_SIZE;
		
		if (FRAGMENT){
			filename += "_fragments";
		}
		else {
			filename += "_tiles";
		}
		filename += ".txt";
		return filename;
	}
	
	public static void deleteOutputFile(){
		if (new File(Monitor.outputFileName()).exists()){
			try {
				Files.delete(new File(Monitor.outputFileName()).toPath());
			} catch (IOException e) {
				// TODO Auto-generated catch block
			}
		}
	}
	
	public static void writeToFile(){
		String filename = outputFileName();
		
		
		try {
			PrintWriter pw = new PrintWriter(new FileWriter(filename, true));
			
			double simTime =(datbaseTileFetchCount*DATABASE_TILE_FETCH_TIME + databaseFragmentFetchCount*DATABASE_FRAGMENT_FETCH_TIME)*SIMULATION_FACTOR;
			//database fetched fragments	cache fetched fragments		cacheusage		sumulation time
			pw.append(databaseTotalFramgnetCount+"\t"+cacheTotalFragmentCount+"\t"+Main.cache.sizeBeingUsed()+"\t"+simTime+"\n");
			pw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public static void display(double starttime){
		//System.out.println("Cache:"+Main.cache);
		
		System.out.println("prefetch: "+PREFETCH);
		System.out.println("fragment: "+FRAGMENT);
		System.out.println("Total User Moves :"+ userMoves);
		System.out.println("fragmentCount :"+ fragmentCount);
		System.out.println("databaseFragmentFetchCount :"+databaseFragmentFetchCount);
		System.out.println("cacheFragmentFetchCount :"+cacheFragmentFetchCount);
		System.out.println("tileCount :"+ tileCount);
		System.out.println("databaseTileFetchCount :"+datbaseTileFetchCount);
		System.out.println("cacheTileFetchCount :"+cacheTileFetchCount);
		System.out.println("cacheSpaceUsed :"+Main.cache.sizeBeingUsed());
		double endtime = (System.nanoTime() - starttime)/1000000000d;
		System.out.println("Running time: "+endtime);
		double simTime =(datbaseTileFetchCount*DATABASE_TILE_FETCH_TIME + databaseFragmentFetchCount*DATABASE_FRAGMENT_FETCH_TIME)*SIMULATION_FACTOR;
		System.out.println("Simulated time: "+simTime);
	}
	
	
	
	
	public static synchronized void databaseTileFetch(){
		try {
			Thread.sleep(simulation.Config.DATABASE_TILE_FETCH_TIME);
			datbaseTileFetchCount++;
			tileCount++;
			databaseTotalFramgnetCount += FRAGMENTS_PER_TILE;
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
			databaseTotalFramgnetCount++;
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
		cacheTotalFragmentCount += FRAGMENTS_PER_TILE;
	}
	
	public static synchronized void cacheFragmentFetch(){
		cacheFragmentFetchCount++;
		fragmentCount++;
		cacheTotalFragmentCount++;
		
	}
	
	
	public static synchronized void userMove(){
		userMoves++;
	}
	
	
	
	
	
	
	
	
	
}
