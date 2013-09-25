package simulation;

public class Point {
	public int x;
	public int y;
	public Point(int y, int x){
		this.y = y;
		this.x = x;
	}
	public int hashCode(){
		return ((this.y+"-"+this.x).hashCode());
	}
}
