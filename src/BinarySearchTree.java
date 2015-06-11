
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

	@Override
	public String toString() {
		return "SearchResult [gp=" + gp + ", p=" + p + ", l=" + l
				+ ", pupdate=" + pupdate + ", gpupdate=" + gpupdate + "]";
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
			pupdate = p.getUpdate();
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
		
		while (true){
			SearchResult search_result = search(key);
//			Thread thread = Thread.currentThread();
//			System.out.println(thread.getName() + " search " + key + ", with result\n" + search_result.toString());
			Leaf l = (Leaf) search_result.l;
			Internal p = search_result.p;
			int[] state = new int[1];
			Info info = search_result.pupdate.update.get(state);
			// int state = pupdate.update.getStamp();
			// Info info = pupdate.update.getReference();

			// Cannot insert duplicate key
			if (l.key == key)
				return false;
			// Help the other operation
			if (state[0] != 0){
				help(info, state);
			} else {
				newSibling = new Leaf(l.key);
				if (new_leaf.key < newSibling.key){
					newInternal = new Internal(Math.max(key, l.key), new Update(), new_leaf, newSibling);
				} else {
					newInternal = new Internal(Math.max(key, l.key), new Update(), newSibling, new_leaf);
				}
				op = new IInfo(p, newInternal, l);
				// iflag CAS
				// TODO: the CAS it must return the old value of p.update
				boolean result_cas = p.update.update.compareAndSet(info, op, state[0], 1);
				if (result_cas){
					helpInsert(op);
					return true;
				} else {
					System.out.println("CAS failed");
					Info new_info = search_result.pupdate.update.get(state);
					help(new_info, state);
				}
			}
			
		}
	}
	
	public void helpInsert(IInfo op){
		casChild(op.p, op.l, op.newInterval);
		op.p.update.update.compareAndSet(op, op, 1, 0);
	}
	
	public void help(Info info, int[] state){
		if (state[0] == 1){
			helpInsert((IInfo) info);
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
	
	public void printTree(){
		System.out.println(Root.toString());
	}

}
