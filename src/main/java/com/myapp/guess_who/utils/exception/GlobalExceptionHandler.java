package com.myapp.guess_who.utils.exception;

import com.myapp.guess_who.utils.response.ProblemDetailCreator;
import com.myapp.guess_who.utils.response.ProblemTitle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        @NonNull MethodArgumentNotValidException exception, @NonNull HttpHeaders headers, @NonNull HttpStatusCode status, @NonNull WebRequest request
    ) {
        log.debug("Invalid http method parameters");
        ProblemDetail problemDetail = exception.getBody();
        Map<String, List<String>> invalidParams = exception.getBindingResult()
            .getAllErrors()
            .stream()
            .collect(Collectors.groupingBy(
                error -> ((FieldError) error).getField(),
                Collectors.mapping(error -> Objects.requireNonNull(error.getDefaultMessage()), Collectors.toList())
            ));
        problemDetail.setProperty("invalid-params", invalidParams);
        return ResponseEntity.of(problemDetail).build();
    }

    @ExceptionHandler({IllegalArgumentException.class})
    protected ResponseEntity<Void> handleIllegalArgument(IllegalArgumentException exception, ServletWebRequest request) {
        log.debug(exception.getMessage());
        return ProblemDetailCreator.getResponseEntity(
            HttpStatus.BAD_REQUEST,
            exception.getMessage(),
            ProblemTitle.BAD_REQUEST,
            request.getRequest().getRequestURI()
        );
    }
}
