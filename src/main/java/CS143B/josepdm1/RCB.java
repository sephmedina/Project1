package CS143B.josepdm1;

import java.util.LinkedList;
import java.util.Queue;

public class RCB {
	//0 - allocated, 1 - free
	public static final int ALLOCATED = 0;
	public static final int FREE = 1;
	private Integer state;
	
	//list of processes blocked/waiting for this resource
	private Queue<Integer> waitlist = null;
	
	public RCB() {
		state = FREE;
		waitlist = new LinkedList<Integer>();
	}
	
	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public Queue<Integer> getWaitlist() {
		return waitlist;
	}
	
}
