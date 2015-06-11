
public class DInfo extends Info{
	Internal gp;
	Internal p;
	Leaf l;
	Update pupdate;
	
	
	public DInfo(Internal gp, Internal p, Leaf l, Update pupdate) {
		super();
		this.gp = gp;
		this.p = p;
		this.l = l;
		this.pupdate = pupdate;
	}

	public DInfo() {
		gp = null;
		p = null;
		l = null;
		pupdate = null;
	}

}
