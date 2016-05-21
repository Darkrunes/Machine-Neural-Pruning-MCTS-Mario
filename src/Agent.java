import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

/**
 * COMP3411 16s1 Assignment 3: Stepping
 * Description: Write an agent to play a simple text based game in which a rectangular map is navigated
 * and items are obtained to pass obstacles to reach the gold and return to the starting position. 
 * 
 * Group number: 13
 * @author Saffat Shams Akanda (z5061498), Richard Luong (z5061456)
 * 
 * Answer to the question:
 * Our program works by having the Agent perform an series of actions to accomplish a specific goal
 * which is determined by the behaviour that the agent is currently utilising. The behaviour used is purely 
 * chosen on whether there is a path to the gold or an item based on the agent's knowledge of the map so far.
 * 
 * Behaviours (Ordered based on priority):
 * - GetGold: Goal is to pick up the gold and then head back to the starting position
 * 	> Constraint 1: Cannot blindly return a path to the gold which contains unexplored tiles in it
 *  > Constraint 2: The agent can only travel on the path if it has the required items to bypass any obstacles in the way 
 * - GetItem: Goal is to pick up the specified item
 * - Explore: Goal is to explore the map up to the specified point
 *  > Constraint: Cannot use a stepping stone while exploring
 *
 * Algorithms:
 * - Flood fill exploration: Used to allow the map to be explored in a manner similar to depth first search to maximise 
 *	 the ratio of moves to tiles explored initially.
 * - A* search: Used to find an optimal path to the goal of the current behaviour using the Manhattan distance 
 *
 * Data structures used:
 * - Map: 2D array of 161 by 161 Tiles. The maximum map size is 80 x 80 so using an array is used for O(1) access
 * - Visited points on the map: Hashmap using the coordinates converted into a string as a key. Used for O(1) access
 * - A stack is used for storing points to explore for customised flood fill algorithm
 */
public class Agent {
	private Graph map;
	private Direction currDirection;
	private Point pos;
	private Point startPos;
	private ArrayList<Tile> inventory;
	private char lastAction;
	private boolean mapInitialised;
	private Queue<Move> exploreQueue;
	private Behaviour currBehaviour;
	private boolean goldFound;
	private Move lastMove;
<<<<<<< HEAD
	private Queue<Move> prevPathGold;
=======
>>>>>>> master
	
	/**
	 * Constructor
	 */
	public Agent() {
		map = new Graph();
		// Assume north is where ever we face at the start
		currDirection = Direction.NORTH;
		lastAction = 'n';
		pos = new Point(80, 80);
		startPos = pos;
		inventory = new ArrayList<Tile>();
		mapInitialised = false;
		exploreQueue = new LinkedList<Move>();
		prevPathGold = new LinkedList<Move>();
		goldFound = false;
		lastMove = null;
	}
	
	/**
	 * Decides the agent's behaviour based on what items the agent has seen, the path it is 
	 * currently on, whether it can reach the gold etc.
	 * @return A valid move the goal of the agent's behaviour that was chosen or previously set
	 */
	public Move decideBehaviours() {
		Move m;
		// let the agent complete its previous move if it only turned in a different direction
		if (lastMove != null) return lastMove;
		// First priority to is find the gold and go home
		m = goldCollection();
		if (m != null) return m;
		// Second priority is to get items that may help the agent get to the gold
		m = itemCollection();
		if (m != null) return m;
		// Third priority is explore the map
		if (exploreQueue.size() > 0 && currBehaviour.getBehaviour() == "Explore") {
			// Reset the behaviour to explore so that the agent can't cross water when exploring
			currBehaviour = new Explore(null, null, null);
<<<<<<< HEAD
			//System.out.println("On previous exploration");
=======
			System.out.println("On previous exploration");
>>>>>>> master
			m = exploreQueue.poll();
			if (map.isValidMove(m.d, currBehaviour.canUseStone())) return m;
			exploreQueue = new LinkedList<Move>();
		} else {
<<<<<<< HEAD
			//System.out.println("Going to start exploration!");
=======
			System.out.println("Going to start exploration!");
>>>>>>> master
			m = exploreUnvisited();
			if (m != null) return m;
			m = exploreRandomDirection();
			if (m != null) return m;
		}
		// Default mode is make a random move
		currBehaviour = new Explore(null, null, null);
		m = map.getRandomMove();
		while (!map.isValidMove(m.d, currBehaviour.canUseStone())) {
			m = map.getRandomMove();
		}
		return m;
	}
	
	/**
	 * If there is a path to the gold or the agent is holding the gold, return the next
	 * move to get the agent to its goal (Either to the gold or its starting position)
	 * @return Valid move to the goal
	 */
	private Move goldCollection() {
		if (map.itemSeen(Tile.Gold) || map.holdingItem(Tile.Gold)) {
			// If the path exists and it requires no additional items use it
			if (prevPathGold.size() != 0 && map.itemsStillRequiredForTravel(prevPathGold, pos).isEmpty()) {
				// Checks if the next move is valid
				if (map.isValidMove(prevPathGold.peek().d, currBehaviour.canUseStone())) {
					// If not facing the direction preserve the queue, as the agent has to rotate
					if (currDirection != prevPathGold.peek().d)
						return prevPathGold.peek();
					else
						return prevPathGold.poll();
				}
			}
			currBehaviour = new GetGold(map, inventory, startPos);
			Queue<Move> moves = map.astar(currBehaviour, currDirection);
			if (moves != null) {
				// If the current path requires no additional items save it to be reused
				if (map.itemsStillRequiredForTravel(moves, pos).isEmpty())
					prevPathGold = moves;
				return moves.poll();	
<<<<<<< HEAD
			}
=======
>>>>>>> master
		}
		return null;
	}
	
	/**
	 * There is no need to pick up any other items except for the stepping stone
	 * since the axe and key can be used unlimited times
	 * @return Valid move to the item
	 */
	private Move itemCollection() {
		if (map.itemSeen(Tile.StepStone)) {
			boolean canUseStone = true;
			currBehaviour = new GetItem(map, inventory, map.getItemPos(Tile.StepStone), canUseStone);
			Queue<Move> moves = map.astar(currBehaviour, currDirection);
			if (moves != null) return moves.poll();	
		}
		if (map.itemSeen(Tile.Key) && !map.holdingItem(Tile.Key)) {
			boolean canUseStone = true;
			currBehaviour = new GetItem(map, inventory, map.getItemPos(Tile.Key), canUseStone);
			Queue<Move> moves = map.astar(currBehaviour, currDirection);
			if (moves != null) return moves.poll();	
		}
		if (map.itemSeen(Tile.Axe) && !map.holdingItem(Tile.Axe)) {
			boolean canUseStone = true;
			currBehaviour = new GetItem(map, inventory, map.getItemPos(Tile.Axe), canUseStone);
			Queue<Move> moves = map.astar(currBehaviour, currDirection);
			if (moves != null) return moves.poll();	
		}
		return null;
	}
	
	/**
	 * If there are any tiles that the agent has seen but hasn't visited, find a path to it
	 * @return Valid move to the tile that the agent hasn't visited
	 */
	private Move exploreUnvisited() {
		Move m;
		Point nextPoint = map.getUnvisitedPoint();
		if (nextPoint != null) {
			currBehaviour = new Explore(map, inventory, nextPoint);
			exploreQueue = map.astar(currBehaviour, currDirection);
			if (exploreQueue != null) {
				m = exploreQueue.poll();
				if (m != null && map.isValidMove(m.d, currBehaviour.canUseStone())) return m;
			} else {
				exploreQueue = new LinkedList<Move>();
			}
		}
		return null;
	}
	
	/**
	 * Create an exploration path in a random direction and attempt to go to it
	 * @return Valid move to the new exploration path
	 */
	private Move exploreRandomDirection() {
		Move m;
<<<<<<< HEAD
=======
		System.out.println("Random search");
>>>>>>> master
		Random rand = new Random();
		Point toExplore = new Point();
		toExplore.x = 0xFFFFFF;
		toExplore.y = 0xFFFFFF;
<<<<<<< HEAD
		int randomInt = rand.nextInt(6); 
		int maxTiles = 10;
		int offset = 5;
		// Choose a direction to explore in
		switch (randomInt) {
		// Exploring to some point to right of the map
		case 0:
			toExplore.x = map.getExploredHigh().x + rand.nextInt(maxTiles) + offset;
			toExplore.y = map.getExploredHigh().y;
			break;
		// Exploring to some point to left of the map
		case 1:
			toExplore.x = map.getExploredHigh().x - rand.nextInt(maxTiles) - offset;
			toExplore.y = map.getExploredHigh().y;
			break;
		// Exploring to some point to top of the map
		case 2:
			toExplore.x = map.getExploredHigh().x;
			toExplore.y = map.getExploredHigh().y + rand.nextInt(maxTiles) + offset;
			break;
		// Exploring to some point to bottom of the map
		case 3:
			toExplore.x = map.getExploredHigh().x;
			toExplore.y = map.getExploredHigh().y - rand.nextInt(maxTiles) - offset;
			break;
		// Exploring to some point in top right of the map
		case 4:
			toExplore.x = map.getExploredHigh().x + rand.nextInt(maxTiles) + offset;
			toExplore.y = map.getExploredHigh().y + rand.nextInt(maxTiles) + offset;
			break;
		// Exploring to some point in the bottom left of the map
		case 5:
			toExplore.x = map.getExploredHigh().x - rand.nextInt(maxTiles) - offset;
			toExplore.y = map.getExploredHigh().y - rand.nextInt(maxTiles) - offset;
			break;
		// Exploring to some point within the current boundaries explored
		default:
			Point withinBoundary = map.getUnexplored();
			if (withinBoundary == null) break;
			toExplore.x = withinBoundary.x;
			toExplore.y = withinBoundary.y;
			break;
		}
		// Attempt to find a path to the point we want to explore
=======
		while (toExplore.x == 0xFFFFFF && toExplore.y == 0xFFFFFF) {
			int randomInt = rand.nextInt(6); 
			System.out.println("Rand: " + randomInt);
			int maxTiles = 10;
			int offset = 5;
			switch (randomInt) {
			// Exploring to some point to right of the map
			case 0:
				toExplore.x = map.getExploredHigh().x + rand.nextInt(maxTiles) + offset;
				toExplore.y = map.getExploredHigh().y;
				break;
			// Exploring to some point to left of the map
			case 1:
				toExplore.x = map.getExploredHigh().x - rand.nextInt(maxTiles) - offset;
				toExplore.y = map.getExploredHigh().y;
				break;
			// Exploring to some point to top of the map
			case 2:
				toExplore.x = map.getExploredHigh().x;
				toExplore.y = map.getExploredHigh().y + rand.nextInt(maxTiles) + offset;
				break;
			// Exploring to some point to bottom of the map
			case 3:
				toExplore.x = map.getExploredHigh().x;
				toExplore.y = map.getExploredHigh().y - rand.nextInt(maxTiles) - offset;
				break;
			// Exploring to some point in top right of the map
			case 4:
				System.out.println("Exploring upper");
				toExplore.x = map.getExploredHigh().x + rand.nextInt(maxTiles) + offset;
				toExplore.y = map.getExploredHigh().y + rand.nextInt(maxTiles) + offset;
				break;
			// Exploring to some point in the bottom left of the map
			case 5:
				System.out.println("Exploring lower");
				toExplore.x = map.getExploredHigh().x - rand.nextInt(maxTiles) - offset;
				toExplore.y = map.getExploredHigh().y - rand.nextInt(maxTiles) - offset;
				break;
			// Exploring to some point within the current boundaries explored
			default:
				Point withinBoundary = map.getUnexplored();
				if (withinBoundary == null) break;
				toExplore.x = withinBoundary.x;
				toExplore.y = withinBoundary.y;
				System.out.println("Exploring within");
				break;
			}
		}
		System.out.printf("Exploring to point (%d,%d)\n", toExplore.x, toExplore.y);
>>>>>>> master
		currBehaviour = new Explore(map, inventory, toExplore);
		exploreQueue = map.astar(currBehaviour, currDirection);
		if (exploreQueue != null) {
			m = exploreQueue.poll();
			if (m != null && map.isValidMove(m.d, currBehaviour.canUseStone())) return m;
		} else {
<<<<<<< HEAD
			// Reset the exploreQueue since no path was found
=======
>>>>>>> master
			exploreQueue = new LinkedList<Move>();
		}
		return null;
	}
	
	/**
	 * Determine which action the agent should take next and send the action to the server
	 * @param view 5 x 5 that the server provides the agent each turn
	 * @return The action that the agent would like to take
	 */
	public char get_action( char view[][] ) {
		// Initialise the map in the first turn
		if (!mapInitialised) {
			map.initialiseMap(view);
			mapInitialised = true;
		} 
		// Update the map or change the direction the agent is facing depending on the last action taken
		switch (lastAction) {
			case 'f':
				map.updateMap(view[0], currDirection);
				inventory = map.getItems();
				pos = map.getPlayerPos();
				break;
			case 'l':
			case 'r':
				currDirection = currDirection.changeDirection(lastAction);
				break;
		}
		// Show the map the agent knows of so far
<<<<<<< HEAD
		//map.displayMap();
		//System.out.println("+-----------------------+");
		//print_view(view);
		//System.out.println("+-----------------------+");
		// Get a move to perform depending on the behaviour that the agent will have
=======
		map.displayMap();
		System.out.println("+-----------------------+");
		print_view(view);
		System.out.println("+-----------------------+");
		
		// Slow down the agent so we can debug
		//createDelay(250);
		//map.printInventory();
>>>>>>> master
		Move m = decideBehaviours();
		if (m != null) {
			// Filter the move from a direction into an action
			lastAction = filterOutput(m, view);
			return lastAction;
		} 
		return 0;
	}

	/**
	 * Gets a move containing the direction that the agent wants to move and translates it into
	 * a game action to output
	 */
	private char filterOutput(Move m, char view[][]) {
		// Case where the Agent is moving forward where it may use an item its holding
		// to clear an obstacle before moving forward
		if (m.d == currDirection) {
			lastMove = m;
			// Cut down tree in the way
			if (view[1][2] == 'T' && inventory.contains(Tile.Axe)) return 'c';
			// Unlock door in the way
			if (view[1][2] == '-' && inventory.contains(Tile.Key)) return 'u';
			lastMove = null;
			return 'f';
		}
		// Case where the agent will need to change directions to be able to move to a specific tile
		if (m.d.changeDirection('r').changeDirection('r') == currDirection) {
			lastMove = m;
			return 'r';
		}
		if (m.d.changeDirection('r') == currDirection) {
			lastMove = m;
			return 'l';
		}
		if (m.d.changeDirection('l') == currDirection) {
			lastMove = m;
			return  'r';
		}
		
		return 0;
	}
	
	// Methods that came with the starter java file

	private void print_view( char view[][] )
	{
		int i,j;

		System.out.println("\n+-----+");
		for( i=0; i < 5; i++ ) {
			System.out.print("|");
			for( j=0; j < 5; j++ ) {
				if(( i == 2 )&&( j == 2 )) {
					System.out.print('^');
				}
				else {
					System.out.print( view[i][j] );
				}
			}
			System.out.println("|");
		}
		System.out.println("+-----+");
	}

	public static void main( String[] args )
	{
		InputStream in  = null;
		OutputStream out= null;
		Socket socket   = null;
		Agent  agent    = new Agent();
		char   view[][] = new char[5][5];
		char   action   = 'F';
		int port;
		int ch;
		int i,j;

		if( args.length < 2 ) {
			System.out.println("Usage: java Agent -p <port>\n");
			System.exit(-1);
		}

		port = Integer.parseInt( args[1] );

		try { // open socket to Game Engine
			socket = new Socket( "localhost", port );
			in  = socket.getInputStream();
			out = socket.getOutputStream();
		}
		catch( IOException e ) {
			System.out.println("Could not bind to port: "+port);
			System.exit(-1);
		}

		try { // scan 5-by-5 wintow around current location
			while( true ) {
				for( i=0; i < 5; i++ ) {
					for( j=0; j < 5; j++ ) {
						if( !(( i == 2 )&&( j == 2 ))) {
							ch = in.read();
							if( ch == -1 ) {
								System.exit(-1);
							}
							view[i][j] = (char) ch;
						}
					}
				}
				//agent.print_view( view ); // COMMENT THIS OUT BEFORE SUBMISSION
				action = agent.get_action( view );
				out.write( action );
			}
		}
		catch( IOException e ) {
			System.out.println("Lost connection to port: "+ port );
			System.exit(-1);
		}
		finally {
			try {
				socket.close();
			}
			catch( IOException e ) {}
		}
	}
}	
