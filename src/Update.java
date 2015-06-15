
public class Update {
	// AtomicStampedReference: reference to an object of Type Info and 
	// an integer stamp that means:
	// 0: CLEAN
	// 1: DFLAG
	// 2: IFLAG
	// 3: MARK
	String state;
	Info info;
	
	public Update(String state, Info info) {
		super();
		this.state = state;
		this.info = info;
	}

	public Update() {
		super();
		this.state = "CLEAN";
		this.info = null;
	}
}