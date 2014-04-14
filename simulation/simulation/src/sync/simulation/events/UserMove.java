package sync.simulation.events;

import static sync.simulation.Config.FRAGMENT;
import static sync.simulation.Config.FRAGMENTS_PER_TILE;

import static sync.simulation.Config.USER_THINK_DISTR;


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
import sync.simulation.regions.UserStudiesCombined;
import util.Util;
import static sync.simulation.Config.THINK_TIME;
import static sync.simulation.Config.FRAGMENTS_PER_TILE;
import static sync.simulation.Config.THINK_TIME;

public class UserMove {
	
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
	
	
	public void prefetch(JumpRegion region,Point current){
		/*if (toPrefetch.size()==0){
			return;
		}*/
		double start = System.currentTimeMillis();
		TreeSet<TileOverall> toPrefetch = region.tree(current);
		Main.updateStatisticsAndCache(current);
		
		Util.debug("Worst Tile"+ Main.cache.getWorst(current));
		Util.debug("Cache before prefetch@@"+Main.cache);
		Util.debug("Prefetch Queue"+toPrefetch);
		Util.debug("Prefetch Queue Size"+toPrefetch.size());
		Iterator<TileOverall> iter = toPrefetch.iterator();
		int availThinkTime = thinkTime;
		//System.out.println(availThinkTime);
		int count = 0;
		while (iter.hasNext() && availThinkTime>0){
			
			boolean prefetched  = false;
			TileOverall tileStatistic = iter.next();
			//Main.cache.updateImportances(current);
			Main.updateStatisticsAndCache(current);
			CachedTile worstTile = Main.cache.getWorst(current);
			//tileStatistic.updateImportance(current);
			if(tileStatistic.totalImportance < worstTile.totalImportance){
				break;
			}
			Point point = tileStatistic.point;
			//System.out.println("Prefetch Point"+point+UserStudiesCombined.tiles[point.y][point.x].totalImportance);
			int index = point.hashCode();
			int fragmentsNeeded = FRAGMENTS_PER_TILE;
			if (FRAGMENT){
				fragmentsNeeded = tileStatistic.howManyFragments(current);
			}
			else {
				fragmentsNeeded = FRAGMENTS_PER_TILE;
			}
		
			
			
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
//			if (availThinkTime<0){
//			System.out.println(availThinkTime);
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
					System.out.println("bika1  "+y+" "+x+" "+Main.cache.queueFind(index.hashCode()));
					System.err.println(Main.cache);
					Tile tile = Main.db.getTileWithFragmentRange(index,1,fragmentsNeeded, this);
					//tile.carryingProbability = 1000000.0d; // carry it to the cache
					Main.cache.cacheTileWithFragmentRange(tile,this.point, 1, fragmentsNeeded);
					this.cacheMissesDuringFetch+=fragmentsNeeded;
					
					this.run.totalCacheMissesDuringFetch+=fragmentsNeeded;
				}
				else {
					CachedTile cachedPartialTile = Main.cache.getTile(index);
					int cachedLOD = cachedPartialTile.getCachedFragmentsNum();
					//what was actually fetched to be viewed
					//Main.cache.getTile(index);
					if (cachedLOD<fragmentsNeeded){
						Tile tile = Main.db.getTileWithFragmentRange( index,cachedLOD+1,fragmentsNeeded,this);
						//tile.carryingProbability = 1000000.0d; // carry it to the cache
						Main.cache.cacheTileWithFragmentRange(tile, this.point,cachedLOD+1,fragmentsNeeded);
					}
					//if (cachedPartialTile.point.equals(upperLeft)){	
					//	cachedPartialTile.carryingProbability = 1000000.0d;
					//}
					
					int cached = cachedPartialTile.getCachedFragmentsNum();
					int misses = (fragmentsNeeded-cached);
					Util.debug("cached "+cached,true);
					Util.debug("fragmentsNeeded "+fragmentsNeeded);
					Util.debug("misses "+misses,true);
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
		
	}

	
	
	
	public UserMove(Point point,Run run,String movementType){
		this.point = point;
		this.run = run;
		this.movementType = movementType;
	}
	
	
	
	public UserMove jumpTo(Point point,Run run){
		return new UserMove(point,run,"jump");
	}
	
	
	public UserMove go(String movementType,Run run,Point current){
		Util.debug(movementType);
		if (movementType.equals("up")){
			Point point = this.point.goUp();
			if (point.equals(current)){
				return new UserMove(current,run,"stay");
			}
			else {
				return new UserMove(point,run,movementType);
			}
		}
		else if (movementType.equals("right")){
			Point point = this.point.goRight();
			if (point.equals(current)){
				return new UserMove(current,run,"stay");
			}
			else {
				return new UserMove(point,run,movementType);
			}
		}
		else if (movementType.equals("down")){
			Point point = this.point.goDown();
			if (point.equals(current)){
				return new UserMove(current,run,"stay");
			}
			else {
				return new UserMove(point,run,movementType);
			}
		}
		else if (movementType.equals("left")){
			Point point = this.point.goLeft();
			if (point.equals(current)){
				return new UserMove(current,run,"stay");
			}
			else {
				return new UserMove(point,run,movementType);
			}
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
	
}
