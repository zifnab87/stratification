package simulation;
import static simulation.Config.DATABASE_WIDTH;

public class Viewport {
	public int width;
	public int height;
	
	public Point upperLeft;
	public Point upperRight;
	public Point lowerLeft;
	public Point lowerRight;
	public Point center;
	
	//public static Point previousCenter = null;
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public Viewport(int height, int width, Point upperLeft){
		this.height = height;
		this.width = width;
		this.upperLeft = upperLeft;
		this.upperRight = new Point(upperLeft.y, upperLeft.x + width);
		this.lowerRight = new Point(upperRight.y+height, upperRight.x);
		this.lowerLeft = new Point(upperLeft.y + height, upperLeft.x);
		//previousCenter = this.center;
		this.center = new Point(Integer.valueOf((int) (upperLeft.y+height/2.0)),Integer.valueOf((int) (upperLeft.x+width/2.0)) );
		//System.out.println(center.y+"!!!"+center.x);
	}
	
	public boolean contains(Point point){
		return contains(point.y,point.x);
	}
	
	public boolean contains(int y,int x){
		if (x>=upperLeft.x && x<=upperRight.x && y>=upperLeft.y && y<=lowerLeft.y){
			return true;
		}
		else
			return false;
	}	
	
	
	public Viewport goLeft(){
		Point newUpperLeft = new Point(this.upperLeft.y,Math.min(this.upperLeft.x-1,0));
		return new Viewport(this.height,this.width,newUpperLeft);
	}
	
	public Viewport goRight(){
		Point newUpperLeft = new Point(this.upperLeft.y,Math.max(this.upperLeft.x+1,DATABASE_WIDTH));
		return new Viewport(this.height,this.width,newUpperLeft);
	}
	
	public Viewport goDown(){
		Point newUpperLeft = new Point(Math.max(this.upperLeft.y+1,DATABASE_WIDTH),this.upperLeft.x);
		return new Viewport(this.height,this.width,newUpperLeft);
	}

	public Viewport goUp(){
		Point newUpperLeft = new Point(Math.min(this.upperLeft.y-1,0),this.upperLeft.x);
		return new Viewport(this.height,this.width,newUpperLeft);
	}
	
	

}
