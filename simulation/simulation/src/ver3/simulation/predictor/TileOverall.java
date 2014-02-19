package ver3.simulation.predictor;

import java.util.Comparator;

import ver3.simulation.Point;
import ver3.simulation.events.UserMove;
import static ver3.simulation.Config.DISTANCE_WEIGHT_FACTOR;
import static ver3.simulation.Config.IMPORTANCE_WEIGHT_FACTOR;
import static ver3.simulation.Config.JUMP_WEIGHT_FACTOR;
public class TileOverall {
	public double jumpImportance;
	public double zoomImportance;
	public double totalImportance;
	public final Point point;
	
	public TileOverall(Point point){
		this.point = point;
	}
	
	public void updateImportance(UserMove currentPosition){
		
		double sum = DISTANCE_WEIGHT_FACTOR + IMPORTANCE_WEIGHT_FACTOR + JUMP_WEIGHT_FACTOR;
		double distance = Point.distance(this.point, currentPosition.upperLeft);
		
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
