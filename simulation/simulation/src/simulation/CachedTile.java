package simulation;

public class CachedTile extends Tile {

	
	public int cachedFragments; //number of fragments it has cached
	

	public CachedTile(Point point,String[] data) {
		super(point);
		// TODO Auto-generated constructor stub
		this.cachedFragments = data.length;
	}
	
	
	public void addFragment(Tile tile,int fragmNumber){
		fragments.remove(fragmNumber);
		this.lod = this.getFragmentNumber();
	}
	
	
	public void removeFragment(int fragmNumber){
		fragments.remove(fragmNumber);
		this.lod = this.getFragmentNumber();
	}
	
	
	public boolean containsFragment(int fragmNumber){
		return fragments.containsKey(fragmNumber);
	}
	
	
}
