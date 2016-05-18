import java.awt.Point;
import java.util.*;

public class Graph {
	private Tile[][] map;
	private ArrayList<Item> itemsOnMap;
	private ArrayList<Tile> playerInv;		// very hacky change later
	private Point currPos;
	private Point exploredLowBound;
	private Point exploredHighBound;
	
	public Graph() {
		map = new Tile[160][160];
		itemsOnMap = new ArrayList<Item>();
		playerInv = new ArrayList<Tile>();
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
	 * Returns true if the move the agent is going to make is valid (i.e. they are moving into a tile
	 * which is either empty or contains an item)
	 * @param currDirection The direction that the agent is going to move in
	 * @return true if the next move forward is valid
	 */
	public boolean isValidMove(Direction currDirection, boolean canUseStone) {
		switch (currDirection) {
		case NORTH:
			return canPassTile(new Point(currPos.x, currPos.y + 1), canUseStone);
		case SOUTH:
			return canPassTile(new Point(currPos.x, currPos.y - 1), canUseStone);
		case EAST:
			return canPassTile(new Point(currPos.x + 1, currPos.y), canUseStone);
		case WEST:
			return canPassTile(new Point(currPos.x - 1, currPos.y), canUseStone);
		}
		System.out.println("Invalid move detected");
		return false;
	}
	
	/**
	 * Get a point within the current boundaries that the agent has gone to which has been unexplored
	 */
	
	public Point getUnexplored() {
		ArrayList<Point> unexploredList = new ArrayList<Point>();
		// Scan the map the agent knows of so far for unexplored points
		for (int y = exploredHighBound.y; y >= exploredLowBound.y; y--) {
			for (int x = exploredLowBound.x; x <= exploredHighBound.x; x++) {
				// If a point hasn't been explored, add the point to the unexplored list
				if (map[y][x].charVal == '?') {
					Point unexploredPoint = new Point(x, y);
					unexploredList.add(unexploredPoint);
				}
			}
		}
		// Randomly select one of the unexplored points and tell the agent to explore up to that point
		Random rand = new Random();
		if (unexploredList.size() == 0) return null; 
		int randomIndex = rand.nextInt(unexploredList.size()); 
		return unexploredList.get(randomIndex);
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
			if (currDirection == Direction.NORTH) {
				for (int x = currPos.x - 2; x <= currPos.x + 2; x++) {
					map[tempBoundary][x] = Tile.getTile(topTiles[tempX]);
					System.out.printf("Tile at (%d,%d) updated to %c\n", x, tempBoundary, topTiles[tempX]);
					// Store location of items 
					if (Tile.isItem(map[tempBoundary][x])) {
						itemsOnMap.add(new Item(map[tempBoundary][x], 1, new Point(x, tempBoundary)));
					}
					tempX += 1;
				}
			} else {
				for (int x = currPos.x + 2; x >= currPos.x - 2; x--) {
					map[tempBoundary][x] = Tile.getTile(topTiles[tempX]);
					System.out.printf("Tile at (%d,%d) updated to %c\n", x, tempBoundary, topTiles[tempX]);
					// Store location of items 
					if (Tile.isItem(map[tempBoundary][x])) {
						itemsOnMap.add(new Item(map[tempBoundary][x], 1, new Point(x, tempBoundary)));
					}
					tempX += 1;
				}
			}
			if (currDirection == Direction.NORTH) {
				if (currPos.y+3 > exploredHighBound.y) exploredHighBound.y = currPos.y+3; 
			}
			if (currDirection == Direction.SOUTH) {
				if (currPos.y-3 < exploredLowBound.y) exploredLowBound.y = currPos.y-3; 
			}
		} else {
			int tempY = 0;
			if (currDirection == Direction.EAST) {
				for (int y = currPos.y + 2; y >= currPos.y - 2; y--) {
					map[y][tempBoundary] = Tile.getTile(topTiles[tempY]);
					System.out.printf("Tile at (%d,%d) updated to %c\n", tempBoundary, y, topTiles[tempY]);
					// Store location of items 
					if (Tile.isItem(map[y][tempBoundary])) {
						itemsOnMap.add(new Item(map[y][tempBoundary], 1, new Point(tempBoundary, y)));
					}
					tempY += 1;
				}
			} else {
				for (int y = currPos.y - 2; y <= currPos.y + 2; y++) {
					map[y][tempBoundary] = Tile.getTile(topTiles[tempY]);
					System.out.printf("Tile at (%d,%d) updated to %c\n", tempBoundary, y, topTiles[tempY]);
					// Store location of items 
					if (Tile.isItem(map[y][tempBoundary])) {
						itemsOnMap.add(new Item(map[y][tempBoundary], 1, new Point(tempBoundary, y)));
					}
					tempY += 1;
				}
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
		if (Tile.isItem(map[currPos.y][currPos.x])) {
			playerInv.add(map[currPos.y][currPos.x]);
			map[currPos.y][currPos.x] = Tile.Empty;
			removeItemOnMap(new Point(currPos.x, currPos.y));
		} 
		// Account for case where stone is placed on water
		if (map[currPos.y][currPos.x] == Tile.Water) {
			map[currPos.y][currPos.x] = Tile.UsedStepStone;
			playerInv.remove(Tile.StepStone);
		}
	}
	
	public void removeItemOnMap(Point pos) {
		Item toRemove = null;
		for (Item currItem: itemsOnMap) {
			if (currItem.getPos().x == pos.x && currItem.getPos().x == pos.x) {
				toRemove = currItem;
				break;
			}
		}
		// Remove the item from the map and add it to the player's inventory
		if (toRemove != null) {
			System.out.println("Item " + toRemove.getItemName().charVal + " removed from map");
			itemsOnMap.remove(toRemove);
		}
	}
	
	public Point getPlayerPos() {
		return currPos;
	}
	
	public ArrayList<Tile> getItems() {
		// SUUUUPER hacky change later
		return playerInv;
	}
	
	public ArrayList<Item> itemsOnMap() {
		// HACKY AS F REPLACE
		return itemsOnMap;
	}
	
	public Point getExploredLow() {
		return this.exploredLowBound;
	}
	
	public Point getExploredHigh() {
		return this.exploredHighBound;
	}
	
	public boolean itemSeen(Tile item) {
		for (Item currItem: itemsOnMap) {
			if (currItem.getItemName().equals(item)) return true;
		}
		return false;
	}
	
	public boolean holdingItem(Tile item) {
		return (playerInv.contains(item)) ? true : false;
	}
	
	/**
	 * Given the starting direction and a behavior which determines the goal and heuristic, 
	 * this will return a queue of moves to the goal or null if none is present.
	 * @param currBehaviour					The behavior of the agent, dictates the goal and heuristic
	 * @param currDirection					The direction the player is currently facing
	 * @return								A Queue of Move to the goal or null if no path available
	 * @see Behaviour#returnHeuristic()
	 * @see Behaviour#getGoal()
	 */
	public Queue<Move> astar(Behaviour currBehaviour, Direction currDirection) {
		Point goal = currBehaviour.getGoal();
		int iterations = 0;
		Point currentNode;
		Point tempPoint;
		State currState = new State(null, currDirection, this.currPos, currBehaviour, this, playerInv, goal);
		PriorityQueue<State> pq = new PriorityQueue<State>();
		ArrayList<Point> visited = new ArrayList<Point>();
		boolean canUseStone = currBehaviour.canUseStone();
		pq.add(currState);
		
		while (true) {
			if (pq.size() == 0) {
				iterations = 2000;
				break;
			}
			currState = pq.poll();
			currentNode = currState.getPos();
			// Check if state has been visited
			if (tileVisited(visited, currentNode)) continue;
			// Add the current node's position to the visited set
			visited.add(currentNode);
			currDirection = currState.getDirection();
			if(currentNode.equals(goal)) break;
			if (currentNode.x > 160 || currentNode.y > 160) continue;
			
			// Tile above
			tempPoint = new Point(currentNode.x, currentNode.y + 1);
			if (canPassTile(tempPoint, canUseStone)) {
				pq.add(new State(currState, Direction.NORTH, tempPoint,
						currBehaviour, this, playerInv, goal));
			}
			
			// Tile Below
			tempPoint = new Point(currentNode.x, currentNode.y - 1);
			if (canPassTile(tempPoint, canUseStone)) {
				pq.add(new State(currState, Direction.SOUTH, tempPoint,
						currBehaviour, this, playerInv, goal));
			}
			
			// Tile Left
			tempPoint = new Point(currentNode.x - 1, currentNode.y);
			if (canPassTile(tempPoint, canUseStone)) {
				pq.add(new State(currState, Direction.WEST, tempPoint,
						currBehaviour, this, playerInv, goal));
			}
			
			// Tile Right
			tempPoint = new Point(currentNode.x + 1, currentNode.y);
			if (canPassTile(tempPoint, canUseStone)) {
				pq.add(new State(currState, Direction.EAST, tempPoint,
						currBehaviour, this, playerInv, goal));
			}
			
			// Hacky method to return null on no path
			iterations++;
			if (iterations == 2000) break;
		}
		
		//currState.printPath();
		Queue<Move> path = currState.getPath();
		// Remove the state that was used to begin the search. It was only temporary to begin with
		// and it has no value in terms of being in the path to the goal as it can lead the agent to lose the game.
		path.remove();
		
		return (iterations == 2000) ? null : path;
	}
	
	public Tile getTileAt(int x, int y) {
		return map[y][x];
	}
	
	public Move getRandomMove() {
		Random rand = new Random();
		Direction d = Direction.NONE;
		switch (rand.nextInt(4)) {
		case 0:
			d = Direction.NORTH;
			break;
		case 1:
			d = Direction.EAST;
			break;
		case 2:
			d = Direction.SOUTH;
			break;
		case 3:
			d = Direction.WEST;
			break;
		}
		Move m = new Move();
		m.d = d;
		return m;
	}
	
	public Point getItemPos(Tile itemName) {
		for (Item currItem: itemsOnMap) {
			if (currItem.getItemName() == itemName) {
				return currItem.getPos();
			}
		}
		return null;
	}
	
	public void printInventory() {
		System.out.println("\n-------------\nInventory:");
		System.out.println("Axe: " + playerInv.contains(Tile.Axe));
		System.out.println("Step stone: " + playerInv.contains(Tile.StepStone));
		System.out.println("Key: " + playerInv.contains(Tile.Key));
		System.out.println("---------------");
	}
	
	private boolean canPassTile(Point p, boolean canUseStone) {
		switch (map[p.y][p.x]) {
			case Door:
			    return playerInv.contains(Tile.Key);
			case Water:
				if (!canUseStone) return false;
				return playerInv.contains(Tile.StepStone);
			case Wall:
				return false;
			case Tree:
				return playerInv.contains(Tile.Axe);
			default:
				return true;
		}
	}
	
	private boolean tileVisited(ArrayList<Point> visited, Point pos) {
		for (Point currPoint: visited) {
			if (currPoint.x == pos.x && currPoint.y == pos.y) return true;
		}
		return false;
	}
}
