package sk.tuke.gamestudio.server.webservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.entity.Comment;
import sk.tuke.gamestudio.service.CommentException;
import sk.tuke.gamestudio.service.CommentService;

import java.util.List;

@RestController
@RequestMapping("/rest/comment")
//http://localhost:8080/api/comment/rummikub
public class CommentRestService
{
    @Autowired
    private CommentService commentService;

    @GetMapping("/{game}")
    public List<Comment> getComments(@PathVariable String game) throws CommentException {
        return commentService.getComments(game);
    }

    @PostMapping
    public void addComment(@RequestBody Comment comment) throws CommentException {
        commentService.addComment(comment);
    }
}
