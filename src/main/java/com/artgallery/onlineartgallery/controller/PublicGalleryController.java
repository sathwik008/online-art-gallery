package com.artgallery.onlineartgallery.controller;

import com.artgallery.onlineartgallery.dto.ArtworkResponse;
import com.artgallery.onlineartgallery.dto.GalleryStatsResponse;
import com.artgallery.onlineartgallery.dto.PurchaseRequest;
import com.artgallery.onlineartgallery.dto.PurchaseResponse;
import com.artgallery.onlineartgallery.model.AppUser;
import com.artgallery.onlineartgallery.model.UserRole;
import com.artgallery.onlineartgallery.service.AuthService;
import com.artgallery.onlineartgallery.service.ArtworkService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/gallery")
public class PublicGalleryController {

    private final ArtworkService artworkService;
    private final AuthService authService;

    public PublicGalleryController(ArtworkService artworkService, AuthService authService) {
        this.artworkService = artworkService;
        this.authService = authService;
    }

    @GetMapping("/artworks")
    public Page<ArtworkResponse> getGallery(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Boolean featured,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return artworkService.getPublicGallery(category, featured, page, size);
    }

    @GetMapping("/artworks/{id}")
    public ArtworkResponse getArtworkById(@PathVariable Long id) {
        return artworkService.getArtworkById(id);
    }

    @PostMapping("/artworks/{id}/purchase")
    public PurchaseResponse purchaseArtwork(@PathVariable Long id, @Valid @RequestBody PurchaseRequest request, HttpSession session) {
        AppUser buyer = authService.requireRole(session, UserRole.BUYER);
        return artworkService.purchaseArtwork(id, request, buyer);
    }

    @GetMapping("/stats")
    public GalleryStatsResponse getStats() {
        return artworkService.getGalleryStats();
    }
}
