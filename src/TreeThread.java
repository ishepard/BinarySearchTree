import java.util.List;


public class TreeThread implements Runnable{
	BinarySearchTree bst;
	private List<Actions> actions;

	public TreeThread(BinarySearchTree bst, List<Actions> actions) {
		super();
		this.actions = actions;
		this.bst = bst;
	}


	public void run(){
		Thread thread = Thread.currentThread();
		for(Actions act: actions){
			// System.out.println("Key is " + act.action + "and value is " + act.value);
			switch(act.action) {
			    case "insert":
			    	System.out.println(thread.getName() + " tring to insert " + act.value);
			    	bst.insert(act.value);
			    	break;
			    case "delete":
			    	System.out.println(thread.getName() + " tring to deleting " + act.value);
			    	bst.delete(act.value);
			    	break;
			    default:
		             throw new IllegalArgumentException("Invalid Argument");
			}
//			bst.printTree();
        }
	}
}
