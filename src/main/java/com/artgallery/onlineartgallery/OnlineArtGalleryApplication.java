package com.artgallery.onlineartgallery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class OnlineArtGalleryApplication {

    public static void main(String[] args) {
        SpringApplication.run(OnlineArtGalleryApplication.class, args);
    }
}
