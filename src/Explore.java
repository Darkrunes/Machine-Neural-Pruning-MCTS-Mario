import java.awt.Point;
import java.util.ArrayList;

/**
 * The Explore behaviour is used when the agent wants to explore up to
 * a specified point on the map
 * @author Saffat Shams Akanda, Richard Luong
 */
public class Explore implements Behaviour{
	private Graph map;
	private ArrayList<Tile> inventory;
	private Point goalPos;
	
	/**
	 * Constructor for the Explore behaviour
	 * @param map	The 2D array of tiles containing the area explored so far by the agent
	 * @param inv	The list of items that the agent is holding
	 * @param goalPos The position that the agent wants to explore to
	 */
	public Explore(Graph map, ArrayList<Tile> inv, Point goalPos) {
		this.map = map;
		this.inventory = inv;
		this.goalPos = goalPos;
	}
	
	/**
	 * Return the coordinates of the tile that the agent wants to explore to
	 */
	@Override
	public Point getGoal() {
		return this.goalPos;
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
		return false;
	}

	/**
	 * Returns the name of the behavior
	 * @return			name of behaviour
	 */
	@Override
	public String getBehaviour() {
		return "Explore";
	}
	
}
