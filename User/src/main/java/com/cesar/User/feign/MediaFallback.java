package com.cesar.User.feign;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.NoArgsConstructor;

@Component
@NoArgsConstructor
public class MediaFallback implements MediaFeign {

    @Override
    public String upload(MultipartFile imageMetadata){
        return null;
    }

    @Override
    public void delete(String path){}
}