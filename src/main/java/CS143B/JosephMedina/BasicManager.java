import java.util.LinkedList;
import java.util.Queue;

public class BasicManager {
	private Queue<Integer> waiting = null;
	private Queue<Integer> ready = null;
	private int N;
	private PCB[] processes;
	
	public Queue<Integer> getWaiting() {
		return waiting;
	}
	public Queue<Integer> getReady() {
		return ready;
	}
	
	//initialize manager
	public BasicManager(int N) {
		this.N = N;
		waiting = new LinkedList<Integer>();
		ready = new LinkedList<Integer>();
		init();
	}

	public void init() {
		processes = new PCB[N];
		waiting.clear();
		ready.clear();
		PCB first = new PCB(1, null);
		processes[0] = first;
		
	}

	public String create(int p, int c) {
		PCB child = new PCB(1, p);
		child.setParent(p);
	
		PCB parent = processes[p];
		if (parent.getChildren() == null) {
			parent.setChildren(new LinkedList<Integer>());
		}
		
		parent.getChildren().add(c);
		
		ready.add(c);
		return String.format("Process %s is created", c);
	}
	
	public String destroy(int process) {
		return String.format("%s processes destroyed", recursiveDestroy(process));
	}
	
	private int recursiveDestroy(int p) {
		PCB process = processes[p];
		Queue<Integer> childList = process.getChildren();
		
		if (childList != null) {
			for (Integer child: childList) {
				recursiveDestroy(child);
			}
		}
		
		return 0;
	}
	
	
}
