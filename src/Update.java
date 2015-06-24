
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