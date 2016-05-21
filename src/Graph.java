import java.awt.Point;
import java.util.*;

/**
 * Graph data structure that handles creating and updating the map
 * It also handles searches and path validation 
 * @author Saffat Shams Akanda, Richard Luong
 */
public class Graph {
	private Tile[][] map;
	private ArrayList<Item> itemsOnMap;
	private ArrayList<Tile> playerInv;
	private ArrayList<Tile> itemsRequired;
	private Point currPos;
	private Point exploredLowBound;
	private Point exploredHighBound;
	private Stack<Point> exploreStack;
	private HashMap<String, Point> visitedPoints;
	
	public Graph() {
		map = new Tile[161][161];
		itemsOnMap = new ArrayList<Item>();
		playerInv = new ArrayList<Tile>();
		itemsRequired = new ArrayList<Tile>();
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
		exploreStack = new Stack<Point>();
		visitedPoints = new HashMap<String, Point>();
		
	}
	
	/**
	 * Set up the map using the 5 x 5 view the player is given at the start and keep track of the 
	 * positions of any items that are spotted.
	 * @param initialView 5 x 5 view at the start
	 */
	public void initialiseMap(char[][] initialView) {
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
		// Update points that haven't been visited
		floodFill();
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
	 * @param canUseStone	Whether or not the Agent is allowed to use a stone
	 * @return true if the next move forward is valid
	 */
	public boolean isValidMove(Direction currDirection, boolean canUseStone) {
		switch (currDirection) {
		case NORTH:
			return canPassTile(new Point(currPos.x, currPos.y + 1), canUseStone, playerInv, false, false);
		case SOUTH:
			return canPassTile(new Point(currPos.x, currPos.y - 1), canUseStone, playerInv, false, false);
		case EAST:
			return canPassTile(new Point(currPos.x + 1, currPos.y), canUseStone, playerInv, false, false);
		case WEST:
			return canPassTile(new Point(currPos.x - 1, currPos.y), canUseStone, playerInv, false, false);
		}
		//System.out.println("Invalid move detected");
		return false;
	}
	
	/**
	 * Get a point within the current boundaries that the agent has gone to which has been unexplored
	 * @return 		A point which has not been explored
	 */
	public Point getUnexplored() {
		ArrayList<Point> unexploredList = new ArrayList<Point>();
		// Scan the map the agent knows of so far for unexplored points
		for (int y = exploredHighBound.y; y >= exploredLowBound.y; y--) {
			for (int x = exploredLowBound.x; x <= exploredHighBound.x; x++) {
				// If a point hasn't been explored, add the point to the unexplored list
				if (map[y][x] == Tile.Unexplored) {
					Point unexploredPoint = new Point(x, y);
					unexploredList.add(unexploredPoint);
				}
			}
		}
		// Randomly select one of the unexplored points and tell the agent to explore up to that point
		Random rand = new Random();
		if (unexploredList.isEmpty()) return null; 
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
					// Store location of items 
					if (Tile.isItem(map[tempBoundary][x])) {
						itemsOnMap.add(new Item(map[tempBoundary][x], 1, new Point(x, tempBoundary)));
					}
					tempX += 1;
				}
			} else {
				for (int x = currPos.x + 2; x >= currPos.x - 2; x--) {
					map[tempBoundary][x] = Tile.getTile(topTiles[tempX]);
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
					// Store location of items 
					if (Tile.isItem(map[y][tempBoundary])) {
						itemsOnMap.add(new Item(map[y][tempBoundary], 1, new Point(tempBoundary, y)));
					}
					tempY += 1;
				}
			} else {
				for (int y = currPos.y - 2; y <= currPos.y + 2; y++) {
					map[y][tempBoundary] = Tile.getTile(topTiles[tempY]);
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
		// Update map if door unlocked or tree is cut down
		if (map[currPos.y][currPos.x] == Tile.Door || map[currPos.y][currPos.x] == Tile.Tree) {
			map[currPos.y][currPos.x] = Tile.Empty;
		}
		// Update points that haven't been visited yet
		floodFill();
		// Add the current point to the visited set
		String key = String.valueOf(currPos.x).concat(String.valueOf(currPos.y));
		visitedPoints.put(key, currPos);
	}
	
	/**
	 * Given the point of an item removes it from the list of items currently on the map
	 * @param pos		Position of item to be removed
	 */
	public void removeItemOnMap(Point pos) {
		Item toRemove = null;
		for (Item currItem: itemsOnMap) {
			if (currItem.getPos().x == pos.x && currItem.getPos().y == pos.y) {
				toRemove = currItem;
				break;
			}
		}
		// Remove the item from the map and add it to the player's inventory
		if (toRemove != null) {
			//System.out.println("Item " + toRemove.getItemName().charVal + " removed from map");
			itemsOnMap.remove(toRemove);
		}
	}
	
	/**
	 * Returns the player's current location
	 * @return
	 */
	public Point getPlayerPos() {
		return currPos;
	}
	
	/**
	 * Returns a list of items representing the player inventory
	 * @return		ArrayList of the player's inventory
	 */
	public ArrayList<Tile> getItems() {
		return playerInv;
	}
	
	/**
	 * Gets a list of the items currently seen on the map
	 * @return		List of items seen so far
	 */
	public ArrayList<Item> itemsOnMap() {
		return itemsOnMap;
	}
	
	/**
	 * Lowest locations on the map explored
	 * @return		Lowest X/Y locations on the map explored
	 */
	public Point getExploredLow() {
		return this.exploredLowBound;
	}
	
	/**
	 * Highest X/Y locations of the map that has been explored
	 * @return		Highest X/Y locations on the map explored
	 */
	public Point getExploredHigh() {
		return this.exploredHighBound;
	}
	
	/**
	 * Returns a boolean indicating if an item is currently present/been seen on the map
	 * @param item		Item to be searched for
	 * @return			Boolean indicating if the item is on the map
	 */
	public boolean itemSeen(Tile item) {
		for (Item currItem: itemsOnMap) {
			if (currItem.getItemName().equals(item)) return true;
		}
		return false;
	}
	
	/**
	 * If the player's inventory contains an item
	 * @param item			Item to be queried for
	 * @return				True if the item is in the inventory
	 */
	public boolean holdingItem(Tile item) {
		return (playerInv.contains(item)) ? true : false;
	}
	
	
	// Start of code for flood fill
	
	/**
	 * Pops a point from the exploreStack and returns it
	 * @return Popped point from the exploreStack
	 */
	public Point getUnvisitedPoint() {
		//System.out.println("Queue size: " + exploreStack.size());
		if (exploreStack.size() == 0) return null; 
		return exploreStack.pop();
	}
	
	/**
	 * Determines if a point has already been visited by the agent or is currently in the exploreStack
	 * @param currPos Current position of the player
	 * @return if the point has been visited or is in the explore Stack
	 */
	public boolean pointVisited(Point currPos) {
		String key = String.valueOf(currPos.x).concat(String.valueOf(currPos.y));
		if (visitedPoints.containsKey(key)) return true;
		for (Point currPoint: exploreStack) {
			if (currPoint.x == currPos.x && currPoint.y == currPos.y) return true;
		}		
		return false;
	}
	
	/**
	 * From the player's current position, add tiles adjacent to the player to the exploreStack that are reachable
	 * excluding diagonal tiles (We would have to perform A star to see if its reachable since it could be behind an
	 * obstacle. This allows the agent to search in a way similar to DFS which is very useful for maps with
	 * dense in the number of obstacles.
	 */
	private void floodFill() {
		if (!pointVisited(currPos)) {
			// Left Tile
			if (!Tile.isObstacle(map[currPos.y][currPos.x-1])) {
				if (!pointVisited(new Point(currPos.x-1, currPos.y))) 
					exploreStack.push(new Point(currPos.x-1, currPos.y));
			}
			// Right Tile
			if (!Tile.isObstacle(map[currPos.y][currPos.x+1])) {
				if (!pointVisited(new Point(currPos.x+1, currPos.y))) 
					exploreStack.push(new Point(currPos.x+1, currPos.y));
			}
			// Top Tile
			if (!Tile.isObstacle(map[currPos.y+1][currPos.x])) {
				if (!pointVisited(new Point(currPos.x, currPos.y+1))) 
					exploreStack.push(new Point(currPos.x, currPos.y+1));
			}
			// Right Tile
			if (!Tile.isObstacle(map[currPos.y-1][currPos.x])) {
				if (!pointVisited(new Point(currPos.x, currPos.y-1))) 
					exploreStack.push(new Point(currPos.x, currPos.y-1));
			}
		}
	}
	
	// End of code for flood fill
	
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
		ArrayList<Tile> invClone = deepClone(playerInv);
		State currState = new State(null, currDirection, this.currPos, currBehaviour, this, invClone, goal, false);
		PriorityQueue<State> pq = new PriorityQueue<State>();
		ArrayList<Point> visited = new ArrayList<Point>();
		boolean canUseStone = currBehaviour.canUseStone();
		pq.add(currState);
		// Check that the point to reach is not out of bounds
		if (goal.x >= 160 || goal.x < 0) return null;
		if (goal.y >= 160 || goal.y < 0) return null;
		
		// Used to identify if a behaviour is the GetGold behaviour
		// If it is GetGold, then the search cannot freely pass unexplored tiles
		// This makes sure there is a path to the gold as the agent treats unexplored tiles
		// as empty tiles which could lead to agent to not get the gold if that tile is something
		// it cannot bypass such as water
		boolean getGold = false;
		//System.out.printf("Goal at (%d, %d)\n", goal.x, goal.y);
		if (map[goal.y][goal.x] == Tile.Gold || Tile.isItem(map[goal.y][goal.x])) getGold = true;
		
		while (true) {
			if (pq.size() == 0) {
				iterations = 5000;
				break;
			}
			currState = pq.poll();
			currentNode = currState.getPos();
			// Clone the inventory of the state as it will be used multiple times
			invClone = currState.getInv();
			// Check if state has been visited
			if (tileVisited(visited, currentNode)) continue;
			// Add the current node's position to the visited set
			visited.add(currentNode);
			currDirection = currState.getDirection();
			if(currentNode.equals(goal)) break;
			// If agent is out of bounds of the map, discard state
			if (currentNode.x >= 160 || currentNode.y >= 160) continue;
			if (currentNode.x < 0 || currentNode.y < 0) continue;
			
			// Tile above
			ArrayList<Tile> tempInv = deepClone(invClone);
			tempPoint = new Point(currentNode.x, currentNode.y + 1);
			if (this.addStateToQueue(tempPoint, canUseStone, tempInv, getGold, currState.getStoneUsage())) {
				pq.add(new State(currState, Direction.NORTH, tempPoint,
						currBehaviour, this, tempInv, goal, currState.getStoneUsage()));
			}
			
			// Tile Below
			tempInv = deepClone(invClone);
			tempPoint = new Point(currentNode.x, currentNode.y - 1);
			if (this.addStateToQueue(tempPoint, canUseStone, tempInv, getGold, currState.getStoneUsage())) {
				pq.add(new State(currState, Direction.SOUTH, tempPoint,
						currBehaviour, this, tempInv, goal, currState.getStoneUsage()));
			}
			
			// Tile Left
			tempInv = deepClone(invClone);
			tempPoint = new Point(currentNode.x - 1, currentNode.y);
			if (this.addStateToQueue(tempPoint, canUseStone, tempInv, getGold, currState.getStoneUsage())) {
				pq.add(new State(currState, Direction.WEST, tempPoint,
						currBehaviour, this, tempInv, goal, currState.getStoneUsage()));
			}
			
			// Tile Right
			tempInv = deepClone(invClone);
			tempPoint = new Point(currentNode.x + 1, currentNode.y);
			if (this.addStateToQueue(tempPoint, canUseStone, tempInv, getGold, currState.getStoneUsage())) {
				pq.add(new State(currState, Direction.EAST, tempPoint,
						currBehaviour, this, tempInv, goal, currState.getStoneUsage()));
			}
			
			iterations++;
			if (iterations == 5000) break;
		}
		

		Queue<Move> path = currState.getPath();
		// Create a list of items to reach the gold
		if (currBehaviour.getBehaviour().equals("GetGold")) {
			itemsRequired = getItemsToReachGold(path, this.currPos);
		}
		// Remove the state that was used to begin the search. It was only temporary to begin with
		// and it has no value in terms of being in the path to the goal as it can lead the agent to lose the game.
		path.remove();
		
		return (iterations == 5000) ? null : path;
	}
	
	public ArrayList<Tile> getItemsRequired() {
		return itemsRequired;
	}
	
	/**
	 *  Determines if a state will be added to the priority queue or not depending on a variety of factors 
	 * @param tempPoint				Proposed Point
	 * @param canUseStone			Whether or not the agent is allowed to use  Stepping Stones
	 * @param tempInv				A copy of the player inventory
	 * @param getGold				Whether or not the agent is searching for gold
	 * @param usesStone				If the current path uses a stepping stone
	 * @return						True/False depending on if the state can be added to the queue
	 */
	private boolean addStateToQueue(Point tempPoint, boolean canUseStone, ArrayList<Tile> tempInv, boolean getGold, boolean usesStone) {
		if (tempPoint.x >= 160 || tempPoint.y >= 160) return false;
		if (canPassTile(tempPoint, canUseStone, tempInv, getGold, usesStone)) {
			if (map[tempPoint.y][tempPoint.x] != Tile.Water) {
				return true;
			} else if (map[tempPoint.y][tempPoint.x] == Tile.Water && tempInv.contains(Tile.StepStone)) {
				tempInv.remove(Tile.StepStone);
				return true;
			} else {
				return false;
			}
		}
		return false;
	}
	
	/**
	 * Returns a list of items required for the trip to gold given a path
	 * Example
	 * [StepStone, Key, StepStone, Axe]
	 * @param path			The path to be used to get to gold
	 * @param startPos		The current player position
	 * @return				List of items needed to get to gold
	 */
	private ArrayList<Tile> getItemsToReachGold(Queue<Move> path, Point startPos) {
		
		ArrayList<Tile> requiredItems = new ArrayList<Tile>();
		Point currPoint = new Point(startPos.x, startPos.y);
		
		for (Move m: path) {
			switch(m.d){
				case NORTH:
					currPoint.translate(0, 1);
					break;
				case SOUTH:
					currPoint.translate(0, -1);
					break;
				case EAST:
					currPoint.translate(1, 0);
					break;
				case WEST:
					currPoint.translate(-1, 0);
					break;
			}
			// If the point is outside the map the path is invalid
			if (currPoint.y >= 160 || currPoint.x >= 160) return null;

			// Determine which item is required to bypass the obstacle and add it to the list
			switch (map[currPoint.y][currPoint.x]) {
			case Water:
				requiredItems.add(Tile.StepStone);
				break;
			case Tree:
				if (!requiredItems.contains(Tile.Axe)) requiredItems.add(Tile.Axe);
				break;
			case Door:
				if (!requiredItems.contains(Tile.Key)) requiredItems.add(Tile.Key);
				break;
			case Unexplored:
				requiredItems.add(Tile.Unexplored); break;
			}
		}
		
		return requiredItems;
	}
	
	/**
	 * Returns a list of items still needed, also includes unexplored tiles.
	 * If a path relies on only unexplored tiles it is an invalid path.
	 * If a path relies on unexplored tiles after using a stepping stone it is again invalid.
	 * @param path			Queue of Move representing the proposed path
	 * @param startPos		Location of the Agent
	 * @return				List of items/unexplored tiles, if not empty path cannot be completed
	 */
	public ArrayList<Tile> itemsStillRequiredForTravel(Queue<Move> path, Point startPos) {
		ArrayList<Tile> items = getItemsToReachGold(path, startPos);
		// If items to gold returns null the path is impossible to complete
		if (items == null) {
			items = new ArrayList<Tile>();
			items.add(Tile.Unexplored);
			return items;
		}
		
		ArrayList<Tile> itemsLeft = deepClone(items);
		ArrayList<Tile> inventory = deepClone(playerInv);
		
		int numUnexploredTiles = 0;
		Tile first;
		boolean allUnexplored = true;
		boolean usingStone = false;
		
		// If the items/tiles left is not zero and is all Unexplored tiles
		// return the list as invalid (not empty)
		if (itemsLeft.size() > 1) {
			first = itemsLeft.get(0);
			if (first.equals(Tile.Unexplored)); {
				for (int i = 1; i < itemsLeft.size(); i++) {
					if (itemsLeft.get(i) != first) allUnexplored = false;
				}
				if (allUnexplored) {
					itemsLeft.add(Tile.Unexplored);
					return itemsLeft;
				}
			}
		}
		
		// Iterate through the tiles and remove them if necessary items are possessed
		// Unexplored tiles are not possible if a stepping stone is used
		for (Tile item: items) {
			if (item == Tile.Water && inventory.contains(Tile.StepStone)) {
				itemsLeft.remove(item);
				inventory.remove(Tile.StepStone);
				usingStone = true;
			}
			else if (item == Tile.Door && inventory.contains(Tile.Key))
				itemsLeft.remove(item);
			else if (item == Tile.Tree && inventory.contains(Tile.Axe))
				itemsLeft.remove(item);
			else if (item == Tile.Unexplored && !usingStone) {
				itemsLeft.remove(item);
			}
		}
		
		return itemsLeft;
	}
	
	/**
	 * Creates a deep copy of the inventory list passed in
	 * @param inventory Inventory list to be cloned
	 * @return Deep clone of the inventory list passed
	 */
	private ArrayList<Tile> deepClone(ArrayList<Tile> inventory) {
		ArrayList<Tile> newInventory = new ArrayList<Tile>();
		for (Tile currItem: inventory) {
			switch (currItem) {
			case StepStone: 
				newInventory.add(Tile.StepStone);
				break;
			case Axe: 
				newInventory.add(Tile.Axe);
				break;
			case Key: 
				newInventory.add(Tile.Key);
				break;
			case Unexplored:
				newInventory.add(Tile.Unexplored);
				break;
			case Water:
				newInventory.add(Tile.Water);
				break;
			case Door:
				newInventory.add(Tile.Door);
				break;
			}
		}
		return newInventory;
	}
	
	/**
	 * Returns the tile at an X/Y coord
	 * @param x			X coordinate to be queried
	 * @param y			Y coordinate to be queried
	 * @return			Type of tile at the location
	 */
	public Tile getTileAt(int x, int y) {
		return map[y][x];
	}
	
	/**
	 * Gets the location of a given tile
	 * @param tile		Tile to be searched for
	 * @return			Location of tile if it has been seen
	 */
	public Point getLocationTile(Tile tile) {
		for (int y = exploredHighBound.y; y >= exploredLowBound.y; y--) {
			for (int x = exploredLowBound.x; x <= exploredHighBound.x; x++) {
				if (x == currPos.x && y == currPos.y) {
					if (map[y][x] == tile) return new Point(x, y);
				}
			}
		}
		return null;
	}
	
	/**
	 * Returns a random move
	 * @return	A random move
	 */
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
	
	/**
	 * Gets the location of a given item type
	 * @param itemName			Type of item being searched for
	 * @return					Location of the item if, it has been seen by the agent
	 */
	public Point getItemPos(Tile itemName) {
		for (Item currItem: itemsOnMap) {
			if (currItem.getItemName() == itemName) {
				return currItem.getPos();
			}
		}
		return null;
	}
	
	/**
	 * Prints out the Agent's inventory
	 */
	public void printInventory() {
		//System.out.println("\n-------------\nInventory:");
		//System.out.println("Axe: " + playerInv.contains(Tile.Axe));
		//int stepStones = 0;
		//for (int i = 0; i < playerInv.size(); i++) {
		//	if (playerInv.get(i) == Tile.StepStone) stepStones += 1;
		//}
		//System.out.println("Step stone: " + stepStones);
		//System.out.println("Key: " + playerInv.contains(Tile.Key));
		//System.out.println("---------------");
	}
	
	/**
	 * Returns a boolean value indicating if the agent can pass a certain tile.
	 * For normal obstacles they can be passed if the appropriate item is possessed
	 * If the tile is unexplored it is not allowed if the agent is using the stone and trying to get to gold
	 * @param p						Location of tile to be checked
	 * @param canUseStone			If the agent is allowed to use stepping stones
	 * @param playerInv				A copy of the player's current inventory
	 * @param getGold				If the player is trying to get to gold
	 * @param usesStone				Whether the path uses a stone
	 * @return						Boolean indicating if the agent can pass throguh the tile
	 */
	private boolean canPassTile(Point p, boolean canUseStone, ArrayList<Tile> playerInv, boolean getGold, boolean usesStone) {
		switch (map[p.y][p.x]) {
			case Door:
			    return playerInv.contains(Tile.Key);
			case Water:
				if (!canUseStone) return false;
				return playerInv.contains(Tile.StepStone);
			case Wall:
				return false;
			case Unexplored:
				if (getGold == true && usesStone) return false;
				return true;
			case Tree:
				return playerInv.contains(Tile.Axe);
			default:
				return true;
		}
	}
	
	/**
	 * Returns a boolean indicating if the tile has been visited in the A* search
	 * @param visited			List of points visited so far
	 * @param pos				Current position
	 * @return					Boolean indicating if the point is in the visited list
	 */
	private boolean tileVisited(ArrayList<Point> visited, Point pos) {
		for (Point currPoint: visited) {
			if (currPoint.x == pos.x && currPoint.y == pos.y) return true;
		}
		return false;
	}
}
