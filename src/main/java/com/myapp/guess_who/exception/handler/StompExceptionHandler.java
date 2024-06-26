package com.myapp.guess_who.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.ControllerAdvice;

@Slf4j
@ControllerAdvice
public class StompExceptionHandler {

    @MessageExceptionHandler
    @SendToUser("/queue/error")
    public String handleIllegalArgument(IllegalArgumentException exception) {
        log.debug(exception.getMessage());
        return exception.getMessage();
    }
}
