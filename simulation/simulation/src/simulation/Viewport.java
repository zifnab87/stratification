package simulation;

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
	
	
}
