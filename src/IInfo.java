/**
 * Class that stores the necessary informations to complete an insert.
 * Specifically, the leaf that is to be replaced, that leaf's parent and 
 * the newly created subtree that will be used to replace the leaf.
 * 
 * @author Davide Spadini
 *
 */

public class IInfo extends Info{
	Internal p;
	Internal newInterval;
	Leaf l;
	
	public IInfo(Internal p, Internal newInterval, Leaf l) {
		super();
		this.p = p;
		this.newInterval = newInterval;
		this.l = l;
	}
	
	public IInfo(){
		p = null;
		newInterval = null;
		l = null;
	}

}
