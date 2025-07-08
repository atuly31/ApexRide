package com.cbs.Feedback.Repository;

import com.cbs.Feedback.Dto.RatingDto;
import com.cbs.Feedback.Entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
}
