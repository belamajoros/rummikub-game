package sk.tuke.gamestudio.game.rummikubGame.core;

import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Player {

    private static final Pattern INPUT_PATTERN1 = Pattern.compile("([0-4])([0-9])([A-S])([0-1])([0-9])");
    private static final Pattern INPUT_PATTERN2 = Pattern.compile("([A-S])([0-1])([0-9])");
    private static final Pattern INPUT_PATTERN3 = Pattern.compile("([A-T])([0-1])([0-9])([A-T])([0-1])([0-9])");

    private final String name;

    private boolean playedFirst = false;

    public boolean playedFirst()
    {
        return playedFirst;
    }

    public void setPlayedFirst()
    {
        playedFirst = true;
    }

    public Player(String name) {
        this.name = name;
    }

    public ArrayList<String> playerHand = new ArrayList<>();

    public String getName(){
        return name;
    }

    private ArrayList<String> listOfPlayedTiles = new ArrayList<>();

    private ArrayList<String> arrOfNext = new ArrayList<>();

    private boolean scanArray(String[][] x, int y, int z){
        return arrOfNext.contains(x[y][z]);
    }

    private void checkBoardStatus(String[][] array, String m, ArrayList<String> tiles, Field field, boolean fullMenu, int x){
        boolean checker = true;
        for (int j=1; j<19; j++){
            int counter = 0;
            for(int i=1; i<19; i++){
                if(field.checkTileCount() == 2){
                    checker = false;
                    break;
                }
                if(array[j][i].equals("     ") || array[j][i + 1].equals("     ")){
                    if(array[j][i].equals("     ") && array[j][i + 1].equals("     ")){
                        continue;
                    }
                    if(arrOfNext.size()<3 && arrOfNext.size()>0){
                        checker = false;
                        break;
                    }
                    else{
                        arrOfNext.clear();
                    }
                }
                else{
                    String [] arrOfStr1 = array[j][i].split("", 5);
                    String [] arrOfStr2 = array[j][i+1].split("", 5);
                    int firstNumber = Integer.parseInt(arrOfStr1[1])*10 + Integer.parseInt(arrOfStr1[2]);
                    int secondNumber = Integer.parseInt(arrOfStr2[1])*10 + Integer.parseInt(arrOfStr2[2]);
                    String firstLetter = arrOfStr1[3];
                    String secondLetter = arrOfStr2[3];

                    if(firstLetter.equals(secondLetter)){
                        if(firstNumber == secondNumber-1){
                            if(!scanArray(array, j, i)){
                                arrOfNext.add(array[j][i]);
                                arrOfNext.add(array[j][i+1]);
                            }
                            else {
                                arrOfNext.add(array[j][i+1]);
                            }
                            System.out.println(arrOfNext.size());
                        }
                    }
                    else {
                        if(counter == 0 && arrOfNext.size()>=3){
                            arrOfNext.clear();
                            counter++;
                        }
                        else {
                            checker = false;
                            break;
                        }
                    }

                    if(firstNumber == secondNumber){
                        if(!firstLetter.equals(secondLetter)){
                            if(!scanArray(array, j, i)){
                                arrOfNext.add(array[j][i]);
                                arrOfNext.add(array[j][i+1]);
                            }
                            else {
                                arrOfNext.add(array[j][i+1]);
                            }
                            System.out.println(arrOfNext.size());
                        }
                    }
                    else {
                        if(counter == 0 && arrOfNext.size()>=3){
                            arrOfNext.clear();
                            counter++;
                        }
                        else {
                            checker = false;
                            break;
                        }
                    }
                }
            }
            if(!checker){
                break;
            }
        }
        if(checker){
            System.out.println("Meld successful.");
            listOfPlayedTiles.clear();

        }
        else {
            field.generateBoard();
            System.out.println("Meld failed. The board has errors.");
            playHand(m, tiles, field, fullMenu, x);
        }
    }

    private void addPlayedTiles(int r, int c){
        String combo = r+" "+c;
        listOfPlayedTiles.add(combo);
    }

    private void removePlayedTiles(int r, int c){
        String combo = r+" "+c;
        listOfPlayedTiles.remove(combo);
    }

    public void pickTile(ArrayList<String> tiles){
        if(tiles.size()>0){
            int rnd = new Random().nextInt(tiles.size());
            playerHand.add(tiles.get(rnd));
            tiles.remove(rnd);
            System.out.println("Remaining tiles: "+tiles.size());
        }
        else{
            System.out.println("No more tiles remaining.");
        }
    }

    public void setPlayerHand(ArrayList<String> tiles){
        for(int j=1; j<15; j++){
            int rnd = new Random().nextInt(tiles.size());
            playerHand.add(tiles.get(rnd));
            tiles.remove(rnd);
        }
    }

    private void showPlayerHand(){
        System.out.println();

        for(int i=1; i<=playerHand.size(); i++){
            if (i<=9){
                System.out.print("  0"+i+"   ");
            }
            else {
                System.out.print("  "+i+"   ");
            }
        }
        System.out.println();
        System.out.println(playerHand);
    }

    public void playHand(String m, ArrayList<String> tiles, Field field, boolean fullMenu, int x){
        System.out.println();
        System.out.println(m+"'s turn.");
        if(fullMenu){
            showPlayerHand();
            System.out.println();
            System.out.print("Enter play (e.g. 05A01, A03, B08C07, P, X): ");
            System.out.println("\n P - Pick Tile\n X - Exit Game");

            String play = new Scanner(System.in).nextLine().trim().toUpperCase();

            Matcher matcher1 = INPUT_PATTERN1.matcher(play);
            Matcher matcher2 = INPUT_PATTERN2.matcher(play);
            Matcher matcher3 = INPUT_PATTERN3.matcher(play);

            if(matcher1.matches()){
                int rowValue = 1 + matcher1.group(3).charAt(0) - 'A';
                int columnValue = Integer.parseInt(matcher1.group(4))*10+Integer.parseInt(matcher1.group(5));
                int tileValue = Integer.parseInt(matcher1.group(1))*10+Integer.parseInt(matcher1.group(2)) - 1;
                addPlayedTiles(rowValue, columnValue);

                if(tileValue>=playerHand.size() || tileValue<0){
                    field.generateBoard();
                    System.out.println();
                    System.out.println("No such tile on hand.");
                    playHand(m, tiles, field, fullMenu, x);
                }
                else{
                    if(!field.setTile(rowValue, columnValue, playerHand.get(tileValue))){
                        field.generateBoard();
                        System.out.println();
                        System.out.println("Location has a tile.");
                        playHand(m, tiles, field, fullMenu, x);
                    }
                    else{
                        playerHand.remove(tileValue);
                        field.generateBoard();
                        fullMenu = false;
                        playHand(m, tiles, field, fullMenu, x);
                    }
                }
            }
            else if (matcher2.matches()){
                int rowValue = 1 + matcher2.group(1).charAt(0) - 'A';
                int columnValue = Integer.parseInt(matcher2.group(2))*10+Integer.parseInt(matcher2.group(3));
                if(listOfPlayedTiles.contains(rowValue+" "+columnValue)){
                    if(!field.checkTile(rowValue, columnValue)){
                        field.generateBoard();
                        System.out.println();
                        System.out.println("Location is empty.");
                        playHand(m, tiles, field, fullMenu, x);
                    }
                    else{
                        playerHand.add(field.gameBoard[rowValue][columnValue]);
                        field.resetTile(rowValue,columnValue);
                        field.generateBoard();
                        removePlayedTiles(rowValue,columnValue);
                        fullMenu = false;
                        playHand(m, tiles, field, fullMenu, x);
                    }
                }
                else {
                    field.generateBoard();
                    System.out.println();
                    System.out.println("You have not played that tile. Cannot move it.");
                    playHand(m, tiles, field, fullMenu, x);
                }

            }
            else if (matcher3.matches()){
                int rowValue = 1 + matcher3.group(1).charAt(0) - 'A';
                int columnValue = Integer.parseInt(matcher3.group(2))*10+Integer.parseInt(matcher3.group(3));
                int rowValue2 = 1 + matcher3.group(4).charAt(0) - 'A';
                int columnValue2 = Integer.parseInt(matcher3.group(5))*10+Integer.parseInt(matcher3.group(6));
                if(!field.checkTile(rowValue, columnValue)){
                    field.generateBoard();
                    System.out.println();
                    System.out.println("Source location is empty.");
                    playHand(m, tiles, field, fullMenu, x);
                }
                else {
                    if(field.checkTile(rowValue2, columnValue2)){
                        field.generateBoard();
                        System.out.println();
                        System.out.println("Destination location is filled.");
                        playHand(m, tiles, field, fullMenu, x);
                    }
                    else{
                        fullMenu = false;
                        field.setTile(rowValue2, columnValue2, field.gameBoard[rowValue][columnValue]);
                        field.resetTile(rowValue, columnValue);
                        field.generateBoard();
                        playHand(m, tiles, field, fullMenu, x);
                    }
                }
            }
            else if ("X".equals(play)){
                System.out.println("The game has exited successfully.");
                System.exit(0);
            }
            else if ("P".equals(play)){
                System.out.println();
                System.out.println("Picking Tile...");
                pickTile(tiles);
            }
            else{
                field.generateBoard();
                System.out.println();
                System.out.println("No such entry. Please retry.");
                playHand(m, tiles, field, fullMenu, x);
            }
        }
        else{
            showPlayerHand();
            System.out.println();
            System.out.print("Enter play (e.g. 05A01, A03, B08C07, M, X, P): ");
            System.out.println("\n P - Pick Tile \n M - Meld\n X - Exit Game");

            String play = new Scanner(System.in).nextLine().trim().toUpperCase();

            Matcher matcher1 = INPUT_PATTERN1.matcher(play);
            Matcher matcher2 = INPUT_PATTERN2.matcher(play);
            Matcher matcher3 = INPUT_PATTERN3.matcher(play);

            if(matcher1.matches()){
                int rowValue = 1 + matcher1.group(3).charAt(0) - 'A';
                int columnValue = Integer.parseInt(matcher1.group(4))*10+Integer.parseInt(matcher1.group(5));
                int tileValue = Integer.parseInt(matcher1.group(1))*10+Integer.parseInt(matcher1.group(2)) - 1;
                addPlayedTiles(rowValue,columnValue);

                if(tileValue>=playerHand.size() || tileValue<0){
                    field.generateBoard();
                    System.out.println();
                    System.out.println("No such tile on hand.");
                    playHand(m, tiles, field, fullMenu, x);
                }
                else{
                    if(!field.setTile(rowValue, columnValue, playerHand.get(tileValue))){
                        field.generateBoard();
                        System.out.println();
                        System.out.println("Location has a tile.");
                        playHand(m, tiles, field, fullMenu, x);
                    }
                    else{
                        playerHand.remove(tileValue);
                        field.generateBoard();
                        playHand(m, tiles, field, fullMenu, x);
                    }
                }
            }
            else if (matcher2.matches()){
                int rowValue = 1 + matcher2.group(1).charAt(0) - 'A';
                int columnValue = Integer.parseInt(matcher2.group(2))*10+Integer.parseInt(matcher2.group(3));
                if(listOfPlayedTiles.contains(rowValue+" "+columnValue)){
                    if(!field.checkTile(rowValue, columnValue)){
                        field.generateBoard();
                        System.out.println();
                        System.out.println("Location is empty.");
                        playHand(m, tiles, field, fullMenu, x);
                    }
                    else{
                        playerHand.add(field.gameBoard[rowValue][columnValue]);
                        field.resetTile(rowValue,columnValue);
                        removePlayedTiles(rowValue,columnValue);
                        field.generateBoard();
                        playHand(m, tiles, field, fullMenu, x);
                    }
                }
                else{
                    field.generateBoard();
                    System.out.println();
                    System.out.println("You have not played that tile. Cannot move it.");
                    playHand(m, tiles, field, fullMenu, x);
                }
            }
            else if (matcher3.matches()){
                int rowValue = 1 + matcher3.group(1).charAt(0) - 'A';
                int columnValue = Integer.parseInt(matcher3.group(2))*10+Integer.parseInt(matcher3.group(3));
                int rowValue2 = 1 + matcher3.group(4).charAt(0) - 'A';
                int columnValue2 = Integer.parseInt(matcher3.group(5))*10+Integer.parseInt(matcher3.group(6));
                if(!field.checkTile(rowValue, columnValue)){
                    field.generateBoard();
                    System.out.println();
                    System.out.println("Source location is empty.");
                    playHand(m, tiles, field, fullMenu, x);
                }
                else {
                    if(field.checkTile(rowValue2, columnValue2)){
                        field.generateBoard();
                        System.out.println();
                        System.out.println("Destination location is filled.");
                        playHand(m, tiles, field, fullMenu, x);
                    }
                    else{
                        field.setTile(rowValue2, columnValue2, field.gameBoard[rowValue][columnValue]);
                        field.resetTile(rowValue, columnValue);
                        field.generateBoard();
                        playHand(m, tiles, field, fullMenu, x);
                    }
                }
            }
            else if ("X".equals(play)){
                System.out.println();
                System.out.println("The game has exited successfully.");
                System.exit(0);
            }
            else if ("P".equals(play)){
                if (listOfPlayedTiles.size() == 0){
                    System.out.println();
                    System.out.println("Picking Tile...");
                    pickTile(tiles);
                }
                else{
                    for (String listOfPlayedTile : listOfPlayedTiles) {
                        String[] playedTileGroup = listOfPlayedTile.split("", 3);
                        int row = Integer.parseInt(playedTileGroup[0]);
                        int column = Integer.parseInt(playedTileGroup[2]);
                        playerHand.add(field.gameBoard[row][column]);
                        field.resetTile(row, column);
                    }
                    listOfPlayedTiles.clear();
                    System.out.println();
                    System.out.println("Picking Tile...");
                    pickTile(tiles);
                }
            }
            else if ("M".equals(play)){
                if (field.checkIfEmpty()){
                    listOfPlayedTiles.clear();
                    field.generateBoard();
                    System.out.println();
                    System.out.println("Board is empty. Cannot meld.");
                    fullMenu = true;
                    playHand(m, tiles, field, fullMenu, x);
                }
                else {
                    if (listOfPlayedTiles.size()==0){
                        field.generateBoard();
                        System.out.println();
                        System.out.println("No play made. Cannot meld.");
                        fullMenu = true;
                        playHand(m, tiles, field, fullMenu, x);
                    }
                    else {
                        if (x == 1){
                            int total = 0;
                            for (String listOfPlayedTile : listOfPlayedTiles) {
                                String[] firstArray = listOfPlayedTile.split("", 3);
                                int row = Integer.parseInt(firstArray[0]);
                                int column = Integer.parseInt(firstArray[2]);
                                String[] secondArray = field.gameBoard[row][column].split("", 5);
                                int val = Integer.parseInt(secondArray[1]) * 10 + Integer.parseInt(secondArray[2]);
                                total = total + val;
                            }
                            if(total>=30){
                                checkBoardStatus(field.gameBoard, m, tiles, field, fullMenu, x);
                            }
                            else {
                                field.generateBoard();
                                System.out.println();
                                System.out.println("Values must be above 30. Cannot meld.");
                                playHand(m, tiles, field, fullMenu, x);
                            }
                        }
                        else {
                            checkBoardStatus(field.gameBoard, m, tiles, field, fullMenu, x);
                        }
                    }
                }
            }
            else{
                field.generateBoard();
                System.out.println();
                System.out.println("No such option. Please retry.");
                playHand(m, tiles, field, fullMenu, x);
            }
        }
    }
}