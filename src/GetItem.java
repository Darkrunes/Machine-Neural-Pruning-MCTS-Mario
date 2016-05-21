import java.awt.Point;
import java.util.ArrayList;

/**
 * The GetItem behaviour is used when the agent wants to obtain a specific item on the map
 * @author Saffat Shams Akanda, Richard Luong
 */
public class GetItem implements Behaviour{
	private Graph map;
	private ArrayList<Tile> inventory;
	private Point itemPos;
	private boolean canUseStone;
	
	/**
	 * Constructor for the GetItem behaviour
	 * @param map	The 2D array of tiles containing the area explored so far by the agent
	 * @param inv	The list of items that the agent is holding
	 * @param itemPos The position of the item that the agent wants to obtain
	 * @param canUseStone Whether the agent is able to use a stone to retrieve an item or not
	 */
	public GetItem(Graph map, ArrayList<Tile> inv, Point itemPos, boolean canUseStone) {
		this.map = map;
		this.inventory = inv;
		this.itemPos = itemPos;
		this.canUseStone = canUseStone;
	}
	
	/**
	 * Returns the goal node for the player,
	 * Tells the player the location of the item if it has been found
	 * otherwise null
	 */
	@Override
	public Point getGoal() {
		return itemPos;
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
		return canUseStone;
	}

	/**
	 * Returns the name of the behavior
	 * @return			name of behaviour
	 */
	@Override
	public String getBehaviour() {
		return "GetItem";
	}
}
