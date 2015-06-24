import java.util.Random;
/**
 * Class for the leaf of the tree. It inherits the key from the superclass Node.
 * 
 * @author Davide Spadini
 *
 */

public class Leaf extends Node{
	Random rn = new Random();
	
	public Leaf(int key) {
		super(key);
	}

	@Override
	public String toString() {
		return "Leaf " + key;
	}

}
