package simulation;

import java.util.Comparator;

public class CachedTile extends Tile {

	public CachedTile(Point point) {
		super(point);
		// TODO Auto-generated constructor stub
	}
	
	public int lod;
	public double likelihood;
	
	public void setLikelihood(Viewport viewport){
		this.likelihood = Predictor.calculateLikelihood(this.point, viewport);
		this.lod = Predictor.calculateLOD(this.point, viewport);
	}
	public void setLikelihood(double likelihood){
		this.likelihood = likelihood;
		this.lod = Predictor.likelihoodToLOD(likelihood);
	}
	
	public void setLOD(Viewport viewport){
		this.setLikelihood(viewport);
	}
	
	public static Comparator<Tile> likelihoodComparator = new Comparator<Tile>(){
		@Override
		public int compare(Tile t1, Tile t2) {
            return (int) (t1.likelihood - t2.likelihood);
        }
	};

}
