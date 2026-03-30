package com.artgallery.onlineartgallery.service;

import com.artgallery.onlineartgallery.dto.ArtworkRequest;
import com.artgallery.onlineartgallery.dto.ArtworkResponse;
import com.artgallery.onlineartgallery.dto.GalleryStatsResponse;
import com.artgallery.onlineartgallery.dto.PurchaseRequest;
import com.artgallery.onlineartgallery.dto.PurchaseResponse;
import com.artgallery.onlineartgallery.exception.ArtworkNotAvailableException;
import com.artgallery.onlineartgallery.exception.ResourceNotFoundException;
import com.artgallery.onlineartgallery.model.AppUser;
import com.artgallery.onlineartgallery.model.Artwork;
import com.artgallery.onlineartgallery.model.ArtworkStatus;
import com.artgallery.onlineartgallery.model.PurchaseOrder;
import com.artgallery.onlineartgallery.repository.ArtworkRepository;
import com.artgallery.onlineartgallery.repository.PurchaseOrderRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ArtworkService {

    private final ArtworkRepository artworkRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;

    public ArtworkService(ArtworkRepository artworkRepository, PurchaseOrderRepository purchaseOrderRepository) {
        this.artworkRepository = artworkRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @Cacheable(value = "gallery", key = "#category + '-' + #featured + '-' + #page + '-' + #size")
    public Page<ArtworkResponse> getPublicGallery(String category, Boolean featured, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (Boolean.TRUE.equals(featured)) {
            return artworkRepository.findByFeaturedTrueAndStatus(ArtworkStatus.AVAILABLE, pageable)
                    .map(this::toResponse);
        }

        if (category != null && !category.isBlank()) {
            return artworkRepository.findByCategoryIgnoreCaseAndStatus(category, ArtworkStatus.AVAILABLE, pageable)
                    .map(this::toResponse);
        }

        return artworkRepository.findByStatus(ArtworkStatus.AVAILABLE, pageable)
                .map(this::toResponse);
    }

    @Cacheable("gallery-stats")
    public GalleryStatsResponse getGalleryStats() {
        return new GalleryStatsResponse(
                artworkRepository.count(),
                artworkRepository.countByStatus(ArtworkStatus.AVAILABLE),
                artworkRepository.countByStatus(ArtworkStatus.SOLD),
                artworkRepository.countByFeaturedTrueAndStatus(ArtworkStatus.AVAILABLE)
        );
    }

    public Page<ArtworkResponse> getAllArtworks(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return artworkRepository.findAll(pageable).map(this::toResponse);
    }

    public ArtworkResponse getArtworkById(Long id) {
        return toResponse(fetchArtwork(id));
    }

    @CacheEvict(value = {"gallery", "gallery-stats"}, allEntries = true)
    public ArtworkResponse createArtwork(ArtworkRequest request, AppUser painter) {
        Artwork artwork = new Artwork();
        applyRequest(artwork, request);
        if (painter != null) {
            artwork.setArtistName(painter.getName());
        }
        return toResponse(artworkRepository.save(artwork));
    }

    @CacheEvict(value = {"gallery", "gallery-stats"}, allEntries = true)
    public ArtworkResponse updateArtwork(Long id, ArtworkRequest request) {
        Artwork artwork = fetchArtwork(id);
        applyRequest(artwork, request);
        return toResponse(artworkRepository.save(artwork));
    }

    @CacheEvict(value = {"gallery", "gallery-stats"}, allEntries = true)
    public void deleteArtwork(Long id) {
        Artwork artwork = fetchArtwork(id);
        artworkRepository.delete(artwork);
    }

    @Transactional
    @CacheEvict(value = {"gallery", "gallery-stats"}, allEntries = true)
    public PurchaseResponse purchaseArtwork(Long artworkId, PurchaseRequest request, AppUser buyer) {
        Artwork artwork = fetchArtwork(artworkId);

        if (artwork.getStatus() != ArtworkStatus.AVAILABLE) {
            throw new ArtworkNotAvailableException("Artwork is no longer available for purchase");
        }

        PurchaseOrder order = new PurchaseOrder();
        order.setArtwork(artwork);
        order.setCustomerName(buyer != null ? buyer.getName() : request.customerName());
        order.setCustomerEmail(buyer != null ? buyer.getEmail() : request.customerEmail());
        order.setPurchasePrice(artwork.getPrice());

        artwork.setStatus(ArtworkStatus.SOLD);
        artwork.setFeatured(false);

        PurchaseOrder savedOrder = purchaseOrderRepository.save(order);

        return new PurchaseResponse(
                savedOrder.getId(),
                artwork.getId(),
                artwork.getTitle(),
                savedOrder.getCustomerName(),
                savedOrder.getCustomerEmail(),
                savedOrder.getPurchasePrice(),
                savedOrder.getPurchasedAt(),
                artwork.getStatus().name()
        );
    }

    private Artwork fetchArtwork(Long id) {
        return artworkRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artwork with id " + id + " was not found"));
    }

    private void applyRequest(Artwork artwork, ArtworkRequest request) {
        artwork.setTitle(request.title());
        artwork.setArtistName(request.artistName());
        artwork.setCategory(request.category());
        artwork.setDescription(request.description());
        artwork.setImageUrl(request.imageUrl());
        artwork.setPrice(request.price());
        artwork.setFeatured(request.featured());
        artwork.setStatus(request.status());
    }

    private ArtworkResponse toResponse(Artwork artwork) {
        return new ArtworkResponse(
                artwork.getId(),
                artwork.getTitle(),
                artwork.getArtistName(),
                artwork.getCategory(),
                artwork.getDescription(),
                artwork.getImageUrl(),
                artwork.getPrice(),
                artwork.isFeatured(),
                artwork.getStatus(),
                artwork.getCreatedAt()
        );
    }
}
