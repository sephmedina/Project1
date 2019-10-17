package CS143B.josepdm1;

import CS143B.josepdm1.Exceptions.RCBException;
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

	@Override
	public String toString() {
		return "\nRCB{" +
				"state=" + state +
				", waitlist=" + waitlist +
				'}';
	}

	public Integer getState() {
		return state;
	}
	public void reduceCount() {
		--state;
	}
	public void increaseCount() {
		++state;
	}

	public void addUnits(int k) { state += k; }
	public void removeUnits(int k) { state -= k; }
	public void removeFromWaitlist(int p) {
		for (Pair<Integer, Integer> pair : getWaitlist()) {
			if (pair.getKey() == p) {
				waitlist.remove(pair);
				break;
			}
		}
	}

}
