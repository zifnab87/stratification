package simulation.events;

import java.util.Vector;

import simulation.Fragment;
import simulation.Main;
import simulation.Point;

public class FragmentedTilePrefetch extends Event {

	Point fragmentedPointToPretch;
	public FragmentedTilePrefetch(Point fragmentedPointToPretch){
		this.fragmentedPointToPretch = fragmentedPointToPretch;
	}
	@Override
	public void action() {
		Vector<Integer> fragmentNums = this.fragmentedPointToPretch.fragmentNums;
		for ( int fragmentNum: fragmentNums){
			Fragment fragment = Main.db.getFragmentOfTile(fragmentNum, this.fragmentedPointToPretch);
			Main.cache.addFragment(fragment, this.fragmentedPointToPretch);
		}
		
		
		// TODO Auto-generated method stub

	}
}
