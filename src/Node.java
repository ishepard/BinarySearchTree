import java.util.Random;

public class Node {
	final int key;
	Random rn = new Random();

	public Node(int key) {
		super();
		this.key = key;
	}
	
	public Node(){
		this.key = rn.nextInt();
	}

	public int getKey() {
		return key;
	}

}
