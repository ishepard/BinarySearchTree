import java.util.List;
/**
 * This class contains the code executed by the threads.
 * 
 * @author Davide Spadini
 *
 */

public class TreeThread implements Runnable{
	BinarySearchTree bst;
	private List<Actions> actions;

	public TreeThread(BinarySearchTree bst, List<Actions> actions) {
		super();
		this.actions = actions;
		this.bst = bst;
	}

	/**
	 * Code executed by the threads. It checks which operation has to do, and it calls
	 * the relative function (insert, delete or find) on the binary search tree.
	 * 
	 */

	public void run(){
		Thread thread = Thread.currentThread();
		for(Actions act: actions){
			switch(act.action) {
			    case "insert":
			    	System.out.println(thread.getName() + " trying to insert " + act.value);
			    	bst.insert(act.value);
			    	break;
			    case "delete":
			    	System.out.println(thread.getName() + " trying to deleting " + act.value);
			    	bst.delete(act.value);
			    	break;
			    case "find":
			    	System.out.println(thread.getName() + " trying to find " + act.value);
			    	Leaf f = bst.find(act.value);
			    	if (f instanceof Leaf){
			    		System.out.println(thread.getName() + " has found key " + f.key);
			    	} else {
			    		System.out.println(thread.getName() + " didn't found key " + act.value + " found");
			    	}
			    	break;
			    default:
		             throw new IllegalArgumentException("Invalid Argument");
			}
        }
	}
}
