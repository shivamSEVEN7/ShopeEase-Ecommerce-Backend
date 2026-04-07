package com.ecommerce.project.service;

import com.ecommerce.project.dto.FileInfo;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface FileService {
    String uploadFile(String path, MultipartFile file) throws IOException;
    FileInfo uploadImage(MultipartFile file) throws IOException;
    FileInfo uploadIcon(MultipartFile file) throws IOException;
    FileInfo uploadAdBanner(MultipartFile file) throws IOException;
    FileInfo uploadAdImage(MultipartFile file) throws IOException;
}
