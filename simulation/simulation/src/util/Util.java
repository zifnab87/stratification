package util;

import java.util.Vector;

import static sync.simulation.Config.DEBUG;

public class Util {
	public static double average(Vector<Integer> vec){
		double sum=0;
		for (int i=0; i<vec.size(); i++){
			sum += vec.get(i);
		}
		return sum/(1.0*vec.size());
	}
	
	public static double variance(Vector<Integer> vec){
		double avg = average(vec);
		double sum=0;
		for (int i=0; i<vec.size(); i++){
			sum += Math.pow(vec.get(i)-avg,2);
		}
		return sum/((1.0*vec.size())+1);
	}
	
	public static void debug(String text,boolean force){
		if (DEBUG || force){
			System.out.println(text);
		}
	}
	
	public static void debug(String text){
		debug(text,false);
	}
}
