import java.awt.Point;
import java.util.ArrayList;

/**
 * Behaviors represent the different modes of the Agent 
 * @author Saffat Shams Akanda, Richard Luong
 */
public interface Behaviour {
	public Point getGoal();
	public int returnHeuristic(Point goal, Point currentPosition);
	public boolean canUseStone();
	public String getBehaviour();
}
