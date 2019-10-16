package CS143B.josepdm1;

import CS143B.josepdm1.Exceptions.PCBException;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.Queue;

public class PCB {
	public static final int BLOCKED = 0;
	public static final int READY = 1;
	private Integer state;
	private Integer parent;
	private Queue<Integer> children = null;
	private Queue<Pair<Integer, Integer>> resources = null;
	private int priority;
	private int index;
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public int getParent() {
		return parent;
	}

	public Queue<Integer> getChildren() {
		return children;
	}

	public int getPriority() {
		return priority;
	}

	public int getIndex() {
		return index;
	}

	public Queue<Pair<Integer, Integer>> getResources() {
		return resources;
	}

	public PCB(int priority, Integer state, Integer parent, int index) {
		assert(priority >= 0 && priority <= 2);
		this.priority = priority;
		this.state = state;
		this.parent = parent;
		this.children = new LinkedList<Integer>();
		this.resources = new LinkedList<Pair<Integer, Integer>>();
		this.index = index;
	}

	public void addResource(int r, int k) {
		//check if resource exists already
		int existingValue = 0;
		for (Pair<Integer, Integer> pair: resources) {
			if (pair.getKey() == r) {
				existingValue = pair.getValue();
				resources.remove(pair);
			}
		}
		resources.add( new Pair<Integer, Integer>(r, k + existingValue));
	}

	@Override
	public String toString() {
		return "\nPCB{" +
				"state=" + state +
				", parent=" + parent +
				", children=" + children +
				", resources=" + resources +
				", priority=" + priority +
				", index=" + index +
				'}';
	}

	//	public boolean hasResource(int r) {
//		for (Pair<Integer, Integer> pair: resources) {
//			if (pair.getKey() == r) {
//				return true;
//			}
//		}
//		return false;
//	}
	public boolean hasEnoughResourceUnits(int r, int n) throws PCBException {
		for (Pair<Integer, Integer> pair: resources) {
			if (pair.getKey() == r) {
				return pair.getValue() >= n;
			}
		}
		throw new PCBException(String.format("Process %s doesn't contain %s units of resource %s", index, n, r));
	}
}
