package simulation.events;

import java.util.Vector;

import simulation.Fragment;
import simulation.Main;
import simulation.Point;
import simulation.Tile;

public class FragmentedTileFetch extends Event {

	
	
	Point fragmentedPointToFetch;
	public FragmentedTileFetch(Point fragmentedPointToFetch){
		this.fragmentedPointToFetch = fragmentedPointToFetch;
	}
	@Override
	public void action() {
		Vector<Integer> fragmentNums = this.fragmentedPointToFetch.fragmentNums;
		for ( int fragmentNum: fragmentNums){
			//TODO replace with Main.cache.getFragmentOfTile( )
			Tile tile = Main.cache.getTile(this.fragmentedPointToFetch);
			Fragment fragment = null;
			if (tile !=null){
				fragment = tile.getFragment(fragmentNum);
			}
			if (fragment==null ){
				System.out.println(this+" ("+fragmentNum+")");
				fragment = Main.db.getFragmentOfTile(fragmentNum, this.fragmentedPointToFetch);
				
				Main.cache.addFragment(fragment, this.fragmentedPointToFetch);
			}
			else {
				System.out.println("Fragment cached! (Fetch)");
			}
			
			//render fragment TOD
			
		}

	}
	
	public String toString(){
		return "FragmentedTileFetch Event for point "+this.fragmentedPointToFetch.toString()+" "+this.fragmentedPointToFetch.fragmentNums;	
	}

}
