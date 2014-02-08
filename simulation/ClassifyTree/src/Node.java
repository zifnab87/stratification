import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;



public class Node {
    String data;
    Node parent;
    private List<Node> children;
  	static HashMap<String,Node> map = new HashMap<String,Node>(); 
    int stepsAwayFromRoot;
    
    
    public Node(String data) {
        this.data = data;
        this.children = new ArrayList<Node>();
        this.parent = null;
        map.put((String)data,this);
       
    }
    
    public Node(String data,Node parent) {
        this.data = data;
        this.children = new ArrayList<Node>();
        parent.addChild(this);
        map.put(data,this);
       
    }
    
    public String toString(){
    	
    	String children = "";
    	for(int i=0; i<this.children.size(); i++){
    		children+= this.children.get(i)+",";
    	}
    	if (children!=""){
    		return (String)this.data+"["+children+"]";
    	}
    	else {
    		return (String)this.data;
    	}
    	
    }
    
    public int fixStepsHelper(){
    	
    	if (this.parent!=null){
    		return this.parent.fixStepsHelper()+1;
    	}
    	else {
    		return 0+1;
    	}
    }
    
    public void fixSteps(){
    	this.stepsAwayFromRoot = fixStepsHelper();
    }
    
    
    public  void findConflictingSet(Node otherNode){
    	
    	Node node = this;
    	Node initialChild =  otherNode;
    	//System.out.print(node.data+",");
    	Set<String> set = new HashSet<String>();
    	set.add(node.data);
    	
    	
    	while (node.parent!=null){
    		node = node.parent;
    		if (set.contains(node.data)){
    			set.remove(node.data);
    		
    		}
    		else {
    			set.add(node.data);
    		}
    		
    		//System.out.print(node.data+",");
    	}
    	//System.out.println("----");
    	//System.out.print(otherNode.data+",");
    	set.add(otherNode.data);
    	while (otherNode.parent!=null){
    		otherNode = otherNode.parent;
    		if (set.contains(otherNode.data)){
    			set.remove(otherNode.data);
    		
    		}
    		else {
    			set.add(otherNode.data);
    		}
    		//System.out.print(otherNode.data+",");
    	}
    	
    	set.remove(initialChild.data);
    	
    	
    	System.out.println("Conflicting Set: "+set);
    	
    	Vector<Node> resolutions = new Vector<Node>();
    	resolutions.add(map.get("d"));
    	resolutions.add(map.get("e"));
    	resolutions.add(map.get("b"));
    	resolutions.add(map.get("c"));
    	Resolution res = new Resolution(initialChild,resolutions,otherNode,set.size());
    	//System.out.println(res.vec);
    	res.resolve();
    }
    public void addChildPlain(Node child){
    	this.children.add(child);
    	child.parent = this;
    	
    }
    
    
    public void addChild(Node child){
  	
    	if (child.stepsAwayFromRoot == this.stepsAwayFromRoot && child.parent!=null){
    		child.parent.children.remove(child);
    	}
    	else if (child.stepsAwayFromRoot != this.stepsAwayFromRoot && child.parent!=null){
    		System.out.println("Conflict found:");
    		findConflictingSet(child);
    		return;
    	}
    	this.children.add(child);
    	child.parent = this;
    	
    	child.fixSteps();
    	//System.out.println("~~~~~~~~~");
    
    }
}