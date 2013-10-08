package simulation.events;

import java.util.Vector;

import simulation.Fragment;
import simulation.Main;
import simulation.Point;
import simulation.Tile;

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
				System.out.println(this+" ("+fragmentNum+")");
				fragment = Main.db.getFragmentOfTile(fragmentNum, this.fragmentedPointToPrefetch);
				//if fragment is in the database (e.g not out of viewport)
				if (fragment!=null){
					Main.cache.addFragment(fragment, this.fragmentedPointToPrefetch);
				}
				else {
					System.out.println("Point out of bound of db");
				}
			}
			else {
				System.out.println("Fragment cached! (Prefetch)");
			}
			
			
		}

	}
	
	public String toString(){
		return "FragmentedTilePrefetch Event for point "+this.fragmentedPointToPrefetch+" "+this.fragmentedPointToPrefetch.fragmentNums;	
	}
}
