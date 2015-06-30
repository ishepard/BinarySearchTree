import java.util.ArrayList;
import java.util.List;

/**
 * Class used for passing the actions to the threads.
 * Contains two variables:
 * - String action: one of "insert", "delete", "find"
 * - int value: value of the key
 * 
 * @author shepard
 *
 */
class Actions {
	String action;
	int value;
	public Actions(String action, int value) {
		super();
		this.action = action;
		this.value = value;
	}
}

/**
 * Main of the BinarySearchTree. In order to test it, I create 5 threads and each one has to
 * insert, delete or find some keys concurrently. At the end of the main I print the tree.
 * For other informations, @see BinarySearchTree documentation
 * 
 * @author Davide Spadini
 *
 */

public class Main {

	public static void main(String[] args) throws InterruptedException {
		BinarySearchTree bst = new BinarySearchTree();
		
		List<Actions> list_t1 = new ArrayList<Actions>();
		list_t1.add(new Actions("insert", 1));
		list_t1.add(new Actions("delete", 1));
		list_t1.add(new Actions("insert", 2));
		list_t1.add(new Actions("insert", 7));
		list_t1.add(new Actions("find", 15));
		list_t1.add(new Actions("delete", 8));
		
		List<Actions> list_t2 = new ArrayList<Actions>();
		list_t2.add(new Actions("insert", 8));
		list_t2.add(new Actions("insert", 3));
		list_t2.add(new Actions("delete", 2));
		list_t2.add(new Actions("find", 11));
		list_t2.add(new Actions("delete", 1));
		list_t2.add(new Actions("find", 1));
		list_t2.add(new Actions("find", 20));
		
		List<Actions> list_t3 = new ArrayList<Actions>();
		list_t3.add(new Actions("insert", 10));
		list_t3.add(new Actions("insert", 20));
		list_t3.add(new Actions("delete", 25));
		list_t3.add(new Actions("insert", 35));
		list_t3.add(new Actions("insert", 40));
		list_t3.add(new Actions("delete", 10));
		list_t3.add(new Actions("find", 40));
		
		List<Actions> list_t4 = new ArrayList<Actions>();
		list_t4.add(new Actions("insert", 11));
		list_t4.add(new Actions("find", 8));
		list_t4.add(new Actions("insert", 22));
		list_t4.add(new Actions("insert", 33));
		list_t4.add(new Actions("find", 15));
		list_t4.add(new Actions("delete", 44));
		list_t4.add(new Actions("delete", 22));
		list_t4.add(new Actions("find", 22));
		
		List<Actions> list_t5 = new ArrayList<Actions>();
		list_t5.add(new Actions("insert", 15));
		list_t5.add(new Actions("find", 11));
		list_t5.add(new Actions("insert", 25));
		list_t5.add(new Actions("insert", 35));
		list_t5.add(new Actions("delete", 25));
		list_t5.add(new Actions("delete", 20));
		list_t5.add(new Actions("find", 8));
        
		Thread t1 = new Thread(new TreeThread(bst, list_t1));
		Thread t2 = new Thread(new TreeThread(bst, list_t2));
		Thread t3 = new Thread(new TreeThread(bst, list_t3));
		Thread t4 = new Thread(new TreeThread(bst, list_t4));
		Thread t5 = new Thread(new TreeThread(bst, list_t5));

		t1.start();
		t2.start();
		t3.start();
		t4.start();
		t5.start();
		
		t1.join();
		t2.join();
		t3.join();
		t4.join();
		t5.join();

		System.out.println("All the threads have finished their operations. Printing the tree...");
		bst.printTree();
		
		try {
			bst.createDotGraph();
		} catch (Exception exc){
			System.out.println(exc.toString());
		}
		
	}

}
