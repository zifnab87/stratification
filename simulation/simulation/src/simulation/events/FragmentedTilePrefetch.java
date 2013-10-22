package simulation.events;

import java.util.Vector;

import simulation.Fragment;
import simulation.Main;
import simulation.Point;
import simulation.Tile;
import simulation.monitor.Monitor;
import static simulation.Config.debug;

public class FragmentedTilePrefetch extends Event {

	Point fragmentedPointToPrefetch;
	public FragmentedTilePrefetch(Point fragmentedPointToPrefetch){
		this.fragmentedPointToPrefetch = fragmentedPointToPrefetch;
	}
	@Override
	public void action() {

		Vector<Integer> fragmentNums = this.fragmentedPointToPrefetch.fragmentNums;
		for ( int fragmentNum: fragmentNums){
			Tile tile = Main.cache.getTile(this.fragmentedPointToPrefetch);
			Fragment fragment = null; 
			if (tile !=null){
				fragment = tile.getFragment(fragmentNum);
			}
			if (fragment==null ){
				if (debug){
					System.out.println(this+" ("+fragmentNum+")");
				}
				fragment = Main.db.getFragmentOfTile(fragmentNum, this.fragmentedPointToPrefetch);
				//if fragment is in the database (e.g not out of viewport)
				Monitor.databaseFragmentFetch();
				if (fragment!=null){
					Main.cache.addFragment(fragment, this.fragmentedPointToPrefetch);
				}
				else {
					if (debug){
						System.out.println("Point out of bound of db");
					}
				}
			}
			else {
				Monitor.cacheFragmentFetch();
				if (debug){
					//System.out.println("Fragment fetched from Cache! (Prefetch)");
				}
			}
			
			
		}

	}
	
	public String toString(){
		return "FragmentedTilePrefetch Event for point "+this.fragmentedPointToPrefetch+" "+this.fragmentedPointToPrefetch.fragmentNums;	
	}
}
