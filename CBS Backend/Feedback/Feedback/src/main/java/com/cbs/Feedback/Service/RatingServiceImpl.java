package com.cbs.Feedback.Service;

import com.cbs.Feedback.Dto.RatingDto;
import com.cbs.Feedback.Entity.Rating;
import com.cbs.Feedback.Repository.RatingRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class RatingServiceImpl implements IRatingService{

    @Autowired
    RatingRepository ratingRepository;
    @Autowired
    ModelMapper modelMapper;
    @Override
    public String postFeedback(RatingDto rating) {
        Rating rate = modelMapper.map(rating,Rating.class);
        rate.setTimestamp(LocalDateTime.now());
        ratingRepository.save(rate);
        return "Feedback Posted";
    }
}
