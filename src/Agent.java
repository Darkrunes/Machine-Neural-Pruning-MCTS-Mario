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
	private ArrayList<Item> inventory;
	private char lastAction;
	private int turnNum;
	
	/**
	 * Constructor
	 */
	public Agent() {
		map = new Graph();
		// Assume north is where ever we face at the start
		currDirection = Direction.NORTH;
		char lastAction = 'n';
		pos = new Point(80, 80);
		inventory = new ArrayList<Item>();
		int turnNum = 0;
	}
	
	public void filterInput() {
		
	}
	
	public void decideBehaviours() {
		
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
			break;
		case 'l':
		case 'r':
			currDirection = currDirection.changeDirection(lastAction);
			System.out.println("Now facing direction: " + currDirection.charVal);
			break;
		}
		// Show the map the agent knows of so far
		map.displayMap();
		// REPLACE THIS CODE WITH AI TO CHOOSE ACTION

		int ch=0;

		System.out.print("Enter Action(s): ");

		try {
			while ( ch != -1 ) {
				// read character from keyboard
				ch  = System.in.read();

				switch( ch ) { // if character is a valid action, return it
				case 'F': case 'L': case 'R': case 'C': case 'U':
				case 'f': case 'l': case 'r': case 'c': case 'u':
					//if ((char) ch == 'f') {
						
					//}
					lastAction = (char) ch;
					return((char) ch );
				}
			}
		}
		catch (IOException e) {
			System.out.println ("IO error:" + e );
		}

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
				agent.print_view( view ); // COMMENT THIS OUT BEFORE SUBMISSION
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
