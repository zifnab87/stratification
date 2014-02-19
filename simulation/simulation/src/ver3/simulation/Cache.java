package ver3.simulation;

import java.util.TreeSet;

import ver3.simulation.CachedTile;

public class Cache {
	private TreeSet<CachedTile> queue = new TreeSet<CachedTile>(CachedTile.importanceComparator);
}
