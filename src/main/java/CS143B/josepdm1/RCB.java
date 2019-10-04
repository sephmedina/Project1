package CS143B.josepdm1;

import javafx.util.Pair;

import java.util.LinkedList;
import java.util.Queue;

public class RCB {
	private Integer state;
	private int inventory;

	//list of processes blocked/waiting for this resource
	private Queue<Pair<Integer, Integer>> waitlist;

	public RCB(int inventory) {
		this.inventory = inventory;
		this.state = inventory;
		waitlist = new LinkedList<Pair<Integer, Integer>>();
	}
	public Queue<Pair<Integer, Integer>> getWaitlist() {
		return waitlist;
	}

	public Integer getState() {
		return state;
	}
	public void setState(Integer state) {
		this.state = state;
	}
	public void reduceCount() {
		--state;
	}
	public void increaseCount() {
		++state;
	}


}
