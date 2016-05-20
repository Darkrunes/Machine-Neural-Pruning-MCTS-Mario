import java.awt.Point;
import java.util.ArrayList;

public class Explore implements Behaviour{
	
	private Graph map;
	private ArrayList<Tile> inventory;
	private Point goalPos;
	
	public Explore(Graph map, ArrayList<Tile> inv, Point goalPos) {
		this.map = map;
		this.inventory = inv;
		this.goalPos = goalPos;
	}
	
	@Override
	public Point getGoal() {
		return this.goalPos;
	}

	@Override
	public int returnHeuristic(Point goal, Point currPosition) {
		return Math.abs(goal.x - currPosition.x) + Math.abs(goal.y - currPosition.y);
	}

	@Override 
	public boolean canUseStone() {
		return false;
	}

	@Override
	public String getBehaviour() {
		return "Explore";
	}
	
}
