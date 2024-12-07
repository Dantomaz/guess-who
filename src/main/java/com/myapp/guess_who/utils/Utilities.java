package com.myapp.guess_who.utils;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public final class Utilities {

    private final static String INTERNAL_LOCAL_STACK_ENDPOINT = "localstack:4566";
    private final static String EXTERNAL_LOCAL_STACK_ENDPOINT = "s3.localhost.localstack.cloud:4566";
    private boolean isAppRunningInDockerContainer;

    @PostConstruct
    private void init() {
        isAppRunningInDockerContainer = isAppRunningInDockerContainer();
    }

    private boolean isAppRunningInDockerContainer() {
        String OS = "linux";
        String DOCKER_FILE = "/.dockerenv";
        return System.getProperty("os.name").toLowerCase().contains(OS) && (new File(DOCKER_FILE)).exists();
    }

    public String resolveS3Url(String url) {
        return isAppRunningInDockerContainer ? url.replace(INTERNAL_LOCAL_STACK_ENDPOINT, EXTERNAL_LOCAL_STACK_ENDPOINT) : url;
    }
}
