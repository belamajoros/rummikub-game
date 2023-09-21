package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Rating;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class RatingServiceJDBC implements RatingService
{
    public static final String URL = "jdbc:postgresql://localhost:5432/gamestudio";
    public static final String USER = "postgres";
    public static final String PASSWORD = "1379";
    private static final String INSERT_RATING = "INSERT INTO rating (player, game, rating, ratedon) VALUES (?, ?, ?, ?)";
    private static final String SELECT_AVERAGE_RATING = "SELECT  game, player, rating, ratedon FROM rating WHERE game = ?";
    private static final String SELECT_RATING = "SELECT  player, game, rating, ratedon FROM rating WHERE (player = ? AND game = ?)";

    @Override
    public void setRating(Rating rating) throws RatingException
    {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try (PreparedStatement ps = connection.prepareStatement(INSERT_RATING)) {
                ps.setString(2, rating.getGame());
                ps.setString(1, rating.getPlayer());
                ps.setInt(3, rating.getRating());
                ps.setDate(4, new Date(rating.getRatedOn().getTime()));
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new ScoreException("Error saving rating", e);
        }
    }

    @Override
    public int getAverageRating(String game) throws RatingException {
        int average = 0;
        int ratingsCount = 0;

        try (Connection c = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try (PreparedStatement ps = c.prepareStatement(SELECT_AVERAGE_RATING)) {

                ps.setString(1, game);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    average = average + rs.getInt(3);
                    ratingsCount++;
                }
            } catch (SQLException e) {
                throw new RatingException("Error saving rating", e);
            }
        } catch (SQLException e) {
            throw new RatingException("Error connecting to database", e);
        }

        if(ratingsCount  == 0)
        {
            return 0;
        }
        System.out.println("Average rating of the game: " + average / ratingsCount);
        return average / ratingsCount;
    }

    @Override
    public int getRating(String game, String player) throws RatingException {
        int rating = 0;

        try (Connection c = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try (PreparedStatement ps = c.prepareStatement(SELECT_RATING)) {
                ps.setString(1, game);
                ps.setString(2, player);
                ResultSet rc = ps.executeQuery();
                while(rc.next()){
                    rating = rc.getInt("rating");
                }
                System.out.println(player +" of the game " + game +" rating is: " + rating);
            } catch (SQLException e) {
                throw new RatingException("Error getting rating", e);
            }
        } catch (SQLException e) {
            throw new RatingException("Error connecting to database", e);
        }

        return rating;
    }
}
