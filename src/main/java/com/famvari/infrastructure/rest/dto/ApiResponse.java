package com.famvari.infrastructure.rest.dto;

import java.util.List;

public record ApiResponse<T>(
    boolean result,
    String message,
    List<T> data
) {}