import java.util.ArrayList;
import java.util.List;

public class Main {

    
    
    public static void main(String[] args){
    	Node a = new Node("a");
    	Node b = new Node("b",a);
    	Node c = new Node("c",b);
    	//eye.addChild(iris);
    	Node d = new Node("d",a);
    	Node f = new Node("f",d);
    	Node e = new Node("e",c);
    	System.out.println(a);
    	e.addChild(f);
    	System.out.println(a);
    	
    	
    }
    

    
    
}