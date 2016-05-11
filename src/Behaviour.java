import java.awt.Point;
import java.util.ArrayList;

public interface Behaviour {
	public Point getGoal();
	public int returnHeuristic(Point goal, Point currentPosition);
}
