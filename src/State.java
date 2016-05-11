import java.awt.Point;
import java.util.*;

public class State implements Comparable<State>{
	private State prevState;
	private Direction currDirection;
	private Point pos;
	private int fCost;
	private int hCost;
	
	private static final int impassableCost = Integer.MAX_VALUE - 0xffff;
	
	public State(State ps, Direction d, Point p) {
		this.prevState = ps;
		this.currDirection = d;
		this.pos = p;	
	}
	
	/**
	 * Calculates the heuristic and sets up the FCost of the state
	 * @param b				The behavior to calculate the heuristic
	 * @param map			The map
	 * @param inventory		Current player inventory
	 */
	public void calculateHeuristic(Behaviour b, Graph map, ArrayList<Item> inventory, Point goal) {
		this.hCost = b.returnHeuristic(goal, pos);
		Tile currTile = map.getTileAt(pos.x, pos.y);
		int gCost = 0;
		
		// Paths that require an item require 2 moves to travel
		// If the item is not in possession path is impassable
		// All other paths cost 1
		switch (currTile) {
		case Wall:
			gCost = impassableCost; break;
		case Tree:
			gCost = (inventory.contains("Axe")) ? 2 : impassableCost; break;
		case Door:
			gCost = (inventory.contains("Key")) ? 2 : impassableCost; break;
		case Water:
			gCost = (inventory.contains("StepStone")) ? 2 : impassableCost; break;
		default:
			gCost = 1; break;
		}	
		
		this.fCost = gCost + hCost;
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

	@Override
	public int compareTo(State o) {
		if (o == this) return 0;
		else if (o.getFCost() < getFCost()) return 1;
		else return -1;
	}
}
