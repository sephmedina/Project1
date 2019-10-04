package CS143B.josepdm1;

import java.util.LinkedList;
import java.util.Queue;
import CS143B.josepdm1.Exceptions.SchedulerException;

//todo : add error codes class
public class BasicManager {
	/* values for # of processes, resource TYPES */
	private final int N = 16;
	private final int R = 4;

	/* levels for ready list */
	private final int LEVEL = 3;

	private int currentSlot;
	private PCB currentProcess;
	private Queue<Integer> waitingList = null;
	private Queue<Integer>[] readyList = null;

	private PCB[] processes;
	private RCB[] resources; 

	public Queue<Integer> getWaiting() {
		return waitingList;
	}
	public Queue<Integer>[] getReady() {
		return readyList;
	}
	
	//initialize manager
	public BasicManager() {
		//initialize waiting/ready list(s)
		waitingList = new LinkedList<Integer>();
		for (int i = 0; i < readyList.length; ++i) {
			readyList[i] = new LinkedList<Integer>();
		}
		init();
	}

	//todo TEST
	public void init() {
		processes = new PCB[N];

		//instantiate resources
		resources = new RCB[R];
		resources[0] = new RCB(1);
		resources[1] = new RCB(1);
		resources[2] = new RCB(2);
		resources[3] = new RCB(3);

		waitingList.clear();
		for (int i = 0; i < readyList.length; ++i) {
			readyList[i].clear();
		}
		currentSlot = 0;
		String creation = create(0);
	}

	//todo TEST
	//note - running process creates child process
	public String create(int priority) {
		PCB child = new PCB(priority, 1, currentProcess.getIndex(), currentSlot);
		processes[currentSlot] = child;
		currentSlot = findAvailableIndex();
		if ( != PCB.BASE_PROCESS) {
			PCB parent = processes[p];
			if (parent.getChildren() == null) {
				parent.setChildren(new LinkedList<Integer>());
			}
			parent.getChildren().add(child.getIndex());
		}

		readyList[child.getPriority()].add(child.getIndex());
		try {
			scheduler(child.getIndex());
		} catch (SchedulerException e) {
			return e.toString();
		}
		return String.format("Process %s is created", child.getIndex());
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
	 
	public String request(int k, int r) {
		//check for errors
		RCB resource = resources[r];
		PCB process = processes[k];
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
			readyList.remove(k);
			resource.getWaitlist().add(k);
			//may switch next two lines 
			scheduler();
			return String.format("process %s allocated", k);
		}
	}
	
	public String release(int i, int r) {
		RCB resource = resources[r];
		PCB process = processes[i];
		if (resource == null || process == null) {
			return "process or resource doesn't exist";
		}
//		process.getResources().remove(r);
//		Queue<Integer> waitlistR = resource.getWaitlist();
//		if (waitlistR.isEmpty()) {
//			resource.setState(RCB.FREE);
//		}
//		else {
//			int j = waitlistR.remove();
//			//ok j can get unit, but is it actually on the ready list wtf
//			readyList.add(j);
//
//			PCB readyProcess = processes[j];
//			readyProcess.setState(PCB.READY);
//			readyProcess.getResources().add(j);
//		}
		return String.format("resource %s released", r);
	}
	//preemptive scheduling
	public void timeout() {
		//readyList.add( readyList.remove() );
		//scheduler(0);
	}
	//do we display lines even if we don't context switch?
	private String scheduler(int j) throws SchedulerException {
		/*
		• create: context switch if new process has higher priority than current
		• release: context switch if release unblocks a higher‐priority process
		• delete: context switch if a deleted process releases a resource on which a
					higher‐level process is blocked
		 */
		PCB process = processes[j];
		if (process.getPriority() > getRunningProcessPriority()) {
			currentProcess = process;
		}
		return String.format("process %s running", currentProcess.getIndex());
	}

	/*******************
	 *  Helper Functions
	 *  ****************/

	public PCB getCurrentProcess() {
		return currentProcess;
	}

	private int getRunningProcessPriority() {
		return currentProcess.getPriority();
	}
	private int findAvailableIndex() {
		for (int i = 0; i < N; ++i) {
			if (processes[i] == null) {
				return i;
			}
		}
		//TODO include descriptive error
		return -1;
	}

}
