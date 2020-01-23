package CS143B.josepdm1;

import CS143B.josepdm1.Exceptions.PCBException;
import CS143B.josepdm1.Exceptions.RCBException;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Manager {
	private final static Logger LOG = Logger.getLogger(Manager.class.getName());
	static {
		FileHandler fh = null;
		try {
			fh = new FileHandler("LogManager.txt");
		} catch (Exception e) {
			e.printStackTrace();
		}

		fh.setFormatter(new SimpleFormatter());
		LOG.addHandler(fh);
	}

	/* values for # of processes, resource TYPES */
	private final int N = 16;
	private final int R = 4;

	/* levels for ready list */
	private final int LEVEL = 3;

	private int currentSlot;
	private Queue<Integer>[] readyList = null;

	private PCB[] processes;
	private RCB[] resources;

	public int getSize() {
		return size;
	}
	private int size;

	@Override
	public String toString() {
		return "Manager{ RUNNING=" + getCurrentProcess().getIndex() +
				", \ncurrentSlot=" + currentSlot +
				", \nreadyList=" + Arrays.toString(readyList) +
				", \nprocesses=" + Arrays.toString(processes) +
				", \nresources=" + Arrays.toString(resources) +
				", \nsize=" + size;
	}

	public Manager() {
		readyList = new Queue[LEVEL];
		for (int i = 0; i < LEVEL; ++i) {
			readyList[i] = new LinkedList<Integer>();
		}
		init();
	}

	public String init() {
		//instantiate resources
		resources = new RCB[R];
		resources[0] = new RCB(1);
		resources[1] = new RCB(1);
		resources[2] = new RCB(2);
		resources[3] = new RCB(3);
		processes = new PCB[N];

		for (int i = 0; i < readyList.length; ++i) {
			readyList[i].clear();
		}
		currentSlot = 0;

		//special case - creating first process
		processes[0] = new PCB(0, PCB.READY, null, currentSlot++);
		readyList[0].add(0);
		size = 1;
		return "0 ";
	}

	//note - running process creates child process
	public String create(int priority) throws PCBException{
		if (++size > 16) {
			size -= 1;
			throw new PCBException("Too many processes");
		}
		PCB child = new PCB(priority, 1, getCurrentProcess().getIndex(), currentSlot);
		processes[currentSlot] = child;
		currentSlot = findAvailableIndex();
		
		PCB parent = getCurrentProcess();
		parent.getChildren().add(child.getIndex());
		
		readyList[child.getPriority()].add(child.getIndex());
		scheduler();
		return scheduler();
	}
	
	public String destroy(int p) throws RCBException, PCBException {
		if (getCurrentProcess().getChildren().contains(p) || p == getCurrentProcess().getIndex()) {
			currentSlot = p;
			recursiveDestroy(p);
			LOG.info("Destroying " + p + "\n" + Arrays.toString(readyList) + Arrays.toString(processes));
			return scheduler();
		}
		throw new PCBException("Process " + p + " is not a child of process " + getCurrentProcess().getIndex());
	}

	public String request(int r, int k) throws PCBException, RCBException{
		if (getCurrentProcess().getIndex() == 0) {
			throw new PCBException("Process 0 cannot request resources");
		}
		if (r < 0 || r >= R) {
			throw new RCBException("Resource out of bounds");
		}

		//todo check for errors
		RCB resource = resources[r];
		if (k > resource.getInventory()) {
			throw new RCBException(String.format("Resource cannot hold more than %s", k));
		}
		PCB process = getCurrentProcess();
		int p = process.getIndex();

		//enough units available
		if (k <= resource.getState()) {
			resource.removeUnits(k);
			process.addResource(r, k);
		}
		//not enough units available
		else {
			process.setState(PCB.BLOCKED);
			readyList[ process.getPriority() ].remove(p);
			resource.getWaitlist().add(new Pair<Integer, Integer>(p, k));
		}
		return scheduler();
	}

	//current process releases n units of resource r
	public String release(int r, int n) throws PCBException, RCBException {
		if (r < 0 || r >= R) {
			throw new RCBException("Resource out of bounds");
		}

		RCB resource = resources[r];
		PCB process = getCurrentProcess();
		if (resource == null || process == null) {
			return "process or resource doesn't exist";
		}
		//does process have n units of resource r
		if (!process.hasEnoughResourceUnits(r, n)) {
			throw new PCBException("Process doesn't contain enough units of r");
		}

		//process will release enough units, adds back to resource
		releaseResource(process, r, n);

		//unblock process that's waiting
		while ( canUnblock(r) ) {
			unblock(r);
		}

		return scheduler();
	}

	//preemptive scheduling
	public String timeout() {
		PCB current = getCurrentProcess();
		readyList[ current.getPriority() ].remove();
		readyList[ current.getPriority() ].add(current.getIndex());
		return scheduler();
	}

	private String scheduler() {
		return String.format("%s ", schedule().getIndex());
	}

	/*******************
	 *  Helper Functions
	 *  ****************/
	//destroy process P
	private int recursiveDestroy(int p) throws RCBException {
		LOG.info("recursively destroying: " + p);
		LOG.info(processes[p].toString());
		PCB process = processes[p];
		Queue<Integer> childList = process.getChildren();
		int total = 0;

		//destroy P's children
		while (!childList.isEmpty()) {
			int child = childList.peek();
			total += recursiveDestroy(child);
		}

		//remove P from parent
		PCB parent = processes[process.getParent()];
		parent.getChildren().remove(p);

		//remove from ready list
		readyList[ process.getPriority() ].remove(p);

		//remove from other waitlists
		removeFromWaitlists(p);

		//release resources
		while (!process.getResources().isEmpty()) {
			//todo refactor - searches through resources list twice
			Pair<Integer, Integer> pair = process.getResources().peek();
			int r = pair.getKey();
			int units = pair.getValue();
			fullRelease(process, r, units);
			while ( canUnblock(r) ) {
				unblock(r);
			}
		}

		//release PCB
		processes[p] = null;
		--size;
		return total + 1;
	}
	private void fullRelease(PCB p, int r, int n) throws RCBException{
		for (Pair<Integer, Integer> pair: p.getResources()) {
			if (pair.getKey() == r) {
				int units = pair.getValue();
				p.getResources().remove(pair);
				resources[r].addUnits(units);
				return;
			}
		}
		throw new RCBException( String.format("Process %s  isn't allocated resource %s ", p.getIndex(), r));
	}
	private int releaseResource(PCB p, int r, int n) throws RCBException{
		for (Pair<Integer, Integer> pair: p.getResources()) {
			if (pair.getKey() == r) {
				int units = pair.getValue();
				int leftoverUnits = units - n;
				p.getResources().remove(pair);
				resources[r].addUnits(n);
				if (leftoverUnits > 0) {
					p.getResources().add(new Pair<Integer, Integer>(r, leftoverUnits));
				}
				return n;
			}
		}
		throw new RCBException( String.format("Process %s  isn't allocated resource %s ", p.getIndex(), r));
	}
	private PCB schedule() {
		return getCurrentProcess();
	}

	public PCB getCurrentProcess() {
		for (int i = readyList.length - 1;   ; --i) {
			if (readyList[i] != null && !readyList[i].isEmpty()) {
				return processes[ readyList[i].peek() ];
			}
		}
	}

	private int findAvailableIndex() {
		for (int i = 0; i < N; ++i) {
			if (processes[i] == null) {
				return i;
			}
		}
		return -1;
	}
	private boolean notBlocked(int p) {
		for (int i = 0; i < resources.length; ++i) {
			RCB resource = resources[i];
			for (Pair<Integer, Integer> pair: resource.getWaitlist()) {
				if (pair.getKey() == p) {
					return false;
				}
			}
		}
		return true;
	}
	private void removeFromWaitlists(int p) {
		for (int i = 0; i < resources.length; ++i) {
			resources[i].removeFromWaitlist(p);
		}
	}

	//return whether Resource R can unblock a process waiting
	private boolean canUnblock(int r) {
		RCB resource = resources[r];
		Queue<Pair<Integer, Integer>> resourceWaitlist = resource.getWaitlist();
		if (!resourceWaitlist.isEmpty() && resource.getState() > 0) {
			Pair<Integer, Integer> pair = resourceWaitlist.peek();
			int requestedUnits = pair.getValue();
			return requestedUnits <= resource.getState();
		}
		return false;
	}
	//unblock the process from resource's waitlist
	private void unblock(int r) {
		RCB resource = resources[r];
		if (resource.getWaitlist().isEmpty()) {
			return;
		}

		Pair<Integer, Integer> pair = resource.getWaitlist().remove();
		PCB process = processes[pair.getKey()];
		int units = pair.getValue();

		//remove units from resource
		resource.removeUnits(units);
		Pair<Integer, Integer> resourcePair = new Pair<Integer, Integer>(r, units);

		//add units to process
		process.getResources().add(resourcePair);

		//check if process isnt blocked, to be put into ready list
		if (notBlocked(process.getIndex())) {
			process.setState(PCB.READY);
			readyList[ process.getPriority() ].add(process.getIndex());
		}
	}

	public boolean isBlocked(int r) {
		return resources[r].getState() == 0;
	}
}
