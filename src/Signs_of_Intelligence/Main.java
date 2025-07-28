package tictactoe;

import java.util.*;


public class Main {

    public static void main(String[] args) {
        // start the game
        GameRun run = new GameRun();
        run.gameLoop();
    }
}

/* rewrite the code with OOD */
abstract class  Player {
    protected char symbol;

    public Player(char symbol){
        this.symbol = symbol;
    }
    public abstract int makeMove(GameState state);
    public char getSymbol() {
        return symbol;
    }
}

class AIPlayer extends Player {
    private final AIStrategy strategy;
    public AIPlayer(char symbol, AIStrategy strategy) {
        super(symbol);
        this.strategy = strategy;
    }

    @Override
    public int makeMove(GameState state){
        int moveIndex = strategy.generateMove(state, getSymbol());
        System.out.println("Making move level \"" + strategy + "\"");
        return moveIndex;
    }
}

class HumanPlayer extends Player{
    public HumanPlayer(char symbol){
        super(symbol);
    }

    @Override
    public int makeMove(GameState state){
        InputHandler humanInput = Configuration.inputHandler;
        return humanInput.getValidMove(state.getBoard());
    }
}

interface AIStrategy {
    /**
     * User should choose the AI Strategy
     * @param state game state
     * @param aiSymbol aiPlayer symbol（'X' or 'O'）
     * @return moveIndex
     */
    int generateMove(GameState state, char aiSymbol);
}

class RandomStrategy implements AIStrategy {
    private final Random random = new Random();

    @Override
    public int generateMove(GameState state, char aiSymbol){
        List<Integer> avaIndices = state.getAvailableMoves();
        return avaIndices.get(random.nextInt(avaIndices.size()));
    }

    @Override
    public String toString() {
        return "easy";
    }
}

class MediumStrategy implements AIStrategy {

    @Override
    public int generateMove(GameState state, char aiSymbol) {
        //Winning Move
        int bestMove = findWinningMove(state, aiSymbol);
        if( bestMove != -1){
            return bestMove;
        }

        //Blocking Move
        char symbol = (aiSymbol == 'X') ? 'O' : 'X';
        bestMove = findWinningMove(state, symbol);
        if (bestMove != -1){
            return bestMove;
        }

        //Fallback Move
        return  new RandomStrategy().generateMove(state, aiSymbol);
    }

    private int findWinningMove(GameState state, char symbol) {
        char[] boardCopy = state.getBoard().clone();
        List<Integer> availableMoves = state.getAvailableMoves();

        for (int move : availableMoves) {
            // try to move every possible step
            boardCopy[move] = symbol;

            // check whether it is a winning step
            if (state.checkWin(boardCopy, symbol)) {
                return move;
            }
            //when failed, fallback
            boardCopy[move] = '_';
        }
        return -1;
    }

    @Override
    public String toString() {
        return "medium";
    }

}

class PlayerFactory{
    public Player createPlayer(String type, char symbol){
        return switch (type) {
            case "easy" -> new AIPlayer(symbol, new RandomStrategy());
            case "medium" -> new AIPlayer(symbol, new MediumStrategy());
            case "user" -> new HumanPlayer(symbol);
            default -> {
                System.out.print("Invalid Type!");
                yield null;
            }
        };
    }
}

class GameState {
    private final int size;
    private final char[] board;
    private final Set<Integer> endRowIndices = new HashSet<>();
    private boolean gameWinner;
    private static final int[][] winMap = new int[][]{
            {0,1,2}, {3,4,5}, {6,7,8}, // 行
            {0,3,6}, {1,4,7}, {2,5,8}, // 列
            {0,4,8}, {2,4,6}};

    public GameState(){
        this.size = Configuration.BOARD_SIZE;
        board = new char[size * size];
        initializeBoard();
    }

    private void initializeBoard(){
        Arrays.fill(board, '_');
        for(int i = 1; i <= size; i++){
            endRowIndices.add(i * size - 1);
        }
    }

    public void printBoard(){
        //start line
        int len = size * size;

        System.out.println("---------");
        for(int i = 0; i < len; i++){
            if(i % size == 0){
                System.out.print("| ");
            }
            if(board[i] ==  '_'){
                System.out.print("  ");
            }else{
                System.out.print(board[i] + " ");
            }
            if(endRowIndices.contains(i)){
                System.out.println("|");
            }
        }
        //end line
        System.out.println("---------");
    }

    public char[] getBoard(){
        return board;
    }

    public List<Integer> getAvailableMoves(){
        List<Integer> avaIndices = new ArrayList<>();
        int len = size * size;
        for(int i = 0; i < len; i++){
            if(board[i] == '_'){
                avaIndices.add(i);
            }
        }
        return avaIndices;
    }

    public void processMove(int moveIndex, char playerSymbol) {
        if(board[moveIndex] == '_'){
            board[moveIndex] = playerSymbol;
        }
    }

    public void setGameWinner(boolean gameWinner) {
        this.gameWinner = gameWinner;
    }

    public boolean isGameWinner() {
        return gameWinner;
    }

    public boolean checkWin(char[] boardCopy, char currentPlayerSymbol){

        for (int[] currentPath : winMap) {
            if (boardCopy[currentPath[0]] == currentPlayerSymbol
                    && boardCopy[currentPath[1]] == currentPlayerSymbol
                    && boardCopy[currentPath[2]] == currentPlayerSymbol) {
                    return  true;
                }
        }
        return false;
    }

    public boolean checkDraw(){
        return (!isGameWinner() && getAvailableMoves().isEmpty());
    }
}

class InputHandler {
    private static final Scanner sc = new Scanner(System.in);
    private final int boardSize;

    public InputHandler(){
        this.boardSize = Configuration.BOARD_SIZE;
    }

    public int getValidMove(char[] board){

        int index = -1;
        while(index == -1){
            System.out.print("Enter the coordinates: ");
            String[] coordinates = sc.nextLine().split("\\s+");
            try{
                int row = Integer.parseInt(coordinates[0]);
                int col = Integer.parseInt(coordinates[1]);
                if(isValidCoordinate(row, col)) {
                    index = convertToIndex(row, col);
                }
            } catch (Exception e) {
                System.out.println("You should enter numbers!");
            }

            if (isOccupiedPosition(index, board)){
                index = -1;
            }
        }
        return index;
    }

    public String[] gamePatternSelection(){
        Set<String> pattern = new HashSet<>(Arrays.asList("easy", "medium","user"));

        while(true){
            System.out.print("Input command: ");
            String commandStr = sc.nextLine().toLowerCase().trim();
            if (commandStr.equals("exit")){
                return null;
            }
            String[] userCommand = commandStr.split("\\s+");
            if(userCommand.length == 3
                    && userCommand[0].equals("start")
                    && pattern.contains(userCommand[1])
                    && pattern.contains(userCommand[2])){
                return userCommand;
            }else{
                System.out.println("Bad parameters!");
            }
        }
    }

    private  int convertToIndex(int row, int col){
        return (row - 1) * boardSize + col -1;
    }

    public boolean isValidCoordinate(int row, int col){
        if(row < 1 || row > boardSize || col < 1 || col > boardSize) {
            System.out.println("Coordinates should be from 1 to " + boardSize + "!");
            return false;
        }
        return true;
    }

    public boolean isOccupiedPosition(int index, char[] board){
        if(index >= 0 && board[index] !='_') {
            System.out.println("This cell is occupied! Choose another one!");
            return true;
        }
        return false;
    }
}

class GameRun{
    InputHandler inputHandler;
    private final Player[] players;
    private int currentPlayerIndex;
    private final GameState state;


    public GameRun(){
        inputHandler = Configuration.inputHandler;
        players = new Player[2];
        currentPlayerIndex = 0;
        state = new GameState();
    }

    public boolean gameInitialization(){
        String[] gamePattern = inputHandler.gamePatternSelection();
        if (gamePattern == null){
            return false;
        }else{
            PlayerFactory playerFactory = new PlayerFactory();
            players[0] = playerFactory.createPlayer(gamePattern[1], 'X');
            players[1] = playerFactory.createPlayer(gamePattern[2], 'O');
            return true;
        }
    }

    public void gameLoop(){
        if (!gameInitialization()){
            return;
        }

        state.printBoard();
        while(true){
            Player currentPlayer = players[currentPlayerIndex];
            int moveIndex = currentPlayer.makeMove(state);
            state.processMove(moveIndex, currentPlayer.getSymbol());
            state.printBoard();
            if(state.checkWin(state.getBoard(), currentPlayer.getSymbol())){
                state.setGameWinner(true);
                System.out.println(currentPlayer.getSymbol() + " wins");
                return;
            }
            if (state.checkDraw()){
                System.out.println("Draw");
                return;
            }
            switchPlayer();
        }
    }

    public void switchPlayer(){
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    }
}

class Configuration{
    public static final int BOARD_SIZE = 3;
    public static final InputHandler inputHandler = new InputHandler();
}
