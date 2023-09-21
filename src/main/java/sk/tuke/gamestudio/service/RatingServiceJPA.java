package sk.tuke.gamestudio.service;

import org.springframework.transaction.annotation.Transactional;
import sk.tuke.gamestudio.entity.Rating;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import java.util.List;

import static org.aspectj.runtime.internal.Conversions.intValue;

@Transactional
public class RatingServiceJPA implements RatingService {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void setRating(Rating rating)
    {
        try {
            Rating playerRating = (Rating) entityManager.createNamedQuery("Rating.set")
                                                        .setParameter("game", rating.getGame())
                                                        .setParameter("player", rating.getPlayer())
                                                        .getSingleResult();
            playerRating.setRating(rating.getRating());
        } catch (NoResultException e) {
            entityManager.persist(rating);
        }
    }

    @Override
    public int getAverageRating(String game)
    {
        return intValue(entityManager.createNamedQuery("Rating.getAverageRating")
                .setParameter("game", game).getSingleResult());
    }

    @Override
    public int getRating(String game, String player)
    {
        return intValue(entityManager.createNamedQuery("Rating.getRating")
                .setParameter("game", game).setParameter("player",player).getSingleResult());
    }
}


