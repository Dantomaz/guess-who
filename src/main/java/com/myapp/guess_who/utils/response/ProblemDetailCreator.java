package com.myapp.guess_who.utils.response;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

import java.net.URI;

@UtilityClass
public class ProblemDetailCreator {

    public ProblemDetail createProblemDetail(HttpStatus status, String detail, ProblemTitle title, String instance) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
        problemDetail.setTitle(title.getName());
        problemDetail.setInstance(URI.create(instance));
        return problemDetail;
    }

    public <T> ResponseEntity<T> getResponseEntity(HttpStatus status, String detail, ProblemTitle title, String instance) {
        return ResponseEntity.of(createProblemDetail(status, detail, title, instance)).build();
    }
}
