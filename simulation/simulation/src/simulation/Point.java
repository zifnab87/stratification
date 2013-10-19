package simulation;

import java.util.Vector;
import static simulation.Config.DATABASE_WIDTH;

public class Point {
	public int x;
	public int y;
	public Vector<Integer> fragmentNums;
	public Point(int y, int x){
		this.y = y;
		this.x = x;
		this.validate();
	}
	public int hashCode(){
		return ((this.y+"-"+this.x).hashCode());
	}
	
	public void setFragmentNums(Vector<Integer> fragmentNums){
		this.fragmentNums = fragmentNums;
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
	
}
