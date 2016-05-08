/**
 * Defines all the directions that the agent can go in
 *
 */
public enum Direction {
	/**
	 * The Directions act as a doubly linked list, where the first argument is its direction
	 * in character form and the 2 arguments after are directions that are before and after the 
	 * current direction in character form (Like pointers).
	 */
	NORTH('n', 'w', 'e'), SOUTH('s', 'e', 'w'), 
	EAST('e', 'n', 's'), WEST('w', 's', 'n'), NONE(' ', ' ', ' ');
	
	public char charVal;
	public char prev;
	public char next;
	
	/**
	 * Constructor
	 * @param charVal character representing the direction
	 * @param prev direction on the left
	 * @param next direction on the right
	 */
	Direction (char charVal, char prev, char next) {
		this.charVal = charVal;
		this.prev = prev;
		this.next = next;
	}
	
	/**
	 * Iterates through directions and finds the one whose character form corresponds
	 * to the parameter/
	 * @param c character form of direction to match
	 * @return corresponding direction to the character passed
	 */
	private Direction charToDirection(char c) {
		for (Direction d: Direction.values()) {
			if (d.charVal == c) return d;
		}
		return Direction.NONE;
	}
	
	/**
	 * Change the direction based on whether the agent moved left or right
	 * @param move right or left move
	 * @return the new direction that the agent is facing
	 */
	public Direction changeDirection (char move) {
		if (move == 'l') {
			return charToDirection(this.prev);
		} else if (move == 'r') {
			return charToDirection(this.next);
		}
		return null;
	}
}
