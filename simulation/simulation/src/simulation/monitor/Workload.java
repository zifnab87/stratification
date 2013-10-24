package simulation.monitor;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.Vector;

import static simulation.Config.WORKLOAD_FILE;

import simulation.events.UserMove;

public class Workload {
	
	public static void init(){
		/*try {
			File file = new File(WORKLOAD_FILE);
			if (file.exists()){
				Files.delete(new File(WORKLOAD_FILE).toPath());
			}
		} catch (IOException e) {
			e.printStackTrace();
			// TODO Auto-generated catch block

		}*/
	}
	
	
	public static void writeMove(UserMove userMove) throws FileNotFoundException, UnsupportedEncodingException{
		String movementType = userMove.movementType;
		DataOutputStream writer = null;
		try {
		    writer = new DataOutputStream(new FileOutputStream(new File(WORKLOAD_FILE),true));
		    		 
		    if (movementType != null){
		    	writer.writeUTF(movementType);

		    }
		} catch (IOException ex){
		  // report
		} finally {
		   try {writer.close();} catch (Exception ex) {}
		}
	}
	
	public static Vector<String> readMoves(){
		DataInputStream reader;
		try {
			reader = new DataInputStream(new FileInputStream(WORKLOAD_FILE));
			Vector<String> moves = new Vector<String>();
			while(true){		
				String move;
				try {
					move = reader.readUTF();
					moves.add(move);
				} catch (EOFException e) {
					//e.printStackTrace();
					return moves;
				} catch (IOException ioe){
					//ioe.printStackTrace();
					return null;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			// TODO Auto-generated catch block
			return null;
		}
		
		

	}
}