package com.myapp.guess_who.session;

import lombok.RequiredArgsConstructor;
import org.springframework.session.FindByIndexNameSessionRepository;
import org.springframework.session.Session;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class RedisSessionService {

    private final FindByIndexNameSessionRepository<? extends Session> sessionRepository;

    public Session getSessionById(String sessionId) {
        return sessionRepository.findById(sessionId);
    }
}
