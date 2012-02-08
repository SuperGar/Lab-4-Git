package ff8cardgame;
import java.util.Comparator;


public class Move implements Comparator {

	private int i;
	private int j;
	private int k;
	
	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public Move(int i, int j, int k){
		this.i = i;
		this.j = j;
		this.k = k;
	}

	public int getI() {
		return i;
	}

	public int getJ() {
		return j;
	}

	public void setI(int i) {
		this.i = i;
	}

	public void setJ(int j) {
		this.j = j;
	}
	
	public void print(){
		System.out.println("(" + i + "," + j + "," + k + ")");
	}

	@Override
	public int compare(Object arg0, Object arg1) {
		// TODO Auto-generated method stub
		Move m1 = (Move) arg0;
		Move m2 = (Move) arg1;
		
		return m1.k - m2.k;
	}

//	@Override
//	public int compareTo(Object o) {
//		// TODO Auto-generated method stub
//		Move move = (Move) o;
//		
//		return move.k - k;
//	}

//@Override
//public int compare(Object arg0, Object arg1) {
//	// TODO Auto-generated method stub
//	return 0;
//}
	
	
	
}
