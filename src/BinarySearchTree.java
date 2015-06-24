
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

public class BinarySearchTree {
	
	// Create Root, shared variable
	Update update_root;
	Leaf dummy_1;
	Leaf dummy_2;
	Internal Root;

	public BinarySearchTree() {
		this.update_root = new Update();
		this.dummy_1 = new Leaf(Integer.MAX_VALUE - 1);
		this.dummy_2 = new Leaf(Integer.MAX_VALUE);
		this.Root = new Internal(Integer.MAX_VALUE, update_root, dummy_1, dummy_2);
	}

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
	
	public Leaf find(int key){
		SearchResult res = search(key);
		if (res.l.key == key)
			return (Leaf) res.l;
		else
			return null;
	}
	
	// Insert a new Key in the tree
	public boolean insert(int key){
		Internal newInternal;
		Leaf newSibling;
		Leaf new_leaf = new Leaf(key);  
		IInfo op;
		Thread thread = Thread.currentThread();
		
		while (true){
			SearchResult search_result = search(key);
			Leaf l = (Leaf) search_result.l;
			Internal p = search_result.p;
			Update pupdate = search_result.pupdate;

			// Cannot insert duplicate key
			if (l.key == key){
				System.out.println("Key already exist");
				return false;
			}
			// Help the other operation
			if (pupdate.state != "CLEAN"){
				System.out.println("INSERT " + key + ": found pupdate.state != Clean, calling help!");
				help(pupdate);
			} else {
				newSibling = new Leaf(l.key);
				if (new_leaf.key < newSibling.key){
					newInternal = new Internal(Math.max(key, l.key), new Update(), new_leaf, newSibling);
				} else {
					newInternal = new Internal(Math.max(key, l.key), new Update(), newSibling, new_leaf);
				}
				op = new IInfo(p, newInternal, l);
				Update new_update = new Update("CLEAN", op);

				// iflag CAS
				boolean cas_result = p.update.compareAndSet(pupdate, new_update);
				if (cas_result){
					helpInsert(op);
					System.out.println(thread.getName() + " inserted correctly key " + key);
					return true;
				} else {
					help(p.update.get());
				}
			}
			
		}
	}
	
	public void helpInsert(IInfo op){
		Update old_update = op.p.update.get();
		Update new_update = new Update("CLEAN", op);
		casChild(op.p, op.l, op.newInterval);
		if (old_update.state == "IFLAG" && old_update.info == op){
			op.p.update.compareAndSet(old_update, new_update);
		}
	}
	
	public void help(Update u){
		System.out.println(u.state);
		if (u.state == "IFLAG"){
			helpInsert((IInfo) u.info);
		} else if (u.state == "MARK"){
			helpMarked((DInfo) u.info);
		} else if (u.state == "DFLAG"){
			helpDelete((DInfo) u.info);
		}
	}
	
	public void casChild(Internal parent, Node old, Node new_node){
		// Tries to change one of the child fields of the node that parent points to from old to new.
		if (new_node.key < parent.key){
			parent.left.compareAndSet(old, new_node);
		} else {
			parent.right.compareAndSet(old, new_node);
		}
	}
	
	public boolean delete(int key){
		Thread thread = Thread.currentThread();
		
		while (true){
			SearchResult search_result = search(key);
			Leaf l = (Leaf) search_result.l;
			Internal p = search_result.p;
			Internal gp = search_result.gp;
			Update pupdate = search_result.pupdate;
			Update gpupdate = search_result.gpupdate;
			
			// TODO: check if key is root or dummy
			if (l.key != key){
				System.out.println(thread.getName() + " not found key " + key);
				return false;
			}
			if (gpupdate.state != "CLEAN"){
//				System.out.println("DELETE " + key + ": found gpupdate.state != Clean, calling help!");
				help(gpupdate);
			} else if (pupdate.state != "CLEAN"){
//				System.out.println("DELETE " + key + ": found pupdate.state != Clean, calling help!");
				help(pupdate);
			} else {
				DInfo op = new DInfo(gp, p, l, pupdate);
				boolean cas_result = gp.update.compareAndSet(gpupdate, new Update("DFLAG", op));
				if (cas_result){
					if (helpDelete(op))
						System.out.println(thread.getName() + " deleted correctly key " + key);
						return true;
				} else {
					help(gp.update.get());
				}
			}
		}
	}
	
	public boolean helpDelete(DInfo op){
		Update old_gp_update = op.gp.update.get();
		Update new_gp_update = new Update("CLEAN", op);
		Update new_update_mark = new Update("MARK", op);
		boolean cas_result = op.p.update.compareAndSet(op.pupdate, new_update_mark);
		if (cas_result){
			helpMarked(op);
			return true;
		} else {
			help(op.p.update.get());
			if (old_gp_update.state == "DFLAG" && old_gp_update.info == op){
				op.gp.update.compareAndSet(old_gp_update, new_gp_update);
			}
			return false;
		}
	}
	
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

	public void printTree(){
		System.out.println(Root.toString());
	}
	
	public void toDot(Node i){
		if (i instanceof Internal){
			Internal l = (Internal) i;
			if (l.left.get() != null){
				System.out.println(l.key + " -> " + l.left.get().key + ";");
				toDot((Node) l.left.get());
			}
			if (l.right.get() != null){
				System.out.println(l.key + " -> " + l.right.get().key + ";");
				toDot((Node) l.right.get());
			}
		}
	}

}
