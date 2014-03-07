package sync.simulation.regions;

import java.util.Comparator;

import simulation.Tile;
import sync.simulation.Point;
import sync.simulation.events.UserMove;
import static sync.simulation.Config.COVERAGE;
import static sync.simulation.Config.DISTANCE_WEIGHT_FACTOR;
import static sync.simulation.Config.IMPORTANCE_WEIGHT_FACTOR;
import static sync.simulation.Config.JUMP_WEIGHT_FACTOR;
import static sync.simulation.Config.FRAGMENTS_PER_TILE;
import static sync.simulation.Config.COVERAGE;
public class TileOverall {
	
	public final Point point;
	public double totalImportance;
	public int jumpToCounts;
	public double[] visitResolutions = new double[FRAGMENTS_PER_TILE+1];
	public int visitsCounts;
	
	
	
	public double distance;
	
	
	
	public void updateImportance(Point current){
		this.distance = Point.distance(this.point, current);
		this.totalImportance = (1/(Point.distance(this.point, current)+0.001))*(UserStudiesCombined.tiles[this.point.y][this.point.x].visitsCounts +
				UserStudiesCombined.tiles[this.point.y][this.point.x].jumpToCounts);
	}
	
	public int howManyFragments(){
		int i=1;
		while (COVERAGE>=this.visitResolutions[i] && i<FRAGMENTS_PER_TILE){
			i++;
			if (COVERAGE<this.visitResolutions[i]){
				return i-1;
				
			}
			
			
		}
		return FRAGMENTS_PER_TILE;
	}
	
	
	/*public double jumpImportance;
	public double zoomImportance;
	public double totalImportance;*/
	
	
	public TileOverall(Point point,double[] visitResolutions, int jumpToCounts,int visitsCounts){
		this.point = point;
		for (int i=0; i<this.visitResolutions.length;i++){
			this.visitResolutions[i] = visitResolutions[i];
		}
		this.jumpToCounts = jumpToCounts;
		this.visitsCounts = visitsCounts;
	}
	
	public TileOverall(Point point){
		this.point = point;
	}
	

	
	
	public static Comparator<TileOverall> comparator = new Comparator<TileOverall>(){
		@Override
		public int compare(TileOverall t1, TileOverall t2) {
			
			
			
			if (t1.totalImportance < t2.totalImportance ){
		    	return 1;
		    }
		    else if (t1.totalImportance > t2.totalImportance  ){
		    	return -1;
		    }
		    else if (t1.totalImportance == t2.totalImportance &&
		    		 t1.point.id < t2.point.id ) {
		    	return 1;
		    }
		    else if (t1.totalImportance == t2.totalImportance &&
		    		 t1.point.id > t2.point.id) {
		    	return -1;
		    }
		    else {
		    	return 0;
		    }
			/*
			if (t1.visitsCounts < t2.visitsCounts ){
		    	return -1;
		    }
		    else if (t1.visitsCounts > t2.visitsCounts  ){
		    	return 1;
		    }
		    else if (t1.visitsCounts == t2.visitsCounts &&
		    		 t1.distance < t2.distance ) {
		    	return 1;
		    }
		    else if (t1.visitsCounts == t2.visitsCounts &&
		    		 t1.distance > t2.distance) {
		    	return -1;
		    }
		    else if (t1.visitsCounts == t2.visitsCounts &&
		    		t1.distance == t2.distance && t1.point.id < t2.point.id)
		    {
		    	return -1;
		    }
		    else if (t1.visitsCounts == t2.visitsCounts &&
		    		t1.distance == t2.distance && t1.point.id > t2.point.id)
		    {
		    	return 1;
		    }
		    else {
		    	return 0;
		    }
			*/
			
			//probability and distance
	
		}
	};
	
}
