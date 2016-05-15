import java.awt.Point;
import java.util.ArrayList;

public class GetGold implements Behaviour{

	private Graph map;
	private ArrayList<Tile> inventory;
	private Point startPoint;
	
	public GetGold(Graph map, ArrayList<Tile> inv, Point startPoint) {
		this.startPoint = startPoint;
		this.map = map;
		this.inventory = inv;
	}
	
	
	@Override
	public Point getGoal() {
		for (Item i: map.itemsOnMap()) {
			if (i.getItemName() == Tile.Gold)
				return i.getPos();
		}
		if (inventory.contains(Tile.Gold)) return startPoint;
		
		return null;
	}
	
	@Override
	public int returnHeuristic(Point goal, Point currPosition) {
		return Math.abs(goal.x - currPosition.x) + Math.abs(goal.y - currPosition.y);
	}

}
