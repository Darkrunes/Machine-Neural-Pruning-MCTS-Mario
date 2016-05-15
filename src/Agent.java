import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.*;

public class Agent {
	private Graph map;
	private Direction currDirection;
	private Point pos;
	private Point startPos;
	private ArrayList<Tile> inventory;
	private char lastAction;
	private int turnNum;
	private Queue<Move> exploreQueue;
	private Behaviour currBehaviour;
	
	/**
	 * Constructor
	 */
	public Agent() {
		map = new Graph();
		// Assume north is where ever we face at the start
		currDirection = Direction.NORTH;
		char lastAction = 'n';
		pos = new Point(80, 80);
		startPos = pos;
		inventory = new ArrayList<Tile>();
		turnNum = 0;
		exploreQueue = new LinkedList<Move>();
	}
	
	public void filterInput() {
		
	}
	
	public Move decideBehaviours() {
		Move m;
		// First priority to is find the gold and go home
		if (map.itemSeen(Tile.Gold) || map.holdingItem(Tile.Gold)) {
			if (map.itemSeen(Tile.Gold)) System.out.println("Going to gold");
			if (map.holdingItem(Tile.Gold)) System.out.println("Going home");
			currBehaviour = new GetGold(map, inventory, startPos);
			Queue<Move> moves = map.astar(currBehaviour, currDirection);
			if (moves != null)
				return moves.poll();	
		}
		// Second priority is to get items that may help the agent get to the gold
		if (map.itemSeen(Tile.StepStone)) {
			System.out.println("Going to get step stone");
			currBehaviour = new GetItem(map, inventory, map.getItemPos(Tile.StepStone));
			Queue<Move> moves = map.astar(currBehaviour, currDirection);
			if (moves != null) return moves.poll();	
		}
		if (map.itemSeen(Tile.Key)) {
			System.out.println("Going to get key");
			currBehaviour = new GetItem(map, inventory, map.getItemPos(Tile.Key));
			Queue<Move> moves = map.astar(currBehaviour, currDirection);
			if (moves != null) return moves.poll();	
		}
		if (map.itemSeen(Tile.Axe)) {
			System.out.println("Going to get axe");
			currBehaviour = new GetItem(map, inventory, map.getItemPos(Tile.Axe));
			Queue<Move> moves = map.astar(currBehaviour, currDirection);
			if (moves != null) return moves.poll();	
		}
		// Third priority is explore the map
		if (exploreQueue.size() > 0) {
			// Reset the behaviour to explore so that the agent can't cross water when exploring
			currBehaviour = new Explore(null, null, null);
			System.out.println("On previous exploration");
			m = exploreQueue.poll();
			if (map.isValidMove(m.d, currBehaviour.canUseStone())) return m;
			exploreQueue = new LinkedList<Move>();
		} else {
			System.out.println("Going to start exploration!");
			Random rand = new Random();
			Point toExplore = new Point();
			if (rand.nextInt(2) == 0) {
				toExplore.x = map.getExploredHigh().x;
				toExplore.y = map.getExploredHigh().y;
				toExplore.x += rand.nextInt(10) + 2;
				toExplore.y += rand.nextInt(10) + 2;
			} else {
				toExplore.x = map.getExploredLow().x;
				toExplore.y = map.getExploredLow().y;
				toExplore.x -= rand.nextInt(10) - 2;
				toExplore.y -= rand.nextInt(10) - 2;
			}
			currBehaviour = new Explore(map, inventory, toExplore);
			exploreQueue = map.astar(currBehaviour, currDirection);
			if (exploreQueue != null) {
				m = exploreQueue.poll();
				if (m != null && map.isValidMove(m.d, currBehaviour.canUseStone())) return m;
			} else {
				exploreQueue = new LinkedList<Move>();
			}
		}
		// Default mode is explore mode (Making random moves to explore the map)
		m = map.getRandomMove();
		while (!map.isValidMove(m.d, currBehaviour.canUseStone())) {
			m = map.getRandomMove();
		}
		return m;
		
	}
	
	public boolean isValidMove() {
		return true;
	}
	
	public Direction returnOutput() {
		return Direction.NONE;
	}
	
	public char get_action( char view[][] ) {
		turnNum += 1;
		// Initialise the map in the first turn
		if (turnNum == 1) {
			map.initialiseMap(view);
		} 
		// Update the map or change directions 
		switch (lastAction) {
			case 'f':
				map.updateMap(view[0], currDirection);
				inventory = map.getItems();
				pos = map.getPlayerPos();
				break;
			case 'l':
			case 'r':
				currDirection = currDirection.changeDirection(lastAction);
				System.out.println("Now facing direction: " + currDirection.charVal);
				break;
		}
		// Show the map the agent knows of so far
		map.displayMap();
		System.out.println("+-----------------------+");
		print_view(view);
		System.out.println("+-----------------------+");
		// Slow down the agent so we can debug
		try {
		    Thread.sleep(250);                
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
		map.printInventory();
		Move m = decideBehaviours();
		if (m != null) {
			lastAction = filterOutput(m);
			System.out.println("Behaviour = " + currBehaviour.toString());
			System.out.println("Can use stone? " + currBehaviour.canUseStone());
			return lastAction;
		} 
		else {
			int ch=0;
	
			System.out.print("Enter Action(s): ");
	
			try {
				while ( ch != -1 ) {
					// read character from keyboard
					ch  = System.in.read();
	
					switch( ch ) { // if character is a valid action, return it
					case 'F': case 'L': case 'R': case 'C': case 'U':
					case 'f': case 'l': case 'r': case 'c': case 'u':
						lastAction = (char) ch;
						return((char) ch );
					}
				}
			}
			catch (IOException e) {
				System.out.println ("IO error:" + e );
			}
		}
		return 0;
	}

	private char filterOutput(Move m) {
		if(m.d == currDirection) {
			return 'f';
		}
		
		if (m.d.changeDirection('r').changeDirection('r') == currDirection)
			return 'r';
		
		if (m.d.changeDirection('r') == currDirection)
			return 'l';
		
		if (m.d.changeDirection('l') == currDirection)
			return  'r';
		
		return 0;
	}

	void print_view( char view[][] )
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
