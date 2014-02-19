package ver3.simulation;


import java.util.Vector;




import static sync.simulation.Config.DATABASE_WIDTH;

public class Point {
	public final int x;
	public final int y;
	public final int id;
	public Point(int y, int x){
		if (x < 0) {
			this.x = 0;
		}
		else if(x > DATABASE_WIDTH-1){
			this.x = DATABASE_WIDTH-1;
		}
		else {
			this.x = 0;
		}
		if (y < 0) {
			this.y = 0;
		}
		else if(y > DATABASE_WIDTH-1){
			this.y = DATABASE_WIDTH-1;
		}
		else {
			this.y =0;
		}
		this.id = (this.y+"-"+this.x).hashCode();
		
	}
	
	public Point(int y, int x,boolean dummy){
		this.y = y;
		this.x = x;
		this.id = (this.y+"-"+this.x).hashCode();
		
	}
	public int hashCode(){
		return id;
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
	
	
	public static double distance(Point a, Point b){
		double dist = Math.sqrt(Math.pow(a.y-b.y,2)+Math.pow(a.x-b.x,2));
		return dist;
	}
	
	/*public Node createNode(){
		return new Node(this.y,this.x);
	}*/
}
