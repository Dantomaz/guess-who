package com.myapp.guess_who.exception.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProblemTitle {
    BAD_REQUEST("Bad Request"),
    NOT_FOUND("Not Found"),
    INTERNAL_SERVER_ERROR("Internal Server Error"),
    NUMBER_FORMAT("NaN");

    private final String name;
}
