package simulation;

import static simulation.Config.viewportHeight;
import static simulation.Config.viewportWidth;
import static simulation.Config.upperLeftStartingPoint;

public class Main {
	public static void main(String args[]){
		Database db = new Database();
		Viewport viewport = new Viewport(viewportHeight,viewportWidth,upperLeftStartingPoint);
		db.setViewport(viewport);
		db.init(625); //625
		//System.out.println("done");
		
		
	}
		
		
		
		//user moved
		//take current position
		//calculate tiles in viewport
		//fetch them 100% (if they are not available in the cache) (less % if there are partially available in cache)
		//render them
		//update LOD of the cache and make the ones just fetched have likelihood = 1 (protected)
		//remove as many as needed for the fetched that have the lowest LOD if cache is full
		//caclulate the tiles of prefetch
		//prefetch them % based in their LOD
		//remove as many as needed for the prefetched that have the lowest LOD if cache is full
		//adjust prefetching diameter and protrusion direction and length
		//evaluating latency = network + database + render, cache hit, prediction hit, average time between user moves
		//protrusion length could be based on speed of user
		
		
	
}
