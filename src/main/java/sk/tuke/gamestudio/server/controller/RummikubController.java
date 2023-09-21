package sk.tuke.gamestudio.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.rummikubGame.core.Field;
import sk.tuke.gamestudio.game.rummikubGame.core.GameState;
import sk.tuke.gamestudio.game.rummikubGame.core.Player;
import sk.tuke.gamestudio.game.rummikubGame.core.Tile;
import sk.tuke.gamestudio.service.*;

import javax.persistence.NoResultException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Controller
@Scope(WebApplicationContext.SCOPE_SESSION)
@RequestMapping("/rummikub")
public class RummikubController {

    @Autowired
    private UserController userController;
    private String gameName = "rummikub";

    private Field field;
    private ArrayList<String> newGameTiles;
    private Player p1;
    private Player p2;
    private Player currentPlayer;
    private static final Pattern INPUT_PATTERN1 = Pattern.compile("([0-4])([0-9])([A-S])([0-1])([0-9])");
    private static final Pattern INPUT_PATTERN2 = Pattern.compile("([A-S])([0-1])([0-9])");
    private static final Pattern INPUT_PATTERN3 = Pattern.compile("([A-T])([0-1])([0-9])([A-T])([0-1])([0-9])");
    private ArrayList<String> listOfPlayedTiles = new ArrayList<>();
    private ArrayList<String> arrOfNext = new ArrayList<>();
    private String errorMessage;

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;

    private void newGame() {
        generate();
    }

    @RequestMapping
    public String rummikub(Model model) {
        if(field == null)
        {
            newGame();
        }
        updateModel(model);
        return "rummikub";
    }

    @RequestMapping("/new")
    public String newGame(Model model) {
        errorMessage = "dontDisplay";
        newGame();
        updateModel(model);
        return "redirect:/rummikub";
    }

    public String getPlayerHand() {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class='field'>\n");
        sb.append("<tr>\n");
        for (int x = 0; x < getCurrentPlayer().playerHand.size(); x++) {
            String tile = getCurrentPlayer().playerHand.get(x);
            sb.append("<td>\n");
            if (field.equals(this.field))
               sb.append("<img src='/images/" + getTileName(tile) + ".png' title='Position: " + picturePosition(x) + "' >");
               if (field.equals(this.field))
                   sb.append("</a>\n");
               sb.append("</td>\n");
        }
        sb.append("</tr>\n");
        sb.append("</table>\n");

        return sb.toString();
    }

    private void generate()
    {
        generateGameComponents();
        generatePlayerNames();
        p1.setPlayerHand(newGameTiles);
        p2.setPlayerHand(newGameTiles);
        currentPlayer = p1;
    }

    private void generateGameComponents()
    {
        field = new Field();
        field.generateBoard();
        Tile tile = new Tile();
        newGameTiles = new ArrayList<>();
        tile.setGameTiles(newGameTiles);
    }

    private void updateHtml()
    {
        getGameBoard();
        getPlayerHand();
    }

    public String getGameBoard()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<table class='field'>\n");
        sb.append("<tr>\n");
        for(int i=0; i<=9; i++){
            sb.append("<th>\n");
            if(i != 0)
                sb.append("<b>" + String.format("0%s",i) + "</b>");
            else
                sb.append(" ");
            sb.append("</th>\n");
        }
        for(int i=10; i<=19; i++){
            sb.append("<th>\n");
            sb.append("<b>" + String.format("%s",i) + "</b>");
            sb.append("</th>\n");
        }
        sb.append("</tr>");
        char alpha = 'A';
        String blank = "     ";
        for(int i = 1; i <= 19; i++)
        {
            sb.append("<tr>\n");
            sb.append("<td>\n");
            sb.append("<b>" + String.format("%s",alpha) + "</b>");
            sb.append("</td>\n");
            for(int x = 1; x <= 19; x++)
            {
                if(field.gameBoard[i][x].equals(blank))
                {
                    sb.append("<td>\n");
                    sb.append("<img src='/images/neutral.png' title='Position: " + calculateBoardPosition(x,i) + "'>");
                    sb.append("</td>\n");
                }
                else
                {
                    String tile = field.gameBoard[i][x];
                    sb.append("<td>\n");
                    sb.append("<img src='/images/" + getTileName(tile) + ".png' title='Position: " + calculateBoardPosition(x,i) + "' >");
                    sb.append("</td>\n");
                }
            }
            sb.append("</tr>\n");
            alpha++;
        }
        sb.append("</table>\n");

        return sb.toString();
    }

    @RequestMapping("/comment")
    public String addComment(String comment, Model model)
    {
        errorMessage = "dontDisplay";
        try {
            commentService.addComment(new Comment(getCurrentPlayerName(), gameName, comment, new Date()));
        } catch (CommentException e) {
            e.printStackTrace();
        }
        updateModel(model);
        return "redirect:/rummikub";
    }

    @RequestMapping("/rating")
    public String addRating(String rating, Model model){
        errorMessage = "dontDisplay";
        try {
            ratingService.setRating(new Rating(getCurrentPlayerName(), gameName, Integer.parseInt(rating), new Date()));
        } catch (RatingException e) {
            e.printStackTrace();
        }
        updateModel(model);
        return "redirect:/rummikub";
    }

    private void updateModel(Model model) {
        model.addAttribute("loggedUser", getCurrentPlayerName());
        model.addAttribute("scores", scoreService.getBestScores(gameName));
        try {
            model.addAttribute("comments",commentService.getComments(gameName));
        } catch (CommentException e) {
            e.printStackTrace();
        }
        try {
            if(ratingService.getAverageRating(gameName) != 0)
                model.addAttribute("avgRating", ratingService.getAverageRating(gameName));
            else
                model.addAttribute("avgRating", "Noone rated the game yet");
        } catch (RatingException e) {
            e.printStackTrace();
        }
        try {
            model.addAttribute("userRating",ratingService.getRating(gameName,getCurrentPlayerName()));
        } catch (NoResultException | RatingException e) {
            model.addAttribute("userRating","No rating yet");
        }
    }

    private void reverseList(ArrayList<String> player)
    {
        for(int i = 0; i < player.size(); i++) {
            player.set(i, new StringBuilder(player.get(i)).reverse().toString());
        }
    }

    @RequestMapping("/sortByColor")
    public String sortByColor()
    {
        errorMessage = "dontDisplay";
        reverseList(getCurrentPlayer().playerHand);
        Collections.sort(getCurrentPlayer().playerHand);
        reverseList(getCurrentPlayer().playerHand);

        return "redirect:/rummikub";
    }

    @RequestMapping("/sortByNumber")
    public String sortByNumber()
    {
        errorMessage = "dontDisplay";
        Collections.sort(getCurrentPlayer().playerHand);

        return "redirect:/rummikub";
    }

    @RequestMapping("/pickTile")
    public String pickTile()
    {
        errorMessage = "dontDisplay";
        if(newGameTiles.size() != 0) {
            getCurrentPlayer().pickTile(newGameTiles);
            updateCurrentPlayer();
        }
        else
        {
            errorMessage = "No more tiles left";
        }
        return "redirect:/rummikub";
    }

    private Player getCurrentPlayer()
    {
        return currentPlayer;
    }

    private void updateCurrentPlayer()
    {
        if(currentPlayer == p1)
            currentPlayer = p2;
        else
            currentPlayer = p1;
    }

    public boolean isGameWon()
    {
        return field.getState() == GameState.SOLVED;
    }

    public String getCurrentPlayerName()
    {
        return currentPlayer.getName();
    }

    private void checkWin()
    {
        if(getCurrentPlayer().playerHand.size() == 0)
        {
            if(userController.getUsersSize() == 2)
            {
                scoreService.addScore(new Score(gameName,getCurrentPlayerName(),100,new Date()));
            }
            updateCurrentPlayer();
            field.state = GameState.SOLVED;
            updateHtml();
        }
    }

    public String getErrorMessage()
    {
        return errorMessage;
    }

    @RequestMapping("/handleInput")
    private String handleInput(String input)
    {
        errorMessage = "dontDisplay";
        String play = input.toUpperCase();
        Matcher matcher1 = INPUT_PATTERN1.matcher(play);
        Matcher matcher2 = INPUT_PATTERN2.matcher(play);
        Matcher matcher3 = INPUT_PATTERN3.matcher(play);

        int tileValue;
        int columnValue;
        int rowValue;
        if(matcher1.matches()){
            rowValue = 1 + matcher1.group(3).charAt(0) - 'A';
            columnValue = Integer.parseInt(matcher1.group(4))*10+Integer.parseInt(matcher1.group(5));
            tileValue = Integer.parseInt(matcher1.group(1))*10+Integer.parseInt(matcher1.group(2)) - 1;
            addPlayedTiles(rowValue, columnValue);

            if(tileValue >= getCurrentPlayer().playerHand.size() || tileValue <0){
                errorMessage = "No such tile on hand.";
                updateHtml();
            }
            else{
                if(!field.setTile(rowValue, columnValue, getCurrentPlayer().playerHand.get(tileValue))){
                    updateHtml();
                    errorMessage = "Location has a tile.";
                }
                else{
                    getCurrentPlayer().playerHand.remove(tileValue);
                    field.generateBoard();
                    updateHtml();
                }
            }
        }
        else if (matcher2.matches()){
            rowValue = 1 + matcher2.group(1).charAt(0) - 'A';
            columnValue = Integer.parseInt(matcher2.group(2))*10+Integer.parseInt(matcher2.group(3));
            if(listOfPlayedTiles.contains(rowValue +" "+ columnValue)){
                if(!field.checkTile(rowValue, columnValue)){
                    errorMessage="Location is empty.";
                }
                else{
                    getCurrentPlayer().playerHand.add(field.gameBoard[rowValue][columnValue]);
                    field.resetTile(rowValue, columnValue);
                    removePlayedTiles(rowValue, columnValue);
                }
            }
            else {
                errorMessage="Not your tile. Cannot move it.";
            }

        }
        else if (matcher3.matches()){
            rowValue = 1 + matcher3.group(1).charAt(0) - 'A';
            columnValue = Integer.parseInt(matcher3.group(2))*10+Integer.parseInt(matcher3.group(3));
            int rowValue2 = 1 + matcher3.group(4).charAt(0) - 'A';
            int columnValue2 = Integer.parseInt(matcher3.group(5)) * 10 + Integer.parseInt(matcher3.group(6));
            if(!field.checkTile(rowValue, columnValue)){
                errorMessage="Source location is empty.";
            }
            else {
                if(field.checkTile(rowValue2, columnValue2)){
                    errorMessage="Destination location is filled.";
                }
                else{
                    field.setTile(rowValue2, columnValue2, field.gameBoard[rowValue][columnValue]);
                    field.resetTile(rowValue, columnValue);
                }
            }
        }
        else if ("M".equals(play)){
            if (field.checkIfEmpty()){
                listOfPlayedTiles.clear();
                errorMessage="Board is empty. Cannot meld.";
            }
            else {
                if (listOfPlayedTiles.size()==0){
                    errorMessage="No play made. Cannot meld.";
                }
                else {
                    if (!getCurrentPlayer().playedFirst()){
                        int total = 0;
                        for (String listOfPlayedTile : listOfPlayedTiles) {
                            String[] firstArray = listOfPlayedTile.split("", 3);
                            rowValue = Integer.parseInt(firstArray[0]);
                            columnValue = Integer.parseInt(firstArray[2]);
                            String[] secondArray = field.gameBoard[rowValue][columnValue].split("", 5);
                            tileValue = Integer.parseInt(secondArray[1]) * 10 + Integer.parseInt(secondArray[2]);
                            total += tileValue;
                        }
                        if(total>=30){
                            checkBoardStatus(field.gameBoard);
                        }
                        else {
                            errorMessage="Values must be above 30.";
                        }
                    }
                    else {
                        checkBoardStatus(field.gameBoard);
                    }
                }
            }
        }
        else{
            errorMessage="No such option. Retry.";
        }
        return "redirect:/rummikub";
    }

    private boolean scanArray(String[][] x, int y, int z){
        return arrOfNext.contains(x[y][z]);
    }

    private void checkBoardStatus(String[][] array){
        boolean checker = true;
        for (int j=1; j<19; j++){
            int counter = 0;
            for(int i=1; i<19; i++){
                if(field.checkTileCount() == 2){
                    checker = false;
                    break;
                }
                if(!(array[j][i].equals("     ")) && array[j-1][i].equals("     "))
                {
                    if(array[j+1][i].equals("     ") ||(!array[j+1][i].equals("     ") && array[j+2][i].equals("     ")))
                    {
                        checker = false;
                        break;
                    }
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
            checkWin();
            updateCurrentPlayer();
            listOfPlayedTiles.clear();
            getCurrentPlayer().setPlayedFirst();
        }
        else {
            errorMessage="Meld failed. Board has errors.";
        }
    }

    public int getRemainingTiles()
    {
        return newGameTiles.size();
    }

    private String picturePosition(int x)
    {
        String pos;
        if(x < 9)
        {
            pos = "0" + (x + 1);
        }
        else
        {
            pos = String.valueOf(x+1);
        }
        return pos;
    }

    private String calculateBoardPosition(int x,int i)
    {
        String pos;
        char xPos = (char) (i + '0');
        xPos += 16;
        pos = String.valueOf(xPos) + picturePosition(x-1);
        return pos;
    }

    private void generatePlayerNames()
    {
        if(userController.getUsersSize() == 2) {
            p1 = new Player(userController.getLoggedUser(0));
            p2 = new Player(userController.getLoggedUser(1));
        }
        else
        {
            p1 = new Player("Player 1");
            p2 = new Player("Player 2");
        }
    }

    @RequestMapping("/menu")
    public String backToMenu()
    {
        errorMessage = "dontDisplay";
        return "redirect:/";
    }

    private void addPlayedTiles(int r, int c){
        String combo = r+" "+c;
        listOfPlayedTiles.add(combo);
    }

    private void removePlayedTiles(int r, int c){
        String combo = r+" "+c;
        listOfPlayedTiles.remove(combo);
    }

    private String getTileName(String name)
    {
        switch (name)
        {
            case "[01R]":
                return "Red1";
            case "[02R]":
                return "Red2";
            case "[03R]":
                return "Red3";
            case "[04R]":
                return "Red4";
            case "[05R]":
                return "Red5";
            case "[06R]":
                return "Red6";
            case "[07R]":
                return "Red7";
            case "[08R]":
                return "Red8";
            case "[09R]":
                return "Red9";
            case "[10R]":
                return "Red10";
            case "[11R]":
                return "Red11";
            case "[12R]":
                return "Red12";
            case "[13R]":
                return "Red13";
            case "[01B]":
                return "Blu1";
            case "[02B]":
                return "Blu2";
            case "[03B]":
                return "Blu3";
            case "[04B]":
                return "Blu4";
            case "[05B]":
                return "Blu5";
            case "[06B]":
                return "Blu6";
            case "[07B]":
                return "Blu7";
            case "[08B]":
                return "Blu8";
            case "[09B]":
                return "Blu9";
            case "[10B]":
                return "Blu10";
            case "[11B]":
                return "Blu11";
            case "[12B]":
                return "Blu12";
            case "[13B]":
                return "Blu13";
            case "[01Y]":
                return "Yel1";
            case "[02Y]":
                return "Yel2";
            case "[03Y]":
                return "Yel3";
            case "[04Y]":
                return "Yel4";
            case "[05Y]":
                return "Yel5";
            case "[06Y]":
                return "Yel6";
            case "[07Y]":
                return "Yel7";
            case "[08Y]":
                return "Yel8";
            case "[09Y]":
                return "Yel9";
            case "[10Y]":
                return "Yel10";
            case "[11Y]":
                return "Yel11";
            case "[12Y]":
                return "Yel12";
            case "[13Y]":
                return "Yel13";
            case "[01G]":
                return "Blk1";
            case "[02G]":
                return "Blk2";
            case "[03G]":
                return "Blk3";
            case "[04G]":
                return "Blk4";
            case "[05G]":
                return "Blk5";
            case "[06G]":
                return "Blk6";
            case "[07G]":
                return "Blk7";
            case "[08G]":
                return "Blk8";
            case "[09G]":
                return "Blk9";
            case "[10G]":
                return "Blk10";
            case "[11G]":
                return "Blk11";
            case "[12G]":
                return "Blk12";
            case "[13G]":
                return "Blk13";
        }
        return "No such tile";
    }
}
