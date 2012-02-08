package ff8cardgame;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class FF8UIReader {

	public static Card [] readAllCards(){
		Card [] cards = new Card[5];
		for(int number = 1; number <= 5; ++number){
			cards[number-1] = readCard(number);
		}
		return cards;
	}

	public static Card readCard(int number){
		System.out.println("Read Card (" + number + ")");
		String name = readName();
		InputStreamReader input = new InputStreamReader(System.in);
		int north = readPower("North",input);
		int east = readPower("East",input);
		int south = readPower("South",input);
		int west = readPower("West",input);
		System.out.println("Card Number " + number + " was read succesfully");
		Card card = new Card(north,east,south,west);
		card.setName(name);
		return card;
	}
	
	public static boolean readTurn(){
		try {
			BufferedReader br = new BufferedReader(new FileReader("whosTurn"));
			Scanner scanner = new Scanner(br.readLine());
			br.close();
			return scanner.nextBoolean();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Error: whosTurn file is wrong!");
		System.exit(1);
		return false;
	}

	public static Player readInFile(String fileName){
	
		Card [] cards = new Card[5];
	
		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
	
			for(int i = 0; i < 5; ++i){
				String line = br.readLine();
				Scanner scanner = new Scanner(line);
				String name = scanner.next();
				int north  = scanner.nextInt();
				int east  = scanner.nextInt();
				int south  = scanner.nextInt();
				int west = scanner.nextInt();
				cards[i] = new Card(name,north,east,south,west);
			}
			
			br.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch(InputMismatchException e){
			System.out.println("Error: not an intger expression");
			System.exit(1);
		} catch(NoSuchElementException e){
			System.out.println("Error: input is exhausted");
			System.exit(1);
		} catch(IllegalStateException e){
			System.out.println("Error: Illegal State Exception");
			System.exit(1);
		}
	
		return new Player(cards);
	}

	public static String readName(){
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		boolean validRead = true;
	
		String line = "";
		do{
			System.out.print("Please input a name:");
			try {
				line = br.readLine();
				validRead = true;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("IO Exception error");
				validRead = false;
			}
		}while(!validRead);
	
		return line;
	}

	public static int readPower(String message, Reader reader){
		BufferedReader br = new BufferedReader(reader);
		boolean validRead = true;
	
		int value = 0;
		System.out.println(message + " read");
		do{
			String line;
			try {
				System.out.print("Input a number between 1 and 10 inclusive: ");
				line = br.readLine();
				value = Integer.parseInt(line);
				if(value >= 1 && value <= 10){
					validRead = true;
					System.out.println("success");
				}else{
					validRead = false;
					System.out.println("error: " + message + " out of range");
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				validRead = false;
				System.out.println("io exception occured");
			} catch (NumberFormatException e){
				validRead = false;
				System.out.println("improper string input");
			}
		}while(!validRead);
	
		return value;
	}

	public static void readSomePlayers(){
		System.out.println("Read in Your Cards");
		Player human = new Player(readAllCards());
		System.out.println("Read in the computers Cards");
		Player computer = new Player(readAllCards());
	}

}
