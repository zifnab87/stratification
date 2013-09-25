package simulation;

public class Viewport {
	public int width;
	public int height;
	
	public Point upperLeft;
	public Point upperRight;
	public Point lowerLeft;
	public Point lowerRight;
	
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
	}
	
	
}
