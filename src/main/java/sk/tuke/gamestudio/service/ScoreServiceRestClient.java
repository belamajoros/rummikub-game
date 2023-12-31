package sk.tuke.gamestudio.service;

import org.springframework.web.client.RestTemplate;
import sk.tuke.gamestudio.entity.Score;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ScoreServiceRestClient implements ScoreService {
    private static final String URL = "http://localhost:8080/rest/score";

    private RestTemplate restTemplate = new RestTemplate();

    @Override
    public void addScore(Score score) {
        restTemplate.postForEntity(URL, score, Score.class);
    }

    @Override
    public List<Score> getBestScores(String game) {
        return Arrays.asList(restTemplate.getForEntity(URL + "/" + game, Score[].class).getBody());
    }
}
