import java.awt.Point;

/**
 * Stores information about an item's name (Corresponding tile), location and priority weight
 * @author Saffat Shams Akanda, Richard Luong
 *
 */
public class Item {
	private Tile itemName;
	private int priorityWeight;
	private Point pos;
	
	/**
	 * Constructor for Item
	 * @param itemName 			Name of the item
	 * @param priorityWeight	Value given to the item to determine how urgently the agent requires it
	 * @param pos				Coordinates of the item if it is on the map
	 */
	public Item(Tile itemName, int priorityWeight, Point pos) {
		this.itemName = itemName;
		this.priorityWeight = priorityWeight;
		this.pos = pos;
	}

	/**
	 * Getter method returns the name of the item
	 * @return Name of the item
	 */
	public Tile getItemName() {
		return itemName;
	}

	/**
	 * Getter method returns the priority weight of the item
	 * @return Priority weight of the item
	 */
	public int getPriorityWeight() {
		return priorityWeight;
	}

	/**
	 * Getter method returns the position of the itme
	 * @return X and Y coordinates of the item
	 */
	public Point getPos() {
		return pos;
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
		if (getClass() == obj.getClass()) {
			Item other = (Item) obj;
			if (itemName != other.itemName)
				return false;
			if (!pos.equals(other.getPos()))
				return false;
		} else if (obj instanceof Tile) {
			Tile other = (Tile) obj;
			if (this.getItemName() != other)
				return false;
		}
		
		return true;
	}
	
	@Override 
	public Item clone() {
		return new Item(itemName, priorityWeight, pos);
	}
	
}
