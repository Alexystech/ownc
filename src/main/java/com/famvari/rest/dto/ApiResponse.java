package com.famvari.rest.dto;

import java.util.List;

public record ApiResponse<T>(
    boolean result,
    String message,
    List<T> data
) {}