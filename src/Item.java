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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((itemName == null) ? 0 : itemName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Item other = (Item) obj;
		if (itemName != other.itemName)
			return false;
		return true;
	}
	
}
