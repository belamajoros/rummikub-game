package sk.tuke.gamestudio.service;

import com.sun.istack.NotNull;
import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.Rating;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.*;


import static org.aspectj.runtime.internal.Conversions.intObject;
import static org.aspectj.runtime.internal.Conversions.intValue;

public class RatingServiceRestClient implements RatingService
{

    private static final String URL = "http://localhost:8080/rest/rating";

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void setRating(Rating rating) {
        restTemplate.postForEntity(URL, rating, Rating.class);
    }

    @Override
    public int getAverageRating(String game) {
        //return Objects.requireNonNull(restTemplate.getForEntity(URL + "/" + game, Integer.class).getBody());
        return restTemplate.getForObject(URL + "/" + game, Integer.class);
    }

    @Override
    public int getRating(String  game, String player) {/*
        Integer rating = restTemplate.getForEntity(URL + "/" + game + "/" + player, Integer.class).getBody();
        return Objects.requireNonNull(rating);*/
        return restTemplate.getForObject(URL + "/" + game + "/" + player, Integer.class);
    }
}
