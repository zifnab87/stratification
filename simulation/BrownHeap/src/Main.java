import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;
import java.util.Vector;


//http://oeis.org/A145787
//http://oeis.org/A019567

public class Main {
	public static HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
	
	public static int[] initArray(int N){
		int[] array = new int[N];
		for (int i=0; i<array.length; i++){
			array[i]=i;
		}
		return array;
	}
	
	public static void shuffleArray(int[] ar)
	  {
	    Random rnd = new Random();
	    for (int i = ar.length - 1; i > 0; i--)
	    {
	      int index = rnd.nextInt(i + 1);
	      // Simple swap
	      int a = ar[index];
	      ar[index] = ar[i];
	      ar[i] = a;
	    }
	  }
	
	
	
	public static int probPappouv2(int n){
		int mmax=100000;
		int m;
		for (m=1; m<mmax;m++){
	        if (((Math.pow(2,m)-1)%(4*n+1)==0 || (Math.pow(2,m)+1)%(4*n+1)==0)){
	            System.out.println("TELIKO~~"+(Math.pow(2, m)-1)+"/"+(4*n+1));
	            System.out.println("TELIKO~~"+(Math.pow(2, m)+1)+"/"+(4*n+1));
	            System.out.println("TELIKO~~"+m);
	        	return m ;
	        }
	        System.out.println((Math.pow(2, m)-1)+"/"+(4*n+1));
            System.out.println((Math.pow(2, m)+1)+"/"+(4*n+1));
	        System.out.println("~~"+m);
		}
		return m;
	}
	
	
	
	
	public static void main(String args[]){
		//Node root = BrownHeap.constructHeap();
		//BrownHeap.print(root);
	
		
		
		for (int i=1; i<500; i++){
			probPappou(i);
		}
		
//		for (int heapSize=0; heapSize<=10000; heapSize++){
//			for (int i=0; i<heapSize; i++){
//				Node node = null;
//				int[] array = initArray(heapSize);
//				shuffleArray(array);
//				node = BrownHeap.insert(node, array[i]);
//				boolean isValid = BrownHeap.isValid(node);
//				System.out.println("AfterInsert "+ isValid);
//				if (!isValid){
//					System.out.println("PROBLEM HOUSTON");
//				}
//				if (Math.random()>0.7d){
//					node = BrownHeap.removeMin(node);
//					isValid = BrownHeap.isValid(node);
//					System.out.println("AfterRemove "+ isValid);
//					if (!isValid){
//						System.out.println("PROBLEM HOUSTON");
//					}
//				}
//			}
//		}
//		boolean isBalanced = BrownHeap.isBrownHeapBalanced(new Node(
//										0,
//										new Node(
//												1,
//												new Node(2,null,null),
//												null
//												),
//										new Node (
//											3,
//											new Node(4,new Node(5,null,null),null),
//											null
//										)
//									)
//						   );
			
//		System.out.println("!!!"+isBalanced);
		/*for (int i= 0 ; i<=10000; i=i+1){
			probPappou(i);
		}
		Iterator<Integer> iter = map.keySet().iterator();
		int max =0;
		int maxi = 0;
		while(iter.hasNext()){
			int iterNum = iter.next();
			int count = map.get(iterNum);
			if (count>max){
				max = count;
				maxi = iterNum;
			}
			//System.out.println("Number of Iterations:"+iterNum+" Count:"+count);
		}
		System.out.println("Max Count"+max+" iterations Num:"+maxi +"percentage"+(max/map.size()*1.0d)*100+"%");*/
		
	}
	
	



	
	
	
	public static void probPappou(int numOfCards){
		ArrayDeque<Integer> cards = new ArrayDeque<Integer>();
		int[] cardArray = new int[numOfCards];
		
		for (int i=0; i<numOfCards; i++){
			cards.addLast(i+1);
			cardArray[i] = i+1;
		}
		int count = 0;
		ArrayDeque<Integer> prevIteration = cards;
		ArrayDeque<Integer> newIteration = cards;
		do{
			boolean flag = false;
			count++;
			prevIteration = newIteration;
		//	System.out.println(newIteration);
			newIteration = new ArrayDeque<Integer>();
			
			for (int i=0; i<numOfCards; i++){
				if (!flag){
					newIteration.addLast(prevIteration.removeFirst());
				}
				else {
					newIteration.addFirst(prevIteration.removeFirst());
				}
				flag = !flag;
			}
			
			
		}
		
		
		while(!isSorted(newIteration,cardArray));
		
		//System.out.println(numOfCards+","+(numOfCards+1)+":"+count);
			boolean printed=false;
			boolean output=false;
			//if (numOfCards%2==0){
			
			/*int n = numOfCards/2;
			if (isPrime(4*n+1)){
				//also archimedes spiral
				System.out.println(2*n+"AAA");
			}*/
			
				//if (output){
					
					if (numOfCards==count){
						//http://oeis.org/A163777 Archimedes Spiral
						System.out.println("sameNumOf Cards with iterations:"+numOfCards+":("+count+")");
						printed = true;
					}
					if (isTriangularNum(numOfCards)){
						//System.out.println("Triangular Number:"+numOfCards+":("+count+")");
						printed = true;
					}
					if (isPowerOfTwo(numOfCards)){
						//System.out.println("Power of two Number:"+numOfCards+":("+count+")");
						printed = true;
					}
					if (isPrime(numOfCards)){
						//System.out.println("Prime Number:"+numOfCards+":("+count+")");
						printed = true;
					}
					if (isPerfectSquare(numOfCards)){
						//System.out.println("Is Perfect Square:"+numOfCards+":("+count+")");
						printed = true;
					}
					if (!printed){
						//System.out.println("Regular Number:"+numOfCards+":("+count+")");
					}
					
					
				//}
				//if (!printed){
					//System.out.println(numOfCards+":("+count+")");
				//}
			//}
			if(!map.containsKey(count)){
				map.put(count, 1);
			}
			else {
				map.put(count,map.get(count)+1);
			}
		
	}
	
	public static boolean isPrime(int n) {
		if (n==2){
	    	return true;
	    }
	    //check if n is a multiple of 2
	    if (n%2==0) return false;
	    //if not, then just check the odds
	    
	    for(int i=3;i*i<=n;i+=2) {
	        if(n%i==0)
	            return false;
	    }
	    return true;
	}
	
	public static boolean isPowerOfTwo(long number) {
        if ((number & -number) == number) {
            return true;
        }
        return false;
    }

	public static boolean isTriangularNum(long n){
		return isPerfectSquare(8*n+1);
	}
	
	public final static boolean isPerfectSquare(long n)
	{
	  if (n < 0)
	    return false;

	  long tst = (long)(Math.sqrt(n) + 0.5);
	  return tst*tst == n;
	}
	
	public static boolean isSorted(ArrayDeque<Integer> cards,int[] cardsArray){
		int size = cards.size();
		ArrayDeque<Integer> toCheck = cards.clone();
		boolean IsSorted = true;
		for (int i=0; i<size; i++){
			if (toCheck.removeFirst()!=cardsArray[i]){
				IsSorted = false;
				break;
			}
		}
		return IsSorted;
	}
}