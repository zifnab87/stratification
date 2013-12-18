package simulation.predictor;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import simulation.Point;
import simulation.Tile;

public class Node {
	public Node up;
	public Node down;
	public Node left;
	public Node right;
	
	public Node parent;
	public int y;
	public int x;
	public int hash;
	
	public double probability;
	public int fragmentsNeeded;
	
	public int waveNum; //for the prediction
	
	public Point point;
	
	Vector<Node> vec = new Vector<Node>();
	
	public Node(int y,int x){ //carrier
		this.y = y;
		this.x = x;
		this.point = new Point(y,x);
	}
	
	public Node(Node parent,int y, int x,double probability){
		this.parent = parent;
		this.y = y;
		this.x = x;
		this.point = new Point(y,x);
		this.hash = this.point.hashCode();

		this.probability = probability;
		vec.add(this.up);
		vec.add(this.down);
		vec.add(this.left);
		vec.add(this.right);
		
	}
	public boolean equals(Object o){
		return ((Node)o).hashCode() == this.hashCode();
	}
	
	public int hashCode(){
		return ((this.y+"-"+this.x).hashCode());
	}
	
	public String toString(){
		return "(y="+y+",x="+x+",wave="+waveNum+",probability="+probability+",fragmentsNeeded="+fragmentsNeeded+")";
	}
	
	//sort with ascending probability
	public static void sortDesc(List<Node> itemLocationList) {
	    Collections.sort(itemLocationList, new Comparator<Node>() {
	        @Override
	        public int compare(Node o1, Node o2) {
	            if (o1.probability > o2.probability){
	            	return -1;
	            }
	            else if(o1.probability < o2.probability){
	            	return 1;
	            }
	            else {
	            	return 0;
	            }
	        }           
	    });
	}
	
}
