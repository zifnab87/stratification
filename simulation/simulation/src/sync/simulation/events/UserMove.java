package sync.simulation.events;

import static sync.simulation.Config.FRAGMENT;
import static sync.simulation.Config.FRAGMENTS_PER_TILE;
import static sync.simulation.Config.DATABASE_WIDTH;
import static sync.simulation.Config.SKIP_PREDICTIONS;
import static sync.simulation.Config.DEBUG;
import static sync.simulation.Config.USER_THINK_DISTR;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;
import java.util.Vector;

import sync.simulation.Cache;
import sync.simulation.CachedTile;
import sync.simulation.Database;
import sync.simulation.Main;
import sync.simulation.Point;
import sync.simulation.Run;
import sync.simulation.Tile;
import sync.simulation.Viewport;
import sync.simulation.predictor.Node;
//import static sync.simulation.Config.PREFETCH;
import sync.simulation.predictor.Predictor;
import sync.simulation.predictor.Tuple;
import sync.simulation.regions.JumpRegion;
import sync.simulation.regions.Region;
import sync.simulation.regions.TileOverall;
import util.Util;
import static sync.simulation.Config.THINK_TIME;
import static sync.simulation.Config.FRAGMENTS_PER_TILE;
import static sync.simulation.Config.THINK_TIME;

public class UserMove {
//	public Point  upperLeft;
//	public Viewport viewport;
//	public String movementType;
//	
//	public int cacheHits = 0;
//	public int cacheMisses = 0;
//	
//
//	public double cacheMissesDuringFetch = 0;
//	public int cacheHitsDuringFetch = 0;
//	public double latencyDuringFetch = 0;
//	
//	public int thinkTime = THINK_TIME;
	
	public Point  point;
	public Viewport viewport;
	public String movementType;
	
	public int cacheHits = 0;
	public int cacheMisses = 0;
	

	public double cacheMissesDuringFetch = 0;
	public int cacheHitsDuringFetch = 0;
	public double latencyDuringFetch = 0;
	
	public int thinkTime = THINK_TIME;
	
	
	public static int currentZoomLevel = 1;
	
	public Run run;
	
	
	
	
	public static int nextFromDistribution(Random rand){
		if (USER_THINK_DISTR.equalsIgnoreCase("Gaussian")){
			double think = rand.nextGaussian()*5.33d + 16;
			return (int)Math.floor(think);
		}
		else if(USER_THINK_DISTR.equalsIgnoreCase("NegativeExponential")){
			double lambda = 0.2;
			double low = 1;
			double high = 32;
			double U = Math.random();
			double think = -Math.log(Math.exp(-lambda*low) - (Math.exp(-lambda*low) - Math.exp(-lambda*high)) * U) / lambda;
			return (int)think;
		}
		return 1;
	}
	
	
/*	public UserMove(Viewport viewport,Run run, int a){
		this.run = run;
		this.point = viewport.upperLeft;
		this.viewport = viewport;
		this.movementType = viewport.resultOfMovement;
	}*/
	
	/*public void write(){
		try {
			Workload.writeMove(this);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	
	public void prefetch(JumpRegion region,Point current){
		/*if (toPrefetch.size()==0){
			return;
		}*/
		double start = System.currentTimeMillis();
		TreeSet<TileOverall> toPrefetch = region.tree(current);
		Util.debug("Prefetch Queue"+toPrefetch);
		Iterator<TileOverall> iter = toPrefetch.iterator();
		int availThinkTime = thinkTime;
		//System.out.println(availThinkTime);
		int count = 0;
		while (iter.hasNext() && availThinkTime>0){
			
			boolean prefetched  = false;
			TileOverall tileStatistic = iter.next();
			Point point = tileStatistic.point;
			int index = point.hashCode();
			int fragmentsNeeded = FRAGMENTS_PER_TILE;
			if (FRAGMENT){
				fragmentsNeeded = tileStatistic.howManyFragments(current);
			}
			else {
				fragmentsNeeded = FRAGMENTS_PER_TILE;//tile.fragmentsNeeded;
			}
		
			
			
			
			//CHECK What is in the cache
			
			if (!Main.cache.tileExists(index)){
				Tile tile = Main.db.getTileWithFragmentRange( point,1,fragmentsNeeded,null);
				Main.cache.cacheTileWithFragmentRange(tile,current,1,fragmentsNeeded);
				count++;
			}
			else {
				CachedTile cachedPartialTile = Main.cache.getTile(point);
				int cachedLOD = cachedPartialTile.getCachedFragmentsNum();
				if (cachedLOD < fragmentsNeeded){
					Tile tile = Main.db.getTileWithFragmentRange( point,cachedLOD+1,fragmentsNeeded,null);
					Main.cache.cacheTileWithFragmentRange(tile,current,cachedLOD+1,fragmentsNeeded);
					count++;
				}
			}
			double end = (System.currentTimeMillis() - start);
			//System.out.println(end);
			availThinkTime -= end; //msec
			
			
			
//			if (LOD == FRAGMENTS_PER_TILE) {// the tile is needed to be full
//				
//				if (Main.cache.tileExistsAndFull(index)){
//					Main.cache.fetchTile(index, this);
//				}
//				else if(Main.cache.tileExistsAndNotFull(index)){
//					//find what's missing
//					CachedTile cachedPartialTile = Main.cache.getTile(point);
//					int cachedLOD = cachedPartialTile.getCachedFragmentsNum();
//					
//					fragmentsToBePrefetched = CachedTile.getMissingFragmentIdsTillFull(cachedLOD);
//					if (FRAGMENT){
//						Vector<Integer> fragmentsAfterThinkingTime = new Vector<Integer>();
//						for (int i=1; i<=Math.min(availThinkTime,fragmentsToBePrefetched.size()); i++){
//							fragmentsAfterThinkingTime.add(fragmentsToBePrefetched.get(i-1));
//						}
//						fragmentsToBePrefetched = fragmentsAfterThinkingTime;
//					}
//					
//					//Util.debug(availThinkTime+"-"+fragmentsToBePrefetched.size());
//					if (availThinkTime-fragmentsToBePrefetched.size()<0){
//						return ;
//					}
//					int fragmCount = fragmentsToBePrefetched.size();
//					int firstFragment = fragmentsToBePrefetched.get(0);
//					int lastFragment = fragmentsToBePrefetched.get(fragmCount-1);
//					
//					Tile tile = Main.db.fetchTileWithFragmentRange(point, firstFragment,lastFragment, this);
//					prefetched  = true;
//					tile.carryingProbability = node.probability; // carry it to the cache
//					Main.cache.cacheTileWithFragmentRange(tile,firstFragment,lastFragment);
//					availThinkTime -= fragmentsToBePrefetched.size();
//					//that many were cached
//					Main.cache.fetchTile(index, this);
//					
//
//				}
//				else { //tile doesn't exist in Cache
//					// full Database Fetch
//					
//					prefetched  = true;
//					fragmentsToBePrefetched = CachedTile.getMissingFragmentIdsTillFull(0);
//					if (FRAGMENT){
//						Vector<Integer> fragmentsAfterThinkingTime = new Vector<Integer>();
//						for (int i=1; i<=Math.min(availThinkTime,fragmentsToBePrefetched.size()); i++){
//							fragmentsAfterThinkingTime.add(fragmentsToBePrefetched.get(i-1));
//						}
//						fragmentsToBePrefetched = fragmentsAfterThinkingTime;
//					}
//					if (availThinkTime-fragmentsToBePrefetched.size()<0){
//						this.thinkTime = availThinkTime;
//						return;
//					}
//					
//					int fragmCount = fragmentsToBePrefetched.size();
//					int firstFragment = fragmentsToBePrefetched.get(0);
//					int lastFragment = fragmentsToBePrefetched.get(fragmCount-1);
//					Tile tile = Main.db.fetchTileWithFragmentRange(point, firstFragment,lastFragment, this);
//					availThinkTime-=fragmCount;
//					tile.carryingProbability = node.probability;
//					Main.cache.cacheTileWithFragmentRange(tile, 1, FRAGMENTS_PER_TILE);
//				}
//			}
//			else if (LOD>0 && LOD<FRAGMENTS_PER_TILE){ //the tile doesn't need to be full
//				if (Main.cache.tileExists(index)){
//					CachedTile cachedPartialTile = Main.cache.getTile(point);
//					int cachedLOD = cachedPartialTile.getCachedFragmentsNum();
//					if (cachedLOD < LOD){
//						
//						if (FRAGMENT){
//							fragmentsToBePrefetched = CachedTile.getMissingFragmentIdsTillLOD(cachedLOD,LOD);
//							Vector<Integer> fragmentsAfterThinkingTime = new Vector<Integer>();
//							for (int i=1; i<=Math.min(availThinkTime,fragmentsToBePrefetched.size()); i++){
//								fragmentsAfterThinkingTime.add(fragmentsToBePrefetched.get(i-1));
//							}
//							fragmentsToBePrefetched = fragmentsAfterThinkingTime;
//						}
//						int fragmCount = fragmentsToBePrefetched.size();
//						int firstFragment = fragmentsToBePrefetched.get(0);
//						int lastFragment = fragmentsToBePrefetched.get(fragmCount-1);
//						Util.debug(availThinkTime+"-"+fragmentsToBePrefetched.size());
//						if (availThinkTime-fragmentsToBePrefetched.size()<0){
//							this.thinkTime = availThinkTime;
//							return ;
//						}
//						
//						Tile tile = Main.db.fetchTileWithFragmentRange( point,firstFragment,lastFragment,this);
//						prefetched  = true;
//						tile.carryingProbability = node.probability; // carry it to the cache
//						Main.cache.cacheTileWithFragmentRange(tile,firstFragment,lastFragment);
//						availThinkTime -= fragmentsToBePrefetched.size();
//						//that many were cached
//						Main.cache.fetchTile(index, this);
//						
//						
//					}
//					else {// cachedLOD > LOD
//						//System.err.println("hhhmmm");
//						//that many were needed and we had even more in the cache
//						/*for (int i=1; i<LOD; i++){
//							Main.cache.fetchFragmentOfTile(i,  new Point(key.y,key.x), this);
//						}*/
//					}
//				}
//				else { //Tile doesn't exist and it is partially needed from Database
//					fragmentsToBePrefetched = CachedTile.getMissingFragmentIdsTillLOD(0, LOD);
//					if (FRAGMENT){
//						Vector<Integer> fragmentsAfterThinkingTime = new Vector<Integer>();
//						for (int i=1; i<=Math.min(availThinkTime,fragmentsToBePrefetched.size()); i++){
//							fragmentsAfterThinkingTime.add(fragmentsToBePrefetched.get(i-1));
//						}
//						fragmentsToBePrefetched = fragmentsAfterThinkingTime;
//					}
//					int fragmCount = fragmentsToBePrefetched.size();
//					int firstFragment = fragmentsToBePrefetched.get(0);
//					int lastFragment = fragmentsToBePrefetched.get(fragmCount-1);
//					Util.debug(availThinkTime+"-"+fragmentsToBePrefetched.size());
//					if (availThinkTime-fragmentsToBePrefetched.size()<0){
//						this.thinkTime = availThinkTime;
//						return ;
//					}
//					Tile tile = Main.db.fetchTileWithFragmentRange( point,firstFragment,lastFragment,this);
//					prefetched  = true;
//					tile.carryingProbability = node.probability; // carry it to the cache
//					Main.cache.cacheTileWithFragmentRange(tile,firstFragment,lastFragment);
//					availThinkTime -= fragmentsToBePrefetched.size();
//				}
//			}
			
			
			
			
			
			
			
			
			
			
			
			if (prefetched){
			
				Util.debug("prefetched!"+point);	
			}
			
			
			
			
		}
		Util.debug("prefetched "+count);
		this.thinkTime = availThinkTime;
	}
	
	
	
	
	
	
	public void viewportFetch(){
		Viewport viewport = this.viewport;
		Point upperLeft = viewport.upperLeft;
		//Point lowerRight = viewport.lowerRight;
		int fragmentsNeeded  = FRAGMENTS_PER_TILE;
		if (FRAGMENT){
			fragmentsNeeded = UserMove.currentZoomLevel;
		}
		
		for (int y=upperLeft.y; y<=upperLeft.y+(viewport.height-1); y++){
			for (int x=upperLeft.x; x<=upperLeft.x+(viewport.width-1); x++){
				Point index = Database.points(y,x);
				if (!Main.cache.tileExists(index)){
					System.out.println("bika1");
					Tile tile = Main.db.getTileWithFragmentRange(index,1,fragmentsNeeded, this);
					tile.carryingProbability = 1000000.0d; // carry it to the cache
					Main.cache.cacheTileWithFragmentRange(tile,this.point, 1, fragmentsNeeded);
					this.cacheMissesDuringFetch+=fragmentsNeeded;
					
					this.run.totalCacheMissesDuringFetch+=fragmentsNeeded;
				}
				else {
					CachedTile cachedPartialTile = Main.cache.getTile(index);
					int cachedLOD = cachedPartialTile.getCachedFragmentsNum();
					//what was actually fetched to be viewed
					Main.cache.getTile(index);
					if (cachedLOD<fragmentsNeeded){
						Tile tile = Main.db.getTileWithFragmentRange( index,cachedLOD+1,fragmentsNeeded,this);
						tile.carryingProbability = 1000000.0d; // carry it to the cache
						Main.cache.cacheTileWithFragmentRange(tile, this.point,cachedLOD+1,fragmentsNeeded);
					}
					
					int cached = cachedPartialTile.getCachedFragmentsNum();
					int misses = (fragmentsNeeded-cached);
					Util.debug("cached "+cached);
					Util.debug("fragmentsNeeded "+fragmentsNeeded);
					Util.debug("misses "+misses);
					if (misses > 0){
						this.cacheMissesDuringFetch += misses;
						this.run.totalCacheMissesDuringFetch += misses;
					}
					if (cached <= fragmentsNeeded){
						this.cacheHitsDuringFetch += cached;
						this.run.totalCacheHitsDuringFetch += cached;
					}
					else {
						this.cacheHitsDuringFetch += fragmentsNeeded;
						this.run.totalCacheHitsDuringFetch += fragmentsNeeded;
					}
				}
				
				Util.debug("cache misses "+this.cacheMissesDuringFetch);
				Util.debug("cache hits "+this.cacheHitsDuringFetch);
				Util.debug("cache misses on run "+this.run.totalCacheMissesDuringFetch);
				Util.debug("cache hits on run "+this.run.totalCacheHitsDuringFetch);
		
			}
		}
		
//		for (int y=upperLeft.y; y<=upperLeft.y+(viewport.height-1); y++){
//			for (int x=upperLeft.x; x<=upperLeft.x+(viewport.width-1); x++){
//				Point index = new Point(y,x);
//				//if tile doesn't exist in cache
//				if (!Main.cache.tileExists(index)){
//					// full Database Fetch
//					Tile tile = Main.db.fetchTile(index, this);
//					tile.carryingProbability = 1.0d; // carry it to the cache
//					Main.cache.cacheTileWithFragmentRange(tile, 1, fragmentsNeeded);
//					this.cacheMissesDuringFetch+=fragmentsNeeded;
//					this.run.totalCacheMissesDuringFetch+=fragmentsNeeded;
//				}
//				//if tile partially exists request missing fragments
//				
//				else if(Main.cache.tileExistsAndNotFull(index)){
//					CachedTile cachedPartialTile = Main.cache.getTile(index);
//					int cachedLOD = cachedPartialTile.getCachedFragmentsNum();
//					//what was actually fetched to be viewed
//					Main.cache.fetchTile(index, this);
//					Tile tile = Main.db.fetchTileWithFragmentRange( index,cachedLOD+1,fragmentsNeeded,this);
//					tile.carryingProbability = 1.0d; // carry it to the cache
//					int cached = cachedPartialTile.getCachedFragmentsNum();
//					int misses = (fragmentsNeeded-cached);
//					Main.cache.cacheTileWithFragmentRange(tile,cachedLOD+1,fragmentsNeeded);
//					this.cacheMissesDuringFetch += misses;
//					this.run.totalCacheMissesDuringFetch += misses;
//					this.cacheHitsDuringFetch += cached;
//					this.run.totalCacheHitsDuringFetch += cached;
//				}
//				else { // tileExistsAndFull == true
//					Main.cache.fetchTile(index, this);
//					this.cacheHitsDuringFetch+=fragmentsNeeded;
//					this.run.totalCacheHitsDuringFetch+=fragmentsNeeded;
//				}
//				
//			}
//		}
	}
	
	
//	public String toString(){
//		//return this.viewport.upperLeft.toString();
//	}
	
	
	
	
	public UserMove(Point point,Run run,String movementType){
		this.point = point;
		this.run = run;
		this.movementType = movementType;
	}
	
	
	
	public UserMove jumpTo(Point point,Run run){
		return new UserMove(point,run,"jump");
	}
	
	
	public UserMove go(String movementType,Run run){
		Util.debug(movementType);
		if (movementType.equals("up")){
			return new UserMove(this.point.goUp(),run,movementType);
		}
		else if (movementType.equals("right")){
			return new UserMove(this.point.goRight(),run,movementType);
		}
		else if (movementType.equals("down")){
			return new UserMove(this.point.goDown(),run,movementType);
		}
		else if (movementType.equals("left")){
			return new UserMove(this.point.goLeft(),run,movementType);
		}
		else if (movementType.equals("zoomin")){
			currentZoomLevel+=1;
			if (currentZoomLevel>FRAGMENTS_PER_TILE){
				currentZoomLevel = FRAGMENTS_PER_TILE;
			}
			return new UserMove(this.point,run,movementType);
		}
		else if (movementType.equals("zoomout")){
			currentZoomLevel-=1;
			
			if (currentZoomLevel<1){
				currentZoomLevel = 1;
			}
			return new UserMove(this.point,run,movementType);
		}
		else if(movementType.equals("zoomjump")){
			currentZoomLevel = Util.randInt(1,FRAGMENTS_PER_TILE);
			return new UserMove(this.point,run,movementType);
		}
		else if (movementType.equals("stay")){
			return new UserMove(this.point,run,movementType);
		}
		else if (movementType.equals("ignore")){
			return new UserMove(this.point,run,movementType);
		}
		else {
			return new UserMove(this.point,run,"ignore");
		}
	}
	
	
	
	
	/*
	public void action() throws Exception{
		
		Workload.writeMove(this);
		Main.cache.updateAllTileLikelihoods(newMove);
		//if (debug){
			
		//}
		boolean isTerminal = newMove.upperLeft.x == DATABASE_WIDTH-1 && newMove.upperLeft.y == DATABASE_WIDTH-1;
		if (isTerminal){
			Event.sendEvent(new StopAll(Main.startTime));
		}
		//if (debug){
			System.out.println("UserMove Event"+this.newMove.upperLeft);
		//}

		Event.sendEvent(new Fetch(this.newMove));
		//if (PREFETCH){
		//	Event.sendEvent(new Prefetch(this.newMove));
		//}
		System.exit(3);
		
		Monitor.userMove();
		
		//Main.db.setViewport(newMove);
		
	}*/
	
}
