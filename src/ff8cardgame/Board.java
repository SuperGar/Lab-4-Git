package ff8cardgame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;


public class Board {

	//Note i is row and j is column

	public enum Elemental {NONE, FIRE, ICE, WATER, HOLY, THUNDER, EARTH, POISON, WIND};
	
	public static boolean debug = false;

	private Player human;
	private Player computer;
	private int [][] board; // -1 represents free space
	private int boardCount;
	private boolean [][] player;
	private Elemental [][] elements;

	//2 player AI information
	private static int maxDepth;
	private static int originalDepth;
	private static Move move;
	private static int numberOfNodes;

	//Rules
	private static boolean sameRule;
	private static boolean plusRule;
	private static boolean sameWall;

	//Differentials for North, East, South, and West positions relative
	private static Move [] NESW = { new Move(-1,0,0),
		new Move(0,1,0), new Move(1,0,0), new Move(0,-1,0)
	};

	public Board(Player human, Player computer, int maxDepth){
		this.human = human;
		this.computer = computer;
		initBoard();
		this.maxDepth = maxDepth;
	}

	private void initBoard(){
		board = new int [3][3];
		player = new boolean [3][3];
		elements = new Elemental [3][3];
		for(int i = 0; i < 3; ++i){
			for(int j = 0; j < 3; ++j){
				board[i][j] = -1;
				player[i][j] = false;
				elements[i][j] = Elemental.NONE;
			}
		}
		loadBoard();
		boardCount = 0;
	}
	
	//NONE, FIRE, ICE, WATER, HOLY, THUNDER, EARTH, POISON, WIND
	private Elemental convertToElement(String token){
		if(token.equals("NONE")){
			return Elemental.NONE;
		}else if(token.equals("FIRE")){
			return Elemental.FIRE;
		}else if(token.equals("ICE")){
			return Elemental.ICE;
		}else if(token.equals("WATER")){
			return Elemental.WATER;
		}else if(token.equals("HOLY")){
			return Elemental.HOLY;
		}else if(token.equals("THUNDER")){
			return Elemental.THUNDER;
		}else if(token.equals("EARTH")){
			return Elemental.EARTH;
		}else if(token.equals("POISON")){
			return Elemental.POISON;
		}else if(token.equals("WIND")){
			return Elemental.WIND;
		}else{
			System.out.println("Error: Unrecognized element (" + token + ")");
			System.exit(1);
		}
		
		return Elemental.NONE;
	}

	private void setElementRow(int row, String rowInput){
		try{
			Scanner scanner = new Scanner(rowInput);
			elements[row][0] = convertToElement(scanner.next());
			elements[row][1] = convertToElement(scanner.next());
			elements[row][2] = convertToElement(scanner.next());
		}catch(NoSuchElementException e){
			e.printStackTrace();
			System.exit(1);
		}catch(IllegalStateException e){
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void loadBoard(){
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("elementalBoard")));
			setElementRow(0,br.readLine());
			setElementRow(1,br.readLine());
			setElementRow(2,br.readLine());
		} catch(NoSuchElementException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (FileNotFoundException e) {
		
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	private void setElement(int i, int j, String token){
		if(token.equals("NONE")){
			elements[i][j] = Elemental.NONE;
		}else if(token.equals("FIRE")){
			elements[i][j] = Elemental.FIRE;
		}else if(token.equals("ICE")){
			elements[i][j] = Elemental.ICE;
		}else if(token.equals("WATER")){
			elements[i][j] = Elemental.WATER;
		}else if(token.equals("HOLY")){
			elements[i][j] = Elemental.HOLY;
		}else if(token.equals("THUNDER")){
			elements[i][j] = Elemental.THUNDER;
		}else if(token.equals("EARTH")){
			elements[i][j] = Elemental.EARTH;
		}else if(token.equals("POISON")){
			elements[i][j] = Elemental.POISON;
		}else{
			System.out.println("Error: parsing file elementalBoard!");
			System.exit(1);
		}
	}

	public void readElemental(){
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("elementalBoard")));
			for(int i = 0; i < 3; ++i){
				String line = br.readLine();
				Scanner scanner = new Scanner(line);
				setElement(i,0,scanner.next());
				setElement(i,1,scanner.next());
				setElement(i,2,scanner.next());
			}
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	//Use true to get from human perspective
	//Use false to get from computer perspective
	public int boardValue(boolean comp){
		if(!comp)
			return human.numLost() - computer.numLost();
		else
			return computer.numLost() - human.numLost();
	}

	public Move getMove(){

		originalDepth = 40;
		int depth = originalDepth;
		numberOfNodes = 0;
		int value = minimax(depth,true);
		System.out.println("Value          : " + value);
		System.out.println("Number of Nodes: " + numberOfNodes);
		System.out.println("Move           : ");
		move.print();
		return move;
	}

	public Move useNegaMax(boolean curPlayer){

		originalDepth = 40;
		int depth = originalDepth;
		numberOfNodes = 0;
		int value = negaMax(depth,curPlayer);
		System.out.println("Value          : " + value);
		System.out.println("Number of Nodes: " + numberOfNodes);
		System.out.println("Move           : ");
		move.print();
		return move;
	}

	public Move useAlphaBeta(boolean curPlayer){

		originalDepth = 40;
		int depth = originalDepth;
		numberOfNodes = 0;

		int value = alphaBeta(depth, -999999, 999999, curPlayer);

		System.out.println("Value          : " + value);
		System.out.println("Number of Nodes: " + numberOfNodes);
		System.out.println("Move           : ");
		move.print();
		return move;		

	}

	//i vertical and j horizontal
	public void playCardHuman(int i, int j, int k, Vector<Move> moves){
		board[i][j] = k;
		player[i][j] = true;
		human.useCard(k);

		human.getCard(k).setBoardElement(elements[i][j]);

		Card humCard = human.getCard(k);
		Card compCard;

//		int q = 0;
//		//North
//		q = i - 1;
//		if(q >= 0){
//			if(board[q][j] != -1){
//				if(player[q][j] == false){
//					compCard = computer.getCard(board[q][j]);
//					if(compCard.notLost()){
//						if(humCard.getNorth() > compCard.getSouth()){
//							compCard.setLost(true);
//							moves.add(new Move(q,j,board[q][j]));
//						}
//					}
//				}else{
//					compCard = human.getCard(board[q][j]);
//					if(compCard.isLost()){
//						if(humCard.getNorth() > compCard.getSouth()){
//							compCard.setLost(false);
//							moves.add(new Move(q,j,board[q][j]));
//						}
//					}
//				}
//			}
//		}
//
//		//South
//		q = i + 1;
//		if(q < 3){
//			if(board[q][j] != -1){
//				if(player[q][j] == false){
//					compCard = computer.getCard(board[q][j]);
//					if(compCard.notLost()){
//						if(humCard.getSouth() > compCard.getNorth()){
//							compCard.setLost(true);
//							moves.add(new Move(q,j,board[q][j]));
//						}
//					}
//				}else{
//					compCard = human.getCard(board[q][j]);
//					if(compCard.isLost()){
//						if(humCard.getSouth() > compCard.getNorth()){
//							compCard.setLost(false);
//							moves.add(new Move(q,j,board[q][j]));
//						}
//					}
//				}
//			}
//		}
//
//		//West
//		q = j - 1;
//		if(q >= 0){
//			if(board[i][q] != -1){
//				if(player[i][q] == false){
//					compCard = computer.getCard(board[i][q]);
//					if(compCard.notLost()){
//						if(humCard.getWest() > compCard.getEast()){
//							compCard.setLost(true);
//							moves.add(new Move(i,q,board[i][q]));
//						}
//					}
//				}else{
//					compCard = human.getCard(board[i][q]);
//					if(compCard.isLost()){
//						if(humCard.getWest() > compCard.getEast()){
//							compCard.setLost(false);
//							moves.add(new Move(i,q,board[i][q]));
//						}
//					}
//				}
//			}
//		}
//
//		//East
//		q = j + 1;
//		if(q < 3){
//			if(board[i][q] != -1){
//				if(player[i][q] == false){
//					compCard = computer.getCard(board[i][q]);
//					if(compCard.notLost()){
//						if(humCard.getEast() > compCard.getWest()){
//							compCard.setLost(true);
//							moves.add(new Move(i,q,board[i][q]));
//						}
//					}
//				}else{
//					compCard = human.getCard(board[i][q]);
//					if(compCard.isLost()){
//						if(humCard.getEast() > compCard.getWest()){
//							compCard.setLost(false);
//							moves.add(new Move(i,q,board[i][q]));
//						}
//					}
//				}
//			}			
//		}

		if(sameRule || sameWall){

			Vector<Move> samega = new Vector<Move>();
			int sameCount = 0;
			int wallCount = 0;
			boolean atLeastOneDifferent = false; //One adjacent card must be different color from humCard

			int row = i - 1;
			int col = j;
			if(row >= 0){
				if(sameTest(Direction.NORTH, humCard, row, col)){
					if(player[row][col]){
						if(human.getCard(board[row][col]).isLost()){
							samega.add(new Move(row,col,board[row][col]));
							atLeastOneDifferent = true;
						}
					}else{
						if(computer.getCard(board[row][col]).notLost()){
							samega.add(new Move(row,col,board[row][col]));
							atLeastOneDifferent = true;
						}
					}
					++sameCount;
				}
			}else if(sameWall){// We know that row is negative now
				if(humCard.getNaturalNorth() == 10){
					++wallCount;
				}
			}

			row = i + 1;
			if(row < 3){	
				if(sameTest(Direction.SOUTH, humCard, row, col)){
					if(player[row][col]){
						if(human.getCard(board[row][col]).isLost()){
							samega.add(new Move(row,col,board[row][col]));
							atLeastOneDifferent = true;
						}
					}else{
						if(computer.getCard(board[row][col]).notLost()){
							samega.add(new Move(row,col,board[row][col]));
							atLeastOneDifferent = true;
						}
					}
					++sameCount;
				}
			}else if(sameWall){
				if(humCard.getNaturalSouth() == 10){
					++wallCount;
				}
			}

			row = i;
			col = j + 1;
			if(col < 3){
				if(sameTest(Direction.EAST, humCard, row, col)){
					if(player[row][col]){
						if(human.getCard(board[row][col]).isLost()){
							samega.add(new Move(row,col,board[row][col]));
							atLeastOneDifferent = true;
						}
					}else{
						if(computer.getCard(board[row][col]).notLost()){
							samega.add(new Move(row,col,board[row][col]));
							atLeastOneDifferent = true;
						}
					}
					++sameCount;
				}
			}else if(sameWall){
				if(humCard.getNaturalEast() == 10){
					++wallCount;
				}
			}

			col = j - 1;
			if(col >= 0){
				if(sameTest(Direction.WEST, humCard, row, col)){
					if(player[row][col]){
						if(human.getCard(board[row][col]).isLost()){
							samega.add(new Move(row,col,board[row][col]));
							atLeastOneDifferent = true;
						}
					}else{
						if(computer.getCard(board[row][col]).notLost()){
							samega.add(new Move(row,col,board[row][col]));
							atLeastOneDifferent = true;
						}
					}
					++sameCount;
				}
			}else if(sameWall){
				if(humCard.getNaturalWest() == 10){
					++wallCount;
				}
			}

			if(atLeastOneDifferent){
				if((sameCount >= 2) || (sameCount == 1 && wallCount >= 1)){
					Card c = null;
					//Then perform a combo using the cards from moves.get(sameIndex) to moves.get(moves.size() -1).
					for(int combo = 0; combo < samega.size(); ++combo){
						Move temp = samega.get(combo);
						if(diffColor(temp.getI(), temp.getJ(), true)){
							if(player[temp.getI()][temp.getJ()]){
								c = human.getCard(board[temp.getI()][temp.getJ()]);
							}else{
								c = computer.getCard(board[temp.getI()][temp.getJ()]);
							}
							c.setLost(!c.getLost());
							moves.add(new Move(temp.getI(), temp.getJ(), board[temp.getI()][temp.getJ()]));
							performCombo(samega.get(combo), true, moves);
						}

					}
				}
			}

		}

		if(plusRule){
			Vector<Move> plusega = new Vector<Move>();
			Card c = null;

			int row = i - 1;
			int col = j;
			if(row >= 0){
				if(board[row][col] != -1){
					if(player[row][col]){
						c = human.getCard(board[row][col]);
						plusega.add(new Move(row,col,c.getNaturalSouth() + humCard.getNaturalNorth()));
					}else{
						c = computer.getCard(board[row][col]);
						plusega.add(new Move(row,col,c.getNaturalSouth() + humCard.getNaturalNorth()));
					}
				}
			}

			row = i + 1;
			if(row < 3){
				if(board[row][col] != -1){
					if(player[row][col]){
						c = human.getCard(board[row][col]);
						plusega.add(new Move(row,col,c.getNaturalNorth() + humCard.getNaturalSouth()));
					}else{
						c = computer.getCard(board[row][col]);
						plusega.add(new Move(row,col,c.getNaturalNorth() + humCard.getNaturalSouth()));
					}
				}
			}

			row = i;
			col = j + 1;
			if(col < 3){
				if(board[row][col] != -1){
					if(player[row][col]){
						c = human.getCard(board[row][col]);
						plusega.add(new Move(row,col,c.getNaturalWest() + humCard.getNaturalEast()));
					}else{
						c = computer.getCard(board[row][col]);
						plusega.add(new Move(row,col,c.getNaturalWest() + humCard.getNaturalEast()));
					}
				}
			}

			col = j - 1;
			if(col >= 0){
				if(board[row][col] != -1){
					if(player[row][col]){
						c = human.getCard(board[row][col]);
						plusega.add(new Move(row,col,c.getNaturalEast() + humCard.getNaturalWest()));
					}else{
						c = computer.getCard(board[row][col]);
						plusega.add(new Move(row,col,c.getNaturalEast() + humCard.getNaturalWest()));
					}
				}
			}

			Collections.sort(plusega, new Move(0,0,0));
			if(debug){
				for(int uv = 0; uv < plusega.size(); ++uv){
					plusega.get(uv).print();
				}
			}

			int dupSize = 1;
			int lastDup = -1;
			for(int plus = 0; plus < plusega.size()-1; ++plus){
				if(debug){
					System.out.println("\tplusega.get(" + plus + ").getK() = " + plusega.get(plus).getK());
					System.out.println("\tplusega.get(" + (plus + 1) + ").getK() = " + plusega.get(plus + 1).getK());
				}
				if(plusega.get(plus).getK() == plusega.get(plus + 1).getK()){
					++dupSize;
					lastDup = plus + 1;
				}else{
					if(dupSize > 1){
						break;
					}
				}
			}
			
			if(debug){
				System.out.println("dupSize = " + dupSize);
			}

			//Handel dup from lastDup to lastDup - dupSize + 1.
			if(dupSize >= 2){
				if(debug){
					System.out.println("Found Duplicates! in human");
				}
				Card ca = null;
				for(int duplicates = lastDup; duplicates >= lastDup - dupSize + 1; --duplicates){
					Move temp = plusega.get(duplicates);
					if(debug){
						if(player[temp.getI()][temp.getJ()]){
							human.getCard(board[temp.getI()][temp.getJ()]).print();
						}else{
							computer.getCard(board[temp.getI()][temp.getJ()]).print();
						}						
						temp.print();
					}
					if(diffColor(temp.getI(), temp.getJ(), true)){
						if(player[temp.getI()][temp.getJ()]){
							ca = human.getCard(board[temp.getI()][temp.getJ()]);
						}else{
							ca = computer.getCard(board[temp.getI()][temp.getJ()]);
						}
						ca.setLost(!ca.getLost());
						moves.add(new Move(temp.getI(), temp.getJ(), board[temp.getI()][temp.getJ()]));
						if(debug){
							this.print();
						}
						performCombo(plusega.get(duplicates), true, moves);
						if(debug){
							this.print();
						}
					}
				}
			}

			//Check for second duplicate
			if(dupSize == 2 && lastDup == 1 && plusega.size() == 4){
				if(plusega.get(2).getK() == plusega.get(3).getK()){
					Card ca = null;
					for(int duplicates = 2; duplicates < 4; ++duplicates){
						Move temp = plusega.get(duplicates);
						if(diffColor(temp.getI(), temp.getJ(), true)){
							if(player[temp.getI()][temp.getJ()]){
								ca = human.getCard(board[temp.getI()][temp.getJ()]);
							}else{
								ca = computer.getCard(board[temp.getI()][temp.getJ()]);
							}
							if(debug){
								this.print();
							}
							ca.setLost(!ca.getLost());
							moves.add(new Move(temp.getI(), temp.getJ(), board[temp.getI()][temp.getJ()]));
							performCombo(plusega.get(duplicates), true, moves);
							if(debug){
								this.print();
							}
						}
					}
				}
			}

		}
		
		int q = 0;
		//North
		q = i - 1;
		if(q >= 0){
			if(board[q][j] != -1){
				if(player[q][j] == false){
					compCard = computer.getCard(board[q][j]);
					if(compCard.notLost()){
						if(humCard.getNorth() > compCard.getSouth()){
							compCard.setLost(true);
							moves.add(new Move(q,j,board[q][j]));
						}
					}
				}else{
					compCard = human.getCard(board[q][j]);
					if(compCard.isLost()){
						if(humCard.getNorth() > compCard.getSouth()){
							compCard.setLost(false);
							moves.add(new Move(q,j,board[q][j]));
						}
					}
				}
			}
		}

		//South
		q = i + 1;
		if(q < 3){
			if(board[q][j] != -1){
				if(player[q][j] == false){
					compCard = computer.getCard(board[q][j]);
					if(compCard.notLost()){
						if(humCard.getSouth() > compCard.getNorth()){
							compCard.setLost(true);
							moves.add(new Move(q,j,board[q][j]));
						}
					}
				}else{
					compCard = human.getCard(board[q][j]);
					if(compCard.isLost()){
						if(humCard.getSouth() > compCard.getNorth()){
							compCard.setLost(false);
							moves.add(new Move(q,j,board[q][j]));
						}
					}
				}
			}
		}

		//West
		q = j - 1;
		if(q >= 0){
			if(board[i][q] != -1){
				if(player[i][q] == false){
					compCard = computer.getCard(board[i][q]);
					if(compCard.notLost()){
						if(humCard.getWest() > compCard.getEast()){
							compCard.setLost(true);
							moves.add(new Move(i,q,board[i][q]));
						}
					}
				}else{
					compCard = human.getCard(board[i][q]);
					if(compCard.isLost()){
						if(humCard.getWest() > compCard.getEast()){
							compCard.setLost(false);
							moves.add(new Move(i,q,board[i][q]));
						}
					}
				}
			}
		}

		//East
		q = j + 1;
		if(q < 3){
			if(board[i][q] != -1){
				if(player[i][q] == false){
					compCard = computer.getCard(board[i][q]);
					if(compCard.notLost()){
						if(humCard.getEast() > compCard.getWest()){
							compCard.setLost(true);
							moves.add(new Move(i,q,board[i][q]));
						}
					}
				}else{
					compCard = human.getCard(board[i][q]);
					if(compCard.isLost()){
						if(humCard.getEast() > compCard.getWest()){
							compCard.setLost(false);
							moves.add(new Move(i,q,board[i][q]));
						}
					}
				}
			}			
		}


		++boardCount;
	}

	public boolean diffColor(int row, int col, boolean curPlayer){
		if(curPlayer){
			if(player[row][col]){
				if(human.getCard(board[row][col]).notLost()){
					return false;
				}
			}else{
				if(computer.getCard(board[row][col]).isLost()){
					return false;
				}
			}
		}else{
			if(player[row][col]){
				if(human.getCard(board[row][col]).isLost()){
					return false;
				}
			}else{
				if(computer.getCard(board[row][col]).notLost()){
					return false;
				}
			}
		}

		return true;
	}

	public void performCombo(Move move, boolean curPlayer, Vector<Move> moves){

		int row = move.getI() - 1;
		int col = move.getJ();
		Card c = null;
		Card init = null;
		if(player[move.getI()][move.getJ()]){
			init = human.getCard(board[move.getI()][move.getJ()]);
		}else{
			init = computer.getCard(board[move.getI()][move.getJ()]);
		}

		if(row >= 0){
			if(board[row][col] != -1){
				if(diffColor(row, col, curPlayer)){
					if(player[row][col]){
						c = human.getCard(board[row][col]);
					}else{
						c = computer.getCard(board[row][col]);
					}
					if(init.getNorth() > c.getSouth()){
						c.setLost(!c.getLost());

						moves.add(new Move(row, col, board[row][col]));

						performCombo(moves.lastElement(), curPlayer, moves);
					}
				}
			}
		}

		row = move.getI() + 1;
		if(row < 3){
			if(board[row][col] != -1){
				if(diffColor(row, col, curPlayer)){
					if(player[row][col]){
						c = human.getCard(board[row][col]);
					}else{
						c = computer.getCard(board[row][col]);
					}
					if(init.getSouth() > c.getNorth()){
						c.setLost(!c.getLost());

						moves.add(new Move(row, col, board[row][col]));

						performCombo(moves.lastElement(), curPlayer, moves);
					}
				}
			}
		}

		row = move.getI();
		col = move.getJ() + 1;
		if(col < 3){
			if(board[row][col] != -1){
				if(diffColor(row, col, curPlayer)){
					if(player[row][col]){
						c = human.getCard(board[row][col]);
					}else{
						c = computer.getCard(board[row][col]);
					}
					if(init.getEast() > c.getWest()){
						c.setLost(!c.getLost());

						moves.add(new Move(row, col, board[row][col]));

						performCombo(moves.lastElement(), curPlayer, moves);			
					}
				}
			}
		}

		col = move.getJ() - 1;
		if(col >= 0){
			if(board[row][col] != -1){
				if(diffColor(row, col, curPlayer)){
					if(player[row][col]){
						c = human.getCard(board[row][col]);
					}else{
						c = computer.getCard(board[row][col]);
					}
					if(init.getWest() > c.getEast()){
						c.setLost(!c.getLost());

						moves.add(new Move(row, col, board[row][col]));

						performCombo(moves.lastElement(), curPlayer, moves);			
					}
				}
			}
		}
	}

	public enum Direction {NORTH, EAST, SOUTH, WEST};

	public boolean sameTest(Direction direction, Card attacker, int row, int col){

		Card defender;
		int attack;
		int defense;

		if(board[row][col] != -1){

			if(player[row][col]){
				defender = human.getCard(board[row][col]);
			}else{
				defender = computer.getCard(board[row][col]);
			}

			if(direction == Direction.NORTH){
				attack = attacker.getNaturalNorth();
				defense = defender.getNaturalSouth();
			}else if(direction == Direction.EAST){
				attack = attacker.getNaturalEast();
				defense = defender.getNaturalWest();
			}else if(direction == Direction.SOUTH){
				attack = attacker.getNaturalSouth();
				defense = defender.getNaturalNorth();
			}else{
				attack = attacker.getNaturalWest();
				defense = defender.getNaturalEast();
			}

			if(attack == defense)
				return true;
		}

		return false;
	}

	public void playCardComputer(int i, int j, int k, Vector<Move> moves){
		board[i][j] = k;
		player[i][j] = false;
		computer.useCard(k);

		computer.getCard(k).setBoardElement(elements[i][j]);

		Card computerCard = computer.getCard(k);
		Card humanCard;
		
//		int q = 0;
//		//North
//		q = i - 1;
//		if(q >= 0){
//			if(board[q][j] != -1){
//				if(player[q][j] == true){
//					humanCard = human.getCard(board[q][j]);
//					if(humanCard.notLost()){
//						if(computerCard.getNorth() > humanCard.getSouth()){
//							humanCard.setLost(true);
//							moves.add(new Move(q,j,board[q][j]));
//						}
//					}
//				}else{
//					humanCard = computer.getCard(board[q][j]);
//					if(humanCard.isLost()){
//						if(computerCard.getNorth() > humanCard.getSouth()){
//							humanCard.setLost(false);
//							moves.add(new Move(q,j,board[q][j]));
//						}
//					}
//				}
//			}
//		}
//
//
//		//South
//		q = i + 1;
//		if(q < 3){
//			if(board[q][j] != -1){
//				if(player[q][j] == true){
//					humanCard = human.getCard(board[q][j]);
//					if(humanCard.notLost()){
//						if(computerCard.getSouth() > humanCard.getNorth()){
//							humanCard.setLost(true);
//							moves.add(new Move(q,j,board[q][j]));
//						}
//					}
//				}else{
//					humanCard = computer.getCard(board[q][j]);
//					if(humanCard.isLost()){
//						if(computerCard.getSouth() > humanCard.getNorth()){
//							humanCard.setLost(false);
//							moves.add(new Move(q,j,board[q][j]));
//						}
//					}
//				}
//			}		
//		}
//
//		//West
//		q = j - 1;
//		if(q >= 0){
//			if(board[i][q] != -1){
//				if(player[i][q] == true){
//					humanCard = human.getCard(board[i][q]);
//					if(humanCard.notLost()){
//						if(computerCard.getWest() > humanCard.getEast()){
//							humanCard.setLost(true);
//							moves.add(new Move(i,q,board[i][q]));
//						}
//					}
//				}else{
//					humanCard = computer.getCard(board[i][q]);
//					if(humanCard.isLost()){
//						if(computerCard.getWest() > humanCard.getEast()){
//							humanCard.setLost(false);
//							moves.add(new Move(i,q,board[i][q]));
//						}
//					}
//				}
//			}
//		}
//
//		//East
//		q = j + 1;
//		if(q < 3){
//			if(board[i][q] != -1){
//				if(player[i][q] == true){
//					humanCard = human.getCard(board[i][q]);
//					if(humanCard.notLost()){
//						if(computerCard.getEast() > humanCard.getWest()){
//							humanCard.setLost(true);
//							moves.add(new Move(i,q,board[i][q]));
//						}
//					}
//				}else{
//					humanCard = computer.getCard(board[i][q]);
//					if(humanCard.isLost()){
//						if(computerCard.getEast() > humanCard.getWest()){
//							humanCard.setLost(false);
//							moves.add(new Move(i,q,board[i][q]));
//						}
//					}
//				}
//			}			
//		}

		if(sameRule || sameWall){

			Vector<Move> samega = new Vector<Move>();
			int sameCount = 0;
			int wallCount = 0;
			boolean atLeastOneDifferent = false; //One adjacent card must be different color from humCard

			int row = i - 1;
			int col = j;
			if(row >= 0){
				if(sameTest(Direction.NORTH, computerCard, row, col)){
					if(player[row][col]){
						if(human.getCard(board[row][col]).notLost()){
							samega.add(new Move(row,col,board[row][col]));
							atLeastOneDifferent = true;
						}
					}else{
						if(computer.getCard(board[row][col]).isLost()){
							samega.add(new Move(row,col,board[row][col]));
							atLeastOneDifferent = true;
						}
					}
					++sameCount;
				}
			}else if(sameWall){// We know that row is negative now
				if(computerCard.getNaturalNorth() == 10){
					++wallCount;
				}
			}

			row = i + 1;
			if(row < 3){	
				if(sameTest(Direction.SOUTH, computerCard, row, col)){
					if(player[row][col]){
						if(human.getCard(board[row][col]).notLost()){
							samega.add(new Move(row,col,board[row][col]));
							atLeastOneDifferent = true;
						}
					}else{
						if(computer.getCard(board[row][col]).isLost()){
							samega.add(new Move(row,col,board[row][col]));
							atLeastOneDifferent = true;
						}
					}
					++sameCount;
				}
			}else if(sameWall){
				if(computerCard.getNaturalSouth() == 10){
					++wallCount;
				}
			}

			row = i;
			col = j + 1;
			if(col < 3){
				if(sameTest(Direction.EAST, computerCard, row, col)){
					if(player[row][col]){
						if(human.getCard(board[row][col]).notLost()){
							samega.add(new Move(row,col,board[row][col]));
							atLeastOneDifferent = true;
						}
					}else{
						if(computer.getCard(board[row][col]).isLost()){
							samega.add(new Move(row,col,board[row][col]));
							atLeastOneDifferent = true;
						}
					}
					++sameCount;
				}
			}else if(sameWall){
				if(computerCard.getNaturalEast() == 10){
					++wallCount;
				}
			}

			col = j - 1;
			if(col >= 0){
				if(sameTest(Direction.WEST, computerCard, row, col)){
					if(player[row][col]){
						if(human.getCard(board[row][col]).notLost()){
							samega.add(new Move(row,col,board[row][col]));
							atLeastOneDifferent = true;
						}
					}else{
						if(computer.getCard(board[row][col]).isLost()){
							samega.add(new Move(row,col,board[row][col]));
							atLeastOneDifferent = true;
						}
					}
					++sameCount;
				}
			}else if(sameWall){
				if(computerCard.getNaturalWest() == 10){
					++wallCount;
				}
			}

			if(atLeastOneDifferent){
				if((sameCount >= 2) || (sameCount == 1 && wallCount >= 1)){
					Card c = null;
					//Then perform a combo using the cards from moves.get(sameIndex) to moves.get(moves.size() -1).
					for(int combo = 0; combo < samega.size(); ++combo){

						Move temp = samega.get(combo);
						if(diffColor(temp.getI(), temp.getJ(), false)){
							if(player[temp.getI()][temp.getJ()]){
								c = human.getCard(board[temp.getI()][temp.getJ()]);
							}else{
								c = computer.getCard(board[temp.getI()][temp.getJ()]);
							}
							c.setLost(!c.getLost());
							moves.add(new Move(temp.getI(), temp.getJ(), board[temp.getI()][temp.getJ()]));
							performCombo(samega.get(combo), false, moves);
						}

					}
				}
			}

		}

		if(plusRule){
			Vector<Move> plusega = new Vector<Move>();
			Card c = null;

			int row = i - 1;
			int col = j;
			if(row >= 0){
				if(board[row][col] != -1){
					if(player[row][col]){
						c = human.getCard(board[row][col]);
						plusega.add(new Move(row,col,c.getNaturalSouth() + computerCard.getNaturalNorth()));
					}else{
						c = computer.getCard(board[row][col]);
						plusega.add(new Move(row,col,c.getNaturalSouth() + computerCard.getNaturalNorth()));
					}
				}
			}

			row = i + 1;
			if(row < 3){
				if(board[row][col] != -1){
					if(player[row][col]){
						c = human.getCard(board[row][col]);
						plusega.add(new Move(row,col,c.getNaturalNorth() + computerCard.getNaturalSouth()));
					}else{
						c = computer.getCard(board[row][col]);
						plusega.add(new Move(row,col,c.getNaturalNorth() + computerCard.getNaturalSouth()));
					}
				}
			}

			row = i;
			col = j + 1;
			if(col < 3){
				if(board[row][col] != -1){
					if(player[row][col]){
						c = human.getCard(board[row][col]);
						plusega.add(new Move(row,col,c.getNaturalWest() + computerCard.getNaturalEast()));
					}else{
						c = computer.getCard(board[row][col]);
						plusega.add(new Move(row,col,c.getNaturalWest() + computerCard.getNaturalEast()));
					}
				}
			}

			col = j - 1;
			if(col >= 0){
				if(board[row][col] != -1){
					if(player[row][col]){
						c = human.getCard(board[row][col]);
						plusega.add(new Move(row,col,c.getNaturalEast() + computerCard.getNaturalWest()));
					}else{
						c = computer.getCard(board[row][col]);
						plusega.add(new Move(row,col,c.getNaturalEast() + computerCard.getNaturalWest()));
					}
				}
			}

			Collections.sort(plusega, new Move(0,0,0));
			if(debug){
				for(int uv = 0; uv < plusega.size(); ++uv){
					plusega.get(uv).print();
				}
			}

			int dupSize = 1;
			int lastDup = -1;
			for(int plus = 0; plus < plusega.size()-1; ++plus){
				if(debug){
					System.out.println("\tplusega.get(" + plus + ").getK() = " + plusega.get(plus).getK());
					System.out.println("\tplusega.get(" + (plus + 1) + ").getK() = " + plusega.get(plus + 1).getK());
				}
				if(plusega.get(plus).getK() == plusega.get(plus + 1).getK()){
					++dupSize;
					lastDup = plus + 1;
				}else{
					if(dupSize > 1){
						break;
					}
				}
			}
			
			if(debug){
				System.out.println("dupSize = " + dupSize);
			}

			//Handel dup from lastDup to lastDup - dupSize + 1.
			if(dupSize >= 2){
				if(debug){
					System.out.println("Found Duplicates! in computer");
				}
				Card ca = null;
				for(int duplicates = lastDup; duplicates >= lastDup - dupSize + 1; --duplicates){
					Move temp = plusega.get(duplicates);
					if(debug){
						if(player[temp.getI()][temp.getJ()]){
							human.getCard(board[temp.getI()][temp.getJ()]).print();
						}else{
							computer.getCard(board[temp.getI()][temp.getJ()]).print();
						}						
						temp.print();
					}
					if(diffColor(temp.getI(), temp.getJ(), false)){
						if(player[temp.getI()][temp.getJ()]){
							ca = human.getCard(board[temp.getI()][temp.getJ()]);
						}else{
							ca = computer.getCard(board[temp.getI()][temp.getJ()]);
						}
						ca.setLost(!ca.getLost());
						if(debug){
							this.print();
						}
						moves.add(new Move(temp.getI(), temp.getJ(), board[temp.getI()][temp.getJ()]));
						performCombo(plusega.get(duplicates), false, moves);
						if(debug){
							this.print();
						}
					}
				}
			}

			//Check for second duplicate
			if(dupSize == 2 && lastDup == 1 && plusega.size() == 4){
				if(plusega.get(2).getK() == plusega.get(3).getK()){
					Card ca = null;
					for(int duplicates = 2; duplicates < 4; ++duplicates){
						Move temp = plusega.get(duplicates);
						if(diffColor(temp.getI(), temp.getJ(), false)){
							if(player[temp.getI()][temp.getJ()]){
								ca = human.getCard(board[temp.getI()][temp.getJ()]);
							}else{
								ca = computer.getCard(board[temp.getI()][temp.getJ()]);
							}
							ca.setLost(!ca.getLost());
							if(debug){
								this.print();
							}
							moves.add(new Move(temp.getI(), temp.getJ(), board[temp.getI()][temp.getJ()]));
							performCombo(plusega.get(duplicates), false, moves);
							if(debug){
								this.print();
							}
						}
					}
				}
			}

		}
		
		int q = 0;
		//North
		q = i - 1;
		if(q >= 0){
			if(board[q][j] != -1){
				if(player[q][j] == true){
					humanCard = human.getCard(board[q][j]);
					if(humanCard.notLost()){
						if(computerCard.getNorth() > humanCard.getSouth()){
							humanCard.setLost(true);
							moves.add(new Move(q,j,board[q][j]));
						}
					}
				}else{
					humanCard = computer.getCard(board[q][j]);
					if(humanCard.isLost()){
						if(computerCard.getNorth() > humanCard.getSouth()){
							humanCard.setLost(false);
							moves.add(new Move(q,j,board[q][j]));
						}
					}
				}
			}
		}


		//South
		q = i + 1;
		if(q < 3){
			if(board[q][j] != -1){
				if(player[q][j] == true){
					humanCard = human.getCard(board[q][j]);
					if(humanCard.notLost()){
						if(computerCard.getSouth() > humanCard.getNorth()){
							humanCard.setLost(true);
							moves.add(new Move(q,j,board[q][j]));
						}
					}
				}else{
					humanCard = computer.getCard(board[q][j]);
					if(humanCard.isLost()){
						if(computerCard.getSouth() > humanCard.getNorth()){
							humanCard.setLost(false);
							moves.add(new Move(q,j,board[q][j]));
						}
					}
				}
			}		
		}

		//West
		q = j - 1;
		if(q >= 0){
			if(board[i][q] != -1){
				if(player[i][q] == true){
					humanCard = human.getCard(board[i][q]);
					if(humanCard.notLost()){
						if(computerCard.getWest() > humanCard.getEast()){
							humanCard.setLost(true);
							moves.add(new Move(i,q,board[i][q]));
						}
					}
				}else{
					humanCard = computer.getCard(board[i][q]);
					if(humanCard.isLost()){
						if(computerCard.getWest() > humanCard.getEast()){
							humanCard.setLost(false);
							moves.add(new Move(i,q,board[i][q]));
						}
					}
				}
			}
		}

		//East
		q = j + 1;
		if(q < 3){
			if(board[i][q] != -1){
				if(player[i][q] == true){
					humanCard = human.getCard(board[i][q]);
					if(humanCard.notLost()){
						if(computerCard.getEast() > humanCard.getWest()){
							humanCard.setLost(true);
							moves.add(new Move(i,q,board[i][q]));
						}
					}
				}else{
					humanCard = computer.getCard(board[i][q]);
					if(humanCard.isLost()){
						if(computerCard.getEast() > humanCard.getWest()){
							humanCard.setLost(false);
							moves.add(new Move(i,q,board[i][q]));
						}
					}
				}
			}			
		}

		++boardCount;
	}

	public void undoMoves(Vector<Move> moves, int q, int j, int k){

		board[q][j] = -1;
		if(player[q][j]){
			human.unUseCard(k);
			human.getCard(k).unSetElement();
		}else{
			computer.unUseCard(k);
			computer.getCard(k).unSetElement();
		}
		--boardCount;

		Move m = null;
		Card c = null;
		for(int i = 0; i < moves.size(); ++i){
			m = moves.get(i);
			if(player[m.getI()][m.getJ()]){
				c = human.getCard(m.getK());
			}else{
				c = computer.getCard(m.getK());
			}
			c.setLost(!c.getLost());
		}
	}
	
	public static void printRules(){
		System.out.println("Same     : " + sameRule);
		System.out.println("Plus     : " + plusRule);
		System.out.println("Same Wall: " + sameWall);
	}

	public static void setRules(){
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File("rules")));
			sameRule = Boolean.parseBoolean(br.readLine());
			plusRule = Boolean.parseBoolean(br.readLine());
			sameWall = Boolean.parseBoolean(br.readLine());
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}

	}

	boolean isTerminal(){
		return boardCount == 9;
	}

	private int alphaBeta(int depth, int alpha, int beta, boolean curPlayer){

		++numberOfNodes;

		if(isTerminal() || depth == 0 ){
			return boardValue(curPlayer);
		}

		for(int i = 0; i < 3; ++i){
			for(int j = 0; j < 3; ++j){
				if(board[i][j] == -1){
					for(int k = 0; k < 5; ++k){

						Vector<Move> changed = new Vector<Move>();
						if(curPlayer){
							if(!human.canUseCard(k))
								continue;
							playCardHuman(i,j,k,changed);
						}else{
							if(!computer.canUseCard(k))
								continue;
							playCardComputer(i,j,k,changed);
						}

						int value = -alphaBeta(depth - 1, -beta, -alpha, !curPlayer);

						undoMoves(changed,i,j,k);

						if(value >= beta){
							return beta;
						}
						if(value > alpha){
							if(depth == originalDepth){
								move = new Move(i,j,k);
							}
							alpha = value;
						}

					}
				}
			}
		}

		return alpha;		
	}

	int negaMax(int depth, boolean curPlayer){
		//		try {
		//			Thread.sleep(100);
		//		} catch (InterruptedException e) {
		//			// TODO Auto-generated catch block
		//			e.printStackTrace();
		//		}
		++numberOfNodes;
		System.out.println(numberOfNodes);
		//		print();
		//		System.out.print("Current Player is: ");
		//		if(curPlayer)
		//			System.out.println("human");
		//		else
		//			System.out.println("computer");
		//		System.out.println("Board value: " + this.boardValue(curPlayer));
		//		System.out.println();

		if(isTerminal() || depth == 0 ){
			return boardValue(curPlayer);
		}

		int alpha = -99999;
		for(int i = 0; i < 3; ++i){
			for(int j = 0; j < 3; ++j){
				if(board[i][j] == -1){
					for(int k = 0; k < 5; ++k){

						Vector<Move> changed = new Vector<Move>();
						if(curPlayer){
							if(!human.canUseCard(k))
								continue;
							playCardHuman(i,j,k,changed);
						}else{
							if(!computer.canUseCard(k))
								continue;
							playCardComputer(i,j,k,changed);
						}

						int value = -negaMax(depth - 1, !curPlayer);
						if(value > alpha){
							if(depth == originalDepth)
								move = new Move(i,j,k);
							alpha = value;
						}

						undoMoves(changed,i,j,k);

					}
				}
			}
		}

		return alpha;
	}

	int minimax(int depth, boolean curPlayer){

		++numberOfNodes;

		if(isTerminal() || depth == 0)
			return boardValue(true);

		int alpha = 0;
		if(curPlayer){
			//Iterate through all possible board values
			alpha = -999999;
			for(int i = 0; i < 3; ++i){
				for(int j = 0; j < 3; ++j){
					if(board[i][j] == -1){ //Free space to choose where to put piece
						for(int k = 0; k < 5; ++k){
							if(human.canUseCard(k)){
								Vector<Move> changed = new Vector<Move>();
								playCardHuman(i,j,k,changed);
								int value = minimax(depth - 1, !curPlayer);
								if(value > alpha){
									if(originalDepth == depth){
										move = new Move(i,j,k);
									}
									alpha = value;
								}
								undoMoves(changed, i, j, k);
							}
						}
					}
				}
			}
		}else{
			alpha = 9999999;
			for(int i = 0; i < 3; ++i){
				for(int j = 0; j < 3; ++j){
					if(board[i][j] == -1){
						//Now put card here for computer
						//Iterate through available card list of computer hand
						for(int k = 0; k < 5; ++k){
							if(computer.canUseCard(k)){
								Vector<Move> changed = new Vector<Move>();
								playCardComputer(i,j,k,changed);
								int value = minimax(depth - 1, !curPlayer);
								if(value < alpha){
									alpha = value;
								}
								undoMoves(changed, i, j, k);
							}
						}
					}
				}
			}
		}

		return alpha;
	}

	private String compSpread(int spread){
		String str = "";
		for(int i = 0; i < spread; ++i){
			str = str + " ";
		}
		return str;
	}

	private String getLine(int length){
		String str = "";
		for(int i = 0 ; i < length; ++i){
			str = str + "-";
		}
		return str;
	}

	public void makeMove(Move move, boolean curPlayer){
		if(curPlayer)
			playCardHuman(move.getI(), move.getJ(), move.getK(), new Vector<Move>());
		else
			playCardComputer(move.getI(), move.getJ(), move.getK(), new Vector<Move>());
	}

	public Move makeMove(){
		Vector<Move> moves = new Vector<Move>();
		for(int i = 0; i < 3; ++i){
			for(int j = 0; j < 3; ++j){
				if(board[i][j] == -1){
					for(int k = 0; k < 5; ++k){
						if(computer.canUseCard(k)){
							moves.add(new Move(i,j,k));
							//							playCardComputer(i,j,k);
							//							return new Move(i,j,k);
						}
					}
				}
			}
		}

		Random random = new Random(System.currentTimeMillis());
		Move move = moves.get(random.nextInt(moves.size()));
		playCardComputer(move.getI(), move.getJ(), move.getK(), new Vector<Move>());

		return move;
	}

	public boolean isValidMove(Move move, boolean curPlayer){
		if(move.getI() < 0 || move.getI() >= 5)
			return false;

		if(move.getJ() < 0 || move.getJ() >= 5)
			return false;

		if(move.getK() < 0 || move.getK() >= 5)
			return false;

		//		move.print();
		//		System.out.println(human.canUseCard(move.getK()));
		//		System.out.println(board[move.getI()][move.getJ()]);

		if(curPlayer){
			if(board[move.getI()][move.getJ()] == -1 && human.canUseCard(move.getK()))
				return true;
		}else{
			if(board[move.getI()][move.getJ()] == -1 && computer.canUseCard(move.getK()))
				return true;
		}

		return false;	
	}

	public boolean isValidMove(Move move){
		if(move.getI() < 0 || move.getI() >= 5)
			return false;

		if(move.getJ() < 0 || move.getJ() >= 5)
			return false;

		if(move.getK() < 0 || move.getK() >= 5)
			return false;

		//		move.print();
		//		System.out.println(human.canUseCard(move.getK()));
		//		System.out.println(board[move.getI()][move.getJ()]);

		if(board[move.getI()][move.getJ()] == -1 && human.canUseCard(move.getK()))
			return true;

		return false;
	}

	public void makeMove(Move move){
		playCardHuman(move.getI(), move.getJ(), move.getK(), new Vector<Move>());
	}
	
	private String getPlayerCardRep(Player curplayer){
		int initDist = 3;
		int northDistance = 11;
		int weDistance = 5;
		int nameDist = 3;
		String nameSpread = compSpread(nameDist);
		String initSpread = compSpread(initDist);
		String northSpread = compSpread(northDistance);
		String weSpread = compSpread(weDistance);

		String nameRep = "";
		String northRep = initSpread;
		String weRep = "";
		String southRep = initSpread;
		for(int j = 0; j < 5; ++j){
			Card c = curplayer.getCard(j);
			
			if(curplayer.notUsed(j)){
				nameRep = nameRep + convertToAcrynom(c.getElement());
				nameRep = nameRep + c.getName() + "(" + j + ")";
				nameRep = nameRep + nameSpread;
				northRep = northRep + c.getNaturalNorth() + northSpread;
				weRep = weRep + c.getWest() + weSpread + c.getEast() + weSpread;
				southRep = southRep + c.getSouth() + northSpread;
				
			}
		}

		return nameRep + "\n" + northRep + "\n" + weRep + "\n" + southRep;		
	}
	
	//NONE, FIRE, ICE, WATER, HOLY, THUNDER, EARTH, POISON, WIND
	private String convertToAcrynom(Elemental element){
		String str = "{";
		if(element == Elemental.NONE){
			return "";
		}else if(element == Elemental.FIRE){
			str = str + "FI";
		}else if(element == Elemental.ICE){
			str = str + "IC";
		}else if(element == Elemental.WATER){
			str = str + "WA";
		}else if(element == Elemental.HOLY){
			str = str + "HO";
		}else if(element == Elemental.THUNDER){
			str = str + "TH";
		}else if(element == Elemental.EARTH){
			str = str + "EA";
		}else if(element == Elemental.POISON){
			str = str + "PO";
		}else if(element == Elemental.WIND){
			str = str + "WI";
		}else{
			str = str + "Error: unrecognized element!";
		}
		
		return str + "}";
	}

	private String getRowRep(int row){

		int initDist = 3;
		int northDistance = 11;
		int weDistance = 5;
		int nameDist = 3;
		String nameSpread = compSpread(nameDist);
		String initSpread = compSpread(initDist);
		String northSpread = compSpread(northDistance);
		String weSpread = compSpread(weDistance);

		Card [] rowCards = new Card[3];
		for(int j = 0; j < 3; ++j){
			if(board[row][j] != -1){
				if(player[row][j]){
					rowCards[j] = human.getCard(board[row][j]);
				}else{
					rowCards[j] = computer.getCard(board[row][j]);
				}
			}else{
				rowCards[j] = null;
			}
		}
		String nameRep = "";
		String northRep = initSpread;
		String weRep = "";
		String southRep = initSpread;
		for(int j = 0; j < 3; ++j){
			nameRep = nameRep + convertToAcrynom(elements[row][j]);
			if(rowCards[j] != null){
				nameRep = nameRep + rowCards[j].getName();
				if(player[row][j]){
					if(rowCards[j].isLost()){
						nameRep = nameRep + "[C]";
					}else{
						nameRep = nameRep + "[P]";
					}
				}else{
					if(rowCards[j].isLost()){
						nameRep = nameRep + "[P]";
					}else{
						nameRep = nameRep + "[C]";
					}
				}
				nameRep = nameRep + nameSpread;
				northRep = northRep + rowCards[j].getNorth() + northSpread;
				weRep = weRep + rowCards[j].getWest() + weSpread + rowCards[j].getEast() + weSpread;
				southRep = southRep + rowCards[j].getSouth() + northSpread;
			}else{
				nameRep = nameRep + "NO CARD" + nameSpread;
				northRep = northRep + 0 + northSpread;
				weRep = weRep + 0 + weSpread + 0 + weSpread;
				southRep = southRep + 0 + northSpread;
			}
		}

		return nameRep + "\n" + northRep + "\n" + weRep + "\n" + southRep;
	}

	public void print(){
		
		System.out.println("Board:");
		int lineLengthSeperator = 40;
		for(int i = 0; i < 3; ++i){
			System.out.println(getRowRep(i));
			System.out.println(getLine(lineLengthSeperator));
		}
		
	}
	
	public void printPlayerCards(){
		System.out.println("Your Cards");
		System.out.println(getPlayerCardRep(human));
		
		System.out.println("Computer Cards");
		System.out.println(getPlayerCardRep(computer));		
	}
	
	public Player getHuman(){
		return human;
	}
	
	public Player getComputer(){
		return computer;
	}

}
