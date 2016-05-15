import java.awt.Point;
import java.util.ArrayList;

public class GetItem implements Behaviour{

	private Graph map;
	private ArrayList<Tile> inventory;
	private Point itemPos;
	
	public GetItem(Graph map, ArrayList<Tile> inv, Point itemPos) {
		this.map = map;
		this.inventory = inv;
		this.itemPos = itemPos;
	}
	
	@Override
	public Point getGoal() {
		return itemPos;
	}


	@Override
	public int returnHeuristic(Point goal, Point currPosition) {
		return Math.abs(goal.x - currPosition.x) + Math.abs(goal.y - currPosition.y);
	}
}
