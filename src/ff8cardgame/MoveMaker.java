package ff8cardgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class MoveMaker {

//	public static void compMakeMove(Board board){
//		System.out.println("Opponent is Making move");
//		for(int i = 0; i < 10; ++i){
//			System.out.print(".");
//			try {
//				Thread.sleep(500);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//			}
//		}
//		System.out.println();
//		Move move = board.makeMove();
//		System.out.print("Computer made move: ");
//		move.print();
//		//board.print();	
//	}
	
	public static void debugMove(Board board, boolean curPlayer){
		Move move = null;
		do{
			move = getMove(board);
			if(board.isValidMove(move,curPlayer))
				break;
			else
				System.out.println("Error: That move is not allowed!");
		}while(true);
		Board.debug = true;
		board.makeMove(move, curPlayer);
		Board.debug = false;
		//board.print();			
	}
	
	public static void makeMove(Board board, boolean curPlayer){
		Move move = null;
		do{
			move = getMove(board);
			if(board.isValidMove(move,curPlayer))
				break;
			else
				System.out.println("Error: That move is not allowed!");
		}while(true);
		//Board.debug = true;
		board.makeMove(move, curPlayer);
		//Board.debug = false;
		//board.print();			
	}

//	public static void compMakeMove(Board board){
//		Move move = null;
//		do{
//			move = getMove(board);
//			if(board.isValidMove(move,false))
//				break;
//			else
//				System.out.println("Error: That move is not allowed!");
//		}while(true);
//		Board.debug = true;
//		board.makeMove(move, false);
//		Board.debug = false;
//		board.print();		
//	}

	public static Move getMove(Board board){
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		boolean isValid = true;
		Move move = null;
	
		do{
			board.print();
			board.printPlayerCards();
			
			System.out.print("\nPlease enter your move and card as (i,j,k): ");
			try {
				String line = br.readLine();
				Scanner scanner = new Scanner(line);
				int i  = scanner.nextInt();
				int j  = scanner.nextInt();
				int k  = scanner.nextInt();
				
				move = new Move(i,j,k);
				isValid = true;
	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				isValid = false;
			} catch(InputMismatchException e){
				System.out.println("Error: not an intger expression");
				isValid = false;
			} catch(NoSuchElementException e){
				System.out.println("Error: input is exhausted");
				isValid = false;
			} catch(IllegalStateException e){
				System.out.println("Error: Illegal State Exception");
				isValid = false;
			}
		}while(!isValid);
	
		return move;
	}

	public static void makeAIMove(Board board, boolean turn){
		System.out.println("\nSearching for Move");
		//Board.debug = false;
		Move move = board.useAlphaBeta(turn);
		//Board.debug = true;
		board.makeMove(move,turn);
		//move.print();
		//board.print();		
	}

//	public static void userMakeMove(Board board, boolean curPlayer){
//		Move move = null;
//		do{
//			move = getMove(board);
//			if(board.isValidMove(move, curPlayer))
//				break;
//			else
//				System.out.println("Error: That move is not allowed!");
//		}while(true);
//		board.makeMove(move);
//		board.print();
//	}

}
