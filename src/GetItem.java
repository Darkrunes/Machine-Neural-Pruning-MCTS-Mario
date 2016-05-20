import java.awt.Point;
import java.util.ArrayList;

public class GetItem implements Behaviour{

	private Graph map;
	private ArrayList<Tile> inventory;
	private Point itemPos;
	private boolean canUseStone;
	
	public GetItem(Graph map, ArrayList<Tile> inv, Point itemPos, boolean canUseStone) {
		this.map = map;
		this.inventory = inv;
		this.itemPos = itemPos;
		this.canUseStone = canUseStone;
	}
	
	@Override
	public Point getGoal() {
		return itemPos;
	}


	@Override
	public int returnHeuristic(Point goal, Point currPosition) {
		return Math.abs(goal.x - currPosition.x) + Math.abs(goal.y - currPosition.y);
	}
	
	@Override 
	public boolean canUseStone() {
		return canUseStone;
	}

	@Override
	public String getBehaviour() {
		return "GetItem";
	}
}
