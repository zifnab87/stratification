package sync.simulation.regions;

import java.util.Comparator;

import sync.simulation.Point;
import sync.simulation.events.UserMove;
import static sync.simulation.Config.IMPORTANCE_METRIC;

import static sync.simulation.Config.FRAGMENTS_PER_TILE;
import static sync.simulation.Config.DATABASE_WIDTH;
public class TileOverall {
	
	public final Point point;
	public double totalImportance;
	public static double distNormalizer = Math.sqrt((DATABASE_WIDTH-1)*(DATABASE_WIDTH-1));//diagonal
	//public int jumpToCounts;
	//public double[] visitResolutions = new double[FRAGMENTS_PER_TILE+1];
	//public int visitsCounts;
	public static double importanceNormalizer;
	
	
	public double distance;
	
	
	
	public void updateImportance(Point current){
		this.distance = Point.distance(this.point, current);
		double distNormalized = Point.distance(this.point, current)/(1.0d*distNormalizer);
		double popularity = UserStudiesCombined.popularities[this.point.y][this.point.x];
		syncNormalizers();
		if (current.equals(this.point)){
			this.totalImportance = 1000000;
			
		}
		else {
			double totalImportance = 0;
			if (IMPORTANCE_METRIC==-1){
				totalImportance = (1-distNormalized);	
				//System.out.println("~~~current="+current+" distance = "+this.distance+" distNormalized="+distNormalized+" importance"+ this.totalImportance +"vs "+ 1/distNormalized +" distNormalizer="+distNormalizer);
			}
			else if (IMPORTANCE_METRIC==1){
				totalImportance = popularity;
			}
			else if (IMPORTANCE_METRIC==0){
				totalImportance = popularity + (1-distNormalized) + (1-distNormalized)*popularity;	
			}
			else if (IMPORTANCE_METRIC>0){ 
				totalImportance = Math.abs(IMPORTANCE_METRIC)*popularity + (1-distNormalized) + (1-distNormalized)*popularity;	
			}
			else if (IMPORTANCE_METRIC<0){ 
				totalImportance = popularity + Math.abs(IMPORTANCE_METRIC)*(1-distNormalized) + (1-distNormalized)*popularity;	
			}
			this.totalImportance = totalImportance/importanceNormalizer;
			//System.out.println("current="+current+" distance = "+this.distance+" distNormalized="+distNormalized+" importance"+ this.totalImportance +"vs "+ 1/distNormalized +" distNormalizer="+distNormalizer);
		}
		
	}
	
	public int howManyFragments(Point current){
		syncNormalizers();
		if(this.point.equals(current)){
			return UserMove.currentZoomLevel;
		}
		
		int fragments = (int)Math.floor((this.totalImportance)*FRAGMENTS_PER_TILE);
		
		if (fragments<1){
			return 1;
		}
		else if(fragments>FRAGMENTS_PER_TILE){
			return FRAGMENTS_PER_TILE;
		}
		else {
			return fragments;
		}
		//return UserMove.currentZoomLevel;
	}
	
	/*public TileOverall(Point point,double[] visitResolutions, int jumpToCounts,int visitsCounts){
		this.point = point;
		for (int i=0; i<this.visitResolutions.length;i++){
			this.visitResolutions[i] = visitResolutions[i];
		}
		this.jumpToCounts = jumpToCounts;
		this.visitsCounts = visitsCounts;
	}*/
	
	public TileOverall(Point point){
		this.point = point;
		syncNormalizers();
	}
	
	public void syncNormalizers(){
		if (IMPORTANCE_METRIC==-1){
			importanceNormalizer = 1.0d; //maximum value when distance is 0
		}
		else if (IMPORTANCE_METRIC==1){
			importanceNormalizer = 1.0d; 
		}
		else if (IMPORTANCE_METRIC==0){
			importanceNormalizer = 3.0d;
		}
		else if (IMPORTANCE_METRIC>0){ 
			importanceNormalizer = Math.abs(IMPORTANCE_METRIC) + 2.0d;
		}
		else if (IMPORTANCE_METRIC<0){ 
			importanceNormalizer = Math.abs(IMPORTANCE_METRIC) + 2.0d;	//distance = 1 (min), popularity 1.0
			
		}
	}
	
	
	public String toString(){
		String str;
		str = "OverallTile("+this.point.y+","+this.point.x+", fragmentsToBeNeed="+howManyFragments(null)+", Total Importance="+this.totalImportance+",Distance="+this.distance+")";
		return str;
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
