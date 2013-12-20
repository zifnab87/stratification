package simulation;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Vector;

import java.sql.Connection;

import simulation.events.UserMove;

import simulation.Point;
import simulation.Tile;
import simulation.Viewport;
import static simulation.Config.FRAGMENTS_PER_TILE;
import static simulation.Config.FRAGMENT_SIZE;
import static simulation.Config.DATABASE_WIDTH;;
public class Database {
	//public Map<Integer,Tile> tiles = new HashMap<Integer, Tile>();
	public Viewport viewport;
	
	
	
	
	public Database(){
		
	}
	
	//initial viewport
	public void setViewport(Viewport viewport){
		this.viewport = viewport;
	}
	
	
	public void clearCache(){
		Connection conn = null;
		try {
		    // The newInstance() call is a work around for some
		    // broken Java implementations
		    Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
		    // handle the error
		}
		 try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/stratification?" +
                     "user=root");
			
			Statement stmt = conn.createStatement();
			 stmt = conn.createStatement();
			 stmt.executeUpdate("RESET QUERY CACHE");
			conn.close();
	 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void init(int numTiles){
		/*for (int i=0; i<numTiles; i++){
			Tile tile = Tile.randomizer();
			putTile(tile);
		}*/
		Connection conn = null;
		try {
		    // The newInstance() call is a work around for some
		    // broken Java implementations
		    Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
		    // handle the error
		}
		 try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/stratification?" +
                     "user=root");
			
			/*for (int y=0; y<DATABASE_WIDTH; y++){
				for (int x=0; x<DATABASE_WIDTH; x++){
					for (int i=1; i<=FRAGMENTS_PER_TILE; i++){
						String data = "[";
						for (int pixel=0; pixel<FRAGMENT_SIZE; pixel++){
							int red = new Random().nextInt(255);
							int green = new Random().nextInt(255);
							int blue = new Random().nextInt(255);
							data += "["+red+","+green+","+blue+"],";
						}
						data +="]";
						Statement stmt = conn.createStatement();
						 stmt = conn.createStatement();
						 stmt.executeUpdate("INSERT INTO fragment " + "VALUES ("+y+","+x+","+i+",'"+data+"')");
					}
				}
			}*/
			 
			
			
		
			
			 conn.close();
			 
			 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
		 
		/* for (int i=0; i<numTiles; i++){
				Tile tile = Tile.randomizer();
				putTile(tile);
		}*/
		 
		 
	}
	
	
	public boolean tileExists(Point index){
		if (index.y >= 0 && index.y< DATABASE_WIDTH && index.x >= 0 && index.x < DATABASE_WIDTH){
			return true;
		}
		else {
			return false;
		}
	}
	
	public Tile fetchTile(Point index,UserMove userMove){
		
		userMove.cacheMisses+=FRAGMENTS_PER_TILE;
		UserMove.totalCacheMisses+=FRAGMENTS_PER_TILE;
		return getTile(index);
	}
	
	private Tile getTile(Point index){
		return getTileWithFragmentRange(index, 1, FRAGMENTS_PER_TILE);
	}
	
	public Tile fetchFragmentOfTile(Point index,int fragmentNumber,UserMove caller){
		caller.cacheMisses+=1;
		UserMove.totalCacheMisses+=1;
		return getFragmentOfTile(index, fragmentNumber);
	}

	
	
	private Tile getFragmentOfTile(Point index,int fragmentNumber){
		
		return getTileWithFragmentRange(index, fragmentNumber, fragmentNumber);
	}
	
	
	private Tile getTileWithFragmentRange(Point index,int firstFragment,int lastFragment){
		double start = System.currentTimeMillis();
		Connection conn = null;
		ResultSet results = null;
		Tile tile = null;
		try {
		    // The newInstance() call is a work around for some
		    // broken Java implementations
		    Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
		    // handle the error
		}
		 try {
			conn = DriverManager.getConnection("jdbc:mysql://localhost/stratification?" +
                     "user=root");
			
			Statement stmt = conn.createStatement();
			stmt = conn.createStatement();
			results = stmt.executeQuery("SELECT * FROM fragment WHERE y="+index.y+" AND x="+index.x+" AND fragment_num BETWEEN "+firstFragment+" AND "+lastFragment);
	
			String[] totalData = new String[FRAGMENTS_PER_TILE];
			while (results.next()){
				int fragment_num = results.getInt("fragment_num");
				totalData[fragment_num-1]=results.getString("data");
			}
			tile = new Tile(index,totalData);
			double end = System.currentTimeMillis() - start;
			System.out.println(end+" msecs");
			conn.close();
	 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tile;
	}
	
	public Tile fetchTileWithFragmentRange(Point index,int firstFragment,int lastFragment,UserMove userMove){
		//fragments fetched
		int num = lastFragment-firstFragment+1;
		for (int i=0; i<num; i++){
			userMove.cacheMisses+=1;
			UserMove.totalCacheMisses+=1;
		}
		return getTileWithFragmentRange(index, firstFragment, lastFragment);
	}
	
	
	
	
	

	
}
