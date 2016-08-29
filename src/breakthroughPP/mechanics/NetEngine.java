package breakthroughPP.mechanics;

import breakthroughPP.preset.*;
import breakthroughPP.gui.Showable;
import breakthroughPP.simpleIO.*;
import breakthroughPP.board.Storable;
import breakthroughPP.players.*;
import breakthroughPP.gui.MainFrame;

import java.rmi.registry.*;
import java.rmi.*;

import java.net.*;
import java.net.UnknownHostException; 
					
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

/** 
 * This class provides the functionality to produce a players of different types such as an
 * interactive player and different computer opponents. Those players can locally play on one
 * computer as well as on different computers if the network is appropriate for this purpuse. The
 * last requires the choice between being a guest simply offering a player or being a host which
 * additionally administrates the gameing process. 
 * For all possibilies this executable class offers different switches which can be set using
 * command line arguments. Functions to administrate a game are completely inherited from {@link
 * breakthroughPP.mechanics.Engine}.
 * 
 * command line options are:
 * -d							Shows the board of the engine and additional information about the
 *								state of the game, default is off
 * -s <rows> <columns>			Sets the dimensions of the board, default: 26 26
 * -m IP|SC|AC|AI IP|SC|AC|AI   Sets the types of players: 
 *									IP: Interactive Player
 *									SC: Simple computer player (random moves)
 *									AC: advanced computer player
 *									AI: artificial intelligent player
 *								default: IP IP
 * -b <int> <int>				Defines the way of input and output for the Players specified with
 *								option "-m"
 *									0:	IP:			input and output inside the terminal, default
 *										SC|AC|AI:	no input or output at all, default
 *									1:	IP:			uses a gui as input and output method
 *										SC|AC|AI	output in the terminal
 * -w <int>						Allowed only with -m SC|AC|AI SC|AC|AI option only. The integer 
 *								defines the delay time in millisconds which is added to the move to
 *								delay an opponent's move, default: 0 ms
 *									with -n local: shows both players on one gui
 *									with -n offer|receive (<IP>): shows a gui of color of the used 
 *												player
 * -l <fileName>				Load a game, with stored board dimensions and used player
 *								types. Allowed in combination "-n" local. There will not be any
 *								outputs except the one given by option "-d", meaning terminal in
 *								and output only
 * -n offer|receive (<IP>)|local Allows playing over network:
 *								offer: offer a player at the current computer (guest), this player
 *								accepts the board size of the receiving player
 *								receive (<IP>): search for a player at the optional <IP> and
 *									run the game (host). If <IP> is not specified, the user will 
 *									be asked to enter it afterwards
 *								local: Play locally, default option
 *								the player used on the receiving computer or offered is the
 *									first argument of "-m"
 * -t <int>						Approximate time in s waiting for a partner for "-n receive <IP>":
 *								default is 10 s
 * -c							In case your are on a linux system you can make the terminal colorful,
 *								everywhere else you get an default output, default is off.
 *
 * Georg-August University Goettingen
 * APP breakthroughPP
 * SoSe 2016
 * Gr CodeSalat
 * @author J.B.
*/	
public class NetEngine extends Engine {

	// Enumerations to make the code better readable
	private enum NetOptions{LOCAL, OFFER, RECEIVE};
	private enum ShowOptions{SHOWMAX, SHOWMIN};
	
	// additional switches to those 
	private ShowOptions[] showOption;
	private PlayerType[] oppChoice;
	private NetOptions netOption;
	private String remoteName;
	private int maxTime;
	private int waitingTime;



// ==== Constructors ==============================================================================
	/** 
	 * Default construktor
	*/
	public NetEngine(){
		// Setting of the Defaults
		showOption		= new ShowOptions[2];
		showOption[0]	= ShowOptions.SHOWMIN;
		showOption[1]	= ShowOptions.SHOWMIN;
		oppChoice		= new PlayerType[2];
		oppChoice[0]	= PlayerType.INTERACTIVE;
		oppChoice[1]	= PlayerType.INTERACTIVE;
		netOption		= NetOptions.LOCAL;
		remoteName		= "";
		maxTime			= 10; //s
		waitingTime		= 0;  // ms
	}

// ==== Getter ====================================================================================
	public String getRemote(){
		return remoteName;
	}

	public NetOptions getNetOption(){
		return netOption;
	}

	public int getMaxTime(){
		return maxTime;
	}

	public PlayerType[] getMode(){ 
		return oppChoice; 
	}

	public ShowOptions[] getShowOption(){
		return showOption;
	}

// ==== Object funktions ==========================================================================
	/** 
	 * This function can be seen as an addition to {@link
	 * breakthroughPP.mechanics.Engine#setOptions(String[] args)} with regard to the amount of
	 * possible switchen a user can set. It processes its input in the same way but collects all the
	 * commands, where no matching input token is found. These non matching Arguments are transfered
	 * to the {@link breakthroughPP.mechanics.Engine#setOptions(String[] args)} in case it belongs
	 * to one of the old arguments
	 * @param args comand line arguments
	 * @throws IllegalArgumentException in case  {@link breakthroughPP.mechanics.Engine#setOptions(String[] args)}
	 * throws one or if for the new options there are either to few or arguments not defined as
	 * parameter for this game
	 */
	@Override 
	protected void setOptions(String[] args) throws IllegalArgumentException{
		// List for collecting non matching command line arguments
		List<String> collect = new LinkedList<String>();

		// Evaluate
		if(args != null){
			int n=0;
			while(n < args.length){

				// netOption field
				if(args[n].equals("-n")){
					if(++n >= args.length){
						throw new IllegalArgumentException("Not enough arguments for option \"-n\"");
					}

					if(args[n].equals("local")){
						netOption = NetOptions.LOCAL;
					}else if(args[n].equals("offer")){
						netOption = NetOptions.OFFER;
					}else if(args[n].equals("receive")){
						netOption = NetOptions.RECEIVE;
					}else{
						throw new IllegalArgumentException("Illegal argument " + args[n] + " for" +
							" option \"-n\"");
					}
					
					// Read inputline IP of the guest if possible
					if(netOption == NetOptions.RECEIVE && n+1 < args.length && !args[n+1].startsWith("-")){
						remoteName = args[++n];
					}
				}

				// Chose different kind of output and input methods
				else if(args[n].equals("-b")){
					int tmp;
					int tmp1;
					try{
						tmp = readNumber(args,++n);
						tmp1= readNumber(args,++n);
					}catch(IllegalArgumentException iae){
						throw new IllegalArgumentException("Illegal arguments for option " +
							"\"-b\"\n"+iae.getMessage());
					}

					switch(tmp){
						case 0: showOption[0] = ShowOptions.SHOWMIN; break;
						case 1: showOption[0] = ShowOptions.SHOWMAX; break;
						default: throw new IllegalArgumentException("First argument " + tmp + " is not"+
										" valid for option -b");
					}

					switch(tmp1){
						case 0: showOption[1] = ShowOptions.SHOWMIN; break;
						case 1: showOption[1] = ShowOptions.SHOWMAX; break;
						default: throw new IllegalArgumentException("Second argument " + tmp1 + " is not"+
										" valid for option -b");
					}
				}

				// Types of opponents, modification of next args
				else if(args[n].equals("-m")){

					// Get the two player types playing against each other
					try{
						oppChoice[0] = PlayerType.fromString(args[++n]);
					}catch(IllegalArgumentException | IndexOutOfBoundsException iae){
						throw new IllegalArgumentException("Illegal first argument for option " +
							"\"-m\"\n"+iae.getMessage());
					}
					try{ 
						oppChoice[1] = PlayerType.fromString(args[++n]);
					}catch(IllegalArgumentException | IndexOutOfBoundsException iae){
						throw new IllegalArgumentException("Illegal second argument for option " +
							"\"-m\"\n"+iae.getMessage());
					}
				}

				// Set the time waiting for an opponent
				else if(args[n].equals("-t")){
					try{
						maxTime = readNumber(args,++n);
					}catch(IllegalArgumentException iae){
						throw new IllegalArgumentException("Illegal argument for option \"-t\"" +
							"\n" +iae.getMessage());
					}
					if(maxTime <= 0){
						throw new IllegalArgumentException("Illegal argument for option \"-t\"" +
							"\nThe argument must be an integer greater than zero");
					}
				}

				// Waiting time after a move for non IP Player
				else if(args[n].equals("-w")){
					try{
						waitingTime = readNumber(args,++n);
					}catch(IllegalArgumentException iae){
						throw new IllegalArgumentException("Illegal argument for option \"-w\"" +
							"\n" +iae.getMessage());
					}
					if(waitingTime <= 0){
						throw new IllegalArgumentException("Illegal argument for option \"-w\"" +
							"\nThe argument must be an non negative integer if it is set");
					}
				}
				

				// Gather the non fitting arguments
				else{
					// put non fitting arguments in a list to hand them away
					collect.add(args[n]);
				}

				n++;
			}
		}

		// call the super function for the List arguments
		// no conflict with -l option as this takes place after the 
		if(collect.isEmpty()){
			super.setOptions(null);	// Activation of the runGame() function
		}else{
			super.setOptions(collect.toArray(new String[0])); // process additional options and
				// activate afterwards
		}

		// Load a file and net != LOCAL cannot be used at the same time
		if(getFileName() != null && netOption != NetOptions.LOCAL){
			throw new IllegalArgumentException("Illegal combination of options. \"-n\" and receive or"+
				" offer cannot be chosen in combination with \"-l\"");
		}


		// Load a file and net != LOCAL cannot be used at the same time
		if(waitingTime != 0 && (oppChoice[0] == PlayerType.INTERACTIVE || oppChoice[1] == PlayerType.INTERACTIVE)){
			throw new IllegalArgumentException("Illegal combination of options. \"-w\" and "+
				" -m option IP not allowed");
		}
	}

	/**
	 * This method creates the specified IOobjects from the chosen options of -b for the Interfaces 
	 * {@link breakthroughPP.preset.Requestable} and {@link breakthroughPP.gui.Showable} and set them
	 * as input and output method respectively. The first player e.g. in -m option uses the
	 * specification of the first argument of option -b. The second player alike. The interactive player
	 * receive in every case an own gui or text output and input.
     * @param index first (0) or second (1) argument of -m or -b option
	 * @param color color of the player 
	 * @param store Instance to store, null if storing should be disabled
	*/
	private void prepareIOForPlayer(int index, int color, Storable store){
			MainFrame mf = new MainFrame(null,color,store);// null as the player must not
														// have access to the game board of the engine
														// initialisation takes place during
														// initialsation of the players in
														// AbstactPlayer init(...)
			Requestable ti = new TextInput(this); // this is a Storable
			Showable to = new TextOutput(getColorTerminal());

		// Interactive player with gui
		if(oppChoice[index] == PlayerType.INTERACTIVE && showOption[index] == ShowOptions.SHOWMAX){
			// two IAPlayer with gui output
			if(oppChoice[0] == oppChoice[1] && showOption[0] == showOption[1] && 
				netOption == NetOptions.LOCAL){
				if(color == RED || color == 2){
					setInputMethodRed(mf);				
					setOutputMethodRed(mf);
					setInputMethodBlue(mf);
					setOutputMethodBlue(null);
				}
			}
			// other pairing or over network		
			else{
				if(color == RED){
					setInputMethodRed(mf);
					setOutputMethodRed(mf);
				}else if(color == BLUE){
					setInputMethodBlue(mf);
					setOutputMethodBlue(mf);
				}
			}
		}
		// Interactive without gui but with Textinput
		else if(oppChoice[index] == PlayerType.INTERACTIVE && showOption[index] == ShowOptions.SHOWMIN){
			// Prevent two outputs in the terminal
			if(((getInputMethodRed() != null && showOption[0] != ShowOptions.SHOWMAX) ||
			   (getInputMethodBlue() != null && showOption[1] != ShowOptions.SHOWMAX)) &&
				netOption == NetOptions.LOCAL){
				to = null;
				// use the same input instance if possible
				if(getInputMethodRed() != null){
					ti = getInputMethodRed();
				}else if(getInputMethodBlue() != null){
					ti = getInputMethodBlue();
				}
			}

			if(color == RED){
				setInputMethodRed(ti);
				setOutputMethodRed(to);
			}else if(color == BLUE){
				setInputMethodBlue(ti);
				setOutputMethodBlue(to);
			}
			waitingTime = 0;
		}
		// w switch set
		else if(waitingTime != 0){
			// inform the engine about the delay time
			setMoveDelay(waitingTime);
	
			// Show only one gui for local or offer
			if(netOption == NetOptions.LOCAL || netOption == NetOptions.OFFER){
				if(color == RED || color == 2){
					setInputMethodRed(null);
					setOutputMethodRed(mf);
				}else if(color == BLUE){
					setInputMethodBlue(null);
					setOutputMethodBlue(null);
				}
			}
			// show the gui for the player running on the receiving computer
			else{
				if(color == RED || color == 2){
					setInputMethodRed(null);
					setOutputMethodRed(null);
				}else if(color == BLUE){
					setInputMethodBlue(null);
					setOutputMethodBlue(mf);
				}
			}
		}
		// non Interactive player with output
		else if(showOption[index] == ShowOptions.SHOWMAX){
			// Prevent two outputs in the terminal
			if((getInputMethodRed() != null && showOption[0] != ShowOptions.SHOWMAX) ||
			   (getInputMethodBlue() != null && showOption[1] != ShowOptions.SHOWMAX)){
				to = null;
			}

			if(color == RED){
				setInputMethodRed(null);
				setOutputMethodRed(to);
			}else if(color == BLUE){
				setInputMethodBlue(null);
				setOutputMethodBlue(to);
			}
		}
		// non interactive player without output
		else{
			if(color == RED){
				setInputMethodRed(null);
				setOutputMethodRed(null);
			}else if(color == BLUE){
				setInputMethodBlue(null);
				setOutputMethodBlue(null);
			}
		}
	}
 
	/** 
	 * Initilization of game according to the choosen players. Both players do not have access to
	 * the board of the (Net)Engine. It expands and uses {@link
	 * breakthroughPP.mechanics.Engine#init()}.If the game is played in -n mode the offered player 
	 * needs to be initilised before starting the game by the computer hosting the game.
     *
	 * In case of playing over network, an offered player accepts the conditions of the game
	 * provided by the receiveing player. For this reason, offering a player does not include the process
	 * of final initilisation of a player because the offered/guest player does not know the size of
	 * the board the receiving player wants to play with. The engine of the
	 * receiving player needs to complete this initialisation.
	 *
	 * By initialising the players the players must also initialise there IOmethods with the
	 * function {@link breakthroughPP.gui.Showable#init(Viewer viewer, int color)} provided by the
	 * interface {@link breakthroughPP.gui.Showable} since the must not have access to the board of
	 * the engine and must pocess an own board, where those IO work on.
	 *
	 * In case of two players playing locally on a single gui, the initiliastion of the player must
	 * fullfil the folowing requirement. If the player is initialised with color int 2, the color of
	 * the player must be set to red and the its board to two player usage. Color int 0 (RED) and 1
	 * (BLUE) lead to boards o which just the red or the blue player can play on.
     *
     * These requirements are fulfilled in this function and in {@link
     * breakthroughPP.players.AbstractPlayer}.
	 * @return if this instance should control the game true else false
	 * @throws RemoteException if remote players cannot be initialised
	 * @throws Exception if something else fails
	*/
	@Override
	public boolean init() throws RemoteException, Exception, IllegalStateException{

		showDebug("Starting Initialisation of NetEngine instance");

		// Definition of the return value of this function
		boolean returnvalue=false;

		// call init of Engine
		returnvalue = super.init();
		// init of the Board
		// showGui
		// Debug oppChoice
		// 2 InterActivePlayers

		// has there been a load operation/ option -l <filename>
		if(getFileName() != null){
			return returnvalue;
		}

		// Override IO
		setInputMethodRed(null);
		setOutputMethodRed(null);
		setInputMethodBlue(null);
		setOutputMethodBlue(null);

		// Overwriting the players
		switch(netOption){
			case OFFER:	// Offer red player over network
				
					// Set IO for Players
					prepareIOForPlayer(0,RED,null);

					// disable storing 
					setEnableStore(false);
		
					// Create the Player
					setRedPlayer(createTypeOfPlayer(oppChoice[0],RED));
					Player n = new NetPlayer(getRedPlayer());

					// Offer a player
					offer(n,"CodeSalat-Player");
					
					returnvalue = false;
					break;
			case RECEIVE: // Confirm IP or type in a new one
					int choice = 1;
					String[] options ={"Connect","New Input"};
					do{
						if(!remoteName.equals("")){
							choice = JOptionPane.showOptionDialog(null, //Component parentComponent
								"Do you really want to connect to IP: " + remoteName,
								"Choose an option",				//String title
								JOptionPane.YES_NO_OPTION, 
								JOptionPane.INFORMATION_MESSAGE,//int messageType
								null,							//Icon icon,
								options,						//Object[] options,
								"Connect");						//Object initialValue
						}
						if(choice != 0 ){
							remoteName = JOptionPane.showInputDialog("Please input a String " +
								"containing the IP"); 	
						}
					}while(!remoteName.equals("") && choice != 0);
					

					// Find a remote partern to play with. In case of r being an IAPlayer calling
					// init(...) upon also initialises the gui with the specification of the
					// receiving player 
					Player r = findPartner(remoteName);
					if(r != null){
						System.out.println("Another player was found");
						r.init(getColumns(),getRows(),RED);	
						setRedPlayer(r);	
					}else{
						System.err.println("Sorry, but there was noone waiting for me in the last "+
							maxTime + " Seconds.");
						System.exit(1);
					}


					// Creating the blue player
		
					// Set IO for blue
					prepareIOForPlayer(0,BLUE,null);

					// disable storing 
					setEnableStore(false);	

					// Create Player
					setBluePlayer(createTypeOfPlayer(oppChoice[0],BLUE));	

					returnvalue = true;
					break;

			default: // Default oppChoice, play locally
					


					// disable storing 
					setEnableStore(true);	

					// Red Player
					// Condition: eiter -w switch or a shared gui between interactive players
					if(waitingTime != 0 || (oppChoice[0] == oppChoice[1] && showOption[0] == showOption[1] &&
							 oppChoice[0] == PlayerType.INTERACTIVE && 
							 showOption[0] == ShowOptions.SHOWMAX)){
					
								// Set IO for the red player	
								prepareIOForPlayer(0,NONE,this);	// this is Storable

								setRedPlayer(createTypeOfPlayer(oppChoice[0],NONE));
					}
					else{

						// Set IO for the red player	
						prepareIOForPlayer(0,RED,this);	// this is Storable

						setRedPlayer(createTypeOfPlayer(oppChoice[0],RED));
					}

					// Set IO for the blue player
					prepareIOForPlayer(1,BLUE,this);	// this is Storable
					
					// Blue Player
					setBluePlayer(createTypeOfPlayer(oppChoice[1],BLUE));

					returnvalue = true;
					break;

			}
	
		showDebug((getRedPlayer() != null ? 
			"Initialisation ot NetEngine finished: ":"Initialisation of NetEngine failed: "));
		if(getRedPlayer() == null){
			throw new IllegalStateException("Problems with initialising the players. Red one is null");
		}
		return returnvalue;
	}


	/** 
	 * Output of the game parameters
	*/
	@Override
	public String toString(){
		return super.toString() +
			"remote computer name: " + (remoteName == null ? "" : remoteName) + "\n\t" +
			"Network: " + netOption + "\n\t" +			
			"ShowOption for player 1: " + showOption[0] + "\n\t" +  
			"ShowOption for player 2: " + showOption[1] + "\n\t" +  
			"Waiting time (s): " + maxTime + "\n\t" +
			"Delay time (ms): " + waitingTime + "\n";
	}

	/**
	 * Creates a player by its player type and initializes its color. This includes the
	 * initialisation of the board of the player by calling {@link
	 * breakthroughPP.preset.Player#init(int XDim, int YDim, int color)}. For this purpose the function
	 * {@link breakthroughPP.players.PlayerFactory#create(IOTransfer inouts, PlayerType playerType,
	 * int color)} is used. 
	 * @param type type of the player which should be produced
	 * @param color color of the player which should be produced
	 * @return an initialised player of the given type and color
	 * @throws RemoteException if during initialisation an exception occures (see {@link
	 * breakthroughPP.preset.Player#init(int XDim, int YDim, int color)})
     * @throws Exception if one occures in {@link
	 * breakthroughPP.preset.Player#init(int XDim, int YDim, int color)}
     */
	private AbstractPlayer createTypeOfPlayer(PlayerType type, int color) throws RemoteException, Exception {
		Player player = PlayerFactory.getInstance().create(this, type, color);
		player.init(getColumns(),getRows(), color);
		return (AbstractPlayer) player;
	}

// ---- Registry functions -------------------------------------------------------------------------
	/** 
	 * Registers a player at the rmiregistry at port 1099 and puts out the IP address of the
	 * computer offering the player
	 * @param p Player who should be registered 
	 * @param name name of the player to play with
	*/
	private void offer(Player p, String name){
		try{
			System.setProperty("java.rmi.server.hostname","127.0.0.1"); // to avoid problems with
							// IPs and JVM IPs
			Registry rmi = LocateRegistry.createRegistry(1099); // Starts the rmiregistry
			rmi.rebind(name,p);
			
			System.out.println("If you want to play with me, use the following address: " +
				InetAddress.getLocalHost().getHostAddress());	
		}catch(RemoteException rex){
			System.err.println("Rebinding of a Player failed. Check if the rmiregistry runs.");
			setRedPlayer(null);	
		}catch(UnknownHostException uhe){
			System.err.println("IP address could not be found but player is successfully offered");
			setRedPlayer(null);	
		}
	}

	/**
	 * Search for the player with known name at a computer with a given name.
	 * @param computername name of the computer where a player is registered
	 * @param name name of the registered player
	 * @return a reference to the remote player object or null in case of failure
	*/
	private Player find(String computername, String name){
		Player p = null;
		try{
			p= (Player) Naming.lookup("rmi://" + computername + "/" + name);
		}catch(NotBoundException nbe){
			System.err.println(nbe.getMessage());
		}catch(MalformedURLException mfe){
			System.err.println("Host "+ computername + " and name " + name + " cannot build a valid" +
				" URL"); 
			return null;
		}catch(RemoteException rex){
			System.err.println("lookup of a Player failed");
			return null;
		}
		return p;
	}


	/**
	 * This function looks up the names of registered remote players at the given computer an
	 * connects to the first fitting one. The approximate maximal time this function searches
	 * for an offered remote player is specified in maxTime switch. If one partner to play with is
	 * found, its reference is returned. 
	 * @return a Player Object or null if no one has been found
	 * @param computername name of the computer where a player is registered
	*/
	private Player findPartner(String computername){
		showDebug("Calling the findPartner(" + computername + ")");
		String[] potential_partners = new String[0];

		int t=0;
		// Search for partner
		while(t< 2*maxTime && potential_partners.length == 0){
			// Check if there is someone else
			try{
				potential_partners = findList(computername);
			}catch(Exception e){
				// As one continues to search for a partner one can ignore single errors
				// in case noone is found. Final failure to find someone is indicated by return value null
				System.err.println("No partners found after approx " + (t*2) + " seconds"); 
			}

			for(int i=0; i<potential_partners.length; i++){
				showDebug("Found potential partner: " + potential_partners[i]);
			}

			// Wait a half second
			if(t < 2*maxTime -1 && potential_partners.length == 0){	// Last wait ignored
				try{
					Thread.sleep(500);
				}catch(InterruptedException ie){
					System.err.println(ie.getMessage());
				} 
			}
	
			t++;
		}


		// Is there anyone to play with 
		if(potential_partners.length == 0){
			return null;
		}else{
			int i=0;
			Player partner = null;
			while(partner == null && i< potential_partners.length){
				String[] sep = potential_partners[i].split("/");
				partner = find(remoteName,sep[sep.length-1]);
			}
			return partner;
		}
	}

	/** 
	 * Method to list the registered players of a computer
	 * @param computername name of the computer where a player is registered
	*/
	private String[] findList(String computername){
		try{
			String[] l = Naming.list("rmi://" + computername +"/");
			return l;
		}catch(MalformedURLException mfe){
			System.err.println(mfe.getMessage());
			return new String[0];
		}catch(RemoteException rex){
			System.err.println(rex.getMessage());
			return new String[0];
		}
	}

// ==== Main functions ============================================================================
	/** 
	 * Excecutable function for starting a new Game
	 * @param args Command line inputs
	*/
	public static void main(String[] args){
		NetEngine nen = new NetEngine();
		
		try{
			// set User Options
			nen.setOptions(args);
		}catch(IllegalArgumentException iae){
			System.err.println("==================================================================\n" +
				"breakthroughPP.mechanics.NetEngine can have the following options:\n\n"+
				"Available options:\n" +
				"-d \t \t \t Shows the board of the engine and additional information about the "+
								" state of the game, default is off \n"+
 				"-s <rows> <columns>   Sets the dimensions of the board, default: 26 26\n"+
				"-m IP|SC|AC|AI IP|SC|AC|AI   \t Sets the types of players: \n"+
				"\t \t \t \t IP: Interactive Player\n"+
				"\t \t \t \t SC: Simple computer player (random moves)\n"+
 				"\t \t \t \t AC: advanced computer player\n" +
 				"\t \t \t \t AI: artificial intelligent player\n" +
 				"\t \t \t \t default: IP IP\n" +
				"-b <int> <int>	\t Defines the way of input and output for the Players specified with" +
								" option \"-m\"\n" +
				"\t \t \t \t 0: IP: input and output inside the terminal, default\n"+
				"\t \t \t \t    SC|AC|AI: no input or output at all, default\n"+
				"\t \t \t \t 1: IP: uses a gui as input and output method\n"+
				"\t \t \t \t    SC|AC|AI output in the terminal\n" +
				"-w <int> \t \t Allowed only with -m SC|AC|AI SC|AC|AI option only. The integer defines"+ 
							" the delay time in millisconds \n\t \t \t which is added to the move "+ 
							"to delay an opponents move, default: 0 ms\n"+
				"\t \t \t \t    -n local: shows both players on one gui\n"+
				"\t \t \t \t    -n offer|receive (<IP>): shows a gui of color of the used player\n"+
				"-l <fileName> \t \t Load a game, with stored board dimensions and used player" +
							" types. Allowed in combination -n local.\n" +
				"\t \t \t There will not be any outputs except the one given by option \"-d\", "+
							" meaning terminal in- and output only\n"+
				"-n offer|receive (<IP>)|local activate playing over network:\n" +
 				"\t \t \t \t offer: offer a player at the current computer (guest), this player "+
									"accepts the board size of the receiving player\n" +
 				"\t \t \t \t receive (<IP>): search for a player at the optional <IP> and run the "+
							"game (host). \n" +
				"\t \t \t \t \t If <IP> is not specified, the user will be asked to enter"+
							"it afterwards\n" +
 				"\t \t \t \t local: Play locally, default option\n" +
 				"\t \t \t \t the player used on the receiving computer or offered is the first " +
								"argument of \"-m\"\n"+
				"-t <int> \t \t Approximate time in s waiting for a partner for \"-n receive <IP>\":" + 
							"default is 10 s\n"+
				"-c	\t \t \t In case your are on a linux system you can make the terminal colorful,"+
 							" everywhere else this has no effect, default is off" +
				"\n\nGame programmed by CodeSalat\n");
			System.err.println("\nThe error you have produced:\n"+iae.getMessage());
			System.err.println("==================================================================");
			return;
		}

		boolean initialize = false;
		try{
				initialize = nen.init();
				nen.showDebug(nen.toString());	
		}catch(IllegalStateException ise){
			System.err.println("Failure due to improper usage. Mind the order how this functions "+
				" need to be called");
		}catch(RemoteException re){
			System.err.println("Failure to connect with an remote object. Exiting.");
			System.exit(1);
		}catch(Exception re){
			System.err.println("Failure to initialise the game. Exiting.");
			System.exit(1);
		}
		if(initialize){
			nen.runGame();

			// Close eventually open guis
			try{
				Thread.sleep(5000);
			}catch(InterruptedException ie){
			}
			// Clean exit 
			System.exit(0);
		}


	} 


}
