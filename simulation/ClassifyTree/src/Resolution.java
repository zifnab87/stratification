import java.util.HashMap;
import java.util.Vector;


public class Resolution<T> {
	
	Vector<Node> vec = new Vector<Node>();
	int numConflicts;
	
	public Resolution(Node start, Vector<Node> resolutions, Node end,int numConflicts){
		vec.add(start);
		vec.addAll(resolutions);
		vec.add(end);
		this.numConflicts = numConflicts;
	}
	
	
	public boolean resolve(){
		if (vec.size()!=this.numConflicts+2){
			return false;
		}
		else {
			Node start = null;
			Node end = null; 
			for(int i=0; i<vec.size(); i++){
				if (i<vec.size()-1){
					start = vec.get(i);
					end = vec.get(i+1);
					start.addChildPlain(end);
					
				}
				
			}
			
			//end.fixSteps();
			
			return true;
		}
	}
	
}
