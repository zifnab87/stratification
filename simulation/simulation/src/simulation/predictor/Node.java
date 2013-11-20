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
	
	public double likelihood;
	
	Vector<Node> vec = new Vector<Node>();
	
	public Node(Node parent,int y, int x,double likelihood){
		this.parent = parent;
		this.y = y;
		this.x = x;
		this.hash = new Point(y,x).hashCode();
		this.likelihood = likelihood;
		vec.add(this.up);
		vec.add(this.down);
		vec.add(this.left);
		vec.add(this.right);
		
	}
	
	public String toString(){
		return "("+y+","+x+")"+likelihood+"";
	}
	
	//sort with ascending likelihood
	public static void sortDesc(List<Node> itemLocationList) {
	    Collections.sort(itemLocationList, new Comparator<Node>() {
	        @Override
	        public int compare(Node o1, Node o2) {
	            if (o1.likelihood > o2.likelihood){
	            	return -1;
	            }
	            else if(o1.likelihood < o2.likelihood){
	            	return 1;
	            }
	            else {
	            	return 0;
	            }
	        }           
	    });
	}
	
}
