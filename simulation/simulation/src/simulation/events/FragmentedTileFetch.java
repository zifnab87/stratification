package simulation.events;

import java.util.Vector;

import simulation.Fragment;
import simulation.Main;
import simulation.Point;
import simulation.Tile;
import simulation.monitor.Monitor;
import static simulation.Config.debug;

public class FragmentedTileFetch extends Event {

	
	
	Point fragmentedPointToFetch;

	public FragmentedTileFetch(Point fragmentedPointToFetch){
		this.fragmentedPointToFetch = fragmentedPointToFetch;
	}
	@Override
	public void action() {
		Vector<Integer> fragmentNums = this.fragmentedPointToFetch.fragmentNums;
		double likelihood = fragmentedPointToFetch.carriedLikeliood;

		for (int fragmentNum: fragmentNums){
			Fragment fragment;
			if (Main.cache.tileExists(this.fragmentedPointToFetch)){
				Tile tile = Main.cache.getTile(this.fragmentedPointToFetch);
				if (!tile.containsFragment(fragmentNum)){ //if this fragment is not in the cache
					if (debug){
						System.out.println(this+" ("+fragmentNum+")");
					}
					fragment = Main.db.getFragmentOfTile(fragmentNum, this.fragmentedPointToFetch);
					Monitor.databaseFragmentFetch();
					Main.cache.cacheFragment(fragment, this.fragmentedPointToFetch,likelihood);
				}
				else { // if fragment is in cache this will not happen normally (as the request was made because the fragment is in cache)
					Monitor.cacheFragmentFetch();
				}
			}
			else {
				if (debug){
					System.out.println(this+" ("+fragmentNum+")");
				}
				fragment = Main.db.getFragmentOfTile(fragmentNum, this.fragmentedPointToFetch);
				Monitor.databaseFragmentFetch();
				Main.cache.cacheFragment(fragment, this.fragmentedPointToFetch,likelihood);
			}
		}
		
		
		
		/*for ( int fragmentNum: fragmentNums){
			
			
			
			
			Tile tile = Main.cache.getTile(this.fragmentedPointToFetch);
			Fragment fragment = null;
			if (tile !=null){
				fragment = tile.getFragment(fragmentNum);
				if (fragment==null ){
					if (debug){
						System.out.println(this+" ("+fragmentNum+")");
					}
					fragment = Main.db.getFragmentOfTile(fragmentNum, this.fragmentedPointToFetch);
					Monitor.databaseFragmentFetch();
					Main.cache.cacheFragment(fragment, this.fragmentedPointToFetch);
				}
			}
			else {
				
				Monitor.cacheFragmentFetch();
				if (debug){
					//System.out.println("Fragment fetched from Cache! (Fetch)");
				}
			}
			
			//render fragment TOD
			
		}*/

	}
	
	public String toString(){
		return "FragmentedTileFetch Event for point "+this.fragmentedPointToFetch.toString()+" "+this.fragmentedPointToFetch.fragmentNums;	
	}

}
