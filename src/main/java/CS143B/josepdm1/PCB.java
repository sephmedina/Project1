package CS143B.josepdm1;

import java.util.LinkedList;
import java.util.Queue;

public class PCB {
	public static final int BLOCKED = 0;
	public static final int READY = 1;
	
	private Integer state;
	private Integer parent;
	private Queue<Integer> children = null;
	private Queue<Integer> resources = null;

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

	public Queue<Integer> getResources() {
		return resources;
	}

	public PCB(Integer state, Integer parent) {
		this.state = state;
		this.parent = parent;
		this.children = null;
		this.resources = new LinkedList<Integer>();
	}

	public void setState(Integer state) {
		this.state = state;
	}

	public void setParent(Integer parent) {
		this.parent = parent;
	}

	public void setChildren(Queue<Integer> children) {
		this.children = children;
	}

	public void setResources(Queue<Integer> resources) {
		this.resources = resources;
	}

	public void addResource(int r) {
		if (resources == null) {
			resources = new LinkedList<Integer>();
		}
		resources.add(r);
	}
}
