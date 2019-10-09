package CS143B.josepdm1;

import CS143B.josepdm1.Exceptions.PCBException;
import CS143B.josepdm1.Exceptions.RCBException;
import javafx.util.Pair;

import java.util.LinkedList;
import java.util.Queue;

//todo : add error codes class
public class Manager {
	/* values for # of processes, resource TYPES */
	private final int N = 16;
	private final int R = 4;

	/* levels for ready list */
	private final int LEVEL = 3;

	private int currentSlot;
	private Queue<Integer>[] readyList = null;

	private PCB[] processes;
	private RCB[] resources;
	private int size;
	//initialize manager
	public Manager() {
		//initialize waiting/ready list(s)
		for (int i = 0; i < LEVEL; ++i) {
			readyList[i] = new LinkedList<Integer>();
		}
		init();
	}

	//todo TEST
	public String init() {
		processes = new PCB[N];

		//instantiate resources
		resources = new RCB[R];
		resources[0] = new RCB(1);
		resources[1] = new RCB(1);
		resources[2] = new RCB(2);
		resources[3] = new RCB(3);

		for (int i = 0; i < readyList.length; ++i) {
			readyList[i].clear();
		}
		currentSlot = 0;

		//special case - creating first process
		processes[0] = new PCB(0, PCB.READY, null, currentSlot++);
		readyList[0].add(0);
		size = 1;
		return "0";
	}

	//todo TEST
	//note - running process creates child process
	public String create(int priority) throws PCBException{
		if (++size > 16) {
			throw new PCBException("Too many processes");
		}
		PCB child = new PCB(priority, 1, getCurrentProcess().getIndex(), currentSlot);
		processes[currentSlot] = child;
		currentSlot = findAvailableIndex();
		
		PCB parent = getCurrentProcess();
		parent.getChildren().add(child.getIndex());
		
		readyList[child.getPriority()].add(child.getIndex());
		scheduler();
		return String.format("Process %s is created", child.getIndex());
	}
	
	public String destroy(int p) throws RCBException {
		if (processes[p] == getCurrentProcess()) {
			return "Running process can't destroy itself";
		}
		currentSlot = p;
		return String.format("%s processes destroyed", recursiveDestroy(p));
	}

	//process p requests k units of resource r
	//todo testing
	public String request(int p, int k, int r) {
		//todo check for errors
		RCB resource = resources[r];
		PCB process = processes[p];
		if (resource == null || process == null) {
			return "process or resource doesn't exist";
		}

		//enough units available
		if (k <= resource.getState()) {
			resource.removeUnits(k);
			process.addResource(r, k);
			return String.format("resource %s allocated", r);
		}
		//not enough units available
		else {
			process.setState(PCB.BLOCKED);
			readyList[ process.getPriority() ].remove(p);
			resource.getWaitlist().add(new Pair<Integer, Integer>(p, k));
			return String.format("process %s blocked", p) + "\n" + scheduler();
		}
	}

	//process p releases all units of resource type r
	//todo testing
	public String release(int p, int r) throws RCBException {
		RCB resource = resources[r];
		PCB process = processes[p];
		if (resource == null || process == null) {
			return "process or resource doesn't exist";
		}
		int units = releaseResource(process, r);
		resource.addUnits(units);

		//unblock units
		Queue<Pair<Integer, Integer>>  resourceWaitlist = resource.getWaitlist();
		while (!resourceWaitlist.isEmpty() && resource.getState() > 0) {
			Pair<Integer, Integer> pair = resourceWaitlist.peek();
			int requestedUnits = pair.getValue();
			process = processes[pair.getKey()];
			if (requestedUnits <= resource.getState()) {
				resource.removeUnits(requestedUnits);
				Pair<Integer, Integer> resourcePair = new Pair<Integer, Integer>(r, requestedUnits);
				process.getResources().add(resourcePair);
				process.setState(PCB.READY);
				//todo - make sure process isn't blocked for any other resource
				readyList[ process.getPriority() ].add(process.getIndex());
				resourceWaitlist.remove();
			}
			else {
				break;
			}
		}
		return String.format("resource %s released", r) + "\n" + scheduler();
	}

	//preemptive scheduling
	//todo testing
	public String timeout() {
		PCB current = getCurrentProcess();
		readyList[ current.getPriority() ].remove();
		readyList[ current.getPriority() ].add(current.getIndex());
		return scheduler();
	}

	private String scheduler() {
		return String.format("process %s running", schedule().getIndex());
	}

	/*******************
	 *  Helper Functions
	 *  ****************/

	//todo testing
	//destroy process P
	private int recursiveDestroy(int p) throws RCBException {
		PCB process = processes[p];
		Queue<Integer> childList = process.getChildren();
		int total = 0;
		//destroy P's children

		for (Integer child: childList) {
			total += recursiveDestroy(child);
		}

		//remove P from parent
		PCB parent = processes[process.getParent()];
		parent.getChildren().remove(p);

		//remove from ready list
		readyList[ process.getPriority() ].remove(p);

		//release resources
		for (Pair<Integer, Integer> pair : process.getResources()) {
			release(p, pair.getKey());
		}

		//release PCB
		processes[p] = null;
		return total + 1;
	}
	private int releaseResource(PCB p, int r) throws RCBException{
		for (Pair<Integer, Integer> pair: p.getResources()) {
			if (pair.getKey() == r) {
				int units = pair.getValue();
				p.getResources().remove(pair);
				return units;
			}
		}
		throw new RCBException( String.format("Process %s isn't allocated resource %s", p.getIndex(), r));
	}
	//get the new process that should be running now
	private PCB schedule() {
		return getCurrentProcess();
	}
	public PCB getCurrentProcess() {
		for (int i = readyList.length; ; --i) {
			if (readyList[i].peek() != null) {
				return processes[ readyList[i].peek() ];
			}
		}
	}

	private int getCurrentProcessPriority() {
		return getCurrentProcess().getPriority();
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
