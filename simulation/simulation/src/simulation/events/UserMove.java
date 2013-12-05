package simulation.events;

import static simulation.Config.FRAGMENT;
import static simulation.Config.FRAGMENTS_PER_TILE;
import static simulation.Config.DATABASE_WIDTH;
import static simulation.Config.debug;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import simulation.Cache;
import simulation.Database;
import simulation.Fragment;
import simulation.Main;
import simulation.Point;
import simulation.Tile;
import simulation.Viewport;
import simulation.monitor.Workload;
import simulation.predictor.Node;
//import static simulation.Config.PREFETCH;
import simulation.predictor.Predictor;
import simulation.predictor.Tuple;

public class UserMove {
	public Point  upperLeft;
	public Viewport viewport;
	public String movementType;
	
	public int cacheHits = 0;
	public int cacheMisses = 0;
	public static int totalCacheMisses = 0;
	public static int totalCacheHits = 0;
	
	
	public UserMove(Viewport viewport){
		this.upperLeft = viewport.upperLeft;
		this.viewport = viewport;
		this.movementType = viewport.resultOfMovement;
	}
	
	public void write(){
		try {
			Workload.writeMove(this);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void prefetch(HashMap <Node, Tuple<Double,Integer>> map){
		Iterator<Node> keys = map.keySet().iterator();
		while (keys.hasNext()){
			Node key = keys.next();
			Tuple<Double,Integer> tuple = map.get(key);
			double likelihood = tuple.x;
			int LOD = tuple.y;
			int index = key.hashCode();
			Point point = new Point(key.y,key.x);
			Vector<Integer> fragmentsNeeded;
			//index.LOD = LOD;
			//System.out.println(LOD);
			//if tile doesn't exist in cache
			//if (index.equals(new Point(5,2)) ){
				//System.out.println("!!!!!"+index.carriedLikeliood+" "+index);
				//System.out.println(Main.cache.tileExistsAndNotFull(index));
				//System.out.println(Main.cache.tileExists(index));
				//System.out.println(Main.cache.getTile(index).getFragmentNumber());
			//}
			if (LOD == FRAGMENTS_PER_TILE) {// the tile is needed to be full
				
				if (Main.cache.tileExistsAndFull(index)){
					Main.cache.fetchTile(index, this);
				}
				else if(Main.cache.tileExistsAndNotFull(index)){
					//find what's missing
					Tile cachedPartialTile = Main.cache.getTile(index);
					int cachedLOD = cachedPartialTile.lod;
					fragmentsNeeded = Tile.getMissingFragmentIdsTillFull(cachedLOD);
					
					for (int i=0; i<fragmentsNeeded.size(); i++){
						Fragment fragment = Main.db.fetchFragmentOfTile(fragmentsNeeded.get(i), point, this);
						Main.cache.cacheFragment(fragment, point,likelihood);
					}
					
					//that many were cached
					for (int i=1; i<cachedLOD; i++){
						Main.cache.fetchFragmentOfTile(i, point, this);
					}
					

				}
				else { //tile doesn't exist in Cache
					// full Database Fetch
					Tile tile = Main.db.fetchTile(point, this);
					Main.cache.cacheFullTile(tile);
				}
			}
			else if (LOD>0 && LOD<FRAGMENTS_PER_TILE){ //the tile doesn't need to be full
				if (Main.cache.tileExists(index)){
					Tile cachedPartialTile = Main.cache.getTile(index);
					int cachedLOD = cachedPartialTile.lod;
					if (cachedLOD <= LOD){
						fragmentsNeeded = Tile.getMissingFragmentIdsTillLOD(cachedLOD,LOD);
						for (int i=0; i<fragmentsNeeded.size(); i++){
							Fragment fragment = Main.db.fetchFragmentOfTile(fragmentsNeeded.get(i), point, this);
							Main.cache.cacheFragment(fragment, point, likelihood);
						}
						//that many were cached
						for (int i=1; i<cachedLOD; i++){
							Main.cache.fetchFragmentOfTile(i,  new Point(key.y,key.x), this);
						}
					}
					else {// cachedLOD > LOD
						//that many were needed and we had even more in the cache
						for (int i=1; i<LOD; i++){
							Main.cache.fetchFragmentOfTile(i,  new Point(key.y,key.x), this);
						}
					}
				}
				else { //Tile doesn't exist and it is partially needed from Database
					fragmentsNeeded = Tile.getMissingFragmentIdsTillLOD(0, LOD);
					for (int i=0; i<fragmentsNeeded.size(); i++){
						Fragment fragment = Main.db.fetchFragmentOfTile(fragmentsNeeded.get(i), new Point(key.y,key.x), this);
						Main.cache.cacheFragment(fragment, point,likelihood);
					}
				}
			}
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
			
		}
	}
	
	
	
	public void viewportFetch(){
		Viewport viewport = this.viewport;
		Point upperLeft = viewport.upperLeft;
		Point lowerRight = viewport.lowerRight;
		Vector<Point> vec = new Vector<Point>();
		Vector<Integer> fragmentsNeeded = null;
		for (int y=upperLeft.y; y<=lowerRight.y; y++){
			for (int x=upperLeft.x; x<=lowerRight.x; x++){
				Point index = new Point(y,x);
				//if tile doesn't exist in cache
				if (!Main.cache.tileExists(index)){
					// full Database Fetch
					Tile tileFetched = Main.db.fetchTile(index, this);
					Main.cache.cacheFullTile(tileFetched);
				}
				//if tile partially exists request missing fragments
				
				else if(Main.cache.tileExistsAndNotFull(index)){
					Tile cachedPartialTile = Main.cache.getTile(index);
					int cachedLOD = cachedPartialTile.lod;
					//fragmentsNeeded = Tile.getMissingFragmentIdsTillFull(cachedLOD);
					//index.setFragmentNums(fragmentsNeeded);
					//vec.add(index);
					for (int i=1; i<=cachedLOD; i++){
						Main.cache.fetchFragmentOfTile(i, index, this);
					}
					for (int i=cachedLOD+1; i<=FRAGMENTS_PER_TILE; i++){
						Fragment fragment = Main.db.fetchFragmentOfTile(i, index, this);
						Main.cache.cacheFragment(fragment, cachedPartialTile.point,cachedPartialTile.likelihood);
					}
				}
				else { // tileExistsAndFull == true
					Main.cache.fetchTile(index, this);
				}
				
			}
		}
	}
	
	
	public String toString(){
		return this.viewport.upperLeft.toString();
	}
	
	
	public UserMove nextMove(String move){
		if (move.equals("up")){
			return this.goUp();
		}
		else if (move.equals("right")){
			return this.goRight();
		}
		else if (move.equals("down")){
			return this.goDown();
		}
		else if (move.equals("left")){
			return this.goLeft();
		}
		else {
			
			return null;
		}
	}
	
	
	public UserMove goLeft(){
		Point newUpperLeft = new Point(this.upperLeft.y,this.upperLeft.x-1);
		//System.out.println("left");
		return new UserMove(new Viewport(this.viewport.height,this.viewport.width,newUpperLeft,"left"));
	}
	
	public UserMove goRight(){
		Point newUpperLeft = new Point(this.upperLeft.y,this.upperLeft.x+1);
		//System.out.println("right");
		return new UserMove(new Viewport(this.viewport.height,this.viewport.width,newUpperLeft,"right"));
	}
	
	public UserMove goDown(){
		Point newUpperLeft = new Point(this.upperLeft.y+1,this.upperLeft.x);
		//System.out.println("down");
		return new UserMove(new Viewport(this.viewport.height,this.viewport.width,newUpperLeft,"down"));
	}

	public UserMove goUp(){
		Point newUpperLeft = new Point(this.upperLeft.y-1,this.upperLeft.x);
		//System.out.println("try" + newUpperLeft);
		//System.out.println("up");
		return new UserMove(new Viewport(this.viewport.height,this.viewport.width,newUpperLeft,"up"));
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
