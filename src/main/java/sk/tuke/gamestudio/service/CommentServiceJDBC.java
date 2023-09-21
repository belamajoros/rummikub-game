package sk.tuke.gamestudio.service;

import sk.tuke.gamestudio.entity.Comment;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class CommentServiceJDBC implements CommentService
{

    public static final String URL = "jdbc:postgresql://localhost:5432/gamestudio";
    public static final String USER = "postgres";
    public static final String PASSWORD = "1379";
    private static final String SELECT_COMMENT = "SELECT player, game, comment, commentedon FROM comment WHERE game = ? ORDER BY comment DESC";
    private static final String INSERT_COMMENT = "INSERT INTO comment ( player, game, comment, commentedon) VALUES (?, ?, ?, ?)";

    @Override
    public void addComment(Comment comment) throws CommentException
    {
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try (PreparedStatement ps = connection.prepareStatement(INSERT_COMMENT)) {
                ps.setString(1, comment.getPlayer());
                ps.setString(2, comment.getGame());
                ps.setString(3, comment.getComment());
                ps.setDate(4, new Date(comment.getCommentedOn().getTime()));
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new CommentException("Error saving comment", e);
        }
    }

    @Override
    public List<Comment> getComments(String game) throws CommentException {
        List<Comment> comments = new ArrayList<>();

        try (Connection c = DriverManager.getConnection(URL, USER, PASSWORD)) {
            try (PreparedStatement ps = c.prepareStatement(SELECT_COMMENT)) {
                ps.setString(1, game);
                ResultSet rc = ps.executeQuery();
                while (rc.next()) {
                    Comment s = new Comment(
                            rc.getString(1),
                            rc.getString(2),
                            rc.getString(3),
                            rc.getTimestamp(4)
                    );
                    comments.add(s);
                }
            } catch (SQLException e) {
                throw new CommentException("Error saving comment", e);
            }
        } catch (SQLException e) {
            throw new CommentException("Error connecting to database", e);
        }
        for(int i=0;i<comments.size();i++){
            System.out.println(comments.get(i));
        }
        return comments;
    }

}
