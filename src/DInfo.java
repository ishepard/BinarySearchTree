/**
 * Class that stores the necessary informations to complete a delete.
 * Specifically, the leaf to be deleted, its parent, its grandparent, and 
 * an Update field containing the state and info fields of the parent
 * 
 * @author Davide Spadini
 *
 */
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
