package com.myapp.guess_who.utils.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProblemTitle {
    BAD_REQUEST("Bad Request"),
    NOT_FOUND("Not Found");

    private final String name;
}
