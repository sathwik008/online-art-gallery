package com.artgallery.onlineartgallery.repository;

import com.artgallery.onlineartgallery.model.Artwork;
import com.artgallery.onlineartgallery.model.ArtworkStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArtworkRepository extends JpaRepository<Artwork, Long> {

    Page<Artwork> findByStatus(ArtworkStatus status, Pageable pageable);

    Page<Artwork> findByCategoryIgnoreCaseAndStatus(String category, ArtworkStatus status, Pageable pageable);

    Page<Artwork> findByFeaturedTrueAndStatus(ArtworkStatus status, Pageable pageable);

    long countByStatus(ArtworkStatus status);

    long countByFeaturedTrueAndStatus(ArtworkStatus status);
}
