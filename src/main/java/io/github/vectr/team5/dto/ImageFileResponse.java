package io.github.vectr.team5.dto;

import org.springframework.http.MediaType;

public record ImageFileResponse(
        byte[] data,
        MediaType contentType
) {}