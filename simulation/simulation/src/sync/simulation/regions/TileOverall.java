package sync.simulation.regions;

import java.util.Comparator;

import sync.simulation.Point;
import sync.simulation.events.UserMove;
import static sync.simulation.Config.DISTANCE_WEIGHT_FACTOR;
import static sync.simulation.Config.IMPORTANCE_WEIGHT_FACTOR;
import static sync.simulation.Config.JUMP_WEIGHT_FACTOR;
import static sync.simulation.Config.FRAGMENTS_PER_TILE;
public class TileOverall {
	
	public final Point point;
	public int jumpToCounts;
	public double[] visitResolutions = new double[FRAGMENTS_PER_TILE+1];
	public int visitsCounts;
	
	
	
	
	
	
	
	
	
	public double jumpImportance;
	public double zoomImportance;
	public double totalImportance;
	
	
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
	
	public void updateImportance(UserMove currentPosition){
		
		double sum = DISTANCE_WEIGHT_FACTOR + IMPORTANCE_WEIGHT_FACTOR + JUMP_WEIGHT_FACTOR;
		double distance = Point.distance(this.point, currentPosition.point);
		
		this.totalImportance = (DISTANCE_WEIGHT_FACTOR*distance + 
			   IMPORTANCE_WEIGHT_FACTOR*this.zoomImportance +
			   JUMP_WEIGHT_FACTOR*this.jumpImportance)/sum;
	}
	
	
	public static Comparator<TileOverall> importanceComparator = new Comparator<TileOverall>(){
		@Override
		public int compare(TileOverall t1, TileOverall t2) {
			
			//probability and distance
		    if (t1.totalImportance < t2.totalImportance ){
		    	return -1;
		    }
		    else if (t1.totalImportance > t2.totalImportance  ){
		    	return 1;
		    }
		   
		    else if (t1.totalImportance == t2.totalImportance &&
		    		t1.point.id < t2.point.id)
		    {
		    	return -1;
		    }
		    else if (t1.totalImportance == t2.totalImportance &&
		    		t1.point.id > t2.point.id)
		    {
		    	return 1;
		    }
		    else {
		    	return 0;
		    }
		}
	};
	
	
	public static Comparator<TileOverall> jumpComparator = new Comparator<TileOverall>(){
		@Override
		public int compare(TileOverall t1, TileOverall t2) {
			
			//probability and distance
		    if (t1.jumpImportance < t2.jumpImportance ){
		    	return -1;
		    }
		    else if (t1.jumpImportance > t2.jumpImportance  ){
		    	return 1;
		    }
		   
		    else if (t1.jumpImportance == t2.jumpImportance &&
		    		t1.point.id < t2.point.id)
		    {
		    	return -1;
		    }
		    else if (t1.jumpImportance == t2.jumpImportance &&
		    		t1.point.id > t2.point.id)
		    {
		    	return 1;
		    }
		    else {
		    	return 0;
		    }
		}
	};
	
}
