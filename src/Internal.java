import java.util.concurrent.atomic.AtomicReference;

public class Internal extends Node{
	Update update;
	AtomicReference<Node> left;
	AtomicReference<Node> right;

	public Internal(int key, Update update, Node left, Node right) {
		super(key);
		this.update = update;
		this.left = new AtomicReference<Node>(left);
		this.right = new AtomicReference<Node>(right);
	}
	
	public Internal() {
		super();
		this.update = null;
		this.left = new AtomicReference<Node>(null);
		this.right = new AtomicReference<Node>(null);
	}

	public Update getUpdate() {
		return update;
	}

	public void setUpdate(Update update) {
		this.update = update;
	}

	public AtomicReference<Node> getLeft() {
		return left;
	}

	public void setLeft(AtomicReference<Node> left) {
		this.left = left;
	}

	public AtomicReference<Node> getRight() {
		return right;
	}

	public void setRight(AtomicReference<Node> right) {
		this.right = right;
	}

	@Override
	public String toString() {
		return "Node(" + key + ", " + left.toString() + ", "
				+ right.toString() + ")";
	}	
}