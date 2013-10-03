package simulation;

import java.util.Vector;

public class Point {
	public int x;
	public int y;
	public Vector<Integer> fragmentNums;
	public Point(int y, int x){
		this.y = y;
		this.x = x;
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
	
	
	
}
