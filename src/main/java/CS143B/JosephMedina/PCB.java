import java.util.LinkedList;
import java.util.Queue;

public class PCB {
	//0 - blocked, 1 - ready
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

	public void setParent(int parent) {
		this.parent = parent;
	}

	public Queue<Integer> getChildren() {
		return children;
	}

	public void setChildren(Queue<Integer> children) {
		this.children = children;
	}

	public Queue<Integer> getResources() {
		return resources;
	}

	public void setResources(Queue<Integer> resources) {
		this.resources = resources;
	}

	public PCB(Integer state, Integer parent) {
		this.state = state;
		this.parent = parent;
		this.children = null;
		this.resources = null;
	}
	
	
	
}
