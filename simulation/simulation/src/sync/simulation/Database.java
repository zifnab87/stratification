package sync.simulation;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Connection;
import java.util.Random;

import sync.simulation.events.UserMove;
import sync.simulation.regions.Region;

import sync.simulation.Point;
import sync.simulation.Tile;
import sync.simulation.Viewport;
import util.Util;
import static sync.simulation.Config.FRAGMENTS_PER_TILE;
import static sync.simulation.Config.FRAGMENT_SIZE;
import static sync.simulation.Config.DATABASE_WIDTH;
import static sync.simulation.Config.CONTIG_FRAGM_IN_SINGLE_QUERY;
import static sync.simulation.Config.DATABASE_WIDTH;
public class Database extends Region {
	//public Map<Integer,Tile> tiles = new HashMap<Integer, Tile>();
	public static Connection conn;
	public Viewport viewport;
	public static Point[][] points = new Point[DATABASE_WIDTH][DATABASE_WIDTH];
	public static Tile[][] tiles = new Tile[DATABASE_WIDTH][DATABASE_WIDTH];
	
	public int width = DATABASE_WIDTH;
	public int height = DATABASE_WIDTH;
	
	
//	public static String connectionStr = "jdbc:mysql://10.116.70.173:3306/stratification?" +
//        "user=root&password=password";
	
	public static String connectionStr = "jdbc:mysql://localhost:3306/stratification?" +
	        "user=root";
	
	public void close(){
		try {
			this.conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public Database(){
		
		for (int y=0; y<points.length; y++){
			for (int x=0; x<points[0].length; x++){
				points[y][x] = new Point(y,x);
				tiles[y][x] = new Tile(points[y][x]);
			}
		}
		

		this.upperLeft = points(0,0);

	}
	
	public Point randomPoint(){
		int ymin = upperLeft.y;
		int xmin = upperLeft.x;
		int ymax = ymin + (height - 1);
		int xmax = xmin + (width - 1);
		
		int yrand = Util.randInt(ymin,ymax);
		int xrand = Util.randInt(xmin,xmax);
		Point point = Database.points(yrand,xrand);
		return point;
		
	}
	
	//initial viewport
	public void setViewport(Viewport viewport){
		this.viewport = viewport;
	}
	
	
	public void clearCache(){
		//Connection conn = null;
		
		try {
		    // The newInstance() call is a work around for some
		    // broken Java implementations
		    Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
		    // handle the error
			System.err.println("clear cache");
		}
		 try {
			if (conn==null || conn.isClosed()){
				conn = DriverManager.getConnection(connectionStr);
				//System.err.println("1ELEOEEEEEEEEEEEEEEEEEEEEOSS");
			}
			Statement stmt = conn.createStatement();
			 stmt = conn.createStatement();
			 stmt.executeUpdate("RESET QUERY CACHE");
			//conn.close();
	 
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	
	public void init(){
		/*for (int i=0; i<numTiles; i++){
			Tile tile = Tile.randomizer();
			putTile(tile);
		}*/
		//Connection conn = null;
		try {
		    // The newInstance() call is a work around for some
		    // broken Java implementations
		    Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
		    // handle the error
			System.err.println("init error");
		}
		 try {
			if (conn==null || conn.isClosed()){
				conn = DriverManager.getConnection(connectionStr);
				//System.err.println("2ELEOEEEEEEEEEEEEEEEEEEEEOSS");
			}
			for (int y=0; y<DATABASE_WIDTH; y++){
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
			}
			 
			
			
		
			
			// conn.close();
			 
			 
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
	
	/*public Tile fetchTile(Point index,UserMove userMove){
		
		userMove.cacheMisses+=FRAGMENTS_PER_TILE;
		userMove.run.totalCacheMisses+=FRAGMENTS_PER_TILE;
		
		return getTile(index,userMove);
	}
	
	private Tile getTile(Point index,UserMove userMove){
		if (!CONTIG_FRAGM_IN_SINGLE_QUERY){
			boolean first = true;
			Tile firstTile = null;
			for (int i=1; i<=FRAGMENTS_PER_TILE; i++){
				Tile partialTile = getTileWithFragmentRange(index, i, i, userMove);
				if (first){
					firstTile = partialTile;
				}
				else {
					firstTile.data[i-1]= partialTile.data[i-1];
				}
				first = false;
				//System.out.println("$$$"+firstTile.dataToString());
			}
			return firstTile;
		}
		else {
			Tile tile = getTileWithFragmentRange(index, 1, FRAGMENTS_PER_TILE, userMove);
			return tile;
		}
	}*/
	
	public Tile fetchFragmentOfTile(Point index,int fragmentNumber,UserMove userMove){
		userMove.cacheMisses+=1;
		//userMove.run.totalCacheMisses+=1;
		return getFragmentOfTile(index, fragmentNumber,userMove);
	}

	
	
	private Tile getFragmentOfTile(Point index,int fragmentNumber,UserMove userMove){
		
		return getTileWithFragmentRange(index, fragmentNumber, fragmentNumber,userMove);
	}
	
	
	public Tile getTileWithFragmentRange(Point index,int firstFragment,int lastFragment,UserMove userMove){
		if (userMove!=null){
		System.out.println("userMove"+ userMove.movementType);
		}
		
		//Connection conn = null;
		ResultSet results = null;
		Tile tile = null;
		try {
		    // The newInstance() call is a work around for some
		    // broken Java implementations
		    Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (Exception ex) {
			System.err.println("gettilewithfragm error");
		    // handle the error
		}
		 try {
			if (conn==null || conn.isClosed()){
				conn = DriverManager.getConnection(connectionStr);
				//System.err.println("3ELEOEEEEEEEEEEEEEEEEEEEEOSS");
			}
			
			double start = System.nanoTime();
			Statement stmt = conn.createStatement();
			//double latency1 = (System.nanoTime() - start)/1000000;
			
			String query = "SELECT * FROM fragment WHERE y="+index.y+" AND x="+index.x+" AND fragment_num BETWEEN "+firstFragment+" AND "+lastFragment;
			//double latency2 = (System.nanoTime() - start)/1000000;
			results = stmt.executeQuery(query);
			results.setFetchSize(1);
			//double latency3 = (System.nanoTime() - start)/1000000;
			
			String[] totalData = new String[FRAGMENTS_PER_TILE];
			//int count = 0;
			while (results.next()){
				int fragment_num = results.getInt("fragment_num");
				totalData[fragment_num-1]=results.getString("data");
				//count++;
			}
			//double latency4 = (System.nanoTime() - start)/1000000;
			
			
			tile = tiles(index.y,index.x);
			tile.setData(totalData);
			//new Tile(index,totalData);
			double latency = (System.nanoTime() - start)/1000000;
			
			if (userMove!=null){ //if null don't count latencies
				userMove.latencyDuringFetch+=latency;
				userMove.run.totalLatencyDuringFetch+=latency;
			}
	 
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tile;
	}
	
//	public Tile fetchTileWithFragmentRange(Point index,int firstFragment,int lastFragment,UserMove userMove){
//		//fragments fetched
//		int num = lastFragment-firstFragment+1;
//		
//		for (int i=0; i<num; i++){
//			userMove.cacheMisses+=1;
//			userMove.run.totalCacheMisses+=1;
//		}
//		if (!CONTIG_FRAGM_IN_SINGLE_QUERY){
//			boolean first = true;
//			Tile firstTile = null;
//			for (int i=firstFragment; i<=lastFragment; i++){
//				Tile partialTile = getTileWithFragmentRange(index, i, i, userMove);
//				if (first){
//					firstTile = partialTile;
//				}
//				else {
//					firstTile.data[i-1]= partialTile.data[i-1];
//				}
//				first = false;
//				//System.out.println("$$$"+firstTile.dataToString());
//			}
//			return firstTile;
//		}
//		else {
//			return getTileWithFragmentRange(index, firstFragment, lastFragment,userMove);
//		}
//	}
	
	
	public static Point points(int y,int x){
		if (x < 0) {
			x = 0;
		}
		else if(x > DATABASE_WIDTH-1){
			x = DATABASE_WIDTH-1;
		}
		if (y < 0) {
			y = 0;
		}
		else if(y > DATABASE_WIDTH-1){
			y = DATABASE_WIDTH-1;
		}

		return points[y][x];
	}
	
	
	public static Tile tiles(int y,int x){
		if (x < 0) {
			x = 0;
		}
		else if(x > DATABASE_WIDTH-1){
			x = DATABASE_WIDTH-1;
		}
		if (y < 0) {
			y = 0;
		}
		else if(y > DATABASE_WIDTH-1){
			y = DATABASE_WIDTH-1;
		}

		return tiles[y][x];
	}
	
	
	
	

	
}
