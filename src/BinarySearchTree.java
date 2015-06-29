import java.io.PrintWriter;

/**
 * Class used by the search method to save the result.
 * 
 * @author shepard
 *
 */
class SearchResult{
	Internal gp, p;
	Node l;
	Update pupdate, gpupdate;

	public SearchResult(Internal gp, Internal p, Node l, Update pupdate, 
			Update gpupdate) {
		super();
		this.gp = gp;
		this.p = p;
		this.l = l;
		this.pupdate = pupdate;
		this.gpupdate = gpupdate;
	}
};

/**
 * BinarySearchTree class is the main class of our concurrent Binary Search Tree.
 * It provides three principle methods: find(key), insert(key), delete(key). This implementation 
 * is non-blocking: starting from any configuration of any infinite asynchronous execution, 
 * with any number of crash failures, some operation always completes. 
 * There are also two possibilities to print the tree:
 * 1) OCaml syntax;
 * 2) Dot format;
 * 
 * @author Spadini Davide
 * @version 1
 */

public class BinarySearchTree {
	// Create Root, shared variable
	Update update_root;
	Leaf dummy_1;
	Leaf dummy_2;
	Internal Root;
	
	/**
	 *  Initialize the BinarySearchTree Root
	 */
	public BinarySearchTree() {
		this.update_root = new Update();
		this.dummy_1 = new Leaf(Integer.MAX_VALUE - 1);
		this.dummy_2 = new Leaf(Integer.MAX_VALUE);
		this.Root = new Internal(Integer.MAX_VALUE, update_root, dummy_1, dummy_2);
	}

	/**
	 * Used by Insert, Delete and Find to traverse the BST searching (if it exists) the key.
	 * 
	 * @param key Key to search
	 * @return SearchResult Structure containing the leaf, and other informations about the parent and grandparent of it 
	 */
	public SearchResult search(int key){
		Internal gp = null;
		Internal p = null;
		Node l = Root;
		Update gpupdate = new Update();
		Update pupdate = new Update();
		
		while (l instanceof Internal){
			gp = p;
			p = (Internal) l;
			gpupdate = pupdate;
			pupdate = p.update.get();
			if (key < l.getKey()){
				l = p.left.get();
			} else {
				l = p.right.get();
			}
		}
		
		return new SearchResult(gp, p, l, pupdate, gpupdate);
	}
	
	/**
	 * Used to find the key.
	 * 
	 * @param key	Key to search
	 * @return 		The corresponding leaf if the key is in the tree, null otherwise
	 */
	public Leaf find(int key){
		SearchResult res = search(key);
		if (res.l.key == key)
			return (Leaf) res.l;
		else
			return null;
	}
	
	/**
	 * Used to insert the key. First of all it checks if the key already exists (duplicated keys are not admitted in the search tree),
	 * then it checks if there are other pending operations on the same nodes that he needs to 
	 * operate on, in this case it tries to complete them, otherwise it builds a new structure 
	 * with the key. 
	 * After that it tries to change the state of its parent to "IFLAG", if this fails it 
	 * helps the operation that has flagged or marked the parent, if any, and begins a new attempt. If the "IFLAG" succeeds, 
	 * the rest of the insertion is done by HelpInsert.
	 * 
	 * @param key	Key to insert
	 * @return		true if it succeeds, false otherwise
	 */
	public boolean insert(int key){
		Internal newInternal;
		Leaf newSibling;
		Leaf new_leaf = new Leaf(key);  
		Thread thread = Thread.currentThread();
		
		while (true){
			SearchResult search_result = search(key);
			Leaf l = (Leaf) search_result.l;
			Internal p = search_result.p;
			Update pupdate = search_result.pupdate;

			// Cannot insert duplicate key
			if (l.key == key){
				System.out.println(thread.getName() + " can not insert duplicate key! " + key +" already exist");
				return false;
			}
			// Help the completion of other operations
			if (pupdate.state != "CLEAN"){
				help(pupdate);
			} else {
				newSibling = new Leaf(l.key);
				if (new_leaf.key < newSibling.key){
					newInternal = new Internal(Math.max(key, l.key), new Update(), new_leaf, newSibling);
				} else {
					newInternal = new Internal(Math.max(key, l.key), new Update(), newSibling, new_leaf);
				}
				IInfo op = new IInfo(p, newInternal, l);
				Update new_update = new Update("IFLAG", op);

				// iflag CAS
				boolean cas_result = p.update.compareAndSet(pupdate, new_update);
				if (cas_result){
					// Finish the insertion
					helpInsert(op);
					System.out.println(thread.getName() + " inserted correctly key " + key);
					return true;
				} else {
					System.out.println(thread.getName() + " failed the iflag CAS; let's help the operation that caused failure");
					help(p.update.get());
				}
			}
			
		}
	}
	
	/**
	 * Used by the insert function in order to complete the insertion of a key.
	 * It attempts to change the appropriate child pointer of the parent 
	 * and it resets to "CLEAN" its update field.
	 * 
	 * @param op	IInfo record that carries on the necessary information for the completion of the insert
	 */
	public void helpInsert(IInfo op){
		Update old_update = op.p.update.get();
		Update new_update = new Update("CLEAN", op);
		
		// change the appropriate child pointer of the parent
		casChild(op.p, op.l, op.newInterval);
		
		if (old_update.state == "IFLAG" && old_update.info == op){
			// Resets to "CLEAN" the update field of the parent
			op.p.update.compareAndSet(old_update, new_update);
		}
	}
	
	/**
	 * General-purpose helping routine. This method checks the state field of the Update
	 * and calls the corresponding function in order to help and possibly finish the pending operation
	 * 
	 * @param u	Contains the state and the info record of the pending operation
	 */
	
	public void help(Update u){
		if (u.state == "IFLAG"){
			helpInsert((IInfo) u.info);
		} else if (u.state == "MARK"){
			helpMarked((DInfo) u.info);
		} else if (u.state == "DFLAG"){
			helpDelete((DInfo) u.info);
		}
	}
	
	/**
	 * Atomically swap the pointer of the parent from the old node to the new one.
	 * Furthermore, it determines whether to change the left or right child of the parent, 
	 * depending on the key values.
	 * 
	 * @param parent	Parent node
	 * @param old	Old node
	 * @param new_node	New node
	 */
	public void casChild(Internal parent, Node old, Node new_node){
		// Tries to change one of the child fields of the node that parent points to from old to new.
		if (new_node.key < parent.key){
			parent.left.compareAndSet(old, new_node);
		} else {
			parent.right.compareAndSet(old, new_node);
		}
	}
	
	/**
	 * Used to delete a key. It first calls the search method to find the leaf to be deleted 
	 * (and its parent and grandparent). If it fails to find the key returns False. If it finds 
	 * that some other operation has already flagged or marked the parent or grandparent, 
	 * it helps that operation complete and then begins over with a new attempt. Otherwise, 
	 * it creates a new DInfo record containing the necessary information and attempts to set the flag 
	 * of the grandparent to "DFLAG". If this fails, it helps the operation that has flagged or 
	 * marked the grandparent, if any, and then begins a new attempt. If the "DFLAG" is successful,
	 * the remainder of the deletion is carried out by HelpDelete.
	 * 
	 * However, it is possible that the attempted deletion will fail to complete even after the 
	 * grandparent is flagged (if some other operation has changed the parent’s state and info 
	 * fields before it can be marked), so HelpDelete returns a boolean value that describes 
	 * whether the deletion was successfully completed. If it is successful, it returns True; 
	 * otherwise, it tries again.
	 * 
	 * @param key	Key to delete
	 * @return		true if it succeeds, false otherwise
	 */
	public boolean delete(int key){
		Thread thread = Thread.currentThread();
		
		while (true){
			SearchResult search_result = search(key);
			Leaf l = (Leaf) search_result.l;
			Internal p = search_result.p;
			Internal gp = search_result.gp;
			Update pupdate = search_result.pupdate;
			Update gpupdate = search_result.gpupdate;
			
			// Key is not in the tree
			if (l.key != key){
				System.out.println(thread.getName() + " didn't find key " + key);
				return false;
			}
			// Help the completion of other operations
			if (gpupdate.state != "CLEAN"){
				help(gpupdate);
			} else if (pupdate.state != "CLEAN"){
				help(pupdate);
			} else {
				DInfo op = new DInfo(gp, p, l, pupdate);
				Update new_gpupdate = new Update("DFLAG", op);
				
				// dflag CAS
				boolean cas_result = gp.update.compareAndSet(gpupdate, new_gpupdate);
				if (cas_result){
					// CAS successful
					// Either finish deletion or unflag
					if (helpDelete(op))
						System.out.println(thread.getName() + " deleted correctly key " + key);
						return true;
				} else {
					// The dflag CAS failed; help the operation that caused the failure
					System.out.println(thread.getName() + " failed the dflag CAS; let's help the operation that caused failure");
					help(gp.update.get());
				}
			}
		}
	}
	
	/**
	 * Used by the delete function in order to proceed with the deletion of a key.
	 * First it attempts to mark the parent of the leaf to be deleted, if it is
	 * successful, the remainder of the deletion is carried out by HelpMarked, otherwise it helps 
	 * the operation that flagged the parent node and performs a backtrack: it removes the flag 
	 * from the grandparent.
	 * 
	 * @param op	DInfo record that carries on the necessary information for the completion of the delete
	 * @return		true if it marks the parent correctly and the operation can go on, false otherwise
	 */
	
	public boolean helpDelete(DInfo op){
		Update old_gp_update = op.gp.update.get();
		Update new_gp_update = new Update("CLEAN", op);
		Update new_update_mark = new Update("MARK", op);
		
		// mark the parent
		boolean cas_result = op.p.update.compareAndSet(op.pupdate, new_update_mark);
		if (cas_result){
			// parent marked correctly, complete the deletion
			// Tell Delete routine it is done
			helpMarked(op);
			return true;
		} else {
			// The mark CAS failed, so help operation that caused failure,
			// backtrack CAS and tell Delete routine to try again
			help(op.p.update.get());
			if (old_gp_update.state == "DFLAG" && old_gp_update.info == op){
				op.gp.update.compareAndSet(old_gp_update, new_gp_update);
			}
			return false;
		}
	}
	
	/**
	 * Used by the helpDelete in order to complete the deletion of a key.
	 * It changes the appropriate child pointer of the grandparent and it resets 
	 * to "CLEAN" its update field.
	 * 
	 * @param op	DInfo record that carries on the necessary information for the completion of the delete
	 */
	
	public void helpMarked(DInfo op){
		Update old_gp_update = op.gp.update.get();
		Update new_gp_update = new Update("CLEAN", op);
		Node other;
		if (op.p.right.get() == op.l){
			other = op.p.left.get();
		} else {
			other = op.p.right.get();
		}
		casChild(op.gp, op.p, other);
		if (old_gp_update.state == "DFLAG" && old_gp_update.info == op){
			op.gp.update.compareAndSet(old_gp_update, new_gp_update);
		}
	}
	
	/**
	 * Print the current tree in OCaml syntax
	 * 
	 */

	public void printTree(){
		System.out.println(Root.toString());
	}
	
	/**
	 * Print the current tree in dot (graph description language). It saves the result
	 * in the external file tree.dot.
	 * 
	 */
	
	public void createDotGraph(){
		PrintWriter out = null;
		try {
			out = new PrintWriter("tree.dot");
			out.write("digraph graphname {\n");
			toDot(Root, out);
			out.write("}");
	    }catch (Exception e){
	    	System.out.println(e);
	    } finally {
	    	out.close();
      }
	}
	
	/**
	 * Used by createDotGraph: recursively print the left and right child of the node.
	 * 
	 * @param i		Current node
	 * @param out	File in which the function has to write.
	 */
	public void toDot(Node i, PrintWriter out){
		if (i instanceof Internal){
			Internal l = (Internal) i;
			if (l.left.get() != null){
				if (l.left.get() instanceof Leaf){
					out.write(l.key + " -> \"Leaf " + l.left.get().key + "\";\n");
				} else {
					out.write(l.key + " -> " + l.left.get().key + ";\n");
				}
				toDot((Node) l.left.get(), out);
			}
			if (l.right.get() != null){
				if (l.right.get() instanceof Leaf){
					out.write(l.key + " -> \"Leaf " + l.right.get().key + "\";\n");
				} else{
					out.write(l.key + " -> " + l.right.get().key + ";\n");
				}
				toDot((Node) l.right.get(), out);
			}
		} else if (i instanceof Leaf){
			out.write("\"Leaf " + i.key + "\"\t[label="+ i.key + ", shape=box, regular=1, color=\"blue\"];\n");
		}
	}

}
