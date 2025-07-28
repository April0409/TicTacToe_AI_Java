package tictactoe;

import java.util.*;

public class Main {

    public static void printBoard(char[] board){
        //start line
        System.out.println("---------");
        for(int i = 0; i < 9; i++){
            if(i % 3 == 0){
                System.out.print("| ");
            }
            if(board[i] ==  '_' || board[i] == '\u0000'){
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

    public static int checkCoordinates(String[] coordinates, char[] board){
        int index = -1;
        try{
            int x = Integer.parseInt(coordinates[0]);
            int y = Integer.parseInt(coordinates[1]);
            if(x < 1 || x > 3 || y < 1 || y > 3){
                System.out.println("Coordinates should be from 1 to 3!");
                return index;
            }
            index = (x-1) * 3 + (y-1);
        } catch (NumberFormatException e) {
            System.out.println("You should enter numbers!");
        }
        if(index >= 0 && board[index] !='_'){
            System.out.println("This cell is occupied! Choose another one!");
            return -1;
        }
        return index;
    }

    public static char moveOneStep(char[] board, int moveIndex){
        int xCount = 0;
        int oCount = 0;

        for(char b : board){
            if(b == 'X'){
                xCount += 1;
            } else if (b == 'O') {
                oCount += 1;
            }
        }

        if(xCount <= oCount){
            board[moveIndex] = 'X';
            return 'X';

        }else{
            board[moveIndex] = 'O';
            return 'O';
        }
    }

    public static void checkWinMap(char[] board, int moveIndex){
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

        char step = moveOneStep(board, moveIndex);
        printBoard(board);

        int[][] checkWinMap = winMap.get(moveIndex);
        boolean isWins = false;
        for (int[] currentPath : checkWinMap) {
            int pathCount = 0;
            for (int p : currentPath) {
                if (board[p] == step) {
                    pathCount += 1;
                }
            }
            if (pathCount == 3) {
                System.out.println(step + " wins");
                isWins = true;
                break;
            }
        }

        if(!isWins){
            boolean isCompleted = true;
            for(char b : board){
                if(b == '_'){
                    System.out.println("Game not finished");
                    isCompleted = false;
                    break;
                }
            }
            if(isCompleted){
                System.out.println("Draw");
            }
        }

    }

    public static void main(String[] args) {
        // write your code here
        Scanner sc = new Scanner(System.in);

        //input and out the chess board
        System.out.print("Enter the cells: ");
        final char[] board = sc.nextLine().trim().toCharArray();
        printBoard(board);
//        System.out.println(Arrays.toString(board));

        //make a move
        int moveIndex = -1;
        while(moveIndex == -1){
            System.out.print("Enter the coordinates: ");
            String[] coordinates = sc.nextLine().split("\\s+");
            moveIndex = checkCoordinates(coordinates,board);
        }

        //check win-map
        checkWinMap(board, moveIndex);
    }
}
