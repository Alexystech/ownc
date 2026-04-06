package com.famvari.domain.service;

import io.smallrye.mutiny.Uni;
import java.io.File;
import java.util.List;

import com.famvari.rest.dto.FileDetails;

public interface StorageService {
    Uni<String> uploadFile(String fileName, String contentType, File file);
    Uni<List<FileDetails>> listFiles();
    Uni<List<FileDetails>> listUserFiles(String email);
}