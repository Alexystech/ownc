package com.famvari.rest.dto;

public record FileDetails(
    String fileName,
    String contentType,
    String storagePath
) {}