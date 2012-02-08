package ff8cardgame;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Vector;


public class Player {

	private Card [] cards;
	private boolean [] used;
	
	public Player(){
		
	}

	public Player(Card [] cards){
		this.setCards(cards);
		this.initUsed();
	}
	
	public void initUsed(){
		used = new boolean[5];
		for(int i = 0; i < 5; ++i){
			used[i] = false;
		}
	}

	public int numLost(){
		int count = 0;
		for(int i = 0; i < 5; ++i){
			if(cards[i].isLost())
				++count;
		}
		return count;
	}

	public void loseCard(int k){
		cards[k].setLost(true);
	}

	public Card getCard(int k){
		return cards[k];
	}

	public void unLoseCard(int k){
		cards[k].setLost(false);
	}

	public Card [] getCards(){
		return cards;
	}

	public void setCards(Card [] cards){
		if(cards.length != 5)
			System.exit(1);
		this.cards = cards;
	}

	public boolean canUseCard(int i){
		return !used[i];
	}

	public void useCard(int i){
		used[i] = true;	
	}
	
	public boolean isUsed(int i){
		return used[i] == true;
	}
	
	public boolean notUsed(int i){
		return used[i] == false;
	}

	public void unUseCard(int i){
		used[i] = false;
	}

	public void printCards(){
		for(int i = 0; i < 5; ++i){
			if(!used[i]){
				System.out.print("(" + i + ") ");
				cards[i].print();
			}
		}
	}
	
	public void loadCards(String fileName){
		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(fileName)));
			Card [] loaded = new Card[5];
			for(int i = 0; i < 5; ++i){
				loaded[i] = Card.findCard(br.readLine());
			}
			this.setCards(loaded);
			this.initUsed();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void randomize(){
		Random random = new Random(System.currentTimeMillis());
		Card [] rand = new Card[5];
		Vector<Card> allCards = Card.getAllCards();
		for(int i = 0; i < 5; ++i){
			rand[i] = allCards.get(random.nextInt(allCards.size()));
		}
		this.setCards(rand);
		this.initUsed();
	}

}
