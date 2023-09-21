package sk.tuke.gamestudio.game.rummikubGame.core;

public class Field {
    public String[][] gameBoard = new String[20][20];

    public GameState state = GameState.PLAYING;

    String blank = "     ";
    private int counter;

    public GameState getState(){
        return state;
    }

    public boolean checkIfEmpty(){
        counter = 0;
        for (int i=1; i<=19; i++){
            for (int j=1; j<=19; j++){
                if(gameBoard[i][j].equals(blank)){
                    counter++;
                }
            }
        }
        if(counter==361){
            return true;
        }
        else {
            return false;
        }
    }

    public boolean checkTile(int r, int c){
        if(gameBoard[r][c]!=blank){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean setTile(int r, int c, String change){
        if(gameBoard[r][c]!=blank){
            return false;
        }
        else{
            gameBoard[r][c] = change;
            return true;
        }
    }

    public boolean resetTile(int r, int c){
        if(gameBoard[r][c]!=blank){
            gameBoard[r][c] = blank;
            return true;
        }
        else{
            return false;
        }
    }

    public void generateBoard(){

        //Initialize columns
        for(int i=0; i<=9; i++){
            gameBoard[0][i] = (" 0"+i+"  ");
        }
        for(int i=10; i<=19; i++){
            gameBoard[0][i] = (" "+i+"  ");
        }

        //Initialize rows
        char alpha = 'A';
        for(int i=1; i<=19; i++){
            gameBoard[i][0] = ("  "+alpha+"  ");
            alpha++;
        }

        //Display the game board
        System.out.println();
        for(int i=0; i<=19; i++){
            for(int j=0; j<=19; j++){
                if(gameBoard[i][j]!=null){
                    System.out.print(gameBoard[i][j]+ " ");
                }
                else{
                    gameBoard[i][j] = blank;
                    System.out.print(gameBoard[i][j]+ " ");
                }
            }
            System.out.println();
        }
    }

    public int checkTileCount(){
        counter = 0;
        for (int i=1; i<=19; i++){
            for (int j=1; j<=19; j++){
                if(!gameBoard[i][j].equals(blank)){
                    counter++;
                }
            }
        }
        return counter;
    }
}
