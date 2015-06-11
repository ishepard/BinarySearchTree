import java.util.Random;


public class Leaf extends Node{
	Random rn = new Random();
	
	public Leaf(int key) {
		super(key);
	}

	public Leaf() {
		key = rn.nextInt();
	}

	@Override
	public String toString() {
		return "Leaf " + key;
	}

}
