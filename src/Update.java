/**
 * Class that contains the state and info fields. 
 * The state has one of four possible values: CLEAN, MARK, IFLAG or DFLAG, and is
 * initially CLEAN. It is used to coordinate actions between updates acting 
 * on the same part of the tree.
 * 
 * 
 * @author Davide Spadini
 *
 */
public class Update {
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