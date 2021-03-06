package es.ucm.fdi.tp.assignment4.ataxx;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import es.ucm.fdi.tp.basecode.attt.AdvancedTTTFactory;
import es.ucm.fdi.tp.basecode.bgame.control.ConsoleCtrl;
import es.ucm.fdi.tp.basecode.bgame.control.ConsoleCtrlMVC;
import es.ucm.fdi.tp.basecode.bgame.control.Controller;
import es.ucm.fdi.tp.basecode.bgame.control.GameFactory;
import es.ucm.fdi.tp.basecode.bgame.control.Player;
import es.ucm.fdi.tp.basecode.bgame.model.AIAlgorithm;
import es.ucm.fdi.tp.basecode.bgame.model.Game;
import es.ucm.fdi.tp.basecode.bgame.model.GameError;
import es.ucm.fdi.tp.basecode.bgame.model.Piece;
import es.ucm.fdi.tp.basecode.connectN.ConnectNFactory;
import es.ucm.fdi.tp.basecode.ttt.TicTacToeFactory;

/**
 * This is the class with the main method for the board games application.
 * 
 * It uses the Commons-CLI library for parsing command-line arguments: the game
 * to play, the players list, etc. More information is available at
 * {@link https://commons.apache.org/proper/commons-cli/}
 * <p>
 * Esta es la clase con el m�todo main de inicio del programa. Se utiliza la
 * librer�a Commons-CLI para leer argumentos de la linea de �rdenes: el juego al
 * que se quiere jugar, la lista de jugadores, etc. Puedes encontrar m�s
 * informaci�n sobre esta librer�a en
 * {@link https://commons.apache.org/proper/commons-cli/} .
 */
public class Main {

	/**
	 * The possible views.
	 * <p>
	 * Vistas disponibles.
	 */
	enum ViewInfo {
		WINDOW("window", "Swing"), CONSOLE("console", "Console");

		private String id; // ViewInfo identifier
		private String desc; // ViewInfo description

		ViewInfo(String id, String desc) {
			this.id = id;
			this.desc = desc;
		}

		public String getId() {
			return id;
		}

		public String getDesc() {
			return desc;
		}

		@Override
		public String toString() {
			return id;
		}
	}

	/**
	 * The available games.
	 * <p>
	 * Juegos disponibles.
	 */
	enum GameInfo {
		CONNECTN("cn", "ConnectN"), TicTacToe("ttt", "Tic-Tac-Toe"), AdvancedTicTacToe("attt",
				"Advanced Tic-Tac-Toe"), Ataxx("ataxx", "Ataxx");

		private String id; // GameInfo identifier
		private String desc; // GameInfo description

		GameInfo(String id, String desc) {
			this.id = id;
			this.desc = desc;
		}

		public String getId() {
			return id;
		}

		public String getDesc() {
			return desc;
		}

		@Override
		public String toString() {
			return id;
		}

	}

	/**
	 * Player modes (manual, random, etc.)
	 * <p>
	 * Modos de juego.
	 */
	enum PlayerMode {
		MANUAL("m", "Manual"), RANDOM("r", "Random"), AI("a", "Automatics");

		private String id; // PlayerMode identifier
		private String desc; // PlayerMode description

		PlayerMode(String id, String desc) {
			this.id = id;
			this.desc = desc;
		}

		public String getId() {
			return id;
		}

		public String getDesc() {
			return desc;
		}

		@Override
		public String toString() {
			return id;
		}
	}

	/**
	 * Default game to play.
	 * <p>
	 * Juego por defecto.
	 */
	final private static GameInfo DEFAULT_GAME = GameInfo.CONNECTN;

	/**
	 * Default view to use.
	 * <p>
	 * Vista por defecto.
	 */
	final private static ViewInfo DEFAULT_VIEW = ViewInfo.CONSOLE;

	/**
	 * Default player mode to use.
	 * <p>
	 * Modo de juego por defecto.
	 */
	final private static PlayerMode DEFAULT_PLAYERMODE = PlayerMode.MANUAL;

	/**
	 * This field includes a game factory that is constructed after parsing the
	 * command-line arguments. Depending on the game selected with the -g option
	 * (by default {@link #DEFAULT_GAME}).
	 * <p>
	 * Este atributo incluye una factor�a de juego que se crea despu�s de
	 * extraer los argumentos de la linea de �rdenes. Depende del juego
	 * seleccionado con la opci�n -g (por defecto, {@link #DEFAULT_GAME}).
	 */
	private static GameFactory gameFactory;

	/**
	 * List of pieces provided with the -p option, or taken from
	 * {@link GameFactory#createDefaultPieces()} if this option was not
	 * provided.
	 * <p>
	 * Lista de fichas proporcionadas con la opci�n -p, u obtenidas de
	 * {@link GameFactory#createDefaultPieces()} si no hay opci�n -p.
	 */
	private static List<Piece> pieces;

	/**
	 * A list of players. The i-th player corresponds to the i-th piece in the
	 * list {@link #pieces}. They correspond to what is provided in the -p
	 * option (or using the default value {@link #DEFAULT_PLAYERMODE}).
	 * <p>
	 * Lista de jugadores. El jugador i-�simo corresponde con la ficha i-�sima
	 * de la lista {@link #pieces}. Esta lista contiene lo que se proporciona en
	 * la opci�n -p (o el valor por defecto {@link #DEFAULT_PLAYERMODE}).
	 */
	private static List<PlayerMode> playerModes;

	/**
	 * The view to use. Depending on the selected view using the -v option or
	 * the default value {@link #DEFAULT_VIEW} if this option was not provided.
	 * <p>
	 * Vista a utilizar. Dependiendo de la vista seleccionada con la opci�n -v o
	 * el valor por defecto {@link #DEFAULT_VIEW} si el argumento -v no se
	 * proporciona.
	 */
	private static ViewInfo view;

	/**
	 * {@code true} if the option -m was provided, to use a separate view for
	 * each piece, and {@code false} otherwise.
	 * <p>
	 * {@code true} si se incluye la opci�n -m, para utilizar una vista separada
	 * por cada ficha, o {@code false} en caso contrario.
	 */
	private static boolean multiviews;

	/**
	 * Number of rows provided with the option -d ({@code null} if not
	 * provided).
	 * <p>
	 * N�mero de filas proporcionadas con la opci�n -d, o {@code null} si no se
	 * incluye la opci�n -d.
	 */
	private static Integer dimRows;

	/**
	 * Number of columns provided with the option -d ({@code null} if not
	 * provided).
	 * <p>
	 * N�mero de columnas proporcionadas con la opci�n -d, o {@code null} si no
	 * se incluye la opci�n -d.
	 * 
	 */
	private static Integer dimCols;

	/**
	 * Number of obstacles provided with the option -o ({@code null} if not
	 * provided).
	 * <p>
	 * N�mero de obst�culos proporcionados con la opci�n -o, o {@code null} si
	 * no se incluye la opci�n -o.
	 * 
	 */
	private static Integer obstacles;

	/**
	 * The algorithm to be used by the automatic player. Not used so far, it is
	 * always {@code null}.
	 * <p>
	 * Algoritmo a utilizar por el jugador autom�tico. Actualmente no se
	 * utiliza, por lo que siempre es {@code null}.
	 */
	private static AIAlgorithm aiPlayerAlg;

	/**
	 * Processes the command-line arguments and modify the fields of this class
	 * with corresponding values. E.g., the factory, the pieces, etc.
	 * <p>
	 * Procesa la linea de �rdenes del programa y crea los objetos necesarios
	 * para los atributos de esta clase. Por ejemplo, la factor�a, las fichas,
	 * etc.
	 * 
	 * 
	 * @param args
	 *            Command line arguments.
	 *            <p>
	 *            Lista de argumentos de la linea de �rdenes.
	 * 
	 * 
	 */
	private static void parseArgs(String[] args) {

		// Define the valid command line options

		Options cmdLineOptions = new Options();
		cmdLineOptions.addOption(constructHelpOption()); // -h or --help
		cmdLineOptions.addOption(constructGameOption()); // -g or --game
		cmdLineOptions.addOption(constructViewOption()); // -v or --view
		cmdLineOptions.addOption(constructMlutiViewOption()); // -m or
																// --multiviews
		cmdLineOptions.addOption(constructPlayersOption()); // -p or --players
		cmdLineOptions.addOption(constructDimensionOption()); // -d or --dim
		cmdLineOptions.addOption(constructObstacleOption()); // -o or --obstacle

		// Parse the command line as provided in args

		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			parseHelpOption(line, cmdLineOptions);
			parseDimOptionn(line);
			parseObstacleOption(line);
			parseGameOption(line);
			parseViewOption(line);
			parseMultiViewOption(line);
			parsePlayersOptions(line);

			// If there are some remaining arguments, then something wrong is
			// provided in the command line!

			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining)
					error += (" " + o);
				throw new ParseException(error);
			}

		} catch (ParseException | GameError e) {
			// new Piece(...) might throw GameError exception
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	/**
	 * Builds the multiview (-m or --multiviews) CLI option.
	 * <p>
	 * Construye la opci�n CLI -m.
	 * 
	 * @return CLI {@link {@link Option} for the multiview option.
	 */

	private static Option constructMlutiViewOption() {
		return new Option("m", "multiviews", false,
				"Create a separate view for each player (valid only when using the " + ViewInfo.WINDOW + " view)");
	}

	/**
	 * Parses the multiview option (-m or --multiview). It sets the value of
	 * {@link #multiviews} accordingly.
	 * 
	 * <p>
	 * Extrae la opci�n multiview (-m) y asigna el valor de {@link #multiviews}.
	 * 
	 * @param line
	 *            CLI {@link CommandLine} object.
	 */
	private static void parseMultiViewOption(CommandLine line) {
		multiviews = line.hasOption("m");
	}

	/**
	 * Builds the view (-v or --view) CLI option.
	 * <p>
	 * Construye la opci�n CLI -v.
	 * 
	 * @return CLI {@link Option} for the view option.
	 *         <p>
	 *         Objeto {@link Option} de esta opci�n.
	 */
	private static Option constructViewOption() {
		String optionInfo = "The view to use ( ";
		for (ViewInfo i : ViewInfo.values()) {
			optionInfo += i.getId() + " [for " + i.getDesc() + "] ";
		}
		optionInfo += "). By defualt, " + DEFAULT_VIEW.getId() + ".";
		Option opt = new Option("v", "view", true, optionInfo);
		opt.setArgName("view identifier");
		return opt;
	}

	/**
	 * Parses the view option (-v or --view). It sets the value of {@link #view}
	 * accordingly.
	 * <p>
	 * Extrae la opci�n view (-v) y asigna el valor de {@link #view}.
	 * 
	 * @param line
	 *            CLI {@link CommandLine} object.
	 * @throws ParseException
	 *             If an invalid value is provided (the valid values are those
	 *             of {@link ViewInfo}.
	 */
	private static void parseViewOption(CommandLine line) throws ParseException {
		String viewVal = line.getOptionValue("v", DEFAULT_VIEW.getId());
		// View type
		for (ViewInfo v : ViewInfo.values()) {
			if (viewVal.equals(v.getId())) {
				view = v;
			}
		}
		if (view == null) {
			throw new ParseException("Unknown view '" + viewVal + "'");
		}
	}

	/**
	 * Builds the players (-p or --player) CLI option.
	 * <p>
	 * Construye la opci�n CLI -p.
	 * 
	 * @return CLI {@link Option} for the list of pieces/players.
	 *         <p>
	 *         Objeto {@link Option} de esta opci�n.
	 */
	private static Option constructPlayersOption() {
		String optionInfo = "A player has the form A:B (or A), where A is sequence of characters (without any whitespace) to be used for the piece identifier, and B is the player mode (";
		for (PlayerMode i : PlayerMode.values()) {
			optionInfo += i.getId() + " [for " + i.getDesc() + "] ";
		}
		optionInfo += "). If B is not given, the default mode '" + DEFAULT_PLAYERMODE.getId()
				+ "' is used. If this option is not given a default list of pieces from the corresponding game is used, each assigned the mode '"
				+ DEFAULT_PLAYERMODE.getId() + "'.";

		Option opt = new Option("p", "players", true, optionInfo);
		opt.setArgName("list of players");
		return opt;
	}

	/**
	 * Parses the players/pieces option (-p or --players). It sets the value of
	 * {@link #pieces} and {@link #playerModes} accordingly.
	 * <p>
	 * Extrae la opci�n players (-p) y asigna el valor de {@link #pieces} y
	 * {@link #playerModes}.
	 * 
	 * @param line
	 *            CLI {@link CommandLine} object.
	 * @throws ParseException
	 *             If an invalid value is provided (@see
	 *             {@link #constructPlayersOption()}).
	 *             <p>
	 *             Si se proporciona un valor inv�lido (@see
	 *             {@link #constructPlayersOption()}).
	 */
	private static void parsePlayersOptions(CommandLine line) throws ParseException {

		String playersVal = line.getOptionValue("p");

		if (playersVal == null) {
			// If no -p option, we take the default pieces from the
			// corresponding factory, and for each one we use the default player
			// mode.
			pieces = gameFactory.createDefaultPieces();
			playerModes = new ArrayList<PlayerMode>();
			for (int i = 0; i < pieces.size(); i++) {
				playerModes.add(DEFAULT_PLAYERMODE);
			}
		} else {
			pieces = new ArrayList<Piece>();
			playerModes = new ArrayList<PlayerMode>();
			String[] players = playersVal.split(",");
			for (String player : players) {
				String[] playerInfo = player.split(":");
				if (playerInfo.length == 1) { // Only the piece name is provided
					pieces.add(new Piece(playerInfo[0]));
					playerModes.add(DEFAULT_PLAYERMODE);
				} else if (playerInfo.length == 2) { // Piece name and mode are
														// provided
					pieces.add(new Piece(playerInfo[0]));
					PlayerMode selectedMode = null;
					for (PlayerMode mode : PlayerMode.values()) {
						if (mode.getId().equals(playerInfo[1])) {
							selectedMode = mode;
						}
					}
					if (selectedMode != null) {
						playerModes.add(selectedMode);
					} else {
						throw new ParseException("Invalid player mode in '" + player + "'");
					}
				} else {
					throw new ParseException("Invalid player information '" + player + "'");
				}
			}
		}
	}

	/**
	 * Builds the game (-g or --game) CLI option.
	 * <p>
	 * Construye la opci�n CLI -g.
	 * 
	 * @return CLI {@link {@link Option} for the game option.
	 *         <p>
	 *         Objeto {@link Option} de esta opci�n.
	 */

	private static Option constructGameOption() {
		String optionInfo = "The game to play ( ";
		for (GameInfo i : GameInfo.values()) {
			optionInfo += i.getId() + " [for " + i.getDesc() + "] ";
		}
		optionInfo += "). By defualt, " + DEFAULT_GAME.getId() + ".";
		Option opt = new Option("g", "game", true, optionInfo);
		opt.setArgName("game identifier");
		return opt;
	}

	/**
	 * Parses the game option (-g or --game). It sets the value of
	 * {@link #gameFactory} accordingly. Usually it requires that
	 * {@link #parseDimOptionn(CommandLine)} has been called already to parse
	 * the dimension option.
	 * <p>
	 * Extrae la opci�n de juego (-g). Asigna el valor del atributo
	 * {@link #gameFactory}. Normalmente necesita que se haya llamado antes a
	 * {@link #parseDimOptionn(CommandLine)} para extraer la dimensi�n del
	 * tablero.
	 * 
	 * @param line
	 *            CLI {@link CommandLine} object.
	 * @throws ParseException
	 *             If an invalid value is provided (the valid values are those
	 *             of {@link GameInfo}).
	 *             <p>
	 *             Si se proporciona un valor inv�lido (los valores v�lidos son
	 *             los de {@link GameInfo}).
	 */
	private static void parseGameOption(CommandLine line) throws ParseException {
		String gameVal = line.getOptionValue("g", DEFAULT_GAME.getId());
		GameInfo selectedGame = null;

		for (GameInfo g : GameInfo.values()) {
			if (g.getId().equals(gameVal)) {
				selectedGame = g;
				break;
			}
		}

		if (selectedGame == null) {
			throw new ParseException("Unknown game '" + gameVal + "'");
		}

		switch (selectedGame) {
		case Ataxx:
			if (dimRows != null && dimCols != null && dimRows == dimCols) {
				if (obstacles != null) {
					gameFactory = new AtaxxFactory(dimRows, obstacles);
				} else {
					gameFactory = new AtaxxFactory(dimRows, 0);
				}
			} else {
				gameFactory = new AtaxxFactory();
			}
			break;
		case AdvancedTicTacToe:
			gameFactory = new AdvancedTTTFactory();
			break;
		case CONNECTN:
			if (dimRows != null && dimCols != null && dimRows == dimCols) {
				gameFactory = new ConnectNFactory(dimRows);
			} else {
				gameFactory = new ConnectNFactory();
			}
			break;
		case TicTacToe:
			gameFactory = new TicTacToeFactory();
			break;
		default:
			throw new UnsupportedOperationException("Something went wrong! This program point should be unreachable!");
		}

	}

	/**
	 * Builds the dimension (-d or --dim) CLI option.
	 * <p>
	 * Construye la opci�n CLI -d.
	 * 
	 * @return CLI {@link {@link Option} for the dimension.
	 *         <p>
	 *         Objeto {@link Option} de esta opci�n.
	 */
	private static Option constructDimensionOption() {
		return new Option("d", "dim", true,
				"The board size (if allowed by the selected game). It must has the form ROWSxCOLS.");
	}

	/**
	 * Parses the dimension option (-d or --dim). It sets the value of
	 * {@link #dimRows} and {@link #dimCols} accordingly. The dimension is
	 * ROWSxCOLS.
	 * <p>
	 * Extrae la opci�n dimensi�n (-d). Asigna el valor de los atributos
	 * {@link #dimRows} y {@link #dimCols}. La dimensi�n es de la forma
	 * ROWSxCOLS.
	 * 
	 * @param line
	 *            CLI {@link CommandLine} object.
	 * @throws ParseException
	 *             If an invalid value is provided.
	 *             <p>
	 *             Si se proporciona un valor inv�lido.
	 */
	private static void parseDimOptionn(CommandLine line) throws ParseException {
		String dimVal = line.getOptionValue("d");
		if (dimVal != null) {
			try {
				String[] dim = dimVal.split("x");
				if (dim.length == 2) {
					dimRows = Integer.parseInt(dim[0]);
					dimCols = Integer.parseInt(dim[1]);
				} else {
					throw new ParseException("Invalid dimension: " + dimVal);
				}
			} catch (NumberFormatException e) {
				throw new ParseException("Invalid dimension: " + dimVal);
			}
		}

	}

	/**
	 * Builds the help (-h or --help) CLI option.
	 * <p>
	 * Construye la opci�n CLI -h.
	 * 
	 * @return CLI {@link {@link Option} for the help option.
	 *         <p>
	 *         Objeto {@link Option} de esta opci�n.
	 */

	private static Option constructHelpOption() {
		return new Option("h", "help", false, "Print this message");
	}

	/**
	 * Parses the help option (-h or --help). It print the usage information on
	 * the standard output.
	 * <p>
	 * Extrae la opci�n help (-h) que imprime informaci�n de uso del programa en
	 * la salida est�ndar.
	 * 
	 * @param line
	 *            * CLI {@link CommandLine} object.
	 * @param cmdLineOptions
	 *            CLI {@link Options} object to print the usage information.
	 * 
	 */
	private static void parseHelpOption(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	/**
	 * Builds the obstacle (-o or --obstacle) CLI option.
	 * <p>
	 * Construye la opci�n CLI -o.
	 * 
	 * @return CLI {@link {@link Option} for the game option.
	 *         <p>
	 *         Objeto {@link Option} de esta opci�n.
	 */

	private static Option constructObstacleOption() {
		return new Option("o", "obstacle", true,
				"The number of obstacles that will appear in each quadrant of the board. It must be an integer.");

	}

	/**
	 * Parses the obstacle option (-o or --obstacle). It adds randomly obstacles
	 * to a quadrant and duplicates them simetrically.
	 * <p>
	 * Extrae la opci�n obst�culo (-o) que a�ade obst�culos aleatoriamente a un
	 * cuadrante del tablero y despu�s los duplica de manera sim�trica en los
	 * restantes.
	 * 
	 * @param line
	 *            * CLI {@link CommandLine} object.
	 */
	private static void parseObstacleOption(CommandLine line) throws ParseException {
		String numObstacles = line.getOptionValue("o");
		if (numObstacles != null) {
			try {
				obstacles = Integer.parseInt(numObstacles);
			} catch (NumberFormatException e) {
				throw new ParseException("Invalid number: " + numObstacles);
			}
		}
	}

	/**
	 * Starts a game using a {@link ConsoleCtrl} which is not based on MVC. Is
	 * used only for teaching the difference from the MVC one.
	 * <p>
	 * M�todo para iniciar un juego con el controlador {@link ConsoleCtrl}, no
	 * basado en MVC. S�lo se utiliza para mostrar las diferencias con el
	 * controlador MVC.
	 * 
	 */
	public static void startGameNoMVC() {
		Game g = new Game(gameFactory.gameRules());
		Controller c = null;

		switch (view) {
		case CONSOLE:
			ArrayList<Player> players = new ArrayList<Player>();
			for (int i = 0; i < pieces.size(); i++) {
				switch (playerModes.get(i)) {
				case AI:
					players.add(gameFactory.createAIPlayer(aiPlayerAlg));
					break;
				case MANUAL:
					players.add(gameFactory.createConsolePlayer());
					break;
				case RANDOM:
					players.add(gameFactory.createRandomPlayer());
					break;
				default:
					throw new UnsupportedOperationException(
							"Something went wrong! This program point should be unreachable!");
				}
			}
			c = new ConsoleCtrl(g, pieces, players, new Scanner(System.in));
			break;
		case WINDOW:
			throw new UnsupportedOperationException(
					"Swing Views are not supported in startGameNoMVC!! Please use startGameMVC instead.");
		default:
			throw new UnsupportedOperationException("Something went wrong! This program point should be unreachable!");
		}

		c.start();
	}

	/**
	 * Starts a game. Should be called after {@link #parseArgs(String[])} so
	 * some fields are set to their appropriate values.
	 * <p>
	 * Inicia un juego. Debe llamarse despu�s de {@link #parseArgs(String[])}
	 * para que los atributos tengan los valores correctos.
	 * 
	 */
	public static void startGame() {
		Game g = new Game(gameFactory.gameRules());
		Controller c = null;

		switch (view) {
		case CONSOLE:
			ArrayList<Player> players = new ArrayList<Player>();
			for (int i = 0; i < pieces.size(); i++) {
				switch (playerModes.get(i)) {
				case AI:
					players.add(gameFactory.createAIPlayer(aiPlayerAlg));
					break;
				case MANUAL:
					players.add(gameFactory.createConsolePlayer());
					break;
				case RANDOM:
					players.add(gameFactory.createRandomPlayer());
					break;
				default:
					throw new UnsupportedOperationException(
							"Something went wrong! This program point should be unreachable!");
				}
			}
			c = new ConsoleCtrlMVC(g, pieces, players, new Scanner(System.in));
			gameFactory.createConsoleView(g, c);
			break;
		case WINDOW:
			throw new UnsupportedOperationException(
					"Swing " + (multiviews ? "Multiviews" : "Views") + " are not supported yet! ");
		default:
			throw new UnsupportedOperationException("Something went wrong! This program point should be unreachable!");
		}

		c.start();
	}

	/**
	 * The main method. It calls {@link #parseArgs(String[])} and then
	 * {@link #startGame()}.
	 * <p>
	 * M�todo main. Llama a {@link #parseArgs(String[])} y a continuaci�n inicia
	 * un juego con {@link #startGame()}.
	 * 
	 * @param args
	 *            Command-line arguments.
	 * 
	 */
	public static void main(String[] args) {
		parseArgs(args);
		startGame();
	}

}
