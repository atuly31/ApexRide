package com.cbs.Feedback.Controller;


import com.cbs.Feedback.Dto.RatingDto;
import com.cbs.Feedback.Entity.Rating;
import com.cbs.Feedback.Service.IRatingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/ratings")
public class RatingController {
    private final IRatingService ratingService;

    @Autowired
    public RatingController(IRatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping
    public ResponseEntity<String> submitRating(@RequestBody RatingDto rating) {
       return new ResponseEntity<>(ratingService.postFeedback(rating),HttpStatus.CREATED);
    }
}



