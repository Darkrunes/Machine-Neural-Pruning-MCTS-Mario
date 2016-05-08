import java.awt.Point;
import java.util.*;

public class State {
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
	public void calculateHeuristic(Behaviour b, Graph map, ArrayList<Item> inventory) {
		this.hCost = b.returnHeuristic(map, inventory);
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
}
