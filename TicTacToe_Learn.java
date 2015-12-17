/**
 * Colleen Rogers
 * Fall 2015
 * Intelligent Tic-Tac-Toe: Using reinforcement learning
 * SOME GENERAL INFORMAITON ABOUT THE ALGORITHM
 * Tic Tac Toe index positions
		
		 0 | 1 | 2     
		-----------
		 3 | 4 | 5 
		-----------
		 6 | 7 | 8
		
		Values of points in each of the strategy groups are represented with integers 0-8
 * 
 * Each player keeps track of game states which are represented as strings
 * 		So for example the state " XOOO  X "
 * 								  012345678
 * 				
 * 				  | X | O     
				-----------
		 		O | O |   
				-----------
		 		  | X |  
 *
 * For each state the player also keeps track of a strategy group consisting of 'points' of different 
 * 'values'(0-8) representing the index of the possible moves. Initially this group is populated with 
 * an equal number of each point value. To decide where to move the user just randomly selects a 
 * value from the group.
 * Then based on the outcome of the game the player will either add another point of that value to the
 * group (increasing the odds it will make the same decision given that state again)
 * or will remove a point of that value from the group (decreasing the odds it will make the same decision 
 * given that same state again) 
 * 
 */


import java.util.*;
import java.io.*;

public class TicTacToe_Learn
{
	//Keeps track of all states. One per player
	public static ArrayList<String> player1StateArray;
	public static ArrayList<String> player2StateArray; 
	
	//Keeps track of the strategy groups (with states) for each player
	public static ArrayList<String> player1StrategyGroup;
	public static ArrayList<String> player2StrategyGroup;
	
	//Which strategy groups the player has used
	public static ArrayList<Integer> player1StrategyGroupsUsed;
	public static ArrayList<Integer> player2StrategyGroupsUsed;
	
	//Which position player chose from the strategy group
	public static ArrayList<Integer> player1ValueUsedFromGroup;
	public static ArrayList<Integer> player2ValueUsedFromGroup;
	
	//maximum amount of 'points'/information each group can have 
	public static int maxPointsPerGroup; 

	final static int NUM_OF_GAMES=1000000;	//See best results between 10000-1000000 games
	
	//By default Player1 is assigned to 'X' and Player2 is assigned to 'O' 
	final static char PLAYER1 = 'X';
	final static char PLAYER2 ='O';
	
	public static int once=0;
	
	public static void main(String[] args) throws IOException
	{
			
		/*Tic Tac Toe index positions
		
		 0 | 1 | 2     
		-----------
		 3 | 4 | 5 
		-----------
		 6 | 7 | 8
		
		Values of points in each of the strategy groups are represented with integers 0-8
		
		
		*/
		

		
		//List of all states. Each player has own stateArray.
		player1StateArray = new ArrayList<String>();
		player2StateArray = new ArrayList<String>();
		
		//List of strategyGroup contents by state. The state array and group contents array will always be same length
		player1StrategyGroup = new ArrayList<String>();
		player2StrategyGroup = new ArrayList<String>();
		
		
		
		player1StrategyGroupsUsed = new ArrayList<Integer>();
		player1ValueUsedFromGroup = new ArrayList<Integer>();
		
		player2StrategyGroupsUsed = new ArrayList<Integer>();
		player2ValueUsedFromGroup = new ArrayList<Integer>();
		
		//The number of points in a strategyGroup won't exceed maxPointsPerGroup. If this is reached, will replace another value
		maxPointsPerGroup = 100;    
		
		                       
		String currentState;
		
	
		//Winner will hold 'X' (player 1 won), 'O'(player 2 won), ' ' (game ongoing), or 'D' (game ended in draw)
		char winner = ' ';
		
		//can have one or both players learn
		boolean player1Learns = true;  
		boolean player2Learns = false;
		
		int player1Wins = 0;
		int player2Wins = 0;
		int endedInDraw = 0;
		
		int player1Started = 0;
		int player2Started = 0;
		

		int counter = 0;
		long startTime = System.currentTimeMillis();
		
		//begin playing NUM_OF_GAMES games of Tic-Tac-Toe
		for(int i = 0; i < NUM_OF_GAMES; i++)
		{
			System.out.println("============= NEW GAME =================");
			
			//clear the current state at beginning of new game
			currentState = getBlankState();
					
			//Clear GroupsUsed and PointValueUsed arrays at the start of each game
			player1StrategyGroupsUsed.clear();
			player1ValueUsedFromGroup.clear();
			player2StrategyGroupsUsed.clear();
			player2ValueUsedFromGroup.clear();
			
			//alternate which player makes first move
			if (counter % 2 == 0)
			{
				//Player2 goes first
				//player2Started++;
				while (true)
				{
					player2Started++;  //counts the number of turns that player 2 is starting 
					
					int player2Move= getNextMove(currentState,2);
					currentState = addMarkToState(currentState, player2Move, PLAYER2);
					displayBoard(currentState);
					winner = findWinner(currentState);
					
					//Game over- either winner or draw
					if (winner != ' '){
						break;
					}
	
					int player1Move= getNextMove(currentState,1);
					currentState = addMarkToState(currentState, player1Move, PLAYER1);
					displayBoard(currentState);
					winner = findWinner(currentState);
					//Game over- either winner or draw
					if (winner != ' ') {
						break;
					}
	
				}
			}
			else
			{
				//Player1 goes first
				//player1Started++;
				while (true)
				{	
					player1Started++;  
					
					int player1Move= getNextMove(currentState,1);
					currentState = addMarkToState(currentState, player1Move, PLAYER1);
					displayBoard(currentState);
					winner = findWinner(currentState);
					//Game over- either winner or draw
					if (winner != ' ') {
						break;
					}
					

					int player2Move= getNextMove(currentState,2);
					currentState = addMarkToState(currentState, player2Move, PLAYER2);
					displayBoard(currentState);
					winner = findWinner(currentState);
					
					//Game over- either winner or draw
					if (winner != ' '){
						break;
					}
					
				}
			}
			counter++;
			
			/*Learning players are rewarded/punished based on results of game (reinforcement)*/

			
			if (winner == PLAYER2)
			{				
				System.out.println("player2 won");
				
				if (player2Learns)
				{
					//Reward PLAYER2 by adding points to it's winning strategy group
					for(int x = 0; x < player2StrategyGroupsUsed.size(); x++)
					{
						rewardPlayer(player2StrategyGroupsUsed.get(x), player2ValueUsedFromGroup.get(x), 8, 2);
					}
				}
				
				if (player1Learns)
				{
					//Punish PLAYER1 by removing points to it's losing strategy group 
					for(int x = 0; x < player1StrategyGroupsUsed.size(); x++)
					{
						punishPlayer(player1StrategyGroupsUsed.get(x), player1ValueUsedFromGroup.get(x), 8, 1);
					}
				}
				player2Wins++;
			}
			else if (winner == PLAYER1)
			{				
				System.out.println("player1 won");
				
				if (player2Learns)
				{
					//Punish PLAYER2 by removing points to it's losing strategy group 
					for(int x = 0; x < player2StrategyGroupsUsed.size(); x++)
					{
						punishPlayer(player2StrategyGroupsUsed.get(x), player2ValueUsedFromGroup.get(x), 8, 1);
					}
				}
			
				if (player1Learns)
				{
					//Reward PLAYER1 by adding points to it's winning strategy group
					for(int x = 0; x < player1StrategyGroupsUsed.size(); x++)
					{
						rewardPlayer(player1StrategyGroupsUsed.get(x), player1ValueUsedFromGroup.get(x), 8, 1);
					}
				}
				player1Wins++;
			}
			else if (winner == 'D')
			{
				//Game ended in a draw so both players are punished
				
				if (player2Learns)
					for(int x = 0; x < player2StrategyGroupsUsed.size(); x++)
					{
						punishPlayer(player2StrategyGroupsUsed.get(x), player2ValueUsedFromGroup.get(x), 9, 2);
					}
				
				if (player1Learns)
					for(int x = 0; x < player1StrategyGroupsUsed.size(); x++)
					{
						punishPlayer(player1StrategyGroupsUsed.get(x), player1ValueUsedFromGroup.get(x), 9 ,1);
					}
				endedInDraw++;
			}
			
			
		}
		
		System.out.println("\n\n\n============= GENERAL STATISTICS =================");
		System.out.println("Player1 'X':");
		
		for(int x = 0; x < player1StateArray.size(); x++)
		{
			displayBoard(player1StateArray.get(x));
			System.out.println("X to move,  strategy group:  '" + player1StrategyGroup.get(x) + "'");
		}
		
		//write player1StateArray and player1StrategyGroup to file 
		
		System.out.println("");
		System.out.println("Player1Wins 'X':  " + player1Wins);
		System.out.println("Player2Wins 'O':  " + player2Wins);
		System.out.println("Ended In Draw; " + endedInDraw);
		
		System.out.println("Player1Started: " + player1Started);
		System.out.println("Player2Started: " + player2Started);
		
		if (player1Learns){
			double rate=(double) player1Wins/NUM_OF_GAMES;
	        System.out.printf("\nSUCCESS RATE: %.2f%% " , (rate*100));
	    	System.out.println("\nWriting PLAYER1 knowledge to file...");
	    	String fileName="TicTacToe-P1-"+NUM_OF_GAMES+".txt";
	    	ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName));
	    	outputStream.writeObject(player1StateArray);
	    	outputStream.writeObject(player1StrategyGroup);
	    	System.out.println("Write to " + fileName+" complete");
		}
		if (player2Learns){
			double rate=(double) player1Wins/NUM_OF_GAMES;
	        System.out.printf("\nSUCCESS RATE: %.2f%% " , (rate*100));
	    	System.out.println("\nWriting PLAYER2 knowledge to file...");
	    	String fileName="TicTacToe-P2"+NUM_OF_GAMES+".txt";
	    	ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(fileName));
	    	outputStream.writeObject(player2StateArray);
	    	outputStream.writeObject(player2StrategyGroup);
	    	System.out.println("Write to " + fileName+" complete");
		}
		/*long total = Runtime.getRuntime().totalMemory();
		long used  = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
		System.out.println("Used: "+used);
		System.out.println("Total: " +total);
		*/
		
		 // Get the Java runtime
	    Runtime runtime = Runtime.getRuntime();
	    // Run the garbage collector
	    runtime.gc();
	    // Calculate the used memory
	    long memory = runtime.totalMemory() - runtime.freeMemory();
	    System.out.println("Used memory is bytes: " + memory);
	    System.out.println("Used memory is megabytes: "
	        + bytesToMegabytes(memory));
	    System.out.println("Free memory: " + runtime.freeMemory() + " bytes.");
	    //Calculate Run time
	    long endTime   = System.currentTimeMillis();
	    long totalTime = endTime - startTime;
	    System.out.println("Average time per game(in miliseconds): "+ (double)totalTime/NUM_OF_GAMES);
	    System.out.println("Total Time(in miliseconds): "+totalTime);
	    System.out.println("Total Time(in seconds): "+ (double)totalTime/1000);
	    
	    
	   
	}
	
	/*Initializes state of board at beginning of each game. 
	 * Consists of a blank space for each position on the board
	 */
	public static String getBlankState()
	{	
		int numOfPositions = 9;  //9 positions on standard 3x3 Tic Tac Toe Board
		String state="";
		
		for (int i =0; i<numOfPositions; i++){
			state=state+" ";
		}
		return state;
	}
	
	
	
	/*
	 * Given current state, prints out the tic tac toe board
	 */

	public static void displayBoard(String state)
	{
		System.out.println("\n " + state.charAt(0) + " | " + state.charAt(1) + " | " + state.charAt(2));
		System.out.println("---|---|---");
		System.out.println(" " + state.charAt(3) + " | " + state.charAt(4) + " | " + state.charAt(5));
		System.out.println("---|---|---");
		System.out.println(" " + state.charAt(6) + " | " + state.charAt(7) + " | " + state.charAt(8));
	}
	
	/*Initialize each of the strategy groups before learning algorithm begins
	 * The strategy group is a string consisting of maxPoints characters
	 * Values: 0 (upper left), 1 (upper middle), 2 (upper right), 3(middle left)...8 (bottom right)
	 * The more points of a certain value there are in the StrategyGroup the higher chance the 
	 * player will choose that position
	 * Initially: 8 points for each valid move (value). 9 values*8pts each= initial length of 72 characters
	 * 				There will initially be the same probability (1 in 9) that the player will chose any of the possible moves
	 * As the game continues and the player learns, algorithm takes and removes points of the varying values
	 * depending on the results of the game and the moves it makes
	 */
	
	public static String initializeStrategyGroups(String state)
	{
		if (findWinner(state) == 'D'){
			throw new RuntimeException("Error in getInitialStrategyGroupContentByState: Board is full");
		}
		
		String strategyGroupContents = "";
		char blank = ' ';
		
		int startingPoints = 8;
		
		for(int index = 0; index < state.length(); index++)
		{
			if (state.charAt(index) == blank)
			{
				for(int x = 0; x < startingPoints; x++)
					strategyGroupContents += "" + index;
			}
		}
		return strategyGroupContents;
		
	}
	
	
	/*Given the passed in state, returns the index where the player should go to win.
	 * If the state is unknown, add a default strategyGroup contents
	 */
	
	public static int getNextMove(String currentState, int player)
	{
		
		int randomMove = -1;
		int stateIndex=getIndexOfState(currentState, player);
		if (player ==1){
					
			if (stateIndex == -1)
			{
				player1StateArray.add(currentState);
				player1StrategyGroup.add(initializeStrategyGroups(currentState));
				stateIndex = player1StrategyGroup.size()-1;
			}
			
			randomMove = getRandomMove(stateIndex,player);
		
			//record the value chosen from strategy group so can use to reward/punish player based on results
			player1StrategyGroupsUsed.add(stateIndex);
			player1ValueUsedFromGroup.add(randomMove);
		}
		else if (player ==2){
						
			if (stateIndex == -1)
			{
				player2StateArray.add(currentState);
				player2StrategyGroup.add(initializeStrategyGroups(currentState));
				stateIndex = player2StrategyGroup.size()-1;
			}
			randomMove = getRandomMove(stateIndex,player);
		
			//record the value chosen from strategy group so can use to reward/punish player based on results
			player2StrategyGroupsUsed.add(stateIndex);
			player2ValueUsedFromGroup.add(randomMove);
			
		}		
		return randomMove;
	}
	
	
	/*Gets a random move from the strategy group
	 * Returns the index of the location where player chooses to place mark
	 * Decided by picking a random position within the string
	 */
	public static int getRandomMove(int stateIndex, int player)
	{
			
		Random r = new Random();
		int move=-1;
		
	//	 int randomNum = rand.nextInt((max - min) + 1) + min;
		if (player==1){
			//randomly selects a value at some point in the corresponding strategy group
			//Example: If strategy group contains 2222255999 will random point in the group (50% chance of choosing 2 etc...)
			int randPosition = r.nextInt(player1StrategyGroup.get(stateIndex).length());
			
			//need to parse to get the index of the position we want to place our mark in
			move= Integer.parseInt(player1StrategyGroup.get(stateIndex).charAt(randPosition) + "");
		}
		else if (player==2){
			int randPosition = r.nextInt(player2StrategyGroup.get(stateIndex).length());
			move= Integer.parseInt(player2StrategyGroup.get(stateIndex).charAt(randPosition) + "");
		}
		
		return move;
		
	}
	
	
	/*Checks the players state array for the state
	 * If that state does not yet exist within the array
	 * return -1. 
	 */
	public static int getIndexOfState(String state, int player)
	{
		//Player1
		if (player == 1){
			for(int x = 0; x < player1StateArray.size(); x++)
			{
				if (state.equals(player1StateArray.get(x)))
				{
					return x;
				}
			}
		}
		//Player2
		else if (player ==2){
			for(int x = 0; x < player2StateArray.size(); x++)
			{
				if (state.equals(player2StateArray.get(x)))
				{
					return x;
				}
			}
		}
		return -1;
	}
	
	/*
	 * Given a position, mark ('X' or 'Y'), and current board
	 * simply places the piece
	 */
	
	public static String addMarkToState(String state, int position, char mark)
	{
		char[] stateArray = state.toCharArray();
		
		stateArray[position] = mark;
		
		String newState = new String(stateArray);
		
		return newState;
	}
	
	/*
	 * Find winner identifies all of the possible winning positions.
	 * It then searches the current state to see if a single player occupies
	 * three locations in a row.
	 * Function returns: 'X' (player 1), 'O' (player 2), 'D' (board is full but no winner)
	 * 					' ' (no winner-game still ongoing)
	 */
	
	public static char findWinner(String state)
	{
		char winner = ' ';
		
		//Otherwise check to see if X or O have three in a row
			
			//create a list of all possible ways to win
			ArrayList<String> winningPositions = new ArrayList<String>();
	
			winningPositions.add("012");
			winningPositions.add("345");
			winningPositions.add("678");
			winningPositions.add("036");
			winningPositions.add("147");
			winningPositions.add("258");
			winningPositions.add("048");
			winningPositions.add("246");
		
			for(int x = 0; x < winningPositions.size(); x++)
			{
				if (state.charAt(Integer.parseInt(winningPositions.get(x).charAt(0) + "")) != ' ' && state.charAt(Integer.parseInt(winningPositions.get(x).charAt(0) + "")) == state.charAt(Integer.parseInt(winningPositions.get(x).charAt(1) + "")) &&
						state.charAt(Integer.parseInt(winningPositions.get(x).charAt(1) + "")) == state.charAt(Integer.parseInt(winningPositions.get(x).charAt(2) + "")))
				{
					winner = state.charAt(Integer.parseInt(winningPositions.get(x).charAt(0) + ""));
				}
			}
		
		//Check for draw
		if (winner==' ' && state.contains(" ") == false){
			winner=  'D';
		}
		
		
		return winner;
		
	}
	
	
	
	/*Will Remove point from strategyGroup if it's there.
	 * If the point is not there it will not do anything
	 * 
	 * Removing a point of pointValue will decrease the 
	 * odds it will make the same decision again
	 */
	public static void punishPlayer(int index, int pointValue, int count,int player)
	{
		if  (player==1){
			for(int x = 0; x < count; x++)
			{
				if (player1StrategyGroup.get(index).length() < 2)
					return;
				
				if (player1StrategyGroup.get(index).lastIndexOf(pointValue + "") != player1StrategyGroup.get(index).indexOf(pointValue + ""))
					player1StrategyGroup.set(index, player1StrategyGroup.get(index).replaceFirst(pointValue + "", ""));
				else
					break;
			}
		}
		if (player ==2){
			for(int x = 0; x < count; x++)
			{
				if (player2StrategyGroup.get(index).length() < 2)
					return;
				
				if (player2StrategyGroup.get(index).lastIndexOf(pointValue + "") != player2StrategyGroup.get(index).indexOf(pointValue + ""))
					player2StrategyGroup.set(index, player2StrategyGroup.get(index).replaceFirst(pointValue + "", ""));
				else
					break;
			}
		}
		
	}
	/*Will add point to strategy group .
	 * If the number of points already in the state, then simply add another.
	 * Otherwise, then  delete a point to make room.
	 * 
	 * Adding a point of pointValue to the group will increase the odds 
	 * it will make the same decision again 
	 */
	
	
	public static void rewardPlayer(int index, int pointValue, int count, int player)
	{
		if (player ==1){
			if (player1StrategyGroup.get(index).length() < maxPointsPerGroup)
			{
				for(int x = 0; x < count; x++)
					player1StrategyGroup.set(index, player1StrategyGroup.get(index) + pointValue);
			}
			else
			{
				//need to erase some memory to make room.  Get the index of the 
				//first spot that is not pointValue, strengthening the good intelligence
				for(int x = 0; x < player1StrategyGroup.get(index).length(); x++)
				{
					if (Integer.parseInt(player1StrategyGroup.get(index).charAt(x) + "") != Integer.toString(pointValue).charAt(0))
					{
						char[] charArray = player1StrategyGroup.get(index).toCharArray();
						charArray[x] = Integer.toString(pointValue).charAt(0);
						player1StrategyGroup.set(index, new String(charArray));
						return;
					}
				}
			
			}
		}
		else if (player==2){
			if (player2StrategyGroup.get(index).length() < maxPointsPerGroup)
			{
				for(int x = 0; x < count; x++)
					player2StrategyGroup.set(index, player2StrategyGroup.get(index) + pointValue);
			}
			else
			{
				//need to erase some memory to make room.  Get the index of the 
				//first spot that is not pointValue, strengthening the good intelligence
				
				for(int x = 0; x < player2StrategyGroup.get(index).length(); x++)
				{
					if (Integer.parseInt(player2StrategyGroup.get(index).charAt(x) + "") != Integer.toString(pointValue).charAt(0))
					{
						char[] charArray = player2StrategyGroup.get(index).toCharArray();
						charArray[x] = Integer.toString(pointValue).charAt(0);
						player2StrategyGroup.set(index, new String(charArray));
						return;
					}
				}
			}
		}	
		
	}
	 private static final long MEGABYTE = 1024L * 1024L;

	  public static long bytesToMegabytes(long bytes) {
	    return bytes / MEGABYTE;
	  }
	
}

/*
 * Some Preliminary Results:
 * 
Games =10
Player1Wins 'X':  2
Player2Wins 'O':  3
Ended In Draw; 5
Player1Started: 21
Player2Started: 24

Games = 100 
Player1Wins 'X':  33				1.06xs better
Player2Wins 'O':  31
Ended In Draw; 36
Player1Started: 213
Player2Started: 211


Games = 10000 
Player1Wins 'X':  411				1.4xs better
Player2Wins 'O':  298
Ended In Draw; 291
Player1Started: 2040
Player2Started: 2034


Games = 10000
Player1Wins 'X':  7056				4.3xs better
Player2Wins 'O':  1640
Ended In Draw; 1304
Player1Started: 18207
Player2Started: 19164


Games = 100000

Player1Wins 'X':  89609				13.7x's better
Player2Wins 'O':  6533
Ended In Draw; 3858
Player1Started: 170692
Player2Started: 173748


Games = 1000000
Player1Wins 'X':  924198			19x's better
Player2Wins 'O':  48629
Ended In Draw; 27173
Player1Started: 1655878
Player2Started: 1706772

Games = 2000000
Player1Wins 'X':  1855602			19.9xs better
Player2Wins 'O':  93367
Ended In Draw; 51031
Player1Started: 3256735
Player2Started: 3405376



Games = 5000000
Player1Wins 'X':  4628710			18 xs better
Player2Wins 'O':  254790
Ended In Draw; 116500
Player1Started: 8078224
Player2Started: 8385678

 * 
 */


/*
MemoryUsage heapMemoryUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
heapMemoryUsage.getUsed();
*/


