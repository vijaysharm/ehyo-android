package com.vijaysharma.ehyo;

import java.util.LinkedHashMap;
import java.util.Map;

public class Toptal {
	public static void main(String[] args) {
//		int[] A = {1, 4, 3, 3, 1, 2};
//		int[] B = {6, 4, 4, 6};
//		System.out.println( "Lowest Index: " + solution(A) );
		
//		System.out.println("Lowest p: " + solution2(102));
		
		int[] A = {1, 3, 2, 5, 4, 4, 6, 3, 2};
		System.out.println("Moves: " + solution3(A));
	}
	
	public static int solution3( int[] A ) {
		int direction = 0;
		
		int current_x = 0;
		int current_y = 0;
		double magnitude = 0;
	
		Map<Integer, Double> dir = new LinkedHashMap<Integer, Double>();
		
		for ( int index = 0; index < A.length; index++ ) {
			int move = A[index];
			int prev_x = current_x;
			int prev_y = current_y;
			
			if ( direction == 0 ) {
				current_y += move;
			} else if ( direction == 1 ) {
				current_x += move;
			} else if ( direction == 2 ) {
				current_y -= move;
			} else {
				current_x -= move;
			}

			magnitude = compute_speed(current_x, current_y);
			Double storedDirection = dir.get(direction);
			if ( storedDirection == null ) {
				dir.put(direction, magnitude);
			} else {
				if ( storedDirection < magnitude )
					return move;
				else
					dir.put(direction, magnitude);
			}
			
			System.out.println("(" + prev_x + "," + prev_y + ") -> (" + current_x + "," + current_y + ") " + (index + 1) + "th move, " +  move + " step " + direction_string(direction) + " speed: " + magnitude);
			direction = next_direction(direction);
		}
		
		return 0;
	}
	
	private static double compute_speed(int current_x, int current_y) {
		int x2 = current_x * current_x;
		int y2 = current_y * current_y;
		int sum = x2 + y2;
		return Math.sqrt(sum);
	}

	private static int next_direction(int direction) {
		return ( direction + 1 ) % 4;
	}
	
	private static String direction_string(int direction) {
		if ( direction == 0 ) return "North";
		if ( direction == 1 ) return "East";
		if ( direction == 2 ) return "South";
		if ( direction == 3 ) return "West";
		return null;
	}
	public static int solution2( int A ) {
		String string = Integer.toBinaryString(A);
		int maxP = string.length() / 2;
		
		int pVal = -1;
		for ( int p = maxP; p > 0; p-- ) {
			if( tryP(string, p, string.length() - p) )
				pVal = p;
		}
		
		return pVal;
	}
	
	private static boolean tryP(String string, int p, int k) {
		for ( int index = 0; index < k; index++ ) {
			int i = index + p;
			
			if ( string.charAt(index) != string.charAt(i) )
				return false;
		}
		
		return true;
	}
	
	public static int solution1(int[] A) {
		Map<Integer, Integer> table = new LinkedHashMap<Integer, Integer>();
		
		for ( int index = 0; index < A.length; index++ ) {
			Integer value = A[index];
			Integer set = table.get(value) == null ? 1 : -1; 
			table.put(value, set);
		}

		for ( Map.Entry<Integer, Integer> entry : table.entrySet() ) {
			if (entry.getValue() == 1)
				return entry.getKey();
		}
		
		return -1;
	}
}