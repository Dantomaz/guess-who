package com.myapp.guess_who.exception.handler;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jackson.jsonpointer.JsonPointerException;
import com.github.fge.jsonpatch.JsonPatchException;
import com.myapp.guess_who.exception.response.ProblemDetailCreator;
import com.myapp.guess_who.exception.response.ProblemTitle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

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

    @ExceptionHandler({HttpServerErrorException.InternalServerError.class})
    protected ResponseEntity<Void> handleInternalServerError(HttpServerErrorException.InternalServerError exception, ServletWebRequest request) {
        log.debug(exception.getMessage());
        return ProblemDetailCreator.getResponseEntity(
            HttpStatus.INTERNAL_SERVER_ERROR,
            exception.getMessage(),
            ProblemTitle.INTERNAL_SERVER_ERROR,
            request.getRequest().getRequestURI()
        );
    }

    @ExceptionHandler({NumberFormatException.class})
    protected ResponseEntity<Void> handleJsonMapping(NumberFormatException exception, ServletWebRequest request) {
        log.debug(exception.getMessage());
        return ProblemDetailCreator.getResponseEntity(
            HttpStatus.UNPROCESSABLE_ENTITY,
            exception.getMessage(),
            ProblemTitle.NUMBER_FORMAT,
            request.getRequest().getRequestURI()
        );
    }

    @ExceptionHandler(
        {
            JsonPatchException.class,
            JsonProcessingException.class,
            JsonParseException.class,
            JsonGenerationException.class,
            JsonPointerException.class
        }
    )
    protected ResponseEntity<Void> handleJsonMapping(Exception jsonException, ServletWebRequest request) {
        log.debug(jsonException.getMessage());
        return ProblemDetailCreator.getResponseEntity(
            HttpStatus.UNPROCESSABLE_ENTITY,
            jsonException.getMessage(),
            ProblemTitle.NUMBER_FORMAT,
            request.getRequest().getRequestURI()
        );
    }
}
