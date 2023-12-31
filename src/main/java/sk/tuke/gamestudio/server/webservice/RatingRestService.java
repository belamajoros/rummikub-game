package sk.tuke.gamestudio.server.webservice;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sk.tuke.gamestudio.entity.Rating;
import sk.tuke.gamestudio.service.RatingException;
import sk.tuke.gamestudio.service.RatingService;

import java.util.List;

@RestController
@RequestMapping("/rest/rating")
//http://localhost:8080/api/rating/avgRating/rummikub
//http://localhost:8080/api/rating/playerRating/rummikub
public class RatingRestService
{
    @Autowired
    private RatingService ratingService;

    @GetMapping("{game}")
    public int getAverageRating(@PathVariable String game) throws RatingException {
        return ratingService.getAverageRating(game);
    }

    @GetMapping("{game}/{player}")
    public int getRating(@PathVariable String game, @PathVariable String player) throws RatingException {
        return ratingService.getRating(game,player);
    }

    @PostMapping
    public void setRating(@RequestBody Rating rating) throws RatingException {
        ratingService.setRating(rating);
    }
}
