package sk.tuke.gamestudio.game.rummikubGame.ConsoleUI;


import org.springframework.beans.factory.annotation.Autowired;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.entity.Score;
import sk.tuke.gamestudio.game.rummikubGame.core.Field;
import sk.tuke.gamestudio.game.rummikubGame.core.GameState;
import sk.tuke.gamestudio.game.rummikubGame.core.Player;
import sk.tuke.gamestudio.game.rummikubGame.core.Tile;
import sk.tuke.gamestudio.service.*;

import javax.persistence.NoResultException;
import java.util.regex.*;

import java.util.*;
import java.util.Date;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


public class ConsoleUI {

    @Autowired
    private ScoreService scoreService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private RatingService ratingService;

    private final Pattern rating = Pattern.compile("([1-5])");
    private int counter = 0;
    private boolean first = false;
    private Scanner in;
    private int players = 0;
    private Field field;
    private ArrayList<String> newGameTiles;
    private Player p1;
    private Player p2;
    private Player p3;
    private Player p4;

    private void runGame() {
        ConsoleUI ui = new ConsoleUI();
        ui.startGame();
    }

    public void startGame()
    {
        /* VIDEO TEST */
        userComment("PLAYER1_TEST");
        userRating("PLAYER1_TEST");
        printUserRating("PLAYER1_TEST");
        userRating("PLAYER1_TEST");
        printUserRating("PLAYER1_TEST");
        userComment("PLAYER1_TEST");
        userAddScore("PLAYER1_TEST");
        if(!first) {
            printScores();
            try {
                printComments();
            } catch (CommentException e) {
                e.printStackTrace();
            }
            printAverageRating();
        }
        /*VIDEO TEST*/

        in = new Scanner(System.in);
        System.out.println("Welcome to Rummikub.");
        System.out.print("Enter the number of players: ");
        String val = in.nextLine();

        if(Objects.equals(val, "2")){
            generate(val);
            while (field.getState() == GameState.PLAYING){
                counter++;
                Player[] currentPlayer = new Player[]{p1,p2};
                whilePlaying(currentPlayer,players,field,newGameTiles);
            }
        }
        else if(Objects.equals(val, "3")){
            generate(val);
            while (field.getState() == GameState.PLAYING){
                Player[] currentPlayer = new Player[]{p1,p2,p3};
                counter++;
                whilePlaying(currentPlayer,players,field,newGameTiles);
            }
        }
        else if(Objects.equals(val, "4")){
            generate(val);
            while (field.getState() == GameState.PLAYING){
                counter++;
                Player[] currentPlayer = new Player[]{p1,p2,p3,p4};
                whilePlaying(currentPlayer,players,field,newGameTiles);
            }
        }
        else{
            System.out.println("Invalid entry. Players must be between 2 and 4!");
            startGame();
        }
    }

    private void generate(String val)
    {
        players = Integer.parseInt(val);
        generateGameComponents();
        p1 = new Player("Player 1");
        p1.setPlayerHand(newGameTiles);
        p2 = new Player("Player 2");
        p2.setPlayerHand(newGameTiles);

        if(players >= 3)
        {
            p3 = new Player("Player 3");
            p3.setPlayerHand(newGameTiles);
        }
        if(players == 4)
        {
            p4 = new Player("Player 4");
            p4.setPlayerHand(newGameTiles);
        }
    }

    private void printWinner(String name)
    {
        System.out.println();
        System.out.println(name+" is the winner.");
        System.out.println();
    }

    private void userComment(String name)
    {
        System.out.println("Please leave a comment: ");
        in = new Scanner(System.in);
        String c = in.nextLine();
        try {
            commentService.addComment(new Comment(
                    name,
                    "rummikub",
                    c,
                    new Date()
            ));
        } catch (CommentException e) {
            System.err.println(e.getMessage());
        }
    }

    private void userAddScore(String name)
    {
        scoreService.addScore(new Score("rummikub",name,20,new Date()));
    }

    private void userRating(String name)
    {
        System.out.println("Rate the game from 1 to 5");
        in = new Scanner(System.in);
        String r = in.nextLine();
        Matcher ratingMatcher = rating.matcher(r);
        if (ratingMatcher.matches()) {
            try {
                ratingService.setRating(new Rating(
                        name,
                        "rummikub",
                        Integer.parseInt(ratingMatcher.group(1)),
                        new Date()
                ));
            } catch (RatingException e) {
                System.err.println(e.getMessage());
            }
        } else {
            System.out.println("Please enter correct value of rating");
            userRating(name);
        }
    }

    private void printScores()
    {
        first = true;
        if(scoreService.getBestScores("rummikub").isEmpty())
        {
            System.err.println("No available scores yet");
            return;
        }
        List<Score> scores = scoreService.getBestScores("rummikub");

        Collections.sort(scores);

        System.out.println("Top scores:");
        for (Score s : scores) {
            System.out.println(s);
        }
    }

    private void printAverageRating(){
        try {
            if(ratingService.getAverageRating("rummikub") == 0)
                System.err.println("No rating of the game yet");
            else
                System.out.println("Average rating of the game is: " + ratingService.getAverageRating("rummikub"));
        } catch (RatingException e) {
            e.printStackTrace();
        }
    }

    private void printUserRating(String name)
    {
        try{
           // ratingService.getRating("rummikub",name);
            System.out.println(name + "'s rating of the game is: " + ratingService.getRating("rummikub",name));
        } catch (RatingException e) {
            System.err.println("There is no rating of the game Rummikub by a player named " + name);
        }
    }

    private void printComments() throws CommentException {
        if(commentService.getComments("rummikub").isEmpty())
        {
            System.err.println("No comments of the game yet");
            return;
        }
        List<Comment> comments = commentService.getComments("rummikub");
        System.out.println("User comments of the game:");
        for (Comment s : comments) {
            System.out.println(s);
        }
    }

    private void endGame(String name)
    {
        boolean playAgain = false;
        do{
            userAddScore(name);
            userComment(name);
            userRating(name);
            System.out.println("Do you wish to start a new game? (Y/N)");
            Scanner scanner = new Scanner(System.in);
            String newVal = scanner.nextLine();
            if(newVal.equals("Y")){
                playAgain = true;
                runGame();
            }
            else if (newVal.equals("N")){
                playAgain = true;
                System.exit(0);
            }
            else {
                System.out.println();
                System.out.println("Not a valid entry. Try Again.");
                System.out.println();
            }
        }while (!playAgain);
    }

    private void whilePlaying(Player[] currentPlayer,int players, Field field, ArrayList<String> newGameTiles)
    {
        for (int i=0; i<players; i++){
            currentPlayer[i].playHand(currentPlayer[i].getName(), newGameTiles, field, true, counter);
            field.generateBoard();
            if(currentPlayer[i].playerHand.size()==0){
                field.state = GameState.SOLVED;
                printWinner(currentPlayer[i].getName());
                endGame(currentPlayer[i].getName());
            }
        }
    }

    private void generateGameComponents()
    {
        field = new Field();
        field.generateBoard();
        Tile tile = new Tile();
        newGameTiles = new ArrayList<>();
        tile.setGameTiles(newGameTiles);
    }
}


