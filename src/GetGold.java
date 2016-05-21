import java.awt.Point;
import java.util.ArrayList;

/**
 * Behavior to direct the Agent towards the Gold/Starting Position
 * 
 * @author Saffat Shams Akanda, Richard Luong
 */
public class GetGold implements Behaviour{

	private Graph map;
	private ArrayList<Tile> inventory;
	private Point startPoint;

	/**
	 * Constructor for GetGold behavior
	 * @param map			Map to be used
	 * @param inv			Player Inventory
	 * @param startPoint	Players starting location
	 */
	public GetGold(Graph map, ArrayList<Tile> inv, Point startPoint) {
		this.startPoint = startPoint;
		this.map = map;
		this.inventory = inv;
	}
	
	
	/**
	 * Returns the goal node for the player,
	 * Tells the player the location of the gold if it has been found,
	 * starting location if it has the gold,
	 * otherwise null
	 */
	@Override
	public Point getGoal() {
		if (inventory.contains(Tile.Gold)) return startPoint;
		for (Item i: map.itemsOnMap()) {
			if (i.getItemName() == Tile.Gold)
				return i.getPos();
		}
		
		return null;
	}
	
	/**
	 * Uses Manhattan distance between the current position and the goal point
	 * to calculate a heuristic
	 * @param goal					Destination point
	 * @param currPosition			Starting point
	 * @return						Heuristic value to goal
	 */
	@Override
	public int returnHeuristic(Point goal, Point currPosition) {
		return Math.abs(goal.x - currPosition.x) + Math.abs(goal.y - currPosition.y);
	}
	
	/**
	 * Whether or not he player is allowed to use a stepping stone in this behaviour
	 * @return			If the agent is allowed to use stepping stones
	 */
	@Override 
	public boolean canUseStone() {
		return true;
	}

	/**
	 * Returns the name of the behavior
	 * @return			name of behaviour
	 */
	@Override
	public String getBehaviour() {
		return "GetGold";
	}

}
