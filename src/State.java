import java.awt.Point;
import java.util.*;

public class State implements Comparable<State>{
	private State prevState;
	private Direction currDirection;
	private Point pos;
	private int fCost;
	private Point goalNode;
	private int hCost;
	private ArrayList<Tile> inventory;
	private boolean usesStone;
	
	private static final int impassableCost = Integer.MAX_VALUE - 0xffffff;
	
	public State(State ps, Direction d, Point p, Behaviour b, Graph map, ArrayList<Tile> inventory, Point goal, boolean usesStone) {
		this.prevState = ps;
		this.currDirection = d;
		this.pos = p;	
		this.inventory = inventory;
		this.usesStone = usesStone;
		
		calculateHeuristic(b, map, inventory, goal);
	}
	
	/**
	 * Calculates the heuristic and sets up the FCost of the state
	 * @param b				The behavior to calculate the heuristic
	 * @param map			The map
	 * @param inventory		Current player inventory
	 */
	public void calculateHeuristic(Behaviour b, Graph map, ArrayList<Tile> inventory, Point goal) {
		this.hCost = b.returnHeuristic(goal, pos);
		Tile currTile = map.getTileAt(pos.x, pos.y);
		int gCost = 0;
		
		goalNode = goal;
		
		// Paths that require an item require 2 moves to travel
		// If the item is not in possession path is impassable
		// All other paths cost 1
		switch (currTile) {
		case Wall:
			gCost = impassableCost; break;
		case Tree:
			gCost = (inventory.contains(Tile.Axe)) ? 2 : impassableCost; break;
		case Door:
			gCost = (inventory.contains(Tile.Key)) ? 2 : impassableCost; break;
		case Water:
			gCost = (inventory.contains(Tile.StepStone)) ? 2 : impassableCost;
			usesStone = true;
			break;
		case Unexplored:
			gCost = 3; break;
		default:
			gCost = 1; break;
		}	
		
		this.fCost = (prevState == null) ? gCost + hCost : gCost + hCost + prevState.getGCost();
	}
	
	/**
	 * Recursively builds a list of moves to get to the goal
	 * @return		Queue of moves to reach the goal from the starting node
	 */
	public Queue<Move> getPath() {
		Queue<Move> qm;
		if (prevState == null) {
			qm = new LinkedList<Move>();
		} else {
			qm = prevState.getPath();
		}
		Move m = new Move();
		m.d = currDirection;
		qm.add(m);
		return qm;
	}
	
	public Direction getDirection() {
		return this.currDirection;
	}
	
	public void printPath() {
		if (prevState != null) {
			prevState.printPath();
			System.out.print(" -> " + this.pos.toString());
		}
		else {
			System.out.println(goalNode.toString());
			System.out.print(this.pos.toString());
		}
	}
	
	protected int getGCost() {
		return this.fCost - this.hCost;
	}
	
	
	/**
	 * Getter for the function cost of a state
	 * @return		Integer function cost of state
	 */
	public int getFCost() {
		return fCost;
	}
	
	/**
	 * Gets a Point object representing the current position in the state
	 * @return		Point representing the current position
	 */
	public Point getPos() {
		return pos;
	}
	
	public boolean getStoneUsage() {
		return usesStone;
	}
	
	
	public ArrayList<Tile> getInv() {
		return inventory;
	}

	@Override
	public boolean equals(Object o) {
		if (o.getClass() == this.getClass()) {
			State otherState = (State) o;
			if (otherState.getPos().x == this.pos.x && otherState.getPos().y == this.pos.y) return true;
		}
		return false;
	}
	
	@Override
	public int compareTo(State o) {
		if (o == this) return 0;
		else if (o.getFCost() < getFCost()) return 1;
		else return -1;
	}
}
