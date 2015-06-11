import java.util.Random;

public class Node {
	int key;
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

	public void setKey(int key) {
		this.key = key;
	}

}
