
public class Node {
	final public Node left;
	final public Node right;
	final public int value;
	
	public Node(int value,Node left, Node right){
		this.value = value;
		this.left = left;
		this.right = right;
	}
	
	
	public String toString(){
		String str = ""+value;
		return str;
	}
}
