package tictactoe;

import java.util.*;

public class Main {

    public static void main(String[] args) {
        // write your code here
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
    private final Random random = new Random();
    public AIPlayer(char symbol) {
        super(symbol);
    }

    @Override
    public int makeMove(GameState state){
        List<Integer> avaIndices = state.getAvailableMoves();
        int moveIndex = random.nextInt(avaIndices.size());
        state.setBoard(moveIndex, getSymbol());
        System.out.println("Making move level \"easy\"");

        return moveIndex;
    }
}

class HumanPlayer extends Player{
    public HumanPlayer(char symbol){
        super(symbol);
    }

    @Override
    public int makeMove(GameState state){
        InputHandler humanInput = new InputHandler();
        int moveIndex = humanInput.getValidMove(state);
        state.setBoard(moveIndex, getSymbol());

        return moveIndex;
    }
}

class GameState {
    int size;
    private char[] board;
    private boolean gameOver;
    private boolean gameWinner;

    public GameState(int size){
        this.size = size;
        board = new char[size * size];
        initializeBoard();
    }

    private void initializeBoard(){
        Arrays.fill(board, '_');
    }

    public void printBoard(){
        //start line
        System.out.println("---------");
        for(int i = 0; i < 9; i++){
            if(i % 3 == 0){
                System.out.print("| ");
            }
            if(board[i] ==  '_'){
                board[i] = '_';
                System.out.print("  ");
            }else{
                System.out.print(board[i] + " ");
            }
            if(i == 2 || i == 5 || i == 8){
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

    public boolean isGameOver(){
        return gameOver;
    }

    public void setBoard(int moveIndex, char playerSymbol) {
        board[moveIndex] = playerSymbol;
    }

    public void setGameOver(boolean gameOver){
        this.gameOver = gameOver;
    }

    public void setGameWinner(boolean gameWinner){this.gameWinner = gameWinner;}

    public boolean isGameWinner() { return gameWinner; }

    public int getSize() {
        return size;
    }
}

class InputHandler {
    public int getValidMove(GameState state){
        Scanner sc = new Scanner(System.in);
        int index = -1;
        while(index == -1){
            System.out.print("Enter the coordinates: ");
            String[] coordinates = sc.nextLine().split("\\s+");
            try{
                int row = Integer.parseInt(coordinates[0]);
                int col = Integer.parseInt(coordinates[1]);
                if(isValidCoordinate(row, col)) {
                    index = convertToIndex(row, col, state.getSize());
                }
            } catch (Exception e) {
                System.out.println("You should enter numbers!");
            }

            if (isOccupiedPosition(index, state.getBoard())){
                index = -1;
            }
        }
        return index;
    }

    private  int convertToIndex(int row, int col, int boardSize){
        return (row - 1) * boardSize + col -1;
    }

    public boolean isValidCoordinate(int row, int col){
        if(row < 1 || row > 3 || col < 1 || col > 3) {
            System.out.println("Coordinates should be from 1 to 3!");
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
    static final private int BOARD_SIZE = 3;
    HumanPlayer humanPlayer = new HumanPlayer('X');
    AIPlayer aiPlayer = new AIPlayer('O');
    private final Player[] players = {humanPlayer, aiPlayer};
    private int currentPlayerIndex = 0;
    private final GameState state = new GameState(BOARD_SIZE);

    public void gameLoop(){
        state.printBoard();

        while(!state.isGameOver()){
            Player currentPlayer = players[currentPlayerIndex];
            int moveIndex = currentPlayer.makeMove(state);
            state.printBoard();
            checkWin(state, currentPlayer.getSymbol(), moveIndex);
            checkGameOver(state);
            checkDraw(state);
            switchPlayer();
        }
    }

    public void switchPlayer(){
        currentPlayerIndex = (currentPlayerIndex + 1) % players.length;
    }

    public void checkDraw(GameState state){
        if(!state.isGameWinner() && state.isGameOver()){
            System.out.println("Draw");
        }
    }

    public void checkGameOver(GameState state){
        if (state.isGameWinner()){
            state.setGameOver(true);
            return;
        }

        char[] board = state.getBoard();
        boolean isGameOver = true;
        for(char grid : board){
            if(grid == '_'){
                isGameOver = false;
                break;
            }
        }
        state.setGameOver(isGameOver);
    }

    public void checkWin(GameState state, char playerSymbol, int moveIndex){
        final Map<Integer, int[][]> winMap = new LinkedHashMap<>();
        winMap.put(0, new int[][]{{0, 1, 2}, {0, 3, 6}, {0, 4, 8}});
        winMap.put(1, new int[][]{{0, 1, 2}, {1, 4, 7}});
        winMap.put(2, new int[][]{{0, 1, 2}, {2, 4, 6}, {2, 5, 8}});
        winMap.put(3, new int[][]{{0, 3, 6}, {3, 4, 5}});
        winMap.put(4, new int[][]{{0, 4, 8}, {1, 4, 7}, {2, 4, 6}, {3, 4, 5}});
        winMap.put(5, new int[][]{{3, 4, 5}, {2, 5, 8}});
        winMap.put(6, new int[][]{{0, 3, 6}, {2, 4, 6}, {6, 7, 8}});
        winMap.put(7, new int[][]{{1, 4, 7}, {6, 7, 8}});
        winMap.put(8, new int[][]{{0, 4, 8}, {2, 5, 8}, {6, 7, 8}});

        int[][] checkWinMap = winMap.get(moveIndex);
        char[] board = state.getBoard();

        for (int[] currentPath : checkWinMap) {
            int pathCount = 0;
            for (int p : currentPath) {
                if (board[p] == playerSymbol) {
                    pathCount += 1;
                }
            }
            if (pathCount == 3) {
                state.setGameWinner(true);
                System.out.println(playerSymbol + " wins");
                break;
            }
        }
    }
}
