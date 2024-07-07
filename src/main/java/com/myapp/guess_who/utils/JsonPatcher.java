package com.myapp.guess_who.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class JsonPatcher {

    private final ObjectMapper objectMapper;

    @SneakyThrows
    public <T> T patch(T object, Class<T> objectClass, JsonPatch jsonPatch) {
        JsonNode patched = jsonPatch.apply(objectMapper.convertValue(object, JsonNode.class));
        return objectMapper.treeToValue(patched, objectClass);
    }
}
