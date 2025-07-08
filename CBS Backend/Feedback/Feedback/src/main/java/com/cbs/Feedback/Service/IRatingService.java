package com.cbs.Feedback.Service;

import com.cbs.Feedback.Dto.RatingDto;
import com.cbs.Feedback.Entity.Rating;

public interface IRatingService {

    String postFeedback(RatingDto rating);
}
