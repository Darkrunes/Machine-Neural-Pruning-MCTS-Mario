/**
 * Defines all the possible types of tiles on the map
 */
public enum Tile {
	Empty(' '), Wall('*'), Tree('T'), Axe('a'), Gold('g'), Key('k'), Door('-'), 
	StartPosition('s'), Water('~'), StepStone('o'), Player('^'), Unexplored('?'),
	UsedStepStone('O');
	
    public char charVal;

    /**
     * Constructor
     * @param charVal Character the tile corresponds to on the map
     */
    Tile (char charVal) {
        this.charVal = charVal;
    }
    
    /**
     * Gets a character and returns the tile corresponding to it
     * @param tileChar character to match tile to
     * @return Tile which matches the character given
     */
    public static Tile getTile(char tileChar) {
    	for (Tile currTile : Tile.values()) {
    		if (currTile.charVal == tileChar) return currTile; 
    	}
    	return Tile.Empty;
    }
    
    /** 
     * Check if the given tile contains an item
     * @param tile tile to check
     * @return whether the tile contains an item
     */
    public static boolean isItem(Tile tile) {
    	switch (tile) {
    	case Axe:
    		return true;
    	case Gold:
    		return true;
    	case Key:
    		return true;
    	case StepStone:
    		return true;
    	default:
    		return false;
    	}
    }
    
    /**
     * Check if the given tile is an obstacle/ possible obstacle
     * @param tile		Tile to be checked
     * @return			Whether the tile is an obstacle
     */
    public static boolean isObstacle(Tile tile) {
    	switch (tile) {
    	case Water:
    	case Door:
    	case Tree:
    	case Wall:
    	case Unexplored:
    		return true;
    	default:
    		return false;
    	}
    }
    
}