package com.cesar.User.service;

import com.cesar.User.feign.MediaFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class MediaService {

    public String upload(MultipartFile imageMetadata, String oldPath) {
        return imageMetadata.isEmpty() ? DEFAULT_IMAGE_PATH : feing.upload(imageMetadata, oldPath);
    }
    @Autowired
    private MediaFeign feing;
    @Value("${media.defaultImage.path}")
    private String DEFAULT_IMAGE_PATH;
}