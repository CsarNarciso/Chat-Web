package com.cesar.User.feign;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class MediaFallback implements MediaFeign {

    @Override
    public String upload(MultipartFile imageMetadata, String oldPath){
        return null;
    }

    @Override
    public void delete(String path){}
}