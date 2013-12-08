package simulation;

import java.util.Vector;

import simulation.events.UserMove;
import simulation.predictor.Node;

import static simulation.Config.DATABASE_WIDTH;

public class Point {
	public int x;
	public int y;
	
	//public int LOD = 0;
	public Point(int y, int x){
		this.y = y;
		this.x = x;
		this.validate();
	}
	public int hashCode(){
		return ((this.y+"-"+this.x).hashCode());
	}
	
	
	public String toString(){
		return "("+y+","+x+")";
	}
	
	public boolean equals(Object o){
		if (this.hashCode()==((Point)o).hashCode()){
			return true;
		}
		else {
			return false;
		}
	}
	
	public void validate(){
		if (this.x < 0) {
			this.x = 0;
		}
		else if(this.x > DATABASE_WIDTH-1){
			this.x = DATABASE_WIDTH-1;
		}
		if (this.y < 0) {
			this.y = 0;
		}
		else if(this.y > DATABASE_WIDTH-1){
			this.y = DATABASE_WIDTH-1;
		}
	}
	
	public Object clone(){
		return new Point(this.y,this.x);
		
	}
	
	public Point goLeft(){
		return new Point(this.y,this.x-1);
	}
	
	public Point goRight(){
		return new Point(this.y,this.x+1);
	}
	
	public Point goDown(){
		return new Point(this.y+1,this.x);
	}

	public Point goUp(){
		return new Point(this.y-1,this.x);
	}
	
	
	public Node createNode(){
		return new Node(this.y,this.x);
	}
}
