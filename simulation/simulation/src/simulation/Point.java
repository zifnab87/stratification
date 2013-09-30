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
	
	
	
}
