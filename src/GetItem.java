import java.awt.Point;
import java.util.ArrayList;

public class GetItem implements Behaviour{

	private Graph map;
	private ArrayList<Item> inventory;
	
	public GetItem(Graph map, ArrayList<Item> inv) {
		this.map = map;
		this.inventory = inv;
	}
	
	
	@Override
	public Point getGoal() {
		return null;
	}


	@Override
	public int returnHeuristic(Point goal, Point currentPosition) {
		// TODO Auto-generated method stub
		return 0;
	}
}
