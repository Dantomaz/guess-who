package com.myapp.guess_who.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
public class MultipartParsingService {

    public HashMap<Integer, byte[]> toBytes(List<MultipartFile> images) {
        return IntStream.range(0, images.size())
            .mapToObj(index -> Map.entry(index, getBytes(images.get(index))))
            .sorted(Map.Entry.comparingByKey())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (map1, map2) -> map1, HashMap::new));
    }

    private byte[] getBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
