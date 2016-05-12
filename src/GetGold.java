import java.awt.Point;
import java.util.ArrayList;

public class GetGold implements Behaviour{

	private Graph map;
	private ArrayList<Item> inventory;
	private Point startPoint;
	
	public GetGold(Graph map, ArrayList<Item> inv, Point startPoint) {
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
		for (Item i : inventory) {
			if (i.getItemName() == Tile.Gold)
				return startPoint;
		}
		
		return null;
	}
	
	@Override
	public int returnHeuristic(Point goal, Point currPosition) {
		return Math.abs(goal.x - currPosition.x) + Math.abs(goal.y - currPosition.y);
	}

}
