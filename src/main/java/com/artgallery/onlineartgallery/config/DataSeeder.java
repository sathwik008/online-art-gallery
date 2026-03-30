package com.artgallery.onlineartgallery.config;

import com.artgallery.onlineartgallery.model.AppUser;
import com.artgallery.onlineartgallery.model.Artwork;
import com.artgallery.onlineartgallery.model.ArtworkStatus;
import com.artgallery.onlineartgallery.model.UserRole;
import com.artgallery.onlineartgallery.repository.AppUserRepository;
import com.artgallery.onlineartgallery.repository.ArtworkRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedGallery(ArtworkRepository artworkRepository, AppUserRepository appUserRepository) {
        return args -> {
            if (appUserRepository.count() == 0) {
                appUserRepository.saveAll(List.of(
                        buildUser("Aarav Buyer", "buyer@gallery.com", "buyer123", UserRole.BUYER),
                        buildUser("Mia Painter", "painter@gallery.com", "painter123", UserRole.PAINTER)
                ));
            }

            if (artworkRepository.count() > 0) {
                return;
            }

            artworkRepository.saveAll(List.of(
                    buildArtwork("Ethereal Bloom", "Ava Carter", "Abstract",
                            "Layered gradients and floral geometry crafted for immersive digital displays.",
                            "https://images.unsplash.com/photo-1579783902614-a3fb3927b6a5",
                            new BigDecimal("299.99"), true, ArtworkStatus.AVAILABLE),
                    buildArtwork("Neon Horizon", "Liam Brooks", "Landscape",
                            "A futuristic cityscape rendered with cinematic lighting and reflective textures.",
                            "https://images.unsplash.com/photo-1515405295579-ba7b45403062",
                            new BigDecimal("459.00"), true, ArtworkStatus.AVAILABLE),
                    buildArtwork("Quiet Motion", "Sophia Nguyen", "Minimal",
                            "A minimal composition balancing negative space, motion, and soft monochrome tones.",
                            "https://images.unsplash.com/photo-1500530855697-b586d89ba3ee",
                            new BigDecimal("189.50"), false, ArtworkStatus.AVAILABLE),
                    buildArtwork("Golden Echo", "Noah Patel", "Portrait",
                            "A stylized portrait blending metallic textures with expressive digital brushwork.",
                            "https://images.unsplash.com/photo-1513364776144-60967b0f800f",
                            new BigDecimal("520.00"), false, ArtworkStatus.SOLD)
            ));
        };
    }

    private Artwork buildArtwork(String title, String artistName, String category, String description,
                                 String imageUrl, BigDecimal price, boolean featured, ArtworkStatus status) {
        Artwork artwork = new Artwork();
        artwork.setTitle(title);
        artwork.setArtistName(artistName);
        artwork.setCategory(category);
        artwork.setDescription(description);
        artwork.setImageUrl(imageUrl);
        artwork.setPrice(price);
        artwork.setFeatured(featured);
        artwork.setStatus(status);
        return artwork;
    }

    private AppUser buildUser(String name, String email, String password, UserRole role) {
        AppUser user = new AppUser();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        return user;
    }
}
