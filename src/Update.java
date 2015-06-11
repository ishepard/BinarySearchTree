import java.util.concurrent.atomic.AtomicStampedReference;

public class Update {
	// AtomicStampedReference: reference to an object of Type Info and 
	// an integer stamp that means:
	// 0: CLEAN
	// 1: DFLAG
	// 2: IFLAG
	// 3: MARK
	final static int CLEAN = 0, DFLAG = 1, IFLAG = 2, MARK = 3;
	AtomicStampedReference<Info> update;
	
	public Update(Info info, int stamp) {
		super();
		this.update = new AtomicStampedReference<Info>(info, stamp);
	}

	public Update(){
		update = new AtomicStampedReference<Info>(null, CLEAN);
	}
}