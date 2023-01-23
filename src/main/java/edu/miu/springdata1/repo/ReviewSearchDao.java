package edu.miu.springdata1.repo;


import edu.miu.springdata1.dto.input.ReviewCriteriaRequest;
import edu.miu.springdata1.entity.Review;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewSearchDao {

    private final EntityManager em;

    //FIXME This method is not working properly use the second one.
    public List<Review> findAllBySimpleQuery(String comment, int numberOfStars) {
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Review> criteriaQuery = criteriaBuilder.createQuery(Review.class);

        // select * from Review
        Root<Review> root = criteriaQuery.from(Review.class);

        // prepare the WHERE clause
        // WHERE id like ''
        Predicate commentPredicate = criteriaBuilder
                .like(root.get("comment"),"%" + comment + "%");
        Predicate starPredicate = criteriaBuilder
                .equal(root.get("numberOfStars"),numberOfStars);

        Predicate orPredicate = criteriaBuilder.or(commentPredicate,starPredicate);

        // Final query ==> select * from review where comment like %comment% or stars like %stars%
        criteriaQuery.where(orPredicate);
        TypedQuery<Review> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public List<Review> findAllByCriteria(ReviewCriteriaRequest reviewCriteriaRequest){ // You can make a search request object for the input
        CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
        CriteriaQuery<Review> criteriaQuery = criteriaBuilder.createQuery(Review.class);
        List<Predicate> predicates = new ArrayList<>();

        // select * from Review
        Root<Review> root = criteriaQuery.from(Review.class);
        if(reviewCriteriaRequest.getComment()!=null){
            Predicate commentPredicate = criteriaBuilder.like(root.get("comment"),"%" + reviewCriteriaRequest.getComment() + "%");
            predicates.add(commentPredicate);
        }
        if(reviewCriteriaRequest.getNumberOfStars()!=null){
            Predicate starPredicate = criteriaBuilder.equal(root.get("numberOfStars"),reviewCriteriaRequest.getNumberOfStars());
            predicates.add(starPredicate);
        }
        criteriaQuery.where(
                criteriaBuilder.or(predicates.toArray(new Predicate[0]))
        );

        TypedQuery<Review> query = em.createQuery(criteriaQuery);
        return query.getResultList();
    }
}

//  criteriaQuery.where(
//  criteriaBuilder.and(orPredicate, idPredicate)
//  );
