package ff8cardgame;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Random;
import java.util.Vector;


public class Program {

	/**
	 * You get to play against a hard computer by yourself.
	 * The hard computer will use current AI method.
	 * 
	 * @param board
	 * @param turn who goes first.
	 *           false for computer
	 *           true for  human
	 */
	public static void playHardComp(Board board, boolean turn){
		
		while(!board.isTerminal()){

			if(turn){
				MoveMaker.makeMove(board, true);
			}
			else{
				MoveMaker.makeAIMove(board, turn);
			}
			
			board.print();
			
			System.out.println("User Score: " + board.boardValue(true));
			
			turn = !turn;
		}
		
		printResult(board);
		
	}
	
	/**
	 * You get to play against the computer where the AI
	 * will tell you what move to play and you make the moves
	 * the computer made on the screen.
	 * 
	 * @param board
	 * @param turn  who goes first.
	 *           false for computer
	 *           true for  human
	 */
	public static void playFF8Comp(Board board, boolean turn){
		
		while(!board.isTerminal()){

			if(turn){
				MoveMaker.makeAIMove(board,turn);
			}
			else{
				MoveMaker.makeMove(board, false); 
			}
			
			board.print();
			
			turn = !turn;
		}
		
		printResult(board);
	}
	
	public static void printResult(Board board){
		if(board.boardValue(true) > 0){
			System.out.println("Human wins");
		}else if(board.boardValue(true) == 0){
			System.out.println("Draw!");
		}else{
			System.out.println("Computer Wins");
		}
	}
	
	/**
	 * An game between human and computer where both take
	 * turns using AI.
	 * 
	 * @param board
	 * @param turn who goes first.
	 *           false for computer
	 *           true for  human
	 */
	public static void playAIGame(Board board, boolean turn){
		
		while(!board.isTerminal()){
			
			MoveMaker.makeAIMove(board, turn);
			board.print();
			turn = !turn;
			
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
		
		printResult(board);
		
	}
	
	/**
	 * User can test own sample game to see if all the rules
	 * of the game correctly execute at run time.
	 * 
	 * @param board
	 * @param turn
	 */
	public static void playInputTester(Board board, boolean turn){
		
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

		String line = "Yes";
		do{

			while(!board.isTerminal()){

				MoveMaker.debugMove(board, turn);
				turn = !turn;
				
			}

			board.print();
			printResult(board);

			System.out.print("Would you like to try again? (Yes or No)");
			try {
				line = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				line = "Yes";
			}

		}while(line.compareTo("No") != 0);

	}
	
	//Randomizes who goes first
	public static void playHardCompRandom() {
		Player human = new Player();
		human.randomize();
		Player computer = new Player();
		computer.randomize();
		Board board = new Board(human,computer,40);
		
		boolean turn = false;
		Random random = new Random(System.currentTimeMillis());
		if(random.nextInt(100) < 50)
			turn = true;
		
		while(!board.isTerminal()){

			if(turn){
				MoveMaker.makeMove(board, true);
			}
			else{
				System.out.println("Opponent is Making move");
				MoveMaker.makeAIMove(board, false);
				for(int i = 0; i < 10; ++i){
					System.out.print(".");
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
					}
				}
			}

			board.print();
			
			System.out.println("User Score: " + board.boardValue(true));
			
			turn = !turn;
		}
		
		printResult(board);	
		
		System.out.println("Exiting in 5 seconds");
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String [] args){
		Board.setRules();
		Board.printRules();
		
		//playHardCompRandom();
		
		Player human = new Player();
		human.loadCards("human");
		Player computer = new Player();
		computer.loadCards("computer");

		Board board = new Board(human, computer, 40);
		
		boolean turn = FF8UIReader.readTurn(); //true for you, and false for computer
		//boolean turn = false;
		
		playAIGame(board, turn);
//		playHardComp(board, turn);
//		playFF8Comp(board,turn);
//		playInputTester(board,turn);
	}

}
