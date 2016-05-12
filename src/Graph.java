import java.awt.Point;
import java.util.*;

public class Graph {
	private Tile[][] map;
	private ArrayList<Item> itemsOnMap;
	private ArrayList<Item> playerInv;		// very hacky change later
	private Point currPos;
	private Point exploredLowBound;
	private Point exploredHighBound;
	
	public Graph() {
		map = new Tile[160][160];
		itemsOnMap = new ArrayList<Item>();
		playerInv = new ArrayList<Item>();
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
	public boolean isValidMove(Direction currDirection) {
		switch (currDirection) {
		case NORTH:
			if (Tile.isItem(map[currPos.y+1][currPos.x])) return true;
			if (map[currPos.y+1][currPos.x] == Tile.Empty) return true;
			break;
		case SOUTH:
			if (Tile.isItem(map[currPos.y-1][currPos.x])) return true;
			if (map[currPos.y-1][currPos.x] == Tile.Empty) return true;
			break;
		case EAST:
			if (Tile.isItem(map[currPos.y][currPos.x+1])) return true;
			if (map[currPos.y][currPos.x+1] == Tile.Empty) return true;
			break;
		case WEST:
			if (Tile.isItem(map[currPos.y][currPos.x-1])) return true;
			if (map[currPos.y][currPos.x-1] == Tile.Empty) return true;
			break;	
		}
		return false;
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
			playerInv.add(new Item(map[currPos.y][currPos.x], 0, new Point(0,0)));
			map[currPos.y][currPos.x] = Tile.Empty;
		} 
	}
	
	public Point getPlayerPos() {
		return currPos;
	}
	
	public ArrayList<Item> getItems() {
		// SUUUUPER hacky change later
		return playerInv;
	}
	
	public ArrayList<Item> itemsOnMap() {
		// HACKY AS F REPLACE
		return itemsOnMap;
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
		State currState = new State(null, currDirection, this.currPos, currBehaviour, this, playerInv, goal);
		PriorityQueue<State> pq = new PriorityQueue<State>();
		pq.add(currState);
		
		while (true) {
			currState = pq.poll();
			currentNode = currState.getPos();
			currDirection = currState.getDirection();
			if(currentNode.equals(goal)) break;
			if (currentNode.x > 160 || currentNode.y > 160) continue;
			
			switch(currDirection) {
				case NORTH:
					// Tile above
					pq.add(new State(currState, currDirection, new Point(currentNode.x, currentNode.y + 1),
							currBehaviour, this, playerInv, goal));
					// Tile below
					pq.add(new State(currState, currDirection.changeDirection('r').changeDirection('r'),
							new Point(currentNode.x, currentNode.y - 1), currBehaviour, this, playerInv, goal));
					// Tile left
					pq.add(new State(currState, currDirection.changeDirection('l'), new Point(currentNode.x - 1, currentNode.y),
							currBehaviour, this, playerInv, goal));
					//Tile Right
					pq.add(new State(currState, currDirection.changeDirection('r'), new Point(currentNode.x + 1, currentNode.y),
							currBehaviour, this, playerInv, goal));
					break;
					
				case SOUTH:
					// Tile above
					pq.add(new State(currState, currDirection.changeDirection('r').changeDirection('r'),
							new Point(currentNode.x, currentNode.y + 1), currBehaviour, this, playerInv, goal));
					// Tile below
					pq.add(new State(currState, currDirection, new Point(currentNode.x, currentNode.y - 1),
							currBehaviour, this, playerInv, goal));
					// Tile left
					pq.add(new State(currState, currDirection.changeDirection('r'), new Point(currentNode.x - 1, currentNode.y), 
							currBehaviour, this, playerInv, goal));
					//Tile Right
					pq.add(new State(currState, currDirection.changeDirection('l'), new Point(currentNode.x + 1, currentNode.y),
							currBehaviour, this, playerInv, goal));
					break;
				case EAST:
					// Tile above
					pq.add(new State(currState, currDirection.changeDirection('l'), new Point(currentNode.x, currentNode.y + 1),
							currBehaviour, this, playerInv, goal));
					// Tile below
					pq.add(new State(currState, currDirection.changeDirection('r'),new Point(currentNode.x, currentNode.y - 1),
							currBehaviour, this, playerInv, goal));
					// Tile left
					pq.add(new State(currState, currDirection.changeDirection('r').changeDirection('r'),
							new Point(currentNode.x - 1, currentNode.y), currBehaviour, this, playerInv, goal));
					//Tile Right
					pq.add(new State(currState, currDirection,
							new Point(currentNode.x + 1, currentNode.y), currBehaviour, this, playerInv, goal));
					break;
				case WEST:
					// Tile above
					pq.add(new State(currState, currDirection.changeDirection('r'), new Point(currentNode.x, currentNode.y + 1),
							currBehaviour, this, playerInv, goal));
					// Tile below
					pq.add(new State(currState, currDirection.changeDirection('l'),	new Point(currentNode.x, currentNode.y - 1),
							currBehaviour, this, playerInv, goal));
					// Tile left
					pq.add(new State(currState, currDirection,
							new Point(currentNode.x - 1, currentNode.y), currBehaviour, this, playerInv, goal));
					//Tile Right
					pq.add(new State(currState, currDirection.changeDirection('r').changeDirection('r'),
							new Point(currentNode.x + 1, currentNode.y),
							currBehaviour, this, playerInv, goal));
					break;
				default:
					break;
			}
			
			// Hacky method to return null on no path
			iterations++;
			if (iterations == 10000) break;
		}
		
		//currState.printPath();
		
		return (iterations == 10000) ? null : currState.getPath();
	}
	
	public Tile getTileAt(int x, int y) {
		return map[y][x];
	}
}
