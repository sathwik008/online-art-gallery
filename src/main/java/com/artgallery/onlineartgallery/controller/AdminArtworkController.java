package com.artgallery.onlineartgallery.controller;

import com.artgallery.onlineartgallery.dto.ArtworkRequest;
import com.artgallery.onlineartgallery.dto.ArtworkResponse;
import com.artgallery.onlineartgallery.model.AppUser;
import com.artgallery.onlineartgallery.model.UserRole;
import com.artgallery.onlineartgallery.service.AuthService;
import com.artgallery.onlineartgallery.service.ArtworkService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/artworks")
public class AdminArtworkController {

    private final ArtworkService artworkService;
    private final AuthService authService;

    public AdminArtworkController(ArtworkService artworkService, AuthService authService) {
        this.artworkService = artworkService;
        this.authService = authService;
    }

    @GetMapping
    public Page<ArtworkResponse> getAllArtworks(
            HttpSession session,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        authService.requireRole(session, UserRole.PAINTER);
        return artworkService.getAllArtworks(page, size);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArtworkResponse createArtwork(@Valid @RequestBody ArtworkRequest request, HttpSession session) {
        AppUser painter = authService.requireRole(session, UserRole.PAINTER);
        return artworkService.createArtwork(request, painter);
    }

    @PutMapping("/{id}")
    public ArtworkResponse updateArtwork(@PathVariable Long id, @Valid @RequestBody ArtworkRequest request, HttpSession session) {
        authService.requireRole(session, UserRole.PAINTER);
        return artworkService.updateArtwork(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteArtwork(@PathVariable Long id, HttpSession session) {
        authService.requireRole(session, UserRole.PAINTER);
        artworkService.deleteArtwork(id);
    }
}
