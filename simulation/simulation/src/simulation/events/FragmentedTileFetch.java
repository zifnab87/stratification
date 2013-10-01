package simulation.events;

import java.util.Vector;

import simulation.Fragment;
import simulation.Main;
import simulation.Point;

public class FragmentedTileFetch extends Event {

	
	
	Point fragmentedPointToFetch;
	public FragmentedTileFetch(Point fragmentedPointToFetch){
		this.fragmentedPointToFetch = fragmentedPointToFetch;
	}
	@Override
	public void action() {
		Vector<Integer> fragmentNums = this.fragmentedPointToFetch.fragmentNums;
		for ( int fragmentNum: fragmentNums){
			Fragment fragment = Main.db.getFragmentOfTile(fragmentNum, this.fragmentedPointToFetch);
			//render fragment TOD
			Main.cache.addFragment(fragment, this.fragmentedPointToFetch);
		}
	

	}

}
