package com.ecommerce.project.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ecommerce.project.dto.FileInfo;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;
@Service
public class FileImpl implements FileService{
    public Cloudinary cloudinary;
    public FileImpl(Cloudinary cloudinary){
        this.cloudinary = cloudinary;
    }
    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;
        String filePath = path + File.separator +  fileName;

        File folder = new File(path);
        if(!folder.exists()){
            folder.mkdir();
        }
        Files.write(Paths.get(filePath), file.getBytes()).toString();
        return fileName;
    }

    @Override
    public FileInfo uploadImage(MultipartFile file) throws IOException {
        Map params = ObjectUtils.asMap(
                "folder", "shopease/images"
        );
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        return new FileInfo(uploadResult.get("public_id").toString(), uploadResult.get("secure_url").toString());
    }
}
