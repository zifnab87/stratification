


public class BrownHeap {
	
	public static Node constructHeap() {
		Node node = insert(null,11);
		System.out.println("isValid"+isValid(node));
		node = insert(node,12);
		System.out.println("isValid"+isValid(node));
		node = insert(node,13);
		System.out.println("isValid"+isValid(node));
		node = insert(node,32);
		System.out.println("isValid"+isValid(node));
		node = insert(node,3);
		System.out.println("isValid"+isValid(node));
		node = insert(node,2);
		System.out.println("isValid"+isValid(node));
		node = insert(node,-2);
		System.out.println("isHeap"+isHeap(node));
		
		
		node = insert(node,1);
		System.out.println("isHeap"+isHeap(node));
		node = insert(node,23);
		System.out.println("isHeap"+isHeap(node));
		node = insert(node,0);
		System.out.println("isHeap"+isHeap(node));
		node = insert(node,-1);
		System.out.println("isHeap"+isHeap(node));
		node = insert(node,-3);
		System.out.println("isHeap"+isHeap(node));
		node = removeMin(node);
		System.out.println("isHeap"+isHeap(node));
		node = removeMin(node);
		System.out.println("isHeap"+isHeap(node));
		node = removeMin(node);
		System.out.println("isHeap"+isHeap(node));
		return node;
	}
	
	public static Node insert(final Node node,int number){
		//System.out.println("insert "+number);
		if (node == null){
			return new Node(number,null,null);
		}
		else {
			return insertHelper(node,number);
		}
	}
	
public static Node insertHelper(final Node node, int number){
		
		if (node.right == null){ //node.right == null
			if (node.value < number){
				return swapChildren(new Node(node.value,node.left,new Node(number,null,null)));
			}
			else {
				return swapChildren(new Node(number,node.left,new Node(node.value,null,null)));
			}
		}
		else {//if (node.right!=null){
			if (node.value < number){
				return swapChildren(new Node(node.value,node.left,insertHelper(node.right,number)));
			}
			else {
				return swapChildren(new Node(number,node.left,insertHelper(node.right,node.value)));
			}
		}
	
	
	}

	
	public static Node swapChildren(final Node node){
		//System.out.println("swapChildren of "+node.value);
		return new Node(node.value,node.right,node.left);
	}
	
	
	public static Node percolateDown(final Node node){
		if (node == null){
			return null;
		}
		else{
			return percolateDownHelper(node);
		}
	}
	
	public static Node percolateDownHelper(final Node node){
		if (node.left!=null){
			if (smallerChild(node.left, node.right).equals("left") && node.left.value < node.value){
				return new Node(node.left.value, percolateDownHelper(new Node(node.value,node.left.left,node.left.right)),node.right);
			}
			else if (smallerChild(node.left, node.right).equals("right") && node.right.value < node.value){
				return new Node(node.right.value,node.left,percolateDownHelper(new Node(node.value,node.right.left,node.right.right)));
			}
			else {
				return node;
			}
		}
		else {
			return node;
		}
	}
	
		
	public static String smallerChild(final Node left, final Node right){
		if (right==null || left.value < right.value){
			return "left";
		}
		else {
			return "right";
		}
	}
	
	public static Node removeMin(final Node node){
		
		if (node==null || node!=null && node.left == null && node.right == null ){
			return null;
		}
		else {
			// finds bottomleftchild - finds and removes it and places it at root and perculates down the root
			Node bottomLeftChild = findBottomLeftChild(node);
			return percolateDown(replaceRootValueWithNodeValue(removeBottomLeftChild(node),findBottomLeftChild(node)));
		}
		
	}
	
	public static Node findBottomLeftChild(final Node node){
		if (node!=null && node.left!=null && node.left.left==null){
			return node.left;
		}
		else {
			return findBottomLeftChild(node.left);
		}
	}
	
	
	public static Node removeBottomLeftChild(final Node node){
		if (node!=null && node.left!=null && node.left.left==null){
			return swapChildren(new Node(node.value,null,node.right));
		}
		else {
			return swapChildren(new Node(node.value,removeBottomLeftChild(node.left),node.right));
		}
	}
	
	public static Node replaceRootValueWithNodeValue(final Node root,final Node node){
		return new Node(node.value,root.left,root.right);
	}
	
	
	public static void print(final Node node){
		print_helper(node,0);
	}
	
	public static void print_helper(final Node node,int rootdistance){
		String str="";

		if (node!=null){
			if (rootdistance==0){
				System.out.println(node+"(0)");
			}
			if (node.left!=null){
				str="";
				for (int i=0;i<rootdistance; i++){
					str+="  ";
				}
				str+="|_"+node.left+"("+(rootdistance+1)+")";
				System.out.println(str);
				print_helper(node.left,rootdistance+1);
			}
			else if (node.left==null && node.right!=null) {
				str="";
				for (int i=0;i<rootdistance; i++){
					str+="  ";
				}
				str+="|_"+"?";
				System.out.println(str);
			}
			
			if (node.right!=null){
				str="";
				for (int i=0;i<rootdistance; i++){
					str+="  ";
				}
				str+="|_"+node.right+"("+(rootdistance+1)+")";
				System.out.println(str);
				print_helper(node.right,rootdistance+1);
				
			}
			else if (node.left!=null && node.right==null) {
				str="";
				for (int i=0;i<rootdistance; i++){
					str+="  ";
				}
				str+="|_"+"?";
				System.out.println(str);
			}		
		}
		else {
			System.out.println("empty");
		}
	}
	public static boolean isValid(final Node node){
		return isHeap(node) && isBrownHeapBalanced(node);
	}
	
	
	public static boolean isHeap(final Node node){
		
		if (node == null){
			return true;
		}
		else {
			return  isParentSmallerThanChildren(node) && isHeap(node.left) && isHeap(node.right);
		}
	}
	
	public static boolean isParentSmallerThanChildren(final Node node){
		if (node!=null){
			if (((node.left!=null && node.left.value > node.value) || node.left == null) && ((node.right!=null && node.right.value > node.value) || node.right == null)){
				return true;
			}
			else {
				return false;
			}
		}
		else {
			return true;
		}
	}
	
	public static boolean isBrownHeapBalanced(final Node node){
		if (node==null){
			return true;
		}
		else {
			int nodesLeftSubHeap = findNumberOfNodes(node.left);
			int nodesRightSubHeap = findNumberOfNodes(node.right);
			boolean isBrownHeap = (nodesLeftSubHeap == nodesRightSubHeap) || (nodesLeftSubHeap-1 == nodesRightSubHeap);
			return isBrownHeap && isBrownHeapBalanced(node.left) && isBrownHeapBalanced(node.right);
		}
	}
	
	public static int findNumberOfNodes(final Node node){
		if (node==null){
			return 0;
		}
		else {
			return 1+findNumberOfNodes(node.left)+findNumberOfNodes(node.right);
		}
	}
	

	
}
