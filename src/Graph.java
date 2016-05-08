import java.awt.Point;
import java.util.*;

public class Graph {
	private Tile[][] map;
	private ArrayList<Item> itemsOnMap;
	private Point currPos;
	private Point exploredLowBound;
	private Point exploredHighBound;
	
	public Graph() {
		map = new Tile[160][160];
		itemsOnMap = new ArrayList<Item>();
		currPos = new Point(80, 80);
		// Used to map which areas have been explored
		exploredLowBound = new Point(80, 80);
		exploredHighBound = new Point(80, 80);
		// All tiles are unexplored at the start
		for (int y = 0; y < 160; y++) {
			for (int x = 0; x < 160; x++) {
				map[y][x] = Tile.Unexplored;
			}
		}
		// Set the starting position so we can backtrack later to it
		map[currPos.y][currPos.x] = Tile.StartPosition;
		
	}
	
	/**
	 * Set up the map using the 5 x 5 view the player is given at the start and keep track of the 
	 * positions of any items that are spotted.
	 * @param initialView 5 x 5 view at the start
	 */
	public void initialiseMap(char[][] initialView) {
		// Lower bound of map explored initially
		Point mapPos = new Point(78, 78);
		for (int y = 4; y >= 0; y--) {
			mapPos.x = 78;
			for (int x = 0; x < 5; x++) {
				// Start position already mapped
				if (x == 2 && y == 2) {
					mapPos.x += 1;
					continue;
				}
				// Map out each tile
				map[mapPos.y][mapPos.x] = Tile.getTile(initialView[y][x]);
				// Store location of items 
				if (Tile.isItem(map[mapPos.y][mapPos.x])) {
					itemsOnMap.add(new Item(map[mapPos.y][mapPos.x], 1, new Point(mapPos.x, mapPos.y)));
				}
				mapPos.x += 1;
			}
			mapPos.y += 1;
		}
		// Update region explored
		exploredLowBound = new Point(78, 78);
		exploredHighBound = new Point(82, 82);
	}
	

	/**
	 * Uses the lower and upper boundaries of the positions of the tiles that have been
	 * explored so far to display a rectangular view of the map the agent knows of so far. 
	 */
	public void displayMap() {
		for (int y = exploredHighBound.y; y >= exploredLowBound.y; y--) {
			System.out.print("|");
			for (int x = exploredLowBound.x; x <= exploredHighBound.x; x++) {
				if (x == currPos.x && y == currPos.y) {
					System.out.print("P");
					continue;
				}
				System.out.print(map[y][x].charVal);
			}
			System.out.print("|\n");
		}
	}
	
	/**
	 * Updates the map using the first row of the 5x5 view that the agent has after it has moved forward
	 * in one of the four directions. Also updates the player's current position on the map.
	 * @param topTiles the first row of tiles that the agent sees in its 5x5 view
	 * @param currDirection the direction that the agent is facing after moving 
	 */
	public void updateMap(char[] topTiles, Direction currDirection) {
		// Extend the boundary since the player has moved forward in some direction
		int tempBoundary = 0;
		switch (currDirection) {
		case NORTH:
			tempBoundary = currPos.y+3;
			break;
		case SOUTH:
			tempBoundary = currPos.y-3;
			break;
		case EAST:
			tempBoundary = currPos.x+3;
			break;
		case WEST:
			tempBoundary = currPos.x-3;
			break;	
		}
		// Iterate through the row of 5 tiles 3 units away from the agent and update the map
		if (currDirection == Direction.NORTH || currDirection == Direction.SOUTH) {
			int tempX = 0;
			for (int x = currPos.x - 2; x <= currPos.x + 2; x++) {
				map[tempBoundary][x] = Tile.getTile(topTiles[tempX]);
				// Store location of items 
				if (Tile.isItem(map[tempBoundary][x])) {
					itemsOnMap.add(new Item(map[tempBoundary][x], 1, new Point(x, tempBoundary)));
				}
				tempX += 1;
			}
			if (currDirection == Direction.NORTH) {
				if (currPos.y+3 > exploredHighBound.y) exploredHighBound.y = currPos.y+3; 
			}
			if (currDirection == Direction.SOUTH) {
				if (currPos.y-3 < exploredLowBound.y) exploredLowBound.y = currPos.y-3; 
			}
		} else {
			int tempY = 0;
			for (int y = currPos.y - 2; y <= currPos.y + 2; y++) {
				map[y][tempBoundary] = Tile.getTile(topTiles[tempY]);
				// Store location of items 
				if (Tile.isItem(map[y][tempBoundary])) {
					itemsOnMap.add(new Item(map[y][tempBoundary], 1, new Point(tempBoundary, y)));
				}
				tempY += 1;
			}
			// Update the approximate locations explored on the map
			if (currDirection == Direction.EAST) {
				if (currPos.x+3 > exploredHighBound.x) exploredHighBound.x = currPos.x+3; 
			} else if (currDirection == Direction.WEST) {
				if (currPos.x-3 < exploredLowBound.x) exploredLowBound.x = currPos.x-3; 
			}
		}
		// Account for the case where the start position gets overwritten
		map[80][80] = Tile.StartPosition;
		// Update the position of the player
		switch (currDirection) {
		case NORTH:
			currPos.y += 1;
			break;
		case SOUTH:
			currPos.y -= 1;
			break;
		case EAST:
			currPos.x += 1;
			break;
		case WEST:
			currPos.x -= 1;
			break;	
		}		
		// Remove the item from the map if the player has picked it up
		// TODO Account for case where stone is placed on water
		if (Tile.isItem(map[currPos.y][currPos.x])) {
			map[currPos.y][currPos.x] = Tile.Empty;
		} 
	}
	
	public ArrayList<Item> getItems() {
		return null;
	}
	
	public Queue<Move> astar(Behaviour currBehaviour) {
		return null;
	}
	
}
