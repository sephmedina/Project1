package CS143B.josepdm1;

import java.util.LinkedList;
import java.util.Queue;

public class BasicManager {
	private Queue<Integer> waitingList = null;
	private Queue<Integer> readyList = null;
	private int N;
	private PCB[] processes;
	private RCB[] resources; 
	
	public Queue<Integer> getWaitingList() {
		return waitingList;
	}
	public Queue<Integer> getReadyList() {
		return readyList;
	}
	public int getN() {
		return N;
	}
	public PCB[] getProcesses() {
		return processes;
	}
	public RCB[] getResources() {
		return resources;
	}
	public Queue<Integer> getWaiting() {
		return waitingList;
	}
	public Queue<Integer> getReady() {
		return readyList;
	}
	
	//initialize manager
	public BasicManager(int N) {
		this.N = N;
		waitingList = new LinkedList<Integer>();
		readyList = new LinkedList<Integer>();
		
		init();
	}

	public void init() {
		processes = new PCB[N];
		resources = new RCB[N];
		waitingList.clear();
		readyList.clear();
		PCB first = new PCB(1, null);
		processes[0] = first;
	}

	public String create(int p, int c) {
		PCB child = new PCB(1, p);
	
		PCB parent = processes[p];
		if (parent.getChildren() == null) {
			parent.setChildren(new LinkedList<Integer>());
		}
		
		parent.getChildren().add(c);
		
		readyList.add(c);
		return String.format("Process %s is created", c);
	}
	
	public String destroy(int process) {
		return String.format("%s processes destroyed", recursiveDestroy(process));
	}
	
	//destroy process P
	private int recursiveDestroy(int p) {
		PCB process = processes[p];
		Queue<Integer> childList = process.getChildren();
		
		//destroy P's children
		if (childList != null) {
			for (Integer child: childList) {
				recursiveDestroy(child);
			}
		}
		//remove P from parent
		PCB parent = processes[process.getParent()];
		parent.getChildren().remove(p);
		
		//remove from ready/waiting lists
		waitingList.remove(p);
		readyList.remove(p);
		
		//release resources
		
		//release PCB
		processes[p] = null;
		return 0;
	}
	 
	public String request(int i, int r) {
		//check for errors
		RCB resource = resources[r];
		PCB process = processes[i];
		if (resource == null || process == null) {
			return "process or resource doesn't exist";
		}
		
		if (resource.getState() == RCB.FREE) {
			resource.setState(RCB.ALLOCATED);
			process.addResource(r);
			return String.format("resource %s allocated", r);
		} 
		else {
			process.setState(PCB.BLOCKED);
			readyList.remove(i);
			resource.getWaitlist().add(i);
			//may switch next two lines 
			scheduler();
			return String.format("process %s allocated", i);
		}
	}
	
	public String release(int i, int r) {
		RCB resource = resources[r];
		PCB process = processes[i];
		if (resource == null || process == null) {
			return "process or resource doesn't exist";
		}
		process.getResources().remove(r);
		Queue<Integer> waitlistR = resource.getWaitlist();
		if (waitlistR.isEmpty()) {
			resource.setState(RCB.FREE);			
		}
		else {
			int j = waitlistR.remove();
			//ok j can get unit, but is it actually on the ready list wtf
			readyList.add(j);
		
			PCB readyProcess = processes[j];
			readyProcess.setState(PCB.READY);
			readyProcess.getResources().add(j);
		}
		return String.format("resource %s released", r);
	}
	//preemptive scheduling
	public void timeout() {
		readyList.add( readyList.remove() );
		scheduler();
	}
	private void scheduler() {
		
	}
}
