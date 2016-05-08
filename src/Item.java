import java.awt.Point;

public class Item {
	private Tile itemName;
	private int priorityWeight;
	private Point pos;
	
	public Item(Tile itemName, int priorityWeight, Point pos) {
		this.itemName = itemName;
		this.priorityWeight = priorityWeight;
		this.pos = pos;
	}

	public Tile getItemName() {
		return itemName;
	}

	public void setItemName(Tile itemName) {
		this.itemName = itemName;
	}

	public int getPriorityWeight() {
		return priorityWeight;
	}

	public void setPriorityWeight(int priorityWeight) {
		this.priorityWeight = priorityWeight;
	}

	public Point getPos() {
		return pos;
	}

	public void setPos(Point pos) {
		this.pos = pos;
	}
	
	
	
}
