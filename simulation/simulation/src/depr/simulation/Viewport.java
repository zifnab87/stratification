package depr.simulation;


public class Viewport {
	public int width;
	public int height;
	
	public Point upperLeft;
	public Point upperRight;
	public Point lowerLeft;
	public Point lowerRight;
	public Point center;
	public String resultOfMovement;
	
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

	public Viewport(int height, int width, Point upperLeft,String resultOfMovement){
		this.height = height;
		this.width = width;
		this.upperLeft = upperLeft;

		this.upperRight = new Point(upperLeft.y, upperLeft.x + width-1);
		this.lowerRight = new Point(upperRight.y + height-1, upperRight.x);
		this.lowerLeft = new Point(upperLeft.y + height-1, upperLeft.x);
		//previousCenter = this.center;
		this.center = new Point(Integer.valueOf((int) (upperLeft.y+height/2.0)),Integer.valueOf((int) (upperLeft.x+width/2.0)) );
		
		this.resultOfMovement = resultOfMovement;
		//System.out.println("upperLeft:"+upperLeft);
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
		Point newUpperLeft = new Point(this.upperLeft.y,this.upperLeft.x-1);
		//System.out.println("left");
		return new Viewport(this.height,this.width,newUpperLeft,"left");
	}
	
	public Viewport goRight(){
		Point newUpperLeft = new Point(this.upperLeft.y,this.upperLeft.x+1);
		//System.out.println("right");
		return new Viewport(this.height,this.width,newUpperLeft,"right");
	}
	
	public Viewport goDown(){
		Point newUpperLeft = new Point(this.upperLeft.y+1,this.upperLeft.x);
		//System.out.println("down");
		return new Viewport(this.height,this.width,newUpperLeft,"down");
	}

	public Viewport goUp(){
		Point newUpperLeft = new Point(this.upperLeft.y-1,this.upperLeft.x);
		//System.out.println("try" + newUpperLeft);
		//System.out.println("up");
		return new Viewport(this.height,this.width,newUpperLeft,"up");
	}
	
	

}
