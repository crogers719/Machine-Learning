/**
 * Colleen Rogers
 * Fall 2015
 * Machine Learning Applications: Intelligent Connect Four
 * About Connect4:
 * 
 *  
 *  Implementing Machine Learning Algorithm for Connect4:
 *  
 *  
 *  Uses the Minimax Algorithm in conjunction with a basic heuristic function I developed
 *  Can change depth of the algorithm; Will affect performance and speed.
 *  
 *  
 *  
 */
import java.util.Scanner;



/**
 *BOARD CLASS:
 *Contains board (a 2-dimensional character array that holds the board in its current state)
 *Performs basic functions pertaining  to alteration of the board itself
 *
 *
 */
class Board{
	
	//Standard Dimensions of a Connect four board (6x7)
	public static final int NUM_ROWS = 6;
	public static final int NUM_COLS = 7;
	
	//Player1 ('Red') is the one using the AI algorithm
	public static final char COMPUTER='R';
	//Player2 ('Yellow') is a naive opponent; Can either have (random) automated moves, or can be controlled by a user
	public static final char OPPONENT='Y';
	//Represents an empty cell
	public static final char EMPTY=' ';

	
   char[][] board = new char[NUM_ROWS][NUM_COLS];
    
   /*
    * Clear board sets all positions in board equal to default value (EMPTY=' ')  
    */

    public void clearBoard(){
    	for (int r=0; r<NUM_ROWS; r++){
    		for (int c=0; c<NUM_COLS; c++){
    			board[r][c]=EMPTY;
    		}
    	}
    } 
    
    /*
     * Will display the contents of the game board in its current state
     */
       public void displayBoard(){
           System.out.print("\n");
           for(int r=0;r<NUM_ROWS;r++){
               for(int c=0;c<NUM_COLS;c++){
                   System.out.print(board[r][c]+" |");
               }
               System.out.print("\n-----------------------\n");
           }
           System.out.println();
       }
    
    /*
     * Given a column number, will place corresponding character (player)
     * in the highest (farthest down) empty row in that column 
     */
    public boolean placePiece(int column, char player){ 
        if(!validMove(column)) {
        	System.out.println("Illegal move!"); 
        	return false;
        }
        for(int r=NUM_ROWS-1;r>=0;r--){
            if(board[r][column] == EMPTY) {
                board[r][column] = player;
                return true;
            }
        }
        return false;
    }
    
    
    /*
     * Valid Move determines whether a move (choice of column) is valid
     * 	Checks that the column number is between 1-7 and that the chosen column is not full
     */
    public boolean validMove(int column){
    	if(column>=0 && column<NUM_COLS && board[0][column]==' '){
    		return true;
        }
        else{
         	return false;
        }
    }
    
   
    
    /*
     * Given the column number will remove the last piece inserted into the column
     * Useful for undoing moves made as part of the Minimax algorithm
     */
    
    public void undo(int column){
        for(int r=0;r<NUM_ROWS;++r){
            if(board[r][column] != EMPTY) {
                board[r][column] = EMPTY;
                break;
            }
        }        
    }
 
}
/**
 * CONNECTFOUR CLASS
 * Contains:
 * 			Constants for: board dimensions, characters, number of games, and autoplay settings
 * 			An object of the board class to hold the game board,the next move and, the maximum depth to be used in Minimax
 * Performs: 
 * 			Machine learning algorithm that enables the intelligent player (COMPUTER) to make advantageous
 * 			decisions using the Minimax Algorithm    
 */	

public class ConnectFour { 
	//Standard Dimensions of a Connect four board (6x7)
	public static final int NUM_ROWS = 6;
	public static final int NUM_COLS = 7;
	
	//Player1 ('Red') is the one using the AI algorithm
	public static final char COMPUTER='R';
	//Player2 ('Yellow') is a naive opponent; Can either have (random) automated moves, or can be controlled by a user
	public static final char OPPONENT='Y';
	//Represents an empty cell
	public static final char EMPTY=' ';
	
	//Number of consecutive games to be played
	public static final int NUM_OF_GAMES=250;
	//If true, will randomize moves; Otherwise a user can play against the machine
	public static final boolean AUTOPLAY=true; 
	
	
    private Board gameBoard;
    private Scanner kbd;
    private int nextMove=-1;
    
    /*The maximum depth used by the min-max algorithm--need cut off point
    * Tested for values between 1-8(depth =1 --> fast with about 97% success rate
    * depth =8 -->slower but with a near 100% success rate
    */ 
    private int maxDepth =7;   
    
    /*
     * ConnectFour constructor
     * 			Takes a Board object, b, as a parameter and assigns gameBoard=b
     * 			Sets up a Scanner object (kbd) to be used in class functions
     */
    
    public ConnectFour(Board b){
        gameBoard = b;
        kbd = new Scanner(System.in);
    }
    
    
    
    /*
     * Allows the opponent to make a move. If AUTOPLAY is on will simply generate a number between 1-7(NUM_COLS)
     * Otherwise it will take input from the user for a column number (1-7)
     * Then calls the place piece method (in Board class) to place the piece at the position corresponding to the 
     * column number obtained.
     * 
     */
    public void opponentMove(){
    	int move=-1;
    	if (AUTOPLAY){
    		do{
    			move= (int)(Math.random()*NUM_COLS) + 1;
    		}while (!gameBoard.validMove(move-1));
    	}
    	else{
    		System.out.println("Enter a Column (1-7): ");
    		move = kbd.nextInt();
    		while(!gameBoard.validMove(move-1)){
    			System.out.println("Invalid move.\n\nEnter a Column (1-7): "); 
    			move = kbd.nextInt();
    		}
    	}
        //call to place piece (in Board class); this assumes that player 2 is the opponent
        gameBoard.placePiece(move-1, OPPONENT); 
    }
    
    
    /*
     * Finds the results of the game by checking the gameBoard for a winner
     * 		Four ways to win:
     * 				Horizontally
     * 				Vertically
     * 				Diagonal#1 (lower left->upper right)
     * 				Diagonal#2 (lower right->upper left)
     * 		If finds a winner, returns the character associated with winning player.
     */
    public char findWinner(Board gameBoard){
        int countComputer = 0;
        int countOpponent = 0;
        for(int r=NUM_ROWS-1;r>=0;r--){
            for(int c=0;c<NUM_COLS;c++){
            	if(gameBoard.board[r][c]==EMPTY){
            		continue;
            	}
                
                //Check For Horizontal Win
                if(c<=3){
                    for(int incr=0;incr<4;incr++){ 
                            if(gameBoard.board[r][c+incr]==COMPUTER) {
                            	countComputer++;
                            }
                            else if(gameBoard.board[r][c+incr]==OPPONENT) {
                            	countOpponent++;
                            }
                            else{
                            	break; 
                            }
                    }
                    if(countComputer==4){
                    	return COMPUTER; 
                    }
                    else if (countOpponent==4){
                    	return OPPONENT;
                    }
                    //No winner-reset counters
                    countComputer = 0; 
                    countOpponent = 0;
                } 
                
                //Check for vertical win
                if(r>=3){
                    for(int incr=0;incr<4;incr++){
                            if(gameBoard.board[r-incr][c]==COMPUTER) {
                            	countComputer++;
                            }
                            else if(gameBoard.board[r-incr][c]==OPPONENT) {
                            	countOpponent++;
                            }
                            else{
                            	break;
                            }
                    }
                    if(countComputer==4){
                    	return COMPUTER; 
                    }
                    else if (countOpponent==4){
                    	return OPPONENT;
                    }
                    countComputer = 0; 
                    countOpponent = 0;
                } 
                
                //Check for diagonal (up-right) win
                if(c<=3 && r>= 3){
                    for(int incr=0;incr<4;incr++){
                        if(gameBoard.board[r-incr][c+incr]==COMPUTER){
                        	countComputer++;
                        }
                        else if(gameBoard.board[r-incr][c+incr]==OPPONENT) {
                        	countOpponent++;
                        }
                        else{
                        	break;
                        }
                    }
                    if(countComputer==4){
                    	return COMPUTER; 
                    }
                    else if (countOpponent==4){
                    	return OPPONENT;
                    }
                    countComputer = 0; 
                    countOpponent = 0;
                }
                
                //Check for diagonal (up-left) win
                if(c>=3 && r>=3){
                    for(int incr=0;incr<4;incr++){
                        if(gameBoard.board[r-incr][c-incr]==COMPUTER){
                        	countComputer++;
                        }
                        else if(gameBoard.board[r-incr][c-incr]==OPPONENT){
                        	countOpponent++;
                        }
                        else{
                        	break;
                        }
                    } 
                    if(countComputer==4){
                    	return COMPUTER; 
                    }
                    else if (countOpponent==4){
                    	return OPPONENT;
                    }
                    countComputer = 0; 
                    countOpponent = 0;
                }  
            }
        }
        
        for(int c=0;c<NUM_COLS;c++){
            //There is still an empty location-Game has not ended yet
            if(gameBoard.board[0][c]==EMPTY){
            	return '-';
            }
        }
        //No winner and no empty locations mean that the game is a DRAW
        return 'D';
    }
    
    /*
     * Calls Minimax, a recursive function, which will eventually change the value of next move
     * Then returns nextMove
     */
    
    public int informedDecision(){
        nextMove = -1;
        int start =0;
      
        Minimax(start, COMPUTER);
        return nextMove;
    }
    
/*
 * MINIMAX ALGORITHM
 * Used when expanding the full game tree is not possible  
 * Aims to minimize the possible loss for worst case scenario. 
 * Minimizes the loss against a perfectly playing opponent. 
 * Uses a a basic heuristic function to evaluate the desirability of given board states
 */
    public int Minimax(int depth, char turn){
        //check for winner will return COMPUTER or OPPONENT if win 
    	char findWinner = findWinner(gameBoard);
    	
        if(findWinner==COMPUTER){	//computer won
        	return Integer.MAX_VALUE;
        }
        else if(findWinner==OPPONENT){	//oponent won
        	return Integer.MIN_VALUE;
        }
        else if(findWinner==EMPTY){	 
        	return 0;
        }
        
        if(depth==maxDepth){
        	return determineFitness(gameBoard);
        }
        
        int maxScore=Integer.MIN_VALUE;
        int minScore = Integer.MAX_VALUE;
        
        for(int c=0;c<NUM_COLS;c++){
            if(!gameBoard.validMove(c)){
            	continue;
            }
               
            if(turn==COMPUTER){
            	gameBoard.placePiece(c, COMPUTER);
                int currentScore = Minimax(depth+1, OPPONENT);
                maxScore = Math.max(currentScore, maxScore);
                if(depth==0){
                	System.out.println("Score for location "+c+" = "+currentScore);
                    if(maxScore==currentScore) {
                    	nextMove = c;
                    }
                }
            }
            
            else if(turn==OPPONENT){
                    gameBoard.placePiece(c, OPPONENT);
                    int currentScore = Minimax(depth+1, COMPUTER);
                    minScore = Math.min(currentScore, minScore);
            }
            gameBoard.undo(c);
        }
        if (turn==COMPUTER){
            
        	return maxScore;
        }
        
        else{				//turn==OPPONENT
 
        	return minScore;
        }
    }
    /*
     * A basic heuristic function to evaluate the desirability of board states
     * Totals up the amount of consecutive pieces and calls calculateScore to 
     * weigh the score based on how close player is to victory 
     *  
     */
     public int determineFitness(Board gameBoard){
       
         int computerPoints=1;
         int score=0;
         int blanks = 0;
         int offset=0;
         int countMoves=0;
         for(int r=NUM_ROWS-1;r>=0;r--){
             for(int c=0;c<NUM_COLS;c++){
                 
                 if(gameBoard.board[r][c]==EMPTY || gameBoard.board[r][c]==OPPONENT){
                 	continue; 
                 }
                 //potential horizontal
                 if(c<=3){ 
                     for(offset=1;offset<4;offset++){
                         if(gameBoard.board[r][c+offset]==COMPUTER){
                         	computerPoints++;
                         }
                         else if(gameBoard.board[r][c+offset]==OPPONENT){
                         	computerPoints=0;
                         	blanks = 0;
                         	break;
                         }
                         else{
                         	blanks++;
                         }
                     }
                      
                     countMoves = 0; 
                     if(blanks>0) {
                         for(int col=1;col<4;col++){
                             int column = c+col;
                             for(int rw=r; rw<= 5;rw++){
                              if(gameBoard.board[rw][column]==EMPTY){
                             	 countMoves++;
                              }
                                 else{
                                 	break;
                                 }
                             } 
                         } 
                     }
                     
                     if(countMoves!=0){
                     	score = score+ calculateScore(computerPoints, countMoves);
                     }
                     computerPoints=1;   
                     blanks = 0;
                 } 
                 
                 //potential vertical
                 if(r>=3){
                     for(offset=1;offset<4;++offset){
                         if(gameBoard.board[r-offset][c]==COMPUTER){
                         	computerPoints++;
                         }
                         else if(gameBoard.board[r-offset][c]==OPPONENT){ 
                         	computerPoints=0;
                         	break;
                         } 
                     } 
                     countMoves = 0; 
                     
                     if(computerPoints>0){
                         int column = c;
                         for(int rw=r-offset+1; rw<=r-1;rw++){
                          if(gameBoard.board[rw][column]==EMPTY){
                         	 countMoves++;
                          }
                             else{
                             	break;
                             }
                         }  
                     }
                     if(countMoves!=0) {
                     	score = score+ calculateScore(computerPoints, countMoves);
                     }
                     computerPoints=1;  
                     blanks = 0;
                 }
                  
                 if(c>=3){
                     for(offset=1;offset<4;offset++){
                         if(gameBoard.board[r][c-offset]==COMPUTER){
                         	computerPoints++;
                         }
                         else if(gameBoard.board[r][c-offset]==OPPONENT){
                         	computerPoints=0; 
                         	blanks=0;
                         	break;
                         }
                         else{
                         	blanks++;
                         }
                     }
                     countMoves=0;
                     if(blanks>0) {
                         for(int col=1;col<4;col++){
                             int column = c- col;
                             for(int rw=r; rw<= 5;rw++){
                              if(gameBoard.board[rw][column]==EMPTY){
                             	 countMoves++;
                              }
                                 else{
                                 	break;
                                 }
                             } 
                         } 
                     }
                     
                     if(countMoves!=0){
                     	score = score+ calculateScore(computerPoints, countMoves);
                     }
                     computerPoints=1; 
                     blanks = 0;
                 }
                 //Potential diagonal1 
                 if(c<=3 && r>=3){
                     for(offset=1;offset<4;offset++){
                         if(gameBoard.board[r-offset][c+offset]==COMPUTER){
                         	computerPoints++;
                         }
                         else if(gameBoard.board[r-offset][c+offset]==OPPONENT){
                         	computerPoints=0;
                         	blanks=0;
                         	break;
                         }
                         else{
                         	blanks++;                        
                         }
                     }
                     countMoves=0;
                     if(blanks>0){
                         for(int col=1;col<4;col++){
                             int column = c+col;
                             int row = r-col;
                             for(int rw=row;rw<=5;++rw){
                                 if(gameBoard.board[rw][column]==EMPTY){
                                 	countMoves++;
                                 }
                                 else if(gameBoard.board[rw][column]==COMPUTER);
                                 else{
                                 	break;
                                 }
                             }
                         } 
                         if(countMoves!=0) {
                         	score = score+ calculateScore(computerPoints, countMoves);
                         }
                         computerPoints=1;
                         blanks = 0;
                     }
                 }
                  //Potential diagonal2
                 if(r>=3 && c>=3){
                     for(offset=1;offset<4;++offset){
                         if(gameBoard.board[r-offset][c-offset]==COMPUTER){
                         	computerPoints++;
                         }
                         else if(gameBoard.board[r-offset][c-offset]==OPPONENT){
                         	computerPoints=0;
                         	blanks=0;
                         	break;
                         }
                         else blanks++;                        
                     }
                     countMoves=0;
                     if(blanks>0){
                         for(int col=1;col<4;col++){
                             int column = c-col;
                             int row = r-col;
                             for(int rw=row;rw<=5;rw++){
                                 if(gameBoard.board[rw][column]==EMPTY){
                                 	countMoves++;
                                 }
                                 else if(gameBoard.board[rw][column]==COMPUTER);
                                 else{
                                 	break;
                                 }
                             }
                         } 
                         if(countMoves!=0){
                         	score = score+ calculateScore(computerPoints, countMoves);
                         }
                         computerPoints=1;
                         blanks = 0;
                     }
                 } 
             }
         }
         return score;
     } 
     
    
  
    
    /*
     * Calculates some score to evaluate the desirability of a given decision for the computer
     * Based on how many points the computer already has, will weight decisions differently
     */
    
    int calculateScore(int computerPoints, int countMoves){   
        
    	//how many moves away from win
    	int moveScore = 4 - countMoves;
    	
        if(computerPoints==0){
        	return 0;
        }
        else if(computerPoints==1){
        	return 1*moveScore;
        }
        else if(computerPoints==2){
        	return 10*moveScore;
        }
        else if(computerPoints==3){
        	return 100*moveScore;
        }
        else{
        	return 1000;
        }
    }
    
  /*
   * playGames controls actual game play
   * Will loop through so multiple games can be played at once. 
   * If playing multiple games toggle which player moves first
   * 		Keeps track of computer wins, opponent wins, and draws. Calculates success rate
   */
    
    
    
    public char playGame(char goesFirst){
    
    	char winner;
        	gameBoard.clearBoard();
        	if(goesFirst==OPPONENT){		//opponent goes first else computer goes first
        		opponentMove();
                gameBoard.displayBoard();
        	}
      
        
            gameBoard.placePiece(3, COMPUTER);
            gameBoard.displayBoard();
        
            while(true){ 
            	opponentMove();
            	gameBoard.displayBoard();
            
            	winner = findWinner(gameBoard);
            	if(winner==COMPUTER){
            		System.out.println("Computer Wins!");
            		break;
            	}
            	else if(winner==OPPONENT){
            		System.out.println("You Win!");
            		break;
            	}
            	else if(winner=='D'){
            		System.out.println("Draw!");
            		break;
            	}
            
            	gameBoard.placePiece(informedDecision(), COMPUTER);
            	gameBoard.displayBoard();
            	winner = findWinner(gameBoard);
            	if(winner==COMPUTER){
            		System.out.println("AI Wins!");
            		//computerWins++;
            		break;
            	}
            	else if(winner==OPPONENT){
            		System.out.println("You Win!");
            		//opponentWins++;
            		break;
            	}
            	else if(winner=='D'){
            		System.out.println("Draw!");
            		//draws++;
            		break;
            	}
            
            }       
            return winner;
    }
    /*
     * Main Method 
     * creates a game board and a learner and calls the play game function
     */
    
    public static void main(String[] args) {
        Board gameBoard = new Board();
        ConnectFour learner = new ConnectFour(gameBoard);  
    	Scanner kbd = new Scanner(System.in);
        int computerWins=0;
        int opponentWins=0;
        int draws=0;
        char winner;
    	long startTime = System.currentTimeMillis();
                
        System.out.println("*******CONNECT FOUR**********");
        for (int gameCount=1; gameCount<=NUM_OF_GAMES;gameCount++){
        	System.out.println("GAME #"+ gameCount);
        	System.out.println("============");
        	if(gameCount%2==0){
        		winner = learner.playGame(OPPONENT);
        	}
        	else{
        		winner = learner.playGame(COMPUTER);        	
        	}
        	if(winner==COMPUTER){
        		computerWins++;
        		
        	}
        	else if(winner==OPPONENT){
        		opponentWins++;
        		
        	}
        	else if(winner=='D'){
        		draws++;
        		
        	}
        	
    }
    
    System.out.println("************STATISTICS***********");
    System.out.println("Games Played: "+ NUM_OF_GAMES);
    System.out.println("Learning Player Won: "+ computerWins);
    System.out.println("Opponent Won: "+ opponentWins);
    System.out.println("Endend in a draw: "+ draws);
    double rate=(double) computerWins/NUM_OF_GAMES;
    System.out.printf("\nSUCCESS RATE: %.2f%% " , (rate*100));
    
    //Max Depth
    System.out.println("\nMax Depth" + learner.maxDepth);
    // Get the Java runtime
    Runtime runtime = Runtime.getRuntime();
    // Run the garbage collector
    runtime.gc();
    // Calculate the used memory
    long memory = runtime.totalMemory() - runtime.freeMemory();
    System.out.println("\nUsed memory is bytes: " + memory);
	
   System.out.println("Used memory is megabytes: "    + memory / (1024L * 1024L));
    System.out.println("Free memory: " + runtime.freeMemory() + " bytes.");
    //Calculate Run time
    long endTime   = System.currentTimeMillis();
    long totalTime = endTime - startTime;
    System.out.println("Total Time(in miliseconds): "+totalTime);
    System.out.println("Total Time(in seconds): "+ (double)totalTime/1000);
    System.out.println("Average time per game(in miliseconds): "+ (double)totalTime/NUM_OF_GAMES);
	   
   
}

}
