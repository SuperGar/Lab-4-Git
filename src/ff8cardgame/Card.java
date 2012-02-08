package ff8cardgame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Vector;

public class Card {

	private static Vector<Card> allCards = readAllCards();

	private String name;

	private int north;
	private int east;
	private int south;
	private int west;

	private Board.Elemental element;  
	private int elementEffect;        //0 for not in effect at all, -1 for effect down, +1 for effect up

	private boolean isLost;

	public Card(){

	}

	public Card(int north, int east, int south, int west){
		this.north = north;
		this.east = east;
		this.south = south;
		this.west = west;

		this.elementEffect = 0;
		this.element = Board.Elemental.NONE;
		this.isLost = false;
	}
	
	public Card(Card card){
		this.name = new String(card.name);
		this.north = card.north;
		this.east = card.east;
		this.south = card.south;
		this.west = card.west;
		this.elementEffect = card.elementEffect;
		this.element = card.element;
		this.isLost = card.isLost;
	}

	public Card(String name, int north, int east, int south, int west){
		this(north,east,south,west);
		this.name = name;
	}

	public Card(int north, int east, int south, int west, Board.Elemental element){
		this(north,east,south,west);
		this.setElement(element);
		this.isLost = false;
	}

	public void setElement(Board.Elemental element){
		this.element = element;
	}

	public void setBoardElement(Board.Elemental element){
		if(element == Board.Elemental.NONE)
			this.elementEffect = 0;
		else if(element == this.element){
			this.elementEffect = 1;
		}else{
			this.elementEffect = -1;
		}
	}

	public void unSetElement(){
		this.elementEffect = 0;
	}

	public void print(){
		System.out.println("Card " + name + ": (" + north + "," +
				east + "," + south + "," + west + ")");
	}

	public boolean notLost(){
		return this.isLost == false;
	}

	public boolean isLost(){
		return this.isLost == true;
	}
	
	private static Card convertToCard(String line){
		try{
			Card card = new Card();
			Scanner scanner = new Scanner(line);
			card.setName(scanner.next());
			card.setNorth(scanner.nextInt());
			card.setEast(scanner.nextInt());
			card.setSouth(scanner.nextInt());
			card.setWest(scanner.nextInt());

			String element = scanner.next();
			Board.Elemental elem = Board.Elemental.NONE;
			if(element.equals("NONE")){
				elem = Board.Elemental.NONE;
			}else if(element.equals("FIRE")){
				elem = Board.Elemental.FIRE;
			}else if(element.equals("ICE")){
				elem = Board.Elemental.ICE;
			}else if(element.equals("WATER")){
				elem = Board.Elemental.WATER;
			}else if(element.equals("HOLY")){
				elem = Board.Elemental.HOLY;
			}else if(element.equals("THUNDER")){
				elem = Board.Elemental.THUNDER;
			}else if(element.equals("EARTH")){
				elem = Board.Elemental.EARTH;
			}else if(element.equals("POISON")){
				elem = Board.Elemental.POISON;
			}else if(element.equals("WIND")){
				elem = Board.Elemental.WIND;
			}else{
				System.out.println("Error: Improper card format elemental");
				System.out.println("Line: " + line);
				System.out.println("\tat Elemental: (" + element + ")");
				System.exit(1);
			}
			card.setElement(elem);
			
			return card;
			
		} catch(InputMismatchException e){
			System.out.println("Error: not an intger expression");
			System.exit(1);
		} catch(NoSuchElementException e){
			System.out.println("Error: input is exhausted");
			System.exit(1);
		} catch(IllegalStateException e){
			System.exit(1);
			System.out.println("Error: Illegal State Exception");
		}

		return null;
	}
	
	public static Card findCard(String cardName){
		int cardIndex = 0;
		while(cardIndex < allCards.size()){
			if(allCards.get(cardIndex).getName().equals(cardName)){
				break;
			}
			++cardIndex;
		}
		
		if(cardIndex == allCards.size()){
			System.out.println("Error: Non Valid Card Name (" + cardName + ")");
			System.exit(1);
		}
		
		return new Card(allCards.get(cardIndex));
	}

	private static Vector<Card> readAllCards(){
		try {
			
			BufferedReader br = new BufferedReader(new FileReader(new File("allCards")));
			
			Vector<Card> cards = new Vector<Card>();
			for(int i = 0; i < 110; ++i){
				String line = br.readLine();
				cards.add(convertToCard(line));
			}
			
			br.close();
		
			
			return cards;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(1);
		}

		//Should not reach this code
		System.exit(1);
		return null;
	}

	public static Vector<Card> getAllCards(){
		return allCards;
	}

	public boolean getLost(){
		return this.isLost;
	}

	public void setLost(boolean lost){
		this.isLost = lost;
	}

	public int getNaturalNorth(){
		return north;
	}

	public int getNaturalEast(){
		return east;
	}

	public int getNaturalSouth(){
		return south;
	}

	public int getNaturalWest(){
		return west;
	}

	public int getNorth() {
		return north + elementEffect;
	}

	public int getEast() {
		return east + elementEffect;
	}

	public int getSouth() {
		return south + elementEffect;
	}

	public int getWest() {
		return west + elementEffect;
	}

	public String getName(){
		return name;
	}

	public Board.Elemental getElement() {
		return element;
	}

	public boolean isElemental() {
		return element != Board.Elemental.NONE;
	}

	public void setNorth(int north) {
		this.north = north;
	}

	public void setEast(int east) {
		this.east = east;
	}

	public void setSouth(int south) {
		this.south = south;
	}

	public void setWest(int west) {
		this.west = west;
	}

	public void setName(String name){
		this.name = name;
	}
}
