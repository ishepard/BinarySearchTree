import java.util.ArrayList;
import java.util.List;

class Actions {
	String action;
	int value;
	public Actions(String action, int value) {
		super();
		this.action = action;
		this.value = value;
	}
}

public class Main {

	public static void main(String[] args) throws InterruptedException {
		BinarySearchTree bst = new BinarySearchTree();
		
		List<Actions> list_t1 = new ArrayList<Actions>();
		list_t1.add(new Actions("insert", 1));
		list_t1.add(new Actions("insert", 2));
		list_t1.add(new Actions("insert", 7));
		
		List<Actions> list_t2 = new ArrayList<Actions>();
		list_t2.add(new Actions("insert", 8));
		list_t2.add(new Actions("insert", 3));
		list_t2.add(new Actions("insert", 4));
        
		Thread t1 = new Thread(new TreeThread(bst, list_t1));
		Thread t2 = new Thread(new TreeThread(bst, list_t2));

		t1.start();
		t2.start();

		t1.join();
		t2.join();
		
		bst.printTree();
		
		
	}

}