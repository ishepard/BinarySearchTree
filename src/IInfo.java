

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
